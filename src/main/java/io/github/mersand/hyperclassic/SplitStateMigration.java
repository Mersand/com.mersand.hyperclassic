package io.github.mersand.hyperclassic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;

/**
 * SplitStateMigration —— v286 的 L1b 层：横竖屏切换时重置 QS / 通知的共享状态机。
 *
 * ===== 主人观察到的症状 =====
 *
 *   "横屏转竖屏时，控制中心是隐藏 QS 的状态（上滑直到完全显示通知列），下滑恢复 QS"
 *   "通知被堆叠在 QS 托盘的后方/底部，两者重叠"
 *   "QS 托盘卡在上滑且即将被隐藏但未完全隐藏的半透明状态"
 *   "试图展开成完整 QS 会回弹到紧凑视图"
 *
 * ===== 实测复现（dumpsys 逐秒采样）=====
 *
 *   阶段                mExpandedHeight  mSplitShadeEnabled  topPadding  qsExpandFraction
 *   横屏展开              1440.0            true               161         0.0
 *   转竖屏 T+1 ~ T+5      3200.0            false              469         0.22853687   ← 卡住
 *   T+6（手动后）          3200.0            false             2038         1.0
 *
 * 三个数字同时错位：
 *   1. qsExpandFraction = 0.22853687  持续 5 秒（合法值应只有 0 或 1）
 *   2. topPadding = 469              竖屏收起应为 1211、展开应为 2038，469 是非法中间值
 *      → 通知从 y=469 开始绘制，直接压在 QS 磁贴下方
 *   3. 这两个值在 5 秒内完全不动，说明没有任何代码去「纠正」它
 *
 * ===== 根因（smali 已完整追到底）=====
 *
 *   NotificationScrollViewModel.qsExpandFraction
 *     <- ShadeInteractorImpl.baseShadeInteractor.getQsExpansion()   （core StateFlow，跨布局共享）
 *     <- NotificationStackScrollLayout.setQsExpansionFraction(float)
 *     <- QuickSettingsControllerImpl.updateExpansion()              ← 唯一写入点
 *
 *   updateExpansion() 尾部（smali L5704-5719）：
 *
 *     :goto_5
 *     invoke-virtual {v1}, QSCI->setClippingBounds()V
 *     iget-boolean v0, v1, QSCI->mSplitShadeEnabled:Z
 *     if-eqz v0, :cond_f
 *       invoke {v0, v5}, NSSL->setQsExpansionFraction(F)V     # split 分支，用 v5
 *       goto :goto_6
 *     :cond_f
 *       invoke {v0, v7}, NSSL->setQsExpansionFraction(F)V     # 普通分支，用 v7
 *
 *   问题：updateExpansion() 在旋转时**不会被重新调用**。
 *   mSplitShadeEnabled 由 SplitForceFlag 正确翻转成 false 了，
 *   但那个「按新模式重算 fraction 并写回」的动作从来没发生，
 *   于是 NSSL.mQsExpansionFraction 停留在横屏时算出的中间值，
 *   并且通过 getQsExpansion() 反向污染了 core StateFlow。
 *
 * ===== 修法 =====
 * 在检测到方向变化后，主动调用 QuickSettingsControllerImpl.updateExpansion()，
 * 让它按当前（已正确的）mSplitShadeEnabled 走对应分支、把 fraction 写成合法值。
 * 同时补一次 setClippingBounds()（它也在 updateExpansion 尾部）刷新裁剪区。
 *
 * 这是纯「重跑官方刷新」语义，不写任何自定义几何值，风险最低。
 *
 * ===== 开关 =====
 * debug.hyperclassic.split_state_migrate   默认 1
 */

final class SplitStateMigration {

    private static final String TAG = "HCSplitStateMig";
    private static final String PROP = "debug.hyperclassic.split_state_migrate";

    private static final String CLS_QSCI = "com.android.systemui.shade.QuickSettingsControllerImpl";

    private static Method propGet;
    private static volatile long propAt;
    private static volatile boolean enabled = true;

    /** 捕获到的 QSCI 实例。 */
    private static volatile Object sQsci;
    private static volatile boolean sLastLandscape;
    private static volatile boolean sHasLast;
    private static volatile long sLastMigrateAt;

    /** updateExpansion / setClippingBounds 的句柄（首次从实例类解析后缓存）。 */
    private static Method sUpdateExpansion;
    private static Method sSetClippingBounds;

    private static int migrations, errors;

    private SplitStateMigration() {
    }

    static void install(XposedModule module, ClassLoader cl) {
        // 1) 捕获 QSCI 实例（updateResources$1 是官方在配置变更时必调的锚点）
        try {
            Class<?> qsci = Class.forName(CLS_QSCI, false, cl);
            Method ur = qsci.getDeclaredMethod("updateResources$1");
            ur.setAccessible(true);
            module.hook(ur)
                    .setId("hc_split_state_capture")
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        try {
                            Object self = chain.getThisObject();
                            if (self != null) {
                                sQsci = self;
                                maybeMigrate();
                            }
                        } catch (Throwable t) {
                            error(t);
                        }
                        return chain.proceed();
                    });
            L.always(TAG, "QSCI capture installed");
        } catch (Throwable t) {
            L.always(TAG, "QSCI capture FAILED: " + t);
        }

        // 2) 兜底轮询：方向变化后即使 updateResources$1 没被调用，也能补一次
        startRotationWatch();
    }

    /**
     * 轻量方向轮询。
     *
     * 为什么不 hook Configuration.onChanged：
     *   SystemUI 里配置变更的分发路径有多条（DisplayManager、
     *   ProcessConfigUpdater、NotificationPanelViewController 各自一份），
     *   逐个 hook 反而更难保证覆盖。200ms 轮询一次布尔值几乎零成本，
     *   而且不依赖任何内部类名，抗版本升级。
     */
    private static void startRotationWatch() {
        Thread th = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(200L);
                    maybeMigrate();
                } catch (InterruptedException ie) {
                    return;
                } catch (Throwable ignored) {
                }
            }
        }, "HCSplitStateWatch");
        th.setDaemon(true);
        th.start();
        L.always(TAG, "rotation watch started (poll 200ms)");
    }

    private static void maybeMigrate() {
        if (!runtime()) {
            return;
        }
        boolean land = CcHooks.diagnosticLandscapeNow();
        if (sHasLast && land == sLastLandscape) {
            return;
        }
        sLastLandscape = land;
        sHasLast = true;

        // 节流：一次旋转会引发多次回调，避免雪崩
        long now = android.os.SystemClock.uptimeMillis();
        if (now - sLastMigrateAt < 400L) {
            return;
        }
        sLastMigrateAt = now;

        Object qsci = sQsci;
        if (qsci == null) {
            // 实例还没抓到，留给下一次
            sHasLast = false;
            return;
        }

        migrate(qsci, land);
    }

    /**
     * 重跑官方刷新：updateExpansion() + setClippingBounds()。
     *
     * 顺序：先 setClippingBounds（此时 mSplitShadeEnabled 已是新值，裁剪区会按新模式算），
     * 再 updateExpansion（它内部会再调一次 setClippingBounds 并写 qsExpansionFraction）。
     */
    private static void migrate(Object qsci, boolean landscape) {
        boolean ok1 = false, ok2 = false;
        try {
            if (sSetClippingBounds == null) {
                Method m = qsci.getClass().getDeclaredMethod("setClippingBounds");
                m.setAccessible(true);
                sSetClippingBounds = m;
            }
            sSetClippingBounds.invoke(qsci);
            ok1 = true;
        } catch (Throwable t) {
            error(t);
        }
        try {
            if (sUpdateExpansion == null) {
                Method m = qsci.getClass().getDeclaredMethod("updateExpansion");
                m.setAccessible(true);
                sUpdateExpansion = m;
            }
            sUpdateExpansion.invoke(qsci);
            ok2 = true;
        } catch (Throwable t) {
            error(t);
        }

        int n = ++migrations;
        if (n <= 10 || n % 20 == 0) {
            L.always(TAG, "migrate#" + n + " to " + (landscape ? "LANDSCAPE" : "PORTRAIT")
                    + " clip=" + ok1 + " expansion=" + ok2);
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
        if (errors++ < 6) {
            L.always(TAG, "fallback: " + t);
        }
    }
}