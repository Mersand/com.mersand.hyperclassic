package io.github.mersand.hyperclassic;

import android.content.res.Configuration;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;

/**
 * PortraitShadeProbe —— v358：竖屏通知几何探针（只读，绝不写入）。
 *
 * ==================================================================
 * ===== 为什么需要这个探针 =====
 * ==================================================================
 *
 * 主人反馈（2026-11-25）：
 *   竖屏下拉通知面板时，整个通知视图叠在 QS 托盘下方（也就是被推到了屏幕很靠下的
 *   位置），而不是正常地跟在 QS 托盘之后。
 *
 * 关键点：**这个问题无法稳定复现**。所以不能靠猜，必须抓现场。
 *
 * ==================================================================
 * ===== 怀疑的污染源 =====
 * ==================================================================
 *
 * 横屏 split 修复里有多处会写「顶部位移 / padding」，这些 write 若没有在
 * 转竖屏时完整撤销，就会把横屏的数值带到竖屏：
 *
 *   ① SplitTopAlignBridge
 *      hook QuickCalculationControllerImpl.calculateNotificationsTopPadding
 *      返回 official + (trayY - stackY) * scale
 *      → 它只在横屏生效，但如果横屏最后一次计算结果被缓存在
 *        AmbientState.mTopPadding 里，转竖屏后可能残留。
 *
 *   ② SplitLayoutBridge 对 parent / header 的 LayoutParams 写入
 *      → 已由 v358 的 save/restore 修复覆盖。
 *
 *   ③ AmbientState.mStackY
 *      堆叠算法的基准 Y，横屏时被 SplitStackRefreshBridge 触发重算过。
 *
 *   ④ SplitRotationReset 的 requestChildrenUpdate
 *      旋转后重算，可能拿到未稳定的几何。
 *
 * ==================================================================
 * ===== 探针观测什么 =====
 * ==================================================================
 *
 * 只在【竖屏 + shade 已展开】时，每个 layout pass 采样一次（限流 500ms）：
 *
 *   AmbientState.mTopPadding    ← 通知顶部 padding（最关键）
 *   AmbientState.mStackY        ← 堆叠基准 Y
 *   AmbientState.mScrollY       ← 当前滚动量
 *   AmbientState.mShadeExpanded ← 面板展开标志
 *   NSSL.getTop() / getHeight() ← 容器自身几何
 *   NSSL.getPaddingTop()        ← View padding（与 AmbientState 分开）
 *   qsFrame.getBottom()         ← QS 托盘底部（正常应与 padding 对应）
 *   第一行通知的 getTop()       ← 最终落点（症状的直接证据）
 *
 * 输出格式（单行，方便 grep）：
 *   portraitGeom pad=? stackY=? scrollY=? shadeExp=? nsslTop=?/h=? vpadTop=?
 *                trayBottom=? row1Top=?/h=? rows=? ori=1
 *
 * ==================================================================
 * ===== 开关 =====
 * ==================================================================
 *
 *   debug.hyperclassic.probe_portrait = 1   打开探针（默认 0，release 静默）
 *
 * 只在竖屏打印，横屏不产生任何日志，不会影响主人正常使用时的日志量。
 */
final class PortraitShadeProbe {

    private static final String TAG = "HCPortraitProbe";
    private static final String PROP = "debug.hyperclassic.probe_portrait";

    /** QS 托盘的 view id（与 SplitLayoutBridge 保持一致）。 */
    private static final int QS_FRAME_ID = 0x7f0b0999;

    private static Method propGet;
    private static volatile long propAt;
    private static volatile boolean enabled;

    private static Field fAmbient;
    private static Field aTopPadding, aStackY, aScrollY, aShadeExpanded;

    // ★ v358：QS 展开高度 —— 用来区分「正常上滑隐藏」与「被覆盖」。
    //   路径：NSSL.mNsslInjector → nsslControllerInjector → qsController(Lazy) → QSC
    private static Field fNsslInjector, fCtrlInjector, fQsLazy, fQsExpansionHeight;
    private static Method lazyGet;
    private static WeakReference<Object> qscRef = new WeakReference<>(null);

    private static long lastLog;
    private static int logs, errors;

    private PortraitShadeProbe() {
    }

    static void install(XposedModule module, ClassLoader cl) {
        try {
            Class<?> nssl = Class.forName(
                    "com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout",
                    false, cl);
            Class<?> ambient = Class.forName(
                    "com.android.systemui.statusbar.notification.stack.AmbientState",
                    false, cl);

            fAmbient = nssl.getField("mAmbientState");
            aTopPadding = ambient.getField("mTopPadding");
            aStackY = ambient.getField("mStackY");
            aScrollY = ambient.getField("mScrollY");
            aShadeExpanded = ambient.getField("mShadeExpanded");

            // ★ v358：QS 展开高度。用来区分「上滑把 QS 收起了」和「QS 还在但通知被压上来」。
            //
            // 链路（smali 已核实）：
            //   NSSL.mNsslInjector              : NotificationStackScrollLayoutInjector
            //     → nsslControllerInjector      : NotificationStackScrollLayoutControllerInjectorImpl
            //       → qsController (dagger.Lazy) → QuickSettingsControllerImpl
            //          .mExpansionHeight : F  ← float（v358 实测确认，不是 mQsExpansionHeight）
            //
            // 用 WeakReference 缓存 QSC 实例，避免每次采样都反射走一遍。
            try {
                Class<?> inj = Class.forName(
                        "com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutInjector",
                        false, cl);
                Class<?> ctl = Class.forName(
                        "com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutControllerInjectorImpl",
                        false, cl);
                Class<?> qsc = Class.forName(
                        "com.android.systemui.shade.QuickSettingsControllerImpl", false, cl);
                fNsslInjector = nssl.getDeclaredField("mNsslInjector");
                fNsslInjector.setAccessible(true);
                fCtrlInjector = inj.getDeclaredField("nsslControllerInjector");
                fCtrlInjector.setAccessible(true);
                fQsLazy = ctl.getDeclaredField("qsController");
                fQsLazy.setAccessible(true);
                fQsExpansionHeight = qsc.getDeclaredField("mExpansionHeight");
                fQsExpansionHeight.setAccessible(true);
                lazyGet = Class.forName("dagger.Lazy", false, cl).getMethod("get");
            } catch (Throwable t) {
                android.util.Log.w(TAG, "qs expansion chain unavailable: " + t);
            }

            propGet = Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class, String.class);

            // ★ 用 onLayout 作为采样点。
            //
            // 为什么不用 onPreDraw：
            //   onPreDraw 每帧都跑（包括动画中间态），日志会被刷爆，
            //   而且动画过程中的数值没有诊断价值。
            //   onLayout 只在几何真的变化时才跑，正好对应「布局算完了」这一刻，
            //   我们能看到最终落点。
            //
            // 为什么不用 setTranslation / updateExpansion：
            //   那些是「过程」，onLayout 是「结果」。我们要抓的是结果。
            Method onLayout = nssl.getDeclaredMethod(
                    "onLayout", boolean.class, int.class, int.class, int.class, int.class);
            onLayout.setAccessible(true);

            module.hook(onLayout).setId("hc_portrait_probe")
                    .setPriority(XposedInterface.PRIORITY_LOWEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.DEFAULT)
                    .intercept(chain -> {
                        Object result = chain.proceed();
                        try {
                            if (runtime()) {
                                sample(chain.getThisObject());
                            }
                        } catch (Throwable t) {
                            error(t);
                        }
                        return result;
                    });

            // ★ v358：写入侧探针 —— 在 updateTopPadding 入口记录「谁传了什么值」。
            //
            // 为什么必须抓写入侧而不是只看结果：
            //   主人的问题（通知贴到屏幕顶部，pad 变成 ~160px 横屏状态栏高度）
            //   **无法稳定复现**。只看 onLayout 的结果只能知道「坏了」，
            //   不知道「谁写坏的」。必须在写入那一刻拿到调用栈。
            //
            // 只在值「明显异常」时打印栈（默认 < 200px 视为可疑），
            // 正常值（469/1211/2038）绝不打印，日志量可控。
            Method updPad = nssl.getDeclaredMethod(
                    "updateTopPadding", float.class, boolean.class);
            updPad.setAccessible(true);

            module.hook(updPad).setId("hc_portrait_probe_writer")
                    .setPriority(XposedInterface.PRIORITY_HIGHEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.DEFAULT)
                    .intercept(chain -> {
                        try {
                            if (runtime()) {
                                float val = ((Number) chain.getArg(0)).floatValue();
                                // 传 this（NSSL 实例），用于解析 QSC 拿 QS 展开高度
                                noteWrite(chain.getThisObject(), val);
                            }
                        } catch (Throwable t) {
                            error(t);
                        }
                        return chain.proceed();
                    });

            L.log(4, TAG, "installed v358 prop=" + PROP + " default=0 (portrait-only read-only probe)");
        } catch (Throwable t) {
            android.util.Log.w(TAG, "install fail-closed", t);
        }
    }

    /**
     * ★ v358：写入侧探针 —— 记录「谁把 topPadding 写成了什么值」，并区分正常/异常。
     *
     * ==================================================================
     * ===== 主人指出的关键区分（务必遵守）=====
     * ==================================================================
     *
     * 「小 padding」本身**不等于 bug**。有两种完全不同的情形：
     *
     *   ① **正常**：用户上滑把 QS 托盘收起 / 隐藏了
     *      → QS 不再占据顶部，通知自然可以贴到屏幕顶部
     *      → mQsExpansionHeight 变小（接近 0）
     *      → 这是官方设计，绝不能干预
     *
     *   ② **异常**：QS 托盘**仍然可见**，通知却被压到它下面（甚至盖住它）
     *      → mQsExpansionHeight 仍然很大（托盘还在）
     *      → 但 padding 却是 ~160px（横屏状态栏高度残留）
     *      → 这才是主人报的 bug
     *
     * 所以判定必须是【QS 仍展开 + padding 很小】这个组合，
     * 单看 padding 会误伤正常的上滑行为。
     *
     * ==================================================================
     * ===== 判定阈值 =====
     * ==================================================================
     *
     *   padding < 200px        ：顶部几乎没有留白（可疑）
     *   qsExpandHeight > 400px ：QS 托盘仍然占据显著高度（可见）
     *   两者同时成立 → 打印 SUSPICIOUS 并附调用栈
     *
     * 正常值参考（主人设备实测）：
     *   紧凑 QS：pad=469  qsH 约 469
     *   完全展开：pad=1211~2038
     */
    private static void noteWrite(Object nsslObj, float val) {
        // 只在竖屏检查（横屏本来就是 161 左右，不是 bug）
        int ori;
        try {
            ori = android.content.res.Resources.getSystem()
                    .getConfiguration().orientation;
        } catch (Throwable t) {
            return;
        }
        if (ori != android.content.res.Configuration.ORIENTATION_PORTRAIT) return;

        // ---- 判定 1：padding 必须显著小 ----
        if (val >= 200f || val < 0f) {
            // 值恢复正常 → 清除「持续异常」计时，避免下次误判
            badSince = 0L;
            return;
        }

        // ---- 判定 2：QS 托盘必须仍然可见 ----
        //
        // 这一条是主人要求的核心区分：
        //   QS 已上滑隐藏 → 小 padding 正常，放行（不打印、不记录）
        //   QS 仍可见     → 小 padding 异常，抓栈
        int qsH = -1;
        try {
            qsH = qsExpandHeight(nsslObj);
        } catch (Throwable ignored) {
        }
        if (qsH <= 400) {
            // QS 已经收起来了，通知贴顶是正常行为
            return;
        }

        // ---- 判定 3：持续性 ----
        //
        // 为什么需要这一条：
        //   面板折叠/展开的**动画中间态**也会短暂写入 pad=0，
        //   但那时 QS 高度可能还没跟着降下来，属于瞬时正常。
        //   主人报的 bug 是「持续停在错误的 padding」，
        //   所以必须要求这个组合连续出现（>=300ms）才算异常。
        long now = SystemClock.uptimeMillis();
        if (now - badSince == 0L || now < badSince) {
            badSince = now;
            return;             // 第一次出现，先观察
        }
        if (now - badSince < 300L) {
            return;             // 还没持续够久，可能是动画
        }

        int n = writeCount++;
        if (n >= 8) return;

        StringBuilder sb = new StringBuilder(96);
        sb.append("SUSPICIOUS pad=").append((int) val)
                .append(" but qsH=").append(qsH)
                .append(" (QS still visible!) portrait #").append(n)
                .append(" stack:");

        StackTraceElement[] st = new Throwable().getStackTrace();
        int shown = 0;
        for (int i = 0; i < st.length && shown < 10; i++) {
            StackTraceElement e = st[i];
            String cls = e.getClassName();
            if (cls.startsWith("de.robv.android.xposed") || cls.startsWith("io.github.libxposed")) continue;
            if (cls.equals(PortraitShadeProbe.class.getName())) continue;
            sb.append("\n    @ ").append(cls).append('#').append(e.getMethodName())
                    .append(':').append(e.getLineNumber());
            shown++;
        }
        android.util.Log.w(TAG, sb.toString());
    }

    /**
     * ★ v358：读取 QS 当前展开高度（px）。
     *
     * 返回 -1 表示链路不可用（此时不参与判定，避免误判）。
     */
    private static int qsExpandHeight(Object nsslObj) {
        if (fNsslInjector == null || fCtrlInjector == null
                || fQsLazy == null || fQsExpansionHeight == null || lazyGet == null) {
            return -1;
        }
        try {
            Object qsc = qscRef.get();
            if (qsc == null) {
                Object inj = fNsslInjector.get(nsslObj);
                if (inj == null) return -1;
                Object ctl = fCtrlInjector.get(inj);
                if (ctl == null) return -1;
                Object lazy = fQsLazy.get(ctl);
                if (lazy == null) return -1;
                qsc = lazyGet.invoke(lazy);
                if (qsc == null) return -1;
                qscRef = new WeakReference<>(qsc);
            }
            return (int) fQsExpansionHeight.getFloat(qsc);
        } catch (Throwable t) {
            return -1;
        }
    }

    /** 写入侧可疑日志计数（独立于 sample 的 logs）。 */
    private static int writeCount;

    /** ★ v358：异常组合的起始时刻（0 表示当前不在异常态）。 */
    private static volatile long badSince;

    /**
     * 采样一次竖屏几何。
     *
     * 只在「竖屏 + 面板展开」时打印，其余情况静默返回。
     */
    private static void sample(Object self) throws Exception {
        if (!(self instanceof View)) return;
        View nssl = (View) self;

        // ---- 门控 1：必须竖屏 ----
        int ori;
        try {
            ori = nssl.getResources().getConfiguration().orientation;
        } catch (Throwable t) {
            return;
        }
        if (ori != Configuration.ORIENTATION_PORTRAIT) return;

        // ---- 门控 2：必须已 attach 且有尺寸 ----
        if (!nssl.isAttachedToWindow() || nssl.getHeight() <= 0) return;

        Object amb = fAmbient.get(self);
        if (amb == null) return;

        // ---- 门控 3：面板必须展开（否则测的是收起态，无意义）----
        boolean shadeExpanded = aShadeExpanded.getBoolean(amb);
        if (!shadeExpanded) return;
// 限流：500ms 一条，避免刷爆
        long now = SystemClock.uptimeMillis();
        if (now - lastLog < 500) return;
        lastLog = now;

        int topPadding = aTopPadding.getInt(amb);
        float stackY = aStackY.getFloat(amb);
        int scrollY = aScrollY.getInt(amb);

        int nsslTop = getAbsTop(nssl);
        int nsslH = nssl.getHeight();
        int vpadTop = nssl.getPaddingTop();
        // ★ v358：translationY 必须单独记。
        //   getTop() 不含 translation，而 split 残留最可能就藏在这里。
        float nsslTY = nssl.getTranslationY();

        // QS 展开高度 + 托盘底部：正常布局里通知区应该紧接它
        int trayBottom = -1;
        int qsExpandH = -1;
        View root = null;
        try {
            root = nssl.getRootView();
            View tray = root.findViewById(QS_FRAME_ID);
            if (tray != null && tray.getHeight() > 0) {
                trayBottom = getAbsTop(tray) + tray.getHeight();
            }
        } catch (Throwable ignored) {
        }
        // ★ v358：QS 展开高度（区分「上滑隐藏」与「被覆盖」的关键信号）
        try {
            qsExpandH = qsExpandHeight(self);
        } catch (Throwable ignored) {
        }

        // 第一条可见通知的落点（症状的直接证据）
        //
        // ★ v358 关键修正：StackScrollAlgorithm 用 translationY 定位每一行，
        //   getTop() 恒为 0！之前只累加 getTop() 得到 row1Top=0 是假数据。
        //   必须同时累加 translationY，才是主人肉眼看到的位置。
        int row1Top = -1, row1H = -1, row1LayoutTop = -1, rows = 0;
        float row1TY = 0f;
        try {
            if (nssl instanceof ViewGroup) {
                ViewGroup g = (ViewGroup) nssl;
                for (int i = 0; i < g.getChildCount(); i++) {
                    View c = g.getChildAt(i);
                    if (c == null || c.getVisibility() != View.VISIBLE) continue;
                    if (c.getHeight() <= 0) continue;
                    rows++;
                    if (row1Top < 0) {
                        row1LayoutTop = c.getTop();
                        row1TY = c.getTranslationY();
                        row1Top = getVisualTop(c);
                        row1H = c.getHeight();
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        // 亮度条视觉位置：它是「通知被推下去」时最先暴露的指示器
        int sliderTop = -1;
        try {
            // 复用上面已解析的 root（避免重复定义）
            View slider = root == null ? null : root.findViewById(0x7f0b0a4e);
            if (slider != null && slider.getHeight() > 0) {
                sliderTop = getVisualTop(slider);
            }
        } catch (Throwable ignored) {
        }

        int n = logs++;
        StringBuilder sb = new StringBuilder(200);
        sb.append("portraitGeom")
                .append(" pad=").append(topPadding)
                .append(" stackY=").append((int) stackY)
                .append(" scrollY=").append(scrollY)
                .append(" shadeExp=").append(shadeExpanded)
                .append(" nssl=").append(nsslTop).append("/h").append(nsslH)
                .append("/ty").append((int) nsslTY)
                .append(" vpad=").append(vpadTop)
                .append(" qsH=").append(qsExpandH)
                .append(" trayBot=").append(trayBottom)
                .append(" slider=").append(sliderTop)
                .append(" row1=(").append(row1LayoutTop).append("+")
                .append((int) row1TY).append(")=").append(row1Top)
                .append("/h").append(row1H)
                .append(" rows=").append(rows)
                .append(" #").append(n);

        // 前 20 条无条件打印（抓冷启动现场），之后每 20 条一条（避免长跑刷屏）
        if (n < 20 || n % 20 == 0) {
            android.util.Log.i(TAG, sb.toString());
        }
    }

    /** 相对根 View 的绝对 Y（跨过所有 parent 累加 getTop）。 */
    private static int getAbsTop(View v) {
        int top = v.getTop();
        for (android.view.ViewParent p = v.getParent(); p instanceof View; p = p.getParent()) {
            top += ((View) p).getTop();
        }
        return top;
    }

    /**
     * ★ v358：视觉 Y —— 把 translationY 也算进去。
     *
     * 为什么必须有这个：
     *   StackScrollAlgorithm 完全不改子 View 的 layout position，
     *   它只设置 translationY。所以：
     *     getTop()         → 恒为 0（假数据）
     *     getTranslationY() → 真实偏移（几百到几千 px）
     *   主人肉眼看到的位置是 layoutTop + translationY，两者缺一不可。
     */
    private static int getVisualTop(View v) {
        float top = v.getTop() + v.getTranslationY();
        for (android.view.ViewParent p = v.getParent(); p instanceof View; p = p.getParent()) {
            View pv = (View) p;
            top += pv.getTop() + pv.getTranslationY();
            // 到达 NSSL 就停：它的坐标已由 nsslTop 单独记录
            if (pv.getId() == 0x7f0b0877) break;
        }
        return Math.round(top);
    }

    private static boolean runtime() throws Exception {
        long now = SystemClock.uptimeMillis();
        if (now - propAt > 800) {
            Object v = propGet.invoke(null, PROP, "0");
            enabled = !"0".equals(String.valueOf(v).trim());
            propAt = now;
        }
        return enabled;
    }

    private static void error(Throwable t) {
        if (errors++ < 4) android.util.Log.w(TAG, "probe fallback: " + t);
    }
}
