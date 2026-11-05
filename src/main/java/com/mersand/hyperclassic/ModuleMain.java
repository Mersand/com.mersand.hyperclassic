package com.mersand.hyperclassic;

import android.util.Log;

import io.github.libxposed.api.XposedModule;

/**
 * HyperClassic —— HyperOS 4 经典控制中心下恢复通知玻璃材质
 *
 * libxposed API 102 入口。
 *
 * ★ 2026-10-31 14:25 重要变更：不再无条件假设「一定是经典控制中心」。
 *
 *   官方决策链（ControlCenterSettingsRepositoryImpl.smali 实证）：
 * <pre>
 *   DEBUG = SystemProperties.getInt("debug.sysui.use_control_center", 0)
 *   if (DEBUG == -1) return false;              // 强制经典
 *   if (DEBUG ==  1) return true;               // 强制新版
 *   if (FORCE_USE_QS_PANEL) return false;       // Lite2Phone 且非国际版
 *   if (forceUseControlCenter) return true;     // 平板/折叠/机型配置
 *   return Settings.System.getIntForUser("use_control_panel", 0) > 0;
 * </pre>
 *
 *   本模块复刻同一套判断，保证和系统真实行为一致：
 *     · 新版 CC → 官方自带完整玻璃链路，我们不碰（只装观测层）
 *     · 经典 CC → 才装玻璃 hook
 */
public final class ModuleMain extends XposedModule {

    private static final String TAG = "HyperClassic";
    private static final String TARGET_PKG = "com.android.systemui";

    /** 用户设置键：0 = 经典二合一，1 = 新版控制中心 */
    private static final String KEY_USE_CONTROL_PANEL = "use_control_panel";
    /** 调试属性：-1 强制经典 / 1 强制新版 / 0 或缺失 = 跟随设置 */
    private static final String PROP_USE_CC = "debug.sysui.use_control_center";

    @Override
    public void onPackageReady(PackageReadyParam param) {
        String pkg = param.getPackageName();
        if (!TARGET_PKG.equals(pkg)) {
            return;
        }

        log(Log.INFO, TAG, "module loaded, pkg=" + pkg);
        L.bind(this);

        // ★ v165：版本门控 —— 仅在 HyperOS 4 / Android 17（K80 Pro 实测环境）上完整工作
        //   非 HyperOS / HyperOS 1~3 → 跳过全部 hook
        //   HyperOS 4 + Android < 17 → 允许注册 hook，但记录「未测试」警告
        Compat.Result gate = Compat.check();
        log(Log.INFO, TAG, gate.toString());
        if (gate.status == Compat.SKIP_NOT_HYPEROS
                || gate.status == Compat.REJECT_UNSUPPORTED) {
            log(Log.WARN, TAG, "版本门控：跳过全部 hook（" + gate.reason + "）");
            return;
        }
        if (gate.status == Compat.PASS_UNTESTED) {
            log(Log.WARN, TAG, "版本门控：未测试环境，继续注册 hook，不保证可用");
        }

        ClassLoader cl = param.getClassLoader();
        try {
            // ① 用户材质设置：material_style（0=BLUR, 1=GLASS, 其他=NONE）
            int style = readIntSetting(cl, "material_style");
            boolean wantGlass = (style != 0);
            log(Log.INFO, TAG, "material_style=" + style + " -> wantGlass=" + wantGlass);

            // ② 先应用 L40：接管「控制中心样式」的决定权（内置，不依赖 KernelSU）
            //
            //  ★ 2026-10-31 14:49 重新设计：不再 hook SystemProperties，
            //    而是直接写 Settings.System.use_control_panel。
            //    理由：这是官方唯一真相源，设置页 UI 也读它，
            //    这样「设置页显示 / 系统实际 / 模块判定」三方永远一致。
            //    （旧方案 hook 属性只能骗 SystemUI，设置页仍显示旧值，
            //      导致主人看到「设置页固定新版」的困惑。）
            boolean l40 = CcHooks.applyControlCenterMode(this, cl);

            // ②b L41：接管 use_control_panel 的读写（真正起作用的层）
            //
            //  ★ 为什么 L40 不够（14:52 实证）：L40 写 0 成功了，但 47 秒后
            //    系统又写回 1，形成拉锯。L41 直接在 Settings 读写层拦截：
            //    读 → 永远返回我们想要的值；写 → 吞掉，谁也别改。
            boolean l41 = CcHooks.hookControlCenterFlow(this, cl);

            // ③ 写入完成后，直接读系统真值判断，不再自己推算
            int usePanel = readIntSetting(cl, KEY_USE_CONTROL_PANEL);
            int debugProp = 0;
            try {
                debugProp = Integer.parseInt(readProp(PROP_USE_CC, "0").trim());
            } catch (Throwable ignored) {
            }
            boolean newCc;
            if (debugProp == 1) {
                newCc = true;
            } else if (debugProp == -1) {
                newCc = false;
            } else {
                newCc = (usePanel > 0);
            }
            log(Log.INFO, TAG, "control_center new=" + newCc
                    + " [use_control_panel=" + usePanel
                    + ", prop=" + debugProp
                    + ", L40=" + l40 + ", L41=" + l41 + "]");

            if (newCc) {
                // ★ 新版控制中心：官方自带完整玻璃（NotificationRowGlassEffect /
                //   HeadsUpNotificationGlassEffect / MediaViewGlassEffect ...）。
                //   我们的覆写只会改坏官方效果（实测把悬浮通知也弄坏了）。
                //   因此只装参数观测层，不做任何视觉干预。
                log(Log.INFO, TAG, "新版控制中心 → 跳过玻璃覆写（官方自带玻璃）");
                CcHooks.installTracerOnly(this, cl);
                return;
            }

            log(Log.INFO, TAG, "经典控制中心 → 安装玻璃 hook");
            CcHooks.install(this, cl, wantGlass);
        } catch (Throwable t) {
            log(Log.ERROR, TAG, "install failed: " + t);
        }
    }

    // ------------------------------------------------------------------
    // 控制中心样式判定（与官方 smali 逻辑一一对应）
    // ------------------------------------------------------------------

    /** 当前是否会使用「新版」控制中心（true = 新版，false = 经典二合一）。 */
    private boolean isNewControlCenter(ClassLoader cl) {
        // ① debug 属性（最高优先级）
        int debug = 0;
        try {
            debug = Integer.parseInt(readProp(PROP_USE_CC, "0").trim());
        } catch (Throwable ignored) {
        }
        if (debug == -1) {
            return false;
        }
        if (debug == 1) {
            return true;
        }

        // ② 用户设置（普通手机走这条）
        int usePanel = readIntSetting(cl, KEY_USE_CONTROL_PANEL);
        if (usePanel >= 0) {
            return usePanel > 0;
        }

        // ③ 读不到：保守认为新版（不干预，避免把官方效果改坏）
        log(Log.WARN, TAG, "无法读取 " + KEY_USE_CONTROL_PANEL + "，保守按新版处理");
        return true;
    }

    // ------------------------------------------------------------------
    // 反射工具
    // ------------------------------------------------------------------

    /**
     * 读 Settings.System 的 int（读不到返回 -1）。
     *
     * ★ 2026-10-31 14:41 修复：直接复用 CcHooks.readSystemInt。
     *   原因：onPackageReady 在 Application.onCreate 之前触发，
     *   ActivityThread.currentApplication() 返回 null，
     *   导致这里每次都读到默认值 → 「跟随系统」实际失效
     *   （实证：settings get system use_control_panel = 1，但模块读到 -1）。
     *   CcHooks 那边已改为退回 getSystemContext() + 缓存 ContentResolver。
     */
    private int readIntSetting(ClassLoader cl, String key) {
        return CcHooks.readSystemInt(key, -1);
    }

    /** 读系统属性。 */
    private String readProp(String key, String def) {
        try {
            Class<?> sp = Class.forName("android.os.SystemProperties");
            Object o = sp.getMethod("get", String.class, String.class)
                    .invoke(null, key, def);
            return o == null ? def : o.toString();
        } catch (Throwable t) {
            return def;
        }
    }
}