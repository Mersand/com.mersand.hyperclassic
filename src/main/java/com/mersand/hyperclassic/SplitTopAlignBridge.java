package com.mersand.hyperclassic;

import android.os.Looper;
import android.view.View;
import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * SplitTopAlignBridge —— v280 L4 层：把通知列顶部对齐到 QS 托盘上沿。
 *
 * 来源：v225 已验证（像素实测 -6px → -1px）。
 * hook QuickSettingsControllerImpl.calculateNotificationsTopPadding(float, boolean, int)
 * 返回官方值 + 「托盘 Y - 通知栈 Y」的差值，按 expansion fraction 渐变。
 *
 * prop: debug.hyperclassic.split_top_align        默认 1
 *       debug.hyperclassic.split_top_align_extra  默认 0（px 微调）
 */
final class SplitTopAlignBridge {
    private static final String TAG = "HCSplitTopAlign", PROP = "debug.hyperclassic.split_top_align";
    private static final String NSSL = "com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout";

    private static Method propGet, lazyGet;
    private static Field nsslInjector, owner, qsLazy, fragment, container, bgField, split, barState, fraction;
    private static volatile WeakReference<View> cachedContainer = new WeakReference<>(null);
    private static volatile WeakReference<View> cachedStack = new WeakReference<>(null);

    private static long propAt;
    private static volatile boolean enabled = true;
    private static int logs, errors;
    private static long lastLog;

    private SplitTopAlignBridge() {
    }

    static void install(XposedModule module, ClassLoader cl) {
        try {
            Class<?> n = Class.forName(NSSL, false, cl);
            Class<?> ni = Class.forName("com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutInjector", false, cl);
            Class<?> ctl = Class.forName("com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutControllerInjectorImpl", false, cl);
            Class<?> q = Class.forName("com.android.systemui.shade.QuickSettingsControllerImpl", false, cl);
            Class<?> f = Class.forName("com.android.systemui.qs.MiuiQSFragment", false, cl);
            Class<?> c = Class.forName("com.android.systemui.qs.MiuiQSContainer", false, cl);

            propGet = Class.forName("android.os.SystemProperties").getMethod("get", String.class, String.class);
            lazyGet = Class.forName("dagger.Lazy", false, cl).getMethod("get");
            nsslInjector = fieldCls(n, "mNsslInjector");
            owner = fieldCls(ni, "nsslControllerInjector");
            qsLazy = fieldCls(ctl, "qsController");
            fragment = fieldCls(q, "mQs");
            container = fieldCls(f, "qsContainer");
            bgField = fieldCls(c, "qsBackground");
            split = fieldCls(q, "mSplitShadeEnabled");
            barState = fieldCls(q, "mBarState");
            fraction = fieldCls(q, "mShadeExpandedFraction");

            Method padding = q.getDeclaredMethod("calculateNotificationsTopPadding", float.class, boolean.class, int.class);
            padding.setAccessible(true);
            module.hook(padding).setId("hc_split_top_align")
                    .setPriority(XposedInterface.PRIORITY_HIGHEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.DEFAULT).intercept(chain -> {
                        Object original = chain.proceed();
                        try {
                            Object qsc = chain.getThisObject();
                            if (!runtime() || Looper.myLooper() != Looper.getMainLooper()) return original;
                            if (!CcHooks.diagnosticLandscapeNow()) return original;
                            if (!split.getBoolean(qsc) || barState.getInt(qsc) != 0) return original;
                            float frac = fraction.getFloat(qsc);
                            if (!(frac > 0.90f)) return original;
                            // 稳定的展开态报告 0.96 而非 1.0，所以按 0.96 做满
                            float scale = (frac - 0.90f) / 0.06f;
                            if (scale > 1f) scale = 1f;
                            View stack = resolveStack(qsc);
                            View tray = resolveTray(qsc);
                            if (stack == null || tray == null) return original;
                            if (stack.getHeight() <= 0 || tray.getHeight() <= 0) return original;
                            int delta = layoutY(tray) - layoutY(stack);
                            if (delta <= 0 || delta >= stack.getHeight() / 2) return original;
                            int extra = extraOffset();
                            float base = original instanceof Float ? (Float) original : 0f;
                            float result = base + (delta + extra) * scale;
                            note("delta=" + delta + " extra=" + extra + " frac=" + frac
                                    + " scale=" + scale + " base=" + base + " result=" + result);
                            return result;
                        } catch (Throwable t) {
                            error(t);
                        }
                        return original;
                    });
            L.log(4, TAG, "installed v280; prop=" + PROP + " default=1; also " + PROP
                    + "_extra for pixel trim; split landscape only, official value untouched elsewhere");
        } catch (Throwable t) {
            android.util.Log.w(TAG, "install fail-closed", t);
        }
    }

    private static View resolveStack(Object qsc) throws Exception {
        View cached = cachedStack.get();
        if (cached != null && cached.isAttachedToWindow()) return cached;
        View c = resolveContainer(qsc);
        if (c == null) return null;
        View stack = c.getRootView().findViewById(0x7f0b0877);
        if (stack == null) return null;
        cachedStack = new WeakReference<>(stack);
        return stack;
    }

    private static View resolveContainer(Object qsc) throws Exception {
        View cached = cachedContainer.get();
        if (cached != null && cached.isAttachedToWindow()) return cached;
        Object f = fragment.get(qsc);
        if (f == null) return null;
        Object c = container.get(f);
        if (!(c instanceof View)) return null;
        cachedContainer = new WeakReference<>((View) c);
        return (View) c;
    }

    private static View resolveTray(Object qsc) throws Exception {
        View c = resolveContainer(qsc);
        return c == null ? null : (View) bgField.get(c);
    }

    private static boolean runtime() throws Exception {
        long now = android.os.SystemClock.uptimeMillis();
        if (now - propAt > 500) {
            Object v = propGet.invoke(null, PROP, "1");
            enabled = !"0".equals(String.valueOf(v).trim());
            propAt = now;
        }
        return enabled;
    }

    private static void note(String text) {
        if (logs >= 10) return;
        long now = android.os.SystemClock.uptimeMillis();
        if (now - lastLog < 400) return;
        lastLog = now;
        logs++;
        android.util.Log.i(TAG, "align " + text);
    }

    private static int layoutY(View v) {
        int r = v.getTop();
        for (android.view.ViewParent p = v.getParent(); p instanceof View; p = p.getParent()) r += ((View) p).getTop();
        return r;
    }

    private static long extraAt;
    private static volatile int extra;

    private static int extraOffset() {
        long now = android.os.SystemClock.uptimeMillis();
        if (now - extraAt > 400) {
            try {
                Object v = Class.forName("android.os.SystemProperties")
                        .getMethod("get", String.class, String.class)
                        .invoke(null, "debug.hyperclassic.split_top_align_extra", "0");
                extra = Integer.parseInt(String.valueOf(v).trim());
            } catch (Throwable ignored) {
                extra = 0;
            }
            extraAt = now;
        }
        return extra;
    }

    private static Field fieldCls(Class<?> c, String n) throws Exception {
        Field f = c.getDeclaredField(n);
        f.setAccessible(true);
        return f;
    }

    private static void error(Throwable t) {
        if (errors++ < 4) android.util.Log.w(TAG, "align fallback to official value: " + t);
    }
}