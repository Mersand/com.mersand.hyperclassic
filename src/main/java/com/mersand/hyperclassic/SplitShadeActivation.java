package com.mersand.hyperclassic;

import android.view.View;
import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;

/**
 * SplitShadeActivation —— v280 的 L1 层：旋转后的状态重同步。
 *
 * ===== v276 与 v280 的区别 =====
 *
 * v276 自己在 shouldUseSplitNotificationShade 上挂了一个「横屏返回 true」的 hook。
 * v280 把这个职责上移给 {@link SplitForceFlag}（hook Resources.getBoolean），
 * 因为那一层能同时覆盖 ShadeMode Flow 路径。本类不再重复 hook 那个方法。
 *
 * 本类现在只负责一件 v276 已经证明必要的事：
 *
 *   mSplitShadeEnabled 的写入只发生在 QuickSettingsControllerImpl.updateResources$1()
 *   被调用的一瞬间（smali L6252-6265）。旋转时若该方法未在资源配置就绪后重新执行，
 *   字段就永久固化在旋转前的值。所以在检测到方向变化时，主动重跑官方刷新链：
 *
 *     NotificationsQSContainerController.updateResources$1()   <- 这里捕获实例
 *     QuickSettingsControllerImpl.updateResources$1()         <- 就地刷新 mSplitShadeEnabled
 *
 * 两个调用都是官方公开/包内方法，纯刷新语义，不写任何自定义几何。
 *
 * ===== 开关 =====
 * debug.hyperclassic.split_enable    默认 1
 */
final class SplitShadeActivation {

    private static final String TAG = "HCSplitEnable";
    private static final String PROP = "debug.hyperclassic.split_enable";

    private static final String CLS_NQSC = "com.android.systemui.shade.NotificationsQSContainerController";
    private static final String CLS_QSCI = "com.android.systemui.shade.QuickSettingsControllerImpl";

    private static Method propGet;
    private static volatile long propAt;
    private static volatile boolean enabled = true;

    /** 最近一次见到的 NotificationsQSContainerController，用于旋转后重跑刷新。 */
    private static volatile Object lastNqsc;
    /** 最近一次见到的 QuickSettingsControllerImpl，同上。 */
    private static volatile Object lastQsci;
    private static volatile boolean lastLandscape;
    private static volatile long lastResyncAt;

    private static int reinstalls, errors;

    private SplitShadeActivation() {
    }

    static void install(XposedModule module, ClassLoader cl) {
        hookContainerCapture(module, cl);
        hookQsControllerCapture(module, cl);
        hookShadeReady(module, cl);
        L.log(4, TAG, "installed v285 prop=" + PROP
                + " default=1; rotation resync only (flag forcing moved to SplitForceFlag)");
    }

    /**
     * 就绪锚点：NotificationPanelViewController.onFinishInflate() 返回之后，
     * 视图树（含 SharedNotificationContainer）才算真正建好，DI 也已完成注入。
     *
     * 在这里把 SplitForceFlag 的强制闸门打开，既保证了 0x7f05008c 最终会返回 true，
     * 又避开了 onFinishInflate 内部那次 updateResources$1 的 NPE 地雷。
     *
     * 用完立刻重跑一次 updateResources$1，把 mSplitShadeEnabled 刷成 true。
     */
    private static void hookShadeReady(XposedModule module, ClassLoader cl) {
        try {
            Class<?> npvc = Class.forName("com.android.systemui.shade.NotificationPanelViewController", false, cl);
            Method inflate = npvc.getDeclaredMethod("onFinishInflate");
            inflate.setAccessible(true);
            module.hook((Executable) inflate)
                    .setId("hc_split_shade_ready")
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        Object r = chain.proceed();
                        try {
                            SplitForceFlag.markShadeReady("onFinishInflate");
                            // inflate 完成后补一次官方刷新，让 mSplitShadeEnabled 跟上
                            Object self = chain.getThisObject();
                            if (self != null) {
                                invokeNoArg(self, "updateResources$1");
                            }
                        } catch (Throwable t) {
                            error(t);
                        }
                        return r;
                    });
            L.always(TAG, "shade-ready hook installed: NotificationPanelViewController.onFinishInflate");
        } catch (Throwable t) {
            L.always(TAG, "shade-ready hook FAILED (fallback to 20s grace): " + t);
        }
    }

    /**
     * 捕获 NotificationsQSContainerController 实例。
     * 它自己的 updateResources$1() 会被官方在配置变更时调用，是天然的旋转锚点。
     */
    private static void hookContainerCapture(XposedModule module, ClassLoader cl) {
        try {
            Class<?> nqsc = Class.forName(CLS_NQSC, false, cl);
            Method ur = nqsc.getDeclaredMethod("updateResources$1");
            ur.setAccessible(true);
            module.hook((Executable) ur)
                    .setId("hc_split_container_capture")
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        try {
                            Object self = chain.getThisObject();
                            if (self != null) {
                                lastNqsc = self;
                                resyncIfRotated();
                            }
                        } catch (Throwable t) {
                            error(t);
                        }
                        return chain.proceed();
                    });
            L.log(4, TAG, "container capture installed: " + CLS_NQSC);
        } catch (Throwable t) {
            error(t);
            L.log(2, TAG, "container capture FAILED: " + t);
        }
    }

    /**
     * 捕获 QuickSettingsControllerImpl 实例。
     * updateResources$1() 是 mSplitShadeEnabled 的唯一写入点，捕获后可主动重跑。
     */
    private static void hookQsControllerCapture(XposedModule module, ClassLoader cl) {
        try {
            Class<?> qsci = Class.forName(CLS_QSCI, false, cl);
            Method ur = qsci.getDeclaredMethod("updateResources$1");
            ur.setAccessible(true);
            module.hook((Executable) ur)
                    .setId("hc_split_qsci_capture")
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        try {
                            Object self = chain.getThisObject();
                            if (self != null) lastQsci = self;
                        } catch (Throwable t) {
                            error(t);
                        }
                        return chain.proceed();
                    });
            L.log(4, TAG, "qs controller capture installed: " + CLS_QSCI);
        } catch (Throwable t) {
            error(t);
            L.log(2, TAG, "qs controller capture FAILED: " + t);
        }
    }

    private static void resyncIfRotated() {
        try {
            if (!runtime()) return;
            boolean land = CcHooks.diagnosticLandscapeNow();
            if (land == lastLandscape) return;
            lastLandscape = land;

            // 节流：一次旋转可能触发多次 updateResources$1，避免雪崩
            long now = android.os.SystemClock.uptimeMillis();
            if (now - lastResyncAt < 220L) return;
            lastResyncAt = now;

            reinstall();
        } catch (Throwable t) {
            error(t);
        }
    }

    /**
     * 按正确顺序重跑官方刷新链。
     *
     * 顺序很重要：先 NQSC（它内部会重算布局约束并可能再调别人的 update），
     * 再 QSCI（它读 shouldUseSplitNotificationShade 写 mSplitShadeEnabled）。
     * 由于 SplitForceFlag 已经让那个资源在横屏返回 true，这次写入会得到正确的 true。
     */
    private static void reinstall() {
        Object nqsc = lastNqsc;
        Object qsci = lastQsci;

        boolean ok = false;
        if (nqsc != null) {
            ok |= invokeNoArg(nqsc, "updateResources$1");
        }
        if (qsci != null) {
            ok |= invokeNoArg(qsci, "updateResources$1");
        }
        if (!ok) return;

        reinstalls++;
        if (reinstalls <= 8 || reinstalls % 40 == 0) {
            L.log(4, TAG, "rotation resync#" + reinstalls
                    + " landscape=" + lastLandscape
                    + " nqsc=" + (nqsc != null) + " qsci=" + (qsci != null));
        }
    }

    private static boolean invokeNoArg(Object target, String method) {
        try {
            Method m = target.getClass().getDeclaredMethod(method);
            m.setAccessible(true);
            m.invoke(target);
            return true;
        } catch (Throwable t) {
            error(t);
            return false;
        }
    }

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
            android.util.Log.w(TAG, "split-enable fallback: " + t);
        }
    }
}