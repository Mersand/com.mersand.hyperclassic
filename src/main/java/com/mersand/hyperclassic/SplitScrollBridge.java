package com.mersand.hyperclassic;

import android.os.Looper;
import android.view.MotionEvent;
import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SplitScrollBridge —— v280 L2 层：通知列表在 split 横屏下可滚动（触摸补丁）。
 *
 * 来源：v212 已验证（用户确认通知滚动生效）。
 *
 * 现象：split 横屏下通知列表无法滑动（单卡片可展开）。
 * 根因：NotificationStackScrollLayoutInjector.scrollCoveringQS() 在单栏语义下
 *       认为「滚动会覆盖 QS」，于是提前返回 true，把滚动吞掉。
 *       但 split 下 QS 与通知分处两列，根本不存在覆盖。
 *
 * 修法：只在「真实 split + 横屏 + 非锁屏 + 经典版 CC」时，把该方法的 true 改成 false。
 * 用一个 ThreadLocal 做作用域，只有从 NSSL.onScrollTouch 进来的调用才改写，绝不误伤其他调用点。
 *
 * prop: debug.hyperclassic.split_scroll   默认 1
 */
final class SplitScrollBridge {
    private static final String TAG = "HCShadeScroll";
    private static final String PROP = "debug.hyperclassic.split_scroll";

    private static final ThreadLocal<Object> ACTIVE_INJECTOR = new ThreadLocal<>();
    private static final AtomicInteger CHANGES = new AtomicInteger();
    private static volatile boolean runtimeOn = true;
    private static volatile long lastCheck;

    private SplitScrollBridge() {
    }

    static void install(XposedModule module, ClassLoader cl) {
        try {
            Class<?> nssl = Class.forName("com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout", false, cl);
            Class<?> injector = Class.forName("com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutInjector", false, cl);
            Class<?> ctl = Class.forName("com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutControllerInjectorImpl", false, cl);
            Class<?> panelInjector = Class.forName("com.android.systemui.shade.NotificationPanelViewControllerInjector", false, cl);
            Class<?> qsController = Class.forName("com.android.systemui.shade.QuickSettingsControllerImpl", false, cl);
            Class<?> cc = Class.forName("com.android.systemui.controlcenter.policy.ControlCenterControllerImpl", false, cl);
            Class<?> lazy = Class.forName("dagger.Lazy", false, cl);

            Method get = lazy.getMethod("get");
            Field nsslInjector = field(nssl, "mNsslInjector");
            Field controller = field(injector, "nsslControllerInjector");
            Field panelLazy = field(ctl, "notifPanelViewInjectorLazy");
            Field qsLazy = field(ctl, "qsController");
            Field split = field(qsController, "mSplitShadeEnabled");
            Field ccOwner = field(panelInjector, "controlCenterController");
            Method isKeyguard = panelInjector.getDeclaredMethod("isOnKeyguard$2");
            Method useCC = cc.getDeclaredMethod("isUseControlCenter");
            Method touch = nssl.getDeclaredMethod("onScrollTouch", MotionEvent.class);
            Method covering = injector.getDeclaredMethod("scrollCoveringQS");
            isKeyguard.setAccessible(true);
            useCC.setAccessible(true);
            touch.setAccessible(true);
            covering.setAccessible(true);

            runtimeOn = enabled();

            // 先装「资格判定」包装：只装这一半不改变任何目标行为
            module.hook(touch).setId("hc_split_scroll_scope")
                    .setPriority(XposedInterface.PRIORITY_HIGHEST - 10)
                    .setExceptionMode(XposedInterface.ExceptionMode.DEFAULT)
                    .intercept(chain -> {
                        Object previous = ACTIVE_INJECTOR.get();
                        Object eligibleInjector = null;
                        try {
                            if (Looper.myLooper() == Looper.getMainLooper() && runtimeEnabled()
                                    && CcHooks.diagnosticLandscapeNow()) {
                                Object inj = nsslInjector.get(chain.getThisObject());
                                Object owner = controller.get(inj);
                                Object qs = get.invoke(qsLazy.get(owner));
                                if (split.getBoolean(qs)) {
                                    Object panel = get.invoke(panelLazy.get(owner));
                                    if (Boolean.FALSE.equals(isKeyguard.invoke(panel))
                                            && Boolean.FALSE.equals(useCC.invoke(ccOwner.get(panel)))) {
                                        eligibleInjector = inj;
                                    }
                                }
                            }
                        } catch (Throwable ignored) {
                            // 缺失/未就绪状态：保留原始路径
                        }
                        if (eligibleInjector == null) return chain.proceed();
                        ACTIVE_INJECTOR.set(eligibleInjector);
                        try {
                            return chain.proceed();
                        } finally {
                            if (previous == null) ACTIVE_INJECTOR.remove();
                            else ACTIVE_INJECTOR.set(previous);
                        }
                    });

            // 再装实际改写：只在 ACTIVE_INJECTOR 命中时，把 true 改成 false
            module.hook(covering).setId("hc_split_scroll_covering")
                    .setExceptionMode(XposedInterface.ExceptionMode.DEFAULT)
                    .intercept(chain -> {
                        Object original = chain.proceed();
                        if (Boolean.TRUE.equals(original) && ACTIVE_INJECTOR.get() == chain.getThisObject()) {
                            if (CHANGES.incrementAndGet() <= 12) {
                                L.log(4, TAG, "scrollCoveringQS true -> false "
                                        + "[scope=NSSL.onScrollTouch, split,landscape,unlocked,classic]");
                            }
                            return Boolean.FALSE;
                        }
                        return original;
                    });

            L.log(4, TAG, "installed v280 runtime=" + runtimeOn + " prop=" + PROP
                    + " default=1 (scroll bypass, panel gate and QS locks unchanged)");
        } catch (Throwable t) {
            L.log(2, TAG, "install failed; no scroll bypass: " + t);
        }
    }

    private static Field field(Class<?> c, String name) throws NoSuchFieldException {
        Field f = c.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    private static boolean runtimeEnabled() {
        long now = android.os.SystemClock.uptimeMillis();
        if (now - lastCheck > 500) {
            runtimeOn = enabled();
            lastCheck = now;
        }
        return runtimeOn;
    }

    private static boolean enabled() {
        try {
            Object v = Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class, String.class)
                    .invoke(null, PROP, "1");
            return !"0".equals(String.valueOf(v).trim());
        } catch (Throwable ignored) {
            return true;
        }
    }
}