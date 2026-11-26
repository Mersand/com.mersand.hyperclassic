package io.github.mersand.hyperclassic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;

/**
 * HunProbe —— 弹出通知（Heads-Up Notification）探针 + 测试通知发送器
 *
 * ===== 为什么需要它 =====
 *
 * 主人反馈（2026-11-24）：横屏下弹出通知的背景合成有问题，
 * 玻璃效果不跟随卡片移动。典型场景是横滑清除 HUN 时，
 * 会有一层薄玻璃背景留在原地，然后上滑消失。
 *
 * 关键线索（主人提供）：弹出通知原本定位在**屏幕居中**，
 * 而 split shade 开启后按 AOSP 规则处于**屏幕右侧**。
 *
 * ⇒ 怀疑：HUN 卡片本体用了 split 的右侧坐标，
 *   但玻璃背景层（或它的绘制缓存）仍按居中/全屏计算。
 *
 * ===== 本类提供什么 =====
 *
 * ① 测试通知发送（sendTestHun）
 *    一条真实 heads-up 通知，带 channel 与 group，
 *    可以让主人随时复现，不用等别人发消息。
 *
 * ② HUN 视图探针（install）
 *    在关键节点打印 HUN 行与它的背景层的几何信息：
 *      · ExpandableNotificationRow 的 left/top/width/height/translationX
 *      · NotificationBackgroundView 的同类信息
 *      · 两者的差值（= 玻璃不跟随的直接证据）
 *
 * 探针只读，不改变任何行为。开关：debug.hyperclassic.probe_hun
 */
final class HunProbe {

    private static final String TAG = "HCHunProbe";
    /** 探针开关（默认关，避免刷屏）。 */
    private static final String PROP_PROBE = "debug.hyperclassic.probe_hun";
    /** 详细 dump 开关（含子 View 树）。 */
    private static final String PROP_DUMP = "debug.hyperclassic.probe_hun_dump";

    private static final AtomicInteger LOGS = new AtomicInteger();
    private static final AtomicInteger DUMPS = new AtomicInteger();
    /** ★ v354：进入计数（证明 hook 是否命中，与日志限流解耦）。 */
    private static final AtomicInteger ENTRIES = new AtomicInteger();
    private static final AtomicInteger MOVES = new AtomicInteger();
    /** ★ v355：修复次数计数。 */
    private static final AtomicInteger FIXES = new AtomicInteger();
    /** ★ v356：顶部留白修复日志计数。 */
    private static final AtomicInteger PAD_LOGS = new AtomicInteger();

    /** 测试通知 id 基址（每条 +1，避免互相覆盖）。 */
    private static final int TEST_ID_BASE = 0x4A0000;
    private static final AtomicInteger TEST_SEQ = new AtomicInteger();

    private static final String CHANNEL_ID = "hc_test_hun";

    /** ★ v355：HUN 横滑修复开关（默认开）。
     *
     * 病变：ExpandableNotificationRow.setTranslation(F) 有两条路径
     *   ① mDismissUsingRowTranslationX == true  → setTranslationX(dx) 整行一起动
     *   ② == false                              → 只遍历 mTranslateableViews
     *                                             逐个 setTranslationX(dx)，
     *                                             再给 shelfIcon 反向 -dx
     * HUN 弹出时 NSSL.mIsExpanded == false，于是
     * updateDismissBehavior() 把 mDismissUsingRowTranslationX 算成 false
     * → 走路径 ② → 玻璃胶囊层不在 mTranslateableViews 里 → 留在原地；
     *   同时 shelfIcon 被反向平移 → 状态栏那个小图标跟着一起跑。
     *
     * 修法：对 HUN 行强制 setDismissUsingRowTranslationX(true, true)，
     * 让它走路径 ① —— 整行（含玻璃）一起动，且跳过 shelfIcon 反向平移。
     */
    private static final String PROP_FIX = "debug.hyperclassic.hun_fix";
    /** ★ v355：shelf icon 是否跟随（默认 0 = 不跟随，即官方反向补偿也一并跳过）。 */
    private static final String PROP_SHELF = "debug.hyperclassic.hun_shelf_follow";

    /** ★ v356：HUN 顶部留白模式。
     *
     * 病变：HeadsUpManagerInjectorImplKt.miuiHeadsUpInset(ctx) 在横屏走
     *   cutout.getSafeInsetTop() + heads_up_status_bar_padding
     * 横屏时 safeInsetTop 通常是 0（挖孔在侧边），base 只有 7.27dp≈27px，
     * 于是 HUN 直接贴在屏幕上沿，压住状态栏 —— 探针里 row ty=27.0 就是这个 27px。
     *
     * 竖屏分支用 SystemBarUtils.getStatusBarHeight(ctx) + base，是正常的。
     *
     * 模式：
     *   0 = 关闭修复（官方原值）
     *   1 = 横屏改成 状态栏高度 + base（与竖屏同语义）★ 默认
     *   2 = 横屏改成 状态栏高度 + base + extra（extra 由 PROP_TOP_EXTRA 给）
     */
    private static final String PROP_PAD_MODE = "debug.hyperclassic.hun_pad_mode";
    /** 模式 2 的额外像素增量。 */
    private static final String PROP_PAD_EXTRA = "debug.hyperclassic.hun_pad_extra";

    private HunProbe() {
    }

    // ==============================================================
    // ① 发送测试通知
    // ==============================================================

    /**
     * 发一条会以 heads-up 形式弹出的测试通知。
     *
     * 关键点：
     *   · IMPORTANCE_HIGH + PRIORITY_HIGH → 触发 HUN
     *   · setFullScreenIntent 不设（那会变成来电样式）
     *   · 用独立 group，避免和真实通知混在一起
     *   · contentIntent 指向模块自己的 Activity，点开不会崩
     */
    static boolean sendTestHun(Context ctx, String title, String text) {
        try {
            if (ctx == null) {
                return false;
            }
            NotificationManager nm =
                    (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm == null) {
                return false;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel ch = new NotificationChannel(
                        CHANNEL_ID, "HyperClassic 测试", NotificationManager.IMPORTANCE_HIGH);
                ch.setDescription("用于复现 HUN 玻璃背景问题");
                ch.enableLights(false);
                ch.enableVibration(false);
                ch.setShowBadge(false);
                nm.createNotificationChannel(ch);
            }

            int seq = TEST_SEQ.incrementAndGet();
            int id = TEST_ID_BASE + seq;

            Intent it = new Intent();
            it.setClassName(ctx.getPackageName(),
                    "io.github.mersand.hyperclassic.MainActivity");
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            int piFlags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                    : PendingIntent.FLAG_UPDATE_CURRENT;
            PendingIntent pi = PendingIntent.getActivity(ctx, id, it, piFlags);

            Notification.Builder b = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                    ? new Notification.Builder(ctx, CHANNEL_ID)
                    : new Notification.Builder(ctx);

            b.setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle(title == null ? ("HC 测试 HUN #" + seq) : title)
                    .setContentText(text == null ? "横滑这条通知，观察玻璃背景是否跟随" : text)
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .setWhen(System.currentTimeMillis())
                    .setShowWhen(true)
                    .setLocalOnly(true);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                b.setPriority(Notification.PRIORITY_HIGH);
            }
            // 让它走 heads-up：需要 high importance（O+）或 PRIORITY_HIGH（旧版）
            b.setDefaults(Notification.DEFAULT_ALL);

            nm.notify(id, b.build());

            L.always(TAG, "test HUN sent id=0x" + Integer.toHexString(id)
                    + " seq=" + seq);
            return true;
        } catch (Throwable t) {
            L.always(TAG, "sendTestHun FAILED: " + t);
            return false;
        }
    }

    /** 清掉所有测试通知。 */
    static void cancelTestHuns(Context ctx) {
        try {
            NotificationManager nm =
                    (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm == null) {
                return;
            }
            int n = TEST_SEQ.get();
            for (int i = 1; i <= n; ++i) {
                nm.cancel(TEST_ID_BASE + i);
            }
            L.always(TAG, "cancelled " + n + " test HUN(s)");
        } catch (Throwable t) {
            L.always(TAG, "cancelTestHuns FAILED: " + t);
        }
    }

    // ==============================================================
    // ② 探针
    // ==============================================================

    /**
     * 安装探针。
     *
     * hook 点选择：
     *   ExpandableNotificationRow.onLayout —— HUN 行每次布局都会走，
     *   在这里读几何值最准（layout 之后 left/top/width/height 都已定稿）。
     *
     * 只读，不做任何修改。
     */
    static void install(XposedModule module, ClassLoader cl) {
        try {
            Class<?> rowCls = Class.forName(
                    "com.android.systemui.statusbar.notification.row.ExpandableNotificationRow",
                    false, cl);

            // ★ v354：主探针改成 hook setTranslation(F)。
            //
            // 原 onLayout hook 完全不输出，原因是 onLayout 是 final 且
            // HyperOS 可能把布局委托到了 PropertiesFrameLayout。
            // setTranslation 在横滑过程中每帧都被调用，是最稳的观测点，
            // 而且它正是"玻璃不跟随"的嫌疑核心：
            //   mDismissUsingRowTranslationX = split && expanded 时为 true
            //   → 只平移 row 自己，玻璃（NotificationBackgroundView）留在原地。
            Method setTrans = null;
            try {
                setTrans = rowCls.getMethod("setTranslation", float.class);
            } catch (Throwable t) {
                L.always(TAG, "setTranslation not found: " + t);
            }
            if (setTrans != null) {
                setTrans.setAccessible(true);
                final Method target = setTrans;
                module.hook(target)
                        .setId("hc_hun_probe_translation")
                        .setPriority(XposedInterface.PRIORITY_LOWEST)
                        .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                        .intercept(chain -> {
                            Object self = chain.getThisObject();
                            int n = MOVES.getAndIncrement();
                            // 前 3 次无条件打印（证明 hook 命中）
                            boolean force = n < 3;
                            Object result = chain.proceed();
                            try {
                                if (!force && !on(PROP_PROBE)) {
                                    return result;
                                }
                                if (!(self instanceof View)) {
                                    return result;
                                }
                                View row = (View) self;
                                if (!force && !isHeadsUp(row)) {
                                    return result;
                                }
                                float tx = ((Number) chain.getArg(0)).floatValue();
                                if (!force && Math.abs(tx) < 0.5f) {
                                    return result;
                                }
                                int k = LOGS.getAndIncrement();
                                if (k < 60) {
                                    logRow(row, "setTx#" + k + " arg=" + tx
                                            + " hud=" + isHeadsUp(row));
                                }
                            } catch (Throwable ignored) {
                            }
                            return result;
                        });
                L.always(TAG, "installed HUN setTranslation probe");
            }

            // 保留原 onLayout 探针（能命中就多一份数据）
            Method onLayout = findOnLayout(rowCls);
            if (onLayout != null) {
                onLayout.setAccessible(true);
                module.hook(onLayout)
                        .setId("hc_hun_probe_layout")
                        .setPriority(XposedInterface.PRIORITY_LOWEST)
                        .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                        .intercept(chain -> {
                            Object result = chain.proceed();
                            try {
                                Object self = chain.getThisObject();
                                int e = ENTRIES.getAndIncrement();
                                if (e < 5) {
                                    L.always(TAG, "onLayout ENTER #" + e
                                            + " self=" + (self == null ? "null"
                                            : self.getClass().getSimpleName()));
                                }
                                if (!on(PROP_PROBE)) {
                                    return result;
                                }
                                if (!(self instanceof View)) {
                                    return result;
                                }
                                View row = (View) self;
                                if (!isHeadsUp(row)) {
                                    return result;
                                }
                                int n = LOGS.getAndIncrement();
                                if (n < 40) {
                                    logRow(row, "layout#" + n);
                                }
                                if (on(PROP_DUMP) && DUMPS.getAndIncrement() < 4) {
                                    dumpTree(row, 0);
                                }
                            } catch (Throwable ignored) {
                            }
                            return result;
                        });
                L.always(TAG, "installed HUN layout probe; prop=" + PROP_PROBE + " default=0");
            }
        } catch (Throwable t) {
            L.always(TAG, "install FAILED: " + t);
        }
    }

    // ==============================================================
    // ③ HUN 横滑修复层
    // ==============================================================

    /**
     * 让 HUN 行在横滑时走「整行平移」路径，玻璃层跟着一起动。
     *
     * 目标方法：ExpandableNotificationRow.setTranslation(F)
     *
     * 原理：这个方法开头读 ExpandableOutlineView.mDismissUsingRowTranslationX：
     *   true  → setTranslationX(dx)，整行（含背景）一起移动，且不碰 shelfIcon
     *   false → 只遍历 mTranslateableViews，逐个 setTranslationX(dx)，
     *           末尾再 getShelfIcon().setTranslationX(-dx)
     *
     * HUN 弹出时 NSSL 的 mIsExpanded == false，
     * NotificationStackScrollLayout.updateDismissBehavior() 因此算出 false，
     * 于是 HUN 走 false 分支 —— 这正是「玻璃留在原地 + 图标反向乱跑」的根因。
     *
     * 修法：在 setTranslation 执行前，对 HUN 行强制把该字段置 true，
     * 让官方代码自己走正确分支（不自己造平移，避免二次错位）。
     */
    static void installHunFix(XposedModule module, ClassLoader cl) {
        try {
            if (!on(PROP_FIX, true)) {
                L.always(TAG, "hunFix disabled (prop " + PROP_FIX + ")");
                return;
            }
            Class<?> rowCls = Class.forName(
                    "com.android.systemui.statusbar.notification.row.ExpandableNotificationRow",
                    false, cl);

            // 字段在父类 ExpandableOutlineView 上
            final Field flagField = findField(rowCls, "mDismissUsingRowTranslationX");
            if (flagField == null) {
                L.always(TAG, "hunFix: mDismissUsingRowTranslationX not found");
                return;
            }
            flagField.setAccessible(true);

            Method setTrans = rowCls.getMethod("setTranslation", float.class);
            setTrans.setAccessible(true);

            module.hook(setTrans)
                    .setId("hc_hun_fix_translation")
                    .setPriority(XposedInterface.PRIORITY_LOWEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        try {
                            Object self = chain.getThisObject();
                            if (self instanceof View) {
                                View row = (View) self;
                                if (isHeadsUp(row) && !flagField.getBoolean(self)) {
                                    flagField.setBoolean(self, true);
                                    int n = FIXES.getAndIncrement();
                                    if (n < 20) {
                                        L.always(TAG, "hunFix: forced rowDX=true on HUN #" + n);
                                    }
                                }
                            }
                        } catch (Throwable ignored) {
                        }
                        return chain.proceed();
                    });

            L.always(TAG, "hunFix installed (prop " + PROP_FIX + "=1)");
        } catch (Throwable t) {
            L.always(TAG, "hunFix install FAILED: " + t);
        }
    }

    // ==============================================================
    // ④ HUN 顶部留白修复层
    // ==============================================================

    /**
     * 让横屏 HUN 不再压住状态栏。
     *
     * 目标方法（Kotlin 顶层函数，编译成静态方法）：
     *   com.android.systemui.statusbar.notification.policy.HeadsUpManagerInjectorImplKt
     *       .miuiHeadsUpInset(Landroid/content/Context;)I
     *
     * 官方实现：
     *   int base = res.getDimensionPixelSize(heads_up_status_bar_padding); // 7.27dp
     *   if (orientation == LANDSCAPE)
     *       return (cutout != null ? cutout.getSafeInsetTop() : 0) + base;
     *   else
     *       return SystemBarUtils.getStatusBarHeight(ctx) + base;
     *
     * 横屏时挖孔在侧边，safeInsetTop == 0，结果只剩 27px，
     * HUN 就贴在屏幕最上沿、盖住状态栏图标区。
     *
     * 修法：横屏分支补上真实状态栏高度，语义与竖屏一致。
     */
    static void installPaddingFix(XposedModule module, ClassLoader cl) {
        try {
            final int mode = intProp(PROP_PAD_MODE, 1);
            if (mode <= 0) {
                L.always(TAG, "padFix disabled (mode=0)");
                return;
            }
            Class<?> kt = Class.forName(
                    "com.android.systemui.statusbar.notification.policy.HeadsUpManagerInjectorImplKt",
                    false, cl);
            Method target = kt.getDeclaredMethod("miuiHeadsUpInset", android.content.Context.class);
            target.setAccessible(true);

            module.hook(target)
                    .setId("hc_hun_pad_inset")
                    .setPriority(XposedInterface.PRIORITY_LOWEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        Object result = chain.proceed();
                        try {
                            android.content.Context ctx =
                                    (android.content.Context) chain.getArg(0);
                            if (ctx == null) {
                                return result;
                            }
                            // 只改横屏：竖屏官方分支本来就是对的
                            if (ctx.getResources().getConfiguration().orientation
                                    != android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
                                return result;
                            }
                            int orig = ((Number) result).intValue();
                            int sbh = statusBarHeightPx(ctx);
                            if (sbh <= 0) {
                                return result;
                            }
                            int extra = mode >= 2 ? intProp(PROP_PAD_EXTRA, 0) : 0;
                            int fixed = orig + sbh + extra;
                            int n = PAD_LOGS.getAndIncrement();
                            if (n < 20) {
                                L.always(TAG, "padFix: miuiHeadsUpInset " + orig
                                        + " -> " + fixed + " (sbh=" + sbh
                                        + " extra=" + extra + " mode=" + mode + ") #" + n);
                            }
                            return fixed;
                        } catch (Throwable ignored) {
                            return result;
                        }
                    });

            L.always(TAG, "padFix installed (mode=" + mode + ")");
        } catch (Throwable t) {
            L.always(TAG, "padFix install FAILED: " + t);
        }
    }

    /** 取当前真实状态栏高度（px）。 */
    private static int statusBarHeightPx(android.content.Context ctx) {
        try {
            int id = ctx.getResources().getIdentifier(
                    "status_bar_height", "dimen", "android");
            if (id > 0) {
                int v = ctx.getResources().getDimensionPixelSize(id);
                if (v > 0) {
                    return v;
                }
            }
        } catch (Throwable ignored) {
        }
        // 退路：从窗口 insets 拿
        try {
            android.view.WindowManager wm =
                    (android.view.WindowManager) ctx.getSystemService(
                            android.content.Context.WINDOW_SERVICE);
            if (wm != null) {
                android.view.WindowMetrics m = wm.getMaximumWindowMetrics();
                android.graphics.Insets in = m.getWindowInsets()
                        .getInsetsIgnoringVisibility(
                                android.view.WindowInsets.Type.statusBars());
                if (in.top > 0) {
                    return in.top;
                }
            }
        } catch (Throwable ignored) {
        }
        return 0;
    }

    /** 读整数 prop，默认 def。 */
    private static int intProp(String key, int def) {
        try {
            Method get = Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class, String.class);
            String v = String.valueOf(get.invoke(null, key, ""));
            if (v != null && v.length() > 0 && !"null".equals(v)) {
                return Integer.parseInt(v.trim());
            }
        } catch (Throwable ignored) {
        }
        return def;
    }

    /** 沿继承链找字段。 */
    private static Field findField(Class<?> c, String name) {
        Class<?> cur = c;
        while (cur != null && cur != Object.class) {
            try {
                return cur.getDeclaredField(name);
            } catch (Throwable ignored) {
                cur = cur.getSuperclass();
            }
        }
        return null;
    }

    /** 在类继承链上找 onLayout(boolean,int,int,int,int)。 */
    private static Method findOnLayout(Class<?> c) {
        Class<?> cur = c;
        while (cur != null && cur != Object.class) {
            try {
                Method m = cur.getDeclaredMethod("onLayout",
                        boolean.class, int.class, int.class, int.class, int.class);
                return m;
            } catch (Throwable ignored) {
                cur = cur.getSuperclass();
            }
        }
        return null;
    }

    /** 判断这一行是不是 HUN。
     *
     * ★ v354：HyperOS 里没有 isHeadsUp()，实际方法名是 isHeadsUpState()。
     *   之前探针一行不出的根因就是这里 —— getMethod("isHeadsUp") 抛
     *   NoSuchMethodException 被吞掉，然后 fallback 的 mIsHeadsUp 字段
     *   名也对不上（实际叫 mIsHeadsUp，但 setAccessible 在 hidden API
     *   限制下可能失败）。现在优先反射方法，再退到直接读字段。
     */
    private static boolean isHeadsUp(View row) {
        // ① 优先：isHeadsUpState()
        String[] methods = {"isHeadsUpState", "isHeadsUp"};
        for (String name : methods) {
            try {
                Method m = row.getClass().getMethod(name);
                Object r = m.invoke(row);
                if (r instanceof Boolean) {
                    return (Boolean) r;
                }
            } catch (Throwable ignored) {
            }
        }
        // ② 退路：直接读 mIsHeadsUp 字段（沿继承链）
        try {
            Class<?> c = row.getClass();
            while (c != null && c != Object.class) {
                try {
                    Field f = c.getDeclaredField("mIsHeadsUp");
                    f.setAccessible(true);
                    return f.getBoolean(row);
                } catch (Throwable ignored) {
                    c = c.getSuperclass();
                }
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    // -------------------------------------------------------------- 日志

    private static void logRow(View row, String tag) {
        StringBuilder sb = new StringBuilder();
        sb.append("HUN ").append(tag).append(' ');
        appendGeom(sb, "row", row);

        // 找它的 NotificationBackgroundView（玻璃层）
        View bg = findBackground(row);
        if (bg != null) {
            sb.append(" | ");
            appendGeom(sb, "bg", bg);
            // 关键：两者的 translationX/Y 差
            sb.append(" | dTX=").append(row.getTranslationX() - bg.getTranslationX());
            sb.append(" dTY=").append(row.getTranslationY() - bg.getTranslationY());
        } else {
            sb.append(" | bg=NOT_FOUND");
        }

        // 屏幕信息
        try {
            Context ctx = row.getContext();
            android.util.DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
            sb.append(" | screen=").append(dm.widthPixels).append('x').append(dm.heightPixels);
            int ori = ctx.getResources().getConfiguration().orientation;
            sb.append(" ori=").append(ori);
        } catch (Throwable ignored) {
        }

        L.always(TAG, sb.toString());
    }

    private static void appendGeom(StringBuilder sb, String name, View v) {
        sb.append(name).append("[l=").append(v.getLeft())
                .append(" t=").append(v.getTop())
                .append(" r=").append(v.getRight())
                .append(" b=").append(v.getBottom())
                .append(" w=").append(v.getWidth())
                .append(" h=").append(v.getHeight())
                .append(" tx=").append(v.getTranslationX())
                .append(" ty=").append(v.getTranslationY())
                .append(" vis=").append(v.getVisibility())
                .append(" alpha=").append(v.getAlpha())
                .append(']');
    }

    /** 在行内找 NotificationBackgroundView。 */
    private static View findBackground(View root) {
        try {
            if (root.getClass().getName().contains("NotificationBackgroundView")) {
                return root;
            }
            if (!(root instanceof ViewGroup)) {
                return null;
            }
            ViewGroup g = (ViewGroup) root;
            for (int i = 0; i < g.getChildCount(); ++i) {
                View found = findBackground(g.getChildAt(i));
                if (found != null) {
                    return found;
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    /** 打印整棵子树（详细模式）。 */
    private static void dumpTree(View v, int depth) {
        if (v == null || depth > 6) {
            return;
        }
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < depth; ++i) {
                sb.append("  ");
            }
            sb.append(v.getClass().getSimpleName());
            sb.append(" id=0x").append(Integer.toHexString(v.getId()));
            sb.append(" [").append(v.getLeft()).append(',').append(v.getTop())
                    .append('-').append(v.getRight()).append(',').append(v.getBottom()).append(']');
            sb.append(" tx=").append(v.getTranslationX())
                    .append(" ty=").append(v.getTranslationY())
                    .append(" a=").append(v.getAlpha());
            L.always(TAG, "  " + sb);
            if (v instanceof ViewGroup) {
                ViewGroup g = (ViewGroup) v;
                for (int i = 0; i < g.getChildCount(); ++i) {
                    dumpTree(g.getChildAt(i), depth + 1);
                }
            }
        } catch (Throwable ignored) {
        }
    }

    // -------------------------------------------------------------- props

    private static boolean on(String key) {
        return on(key, false);
    }

    /**
     * 读布尔 prop。
     *
     * @param def 属性不存在时的默认值。
     *
     * 【v359 为什么要带默认值参数】
     *   本类里有两类开关，语义完全不同：
     *     · 功能修复类（hun_fix / hun_pad_mode）—— 属于稳定功能，缺省应【开启】。
     *       设备重启后 debug.* 属性全部丢失，若缺省关闭，
     *       这些修复就集体失效（主人 2026-11-26 实测：
     *       HUN 横滑玻璃不跟随、状态栏图标反向乱跑）。
     *     · 探针/调试类（probe_hun / probe_hun_dump）—— 只用于排查，
     *       缺省必须【关闭】，否则会持续刷 logcat。
     *   所以不能一刀切，由调用方显式传 def。
     */
    private static boolean on(String key, boolean def) {
        try {
            Method get = Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class, String.class);
            Object v = get.invoke(null, key, def ? "1" : "0");
            return !"0".equals(String.valueOf(v).trim());
        } catch (Throwable t) {
            return false;
        }
    }
}