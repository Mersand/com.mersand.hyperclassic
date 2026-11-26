package io.github.mersand.hyperclassic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Locale;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;

/**
 * ClockGlassP0 —— 【P0 实验】纯 Paint 级玻璃文字
 *
 * ==================================================================
 * ===== 一、这个类要验证什么 =====
 * ==================================================================
 *
 * 【背景】
 *   v370~v377 共 8 轮实验，自建 View + 玻璃一直输出「黄块」
 *   （尺寸等于整个 View 的黄色矩形）。
 *
 * 【2026-11-26 凌晨 libhwui.so 调研的关键结论】
 *   玻璃有**两条完全独立**的渲染通道：
 *
 *     通道 A（View 材质）：
 *       触发 = View.setMiViewMaterialType(1)
 *       着色器 = MiGlassFilter
 *       载体 = **矩形**（View 背景层）
 *
 *     通道 B（Path 玻璃）：
 *       触发 = Paint.setGlassEffect(true)
 *       着色器 = **MiDrawGlassPath**（独立渲染器！）
 *       载体 = **任意 Path**
 *       链路 = nSetGlassEffect → RenderNode.setUsageHint(SDF_GLASS)
 *              → UsageHint::SDF_GLASS MiDrawGlassPath::Draw
 *              → FontSdfVulkanSvg / FontSdfGLSvg（Vulkan/GLES 双后端）
 *
 *   ⇒ 这两套**互不相干**。
 *
 * 【v370~v377 的问题】
 *   我们**同时**开了两套：
 *     View.setMiViewMaterialType(1)   ← 通道 A 会画一个矩形玻璃
 *     View.setMiGlass(42项)           ← 通道 A 的参数
 *     View.setMiGlassClip(...)        ← 通道 A 的裁剪区
 *     paint.setGlassEffect(true)      ← 通道 B
 *     canvas.drawPath(...)            ← 通道 B
 *
 *   「黄块尺寸 = 整个 View」这个现象，**正好对应通道 A 输出的矩形**，
 *   而不是通道 B 的字形玻璃画错了。
 *   ⇒ 通道 A 的矩形盖在通道 B 的字形下面，还挡住了壁纸。
 *
 * ==================================================================
 * ===== 二、P0 的做法：只留通道 B =====
 * ==================================================================
 *
 *   全程**不调用任何 View 级玻璃 API**：
 *     ✗ View.setMiViewMaterialType
 *     ✗ View.setMiGlass / setMiGlassClip / setMiGlassBlurRadius
 *     ✗ MiuiBlurUtils.setGlassEffectMethod
 *     ✗ setPassWindowBlurEnabled / setMiBlurWinExc / setMiBackgroundBlur*
 *
 *   只做通道 B 的三件事：
 *     ✓ paint.setGlassEffect(true)
 *     ✓ paint.setAtlasMaxPathSize(2048, 2048)
 *     ✓ canvas.drawPath(字形Path, paint)
 *
 * ==================================================================
 * ===== 三、实验判定标准 =====
 * ==================================================================
 *
 *   ✅ 期望 1：黄块消失，能看到「玻璃质感的字形」→ 通道 A 是元凶，方向正确
 *   ⚠️ 期望 2：黄块消失，但字形完全看不见 → 通道 B 也需要环境，继续 P2/P3
 *   ❌ 期望 3：黄块仍在 → 说明黄块来自通道 B 本身（推翻本轮假设）
 *
 *   三种结果都有明确含义，这就是 P0 的价值。
 *
 * ==================================================================
 * ===== 四、开关（全部热调，改完下拉状态栏即可看到）=====
 * ==================================================================
 *
 *   debug.hyperclassic.local.clock_p0=1          总开关（默认关！要显式开）
 *   debug.hyperclassic.local.clock_p0_size=44    字号 sp
 *   debug.hyperclassic.local.clock_p0_weight=700 字重（0=不改）
 *   debug.hyperclassic.local.clock_p0_atlas=2048 setAtlasMaxPathSize 参数
 *   debug.hyperclassic.local.clock_p0_mode=0      0=纯 Paint 玻璃 1=普通白字（对照）
 *   debug.hyperclassic.local.clock_p0_clip=0     1=同时设 setMiGlassClip（对照实验）
 *
 *   注意：clock_p0_clip=1 会重新引入通道 A 的一部分，
 *        用来验证「是不是 setMiGlassClip 导致黄块」。
 *
 * ==================================================================
 * ===== 五、与 PortraitClockStyle 的关系 =====
 * ==================================================================
 *
 *   完全独立。本类：
 *     · 不改官方 MiuiClock 的字体（那是 PortraitClockStyle 的活）
 *     · 而是把官方时钟 INVISIBLE，用 FrameLayout 原地包装叠一个自绘 View
 *       （这个包装方案 v373 已实机验证过，布局零影响）
 *
 *   ⇒ 两者可以同时开。但为了 P0 结果干净，建议：
 *     setprop debug.hyperclassic.local.clock_style 0
 *     setprop debug.hyperclassic.local.clock_p0 1
 */
final class ClockGlassP0 {

    private static final String TAG = "HCClockP0";

    /** 总开关。 */
    private static final String PROP_ON = "debug.hyperclassic.local.clock_p0";

    /** 已处理过的时钟 View（防止重复包装）。 */
    private static final java.util.Set<View> DONE =
            java.util.Collections.synchronizedSet(
                    java.util.Collections.newSetFromMap(
                            new java.util.WeakHashMap<View, Boolean>()));

    /** 重入保护。 */
    private static final ThreadLocal<Boolean> IN_HOOK = new ThreadLocal<>();

    // 反射句柄（全部来自 SystemUI 的 ClassLoader）
    private static Method M_SET_GLASS_EFFECT;      // Paint.setGlassEffect(boolean)
    private static Method M_SET_ATLAS_MAX_PATH;    // Paint.setAtlasMaxPathSize(int,int)
    private static Method M_SIMPLIFY_PATH;         // MiuiBlurUtils.simplifyPath(Path,Paint)
    private static Method M_SET_MI_GLASS_CLIP;     // View.setMiGlassClip(float,float,float,float)
    private static Method M_SET_SDF_MAX_SIZE;      // View.setMiGlassSdfMaxSize(float,float) ★

    // ---- 采样环境（uBgColorTex 的来源）----
    private static Method M_PASS_WINDOW_BLUR;      // MiuiBlurUtils.setPassWindowBlurEnabled(View,boolean)
    private static Method M_SET_MI_BLUR_WIN_EXC;   // View.setMiBlurWinExc(String)
    private static Method M_GLASS_BLUR_RADIUS;     // MiuiBlurUtils.setMiGlassBlurRadius(View,int,int)
    private static Method M_BG_BLUR_MODE;          // MiuiBlurUtils.setMiBackgroundBlurMode(int,View)
    private static Method M_BG_BLUR_RADIUS;        // MiuiBlurUtils.setMiBackgroundBlurRadius(int,View)
    private static Method M_CHOOSE_BG_BLUR;        // MiuiBlurUtils.chooseBackgroundBlurContainer(View,View)
    private static Method M_SET_MI_GLASS;          // View.setMiGlass(float[])
    private static Method M_SET_VIEW_MAT_TYPE;     // View.setMiViewMaterialType(int)
    private static Method M_SET_MI_VIEW_BLUR_MODE; // MiuiBlurUtils.setMiViewBlurMode(int,View)
    private static Method M_SET_SURFACE_COLOR_TYPE;// MiuiBlurUtils.setMiCustomSurfaceColorType(int,View)

    private static boolean APIS_RESOLVED;
    private static String API_LOG = "?";

    /**
     * 官方 TimeView 的 42 项玻璃配方（原值，逐项手工抄自 smali）。
     *
     * 来源：hyperos4_sysui_decode/smali_classes3/com/miui/clock/allInOne/TimeView.smali
     *       array_0，共 42 项 float。
     *
     * 索引语义（TimeView 的 GLASS_*_INDEX 常量实证）：
     *   [5]  饱和度            2.0
     *   [6]  亮度              0.3
     *   [11] 颜色 R            1.0
     *   [12] 颜色 G            1.0
     *   [13] 颜色 B            1.0
     *   [14] 颜色 A            0.4
     *   [16] 混色              0.0
     *   [17] 颜色强度          1.1
     *   [19] edgePx  → StrokeWidth 30.0
     *   [20] edgePow → StrokeMiter 2.0
     *   [21] SDF 上限 w        200.0
     *   [22] SDF 上限 h        400.0
     */
    private static final float[] OFFICIAL_GLASS_DATA = new float[]{
            0.05f, 0.35f, 0.5f, 0.55f,          // 0..3
            1.0f, 2.0f, 0.3f, 0.0f,             // 4..7
            0.0f, 1.0f, 0.05f, 1.0f,            // 8..11
            1.0f, 1.0f, 0.4f, 0.8f,             // 12..15
            0.0f, 1.1f, 1.0f, 30.0f,            // 16..19
            2.0f, 200.0f, 400.0f, 0.3f,         // 20..23
            2.0f, -2.0f, 2.0f, -1.0f,           // 24..27
            6.0f, 3.0f, 0.3f, 1.176f,           // 28..31
            1.33f, 1.0f, 1.0f, 1.0f,            // 32..35
            0.0f, 0.8f, 0.82f, 0.0f,            // 36..39
            0.0f, 0.0f                          // 40..41
    };

    private ClockGlassP0() {
    }

    // ==================================================================
    // 安装
    // ==================================================================
    static void install(XposedModule module, ClassLoader cl) {
        // 开关默认关 —— 必须显式 setprop 才生效，
        // 这样主人没开的时候模块行为与之前完全一致（零风险）。
        if (propInt(PROP_ON, 0) == 0) {
            L.always(TAG, "disabled (clock_p0=0)");
            return;
        }

        resolveApis(cl);

        try {
            Class<?> clockCls = Class.forName(
                    "com.android.systemui.statusbar.views.MiuiClock", false, cl);

            // 挂 onAttachedToWindow —— 时钟每次上屏都会走
            Method m = clockCls.getDeclaredMethod("onAttachedToWindow");
            m.setAccessible(true);
            module.hook(m)
                    .setId("hc_clock_p0_attach")
                    .setPriority(XposedInterface.PRIORITY_LOWEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        Object r = chain.proceed();
                        try {
                            Object self = chain.getThisObject();
                            if (self instanceof View) {
                                final View v = (View) self;
                                v.post(() -> maybeReplace(v));
                            }
                        } catch (Throwable t) {
                            L.always(TAG, "hook err: " + t);
                        }
                        return r;
                    });
            L.always(TAG, "installed; apis=" + API_LOG);
        } catch (Throwable t) {
            L.always(TAG, "install FAILED: " + t);
        }
    }

    // ==================================================================
    // 反射解析
    // ==================================================================
    private static void resolveApis(ClassLoader cl) {
        if (APIS_RESOLVED) return;
        APIS_RESOLVED = true;

        StringBuilder sb = new StringBuilder();
        Class<?> paintCls = null;
        try {
            paintCls = Class.forName("android.graphics.Paint", false, cl);
        } catch (Throwable t) {
            sb.append("paint=CLFAIL ");
        }

        // ---- Paint.setGlassEffect(boolean) ----
        if (paintCls != null) {
            try {
                M_SET_GLASS_EFFECT = paintCls.getDeclaredMethod("setGlassEffect", boolean.class);
                sb.append("effect=true ");
            } catch (Throwable t) {
                sb.append("effect=false ");
            }
            try {
                M_SET_ATLAS_MAX_PATH = paintCls.getDeclaredMethod(
                        "setAtlasMaxPathSize", int.class, int.class);
                sb.append("atlas=true ");
            } catch (Throwable t) {
                sb.append("atlas=false ");
            }
        }

        // ---- MiuiBlurUtils.simplifyPath(Path, Paint) + 采样环境 API ----
        try {
            Class<?> blur = Class.forName("com.miui.clock.utils.MiuiBlurUtils", false, cl);
            for (Method m : blur.getDeclaredMethods()) {
                String n = m.getName();
                int pc = m.getParameterCount();
                if ("simplifyPath".equals(n) && pc == 2) {
                    M_SIMPLIFY_PATH = m;
                } else if ("setPassWindowBlurEnabled".equals(n) && pc == 2) {
                    M_PASS_WINDOW_BLUR = m;
                } else if ("setMiGlassBlurRadius".equals(n) && pc == 3) {
                    M_GLASS_BLUR_RADIUS = m;
                } else if ("setMiBackgroundBlurMode".equals(n) && pc == 2) {
                    M_BG_BLUR_MODE = m;
                } else if ("setMiBackgroundBlurRadius".equals(n) && pc == 2) {
                    M_BG_BLUR_RADIUS = m;
                } else if ("chooseBackgroundBlurContainer".equals(n) && pc == 2) {
                    M_CHOOSE_BG_BLUR = m;
                } else if ("setMiGlass".equals(n) && pc == 2) {
                    M_SET_MI_GLASS = m;
                } else if ("setMiViewBlurMode".equals(n) && pc == 2) {
                    M_SET_MI_VIEW_BLUR_MODE = m;
                } else if ("setMiCustomSurfaceColorType".equals(n) && pc == 2) {
                    M_SET_SURFACE_COLOR_TYPE = m;
                }
            }
            sb.append("simplify=").append(M_SIMPLIFY_PATH != null)
              .append(" passBlur=").append(M_PASS_WINDOW_BLUR != null)
              .append(" bgBlur=").append(M_BG_BLUR_MODE != null && M_BG_BLUR_RADIUS != null)
              .append(" chooseC=").append(M_CHOOSE_BG_BLUR != null)
              .append(" blurMode=").append(M_SET_MI_VIEW_BLUR_MODE != null)
              .append(" surfColor=").append(M_SET_SURFACE_COLOR_TYPE != null)
              .append(' ');
        } catch (Throwable t) {
            sb.append("blurUtils=CLFAIL ");
        }

        // ---- View 级 API ----
        try {
            Class<?> viewCls = Class.forName("android.view.View", false, cl);
            try {
                M_SET_MI_GLASS_CLIP = viewCls.getDeclaredMethod("setMiGlassClip",
                        float.class, float.class, float.class, float.class);
                sb.append("clip=true ");
            } catch (Throwable t) {
                sb.append("clip=false ");
            }
            // ★ setMiGlassSdfMaxSize(float,float) —— HyperLight eb1.m 实证必需
            try {
                M_SET_SDF_MAX_SIZE = viewCls.getDeclaredMethod("setMiGlassSdfMaxSize",
                        float.class, float.class);
                sb.append("sdfMax=true ");
            } catch (Throwable t) {
                sb.append("sdfMax=false ");
            }
            try {
                M_SET_MI_GLASS = viewCls.getDeclaredMethod("setMiGlass", float[].class);
            } catch (Throwable t) { /* 上面 MiuiBlurUtils 版已兜底 */ }
            try {
                M_SET_MI_BLUR_WIN_EXC = viewCls.getDeclaredMethod("setMiBlurWinExc", String.class);
                sb.append("winExc=true ");
            } catch (Throwable t) {
                sb.append("winExc=false ");
            }
            try {
                M_SET_VIEW_MAT_TYPE = viewCls.getDeclaredMethod("setMiViewMaterialType", int.class);
                sb.append("matType=true ");
            } catch (Throwable t) {
                sb.append("matType=false ");
            }
            // ★ setMiCustomSurfaceColorType —— v376 实证的核心开关之一
            //   （当时补上 blurMode=3 + surfaceColor=16 后，通知卡片立刻变彩虹玻璃）
            //   它在 android.view.View 上，不在 MiuiBlurUtils 里。
            try {
                M_SET_SURFACE_COLOR_TYPE =
                        viewCls.getDeclaredMethod("setMiCustomSurfaceColorType", int.class);
                sb.append("surfColor=true ");
            } catch (Throwable t) {
                sb.append("surfColor=false ");
            }
        } catch (Throwable t) {
            sb.append("view=CLFAIL ");
        }

        API_LOG = sb.toString().trim();
        L.always(TAG, "apis: " + API_LOG);
    }

    // ==================================================================
    // 替换
    // ==================================================================
    private static void maybeReplace(View clock) {
        if (propInt(PROP_ON, 0) == 0) return;
        if (IN_HOOK.get() != null) return;
        if (DONE.contains(clock)) return;

        try {
            IN_HOOK.set(Boolean.TRUE);

            // 只认 @id/big_time，别碰日期时钟
            int id = clock.getId();
            if (id == View.NO_ID) return;
            String name;
            try {
                name = clock.getResources().getResourceEntryName(id);
            } catch (Throwable t) {
                return;
            }
            if (!"big_time".equals(name)) return;

            // ============================================================
            // 互斥检查：PortraitClockStyle（字体管线）现在管着这个 View。
            //
            // 两者都要动 big_time：
            //   · PortraitClockStyle 直接改它的 Typeface（不换 View）
            //   · P0 把它包进 FrameLayout 并设 INVISIBLE（换结构）
            //
            // 如果 P0 赢了，字体管线改的是 INVISIBLE 的官方时钟，白费；
            // 行为也对不上。所以这里显式要求：
            //   clock_style=0 时 P0 才工作（P0 需要接管整个时钟外观）。
            // ============================================================
            int styleOn = propInt("debug.hyperclassic.local.clock_style", 0);
            if (styleOn != 0) {
                L.always(TAG, "skip: clock_style=" + styleOn
                        + " (请先 setprop debug.hyperclassic.local.clock_style 0)");
                return;
            }

            ViewGroup parent = (ViewGroup) clock.getParent();
            if (parent == null) return;
            if (parent instanceof GlassHost) return;   // 已包装过

            // ============================================================
            // ★ 只处理竖屏 QS 的那个 big_time，别碰悬浮通知头部的 ★
            //
            // 实测（v378）SystemUI 里有两个 @id/big_time：
            //   ① parent = MiuiNotificationHeaderView  ← 悬浮通知(heads-up)头部时钟
            //   ② parent = LinearLayout                ← 竖屏 QS 通知头部大时钟 ★ 目标
            //
            // ① 也在 QS 布局里会连带刷新，如果我们把它也换掉，
            //    会导致悬浮通知头的时钟错乱，而且白白多一次 onDraw。
            // ⇒ 只认 LinearLayout 父容器那个。
            // ============================================================
            if (parent.getClass().getName().contains("MiuiNotificationHeaderView")) {
                L.always(TAG, "skip: parent=MiuiNotificationHeaderView (heads-up clock)");
                return;
            }

            // ============================================================
            // FrameLayout 原地包装（v373 已实机验证的布局安全做法）
            //
            // 为什么必须这样：
            //   父 LinearLayout 里 date_time 是 height=match_parent，
            //   它依赖 big_time 撑起父高度。
            //   · GONE  → 父容器塌成 0，日期消失
            //   · 兄弟 View → 总高超过 336px，日期被挤出可视区
            //   · 原地包装 → 子 View 数量/位置/LayoutParams 全不变 ✅
            // ============================================================
            ViewGroup.LayoutParams clockLp = clock.getLayoutParams();
            int idx = parent.indexOfChild(clock);

            GlassHost host = new GlassHost(clock.getContext());
            parent.removeViewAt(idx);
            host.addView(clock,
                    new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            parent.addView(host, idx, clockLp);

            // ★ 关键：把「绘制」交给一个纯 View 子类
            //
            // 【为什么不能直接在 FrameLayout 上 onDraw】
            //   ① ViewGroup 默认 setWillNotDraw(true)，onDraw 不执行（v378 已踩）
            //   ② 即使设 false，hwui 给 ViewGroup 的 RenderNode 与给 View 的不同。
            //      官方 TimeView / BlurProviderView 都是**纯 View**，
            //      UsageHint::SDF_GLASS → MiDrawGlassPath 这条路径对纯 View 才完全一致。
            //   ⇒ 布局用 FrameLayout（host），绘制用纯 View（paintView）。
            GlassPaintView paintView = new GlassPaintView(clock.getContext());
            host.addView(paintView, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            host.paintView = paintView;
            paintView.officialClock = clock;
            host.officialClock = clock;      // ★ onMeasure 用它量尺寸

            // INVISIBLE 而不是 GONE —— GONE 会让 host 的 wrap_content 塌成 0
            clock.setVisibility(View.INVISIBLE);
            DONE.add(clock);

            L.always(TAG, "replaced big_time #" + DONE.size()
                    + " parent=" + parent.getClass().getSimpleName()
                    + " idx=" + idx
                    + " clockLp=" + (clockLp == null ? "null"
                    : clockLp.width + "x" + clockLp.height));
        } catch (Throwable t) {
            L.always(TAG, "maybeReplace FAILED: " + t);
        } finally {
            IN_HOOK.remove();
        }
    }

    // ==================================================================
    // 自绘玻璃文字
    // ==================================================================

    /**
     * GlassHost —— 只负责布局（FrameLayout 容器）。
     *
     * 它做三件事：
     *   ① 包住官方时钟（INVISIBLE，保持父容器高度锚点）
     *   ② 放一个隐藏 TextView 作为「时间源 + 字体测量」
     *   ③ 放一个纯 View（GlassPaintView）承担真正的绘制
     *
     * 为什么绘制要单独一个纯 View：
     *   官方 TimeView / BlurProviderView 都是直接 extends View。
     *   hwui 对「ViewGroup 的 RenderNode」与「View 的 RenderNode」处理不同，
     *   UsageHint::SDF_GLASS → MiDrawGlassPath 这条路径在纯 View 上才是官方一致路径。
     */
    static final class GlassHost extends FrameLayout {

        final TextView text;
        GlassPaintView paintView;
        /** 官方时钟（INVISIBLE，作为尺寸锚点）。 */
        View officialClock;

        GlassHost(Context ctx) {
            super(ctx);
            // 依然打开绘制许可（虽然我们不在 host 上画，但保险）
            setWillNotDraw(false);

            text = new TextView(ctx);
            text.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            text.setIncludeFontPadding(false);
            text.setTextColor(0xFFFFFFFF);
            text.setVisibility(View.INVISIBLE);   // 只是时间源，不显示
            addView(text, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        /**
         * ★ 尺寸必须以官方时钟为准，不能用 MATCH_PARENT 撑满。
         *
         * 【v379 踩坑】
         *   FrameLayout 里有 MATCH_PARENT 子 View 时，onMeasure 会把
         *   AT_MOST(全屏宽) 直接传给子 View，于是 host 自己也被撑成 1334x389，
         *   结果把下面的 date_time 挤出可视区 —— 日期消失了。
         *
         * 【正解】
         *   手动测量：先量官方时钟拿自然尺寸，再把 paintView 精确设成同尺寸，
         *   自己 setMeasuredDimension 成时钟尺寸。这样父容器看到的
         *   子 View 尺寸与替换前 **完全一致**，布局零影响。
         */
        @Override
        protected void onMeasure(int wSpec, int hSpec) {
            if (officialClock == null) {
                super.onMeasure(wSpec, hSpec);
                return;
            }
            measureChild(officialClock, wSpec, hSpec);
            int w = officialClock.getMeasuredWidth();
            int h = officialClock.getMeasuredHeight();
            if (w <= 0 || h <= 0) {
                super.onMeasure(wSpec, hSpec);
                return;
            }
            int wExact = MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY);
            int hExact = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
            if (paintView != null) paintView.measure(wExact, hExact);
            text.measure(wExact, hExact);
            setMeasuredDimension(w, h);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            tick();
        }

        private void tick() {
            updateTimeText();
            if (paintView != null) paintView.syncStyle();
            invalidate();
            postDelayed(this::tick, 1000L);
        }

        /** 拉系统当前时间，格式与官方一致。 */
        private void updateTimeText() {
            try {
                Calendar c = Calendar.getInstance();
                boolean h24 = android.text.format.DateFormat.is24HourFormat(getContext());
                String s;
                if (h24) {
                    s = String.format(Locale.US, "%02d:%02d",
                            c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
                } else {
                    int h = c.get(Calendar.HOUR);
                    if (h == 0) h = 12;
                    s = String.format(Locale.US, "%d:%02d", h, c.get(Calendar.MINUTE));
                }
                text.setText(s);
            } catch (Throwable t) {
                L.always(TAG, "updateTimeText: " + t);
            }
        }
    }

    /**
     * GlassPaintView —— 纯 View，承担全部绘制。
     *
     * 【为什么必须是纯 View】
     *   见 GlassHost 注释。官方 TimeView extends View，不是 ViewGroup。
     */
    static final class GlassPaintView extends View {

        /** 时间源（宿主里的 TextView）。 */
        TextView timeSource;
        /** 官方时钟（保留引用，备用）。 */
        View officialClock;

        private final TextPaint glassPaint;
        private final Paint plainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path textPath = new Path();
        private final RectF tmpRect = new RectF();

        private int lastMode = -1;
        private int lastSize = -1;
        private int lastWeight = -1;
        private boolean envApplied;

        private final java.util.concurrent.atomic.AtomicInteger paintPass =
                new java.util.concurrent.atomic.AtomicInteger();

        GlassPaintView(Context ctx) {
            super(ctx);
            // 纯 View 也需要显式许可（保险 + 明确语义）
            setWillNotDraw(false);

            plainPaint.setColor(0xFFFFFFFF);
            plainPaint.setStyle(Paint.Style.FILL);

            glassPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            glassPaint.setColor(0xFFFFFFFF);
            glassPaint.setStyle(Paint.Style.FILL);
        }

        void syncStyle() {
            if (timeSource == null) {
                ViewParent p = getParent();
                if (p instanceof GlassHost) timeSource = ((GlassHost) p).text;
            }
            if (timeSource == null) return;

            int size = propInt("debug.hyperclassic.local.clock_p0_size", 44);
            int weight = propInt("debug.hyperclassic.local.clock_p0_weight", 700);
            int mode = propInt("debug.hyperclassic.local.clock_p0_mode", 0);

            if (size == lastSize && weight == lastWeight && mode == lastMode) return;
            lastSize = size;
            lastWeight = weight;
            lastMode = mode;

            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    size, getResources().getDisplayMetrics());

            Typeface tf = null;
            try {
                tf = Typeface.create("mipro", Typeface.NORMAL);
                if (weight > 0 && tf != null) {
                    tf = Typeface.create(tf, weight, false);
                }
            } catch (Throwable t) {
                L.always(TAG, "typeface: " + t);
            }
            if (tf != null) {
                timeSource.setTypeface(tf);
                glassPaint.setTypeface(tf);
                plainPaint.setTypeface(tf);
            }
            timeSource.setTextSize(TypedValue.COMPLEX_UNIT_PX, px);
            glassPaint.setTextSize(px);
            plainPaint.setTextSize(px);

            L.always(TAG, "style size=" + size + " weight=" + weight
                    + " mode=" + mode + " px=" + (int) px);
            invalidate();
        }

        @Override
        protected void onSizeChanged(int w, int h, int ow, int oh) {
            super.onSizeChanged(w, h, ow, oh);
            L.always(TAG, "paintView size " + w + "x" + h);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (timeSource == null) syncStyle();
            if (timeSource == null) return;

            int mode = lastMode < 0 ? 0 : lastMode;

            CharSequence cs = timeSource.getText();
            String s = cs == null ? "" : cs.toString();
            if (s.isEmpty()) return;

            textPath.reset();
            glassPaint.getTextPath(s, 0, s.length(), 0f, 0f, textPath);
            textPath.computeBounds(tmpRect, true);
            float dx = (getWidth() - tmpRect.width()) * 0.5f - tmpRect.left;
            float dy = (getHeight() - tmpRect.height()) * 0.5f - tmpRect.top;
            textPath.offset(dx, dy);
            textPath.computeBounds(tmpRect, true);

            if (mode == 2) {
                paintPass.incrementAndGet();
                canvas.drawPath(textPath, plainPaint);
                if (paintPass.get() <= 3) {
                    L.always(TAG, "drawPath(plain) #" + paintPass.get()
                            + " view=" + getWidth() + "x" + getHeight()
                            + " bounds=" + (int) tmpRect.width() + "x"
                            + (int) tmpRect.height());
                }
                return;
            }

            if (mode == 3) {
                paintPass.incrementAndGet();
                Paint.FontMetrics fm = plainPaint.getFontMetrics();
                float baseline = (getHeight() - fm.top - fm.bottom) * 0.5f - fm.bottom;
                canvas.drawText(s, dx + tmpRect.left - (tmpRect.left),
                        baseline, plainPaint);
                if (paintPass.get() <= 3) {
                    L.always(TAG, "drawText(plain) #" + paintPass.get());
                }
                return;
            }

            // ===== mode=0：Path + Paint 玻璃 =====
            if (propInt("debug.hyperclassic.local.clock_p0_env", 1) == 1) {
                applyGlassEnv();
            }

            if (M_SET_MI_GLASS_CLIP != null) {
                try {
                    M_SET_MI_GLASS_CLIP.invoke(this,
                            tmpRect.left - 50f, tmpRect.top - 50f,
                            tmpRect.right + 50f, tmpRect.bottom + 50f);
                } catch (Throwable t) {
                    L.always(TAG, "clip: " + t);
                }
            }

            // ★★ setMiGlassSdfMaxSize(w, h) —— HyperLight 用、我们一直漏掉 ★★
            //
            // 【HyperLight eb1.f 实证】它把 View 的 getWidth/getHeight 传进去：
            //   iget-object v5, Leb1;->m   (= setMiGlassSdfMaxSize)
            //   int-to-float v6, v3        (= view.getWidth())
            //   int-to-float v9, v4        (= view.getHeight())
            //   filled-new-array {v6, v9}, [Ljava/lang/Object;
            //   invoke-virtual {v5, v0, v6}   // v0 = 目标 View
            //
            // 这是 SDF 纹理的预分配尺寸。不给或给错，
            // MiDrawGlassPath 拿到的 SDF 就是空白／全内侧 → 输出纯色块（黄块）。
            if (M_SET_SDF_MAX_SIZE != null) {
                try {
                    float sw = getWidth() > 0 ? getWidth() : 400f;
                    float sh = getHeight() > 0 ? getHeight() : 400f;
                    M_SET_SDF_MAX_SIZE.invoke(this, sw, sh);
                } catch (Throwable t) {
                    L.always(TAG, "setSdfMaxSize: " + t);
                }
            }

            // ★★ setMiGlass(42项) —— MiDrawGlassPath 的 uniform 参数来源 ★★
            //
            // 【为什么必须有】
            //   MiDrawGlassPath 的 runtime shader 需要这些 uniform：
            //     uAlpha_shapeEdgeThickReflect   ← glassData[0..3]
            //     uRefractIORStrengthLight_clrpow← glassData[4..7] 等
            //     uGlassColor                    ← glassData[11..14]
            //     ...
            //   这些值来自 View 的 glassData 数组，入口就是 setMiGlass(float[])。
            //
            // 【它不是「材质开关」】
            //   真正画矩形的是 setMiViewMaterialType(1)。
            //   setMiGlass 只负责把 42 项参数塞进 ViewProperty，
            //   供后续 MiDrawGlassPath 读取。所以我们**保留**它。
            if (M_SET_MI_GLASS != null) {
                try {
                    M_SET_MI_GLASS.invoke(this, OFFICIAL_GLASS_DATA);
                } catch (Throwable t) {
                    L.always(TAG, "setMiGlass: " + t);
                }
            }
            if (M_SET_GLASS_EFFECT != null) {
                try {
                    M_SET_GLASS_EFFECT.invoke(glassPaint, true);
                } catch (Throwable t) {
                    L.always(TAG, "setGlassEffect: " + t);
                }
            }
            if (M_SET_ATLAS_MAX_PATH != null) {
                try {
                    int atlas = propInt("debug.hyperclassic.local.clock_p0_atlas", 2048);
                    M_SET_ATLAS_MAX_PATH.invoke(glassPaint, atlas, atlas);
                } catch (Throwable t) {
                    L.always(TAG, "setAtlas: " + t);
                }
            }
            if (M_SIMPLIFY_PATH != null) {
                try {
                    M_SIMPLIFY_PATH.invoke(null, textPath, glassPaint);
                } catch (Throwable ignored) { }
            }

            paintPass.incrementAndGet();
            canvas.drawPath(textPath, glassPaint);
            if (paintPass.get() <= 5) {
                L.always(TAG, "drawPath(glass) #" + paintPass.get()
                        + " view=" + getWidth() + "x" + getHeight()
                        + " bounds=" + (int) tmpRect.left + ","
                        + (int) tmpRect.top + "-" + (int) tmpRect.right + ","
                        + (int) tmpRect.bottom);
            }
        }

        @Override
        public boolean hasOverlappingRendering() {
            return false;
        }

        /** 建立玻璃采样环境（让 uBgColorTex 有内容）。 */
        private void applyGlassEnv() {
            if (envApplied) return;
            envApplied = true;

            StringBuilder sb = new StringBuilder();
            int blur = propInt("debug.hyperclassic.local.clock_p0_blur", 60);

            if (M_PASS_WINDOW_BLUR != null) {
                try {
                    Object r = M_PASS_WINDOW_BLUR.invoke(null, this, true);
                    sb.append("passBlur=").append(r).append(' ');
                } catch (Throwable t) {
                    sb.append("passBlur=ERR ");
                }
            }
            if (M_SET_MI_BLUR_WIN_EXC != null) {
                try {
                    M_SET_MI_BLUR_WIN_EXC.invoke(this, "NotificationShade");
                    sb.append("winExc=ok ");
                } catch (Throwable t) {
                    sb.append("winExc=ERR ");
                }
            }
            if (M_BG_BLUR_MODE != null) {
                try {
                    M_BG_BLUR_MODE.invoke(null, 1, this);
                    sb.append("bgMode=1 ");
                } catch (Throwable t) { sb.append("bgMode=ERR "); }
            }
            if (M_BG_BLUR_RADIUS != null) {
                try {
                    M_BG_BLUR_RADIUS.invoke(null, blur, this);
                    sb.append("bgRadius=").append(blur).append(' ');
                } catch (Throwable t) { sb.append("bgRadius=ERR "); }
            }
            if (M_GLASS_BLUR_RADIUS != null) {
                try {
                    M_GLASS_BLUR_RADIUS.invoke(null, this, blur, blur);
                    sb.append("glassBlur=").append(blur).append(' ');
                } catch (Throwable t) { sb.append("glassBlur=ERR "); }
            }
            if (M_SET_MI_VIEW_BLUR_MODE != null) {
                try {
                    M_SET_MI_VIEW_BLUR_MODE.invoke(null, 3, this);
                    sb.append("blurMode=3 ");
                } catch (Throwable t) { sb.append("blurMode=ERR "); }
            }
            if (propInt("debug.hyperclassic.local.clock_p0_choose", 0) == 1) {
                View container = findBlurProvider();
                if (container != null && M_CHOOSE_BG_BLUR != null) {
                    try {
                        M_CHOOSE_BG_BLUR.invoke(null, this, container);
                        sb.append("chooseC=")
                          .append(container.getClass().getSimpleName()).append(' ');
                    } catch (Throwable t) { sb.append("chooseC=ERR "); }
                } else {
                    sb.append("chooseC=skip ");
                }
            }
            L.always(TAG, "glass env: " + sb);
        }

        private View findBlurProvider() {
            try {
                View root = this;
                while (root.getParent() instanceof View) {
                    root = (View) root.getParent();
                }
                return findByIdInTree(root, "blur_provider", 0);
            } catch (Throwable t) {
                return null;
            }
        }

        private View findByIdInTree(View v, String idName, int depth) {
            if (depth > 30 || v == null) return null;
            try {
                int id = v.getId();
                if (id != View.NO_ID) {
                    String n = v.getResources().getResourceEntryName(id);
                    if (idName.equals(n)) return v;
                }
            } catch (Throwable ignored) { }
            if (v instanceof ViewGroup) {
                ViewGroup g = (ViewGroup) v;
                for (int i = 0; i < g.getChildCount(); i++) {
                    View r = findByIdInTree(g.getChildAt(i), idName, depth + 1);
                    if (r != null) return r;
                }
            }
            return null;
        }
    }
    // ==================================================================
    // 工具
    // ==================================================================
    static int propInt(String key, int def) {
        try {
            Class<?> sp = Class.forName("android.os.SystemProperties");
            Method g = sp.getMethod("getInt", String.class, int.class);
            Object v = g.invoke(null, key, def);
            return v instanceof Integer ? (Integer) v : def;
        } catch (Throwable t) {
            return def;
        }
    }
}
