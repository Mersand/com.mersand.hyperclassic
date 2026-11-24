package com.mersand.hyperclassic;

import android.os.Process;
import android.os.SystemClock;
import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * ShadeQsHeightFix —— v280 L5 层：修复 split 下 QS 的 mExpansionHeight 卡 0。
 *
 * 来源：v227 已验证（A/B: h=0/fully=false/visible=false → h=1400/fully=true/visible=true）。
 *
 * 现象：竖屏展开完整 CC 后关闭 → 转横屏 → 下拉，磁贴不绘制。
 * 根因：mExpansionHeight 停在 0，官方绘制判断 `height>0` 不成立。
 *
 * 修法：只在「split + height==0 + min==0 + 面板确实展开(frac>=0.5)」时，
 * 调官方 setExpansionHeight(max)，它会自动 clamp、置 mFullyExpanded、
 * 并走上 `h > mMinExpansionHeight` 分支调用 setExpanded(true)。
 * 不写任何字段，只用官方 setter。
 *
 * prop: debug.hyperclassic.split_qs_height_fix   默认 1
 */
final class ShadeQsHeightFix {
    private static final String TAG = "HCQsHeightFix", PROP = "debug.hyperclassic.split_qs_height_fix";
    private static final String QSC = "com.android.systemui.shade.QuickSettingsControllerImpl";

    private static Method propGet, getExpanded, setExpansionHeight;
    private static Field fSplit, fHeight, fMax, fMin, fFully, fFraction, fVisible;

    private static long propAt;
    private static volatile boolean enabled = true;
    private static boolean busy;
    private static int repairs, errors;

    private ShadeQsHeightFix() {
    }

    static void install(XposedModule module, ClassLoader cl) {
        try {
            Class<?> q = Class.forName(QSC, false, cl);
            propGet = Class.forName("android.os.SystemProperties").getMethod("get", String.class, String.class);
            fSplit = field(q, "mSplitShadeEnabled");
            fHeight = field(q, "mExpansionHeight");
            fMax = field(q, "mMaxExpansionHeight");
            fMin = field(q, "mMinExpansionHeight");
            fFully = field(q, "mFullyExpanded");
            fFraction = field(q, "mShadeExpandedFraction");
            fVisible = field(q, "mVisible");
            getExpanded = q.getDeclaredMethod("getExpanded");
            getExpanded.setAccessible(true);
            setExpansionHeight = q.getDeclaredMethod("setExpansionHeight", float.class);
            setExpansionHeight.setAccessible(true);

            Method upd = q.getDeclaredMethod("updateExpansion");
            upd.setAccessible(true);
            module.hook(upd).setId("hc_qs_height_fix")
                    .setPriority(XposedInterface.PRIORITY_LOWEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.DEFAULT).intercept(chain -> {
                        Object r = chain.proceed();
                        try {
                            afterUpdate(chain.getThisObject());
                        } catch (Throwable t) {
                            error(t);
                        }
                        return r;
                    });
            L.log(4, TAG, "installed v280 prop=" + PROP
                    + " default=1 (split-only zero-height repair, official setter only)");
        } catch (Throwable t) {
            android.util.Log.w(TAG, "install fail-closed", t);
        }
    }

    private static void afterUpdate(Object q) throws Exception {
        if (busy) return;
        if (!runtime()) return;
        if (!fSplit.getBoolean(q)) return;
        float h = fHeight.getFloat(q);
        if (h != 0f) return;
        int max = fMax.getInt(q);
        if (max <= 0) return;
        if (fMin.getInt(q) != 0) return;
        // 以「面板确实展开」为门控，而不是 getExpanded()：
        // split 下 collapsed 正是故障态，需要修复时 getExpanded() 恰好是 false。
        if (fFraction.getFloat(q) < 0.5f) return;
        busy = true;
        try {
            // 官方 setter 会 clamp 到 [min,max]，于是 h=max、mFullyExpanded=true，
            // 并走上 h > mMinExpansionHeight 分支调用 setExpanded(true)。
            setExpansionHeight.invoke(q, (float) max);
            repairs++;
            if (repairs <= 12 || repairs % 40 == 0) {
                android.util.Log.i(TAG, "repair#" + repairs + " pid=" + Process.myPid()
                        + " h0->max=" + max + " expanded=" + getExpanded.invoke(q)
                        + " fully=" + fFully.getBoolean(q)
                        + " visible=" + fVisible.getBoolean(q)
                        + " frac=" + fFraction.getFloat(q));
            }
        } finally {
            busy = false;
        }
    }

    private static boolean runtime() throws Exception {
        long now = SystemClock.uptimeMillis();
        if (now - propAt > 500) {
            Object v = propGet.invoke(null, PROP, "1");
            enabled = !"0".equals(String.valueOf(v).trim());
            propAt = now;
        }
        return enabled;
    }

    private static Field field(Class<?> c, String n) throws Exception {
        Field f = c.getDeclaredField(n);
        f.setAccessible(true);
        return f;
    }

    private static void error(Throwable t) {
        if (errors++ < 4) android.util.Log.w(TAG, "fix fallback: " + t);
    }
}