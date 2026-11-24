package com.mersand.hyperclassic;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;

/**
 * SplitClipBoundsFix —— v284 的 L2c 层：修复 split 横屏下 NSSL 的错误裁剪区。
 *
 * ===== 决定性证据（dumpsys 实测）=====
 *
 *   NotificationStackScrollLayout{5353fc8 VFE 1612,0-3189,1440}   宽 1577，位于右半屏
 *   Internal state:
 *     roundedRectClipping{l=-1773 t=0 r=1427 b=1440 +y=0}           ← 全在左边！
 *     sidePaddings=53
 *     lastUpdateSidePadding=viewWidth=1577 orientation=2
 *
 * l=-1773、r=1427 对应的是「NSSL 还在 x=0」时的几何：
 *   左界 = -sidePadding - 某个额外量
 *   右界 = width - sidePadding - 额外量
 *
 * 而 split 后 NSSL.getLeft()=1612，正确边界应该是 l≈1612-53=1559, r≈3189-53=3136。
 * 现状 r=1427 < l=1559，裁剪矩形是**空集**，于是所有子 View 都被裁掉 —— 右栏空白。
 *
 * ===== 为什么它会算错 =====
 * QuickSettingsControllerImpl.updateRoundedClippingBounds 里：
 *   v1 = panelLeft  - nssl.getLeft()
 *   v3 = panelRight - nssl.getLeft()
 * 这个公式假设 NSSL 与 QS panel 共处同一坐标系。split 下两者已经并排，
 * 减 getLeft() 反而把坐标推到负值区。而且这段代码只在 QS 更新时跑，
 * 旋转后不会自动重跑，所以一直保留着旧值。
 *
 * ===== 修法 =====
 * hook NotificationStackScrollLayout.setRoundedClippingBounds(IIIIII)，
 * 直接用 NSSL 自己的真实几何覆盖 l/r：
 *   left  = nssl.sidePadding                （相对自身坐标系）
 *   right = nssl.getWidth() - nssl.sidePadding
 * top/bottom/radius 保持官方值（它们没算错）。
 *
 * 之所以在方法入口覆盖而不是在 dispatchDraw 里改，是因为
 * mRoundedClipPath 是在 setRoundedClippingBounds 内部根据这些字段构建的，
 * 必须在它构建之前就把正确值喂进去。
 *
 * ===== 开关 =====
 * debug.hyperclassic.split_clip_fix   默认 1
 */

final class SplitClipBoundsFix {

    /**
     * SplitClipBoundsFix —— v293：修复 split 横屏下 NSSL 的错误裁剪区。
     *
     * ===== 决定性证据（dumpsys 实测，v292 复现）=====
     *
     *   NotificationStackScrollLayout{dc26800 VFE 1612,0-3189,1440}   宽 1577，右半屏
     *     roundedRectClipping{l=-1773 t=0 r=1427 b=1440 +y=0}          ← 空集！
     *   ShadeHeaderContainer{5b5d329 0,0-1577,1440}                    已正确收窄
     *
     * r=1427 < 0，且 l=-1773。裁剪矩形是空集 → 右栏所有子 View（通知行 + 清除按钮）
     * 全部被裁掉 → 主人看到的「通知与按钮均无法显示」。
     *
     * ===== 错误来源（smali 已核实）=====
     *
     * QuickSettingsControllerImpl.applyClippingImmediately(IIIIZ)（smali line 344）：
     *   line 850-854:
     *       invoke-virtual {v5}, ViewGroup;->getLeft()I     # NSSL.getLeft() = 1612
     *       sub-int/2addr v1, v5                            # v1 = panelLeft - 1612
     *   这个公式假设 NSSL 与 QS panel 共处同一坐标系（AOSP 单栏）。
     *   split 下两者并排，NSSL.getLeft()=1612，减完就把坐标推进负值区。
     *
     * ===== 为什么 v284 的 hook 没生效 =====
     *
     * v284 hook 的是 NotificationStackScrollLayout.setRoundedClippingBounds(IIIIII)。
     * 但实测该 hook 一次都没被触发（日志只有 install 行），而 dumpsys 里的字段
     * 依然是坏值 —— 说明 HyperOS 走了另一条直接写字段的路径，
     * 或者 setRoundedClippingBounds 的「幂等短路」让重复调用直接 return。
     *
     * ===== 本版修法（双保险）=====
     *
     * 1) hook QuickSettingsControllerImpl.applyClippingImmediately(IIIIZ)：
     *    在 split + 横屏 + 经典控制中心时，用 NSSL 自身几何重算 left/right：
     *       left  = NSSL.getLeft() + sidePadding
     *       right = NSSL.getLeft() + NSSL.getWidth() - sidePadding
     *    即把「panel 坐标系」翻译成「屏幕坐标系」，与 NSSL 实际位置一致。
     *
     * 2) 保留原有 setRoundedClippingBounds hook 作为兜底（万一官方走那条路）。
     *
     * ===== 开关 =====
     * debug.hyperclassic.split_clip_fix   默认 1
     */

    private static final String TAG = "HCSplitClipFix";
    private static final String PROP = "debug.hyperclassic.split_clip_fix";
    private static final String PROP_IMM = "debug.hyperclassic.split_clip_immediate";

    private static Method propGet;
    private static volatile long propAt;
    private static volatile boolean enabled = true;
    private static volatile boolean immediEnabled = true;

    private static Field fLeft, fRight, fTop, fBottom;
    private static Field fRadii;

    private static int fixed, errors, immediFixed;

    private SplitClipBoundsFix() {
    }

    static void install(XposedModule module, ClassLoader cl) {
        try {
            Class<?> nsslCls = Class.forName(
                    "com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout",
                    false, cl);

            fLeft = nsslCls.getField("mRoundedRectClippingLeft");
            fRight = nsslCls.getField("mRoundedRectClippingRight");
            fTop = nsslCls.getField("mRoundedRectClippingTop");
            fBottom = nsslCls.getField("mRoundedRectClippingBottom");

            Method set = nsslCls.getDeclaredMethod("setRoundedClippingBounds",
                    int.class, int.class, int.class, int.class, int.class, int.class);
            set.setAccessible(true);

            module.hook(set)
                    .setId("hc_split_clip_fix")
                    .setPriority(XposedInterface.PRIORITY_HIGHEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        try {
                            if (!runtime() || !CcHooks.diagnosticLandscapeNow()) {
                                return chain.proceed();
                            }
                            Object self = chain.getThisObject();
                            if (!(self instanceof View)) {
                                return chain.proceed();
                            }
                            View nssl = (View) self;
                            int w = nssl.getWidth();
                            if (w <= 0) {
                                return chain.proceed();
                            }

                            // 官方参数：arg0=left arg1=top arg2=right arg3=bottom arg4=topRadius arg5=bottomRadius
                            int officialLeft = ((Number) chain.getArg(0)).intValue();
                            int officialTop = ((Number) chain.getArg(1)).intValue();
                            int officialRight = ((Number) chain.getArg(2)).intValue();
                            int officialBottom = ((Number) chain.getArg(3)).intValue();
                            int topRadius = ((Number) chain.getArg(4)).intValue();
                            int bottomRadius = ((Number) chain.getArg(5)).intValue();

                            // 判定是否已经算错：右界 <= 左界，或者右界明显小于半宽
                            boolean broken = officialRight <= officialLeft || officialRight < w / 2;
                            if (!broken) {
                                return chain.proceed();
                            }

                            // 用自身几何重算：NSSL 的 left 在 split 下就是 1612，
                            // 但裁剪字段用的是 NSSL 自己的局部坐标系，所以从 0 起算。
                            int pad = sidePaddingOf(nssl, cl);
                            int newLeft = pad;
                            int newRight = w - pad;
                            if (newRight <= newLeft) {
                                return chain.proceed();
                            }

                            int n = fixed++;
                            if (n < 8) {
                                L.always(TAG, "clip " + officialLeft + ".." + officialRight
                                        + " -> " + newLeft + ".." + newRight
                                        + " (w=" + w + " pad=" + pad + ") #" + n);
                            }
                            return chain.proceed(new Object[]{
                                    newLeft, officialTop, newRight, officialBottom,
                                    topRadius, bottomRadius
                            });
                        } catch (Throwable t) {
                            error(t);
                            return chain.proceed();
                        }
                    });

            L.always(TAG, "installed setRoundedClippingBounds hook");

            // ★ v293 主修复：直接 hook QuickSettingsControllerImpl.applyClippingImmediately
            //
            // 这是 setRoundedClippingBounds 的唯一调用者（smali line 969），
            // 而错误的 left/right 就在这个方法内部被算出来（line 850-854 减 getLeft()）。
            // 在这里改参数，等于从源头纠正，比在下游纠正字段更可靠。
            Class<?> qsci = Class.forName(
                    "com.android.systemui.shade.QuickSettingsControllerImpl", false, cl);
            Method imm = qsci.getDeclaredMethod("applyClippingImmediately",
                    int.class, int.class, int.class, int.class, boolean.class);
            imm.setAccessible(true);
            fNssl = fieldOpt2(qsci, "mNotificationStackScrollLayoutController");
            module.hook(imm)
                    .setId("hc_split_clip_immediate")
                    .setPriority(XposedInterface.PRIORITY_HIGHEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        try {
                            if (!runtimeImmediate() || !CcHooks.diagnosticLandscapeNow()) {
                                return chain.proceed();
                            }
                            Object self = chain.getThisObject();
                            if (self == null) {
                                return chain.proceed();
                            }
                            Object splitFlag = fieldGet(self, "mSplitShadeEnabled");
                            if (!Boolean.TRUE.equals(splitFlag)) {
                                return chain.proceed();
                            }
                            View nssl = nsslOf(self);
                            if (nssl == null || nssl.getWidth() <= 0) {
                                return chain.proceed();
                            }
                            int l = ((Number) chain.getArg(0)).intValue();
                            int t = ((Number) chain.getArg(1)).intValue();
                            int r = ((Number) chain.getArg(2)).intValue();
                            int b = ((Number) chain.getArg(3)).intValue();
                            boolean fifth = Boolean.TRUE.equals(chain.getArg(4));

                            // 官方值已是「相对 NSSL 坐标系」的话，右界会接近 NSSL 宽度。
                            int w = nssl.getWidth();
                            int nsslLeft = nssl.getLeft();

                            // ★ 关键：mRoundedRectClipping* 是【NSSL 自身局部坐标系】的边界，
                            //   不是屏幕坐标。dumpsys 打印的 l/r 就是这些字段原值，
                            //   而 NSSL 的屏幕位置由 getLeft()/getTop() 单独表达。
                            //
                            //   实测校正前的坏值 l=-1773 r=1427：
                            //     官方公式 v1 = panelLeft - nssl.getLeft()
                            //     panelLeft 可能是 0（panel 覆盖整屏），
                            //     nssl.getLeft()=1612 在旋转瞬间还没更新（=292），
                            //     于是 v1 = 0 - 292 = -292 …再叠加其它偏移变成 -1773。
                            //
                            //   正确做法：完全不看官方传进来的 l/r，直接用 NSSL 自己的
                            //   宽度和 sidePadding 重算局部坐标：
                            //       left  = pad
                            //       right = w - pad
                            //   这样无论官方怎么把屏幕坐标混进来，裁剪区永远等于
                            //   「NSSL 内部可见区域」，这正是在 split 下我们想要的。
                            int pad = sidePaddingOf(nssl, cl);
                            int newLeft = pad;
                            int newRight = w - pad;
                            if (newRight <= newLeft) {
                                return chain.proceed();
                            }
                            // 如果已经是对的，不重复改写（避免无意义的 setLayout/重绘）。
                            if (l == newLeft && r == newRight) {
                                return chain.proceed();
                            }
                            if (immediFixed++ < 8) {
                                L.always(TAG, "immediate clip " + l + ".." + r
                                        + " -> " + newLeft + ".." + newRight
                                        + " (nsslLeft=" + nsslLeft + " w=" + w + " pad=" + pad + ")");
                            }
                            return chain.proceed(new Object[]{newLeft, t, newRight, b, fifth});
                        } catch (Throwable tx) {
                            error(tx);
                            return chain.proceed();
                        }
                    });

            L.always(TAG, "installed applyClippingImmediately hook (primary fix)");
        } catch (Throwable t) {
            L.always(TAG, "install FAILED: " + t);
        }
    }

    /** 读 NSSL 的 sidePadding 字段（HyperOS 给 split 用的左右边距）。 */
    private static int sidePaddingOf(View nssl, ClassLoader cl) {
        try {
            if (sidePaddingField == null) {
                sidePaddingField = nssl.getClass().getField("sidePaddings");
            }
            int v = sidePaddingField.getInt(nssl);
            if (v > 0 && v < nssl.getWidth() / 2) {
                return v;
            }
        } catch (Throwable ignored) {
        }
        // 兜底：用 dumpsys 里观测到的 53
        return Math.max(0, Math.min(53, nssl.getWidth() / 4));
    }

    private static Field sidePaddingField;

    private static boolean runtime() {
        long now = android.os.SystemClock.uptimeMillis();
        if (now - propAt > 500L) {
            try {
                if (propGet == null) {
                    propGet = Class.forName("android.os.SystemProperties")
                            .getMethod("get", String.class, String.class);
                }
                Object v = propGet.invoke(null, PROP, "1");
                enabled = !"0".equals(String.valueOf(v).trim());
            } catch (Throwable ignored) {
                enabled = true;
            }
            propAt = now;
        }
        return enabled;
    }

    private static void error(Throwable t) {
        if (errors++ < 4) {
            L.always(TAG, "fallback to official: " + t);
        }
    }

    // ==================== v293 辅助 ====================

    private static Field fNssl;

    private static Field fieldOpt2(Class<?> c, String name) {
        try {
            Field f = c.getDeclaredField(name);
            f.setAccessible(true);
            return f;
        } catch (Throwable t) {
            return null;
        }
    }

    private static Object fieldGet(Object o, String name) {
        try {
            Field f = o.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(o);
        } catch (Throwable t) {
            return null;
        }
    }

    /** 从 QuickSettingsControllerImpl 实例上拿到 NSSL 的 View。 */
    private static View nsslOf(Object qsci) {
        try {
            if (fNssl == null) {
                fNssl = fieldOpt2(qsci.getClass(), "mNotificationStackScrollLayoutController");
            }
            if (fNssl == null) return null;
            Object ctrl = fNssl.get(qsci);
            if (ctrl == null) return null;
            Object view = fieldGet(ctrl, "mView");
            return view instanceof View ? (View) view : null;
        } catch (Throwable t) {
            return null;
        }
    }

    private static volatile long immPropAt;
    private static boolean runtimeImmediate() {
        long now = android.os.SystemClock.uptimeMillis();
        if (now - immPropAt > 500L) {
            try {
                if (propGet == null) {
                    propGet = Class.forName("android.os.SystemProperties")
                            .getMethod("get", String.class, String.class);
                }
                Object v = propGet.invoke(null, PROP_IMM, "1");
                immediEnabled = !"0".equals(String.valueOf(v).trim());
            } catch (Throwable ignored) {
                immediEnabled = true;
            }
            immPropAt = now;
        }
        return immediEnabled;
    }
}