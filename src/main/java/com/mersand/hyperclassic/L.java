package com.mersand.hyperclassic;

import io.github.libxposed.api.XposedModule;

/**
 * 统一日志闸门。
 *
 * GitHub 托管规范：
 *   debug 版   → BuildConfig.DEBUG = true  → 全量日志（排查用）
 *   release 版 → BuildConfig.DEBUG = false → 只输出 WARN/ERROR（静默工作）
 */
public final class L {
    private static volatile XposedModule m;

    private L() {
    }

    /** ModuleMain.onPackageReady 里绑定一次。 */
    public static void bind(XposedModule module) {
        m = module;
    }

    /**
     * @param pri libxposed 日志优先级（4=INFO, 5=WARN, 6=ERROR）
     */
    public static void log(int pri, String tag, String msg) {
        XposedModule mod = m;
        if (mod == null) {
            return;
        }
        if (com.mersand.hyperclassic.BuildConfig.DEBUG || pri >= 5) {
            try {
                mod.log(pri, tag, msg);
            } catch (Throwable ignored) {
            }
        }
    }
}