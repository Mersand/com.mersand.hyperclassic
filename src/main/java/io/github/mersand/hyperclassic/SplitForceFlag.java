package io.github.mersand.hyperclassic;

import android.content.res.Resources;

import java.lang.reflect.Method;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;

/**
 * SplitForceFlag —— v280 的 L0 层：唯一的 split shade 总开关。
 *
 * ===== 为什么必须 hook Resources.getBoolean =====
 *
 * HyperOS 4 的 split shade 有两条并行的决策路径，但起点是同一个布尔资源：
 *
 *   0x7f05008c = bool/config_use_split_notification_shade   （官方 APK 里恒为 false）
 *
 *   路径 A（旧的字段型）
 *     SplitShadeStateControllerImpl.shouldUseSplitNotificationShade(Resources)
 *       -> return res.getBoolean(0x7f05008c)
 *     QuickSettingsControllerImpl.updateResources$1()
 *       L6261 invoke shouldUseSplitNotificationShade
 *       L6265 iput mSplitShadeEnabled            <- 全 SystemUI 唯一写入点
 *
 *   路径 B（新的 Flow 型）
 *     ShadeConfigRepository.legacyUseSplitShade = map { res.getBoolean(0x7f05008c) }
 *       -> ShadeModeInteractorImpl.shadeMode : StateFlow<ShadeMode>
 *       -> NotificationsQSContainerController.observeSplitShadeState$...()
 *
 * 只 hook shouldUseSplitNotificationShade 只能覆盖路径 A（这也是过去版本效果不稳的根因之一）。
 * 直接 hook Resources.getBoolean(int) 则两条路径同时生效，且它是**无状态**的
 * （每次调用现场计算），不存在「旋转那一刻算错就永久固化」的问题。
 *
 * ===== 横屏判定 =====
 * 用 CcHooks.diagnosticLandscapeNow()（内部走 ActivityThread 的 systemContext），
 * 而不是被 hook 的 Resources 本身，避免任何递归风险。
 *
 * ===== 开关 =====
 * debug.hyperclassic.split_force_flag    默认 1（装上即生效）
 * 设为 0 立刻退化到官方单栏，无需重启。
 *
 * ===== v281 性能修正 =====
 * v280 的 intercept 是「先 chain.proceed() 再判断资源 ID」，意味着每一次
 * Resources.getBoolean 调用（SystemUI 里每秒可能上百次）都要过一次
 * try/catch + 多次虚调用。现在改成「先判 ID，不匹配立刻短路」，
 * 只有目标 ID 才会走完整逻辑。这对 SystemUI 的帧率有实际意义。
 */
final class SplitForceFlag {

    private static final String TAG = "HCSplitForceFlag";
    private static final String PROP = "debug.hyperclassic.split_force_flag";

    /** bool/config_use_split_notification_shade —— 官方恒 false 的总闸。 */
    private static final int RES_SPLIT_SHADE = 0x7f05008c;

    private static Method propGet;
    private static volatile long propAt;
    private static volatile boolean enabled = true;

    /** 命中计数：真正被改写的次数。 */
    private static int forced;
    /** 未改写次数（横屏但官方已 true / 非横屏 / 开关关闭），仅用于首次日志。 */
    private static int skipped;
    private static int errors;

    // ---------------------------------------------------------------- 就绪闸门
    /**
     * ===== v285 关键修正：为什么必须加就绪闸门 =====
     *
     * v280-v284 在横屏下**无条件**把 0x7f05008c 改成 true，包括 SystemUI 初始化最早期。
     * 结果踩到一个致命地雷：
     *
     *   NotificationPanelViewController.<init>
     *     -> onFinishInflate()
     *       -> updateResources$1()
     *         -> NotificationPanelViewControllerInjector.updateResources$1()
     *           -> 走 split 分支，读 sharedNotificationContainer 字段
     *           -> 该字段此时仍是 null（DI 还没注入）
     *           -> NullPointerException: getClass() on null
     *           -> SystemUIService 创建失败 -> 进程被杀 -> 重启 -> 又横屏 -> 又崩
     *
     * 实测表现为 SystemUI 无限重启（PID 每 1.5 秒变一次）。
     *
     * 修法：只有在「视图树已经完成 inflate」之后才允许强制。
     * onFinishInflate 返回时才置位，因此 onFinishInflate 内部那次
     * updateResources$1 会拿到官方 false，走安全分支，不会碰 null 字段。
     */
    private static volatile boolean sShadeReady = false;
    /** 进程启动时刻（用于兜底：即使 latch 没置位，进程活过 20s 也认为已就绪）。 */
    private static long sProcessStartUptime = -1L;
    /** 兜底超时：20 秒。 */
    private static final long BOOT_GRACE_MS = 20000L;

    private SplitForceFlag() {
    }

    /** 由 SplitShadeActivation 在 NotificationPanelViewController.onFinishInflate 返回后调用。 */
    static void markShadeReady(String why) {
        if (sShadeReady) {
            return;
        }
        sShadeReady = true;
        L.always(TAG, "shade ready via " + why + " -> forcing ENABLED");
    }

    static boolean shadeReady() {
        return sShadeReady;
    }

    /** 就绪判定：latch 已置位，或进程已经活了足够久。 */
    private static boolean readyNow() {
        if (sShadeReady) {
            return true;
        }
        try {
            if (sProcessStartUptime < 0) {
                sProcessStartUptime = android.os.Process.getStartUptimeMillis();
            }
            long alive = android.os.SystemClock.uptimeMillis() - sProcessStartUptime;
            if (alive > BOOT_GRACE_MS) {
                sShadeReady = true;
                L.always(TAG, "shade ready via boot-grace (" + alive + "ms) -> forcing ENABLED");
                return true;
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    static void install(XposedModule module, ClassLoader cl) {
        try {
            Method getBoolean = Resources.class.getDeclaredMethod("getBoolean", int.class);
            module.hook(getBoolean)
                    .setId("hc_split_force_flag")
                    .setPriority(XposedInterface.PRIORITY_HIGHEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        // ---- 快路径：非目标资源立刻放行，零额外开销 ----
                        int id;
                        try {
                            Object a0 = chain.getArg(0);
                            if (!(a0 instanceof Number)) {
                                return chain.proceed();
                            }
                            id = ((Number) a0).intValue();
                        } catch (Throwable t) {
                            return chain.proceed();
                        }
                        if (id != RES_SPLIT_SHADE) {
                            return chain.proceed();
                        }

                        // ---- 慢路径：只对 0x7f05008c 生效 ----
                        Object result = chain.proceed();
                        try {
                            if (!runtime()) {
                                noteSkipped("switch-off");
                                return result;
                            }
                            // 官方值已经是 true（例如将来系统更新打开了）则尊重官方
                            if (Boolean.TRUE.equals(result)) {
                                noteSkipped("official-true");
                                return result;
                            }
                            // ★ v285 关键：视图树未就绪前绝不强制。
                            // 否则 NotificationPanelViewController.onFinishInflate 内部那次
                            // updateResources$1 会走 split 分支读 null 的 sharedNotificationContainer，
                            // 抛 NPE 导致 SystemUIService 创建失败 + 无限重启。
                            if (!readyNow()) {
                                noteSkipped("not-ready");
                                return result;
                            }
                            if (!CcHooks.diagnosticLandscapeNow()) {
                                noteSkipped("portrait");
                                return result;
                            }
                            forced++;
                            if (forced <= 8) {
                                L.always(TAG, "force split flag -> TRUE #" + forced
                                        + " (landscape, official=false)");
                            }
                            return Boolean.TRUE;
                        } catch (Throwable t) {
                            error(t);
                            return result;
                        }
                    });
            L.always(TAG, "installed v281 prop=" + PROP + " default=1; hook=Resources.getBoolean(0x"
                    + Integer.toHexString(RES_SPLIT_SHADE) + ") landscape-only, fast-path short-circuit");
        } catch (Throwable t) {
            L.always(TAG, "install FAILED: " + t);
        }
    }

    /** 首次出现某种跳过原因时打一条，之后静默（避免热路径刷日志）。 */
    private static void noteSkipped(String why) {
        int n = skipped++;
        if (n < 4) {
            L.always(TAG, "pass-through (reason=" + why + ") #" + n);
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
            L.always(TAG, "force-flag fallback to official value: " + t);
        }
    }
}