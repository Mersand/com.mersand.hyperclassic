package io.github.mersand.hyperclassic;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import java.lang.reflect.Method;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;

/**
 * SplitRotationReset —— 横竖屏切换后立刻重置一次通知堆叠状态。
 *
 * ==================================================================
 * ===== 症状（主人反馈问题②）=====
 * ==================================================================
 *
 *   「竖屏与横屏切换时会带来严重的通知布局问题，并在滑动后修复，
 *     因此需要在切换状态时立刻重置一次通知状态。」
 *
 * 也就是说：旋转后通知行的位置/宽度/透明度假的残留旧值，
 * 必须等用户手动滑一下列表、触发 StackScrollAlgorithm 重算才恢复。
 *
 * ==================================================================
 * ===== 为什么需要主动重置 =====
 * ==================================================================
 *
 * NotificationStackScrollLayout.onConfigurationChanged 的官方实现只做了：
 *   - 翻转屏（flip tiny screen）状态同步
 *   - updateSplitNotificationShade()
 *   - 更新 Resources 缓存
 *
 * 它【不会】主动重算所有通知行的 ViewState。
 * 而通知行的位置/alpha/hidden 是 StackScrollAlgorithm.updateChild()
 * 在每次 requestChildrenUpdate() 时逐行写入的。
 *
 * split shade 下我们用 SplitLayoutBridge 改了 NSSL 的父级几何
 * （colocCenter / 宽度等），这些变更在旋转瞬间不会自动传导到每一行，
 * 于是行保留了上一方向的几何 → 布局错乱，直到下一次滑动
 * 触发 mStackScrollAlgorithm 重算才恢复。
 *
 * ==================================================================
 * ===== 修法 =====
 * ==================================================================
 *
 * hook NotificationStackScrollLayout.onConfigurationChanged，
 * 在官方逻辑跑完后，post 一次 requestChildrenUpdate()。
 *
 * 为什么用 post 而不是直接调用：
 *   onConfigurationChanged 期间视图树还在重建（measure/layout 未完成），
 *   此时读 getWidth()/getHeight() 拿到的还是旧值。
 *   post 到消息队列尾部，能保证在本轮 layout 之后执行，
 *   那时尺寸已更新，重算才有效。
 *
 * 为什么延迟 2 帧（postDelayed 32ms）而不是 1 帧：
 *   旋转后 HyperOS 会连续触发多次 configuration change + 专门的
 *   updateResources 流程；只 post 一次常常赶在官方第二次
 *   requestLayout 之前，被随后的 layout 冲掉。
 *   实测 32ms（约 2 帧 @60Hz）能稳定落在官方流程之后。
 *
 * 只在「方向真的变了」时才重置，靠记住上一次 orientation 判断，
 * 避免无关的 configuration change（如字体大小、深色模式）也触发。
 *
 * ==================================================================
 * ===== v309/v310：分级回滚「改 View 状态」的实验代码 =====
 * ==================================================================
 *
 * 主人定调（两轮）：
 *   ① 「范围限于这次引入的改状态来修复『竖屏隐藏 qs 转横屏时导致
 *       qs 不显示』这个问题的代码。其他的修复代码是正常的，可以保留。」
 *   ② 「看起来这次代码回滚的多了一点，之前修好了 qs 隐藏状态切横屏时
 *      通知列的显示，现在操作后 qs 与通知都被隐藏，只剩状态栏。」
 *
 * 所以不是「全删」，而是按证据分级：
 *
 * 【彻底删除 —— 有害】
 *
 *   1) installVisibilityBlocker   (v305)
 *      hook View.setVisibility，把 QS 子树的 INVISIBLE 改写成 VISIBLE。
 *
 *   2) installAlphaScaleBlocker   (v306)
 *      hook View.setAlpha / setScaleX / setScaleY，强顶成 1。
 *
 *   3) clearAncestors             (v307)
 *      沿 parent 链清理窗口级容器（NPV / MiuiNQSContainer /
 *      NotificationShadeWindowView）的可见性。
 *
 *   以上三项的共同罪行：在绘制事件流之外改写 View 属性，
 *   使 RenderNode 与 WMS Surface 状态脱节 →
 *   主人实测到「磁贴挂在屏幕上但点不到 / 悬浮通知变成幽灵磁贴挡住屏幕」。
 *
 * 【保留 —— 有效且无害】
 *
 *   4) refreshStack               (v301)
 *      resetGroupChildrenStackingInfo + updateResourceValues +
 *      requestChildrenUpdate，修「旋转后通知堆叠布局错乱、要滑动才恢复」。
 *      纯状态机 API 调用，不碰任何 View 属性。
 *
 *   5) restoreSnc                 (v310，本次恢复)
 *      只恢复 SharedNotificationContainer 自己的可见性。
 *      它是 NotificationShadeWindowView 的直接子层，在 NPV 之外，
 *      改它不会造成 Surface 脱节（v308 实测：通知能显示，只有 QS 空白）。
 *      删掉它就会退化成「qs 与通知都被隐藏，只剩状态栏」。
 *
 * 【不恢复 —— 禁区】
 *
 *      NotificationPanelView / MiuiNQSContainer /
 *      NotificationShadeWindowView 的可见性，
 *      一律交给 WMS / ShadeController 状态机，外部不得强改。
 *
 * ===== 为什么 QS 空白不能靠改属性解决（probe_22 硬证据）=====
 *
 *   probe_22（Frida，v308 空白态实测）：
 *     [0] MiuiQSContainer              vis=0 a=1 tXY=0,0  scale=1,1
 *         > MiuiQSPanel                vis=0 a=1
 *         > MiuiQuickQSPanel           vis=0 a=1
 *     [1] FrameLayout                  vis=0 a=1
 *     [2] MiuiNQSContainer             vis=4
 *     [3] NotificationPanelView        vis=4 a=0
 *     [4] NotificationShadeWindowView  vis=4
 *
 *   → QS 子树内部（MiuiQSContainer 及其所有子层）已经彻底正常，
 *     唯一的隐藏点是窗口级的 MiuiNQSContainer / NPV /
 *     NotificationShadeWindowView，而这三层正是禁区。
 *
 *   根因在状态机：KeyguardPanelViewController.updateVisibility() 里
 *     if (!mHeadsUpAnimatingAway && !mHeadsUpPinnedMode) {
 *         if (!isExpanded())  → 隐藏路径
 *         else                → 显示路径 (:cond_7)
 *     }
 *   而 isExpanded() 的本质是 mExpandedFraction > 0。
 *   竖屏「上滑隐藏 QS」走到完整列表态时 mExpandedFraction 归 0，
 *   NPV 被判为「该收起」→ INVISIBLE，转横屏后没有人重新把它打开。
 *
 *   正确方向：在 ShadeController / NotificationPanelViewController
 *   这一层用官方展开 API（expandToQs / expandToNotifications /
 *   instantExpandShade / makeExpandedVisible 等）把面板打开，
 *   绝不能再从 View 属性上硬掰。
 *
 *   本文件当前职责：堆叠重算 + SNC 可见性恢复。
 *
 * ==================================================================
 * ===== 开关 =====
 * ==================================================================
 *
 *   debug.hyperclassic.split_rotation_reset  默认 1
 *   跟随 debug.hyperclassic.split_chrome 总闸。
 */
final class SplitRotationReset {

    private static final String TAG = "HCRotationReset";
    private static final String PROP = "debug.hyperclassic.split_rotation_reset";
    private static final String PROP_CHROME = "debug.hyperclassic.split_chrome";

    // ===== v330：逐 hook 独立开关（二分定位「通知立刻出现」的元凶）=====
    //
    // 全部默认 1（开）。单独设 0 即可停用某一项，用于二分。
    //
    //   debug.hyperclassic.local.panel_state   NPVC.updateResources$1 -> setExpandedFraction(1)
    //   debug.hyperclassic.local.qscontent     QSContent emit 改写
    //   debug.hyperclassic.local.nsscover      nssCoverQsFraction 复位
    //   debug.hyperclassic.local.config        NSSL.onConfigurationChanged -> refreshStack/restoreSnc
    //   debug.hyperclassic.local.stack_refresh refreshStack（堆叠重算）
    //   debug.hyperclassic.local.snc_restore   restoreSnc（SNC 属性恢复）
    //   debug.hyperclassic.local.qs_expanded   QSCI.setExpanded 恢复
    private static final String L_PANEL = "debug.hyperclassic.local.panel_state";
    private static final String L_QSCONTENT = "debug.hyperclassic.local.qscontent";
    private static final String L_NSSCOVER = "debug.hyperclassic.local.nsscover";
    private static final String L_CONFIG = "debug.hyperclassic.local.config";
    private static final String L_STACK = "debug.hyperclassic.local.stack_refresh";
    private static final String L_SNC = "debug.hyperclassic.local.snc_restore";

    private static Method propGet;
    private static volatile long propAt;
    private static volatile boolean enabled = true;
    private static volatile long chromeAt;
    private static volatile boolean chromeOn;

    private static volatile int lastOrientation = -1;
    private static int resets, errors;
    /** v311：状态机恢复的调用计数（用于日志限流）。 */
    private static int panelN;
    /** v312：QS 展开恢复的计数。 */
    private static int qsN;
    /** v313：QSContent emit 改写的计数。 */
    private static int qsContentN;
    /** ★ v336：横→竖残留清理的计数。 */
    private static int landCleanN;
    /** v313：emit 被调用次数（无条件诊断）。 */
    private static int emitSeen;
    /** v314：nssCoverQs 复位计数。 */
    private static int nssN;

    private SplitRotationReset() {
    }

    static void install(XposedModule module, ClassLoader cl) {
        installConfigHook(module, cl);
        installPanelStateRestore(module, cl);
        installQsContentAlphaFix(module, cl);
    }

    // ------------------- ★ v313：修 QSContent 的 alpha/scale（数据流层）

    /**
     * 修「横屏 split 下左栏 QS 区完全空白」的最后一步。
     *
     * ==================================================================
     * ===== 决定性实测（probe_22，v312 之后）=====
     * ==================================================================
     *
     *   [2] MiuiNQSContainer             vis=0 shown=true   ✅
     *   [3] NotificationPanelView        vis=0 shown=true   ✅
     *   [4] NotificationShadeWindowView  vis=0 shown=true   ✅
     *
     *     > MiuiQSPanel                 vis=0 a=1          ✅
     *     > MiuiQuickQSPanel            vis=0 a=1          ✅
     *     > QSContent  vis=0  a=0  scale=0.9,0.9   ★★★ 罪魁
     *
     * v311/v312 把整条祖先链和 shade 状态都修好了（通知列已正常），
     * 只剩 QSContent 自己 alpha=0 —— 它把整块 QS 内容透明掉了。
     *
     * ==================================================================
     * ===== 谁在写这个 alpha（smali 完整链）=====
     * ==================================================================
     *
     *   MiuiQSController.qsTranslation            (StateFlow<Float>)
     *     → MiuiQSContainerController$onInit$1$3   (classId = 2)
     *       → collect → $1$1$1.emit(value)         (classId = 2)
     *         → 默认分支（smali）：
     *             alpha = 1.0f - value
     *             scale = 1.0f - value * 0.1f
     *             setPivotX(getWidth() / 2)
     *             setPivotY(header.getHeight())
     *
     *   实测 a=0 / scale=0.9 反推：value = 1.0
     *   即 qsTranslation = 1.0（QS 的「收起进度」满值）。
     *
     * ===== 同一个 emit 的三个 classId 分支 =====
     *
     *   pswitch_1 (classId=0) → MiuiQSContainer.setTranslationY(v)
     *   pswitch_0 (classId=1) → MiuiQSContainer.setAlpha(v)
     *                           setScale(lerp(0.9, 1.0, v))
     *                           // 来自 appearAlpha，实测 v=1 → alpha=1 ✅
     *   default   (classId=2) → QSContent.setAlpha(1 - v)     ★
     *                           setScale(1 - v*0.1)          ★
     *
     * 所以只需处理 classId=2，不会影响另外两路。
     *
     * ==================================================================
     * ===== 为什么在 emit 层修，而不是 setAlpha 层 =====
     * ==================================================================
     *
     * 已删除的 v306 做法是 hook View.setAlpha，在官方调用【之后】
     * 把值改写成 1 —— 那会让 RenderNode 与实际绘制指令不一致
     * （主人实测：幽灵磁贴挡屏、点不到）。
     *
     * 现在的做法是【在数据流上把输入改对】，然后让官方自己的代码
     * 走完 setAlpha / setScale / 布局的完整流程：
     *
     *   emit(0.0f) → 官方算出 alpha = 1-0 = 1, scale = 1-0 = 1
     *              → 官方自己调 setAlpha(1) / setScale(1)
     *
     * 官方代码路径完整执行，没有任何「事后覆盖」，因此不会脱节。
     *
     * ==================================================================
     * ===== 只在横屏 + 只改 classId=2 =====
     * ==================================================================
     *
     * 竖屏必须完全放行 —— 那里的 qsTranslation 驱动的是
     * 「QS 收起时淡出并上移」的正常交互，改了就坏了。
     *
     * 横屏 split 下 QS 是常驻左栏，语义上永远「完全展开」，
     * 所以 qsTranslation 恒 0 才是正确值。
     */
    private static void installQsContentAlphaFix(XposedModule module, ClassLoader cl) {
        try {
            Class<?> collector = Class.forName(
                    "com.android.systemui.qs.MiuiQSContainerController$onInit$1$1$1",
                    false, cl);

            Method emit = null;
            for (Method m : collector.getDeclaredMethods()) {
                if ("emit".equals(m.getName()) && m.getParameterCount() == 2) {
                    emit = m;
                    break;
                }
            }
            if (emit == null) {
                L.always(TAG, "installQsContentAlphaFix: emit not found");
                return;
            }
            emit.setAccessible(true);

            // $r8$classId 是 public 字段（smali 里 .field public final synthetic）
            final java.lang.reflect.Field classIdF =
                    collector.getDeclaredField("$r8$classId");
            classIdF.setAccessible(true);

            module.hook(emit)
                    .setId("hc_split_qscontent_alpha")
                    .setPriority(XposedInterface.PRIORITY_HIGHEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        if (!qsContentOn()) {
                            return chain.proceed();
                        }
                        try {
                            Object self = chain.getThisObject();
                            if (self == null) {
                                return chain.proceed();
                            }
                            // 只处理 classId=2（默认分支：QSContent 的 alpha/scale）
                            int classId = classIdF.getInt(self);
                            // ★ 诊断：无条件记录前几次，确认 hook 是否真的被调用
                            int seen = emitSeen++;
                            if (seen < 6) {
                                L.always(TAG, "emit called classId=" + classId
                                        + " arg0=" + chain.getArg(0));
                            }
                            if (classId != 2) {
                                return chain.proceed();
                            }
                            if (!gate()) {
                                return chain.proceed();
                            }
                            if (!CcHooks.diagnosticLandscapeNow()) {
                                return chain.proceed();   // 竖屏完全放行
                            }
                            Object arg0 = chain.getArg(0);
                            if (!(arg0 instanceof Float)) {
                                return chain.proceed();
                            }
                            float v = ((Float) arg0).floatValue();
                            if (v <= 0.01f) {
                                return chain.proceed();   // 已经是 0，不用管
                            }
                            int n = qsContentN++;
                            if (n < 10) {
                                L.always(TAG, "qsContent emit " + v + " -> 0.0f (split landscape) #" + n);
                            }
                            return chain.proceed(new Object[]{Float.valueOf(0f), chain.getArg(1)});
                        } catch (Throwable t) {
                            error(t);
                            return chain.proceed();
                        }
                    });

            L.always(TAG, "installed QSContent emit hook (classId=2, split landscape only, v313)");
        } catch (Throwable t) {
            L.always(TAG, "installQsContentAlphaFix FAILED: " + t);
        }

        // ★ v314：数据源层修复 —— 直接改 _nssCoverQsFraction 的输入。
        //
        // 为什么还要这一层：
        //   emit hook 是「下游」，若下游没被调用（协程未 collect、
        //   接口调用绕过具体类等），就完全不起作用（v313 实测：
        //   一条 emit 日志都没有）。这里从【上游】下手更可靠。
        //
        // 官方写入点（smali，唯一）：
        //   NotificationPanelViewControllerInjector.updateScrollerTopPadding(F)
        //
        //     qsTopPadding = p1
        //     nssCoveredQs = (p1 == 0 || p1 == QSCI.getHeaderHeight())
        //     v1 = calculateQsNotificationsTopPadding()
        //     frac = (v1 - qsTopPadding) / (v1 - headerHeight)
        //     _nssCoverQsFraction.updateState(coerceIn(frac, 0f, 1f))
        //
        //   竖屏 QS 收起时 qsTopPadding = 0 →
        //     frac = v1 / (v1 - headerHeight) > 1 → clamp 成 1.0
        //     → QSContent.alpha = 1 - 1 = 0 → 整块 QS 透明
        //
        // 修法：横屏跑完官方逻辑后，把 _nssCoverQsFraction 复位成 0。
        //   split 横屏下 NSS 在右栏、QS 在左栏，不存在覆盖关系，
        //   所以「覆盖比例」语义上恒为 0 —— 这是数据本身的正确值。
        //   官方收到 0 后会自己算出 alpha=1 / scale=1，走完整绘制流程。
        installNssCoverQsReset(module, cl);
    }

    /**
     * ★ v314：横屏下把 _nssCoverQsFraction 复位为 0（数据源层）。
     *
     * 见 installQsContentAlphaFix 尾部注释里的完整公式与理由。
     */
    private static void installNssCoverQsReset(XposedModule module, ClassLoader cl) {
        try {
            Class<?> inj = Class.forName(
                    "com.android.systemui.shade.NotificationPanelViewControllerInjector",
                    false, cl);
            Method mt = inj.getDeclaredMethod("updateScrollerTopPadding", float.class);
            mt.setAccessible(true);

            final java.lang.reflect.Field miuiQsF = inj.getDeclaredField("miuiQsController");
            miuiQsF.setAccessible(true);

            module.hook(mt)
                    .setId("hc_split_nss_cover_qs")
                    .setPriority(XposedInterface.PRIORITY_LOWEST)   // 官方先跑完
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        Object result = chain.proceed();
                        if (!nssCoverOn()) {
                            return result;
                        }
                        try {
                            if (!gate()) {
                                return result;
                            }
                            if (!CcHooks.diagnosticLandscapeNow()) {
                                return result;
                            }
                            Object self = chain.getThisObject();
                            if (self == null) {
                                return result;
                            }
                            Object qsCtrl = miuiQsF.get(self);
                            if (qsCtrl == null) {
                                return result;
                            }
                            Object flow = getField(qsCtrl, "_nssCoverQsFraction");
                            if (flow == null) {
                                return result;
                            }
                            // StateFlowImpl.updateState$1(Object prev, Object next)
                            // prev 传 null 表示「不校验旧值」，官方自己也这么用。
                            Class<?> cls = flow.getClass();
                            Method upd = null;
                            for (Method m : cls.getMethods()) {
                                if (m.getName().startsWith("updateState") && m.getParameterCount() == 2) {
                                    upd = m;
                                    break;
                                }
                            }
                            if (upd == null) {
                                if (nssN < 4) L.always(TAG, "nssCoverQs: updateState not found");
                                nssN++;
                                return result;
                            }
                            upd.setAccessible(true);
                            upd.invoke(flow, null, Float.valueOf(0f));
                            int n = nssN++;
                            if (n < 8) {
                                L.always(TAG, "nssCoverQs -> 0.0f (split landscape) #" + n);
                            }
                        } catch (Throwable t) {
                            error(t);
                        }
                        return result;
                    });

            L.always(TAG, "installed updateScrollerTopPadding hook (nssCoverQs reset, v314)");
        } catch (Throwable t) {
            L.always(TAG, "installNssCoverQsReset FAILED: " + t);
        }
    }

    // ------------------------- ★ v311：走状态机恢复 shade（不再改 View 属性）

    /**
     * 修「竖屏隐藏 QS 后转横屏 → 整个 shade 空白（只剩状态栏）」的【正规】修法。
     *
     * ==================================================================
     * ===== 根因（本轮实测 + smali 双向确认）=====
     * ==================================================================
     *
     * 实测时序（本次 v310 现场）：
     *
     *   竖屏下拉展开后：  mExpandedFraction = 1.0
     *   转横屏瞬间后：    mExpandedFraction = 0.0   ← ★ 旋转把它清零了
     *   结果：            NotificationPanelView  I.E......（INVISIBLE）
     *                     MiuiNQSContainer       I.E......（INVISIBLE）
     *                     SharedNotificationContainer I.E......（INVISIBLE）
     *   手动下拉一次后：  mExpandedFraction = 1.0
     *                     → 三层全部 V.E......，全部恢复
     *
     * → 旋转把 mExpandedFraction 清零，isExpanded() 变成 false，
     *   于是官方自己把面板判为「该收起」。
     *   这不是渲染属性没复位，而是【状态】错了。
     *
     * smali 确认的判定链（KeyguardPanelViewController.updateVisibility）：
     *
     *   if (!mHeadsUpAnimatingAway && !mHeadsUpPinnedMode) {
     *       if (!isExpanded())  → 隐藏路径
     *       else                → 显示路径
     *   }
     *   // isExpandedWithoutHeadsUp() 的本质就是 mExpandedFraction > 0
     *
     * ==================================================================
     * ===== 为什么用 setExpandedFraction(1.0f) =====
     * ==================================================================
     *
     * NotificationPanelViewController 里 mExpandedFraction 的写入路径：
     *
     *   setExpandedFraction(f)
     *     → getMaxPanelTransitionDistance() * f
     *     → setExpandedHeight(height)
     *       → mNotifInjector.setExpandedHeightInternal$1(height)
     *         → ……官方完整的 measure / layout / expansion 回调链
     *
     * 也就是【官方自己的展开入口】，和手势下拉走的是同一条路。
     * 它会自然带动：
     *   - ShadeExpansionStateManager.onPanelExpansionChanged
     *   - QuickSettingsControllerImpl 的展开度
     *   - 通知堆叠重算
     *   - NPV 的 visibility/alpha（由状态机自己决定，不用我们碰）
     *
     * ===== 为什么 hook updateResources$1 而不是自己定时轮询 =====
     *
     * updateResources$1() 是旋转/尺寸变化时的官方重算入口（smali 10380）：
     *
     *   mSplitShadeEnabled = shouldUseSplitNotificationShade(mResources)
     *   mQsController.updateResources$1()
   *   mNotificationsQSContainerController.updateResources$1()
     *   if (mSplitShadeEnabled 变了) {
     *       onPanelStateChanged$1(0)
     *       onSplitShadeEnabledChanged()   ← 这里才有 setExpanded(...)
     *   }
     *
     * 它是「旋转后 split 状态更新的必经之路」，hook 在这里能保证：
     *   1) 拿到 NPVC 的 this（不用反射找单例）
     *   2) 时机正确（官方已经算完 mSplitShadeEnabled）
     *   3) 幂等（updateResources 本身可重复调用）
     *
     * ===== 与已删除方案的本质区别（重要）=====
     *
     *   已删除（v302~v308）：直接写 View 的 visibility/alpha/scale
     *                        → RenderNode 与 WMS Surface 脱节 → 幽灵磁贴
     *
     *   本方案（v311）：调用官方 setExpandedFraction，让【状态机自己】
     *                  去决定 View 怎么变
     *                  → 不产生任何渲染层不一致
     *
     * ==================================================================
     * ===== 只在横屏生效 =====
     * ==================================================================
     *
     * 竖屏单栏有自己的展开/收起语义，绝不能强行 setExpandedFraction(1)。
     * 所以这里必须判方向（与 restoreSnc 同理，gate() 不判）。
     */
    /**
     * ★ v336：上一次 updateResources 时是否处于横屏。
     * 用于识别「横 → 竖」的方向切换，只在那一瞬间做一次残留清理。
     */
    private static volatile boolean wasLandscape;

    /** ★ v336：横转竖残留清理是否启用。 */
    private static final String L_LAND_CLEAN = "debug.hyperclassic.local.landscape_residue_clean";

    private static boolean landscapeCleanOn() {
        return on(L_LAND_CLEAN);
    }

    /**
     * ★ v336：清理「横转竖」时留下的 QS 状态残留。
     *
     * ===== 症状（主人实测）=====
     *
     *   横屏下用过控制中心 → 转回竖屏 → 竖屏下拉时 QS 偶发
     *   【一开始就是完整展开的托盘】，而不是正常的收起态。
     *
     * ===== 根因 =====
     *
     *   横屏 split 下 restorePanelState() 会调 setExpandedFraction(1.0f)，
     *   restoreQsExpanded() 会调 qsci.setExpanded(true)。
     *   转到竖屏后，官方 updateResources 只重算资源，
     *   并不会把这两个「由我们拉起来的」状态清回去。
     *   竖屏 QS 于是继承了横屏的展开度，一下拉就直接是整片托盘的终态。
     *
     * ===== 修法 =====
     *
     *   检测到方向从横 → 竖的那一次，用官方入口把它们复位：
     *     - qsci.setExpanded(false)     ← 官方收起入口，幂等
     *     - 若面板此时也是收起态（f 接近 0），不再额外 setExpandedFraction，
     *       避免把用户正在展开的手势打断。
     *
     *   关键：只在方向切换的【那一次】执行，正常竖屏使用完全不干预。
     */
    private static void clearLandscapeResidue(Object npvc, Method setFraction) {
        try {
            if (npvc == null) {
                return;
            }
            // 1) QS 收起
            Object qsci = getField(npvc, "mQsController");
            boolean qsWas = false;
            if (qsci != null) {
                try {
                    Method getExpanded = qsci.getClass().getMethod("getExpanded");
                    getExpanded.setAccessible(true);
                    qsWas = Boolean.TRUE.equals(getExpanded.invoke(qsci));
                    if (qsWas) {
                        Method setExpanded = qsci.getClass().getMethod("setExpanded", boolean.class);
                        setExpanded.setAccessible(true);
                        setExpanded.invoke(qsci, false);
                    }
                } catch (Throwable t) {
                    error(t);
                }
            }
            // 2) 面板 fraction：只在「几乎收起但没归零」时补一刀，
            //    不碰用户已经拉起来的状态。
            float cur = curFraction(npvc);
            boolean fracFixed = false;
            if (cur > 0.02f && cur < 0.9f) {
                try {
                    setFraction.invoke(npvc, 0f);
                    fracFixed = true;
                } catch (Throwable t) {
                    error(t);
                }
            }
            int n = landCleanN++;
            if (n < 8) {
                L.always(TAG, "land->port clean #" + n
                        + ": qsWasExpanded=" + qsWas
                        + " frac=" + cur + (fracFixed ? " -> 0" : " (kept)"));
            }
        } catch (Throwable t) {
            error(t);
        }
    }

    private static void installPanelStateRestore(XposedModule module, ClassLoader cl) {
        try {
            final Class<?> npvc = Class.forName(
                    "com.android.systemui.shade.NotificationPanelViewController",
                    false, cl);
            final Method updateResources = npvc.getDeclaredMethod("updateResources$1");
            updateResources.setAccessible(true);

            // setExpandedFraction(float) —— 官方展开入口
            final Method setFraction = npvc.getDeclaredMethod("setExpandedFraction", float.class);
            setFraction.setAccessible(true);

            // isExpanded() —— 只读，用来判断要不要干预
            final Method isExpanded = npvc.getDeclaredMethod("isExpanded");
            isExpanded.setAccessible(true);

            final Handler handler = new Handler(Looper.getMainLooper());

            module.hook(updateResources)
                    .setId("hc_split_panel_state")
                    .setPriority(XposedInterface.PRIORITY_LOWEST)   // 官方先跑完
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        Object result = chain.proceed();
                        if (!panelOn()) {
                            return result;
                        }
                        try {
                            if (!gate()) {
                                return result;
                            }
                            final Object self = chain.getThisObject();
                            if (self == null) {
                                return result;
                            }
                            // 官方逻辑刚跑完，此时 mSplitShadeEnabled 已是最新值。
                            // 只在横屏提供服务 —— 竖屏有自己的一套语义。
                            boolean land = CcHooks.diagnosticLandscapeNow();
                            if (!land) {
                                // ★ v336：横 → 竖方向切换时，清理横屏残留。
                                //
                                // 主人实测（2026-11-24）：
                                //   「竖屏时错误展开的 QS 来源是横屏转竖屏时的状态残留」
                                //
                                // 机理：横屏下前面这段会 setExpandedFraction(1.0)
                                //   + qsci.setExpanded(true)。转到竖屏之后，
                                //   官方 updateResources 未必把这两个状态清回来，
                                //   于是竖屏一下拉就是「完整 QS 托盘」。
                                //
                                // 处理：只在【刚从横屏过来】的那一次清理（用 prop 闸门 +
                                //   方向变化标志双保险，避免每帧都跑）。
                                if (wasLandscape && landscapeCleanOn()) {
                                    wasLandscape = false;
                                    final Object self2 = self;
                                    handler.post(() -> clearLandscapeResidue(self2, setFraction));
                                } else {
                                    wasLandscape = false;
                                }
                                return result;
                            }
                            wasLandscape = true;
                            // post 到下一轮消息队列：避开当前 layout，且不打断官方流程。
                            handler.post(() -> {
                                restorePanelState(self, setFraction, isExpanded);
                                // v312：shade 展开后，再把 QS 也展开（split 左栏）。
                                // 必须排在 restorePanelState 之后 ——
                                // QSCI.setExpanded 内部会回调
                                // NPVC.updateExpandedHeightToMaxHeight$1()，
                                // 依赖 shade 已经是展开态。
                                restoreQsExpanded(self);
                            });
                        } catch (Throwable t) {
                            error(t);
                        }
                        return result;
                    });

            L.always(TAG, "installed NPVC.updateResources hook (panel state restore, v311)");
        } catch (Throwable t) {
            L.always(TAG, "installPanelStateRestore FAILED: " + t);
        }
    }

    /**
     * 用官方入口把 shade 的展开状态拉回来。
     *
     * 只做一件事：如果官方判定「面板已收起」（isExpanded=false），
     * 就调 setExpandedFraction(1.0f) 让它回到完全展开。
     *
     * 为什么不判断「用户是不是真的想收起」：
     *   split shade 的语义是硬约束 —— 横屏时左栏 QS + 右栏通知必须都在。
     *   横屏下不存在「合法的收起状态」，所以拉回展开永远是对的。
     *
     * 为什么 isExpanded=true 时也调一次：
     *   mExpandedFraction 可能停在 0.96 之类的中间值，
     *   此时 isExpanded 已是 true 但视觉上不足满屏。
     *   直接拉到 1.0 不会有副作用（官方自己也会这么干）。
     */
    private static void restorePanelState(Object npvc, Method setFraction, Method isExpanded) {
        try {
            float cur = curFraction(npvc);
            boolean expanded;
            try {
                expanded = Boolean.TRUE.equals(isExpanded.invoke(npvc));
            } catch (Throwable t) {
                expanded = false;
            }

            if (expanded && cur >= 0.99f) {
                if (panelN < 6) {
                    L.always(TAG, "panel state ok (expanded, f=" + cur + ")");
                }
                panelN++;
                return;
            }

            setFraction.invoke(npvc, 1.0f);
            int n = panelN++;
            if (n < 8) {
                L.always(TAG, "panel state restore #" + n
                        + ": was expanded=" + expanded + " f=" + cur + " -> setExpandedFraction(1.0)");
            }
        } catch (Throwable t) {
            error(t);
        }
    }

    /**
     * ★ v312：把 QS 也展开（split 横屏下左栏必须显示磁贴）。
     *
     * ===== 症状（v311 实测）=====
     *
     *   setExpandedFraction(1.0) 之后：
     *     mExpandedFraction = 0.96        ← shade 展开了
     *     mQsExpansionFraction = 0.0      ← 但 QS 没展开
     *     NotificationPanelView       V.E
     *     MiuiQSContainer             V.E
     *     SharedNotificationContainer V.E  ← 通知显示了
     *
     *   截图：右栏通知正常，左栏 QS 仍然空白。
     *
     * ===== 为什么 shade 展开了 QS 却不展开 =====
     *
     * smali 确认 onSplitShadeEnabledChanged() 里：
     *
     *   if (!isKeyguardShowing() && isPanelExpanded()) {
     *       mQsController.setExpanded(mSplitShadeEnabled);
     *   }
     *
     * 也就是说：只有 mSplitShadeEnabled 发生变化的那一刻，
     * 官方才会把 QS 的展开状态同步到 split 值。
     * 而旋转过程中 mSplitShadeEnabled 可能一直是 true（没变），
     * 这个分支压根不执行 → QS 保持收起态。
     *
     * ===== 修法 =====
     *
     * 主动调 QuickSettingsControllerImpl.setExpanded(true)。
     * 它会（smali 4480）：
     *   _legacyIsQsExpanded.updateState(true)
     *   updateQsState()
     *   NPVC.updateExpandedHeightToMaxHeight$1()
     *   NPVC.updateSystemUiStateFlags()
     *   PanelExpansionObserver 回调通知
     *
     * 这是官方自己用的 QS 展开入口，不是渲染层硬改。
     *
     * ===== 幂等 =====
     *
     * setExpanded 开头有 if (getExpanded() != value) 的短路，
     * 重复调用无副作用。
     */
    private static void restoreQsExpanded(Object npvc) {
        try {
            Object qsci = getField(npvc, "mQsController");
            if (qsci == null) {
                if (panelN < 6) L.always(TAG, "restoreQs: mQsController not found");
                return;
            }
            Method getExpanded = qsci.getClass().getMethod("getExpanded");
            getExpanded.setAccessible(true);
            boolean cur = Boolean.TRUE.equals(getExpanded.invoke(qsci));
            if (qsN < 10) {
                L.always(TAG, "restoreQs: qsci.getExpanded()=" + cur
                        + " qsciCls=" + qsci.getClass().getSimpleName());
            }
            if (cur) {
                qsN++;
                return;   // 已经展开，不动
            }
            Method setExpanded = qsci.getClass().getMethod("setExpanded", boolean.class);
            setExpanded.setAccessible(true);
            setExpanded.invoke(qsci, true);
            if (qsN < 10) {
                L.always(TAG, "QS expanded: false -> true (split landscape) #" + qsN);
            }
            qsN++;
        } catch (Throwable t) {
            error(t);
        }
    }

    /** 反射读实例字段（失败返回 null）。 */
    private static Object getField(Object target, String name) {
        try {
            Class<?> c = target.getClass();
            while (c != null) {
                try {
                    java.lang.reflect.Field f = c.getDeclaredField(name);
                    f.setAccessible(true);
                    return f.get(target);
                } catch (NoSuchFieldException e) {
                    c = c.getSuperclass();
                }
            }
        } catch (Throwable ignored) { }
        return null;
    }

    /** 反射读 mExpandedFraction（公共字段，读失败返回 -1）。 */
    private static float curFraction(Object npvc) {
        try {
            java.lang.reflect.Field f = npvc.getClass().getDeclaredField("mExpandedFraction");
            f.setAccessible(true);
            return f.getFloat(npvc);
        } catch (Throwable t) {
            return -1f;
        }
    }

    // ------------------------------------------------------------- 主 hook

    private static void installConfigHook(XposedModule module, ClassLoader cl) {
        try {
            Class<?> nssl = Class.forName(
                    "com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout",
                    false, cl);
            Method changed = nssl.getDeclaredMethod(
                    "onConfigurationChanged", android.content.res.Configuration.class);
            changed.setAccessible(true);

            Method request = nssl.getDeclaredMethod("requestChildrenUpdate");
            request.setAccessible(true);

            // ★ v301：堆叠重算的关键入口。
            //
            // NotificationStackingInteractor.updateResourceValues 是**方向相关**的：
            // 它从 Resources 里重新读取 stackingHeight1/2、stackingBoundary、
            // padding 等全部堆叠参数，然后触发每行的 stackingInfo 重算。
            // 官方只在启动和部分配置变化时调用它，旋转时不一定跑，
            // 于是行保留了上一方向的堆叠参数 → 主人看到的「平铺开超过堆叠数量、
            // 下面的通知没有缩小模糊」。
            Method updateResourceValues = null;
            try {
                Class<?> inter = Class.forName(
                        "com.miui.systemui.notification.domain.interactor.NotificationStackingInteractor",
                        false, cl);
                for (Method m : inter.getDeclaredMethods()) {
                    if (m.getName().startsWith("updateResourceValues")) {
                        m.setAccessible(true);
                        updateResourceValues = m;
                        break;
                    }
                }
            } catch (Throwable ignored) { }

            // ★ v301：分组子通知的堆叠信息也需要重置。
            // TITLE: resetGroupChildrenStackingInfo(ExpandableNotificationRow)
            Method resetGroup = null;
            try {
                Class<?> inter = Class.forName(
                        "com.miui.systemui.notification.domain.interactor.NotificationStackingInteractor",
                        false, cl);
                Class<?> rowCls = Class.forName(
                        "com.android.systemui.statusbar.notification.row.ExpandableNotificationRow",
                        false, cl);
                resetGroup = inter.getDeclaredMethod("resetGroupChildrenStackingInfo", rowCls);
                resetGroup.setAccessible(true);
            } catch (Throwable ignored) { }

            final Method fUpdate = updateResourceValues;
            final Method fReset = resetGroup;

            final Handler handler = new Handler(Looper.getMainLooper());

            module.hook(changed)
                    .setId("hc_split_rotation_reset")
                    .setPriority(XposedInterface.PRIORITY_LOWEST)   // 让官方先跑完
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        Object result = chain.proceed();
                        if (!configOn()) {
                            return result;
                        }
                        try {
                            if (!gate()) {
                                return result;
                            }
                            if (!(chain.getThisObject() instanceof View)) {
                                return result;
                            }
                            final View nsslView = (View) chain.getThisObject();

                            // 用 Configuration.orientation 判断，比 getWidth() 更早可用。
                            Object cfgObj = chain.getArg(0);
                            if (!(cfgObj instanceof android.content.res.Configuration)) {
                                return result;
                            }
                            int orientation = ((android.content.res.Configuration) cfgObj).orientation;
                            if (orientation == lastOrientation) {
                                return result;   // 方向没变，不打扰
                            }
                            lastOrientation = orientation;

                            final int n = resets++;
                            if (n < 10) {
                                L.always(TAG, "orientation -> " + orientation
                                        + "，scheduled stack refresh #" + n);
                            }
                            // 双段堆叠刷新：32ms 覆盖「首帧已布好」，
                            // 120ms 覆盖「官方 updateResources 完成」。
                            handler.postDelayed(() -> refreshStack(nsslView, request, fUpdate, fReset, n, "t1"), 32L);
                            handler.postDelayed(() -> refreshStack(nsslView, request, fUpdate, fReset, n, "t2"), 120L);

                            // ★ v310：只恢复 SNC（SharedNotificationContainer）。
                            //
                            // 主人反馈：「这次代码回滚的多了一点，之前修好了
                            //   qs 隐藏状态切横屏时通知列的显示，
                            //   现在操作后 qs 与通知都被隐藏，只剩状态栏。」
                            //   → v308 里唯一有效且无害的修复就是 SNC 这一段，
                            //     回滚时被一起删掉了。
                            //
                            // ===== 为什么单独保留 SNC，而不恢复 NPV / 窗口级 =====
                            //
                            // View 树结构（dumpsys 实测）：
                            //   NotificationShadeWindowView      ← 窗口级（禁改）
                            //     ├─ NotificationPanelView       ← 窗口级（禁改）
                            //     │    └─ MiuiNQSContainer
                            //     │         └─ MiuiQSContainer   （左栏 QS）
                            //     └─ SharedNotificationContainer （右栏通知）★ 直接子层
                            //
                            // SNC 不在 NPV 子树里，它的可见性独立于 NPV。
                            // 而且 SNC 是纯应用层 ViewGroup，不是窗口根，
                            // 改它的 visibility/alpha 不会造成 WMS Surface 脱节，
                            // 所以「幽灵磁贴」不会由它产生（v308 实测已证实：
                            // 幽灵磁贴是 v307 改 NotificationShadeWindowView 造成的）。
                            //
                            // ===== 恢复哪些属性 =====
                            //
                            // v302 的实测：竖屏「QS 隐藏态」切横屏后
                            //   SharedNotificationContainer  I.E......  ← INVISIBLE
                            // 官方把它当作「shade 收起」的一部分一起隐藏了。
                            // 所以必须把 visibility / alpha / transitionAlpha /
                            // translation / scale 全部清回可见态。
                            //
                            // 时机：60ms / 160ms / 320ms 三个点，覆盖官方多轮回写。
                            handler.postDelayed(() -> restoreSnc(nsslView, n, "t60"), 60L);
                            handler.postDelayed(() -> restoreSnc(nsslView, n, "t160"), 160L);
                            handler.postDelayed(() -> restoreSnc(nsslView, n, "t320"), 320L);
                        } catch (Throwable t) {
                            error(t);
                        }
                        return result;
                    });

            L.always(TAG, "installed NSSL.onConfigurationChanged hook"
                    + " (stack refresh only, v309);"
                    + " updateResourceValues=" + (fUpdate != null) + " resetGroup=" + (fReset != null) + ";"
                    + " prop=" + PROP + " default=1");
        } catch (Throwable t) {
            L.always(TAG, "install FAILED (fail-closed): " + t);
        }
    }
    /** notification_shade 里的 shared_notification_container id。 */
    private static final int ID_SNC = 0x7f0b0ad3;


    /**
     * ★ v310：恢复 SharedNotificationContainer（右栏通知列）的可见性。
     *
     * ===== 只碰 SNC，绝不碰窗口级容器 =====
     *
     * View 树（dumpsys 实测）：
     *   NotificationShadeWindowView   ← 窗口级 root，禁改
     *     ├─ NotificationPanelView    ← 窗口级，禁改
     *     │    └─ MiuiNQSContainer    ← 禁改
     *     │         └─ MiuiQSContainer
     *     │              └─ ... QS 子树
     *     └─ SharedNotificationContainer  ★ 我们只改这一层
     *
     * SNC 是窗口 root 的直接子层，在 NPV 之外，
     * 所以：
     *   1) NPV 被官方设成 INVISIBLE 时，SNC 依然能独立显示
     *      （这正是 v308 实测「通知能显示、只有 QS 空白」的原因）
     *   2) 它不是窗口根，改属性不影响 WMS 的 Surface 生命周期
     *      （幽灵磁贴是 v307 改 NotificationShadeWindowView 造成的）
     *
     * ===== 为什么需要 =====
     *
     * 竖屏「上滑隐藏 QS」走到完整通知列表态时，官方把整个 shade
     * 视为「已收起」，连带把 SNC 也设成 INVISIBLE。
     * 转横屏后 split 布局要求右栏必须显示通知，但这个 INVISIBLE 没有复位。
     *
     * ===== 幂等 =====
     *
     * 只在值确实异常时才写，且三个时间点重复调用安全。
     */
    private static void restoreSnc(View nsslView, int n, String phase) {
        try {
            if (!sncRestoreOn()) {
                return;
            }
            if (!nsslView.isAttachedToWindow()) {
                return;
            }
            // ★ v311：必须只在横屏执行！
            //
            // 主人实测（v310）：「有时竖屏下拉打开控制中心时，
            //   进入就是展开态而不是紧凑态」。
            //
            // 原因：v310 的 restoreSnc 只过了 gate()（它只看 prop，不看方向），
            // 于是「横屏 → 竖屏」时也会把 SNC 强设成 VISIBLE。
            // 竖屏 split shade 不生效、左右分栏不存在，
            // SNC 本该跟随 shade 一起隐藏；被强顶之后，
            // 用户下拉时 SNC 的内容提前可见 → 看起来「一进来就是展开态」。
            //
            // 注意与 gate() 的区别（别搞混）：
            //   refreshStack（堆叠重算）是【双向】都需要，所以 gate() 不判方向；
            //   restoreSnc（split 右栏恢复）只在【横屏】有意义，所以判在这里。
            if (!CcHooks.diagnosticLandscapeNow()) {
                if (n < 6) L.always(TAG, "restoreSnc " + phase + ": portrait, skip");
                return;
            }
            View root = nsslView.getRootView();
            if (root == null) {
                return;
            }
            View snc = root.findViewById(ID_SNC);
            if (snc == null) {
                if (n < 6) L.always(TAG, "restoreSnc " + phase + ": SNC not found");
                return;
            }

            int fixed = 0;
            StringBuilder detail = new StringBuilder();

            if (snc.getVisibility() != View.VISIBLE) {
                snc.setVisibility(View.VISIBLE);
                fixed++;
                detail.append(" vis");
            }
            if (snc.getAlpha() < 0.99f) {
                snc.setAlpha(1f);
                fixed++;
                detail.append(" alpha");
            }
            try {
                if (snc.getTransitionAlpha() < 0.99f) {
                    snc.setTransitionAlpha(1f);
                    fixed++;
                    detail.append(" tA");
                }
            } catch (Throwable ignored) { }
            if (Math.abs(snc.getTranslationX()) > 0.5f) {
                snc.setTranslationX(0f);
                fixed++;
                detail.append(" tX");
            }
            if (Math.abs(snc.getTranslationY()) > 0.5f) {
                snc.setTranslationY(0f);
                fixed++;
                detail.append(" tY");
            }
            if (Math.abs(snc.getScaleX() - 1f) > 0.01f) {
                snc.setScaleX(1f);
                fixed++;
                detail.append(" sx");
            }
            if (Math.abs(snc.getScaleY() - 1f) > 0.01f) {
                snc.setScaleY(1f);
                fixed++;
                detail.append(" sy");
            }

            if (n < 8) {
                L.always(TAG, "restoreSnc " + phase + ": fixed=" + fixed + detail);
            }
        } catch (Throwable t) {
            error(t);
        }
    }

    /**
     * 一次完整的堆叠刷新：
     *   1) resetGroupChildrenStackingInfo —— 清掉分组子通知残留的堆叠值
     *   2) updateResourceValues          —— 按新方向重新读取堆叠参数
     *   3) requestChildrenUpdate         —— 让 StackScrollAlgorithm 逐行重算
     *
     * 顺序不能反：先清残留、再读新参数、最后触发重算。
     */
    private static void refreshStack(View nsslView, Method request, Method updateResourceValues,
                                     Method resetGroup, int n, String phase) {
        try {
            if (!stackRefreshOn()) {
                return;
            }
            if (!nsslView.isAttachedToWindow()) {
                return;
            }
            int okReset = 0, okResource = 0;

            // 1) 逐个通知行清分组堆叠残留
            if (resetGroup != null && nsslView instanceof android.view.ViewGroup) {
                android.view.ViewGroup vg = (android.view.ViewGroup) nsslView;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    if (child == null) continue;
                    try {
                        resetGroup.invoke(null, child);
                        okReset++;
                    } catch (Throwable ignored) { }
                }
            }

            // 2) 重读堆叠参数（方向相关）
            if (updateResourceValues != null) {
                try {
                    Object inter = findInteractor(nsslView);
                    if (inter != null) {
                        updateResourceValues.invoke(inter);
                        okResource = 1;
                    }
                } catch (Throwable ignored) { }
            }

            // 3) 触发逐行重算
            request.invoke(nsslView);

            if (n < 8) {
                L.always(TAG, "stack refresh " + phase + " #" + n
                        + " resetGroups=" + okReset + " updateResourceValues=" + okResource);
            }
        } catch (Throwable t) {
            error(t);
        }
    }

    /**
     * 从 NSSL 的 injector 链里取 NotificationStackingInteractor。
     *
     * 路径（与 SplitStackRefreshBridge 中已验证的一致）：
     *   NSSL.mStackScrollAlgorithm            (StackScrollAlgorithm)
     *     → .mInjector                        (StackScrollAlgorithmInjector)
     *       → .notifStackingInteractor        (dagger.Lazy)
     *         → .get()                        → NotificationStackingInteractor
     *
     * 为什么必须走这条链：
     *   interactor 是 Dagger @Singleton，Xposed 里没有 Java.choose 那样的
     *   堆枚举能力，也无法反射构造。只能从已存在的对象图里摸过去。
     */
    private static Object findInteractor(View nsslView) {
        try {
            java.lang.reflect.Field algF = nsslView.getClass().getDeclaredField("mStackScrollAlgorithm");
            algF.setAccessible(true);
            Object alg = algF.get(nsslView);
            if (alg == null) return null;

            java.lang.reflect.Field injF = alg.getClass().getDeclaredField("mInjector");
            injF.setAccessible(true);
            Object inj = injF.get(alg);
            if (inj == null) return null;

            java.lang.reflect.Field lazyF = inj.getClass().getDeclaredField("notifStackingInteractor");
            lazyF.setAccessible(true);
            Object lazy = lazyF.get(inj);
            if (lazy == null) return null;

            // dagger.Lazy 不在本模块的编译 classpath 里，
            // 所以不能写 `instanceof dagger.Lazy`，改用反射调 get()。
            // 用接口名判断，避免误调其他类型的 get()。
            boolean isLazy = false;
            for (Class<?> itf : lazy.getClass().getInterfaces()) {
                if ("dagger.Lazy".equals(itf.getName())) { isLazy = true; break; }
            }
            if (!isLazy) {
                // 有些实现把接口放在父类上，再往上找一层
                Class<?> sup = lazy.getClass().getSuperclass();
                if (sup != null) {
                    for (Class<?> itf : sup.getInterfaces()) {
                        if ("dagger.Lazy".equals(itf.getName())) { isLazy = true; break; }
                    }
                }
            }
            if (!isLazy) return null;

            Method get = lazy.getClass().getMethod("get");
            get.setAccessible(true);
            return get.invoke(lazy);
        } catch (Throwable t) {
            return null;
        }
    }

    // ------------------------------------------------------------------ props

    /**
     * ★ 注意这里【不能】加 CcHooks.diagnosticLandscapeNow()。
     *
     * 血泪教训（v299 实测）：gate() 里带上横屏判定后，
     *   logcat 只有 "orientation -> 2"（转横屏），
     *   完全没有 "orientation -> 1"（转竖屏）——
     *   因为 diagnosticLandscapeNow() 在转成竖屏之后就已经是 false，
     *   于是整个钩子在【最需要重置的那一刻】直接跳过。
     *
     * 正确做法：旋转重置天然是双向的，
     *   横→竖和竖→横都需要 refresh 一次，
     *   所以闸门只看「功能开关」和「chrome 总闸」。
     */
    private static boolean gate() {
        return runtime() && chromeRuntimeOn();
    }

    /** v330：读某个本地子开关（默认 1=开）。 */
    private static boolean on(String key) {
        return !"0".equals(prop(key, "1"));
    }

    /** v330：面板状态恢复（setExpandedFraction / setExpanded）是否启用。 */
    private static boolean panelOn() {
        return on(L_PANEL);
    }

    /** v330：QSContent emit 改写是否启用。 */
    private static boolean qsContentOn() {
        return on(L_QSCONTENT);
    }

    /** v330：nssCoverQs 复位是否启用。 */
    private static boolean nssCoverOn() {
        return on(L_NSSCOVER);
    }

    /** v330：onConfigurationChanged 主钩子是否启用。 */
    private static boolean configOn() {
        return on(L_CONFIG);
    }

    /** v330：refreshStack 是否启用。 */
    private static boolean stackRefreshOn() {
        return on(L_STACK);
    }

    /** v330：restoreSnc 是否启用。 */
    private static boolean sncRestoreOn() {
        return on(L_SNC);
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
            chromeOn = "1".equals(prop(PROP_CHROME, "0"));
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
