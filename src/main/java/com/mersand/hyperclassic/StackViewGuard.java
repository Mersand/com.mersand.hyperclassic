package com.mersand.hyperclassic;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * StackViewGuard —— v280 L3 层：阻止「仍应堆叠」的通知行被写成默认变换。
 *
 * 来源：v220 已验证实现（blocked=226 / seenZ=572 / seenS=1339）。
 * 只做一件事：当 ExpandableViewInjector 把 stacking 行的 scaleX 写成 1.0
 * 或 translationZ 写成 0 时拦下，保留上一个正确的变换值。
 *
 * prop: debug.hyperclassic.split_view_guard   默认 1
 */
final class StackViewGuard {
    private static final String TAG = "HCStackViewGuard", PROP = "debug.hyperclassic.split_view_guard";
    private static final String NSSL = "com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout";

    private static Class<?> rowClass;
    private static Field nsslInjector, owner, qsLazy, ccLazy, split, bar, fraction, rowStackZ, rowProgress;
    private static Method propGet, lazyGet, useCC, getInjector, extTZ, realTZ, realSX;

    private static long propAt;
    private static volatile boolean enabled = true, armed;
    private static int events, bytes, errors, blocked, seenZ, seenS, armedFrames;
    private static final Map<View, Long> LAST = new WeakHashMap<>();
    private static Handler writer;
    private static boolean inSelf;

    private StackViewGuard() {
    }

    static void install(XposedModule module, ClassLoader cl) {
        try {
            Class<?> n = Class.forName(NSSL, false, cl);
            Class<?> ni = Class.forName("com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutInjector", false, cl);
            Class<?> ctl = Class.forName("com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutControllerInjectorImpl", false, cl);
            Class<?> q = Class.forName("com.android.systemui.shade.QuickSettingsControllerImpl", false, cl);
            Class<?> cc = Class.forName("com.android.systemui.controlcenter.policy.ControlCenterControllerImpl", false, cl);
            Class<?> ri = Class.forName("com.android.systemui.statusbar.notification.row.ExpandableViewInjector", false, cl);
            Class<?> base = Class.forName("com.miui.systemui.notification.row.ExpandableViewBase", false, cl);
            Class<?> properties = Class.forName("com.miui.systemui.widget.PropertiesFrameLayout", false, cl);
            rowClass = Class.forName("com.android.systemui.statusbar.notification.row.ExpandableNotificationRow", false, cl);

            nsslInjector = field(n, "mNsslInjector");
            owner = field(ni, "nsslControllerInjector");
            qsLazy = field(ctl, "qsController");
            ccLazy = field(ctl, "controlCenterControllerImpl");
            split = field(q, "mSplitShadeEnabled");
            bar = field(q, "mBarState");
            fraction = field(q, "mShadeExpandedFraction");
            rowStackZ = field(ri, "stackingTransZ");
            rowProgress = field(ri, "stackingProgress");

            propGet = Class.forName("android.os.SystemProperties").getMethod("get", String.class, String.class);
            lazyGet = method(Class.forName("dagger.Lazy", false, cl), "get");
            useCC = method(cc, "isUseControlCenter");
            getInjector = rowClass.getMethod("getInjector");
            extTZ = method(base, "getExtTranslationZ");
            realTZ = method(properties, "getSuperTranslationZ");
            realSX = method(properties, "getSuperScaleX");

            module.hook(method(n, "dispatchDraw", Canvas.class)).setId("hc_stack_view_guard_arm")
                    .setPriority(XposedInterface.PRIORITY_HIGHEST - 60)
                    .setExceptionMode(XposedInterface.ExceptionMode.DEFAULT).intercept(chain -> {
                        try {
                            arm((ViewGroup) chain.getThisObject());
                        } catch (Throwable t) {
                            error(t);
                        }
                        return chain.proceed();
                    });

            watch(module, "setTranslationZ", "z");
            watch(module, "setScaleX", "s");
            watch(module, "setScaleY", "s");

            L.log(4, TAG, "installed v280 prop=" + PROP
                    + " default=1; View-level guard, blocks default transform on still-stacked rows only");
        } catch (Throwable t) {
            android.util.Log.w(TAG, "install fail-closed", t);
        }
    }

    private static void watch(XposedModule module, String name, String kind) {
        try {
            module.hook(method(View.class, name, float.class)).setId("hc_stack_view_guard_" + name)
                    .setPriority(XposedInterface.PRIORITY_HIGHEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.DEFAULT).intercept(chain -> {
                        try {
                            if (armed && !inSelf && runtime()) {
                                View v = (View) chain.getThisObject();
                                float value = ((Number) chain.getArg(0)).floatValue();
                                if (rowClass.isInstance(v) && !Float.isNaN(value)) {
                                    if (kind.equals("z")) seenZ++;
                                    else seenS++;
                                    boolean dflt = kind.equals("z")
                                            ? Math.abs(value) < 0.05f
                                            : Math.abs(value - 1f) < 0.0001f;
                                    if (dflt && shouldBlock(v)) {
                                        blocked++;
                                        report(v, name, value);
                                        return null; // 保留上一个正确变换；官方数学仍认为它应堆叠
                                    }
                                }
                            }
                        } catch (Throwable t) {
                            error(t);
                        }
                        return chain.proceed();
                    });
        } catch (Throwable t) {
            android.util.Log.w(TAG, "watch fail-closed " + name, t);
        }
    }

    private static boolean shouldBlock(View row) throws Exception {
        if (inSelf) return false;
        if (!row.isAttachedToWindow() || row.getVisibility() != View.VISIBLE || !row.isShown()) return false;
        if (!(row.getParent() instanceof ViewGroup)) return false;
        if (!row.getParent().getClass().getName().equals(NSSL)) return false;
        Object inj = getInjector.invoke(row);
        float progress = rowProgress.getFloat(inj), stack = rowStackZ.getFloat(inj);
        if (!Float.isFinite(progress) || progress <= 0f || !Float.isFinite(stack) || stack >= -0.5f) return false;
        float expected = row.getTranslationZ() + ((Number) extTZ.invoke(row)).floatValue();
        return Float.isFinite(expected) && expected <= -0.5f;
    }

    private static void arm(ViewGroup n) throws Exception {
        if (Looper.myLooper() != Looper.getMainLooper()) return;
        try {
            if (!runtime()) {
                armed = false;
                return;
            }
            boolean next = CcHooks.diagnosticLandscapeNow() && n.isShown()
                    && n.getResources().getConfiguration().orientation == 2
                    && n.getRootView().getWidth() > n.getRootView().getHeight();
            if (next) {
                Object ctl = owner.get(nsslInjector.get(n)), q = lazyGet.invoke(qsLazy.get(ctl));
                next = split.getBoolean(q) && bar.getInt(q) == 0 && fraction.getFloat(q) >= 0.90f
                        && Boolean.FALSE.equals(useCC.invoke(lazyGet.invoke(ccLazy.get(ctl))));
            }
            armed = next;
            if (next && ++armedFrames % 600 == 0) report(n, "heartbeat", 0f);
        } catch (Throwable t) {
            armed = false;
            throw t;
        }
    }

    private static boolean runtime() throws Exception {
        long now = SystemClock.uptimeMillis();
        if (now - propAt > 300) {
            Object v = propGet.invoke(null, PROP, "1");
            enabled = !"0".equals(String.valueOf(v).trim());
            propAt = now;
        }
        return enabled;
    }

    private static void report(View row, String kind, float value) {
        long now = SystemClock.uptimeMillis();
        Long last = LAST.get(row);
        boolean beat = kind.equals("heartbeat");
        if (events >= 150) return;
        if (!beat) {
            if (last != null && now - last < 800) return;
            LAST.put(row, now);
        }
        events++;
        StringBuilder text = new StringBuilder("pid=").append(Process.myPid()).append(" t=").append(now)
                .append(" event=").append(events).append(' ').append(kind).append(" arg=").append(value)
                .append(" blocked=").append(blocked).append(" seenZ=").append(seenZ).append(" seenS=").append(seenS);
        if (rowClass.isInstance(row)) {
            try {
                Object inj = getInjector.invoke(row);
                text.append(" progress=").append(rowProgress.getFloat(inj))
                        .append(" stackTZ=").append(rowStackZ.getFloat(inj))
                        .append(" expectedTZ=").append(row.getTranslationZ() + ((Number) extTZ.invoke(row)).floatValue())
                        .append(" actualTZ=").append(realTZ.invoke(row))
                        .append(" actualSX=").append(realSX.invoke(row));
                for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
                    String c = e.getClassName();
                    if (c.contains("statusbar.notification") || c.contains("miuix") || c.contains("shade")) {
                        text.append(" via ").append(e).append('\n');
                        break;
                    }
                }
            } catch (Throwable ignored) {
            }
        }
        emit(row, text.append('\n').toString());
    }

    private static void emit(View v, String text) {
        android.util.Log.i(TAG, text.trim());
        if (writer == null) {
            HandlerThread t = new HandlerThread("HC-StackViewGuard", Process.THREAD_PRIORITY_BACKGROUND);
            t.start();
            writer = new Handler(t.getLooper());
        }
        File file = new File(v.getContext().getCacheDir(), "hyperclassic/stackviewguard-" + Process.myPid() + ".log");
        writer.post(() -> {
            try {
                byte[] data = text.getBytes(StandardCharsets.UTF_8);
                if (bytes + data.length > 192 * 1024) return;
                File parent = file.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) return;
                try (FileOutputStream stream = new FileOutputStream(file, true)) {
                    stream.write(data);
                    bytes += data.length;
                }
            } catch (Throwable ignored) {
            }
        });
    }

    private static Field field(Class<?> c, String n) throws Exception {
        Field f = c.getDeclaredField(n);
        f.setAccessible(true);
        return f;
    }

    private static Method method(Class<?> c, String n, Class<?>... args) throws Exception {
        Method m = c.getDeclaredMethod(n, args);
        m.setAccessible(true);
        return m;
    }

    private static void error(Throwable t) {
        if (errors++ < 4) android.util.Log.w(TAG, "guard fallback; original write preserved", t);
    }
}