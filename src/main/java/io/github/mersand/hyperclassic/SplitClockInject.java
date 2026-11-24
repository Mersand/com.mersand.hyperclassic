package io.github.mersand.hyperclassic;

import android.content.Context;
import android.content.res.Configuration;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Method;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;

/**
 * SplitClockInject —— 横屏 split shade 下，在左栏 QS 顶部注入一个「状态栏小时钟」。
 *
 * ==================================================================
 * ===== 为什么需要这一层（完整溯源）=====
 * ==================================================================
 *
 * 主人实测（2026-11-24）：横屏 split 展开后，左栏 QS 顶部**没有时钟**，
 * 而竖屏下拉时 QS 顶部有一个大时钟（18:26 + 11月24日 周二傍晚）。
 *
 * ===== 官方结构（res/layout/miui_ns_qs_header_view.xml）=====
 *
 *   MiuiQSHeaderView
 *     └─ LinearLayout @id/qs_header_vertical_info        ← 竖屏那一片
 *          ├─ MiuiClock @id/big_time    (44dp, mode=1)   ← ★ 竖屏大时钟
 *          └─ MiuiClock @id/date_time   (mode=1 date)
 *     └─ LinearLayout @id/notification_shade_carrier_layout_container  ← 高 75px
 *          ├─ MiuiCarrierTextLayout  运营商（默认 gone）
 *          ├─ MiuiClock @id/horizontal_date_time         ← ★ 横屏小时钟
 *          │     textAppearance = ...QuickSettingDate
 *          │     即 status_bar_clock_size = 13.45dp
 *          └─ FrameLayout → system_icon_area（电量等）
 *
 * ⇒ 官方【本来就设计好了】横屏显示 @id/horizontal_date_time 这个 13.45dp 小时钟。
 *
 * ===== 官方切换逻辑 =====
 *
 *   MiuiQSHeaderView.updateLayout()：
 *     if (mOrientation == newOri) return;          // 方向没变直接返回
 *     if (newOri == PORTRAIT) {
 *         mVerticalInfo.setVisibility(VISIBLE);    // 大时钟
 *         mHorizontalDateView.setClockMode(1);     // date
 *     } else {
 *         mVerticalInfo.setVisibility(GONE);       // 横屏隐藏大时钟
 *         mHorizontalDateView.setClockMode(2);     // time ← 变时钟！
 *     }
 *
 *   MiuiQSHeaderView.updateCarrierVisibility()：
 *     isPortrait = (mOrientation == 1)
 *     mDateView.setPolicyVisibility(isPortrait ? VISIBLE : GONE)
 *     mHorizontalDateView.setPolicyVisibility(isPortrait ? GONE : VISIBLE)
 *
 * ⇒ 官方意图：横屏隐藏大时钟、显示 13.45dp 小时钟。
 *
 * ===== 但实测失效 =====
 *
 *   dumpsys：
 *     MiuiQSHeaderView{1249021 I.E ...... 0,0-1440,469}     ← INVISIBLE
 *     MiuiClock{8568c3b GFED..C.. 0,0-0,75 ... /horizontal_date_time}  ← GONE
 *
 * 整个 MiuiQSHeaderView 在 split shade 下是不可见的（小米的横屏 QS
 * 只显示磁贴区，不显示 header 那一整条），所以里面的时钟自然看不见。
 *
 * ==================================================================
 * ===== 本层做法：独立实例 + 复用系统时钟规则 =====
 * ==================================================================
 *
 * 不去「救」那个被隐藏的官方实例，而是【新建一个 MiuiClock】挂到
 * 横屏左栏的 QS 容器顶部。好处：
 *
 *   ① 完全不动官方 header，零破坏风险
 *   ② 位置/大小由我们自己控制，不会溢出屏幕
 *   ③ 时间刷新走系统自己的机制（见下）
 *
 * ===== 为什么时间会自动刷新（关键）=====
 *
 *   MiuiClock.onAttachedToWindow()：
 *     mMiuiStatusBarClockController.mClockListeners.add(this);   // 注册
 *     updateTime();                                              // 立即刷新
 *     if (mShowDark) mDarkIconDispatcher.addDarkReceiver(this);   // 跟随深色
 *
 *   MiuiClock.onDetachedFromWindow()：对应地反注册。
 *
 * ⇒ 只要 addView 上去，系统就会【主动】把时间推给它，深色模式也自动跟随。
 *   这就是主人要求的「直接调用系统的时钟规则」。
 *
 * ===== 样式 =====
 *
 * 用官方的 TextAppearance.StatusBar.Expanded.Clock.QuickSettingDate
 * （字号 = status_bar_clock_size = 13.45dp，正是状态栏小时钟尺寸）。
 * 通过 getIdentifier 动态取 id，避免硬编码。
 *
 * 开关：debug.hyperclassic.local.inject_clock  默认 1
 */
final class SplitClockInject {

    private static final String TAG = "HCClockInject";

    /** 主开关。 */
    private static final String PROP = "debug.hyperclassic.local.inject_clock";

    /** wrapper 的自定义 id（避免和数据冲突，用于查重）。 */
    private static final int WRAPPER_ID = 0x0f000001;

    private static Method propGet;
    private static volatile long propAt;
    private static volatile boolean enabled = true;

    private static int injectN, errors;

    /** 我们注入的时钟（跨方向复用同一个实例）。 */
    private static volatile View injectedClock;

    /** 曾经挂上去的父容器（用于跨方向时先摘下来）。 */
    private static volatile ViewGroup clockHost;

    /** 上一次看到的方向，避免每帧重复工作。 */
    private static volatile int lastOrientation = -1;

    private SplitClockInject() {
    }

    static void install(XposedModule module, ClassLoader cl) {
        try {
            // ★ 挂点选择（重要）：
            //
            // QSContent 是 final class extends FrameLayout，smali 里只有
            // <init> / dispatchTouchEvent / hasOverlappingRendering 三个方法，
            // 没有覆写 onLayout。所以不能 hook QSContent.onLayout（不存在）。
            //
            // 改用【父类 FrameLayout.onLayout】+ 实例类型过滤：
            //   FrameLayout.onLayout 是 ViewGroup 布局链的必经点，
            //   在 hook 里判 chain.getThisObject() instanceof QSContent 即可。
            //
            // 为什么不用 dispatchTouchEvent：用户不下拉就不触发，
            // 而展开面板必须先挂好时钟，否则第一帧会闪。
            Class<?> frameLayout = Class.forName("android.widget.FrameLayout", false, cl);
            Method onLayout = frameLayout.getDeclaredMethod("onLayout",
                    boolean.class, int.class, int.class, int.class, int.class);
            onLayout.setAccessible(true);

            final Class<?> qsContentCls = Class.forName(
                    "com.android.systemui.qs.QSContent", false, cl);

            module.hook(onLayout)
                    .setId("hc_split_inject_clock")
                    .setPriority(XposedInterface.PRIORITY_LOWEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        Object result = chain.proceed();
                        try {
                            if (!runtime()) {
                                return result;
                            }
                            Object self = chain.getThisObject();
                            if (!qsContentCls.isInstance(self)) {
                                return result;   // 只处理 QSContent
                            }
                            if (self instanceof View) {
                                ensureClock((View) self);
                            }
                        } catch (Throwable t) {
                            error(t);
                        }
                        return result;
                    });

            L.always(TAG, "installed FrameLayout.onLayout hook (QSContent filter);"
                    + " prop=" + PROP + " default=1");

            // ★ v350：时钟反色锁定白色（必须在 onLayout hook 之后装，
            //   因为要读 injectedClock 引用做实例比对）
            installWhiteLock(module, cl);
        } catch (Throwable t) {
            L.always(TAG, "install FAILED: " + t);
        }
    }

    /**
     * 保证时钟挂在该出现的地方，且只在该出现的时候挂着。
     *
     * 两个方向都跑，但只有横屏才挂上去；竖屏就摘掉（竖屏官方有大时钟）。
     */
    private static void ensureClock(View qsContent) {
        int ori = orientation(qsContent.getContext());

        // ★ v340：横屏且已挂好 → 只刷新颜色，不做任何布局操作
        if (ori == Configuration.ORIENTATION_LANDSCAPE
                && injectedClock != null
                && injectedClock.getParent() == clockHost
                && clockHost != null
                && clockHost.getParent() == qsContent) {
            // 确保颜色一直是白的（系统可能在别处改回）
            if (injectedClock instanceof TextView) {
                ((TextView) injectedClock).setTextColor(0xFFFFFFFF);
            }
            return;
        }

        if (ori == lastOrientation && injectedClock != null
                && injectedClock.getParent() != null
                && ori == Configuration.ORIENTATION_LANDSCAPE) {
            return;
        }
        lastOrientation = ori;

        if (ori != Configuration.ORIENTATION_LANDSCAPE) {
            // 竖屏：摘掉我们注入的时钟（竖屏有官方大时钟）
            detach();
            return;
        }

        // 横屏：挂上去
        attach(qsContent);
    }

    private static void attach(View qsContent) {
        try {
            if (!(qsContent instanceof ViewGroup)) {
                return;
            }
            ViewGroup parent = (ViewGroup) qsContent;

            View clock = injectedClock;
            if (clock == null) {
                clock = createClock(qsContent.getContext());
                if (clock == null) {
                    return;
                }
                injectedClock = clock;
            }

            // 已经挂在这个父容器里，只确保可见即可
            if (clock.getParent() == parent) {
                if (clock.getVisibility() != View.VISIBLE) {
                    clock.setVisibility(View.VISIBLE);
                }
                return;
            }

            // 先摘下来（可能挂在旧的父容器上）
            detach();

            // ★ v339：位置对齐 —— 实测数据
            //
            //   QSContent{... 53,0-3147,1708}
            //   MiuiQSTileView{... 15,37-627,367}    ← 第一个磁贴 x=15
            //
            // 所以时钟左边距应当是 15px（相对 QSContent），
            // 才能和磁贴左边缘对齐。不是 24dp（90px）。
            Context ctx = clock.getContext();

            // ★ v340：位置与尺寸 —— 实测数据
            //
            //   QSContent{fe2353d ... 53,0-1524,1730}
            //     MiuiQSTileView{... 15,37-375,367}     ← 磁贴 x=15, y=37
            //
            // 我们要让时钟**不占磁贴的位置**，而是叠在磁贴之上方的空白处。
            //
            // 但 QSContent 是 FrameLayout（不是 LinearLayout），
            // 之前 addView(wrapper, 0) 会把它当第一层叠上去 —— 
            // 实测它占了 0,0-1471,68，把磁贴往下挤了。
            //
            // ⇒ 改用 FrameLayout.LayoutParams + TOP|START 精确摆放，
            //    并且**不参与布局测量**（固定高度，不挤压其他子项）。
            LinearLayout wrapper = new LinearLayout(ctx);
            wrapper.setOrientation(LinearLayout.HORIZONTAL);
            wrapper.setGravity(Gravity.CENTER_VERTICAL);
            wrapper.setId(WRAPPER_ID);

            // ★ v341：竖向位置 —— 像素实测
            //
            // v340 截图逐像素测量（3200x1440）：
            //   时钟文字亮像素  y = 24..68    （中心 ≈ 46）
            //   左栏托盘顶边    y = 165
            //
            // ⇒ 时钟比托盘高出 ~100px，飘在托盘外面的透明区，很难看。
            //
            // 目标：时钟落进托盘内部，与第一行磁贴同一水平带。
            //   磁贴 y = 37..367（相对 QSContent，它本身从 0 开始，
            //   但视觉上托盘顶 = 165，即 QSContent 有 -165 的视觉偏移）。
            //
            // 实测：wrapper 挂在 QSContent 的 y=0 时，文字中心落在 46。
            //   要让它落到托盘顶内的合适位置（约 165+50=215），
            //   需要下移 (215-46) ≈ 169px ≈ 45dp @600dpi。
            //
            // 用 topMargin 而不是 paddingTop，因为 topMargin 不增加
            // wrapper 自身高度，不会把 QSContent 里的其它子项推下去。
            int padStart = padStartPx(ctx);
            int padEnd = dp(ctx, 12);
            wrapper.setPadding(padStart, 0, padEnd, 0);

            // ★ 用 FrameLayout.LayoutParams（QSContent 的真实父类是 FrameLayout）
            android.widget.FrameLayout.LayoutParams flp =
                    new android.widget.FrameLayout.LayoutParams(
                            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                            Gravity.TOP | Gravity.START);
            // 上边距：把时钟推进托盘内部（像素实测值，可用 prop 热调）
            flp.topMargin = topMarginPx(ctx);
            wrapper.setLayoutParams(flp);

            // ★ 反色：控制中心内固定白色，不跟随状态栏
            //   （主人定调：这里背景永远是深色托盘，白色才看得清）
            if (clock instanceof TextView) {
                ((TextView) clock).setTextColor(0xFFFFFFFF);
            }
            wrapper.addView(clock, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            parent.addView(wrapper);
            clockHost = wrapper;

            int n = injectN++;
            if (n < 6) {
                L.always(TAG, "clock injected into QSContent #" + n
                        + " padStart=" + padStart + " color=WHITE(fixed)");
            }
        } catch (Throwable t) {
            error(t);
        }
    }

    /**
     * ★ v339：让时钟立刻拿到正确的明暗颜色。
     *
     * 走官方 applyDark 链路：DarkIconDispatcherImpl.applyDark(receiver)
     * 会把 onDarkChanged / onDarkChangedWithContrast /
     * onLightDarkTintChanged 三个回调重新推一遍。
     *
     * 此时 mLightColor / mDarkColor 都已经是正确值，
     * 所以 onDarkChanged 里算出来的颜色就对了。
     */
    private static void refreshClockTint(View clock) {
        try {
            // 1) 找到 MiuiClock 持有的 clock controller
            java.lang.reflect.Field fCtrl = clock.getClass().getDeclaredField(
                    "mMiuiStatusBarClockController");
            fCtrl.setAccessible(true);
            Object controller = fCtrl.get(clock);
            if (controller == null) {
                return;
            }
            // 2) 取它的 mDarkIconDispatcher
            java.lang.reflect.Field fDisp = controller.getClass().getDeclaredField(
                    "mDarkIconDispatcher");
            fDisp.setAccessible(true);
            Object dispatcher = fDisp.get(controller);
            if (dispatcher == null) {
                return;
            }
            // 3) applyDark(clock)
            Method applyDark = dispatcher.getClass().getMethod(
                    "applyDark", Class.forName("com.android.systemui.plugins.DarkIconDispatcher$DarkReceiver"));
            applyDark.setAccessible(true);
            applyDark.invoke(dispatcher, clock);
            L.always(TAG, "clock tint refreshed via applyDark");
        } catch (Throwable t) {
            // 退化方案：直接按当前暗度选黑或白
            fallbackTint(clock, t);
        }
    }

    /** applyDark 不可用时的退化：按系统当前 uiMode 直接设颜色。 */
    private static void fallbackTint(View clock, Throwable cause) {
        try {
            if (!(clock instanceof TextView)) {
                return;
            }
            int mode = clock.getContext().getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK;
            boolean dark = (mode == Configuration.UI_MODE_NIGHT_YES);
            ((TextView) clock).setTextColor(dark ? 0xFFFFFFFF : 0xFF000000);
            L.always(TAG, "clock tint fallback (dark=" + dark + "): " + cause);
        } catch (Throwable t) {
            error(t);
        }
    }

    // ==============================================================
    // ★ v350：时钟反色锁定白色
    //
    // ===== 问题 =====
    //
    // 主人反馈：「qs 上方的状态栏时钟反色问题需要解决，固定成白色」。
    // 实测截图（横屏展开态）：左栏顶部「晚上 9:18」显示为暗灰色，
    // 而背景是深色托盘 → 几乎看不清。
    //
    // ===== 为什么 setTextColor(0xFFFFFFFF) 不够 =====
    //
    // 我们在 attach() / ensureClock() 里确实设了白色，但会被系统覆盖。
    // 溯源 MiuiClock.smali：
    //
    //   onDarkChanged(ArrayList, float, int)       第 229 行
    //     → 存 mAreas / mDarkIntensity / mTint
    //     → updateLightDarkTint$2(mAreas, mDarkIntensity, mTint,
    //                             mLightColor, mDarkColor, mUseTint)
    //
    //   updateLightDarkTint$2(...)                 第 422 行
    //     if (mUseTint) mNonAdaptedColor = DarkIconDispatcher.getTint(...)
    //     else {
    //         float d = DarkIconDispatcherExt.getDarkIntensity(...)
    //         if (d > 0) mNonAdaptedColor = mDarkColor;   ← 走这里
    //         else       mNonAdaptedColor = mLightColor;
    //     }
    //     setTextColor(mNonAdaptedColor);          ← ★ 无条件覆盖
    //
    // 只要状态栏反色状态一变（下拉/收起/切换应用），
    // DarkIconDispatcher 就会重新推 onDarkChanged → 覆盖我们的白色。
    // 这就是主人之前说的「反色跟随延迟，重启控制中心后正常」的本质。
    //
    // ===== 修法 =====
    //
    // hook MiuiClock.updateLightDarkTint$2，在 chain.proceed() 之后
    // 把颜色强行改回白色 —— 只针对我们注入的那个实例，不碰官方的。
    //
    // 识别方式：注入的 clock 有 mInjected 标记，或比对 injectedClock 引用。
    // ==============================================================

    private static void installWhiteLock(XposedModule module, ClassLoader cl) {
        try {
            Class<?> clockCls = Class.forName(
                    "com.android.systemui.statusbar.views.MiuiClock", false, cl);
            Method tint = clockCls.getDeclaredMethod("updateLightDarkTint$2",
                    java.util.ArrayList.class, float.class, int.class,
                    int.class, int.class, boolean.class);
            tint.setAccessible(true);

            module.hook(tint)
                    .setId("hc_split_clock_white_lock")
                    .setPriority(XposedInterface.PRIORITY_LOWEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        Object result = chain.proceed();
                        try {
                            Object self = chain.getThisObject();
                            // 只处理我们自己注入的那个实例
                            if (self != null && self == injectedClock
                                    && self instanceof TextView) {
                                ((TextView) self).setTextColor(0xFFFFFFFF);
                                int n = whiteLockN++;
                                if (n < 6) {
                                    L.always(TAG, "clock white lock applied #" + n);
                                }
                            }
                        } catch (Throwable t) {
                            error(t);
                        }
                        return result;
                    });

            L.always(TAG, "installed MiuiClock.updateLightDarkTint$2 white-lock hook");
        } catch (Throwable t) {
            L.always(TAG, "white-lock hook FAILED: " + t);
        }
    }

    /** 白锁生效次数（日志限流）。 */
    private static int whiteLockN;

    private static void detach() {
        try {
            View clock = injectedClock;
            ViewGroup host = clockHost;
            if (clock == null) {
                return;
            }
            if (host != null) {
                host.removeView(clock);
                ViewGroup hp = (ViewGroup) host.getParent();
                if (hp != null) {
                    hp.removeView(host);
                }
                clockHost = null;
            } else {
                ViewGroup p = (ViewGroup) clock.getParent();
                if (p != null) {
                    p.removeView(clock);
                }
            }
        } catch (Throwable t) {
            error(t);
        }
    }

    /**
     * 创建一个「状态栏小时钟」。
     *
     * 关键：
     *   - new MiuiClock(ctx, null, 0) → attrs 为 null，走默认值，不会崩
     *   - setClockMode(2) → 显示时间（1=date，2=time）
     *   - setTextAppearance(QuickSettingDate) → 13.45dp
     *   - setPolicyVisibility(VISIBLE) → 允许显示
     *
     * onAttachedToWindow 会自动注册到 MiuiStatusBarClockController，
     * 之后时间变化由系统推送，我们不用管。
     */
    private static View createClock(Context ctx) {
        try {
            ClassLoader cl = ctx.getClassLoader();
            Class<?> clockCls = Class.forName(
                    "com.android.systemui.statusbar.views.MiuiClock", false, cl);

            // MiuiClock(Context, AttributeSet)
            java.lang.reflect.Constructor<?> ctor =
                    clockCls.getDeclaredConstructor(Context.class,
                            android.util.AttributeSet.class);
            ctor.setAccessible(true);
            Object clock = ctor.newInstance(ctx, (android.util.AttributeSet) null);
            if (!(clock instanceof TextView)) {
                L.always(TAG, "MiuiClock not a TextView?!");
                return null;
            }
            TextView tv = (TextView) clock;

            // ★ setClockMode(2) = 时间
            try {
                Method setMode = clockCls.getMethod("setClockMode", int.class);
                setMode.setAccessible(true);
                setMode.invoke(clock, 2);
            } catch (Throwable t) {
                error(t);
            }

            // ★ setPolicyVisibility(VISIBLE)
            try {
                Method setPolicy = clockCls.getMethod("setPolicyVisibility", int.class);
                setPolicy.setAccessible(true);
                setPolicy.invoke(clock, View.VISIBLE);
            } catch (Throwable t) {
                error(t);
            }

            // ★ 用官方样式（= status_bar_clock_size = 13.45dp）
            try {
                int styleId = ctx.getResources().getIdentifier(
                        "TextAppearance_StatusBar_Expanded_Clock_QuickSettingDate",
                        "style", "com.android.systemui");
                if (styleId != 0) {
                    tv.setTextAppearance(styleId);
                } else {
                    // 退化方案：手动设 13.45dp 字号
                    tv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, 13.45f);
                }
            } catch (Throwable t) {
                error(t);
            }

            tv.setSingleLine(true);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            try {
                Method updateTime = clockCls.getMethod("updateTime");
                updateTime.setAccessible(true);
                updateTime.invoke(clock);
            } catch (Throwable t) {
                error(t);
            }

            L.always(TAG, "MiuiClock created (mode=2 time, style 13.45dp)");
            return tv;
        } catch (Throwable t) {
            L.always(TAG, "createClock FAILED: " + t);
            return null;
        }
    }

    private static int orientation(Context ctx) {
        try {
            return ctx.getResources().getConfiguration().orientation;
        } catch (Throwable t) {
            return Configuration.ORIENTATION_UNDEFINED;
        }
    }

    private static int dp(Context ctx, int v) {
        try {
            return Math.round(v * ctx.getResources().getDisplayMetrics().density);
        } catch (Throwable t) {
            return v;
        }
    }

    /**
     * ★ v343：时钟的上边距（px）。
     *
     * 像素实测标定（3200x1440 横屏截图）：
     *
     *   topMargin = 0    → 文字亮像素 y = 24..68     （飘在托盘外上方）
     *   topMargin = 62   → 文字亮像素 y = 77..118    （偏高）
     *   topMargin = 71   → 文字亮像素 y = 86..127
     *
     * 对齐基准是右侧真状态栏图标带：
     *   KB/s / VPN / 5G / 电池  y = 83..133，中心 108
     *
     * v342 时 topMargin=71 测得中心 106.5，与 108 只差 1.5px，
     * 但主人实机观感仍是「时钟文字比右侧图标略高几 px」。
     *
     * 为什么数值对齐了观感还偏高：
     *   时钟文字含中文（"晚上"）+ 阿拉伯数字，字形上沿（ascent）
     *   比数字图标高，视觉重心天然偏上；而右侧那组是图标 + 小字，
     *   视觉重心偏下。所以【数值中心对齐 ≠ 视觉对齐】，
     *   需要往下补偿几个 px。
     *
     * ⇒ 71 → 78（下移 7px），让视觉重心与图标带齐平。
     *
     * 热调：debug.hyperclassic.local.clock_top_margin  单位 px，默认 78
     */
    private static int topMarginPx(Context ctx) {
        String s = prop("debug.hyperclassic.local.clock_top_margin", "78");
        try {
            return Integer.parseInt(s.trim());
        } catch (Throwable t) {
            return 78;
        }
    }

    /**
     * ★ v343：时钟的左边距（px）。
     *
     * 主人定调（2026-11-24）：
     *   「时钟左边缘是对齐 qs 的，你的测量多半被圆角干扰了」
     *
     * 复核：之前像素扫描得到「时钟左亮像素 x=84 / 托盘左边缘 x=142」，
     * 但 142 是托盘**圆角**的内切位置，不是真正的布局左边界 ——
     * 圆角半径把亮像素起点推后了几十 px，所以那个 58px 的差值是假象。
     *
     * ⇒ 回到 4dp（≈15px），与 QSContent 内磁贴的 x=15 一致。
     *
     * 热调：debug.hyperclassic.local.clock_pad_start  单位 px，默认 15
     */
    private static int padStartPx(Context ctx) {
        String s = prop("debug.hyperclassic.local.clock_pad_start", "15");
        try {
            return Integer.parseInt(s.trim());
        } catch (Throwable t) {
            return dp(ctx, 4);
        }
    }

    private static boolean runtime() {
        long now = android.os.SystemClock.uptimeMillis();
        if (now - propAt > 500L) {
            enabled = !"0".equals(prop(PROP, "1"));
            propAt = now;
        }
        return enabled;
    }

    private static String prop(String key, String def) {
        try {
            if (propGet == null) {
                propGet = Class.forName("android.os.SystemProperties")
                        .getMethod("get", String.class, String.class);
            }
            return String.valueOf(propGet.invoke(null, key, def)).trim();
        } catch (Throwable t) {
            return def;
        }
    }

    private static void error(Throwable t) {
        if (errors++ < 5) {
            L.always(TAG, "error: " + t);
        }
    }
}
