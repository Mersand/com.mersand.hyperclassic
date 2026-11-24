package com.mersand.hyperclassic;

import android.util.Log;

import io.github.libxposed.api.XposedModule;

/**
 * 统一日志闸门（双写版）。
 *
 * ===== 为什么改成双写 =====
 *
 * 旧实现只调 mod.log()，实际走 libxposed 的模块日志通道：
 *   - 落盘到 /data/adb/lspd/log/modules_*.log
 *   - LSPosed 只在模块启用状态变化时才 flush 新文件
 *   - 结果：logcat 里一条 HC 日志都没有，调试全靠猜
 *
 * 现在同时写 android.util.Log，理由：
 *   - logcat 实时可读，配合 grep 立刻定位
 *   - SystemUI 的 Log 不需要任何权限
 *   - 崩溃前的日志能进 logcat ring buffer（mod.log 做不到）
 *
 * 双写不增加风险，唯一代价是日志量翻倍，所以加了 prop 闸门：
 *   debug.hyperclassic.log=1  → 开 INFO 及以下
 *   debug.hyperclassic.log=0  → 只输出 WARN/ERROR
 *   prop 不存在               → 跟随 BuildConfig.DEBUG
 *
 * 另外加了 always()，给"必须看到"的关键节点用，不受闸门影响。
 */
public final class L {
    /** logcat 里统一用这个前缀，方便 grep -E 'HC/' */
    private static final String PREFIX = "HC/";

    private static volatile XposedModule m;

    /** 缓存日志开关，避免每次打日志都读 SystemProperties（binder 往返很贵）。 */
    private static volatile int sGate = -1;   // -1 未探测，0 关，1 开

    private L() {
    }

    /** ModuleMain.onPackageReady 里绑定一次。 */
    public static void bind(XposedModule module) {
        m = module;
    }

    /**
     * 日志闸门探测。
     * 优先级：debug.hyperclassic.log > BuildConfig.DEBUG
     */
    private static boolean gate() {
        int g = sGate;
        if (g >= 0) {
            return g == 1;
        }
        boolean on;
        String prop = null;
        try {
            Class<?> sp = Class.forName("android.os.SystemProperties");
            java.lang.reflect.Method get = sp.getMethod("get", String.class, String.class);
            prop = (String) get.invoke(null, "debug.hyperclassic.log", "");
        } catch (Throwable ignored) {
        }
        if ("1".equals(prop)) {
            on = true;
        } else if ("0".equals(prop)) {
            on = false;
        } else {
            on = BuildConfig.DEBUG;
        }
        sGate = on ? 1 : 0;
        return on;
    }

    /** 允许外部强制刷新闸门（比如 prop 热改变后）。 */
    public static void resetGate() {
        sGate = -1;
    }

    /**
     * @param pri libxposed 日志优先级（4=INFO, 5=WARN, 6=ERROR）
     */
    public static void log(int pri, String tag, String msg) {
        // INFO/DEBUG 受闸门管；WARN/ERROR 永远输出
        if (pri < 5 && !gate()) {
            return;
        }
        emit(pri, tag, msg);
    }

    /**
     * 不受闸门影响的关键日志（WARN 级）。
     * 用于"这个必须出现在 logcat 里"的节点，比如安装失败、hook 未命中。
     */
    public static void always(String tag, String msg) {
        emit(5, tag, msg);
    }

    private static void emit(int pri, String tag, String msg) {
        String fullTag = PREFIX + tag;
        String text = msg == null ? "null" : msg;

        // ---- 通道 1：logcat（实时、可 grep） ----
        try {
            switch (pri) {
                case 6:
                    Log.e(fullTag, text);
                    break;
                case 5:
                    Log.w(fullTag, text);
                    break;
                case 4:
                    Log.i(fullTag, text);
                    break;
                case 3:
                    Log.d(fullTag, text);
                    break;
                case 2:
                    Log.v(fullTag, text);
                    break;
                default:
                    Log.i(fullTag, text);
                    break;
            }
        } catch (Throwable ignored) {
        }

        // ---- 通道 2：libxposed 模块日志（落盘 /data/adb/lspd/log/） ----
        XposedModule mod = m;
        if (mod != null) {
            try {
                mod.log(pri, tag, text);
            } catch (Throwable ignored) {
            }
        }
    }

    /** 便捷方法：INFO。 */
    public static void i(String tag, String msg) {
        log(4, tag, msg);
    }

    /** 便捷方法：WARN。 */
    public static void w(String tag, String msg) {
        log(5, tag, msg);
    }

    /** 便捷方法：ERROR。 */
    public static void e(String tag, String msg) {
        log(6, tag, msg);
    }

    /** 便捷方法：DEBUG。 */
    public static void d(String tag, String msg) {
        log(3, tag, msg);
    }
}