package io.github.mersand.hyperclassic;

import android.view.View;

import java.lang.reflect.Method;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;

/**
 * SplitSncTransitionAlpha —— split 横屏下通知列的可见性 / 触摸 / 阴影修复。
 *
 * ==================================================================
 * ===== 症状 =====
 * ==================================================================
 *
 *   ① 横屏 split 展开时，右栏通知整片黑（SharedNotificationContainer
 *      的 transitionAlpha = 0，HWUI 合成时整体乘 0）
 *   ② 通知完全无法触摸（连卡片内部展开也不行）
 *   ③ 顶部通知行的 elevation 阴影被裁掉（左侧 QS 正常）
 *   ④ 通知淡入淡出与背景模糊不同步（背景模糊消失后通知才延迟收起）
 *
 * ==================================================================
 * ===== ① ② 的根因（已 100% 确认）=====
 * ==================================================================
 *
 * dumpsys（横屏 split 展开）实测五路 alpha：
 *   keyguardInfoLayerAlpha   = 1.0
 *   blurRatioAlpha           = 1.0
 *   keyguardPanelMoveAlpha   = 1.0
 *   classicQsExpansionAlpha  = 0.0   ← 唯一的零
 *   fullAodLinkageAlpha      = 1.0
 *
 * classicQsExpansionAlpha 的生产者
 * （NotificationStackingQuickSettingsInteractor$updateSncAlpha$1$1$1）：
 *
 *   if (showKeyguardNotif || orientation == 2 [LANDSCAPE]) out = qsExpansion
 *   else                                                     out = 0f
 *
 * 下游（updateSncAlpha$1$3 :pswitch_1）：
 *   classicQsExpansionAlpha = coerceIn(1.0f - emitted, 0f, 1f)   ← 1 减
 *
 * ⇒ 横屏命中 ⇒ emitted = qsExpansion，而它在 split 下恒为 1.0
 * ⇒ classicQsExpansionAlpha = 0
 *
 * 同一份数据同时造成两个 bug：
 *   [A] sncTransitionAlpha = min(1,1,1, 0, 1) = 0 → 通知列整片黑
 *   [B] isQSLandscapeTouchBlocked = !keyguard && landscape && qsExpansion>=1
 *       = true → NSSL.dispatchTouchEvent 直接 return false → 触摸全丢
 *
 * ==================================================================
 * ===== ③ 的根因 =====
 * ==================================================================
 *
 * SNC.drawChild → NotificationHeaderClipHelper.drawChild →
 * ShadeHeaderClipController.drawChild：
 *   if (clipRatio == 0f) superDrawChild
 *   else { saveLayer + superDrawChild + drawRect 遮罩 }
 *
 * saveLayer 把子树隔离到离屏图层，通知行的 elevation 阴影在图层边界被切掉。
 *
 * ==================================================================
 * ===== ④ 的处理原则（本版最重要的一条）=====
 * ==================================================================
 *
 * 主人多次实测定调：
 *   「淡入淡出时的动画是被强行拉出来渲染的，而随模糊进度一起进行的
 *     动画本身是完整自洽的，问题在于之前的保证通知在横屏下显示的 hook
 *     把不该显示的东西也拉入渲染序列中。」
 *
 * 因此【绝不再尝试】用任何值去驱动 classicQsExpansionAlpha 做动画：
 *
 *   ✗ 常量 0f            → classicAlpha 恒 1 → 通知被强行拉入渲染
 *   ✗ 1 - qsExpansion    → 横屏 qsExpansion 恒 1 → 同样恒 1
 *   ✗ 1 - mExpandedFraction → 该值只在离散节点更新，不是逐帧值
 *   ✗ 1 - setExpandedHeight 进度 → max 基准不稳定，实测 h/max 会恒 1
 *
 * 这四种都试过，都有确定性的失败表现（详见 CHANGELOG）。
 *
 * ① 的正确修法其实只需要【一件事】：
 *   让 classicQsExpansionAlpha 不再是 0，从而不再把 SNC 钉死成全黑。
 *
 * 我们让它恒为 1 —— 语义上这是对的：
 *   classicQsExpansionAlpha 服务的是「竖屏 QS 占满屏幕时通知让位」，
 *   split 横屏两栏并排，这个约束根本不存在，
 *   官方本来就该让它不参与 min()。
 *
 * 但它【不是动画驱动源】。通知行的逐行淡入淡出、堆叠缩放、模糊联动，
 * 全部由官方 StackScrollAlgorithm + ShadeWindowBlurController 完成 ——
 * 只要 SNC 这一层不被乘 0，那些自洽的动画自然就能正常播。
 *
 * 这正是 ④ 的正解：不干预动画，只解除错误的约束。
 *
 * ==================================================================
 * ===== 开关 =====
 * ==================================================================
 *
 *   debug.hyperclassic.split_snc_alpha  默认 1
 *   总闸：debug.hyperclassic.split_chrome
 */
final class SplitSncTransitionAlpha {

    private static final String TAG = "HCSncAlpha";
    /** ★ v332：topPadding 对齐用独立 TAG，便于单独过滤。 */
    private static final String TAG2 = "HCTopPad";
    private static final String PROP = "debug.hyperclassic.split_snc_alpha";
    private static final String PROP_CHROME = "debug.hyperclassic.split_chrome";

    private static Method propGet;

    private static volatile long propAt;
    private static volatile boolean enabled = true;

    private static volatile long chromeAt;
    private static volatile boolean chromeOn;
    private static int fixedAlpha, fixedTouch, fixedClip, errors;

    /** ★ v332：topPadding 对齐的调用计数（日志限流）。 */
    private static int topPadN;

    /** ★ v322：模糊进度探针（只测量，不改变行为）。 */
    private static volatile boolean probeOn;
    private static volatile long probeAt;
    private static volatile long probePropAt;
    private static volatile float probeLast = -1f;
    private static volatile int probeTicks;
    /** ★ v327：上一次看到的面板级 alpha（0~255）。 */
    private static volatile int probeLastAlpha = -1;
    /** ★ v328：去重签名（panelAlpha + 行 y + 行 alpha）。 */
    private static volatile int probeLastSig = Integer.MIN_VALUE;

    /**
     * ★ v323：ShadeWindowBlurControllerImpl 实例缓存。
     *
     * 从 access$updateBackgroundBlurEffect(impl, List, float) 的 arg0 抓。
     * 拿到它就能摸到 sncInteractor → _blurRatioAlpha。
     */
    private static volatile Object blurImpl;

    /** ★ v323：写入次数 / 最近写入值（诊断）。 */
    private static volatile int alphaWrites;
    private static volatile float alphaLast;
    /** split 横屏完整转换距离（实测 1500，用作 progress 归一化基准）。 */
    private static final float SPLIT_FULL_DISTANCE = 1500f;


    /** ★ v326：NSSL 视图缓存（探针用）。 */
    private static volatile View probeNssl;

    /** ★ v344：collapseFix 生效次数（日志限流）。 */
    private static int fixedCollapse;

    private SplitSncTransitionAlpha() {
    }

    static void install(XposedModule module, ClassLoader cl) {
        installAlphaLambda(module, cl);
        installTouchBlockLambda(module, cl);
        installHeaderClipBypass(module, cl);
        installTopPaddingInContainer(module, cl);
        installCollapseFix(module, cl);
        installCollapseWide(module, cl);
    }

    // ==============================================================
    // ④ 右栏通知顶部 padding 对齐（v334，容器内部方案）
    //
    // ===== 为什么 v332 失败 =====
    //
    // v332 hook 的是 NPVCInjector.calculateQsNotificationsTopPadding()。
    // 实测它有 6 个调用点，其中 2 个在 TouchHandler 里：
    //
    //   NotificationPanelViewController$TouchHandler.smali:4971
    //   NotificationPanelViewController$TouchHandler.smali:5600
    //
    // 它是「手势判定阈值」的输入之一。给它加 161px 之后，
    // 判定门槛整体偏移，于是横屏完全下拉不动。
    // 而且它算出来的值还会被后面重算覆盖，所以视觉上一点变化都没有。
    //
    // ===== 正确切入点：NSSL.updateTopPadding(FZ)V =====
    //
    // 全模块只有一处调用（纯数据流绑定，不参与手势）：
    //
    //   SharedNotificationContainerBinder$bind$2$2$2$1.emit()
    //     → NotificationStackScrollLayout.updateTopPadding(FZ)V
    //       → AmbientState.mTopPadding   ← 纯布局参数
    //         → StackScrollAlgorithm 布局各子 View
    //
    // 而 AmbientState.mTopPadding 的读取点全部在布局方法里：
    //   getAppearEndPosition / getExpandTranslationStart /
    //   setExpandedHeight / updateContentHeight / updateStackPosition /
    //   targetScrollForView / isBelowLastNotification / getTopHeadsUpHeight
    //
    // 【没有任何一个在 TouchHandler 里】—— 这正是它安全的原因。
    //
    // 另外 TouchHandler 读的是 NotificationTopPaddingControllerImpl
    // .getTopPadding()，那是另一个类（MIUI 自己的路径），与本 hook 无关。
    //
    // ===== 补多少 =====
    //
    // 运行时读左栏 MiuiNotificationShadeHeader 的真实高度（实测 161px），
    // 不写死，DPI / 折叠态变化时自动跟随。读不到就完全不干预。
    //
    // 开关：debug.hyperclassic.local.top_padding_align  默认 1
    // ==============================================================
    private static void installTopPaddingInContainer(XposedModule module, ClassLoader cl) {
        try {
            Class<?> nssl = Class.forName(
                    "com.android.systemui.statusbar.notification.stack."
                            + "NotificationStackScrollLayout", false, cl);
            Method m = nssl.getDeclaredMethod("updateTopPadding", float.class, boolean.class);
            m.setAccessible(true);

            module.hook(m)
                    .setId("hc_split_top_padding_in_container")
                    .setPriority(XposedInterface.PRIORITY_LOWEST)   // 官方先算完
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        try {
                            // ★ v359：本层默认【关闭】。
                            //
                            // 【为什么】顶部对齐现在由 SplitTopAlignBridge（L4）负责：
                            //   hook QuickSettingsControllerImpl.calculateNotificationsTopPadding，
                            //   已有实测对齐（delta=161）。
                            //
                            //   而本层 hook 的是 NotificationStackScrollLayout.updateTopPadding，
                            //   它也加 qsHeader(161) + extra(4)，两层同时生效就变成：
                            //       HCSplitTopAlign: align result=161.0
                            //       HCTopPad:        updateTopPadding 161.0 -> 326.0   ← 161+161+4
                            //   结果通知比 QS 托盘低一大截（主人 2026-11-26 20:01 实测）。
                            //
                            // 【如需回退】setprop debug.hyperclassic.local.top_padding_align 1
                            //   但必须先关掉 split_top_align，否则必定双重补偿。
                            if (!"1".equals(prop(L_TOP_PAD, "0"))) {
                                return chain.proceed();
                            }
                            if (!gate()) {
                                return chain.proceed();
                            }
                            Object self = chain.getThisObject();
                            if (!(self instanceof View)) {
                                return chain.proceed();
                            }
                            float official = ((Number) chain.getArg(0)).floatValue();
                            float headerH = qsHeaderHeight((View) self);
                            if (headerH <= 0f) {
                                return chain.proceed();   // 读不到就不干预
                            }
                            float want = official + headerH + padExtra();
                            int n = topPadN++;
                            if (n < 8) {
                                L.always(TAG2, "updateTopPadding " + official + " -> " + want
                                        + " (qsHeader=" + headerH
                                        + " extra=" + padExtra() + ") #" + n);
                            }
                            // ★ 只调一次 proceed：用新入参替换 arg0，保持 animate 不变
                            return chain.proceed(new Object[]{Float.valueOf(want), chain.getArg(1)});
                        } catch (Throwable t) {
                            error(t);
                            return null;   // void 方法；绝不二次执行
                        }
                    });

            L.always(TAG2, "installed topPadding-in-container hook (NSSL.updateTopPadding)");
        } catch (Throwable t) {
            L.always(TAG2, "topPadding-in-container hook FAILED: " + t);
        }
    }

    /**
     * ★ v336：像素比对得出的微调量。
     *
     * 主人实测（v334 截图逐像素互相关，3200x1440 横屏）：
     *
     *   extra=0 → 互相关 shift = -6.0, 峰值 L=162 / R=156
     *             ⇒ 右栏比左栏【高 6px】
     *   extra=4 → 互相关 shift =  0.0, 峰值 L=162 / R=162   ★ 完美对齐
     *   extra=6 → 互相关 shift = +2.0, 峰值 L=162 / R=164
     *             ⇒ 右栏比左栏【低 2px】，过冲
     *
     * 注意不是 +6：
     *   topPadding 加了 N，通知行位置并不是严格下移 N —— 堆叠算法
     *   会把一部分增量吃掉（实测 extra 加 4px 就抵消了 6px 视觉差，
     *   即有效位移系数约 1.5x）。所以这个值必须靠像素比对实测，
     *   不能靠算术推导。
     *
     * 热调：debug.hyperclassic.local.top_padding_extra   单位 px，默认 4
     *   （改完需重启 SystemUI：官方 flow 在值不变时不会重新 emit，
     *     热改 prop 不会立刻触发 updateTopPadding）
     */
    private static float padExtra() {
        String s = prop(L_TOP_EXTRA, "4");
        try {
            return Float.parseFloat(s);
        } catch (Throwable t) {
            return 4f;
        }
    }

    /**
     * 读左栏 MiuiNotificationShadeHeader 的真实高度。
     *
     * 从 NSSL 自己往上找 rootView，再 findViewById(R.id.header)，
     * 并校验类名含 NotificationShadeHeader（避免误取到别的同 id View）。
     * 任何一步失败都返回 0（表示「不要干预」）。
     */
    private static float qsHeaderHeight(View nssl) {
        try {
            View root = nssl.getRootView();
            if (root == null) {
                return 0f;
            }
            int id = root.getResources().getIdentifier(
                    "header", "id", "com.android.systemui");
            if (id == 0) {
                return 0f;
            }
            View header = root.findViewById(id);
            if (header == null || header.getVisibility() == View.GONE) {
                return 0f;
            }
            String cn = header.getClass().getName();
            if (!cn.contains("NotificationShadeHeader")) {
                return 0f;
            }
            int h = header.getHeight();
            // ★ v336：只认「收起态 header」。
            //
            // 实测两个高度值：
            //   收起态 161px  ← 这是我们要对齐的基准（正常情况）
            //   展开态 469px  ← QS 托盘完全展开后的 header（动画中间态也会出现）
            //
            // 499 以上一律视为异常态（展开/过渡），不干预，
            // 否则会一下子补进几百 px，造成跳变。
            if (h <= 0 || h > 300) {
                return 0f;
            }
            return h;
        } catch (Throwable t) {
            return 0f;
        }
    }


    // ------------------------------------------------ ① SNC 整片黑的源头

    /**
     * updateSncAlpha$1$1$1 是 combine 的 Function4：
     *   (qsExpansion: Float, showKeyguardNotif: Boolean, orientation: Int) -> Float
     *
     * 下游做 classicQsExpansionAlpha = 1 - emitted。
     *
     * 横屏 split 下让它输出 0f ⇒ classicAlpha = 1 ⇒ SNC 不再被乘 0 ⇒ 通知可见。
     *
     * ===== 为什么这是「解除约束」而不是「强行拉渲染」=====
     *
     * 这个 alpha 的语义是「QS 展开时通知让位」。
     * 竖屏单栏：QS 下拉占满屏幕，通知确实该让位 → 有意义。
     * split 横屏：左栏 QS、右栏通知，互不遮挡 → 这个约束不该存在。
     *
     * 官方在横屏分支直接拿 qsExpansion（恒 1）算，等于把「让位」拉满，
     * 把整个 SNC 乘 0。我们把它置 1，等于告诉 min()：
     * 「这一路没有约束」，让其余四路按官方逻辑正常合成。
     *
     * ===== 为什么不是动画驱动源 =====
     *
     * 它恒为 1，所以 min() 的结果由其余四路决定 ——
     * 其中 blurRatioAlpha 正是与背景模糊同源的那个值。
     * 因此通知的淡入淡出跟着模糊走，是官方自己的逻辑，不是我们造的。
     */
    private static void installAlphaLambda(XposedModule module, ClassLoader cl) {
        try {
            Class<?> c = Class.forName(
                    "com.miui.systemui.notification.domain.interactor."
                            + "NotificationStackingQuickSettingsInteractor$updateSncAlpha$1$1$1",
                    false, cl);
            Method m = c.getDeclaredMethod("invokeSuspend", Object.class);
            m.setAccessible(true);

            module.hook(m)
                    .setId("hc_split_snc_alpha_lambda")
                    .setPriority(XposedInterface.PRIORITY_HIGHEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        try {
                            if (!gate() || !alphaOn()) {
                                return chain.proceed();
                            }
                            Object self = chain.getThisObject();
                            int orientation = readInt(self, "I$0");
                            boolean keyguardNotif = readBool(self, "Z$0");
                            // 官方在「非横屏 + 无锁屏通知」时本来输出 0f，与我们一致 → 放行。
                            if (orientation != 2 && !keyguardNotif) {
                                return chain.proceed();
                            }
                            int n = fixedAlpha++;
                            if (n < 6) {
                                L.always(TAG, "sncAlpha emit 0.0 -> classicAlpha=1"
                                        + " (orientation=" + orientation
                                        + " keyguardNotif=" + keyguardNotif
                                        + " qsExpansion=" + readFloat(self, "F$0") + ") #" + n);
                            }
                            return Float.valueOf(0f);
                        } catch (Throwable t) {
                            error(t);
                            return chain.proceed();
                        }
                    });

            L.always(TAG, "installed updateSncAlpha$1$1$1 hook (classicAlpha=1)");
        } catch (Throwable t) {
            L.always(TAG, "updateSncAlpha$1$1$1 hook FAILED: " + t);
        }
    }

    // ------------------------------------------------------ ② 触摸阻断源头

    /**
     * updateQSLandscapeTouchBlock$1$1$1 输出 Boolean：
     *   (!showKeyguardNotif) && (orientation == 2) && (qsExpansion >= 1.0f)
     *
     * true 会让 NotificationStackScrollLayout.dispatchTouchEvent
     * 直接 return false，把整列通知的触摸全部丢掉。
     *
     * split 横屏两栏并排，不存在「QS 占满挡住通知」的语义，
     * 所以恒返回 false。
     */
    private static void installTouchBlockLambda(XposedModule module, ClassLoader cl) {
        try {
            Class<?> c = Class.forName(
                    "com.miui.systemui.notification.domain.interactor."
                            + "NotificationStackingQuickSettingsInteractor$updateQSLandscapeTouchBlock$1$1$1",
                    false, cl);
            Method m = c.getDeclaredMethod("invokeSuspend", Object.class);
            m.setAccessible(true);

            module.hook(m)
                    .setId("hc_split_qs_touch_block")
                    .setPriority(XposedInterface.PRIORITY_HIGHEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        try {
                            if (!gate() || !touchOn()) {
                                return chain.proceed();
                            }
                            Object self = chain.getThisObject();
                            if (readInt(self, "I$0") != 2) {
                                return chain.proceed();   // 只干预横屏
                            }
                            int n = fixedTouch++;
                            if (n < 6) {
                                L.always(TAG, "touchBlock -> false (qsExpansion="
                                        + readFloat(self, "F$0") + ") #" + n);
                            }
                            return Boolean.FALSE;
                        } catch (Throwable t) {
                            error(t);
                            return chain.proceed();
                        }
                    });

            L.always(TAG, "installed updateQSLandscapeTouchBlock$1$1$1 hook (force false)");
        } catch (Throwable t) {
            L.always(TAG, "updateQSLandscapeTouchBlock$1$1$1 hook FAILED: " + t);
        }
    }

    // -------------------------------------------- ③ 通知行顶部阴影被裁剪掉

    /**
     * ShadeHeaderClipController$clipRatio$1 的输出决定是否走裁剪路径：
     *   ratio == 0  → superDrawChild（正常，阴影保留）
     *   ratio != 0  → saveLayer + drawRect 遮罩（阴影被图层边界切掉）
     *
     * split 横屏两栏并排，不需要「header 高度裁剪」这套单栏动效，
     * 恒置 0 即可。
     */
    private static void installHeaderClipBypass(XposedModule module, ClassLoader cl) {
        try {
            Class<?> c = Class.forName(
                    "com.miui.systemui.shade.header.ShadeHeaderClipController$clipRatio$1",
                    false, cl);
            Method m = c.getDeclaredMethod("invokeSuspend", Object.class);
            m.setAccessible(true);

            module.hook(m)
                    .setId("hc_split_header_clip_ratio")
                    .setPriority(XposedInterface.PRIORITY_HIGHEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        try {
                            if (!gate() || !clipOn()) {
                                return chain.proceed();
                            }
                            int n = fixedClip++;
                            if (n < 6) {
                                L.always(TAG, "headerClip clipRatio -> 0.0f #" + n);
                            }
                            return Float.valueOf(0.0f);
                        } catch (Throwable t) {
                            error(t);
                            return chain.proceed();
                        }
                    });

            L.always(TAG, "installed ShadeHeaderClipController$clipRatio$1 hook (force 0f)");
        } catch (Throwable t) {
            L.always(TAG, "clipRatio hook FAILED: " + t);
        }
    }

    // ==============================================================
    // ⑤ ★ v344：上滑退出控制中心修复（split 转换距离钳制）
    //
    // ===== 根因（2026-11-24 实测定调，证据完整）=====
    //
    // 病灶：res/values/dimens.xml
    //   split_shade_full_transition_distance = 400.0dp
    //   split_shade_scrim_transition_distance = 600.0dp
    //
    // 本机 density = 600 → 缩放 3.75：
    //   mSplitShadeFullTransitionDistance  = 400dp × 3.75 = 1500 px
    //   mSplitShadeScrimTransitionDistance = 600dp × 3.75 = 2250 px
    //
    // 而横屏屏幕高度只有 1440 px！
    //   → 1500 > 1440，阈值永远达不到。
    //
    // 实测 dumpsys（横屏完全下拉后）：
    //   mExpandedHeight                   = 1440.0
    //   mExpandedFraction                 = 0.96      ← 1440/1500
    //   mSplitShadeFullTransitionDistance = 1500
    //   mSplitShadeScrimTransitionDistance= 2250
    //
    // 因果链：
    //   getMaxPanelTransitionDistance() 恒返回 1500
    //     → isFullyExpanded$1() {
    //           return mExpandedHeight >= getMaxPanelTransitionDistance();
    //           //     1440.0        >=        1500       → 永远 false
    //       }
    //     → handleCollapseState(dy, dx, y) {
    //           if (!isFullyExpandedOnDown) return false;   // 第一道门就挡住
    //       }
    //     → 上滑永不被识别为「收起」，被误判为「继续展开」
    //     → 实测日志：flingSpring mSpringLength=0.0, expand=true, vel=0.0
    //
    // ===== 为什么官方会这样写（不是 bug，是场景错配）=====
    //
    // getMaxPanelTransitionDistance() 里已有同样的防御：
    //   if (headsUp追踪中 || mExpandingFromHeadsUp) {
    //       return (int) min(max(mSplitShadeFullTransitionDistance,
    //                           mHeadsUpStartHeight * 2.5),
    //                        getMaxPanelHeight$1());   ← 已经有 min 钳制！
    //   }
    //   return mSplitShadeFullTransitionDistance;         ← 这里漏了钳制
    //
    // 400dp 是为「竖屏大屏 / 平板」设计的（竖屏 3200px 时 1500 完全合理），
    // 横屏只剩 1440px 就必然失败。属于「单栏竖屏假设」的遗留。
    //
    // ===== 修法：方案 A =====
    //
    // hook getMaxPanelTransitionDistance()，返回值钳制到 getMaxPanelHeight$1()：
    //   return min(orig, maxPanelHeight);
    // 语义与官方 headsUp 分支的 min 完全一致，属于「补齐官方漏掉的钳制」。
    //
    // ===== 为什么改这里最安全（6 个消费点全是比例换算）=====
    //
    //   1. isFullyExpanded$1()            → 收起判定门槛      ★核心
    //   2. setExpandedFraction(float)     → 分数↔高度换算
    //   3. fling$1()                      → 惯性动画终点
    //   4. setHeadsUpDraggingStartingHeight() → HUN 拖动比例
    //   5. toAodResetDefault()            → AOD 过渡判定
    //   6. $$ExternalSyntheticLambda9     → 窗口 LP 批量应用
    //
    // 全部是「拿它当分母/参照」做比例换算，返回值变小只会让 fraction 更准
    // （1.0 就对应真实满高度 1440），不会破坏其它逻辑。
    //
    // ===== 注意：不要碰 getMaxPanelHeight$1() =====
    //
    // 它就是面板能到达的真实最大高度（= 屏幕可用高度 1440），
    // 而且它内部有 mBarState / NSSL 子项数 / mMinExpansionHeight 等多分支，
    // 动它会牵扯到完全无关的布局计算。我们只钳制「过渡距离」这一个派生量。
    // ==============================================================

    private static void installCollapseFix(XposedModule module, ClassLoader cl) {
        try {
            Class<?> c = Class.forName(
                    "com.android.systemui.shade.NotificationPanelViewController", false, cl);
            Method m = c.getDeclaredMethod("getMaxPanelTransitionDistance");
            m.setAccessible(true);

            // getMaxPanelHeight$1 是同类里的 public final 方法（名字带 $1 但不能改名）。
            final Method maxHeight;
            try {
                maxHeight = c.getDeclaredMethod("getMaxPanelHeight$1");
                maxHeight.setAccessible(true);
            } catch (Throwable t) {
                L.always(TAG, "collapseFix: getMaxPanelHeight$1 not found, abort");
                return;
            }

            module.hook(m)
                    .setId("hc_split_collapse_fix")
                    .setPriority(XposedInterface.PRIORITY_HIGHEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        try {
                            Object orig = chain.proceed();
                            if (!gate() || !collapseOn()) {
                                return orig;
                            }
                            if (!(orig instanceof Integer)) {
                                return orig;
                            }
                            int o = (Integer) orig;
                            Object self = chain.getThisObject();
                            if (self == null) {
                                return orig;
                            }
                            int maxH;
                            try {
                                Object r = maxHeight.invoke(self);
                                if (!(r instanceof Integer)) {
                                    return orig;
                                }
                                maxH = (Integer) r;
                            } catch (Throwable t) {
                                return orig;
                            }
                            if (maxH <= 0) {
                                return orig;
                            }
                            if (o <= maxH) {
                                return orig;      // 已经合法，不动
                            }
                            int n = fixedCollapse++;
                            if (n < 8) {
                                L.always(TAG, "collapseFix transitionDistance "
                                        + o + " -> " + maxH + " #" + n);
                            }
                            return Integer.valueOf(maxH);
                        } catch (Throwable t) {
                            error(t);
                            return chain.proceed();
                        }
                    });

            L.always(TAG, "installed getMaxPanelTransitionDistance clamp hook (v344)");
        } catch (Throwable t) {
            L.always(TAG, "collapseFix hook FAILED: " + t);
        }
    }

    // ==============================================================
    // ⑥ ★ v345：上滑退出补完（绕过 split 下的 QS 展开门）
    //
    // ===== v344 之后剩下的问题 =====
    //
    // v344 修好了「阈值 1500 > 屏幕 1440」，isFullyExpandedOnDown 能正确为 true。
    // 但实测（2026-11-24）只有「底部热区」能关闭：
    //
    //   x=400  y=1400 → 1.0 -> 0.0  ✅（底部热区）
    //   x=800  y=1400 → 1.0 -> 0.0  ✅
    //   x=1400 y=1400 → 1.0 -> 0.0  ✅
    //   x=400  y=900  → 1.0 -> 1.0  ❌（左栏中部）
    //   x=2400 y=900  → 1.0 -> 1.0  ❌（右栏中部）
    //   x=400  y=400  → 1.0 -> 1.0  ❌（左栏上部）
    //   x=2400 y=400  → 1.0 -> 1.0  ❌（右栏上部）
    //
    // ===== 根因：handleCollapseState 的判定顺序 =====
    //
    //   NotificationPanelViewControllerInjector.handleCollapseState(FFF)Z
    //   参数：(dy, dx, initialTouchY)
    //
    //   ① if (!isFullyExpandedOnDown) return false;
    //   ② if (|dy| <= |dx|)          return false;
    //   ③ if (dy >= 0)               return false;
    //   ④ if (|dy| <= touchSlop)     return false;
    //   ⑤ if (touchY > panelH - hotZone - paddingBottom) return true;   ← 底部热区
    //   ⑥ if (qsController.getExpanded())                return false;  ← ★★★
    //   ⑦ if (touchHeaderArea)                           return true;
    //   ⑧ if (nss.isScrolledToBottom())                  return true;
    //   ⑨ if (nss.isBelowLastNotification(x, y))         return true;
    //   ⑩ return false;
    //
    // 第 ⑥ 关是单栏时代的语义：「QS 展开时，上滑先收 QS，不收面板」。
    // 但 split shade 下 QS 恒占左栏、恒展开（实测 mQsExpanded=true），
    // 于是 getExpanded() 永远为 true → ⑦⑧⑨ 三张安全网永远走不到
    // → 只有第 ⑤ 关的底部热区能过。
    //
    // ===== 修法：重写判定，跳过第 ⑥ 关，保留 ⑦⑧⑨ 安全网 =====
    //
    // 为什么不能简单「无条件 return true」：
    //   NotificationPanelView 是 NSSL 的父容器，父容器的
    //   onInterceptTouchEvent 先于子 View 的 dispatchTouchEvent。
    //   一旦 panelCollapsing=true，面板会抢占触摸，
    //   用户在通知列表里上滑看更多通知时就变成「收起面板」——这是回归。
    //
    // 所以保留官方的三张安全网（header / 列表滚到底 / 最后一条通知下方），
    // 它们表达的正是「这里没有可滚动内容，上滑就该收面板」。
    // 我们只删掉第 ⑥ 关这个 split 下永远为真的伪条件。
    // ==============================================================

    private static void installCollapseWide(XposedModule module, ClassLoader cl) {
        try {
            Class<?> inj = Class.forName(
                    "com.android.systemui.shade.NotificationPanelViewControllerInjector",
                    false, cl);
            final Method m = inj.getDeclaredMethod("handleCollapseState",
                    float.class, float.class, float.class);
            m.setAccessible(true);

            // NSSL.isBelowLastNotification(FF)Z  —— public
            final Class<?> nsslCls = Class.forName(
                    "com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout",
                    false, cl);
            final Method belowLast = nsslCls.getMethod("isBelowLastNotification",
                    float.class, float.class);

            // NSSL.mScrollAdapter（内部类 6）→ isScrolledToBottom()Z
            final java.lang.reflect.Field scrollAdapterF;
            final Method scrolledToBottom;
            try {
                scrollAdapterF = nsslCls.getField("mScrollAdapter");
                scrollAdapterF.setAccessible(true);
                scrolledToBottom = scrollAdapterF.getType()
                        .getMethod("isScrolledToBottom");
                scrolledToBottom.setAccessible(true);
            } catch (Throwable t) {
                L.always(TAG, "collapseWide: scrollAdapter unavailable: " + t);
                return;
            }

            module.hook(m)
                    .setId("hc_split_collapse_wide")
                    .setPriority(XposedInterface.PRIORITY_HIGHEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        try {
                            // ★ 无条件入口探针（确认 hook 是否真的命中）
                            java.util.List<Object> args0 = chain.getArgs();
                            int we = wideEnter++;
                            if (we < 8) {
                                L.always(TAG, "collapseWide ENTER argc="
                                        + (args0 == null ? -1 : args0.size()) + " #" + we);
                            }

                            Object orig = chain.proceed();
                            if (Boolean.TRUE.equals(orig)) {
                                if (we < 8) {
                                    L.always(TAG, "collapseWide official=true #" + we);
                                }
                                return orig;      // 官方已放行（底部热区）
                            }
                            if (!gate() || !collapseOn()) {
                                if (we < 8) {
                                    L.always(TAG, "collapseWide gate=" + gate()
                                            + " collapseOn=" + collapseOn() + " #" + we);
                                }
                                return orig;
                            }
                            Object self = chain.getThisObject();
                            if (self == null) {
                                return orig;
                            }

                            java.util.List<Object> args = chain.getArgs();
                            if (args.size() < 3) {
                                return orig;
                            }
                            float dy = asFloat(args.get(0));
                            float dx = asFloat(args.get(1));
                            float ty = asFloat(args.get(2));

                            boolean expOnDown = readBool(self, "isFullyExpandedOnDown");
                            // ★ 诊断：把三项关键值都打出来
                            noteWide("probe dy=" + dy + " dx=" + dx + " ty=" + ty
                                    + " expOnDown=" + expOnDown
                                    + " header=" + readBool(self, "touchHeaderArea"));

                            // ① 必须「按下时就已完全展开」
                            if (!expOnDown) {
                                return orig;
                            }
                            // ② 垂直位移必须大于水平
                            if (Math.abs(dy) <= Math.abs(dx)) {
                                noteWide("reject:dxTooBig");
                                return orig;
                            }
                            // ③ 必须是向上
                            if (dy >= 0f) {
                                noteWide("reject:notUp");
                                return orig;
                            }
                            // ④ 必须超过触摸阈值
                            int slop = readInt(self, "touchSlop");
                            if (slop < 0) {
                                noteWide("reject:noSlop");
                                return orig;
                            }
                            if (Math.abs(dy) <= (float) slop) {
                                noteWide("reject:belowSlop");
                                return orig;
                            }

                            // ⑤ ★ 跳过 qsController.getExpanded() 门
                            //    （split 下恒 true，是单栏语义的遗留）

                            // ⑦ header 区域
                            if (readBool(self, "touchHeaderArea")) {
                                noteWide("header");
                                return Boolean.TRUE;
                            }

                            // ⑧⑨ 通知列表安全网
                            Object nss = readObj(self, "notificationStackScroller");
                            if (nss != null) {
                                try {
                                    Object sa = scrollAdapterF.get(nss);
                                    if (sa != null
                                            && Boolean.TRUE.equals(
                                                    scrolledToBottom.invoke(sa))) {
                                        noteWide("nssBottom");
                                        return Boolean.TRUE;
                                    }
                                } catch (Throwable ignore) {
                                    // 继续尝试下一张网
                                }
                                try {
                                    float x = readFloat(self, "initialTouchX");
                                    float y = readFloat(self, "initialTouchY");
                                    if (x >= 0f && y >= 0f
                                            && Boolean.TRUE.equals(
                                                    belowLast.invoke(nss, x, y))) {
                                        noteWide("belowLast");
                                        return Boolean.TRUE;
                                    }
                                } catch (Throwable ignore) {
                                    // 继续
                                }

                                // ⑨b ★ v349：split 横屏专用 —— 触摸点不在通知栏横向范围内时直接放行
                                //
                                // 官方的 ⑧⑨ 两张网是单栏时代的语义：
                                //   「通知列表还有内容可滚 → 上滑先去滚列表，不收面板」
                                // 但 split shade 横屏下 NSSL 只占右栏（实测 x≈1524..3170），
                                // 左栏 x<1524 是 QS 磁贴区，那里根本没有可滚动的通知。
                                //
                                // 实测（v348 探针）：x=400 y=900（左栏 QS 区）上滑时
                                //   isScrolledToBottom=false（右栏有通知可滚）
                                //   isBelowLastNotification(400,900)=false（点在 NSSL 之外）
                                // 两张网都不成立 → 返回 false → 左栏中部上滑无响应。
                                //
                                // 修法：只要触摸点横向不落在 NSSL 的 [getLeft, getRight] 内，
                                // 就说明用户滑的不是通知列表，上滑语义只能是「收面板」。
                                // 这样左栏 QS 区全境可收起，右栏仍保留官方滚动优先级。
                                try {
                                    float x = readFloat(self, "initialTouchX");
                                    if (x >= 0f) {
                                        java.lang.reflect.Method getLeft =
                                                nss.getClass().getMethod("getLeft");
                                        java.lang.reflect.Method getRight =
                                                nss.getClass().getMethod("getRight");
                                        int l = ((Number) getLeft.invoke(nss)).intValue();
                                        int r = ((Number) getRight.invoke(nss)).intValue();
                                        if (r > l && (x < (float) l || x > (float) r)) {
                                            noteWide("outsideNssl x=" + x
                                                    + " nss=[" + l + "," + r + "]");
                                            return Boolean.TRUE;
                                        }
                                    }
                                } catch (Throwable ignore) {
                                    // 继续
                                }
                            }

                            return orig;
                        } catch (Throwable t) {
                            error(t);
                            return chain.proceed();
                        }
                    });

            L.always(TAG, "installed handleCollapseState widen hook (v345)");
        } catch (Throwable t) {
            L.always(TAG, "collapseWide hook FAILED: " + t);
        }
    }

    private static float asFloat(Object o) {
        return (o instanceof Number) ? ((Number) o).floatValue() : 0f;
    }

    private static Object readObj(Object self, String name) {
        try {
            java.lang.reflect.Field f = self.getClass().getField(name);
            f.setAccessible(true);
            return f.get(self);
        } catch (Throwable t) {
            return null;
        }
    }

    private static int wideHits;
    /** ★ v348：hook 入口独立计数（不受 noteWide 影响）。 */
    private static int wideEnter;

    private static void noteWide(String why) {
        int n = wideHits++;
        if (n < 40) {
            L.always(TAG, "collapseWide: " + why + " #" + n);
        }
    }

    // ------------------------------------------------------- v326 探针（只读）

    /**
     * 纯诊断探针。
     *
     * 目的：搞清楚「横屏通知为什么没有淡入」。
     *
     * 已确认的事实：
     *   · sncTransitionAlpha = min(5 路离散状态量)，手势期间几乎不重算；
     *   · 竖屏能正常淡入 → 淡入不是 SNC 这层 alpha 提供的；
     *   · 横屏 SNC 恒 ta=1.0（满亮）→ 这一层永远不是瓶颈。
     *
     * 所以真正要看的是「通知行自己」：
     *   NotificationStackScrollLayout.mChildren 里第一条 ExpandableNotificationRow
     *   的 alpha / translationY / height，在 setExpandedHeight 逐帧推进时怎么变。
     *
     * 竖屏（正常）与横屏（异常）各跑一次，对比即知断在哪一环。
     *
     * 开关：debug.hyperclassic.probe_blur_ratio
     */
    private static void installProbe(XposedModule module, ClassLoader cl) {
        try {
            Class<?> c = Class.forName(
                    "com.android.systemui.shade.NotificationPanelView",
                    false, cl);
            Method m = c.getDeclaredMethod("dispatchDraw", android.graphics.Canvas.class);
            m.setAccessible(true);

            module.hook(m)
                    .setId("hc_probe_panel_alpha")
                    .setPriority(XposedInterface.PRIORITY_LOWEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        try {
                            Object result = chain.proceed();
                            if (probeEnabled()) {
                                probeTick(chain.getThisObject());
                            }
                            return result;
                        } catch (Throwable t) {
                            error(t);
                            return chain.proceed();
                        }
                    });

            L.always(TAG, "installed probe (NotificationPanelView.dispatchDraw)");
        } catch (Throwable t) {
            L.always(TAG, "probe hook FAILED: " + t);
        }
    }

    /**
     * 每帧打印：面板级 alpha（mCurrentPanelAlpha）+ 首个通知行状态。
     *
     * mCurrentPanelAlpha 就是 NPV.dispatchDraw 里那个 MULTIPLY 遮罩的值，
     * 也就是「通知与 QS 一起淡入」的真正载体。0=全黑, 255=全亮。
     */
    private static void probeTick(Object npv) {
        long now = android.os.SystemClock.uptimeMillis();
        if (now - probeAt < 100L) {
            return;
        }
        probeAt = now;

        int pa = -1;
        Object v = readField(npv, "mCurrentPanelAlpha");
        if (v instanceof Integer) {
            pa = (Integer) v;
        }

        View nsslView = probeNssl;
        if (nsslView == null) {
            nsslView = findNssl(npv);
            probeNssl = nsslView;
        }

        // 取第一条通知行
        View firstRow = null;
        if (nsslView instanceof android.view.ViewGroup) {
            android.view.ViewGroup g = (android.view.ViewGroup) nsslView;
            for (int i = 0; i < Math.min(g.getChildCount(), 8); i++) {
                View ch = g.getChildAt(i);
                if (ch != null
                        && ch.getClass().getSimpleName()
                                .contains("ExpandableNotificationRow")) {
                    firstRow = ch;
                    break;
                }
            }
        }

        // 去重：panelAlpha + 行 y（取整）+ 行 alpha 都参与
        int rowY = firstRow == null ? -99999 : (int) firstRow.getY();
        int rowA = firstRow == null ? -1 : (int) (firstRow.getAlpha() * 1000f);
        int sig = pa * 31 + rowY * 7 + rowA;
        if (sig == probeLastSig && probeTicks > 12) {
            return;
        }
        probeLastSig = sig;
        int n = probeTicks++;
        if (n > 300) {
            return;
        }

        StringBuilder sb = new StringBuilder(160);
        sb.append("probe pa=").append(pa);
        sb.append(" land=").append(CcHooks.diagnosticLandscapeNow());

        if (npv instanceof View) {
            View p = (View) npv;
            sb.append(" NPV[vis=").append(p.getVisibility())
              .append(" a=").append(trim(p.getAlpha()))
              .append(" y=").append(trim(p.getY()))
              .append(" ty=").append(trim(p.getTranslationY()))
              .append(" h=").append(p.getHeight())
              .append("]");
        }
        if (nsslView != null) {
            sb.append(" NSSL[vis=").append(nsslView.getVisibility())
              .append(" a=").append(trim(nsslView.getAlpha()))
              .append(" ta=").append(trim(nsslView.getTransitionAlpha()))
              .append(" y=").append(trim(nsslView.getY()))
              .append(" h=").append(nsslView.getHeight())
              .append("]");
        } else {
            sb.append(" NSSL[none]");
        }
        if (firstRow != null) {
            sb.append(" ROW[a=").append(trim(firstRow.getAlpha()))
              .append(" ta=").append(trim(firstRow.getTransitionAlpha()))
              .append(" y=").append(trim(firstRow.getY()))
              .append(" top=").append(firstRow.getTop())
              .append(" h=").append(firstRow.getHeight())
              .append(" vis=").append(firstRow.getVisibility())
              .append("]");
        } else {
            sb.append(" ROW[none]");
        }
        sb.append(" #").append(n);

        L.always(TAG, sb.toString());
    }

    private static String trim(float f) {
        int i = (int) (f * 1000f);
        return String.valueOf(i / 1000.0);
    }

    /** 从 NPV 里递归找 notification_stack_scroller。 */
    private static View findNssl(Object npv) {
        try {
            if (!(npv instanceof View)) {
                return null;
            }
            View root = ((View) npv).getRootView();
            if (root == null) {
                return null;
            }
            int id = root.getResources().getIdentifier(
                    "notification_stack_scroller", "id", "com.android.systemui");
            if (id == 0) {
                return null;
            }
            return root.findViewById(id);
        } catch (Throwable t) {
            return null;
        }
    }

    /** 逐帧打印 h 与第一条通知行的状态（限流 120ms，最多 200 行）。 */
    private static void probeTickH(float h) {
        long now = android.os.SystemClock.uptimeMillis();
        if (now - probeAt < 120L) {
            return;
        }
        probeAt = now;
        if (Math.abs(h - probeLast) < 1.0f && probeTicks > 8) {
            return;
        }
        probeLast = h;
        int n = probeTicks++;
        if (n > 200) {
            return;
        }
        L.always(TAG, "probe h=" + h + " #" + n);
    }

    /** 在最上层窗口里按资源名找 View。 */
    private static View findViewByName(String resName) {
        try {
            Class<?> wmg = Class.forName("android.view.WindowManagerGlobal");
            java.lang.reflect.Method getViews = wmg.getMethod("getRootViews");
            java.util.List<?> roots = (java.util.List<?>) getViews.invoke(null);
            if (roots == null) {
                return null;
            }
            for (Object rv : roots) {
                if (rv == null) {
                    continue;
                }
                View pv = (View) rv;
                int id = pv.getResources().getIdentifier(
                        resName, "id", "com.android.systemui");
                if (id == 0) {
                    continue;
                }
                View found = pv.findViewById(id);
                if (found != null) {
                    return found;
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    /** 从 impl 的视图列表里找 SharedNotificationContainer（失败返回 null）。 */
    private static View findSnc(Object impl) {
        try {
            if (impl == null) {
                return null;
            }
            Object lazy = readField(impl, "viewsBelowNotif$delegate");
            Object list = lazy == null ? null
                    : lazy.getClass().getMethod("get").invoke(lazy);
            if (list instanceof Iterable) {
                for (Object o : (Iterable<?>) list) {
                    if (o instanceof View
                            && o.getClass().getName()
                                    .endsWith("SharedNotificationContainer")) {
                        return (View) o;
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static boolean probeEnabled() {
        long now = android.os.SystemClock.uptimeMillis();
        if (now - probePropAt > 500L) {
            probeOn = "1".equals(prop("debug.hyperclassic.probe_blur_ratio", "1"));
            probePropAt = now;
        }
        return probeOn;
    }

    // ------------------------------------------------------------- 字段读取

    /** 逐级向上找字段（含 superclass），找不到返回 null。 */
    private static Object readField(Object target, String name) {
        if (target == null) {
            return null;
        }
        Class<?> c = target.getClass();
        while (c != null) {
            try {
                java.lang.reflect.Field f = c.getDeclaredField(name);
                f.setAccessible(true);
                return f.get(target);
            } catch (NoSuchFieldException ignored) {
                c = c.getSuperclass();
            } catch (Throwable t) {
                return null;
            }
        }
        return null;
    }

    private static int readInt(Object self, String name) {
        try {
            return self.getClass().getField(name).getInt(self);
        } catch (Throwable t) {
            return -1;
        }
    }

    private static boolean readBool(Object self, String name) {
        try {
            return self.getClass().getField(name).getBoolean(self);
        } catch (Throwable t) {
            return false;
        }
    }

    private static float readFloat(Object self, String name) {
        try {
            return self.getClass().getField(name).getFloat(self);
        } catch (Throwable t) {
            return -1f;
        }
    }

    // ------------------------------------------------------------------ props

    /** 统一闸门：本地开关 + chrome 总闸 + 真实横屏。 */
    private static boolean gate() {
        return runtime() && chromeRuntimeOn() && CcHooks.diagnosticLandscapeNow();
    }

    // ===== v330：逐 hook 独立开关（二分定位「通知立刻出现」的元凶）=====
    private static final String L_ALPHA = "debug.hyperclassic.local.classic_alpha";
    private static final String L_TOUCH = "debug.hyperclassic.local.touch_block";
    private static final String L_CLIP = "debug.hyperclassic.local.clip_ratio";
    private static final String L_TOP_PAD = "debug.hyperclassic.local.top_padding_align";
    private static final String L_TOP_EXTRA = "debug.hyperclassic.local.top_padding_extra";
    /** ★ v344：上滑退出修复（split 转换距离钳制）。 */
    private static final String L_COLLAPSE = "debug.hyperclassic.local.collapse_fix";

    private static boolean on(String key) {
        return !"0".equals(prop(key, "1"));
    }

    private static boolean alphaOn() {
        return on(L_ALPHA);
    }

    private static boolean touchOn() {
        return on(L_TOUCH);
    }

    private static boolean clipOn() {
        return on(L_CLIP);
    }

    private static boolean collapseOn() {
        return on(L_COLLAPSE);
    }

    private static boolean runtime() {
        long now = android.os.SystemClock.uptimeMillis();
        if (now - propAt > 500L) {
            enabled = !"0".equals(prop(PROP, "1"));
            propAt = now;
        }
        return enabled;
    }

    private static boolean chromeRuntimeOn() {
        long now = android.os.SystemClock.uptimeMillis();
        if (now - chromeAt > 500L) {
            chromeOn = "1".equals(prop(PROP_CHROME, "1"));
            chromeAt = now;
        }
        return chromeOn;
    }

    private static String prop(String key, String def) {
        try {
            if (propGet == null) {
                propGet = Class.forName("android.os.SystemProperties")
                        .getMethod("get", String.class, String.class);
            }
            return String.valueOf(propGet.invoke(null, key, def)).trim();
        } catch (Throwable t) {
            return def;
        }
    }

    private static void error(Throwable t) {
        if (errors++ < 4) {
            L.always(TAG, "fallback to official: " + t);
        }
    }
}