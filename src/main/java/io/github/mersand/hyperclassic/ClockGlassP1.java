package io.github.mersand.hyperclassic;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Method;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;

/**
 * ClockGlassP1 —— 【P1】按 HyperLight 正解：Hook 官方 TextView.onDraw
 *
 * ==================================================================
 * ===== 一、为什么推翻 P0 =====
 * ==================================================================
 *
 * P0 用的是「自建 View + 在自建 View 上设玻璃」，八轮全部输出黄块。
 *
 * 2026-11-26 逆向 HyperLight 得到正确答案（eb1.smali / t11.smali 实证）：
 *   HyperLight **不换 View**，它 hook 官方 TextView 的 onDraw，
 *   然后：
 *     ① 玻璃参数全部设在【官方 TextView 自己】身上
 *     ② 但绘制用一个【new Paint(textView.getPaint()) 副本】
 *
 *   ⇒ 玻璃材质必须挂在**官方 View** 上。
 *     自建 View 不在 MiUI 材质系统的管辖链里，
 *     所以 setMiGlass* 全部"成功"但渲染器永远拿不到数据 → 黄块。
 *
 * ==================================================================
 * ===== 二、HyperLight 的完整序列（eb1.f 逐条抄）=====
 * ==================================================================
 *
 *   前置校验：
 *     view.getWidth() > 0 && view.getHeight() > 0
 *     textView.getLayout() != null
 *     text 非空、无 \t、非全空白、非 RTL
 *
 *   在官方 TextView 上（eb1 字段映射）：
 *     eb1.m → View.setMiGlassSdfMaxSize(w, h)      ← P0 一直漏掉！★
 *     eb1.j → View.setMiViewMaterialType(int)
 *     eb1.k → View.setMiGlass(float[42])
 *     eb1.n → View.setMiViewBlurMode(3)
 *     eb1.p → View.setMiCustomSurfaceColorType(int)
 *     eb1.l → View.setMiGlassClip(l, t, r, b)
 *     eb1.o → View.setMiGlassBlurRadius(int, int)
 *
 *   新建 Paint 副本：
 *     Paint g = new Paint(textView.getPaint())
 *     eb1.q → Paint.setGlassEffect(true)
 *     eb1.r → Paint.setAtlasMaxPathSize(2048, 2048)
 *     g.setStrokeWidth(glassData[19])
 *     g.setStrokeMiter(glassData[20])
 *
 *   逐行绘制：
 *     for line in layout:
 *       paint.getTextPath(lineText, 0, len, x, y, path)
 *       eb1.j → MiuiBlurUtils.simplifyPath(path, paint)
 *       canvas.drawPath(path, g)
 *
 * ==================================================================
 * ===== 三、开关 =====
 * ==================================================================
 *
 *   debug.hyperclassic.local.clock_p1=1           总开关
 *   debug.hyperclassic.local.clock_p1_mode=0      0=玻璃 2=纯白字(对照) 3=原样放行
 *   debug.hyperclassic.local.clock_p1_sdf=1       1=调 setMiGlassSdfMaxSize（关键）
 *   debug.hyperclassic.local.clock_p1_blur=60     玻璃模糊半径
 *   debug.hyperclassic.local.clock_p1_mat=0       setMiViewMaterialType 参数（0=不调）
 *   debug.hyperclassic.local.clock_p1_surf=0      setMiCustomSurfaceColorType（0=不调）
 *   debug.hyperclassic.local.clock_p1_clip=50     setMiGlassClip 外扩像素
 */
final class ClockGlassP1 {

    private static final String TAG = "HCClockP1";

    private static final String PROP_ON = "debug.hyperclassic.local.clock_p1";

    // ---- Paint 级 ----
    private static Method M_SET_GLASS_EFFECT;      // Paint.setGlassEffect(boolean)
    private static Method M_SET_ATLAS_MAX_PATH;    // Paint.setAtlasMaxPathSize(int,int)

    // ---- View 级（设在官方 TextView 自身上）----
    private static Method M_SET_MAT_TYPE;          // setMiViewMaterialType(int)
    private static Method M_SET_GLASS;             // setMiGlass(float[])
    private static Method M_SET_GLASS_CLIP;        // setMiGlassClip(f,f,f,f)
    private static Method M_SET_SDF_MAX;           // setMiGlassSdfMaxSize(f,f)  ★
    private static Method M_SET_BLUR_MODE;         // setMiViewBlurMode(int)
    private static Method M_SET_GLASS_BLUR_R;      // setMiGlassBlurRadius(int,int)
    private static Method M_SET_SURF_COLOR;        // setMiCustomSurfaceColorType(int)

    // ---- 工具 ----
    private static Method M_SIMPLIFY_PATH;         // MiuiBlurUtils.simplifyPath(Path,Paint)

    private static boolean RESOLVED;
    private static String API_LOG = "?";

    /**
     * 42 项配方（可按需运行时覆盖）。
     *
     * 【为什么需要运行时覆盖】2026-11-26 实测症状：
     *   「直线段完整保留，曲线段整块消失」。
     *
     * 判断：MiDrawGlassPath 用 SDF 梯度计算玻璃法线 / 折射，
     *   glassData[19] = 30.0 既当 Paint.setStrokeWidth，
     *   也是 shader 的 edgePx（边缘带宽）。
     *   30px 的带宽 > 我们的笔画宽度，曲率半径 < 30px 的圆弧
     *   （如「3」的上下两个半圆）内部整体落进 SDF 饱和区，
     *   梯度恒为 0 → 法线退化 → 该区域被丢弃。
     *   直线段没有曲率问题，所以被保留。
     *
     *   30.0 是官方 TimeView（字号 200+）的配方，
     *   套到我们 44px 的时钟上尺度完全不对。
     *
     * 【用法】不用重编译，直接 setprop 后重绘：
     *   debug.hyperclassic.local.clock_p1_edge=2       → 覆盖 [19]
     *   debug.hyperclassic.local.clock_p1_miter=2      → 覆盖 [20]
     *   debug.hyperclassic.local.clock_p1_sdfw=200     → 覆盖 [21]
     *   debug.hyperclassic.local.clock_p1_sdfh=400     → 覆盖 [22]
     *   debug.hyperclassic.local.clock_p1_gd="19=2;6=0.3"   → 任意索引批量覆盖
     */
    private static float[] GLASS_CACHE;
    private static String GLASS_CACHE_KEY;
private static float[] glassData() {
        float edge = propFloat("debug.hyperclassic.local.clock_p1_edge", 15f);
        float miter = propFloat("debug.hyperclassic.local.clock_p1_miter", 2f);
        // 【2026-11-26 修】默认 0 = 自动取 View 尺寸
        //
        // 之前固定 200x400，而 View 是 357x199。
        // 我们命中的 B 套 shader 采样是 uSdfTex.eval(cornerPos)，
        // 直接把像素坐标当 texel 索引（无缩放），
        // 所以纹理横向只有 200 texel 时，x>200 的字形被整块裁掉
        // ——实测「最右侧的 8 右边被削平」正是此因。
        float sdfw = propFloat("debug.hyperclassic.local.clock_p1_sdfw", 0f);
        float sdfh = propFloat("debug.hyperclassic.local.clock_p1_sdfh", 0f);
        // 【2026-11-26 定版】自复刻灰底配方（主人实测 20:24 版确认）
        //
        // [11][12][13] = 颜色 RGB，[14] = 颜色 A，[16] = 混色，[17] = 颜色强度
        // 之前误把 [11..13] 设成 0.42 暗灰 → 整体发灰；
        // 现在提亮到 0.93/0.93/0.96 近白，[16]=0.62 保留部分背景采样，
        // 高亮白背景下依然可读，同时不丢玻璃通透感。
        String spec = propStr("debug.hyperclassic.local.clock_p1_gd",
                "0=0.67;1=0.50;3=0.20;11=0.93;12=0.93;13=0.96;14=0.78;16=0.62;17=1.15");

        String key = edge + "|" + miter + "|" + sdfw + "|" + sdfh + "|" + spec;
        if (key.equals(GLASS_CACHE_KEY) && GLASS_CACHE != null) return GLASS_CACHE;

        float[] gd = GLASS_DATA.clone();
        gd[19] = edge;
        gd[20] = miter;
        // sdfw/sdfh 为 0 时由调用方（drawGlass）填 View 实际尺寸
        gd[21] = sdfw;
        gd[22] = sdfh;

        // spec 形如 "19=2;20=3;5=1.5"（分号或逗号分隔）
        if (spec.length() > 0) {
            for (String part : spec.split("[;,]")) {
                int eq = part.indexOf('=');
                if (eq <= 0) continue;
                try {
                    int idx = Integer.parseInt(part.substring(0, eq).trim());
                    float val = Float.parseFloat(part.substring(eq + 1).trim());
                    if (idx >= 0 && idx < gd.length) gd[idx] = val;
                } catch (Throwable ignored) { }
            }
        }

        GLASS_CACHE = gd;
        GLASS_CACHE_KEY = key;
        L.always(TAG, "glassData applied: edge=" + gd[19] + " miter=" + gd[20]
                + " sdf=" + gd[21] + "x" + gd[22]
                + (spec.length() > 0 ? " gd=" + spec : ""));
        return gd;
    }

    /** 官方 TimeView 42 项配方基线（与 HyperLight eb1.a 逐项一致）。 */
    private static final float[] GLASS_DATA = new float[]{
            0.05f, 0.35f, 0.5f, 0.55f,
            1.0f, 2.0f, 0.3f, 0.0f,
            0.0f, 1.0f, 0.05f, 1.0f,
            1.0f, 1.0f, 0.4f, 0.8f,
            0.0f, 1.1f, 1.0f, 30.0f,     // [19] edgePx → StrokeWidth
            2.0f, 200.0f, 400.0f, 0.3f,
            2.0f, -2.0f, 2.0f, -1.0f,     // [20] edgePow → StrokeMiter
            6.0f, 3.0f, 0.3f, 1.176f,
            1.33f, 1.0f, 1.0f, 1.0f,
            0.0f, 0.8f, 0.82f, 0.0f,
            0.0f, 0.0f
    };

    private ClockGlassP1() {
    }

    // ==================================================================
    static void install(XposedModule module, ClassLoader cl) {
        if (propInt(PROP_ON, 1) == 0) {
            L.always(TAG, "disabled (clock_p1=0)");
            return;
        }
        resolve(cl);

        try {
            Class<?> tvCls = Class.forName("android.widget.TextView", false, cl);
            Method m = tvCls.getDeclaredMethod("onDraw", Canvas.class);
            m.setAccessible(true);
            module.hook(m)
                    .setId("hc_clock_p1_draw")
                    .setPriority(XposedInterface.PRIORITY_LOWEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        try {
                            Object self = chain.getThisObject();
                            if (self instanceof TextView
                                    && isTarget((TextView) self)) {
                                int mode = propInt(
                                        "debug.hyperclassic.local.clock_p1_mode", 0);
                                if (mode != 3) {
                                    Object canvas = chain.getArg(0);
                                    if (canvas instanceof Canvas
                                            && drawGlass((TextView) self,
                                                    (Canvas) canvas, mode)) {
                                        return null;   // 已接管，不再走官方
                                    }
                                }
                            }
                        } catch (Throwable t) {
                            L.log(5, TAG, "onDraw hook: " + t);
                        }
                        return chain.proceed();
                    });
            L.always(TAG, "installed; apis=" + API_LOG);
        } catch (Throwable t) {
            L.always(TAG, "install FAILED: " + t);
        }
    }

    // ==================================================================
    /** 只抓竖屏 QS 通知头部的大时钟（@id/big_time，且父不是 heads-up 容器）。 */
    private static boolean isTarget(TextView tv) {
        try {
            int id = tv.getId();
            if (id == View.NO_ID) return false;
            String n = tv.getResources().getResourceEntryName(id);
            if (!"big_time".equals(n)) return false;
            // 排除悬浮通知头部那个
            android.view.ViewParent p = tv.getParent();
            if (p != null && p.getClass() != null
                    && p.getClass().getName().contains("MiuiNotificationHeaderView")) {
                return false;
            }
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    // ==================================================================
    private static boolean drawGlass(TextView tv, Canvas canvas, int mode) {
        int w = tv.getWidth();
        int h = tv.getHeight();
        if (w <= 0 || h <= 0) return false;

        Layout layout = tv.getLayout();
        if (layout == null) return false;
        CharSequence text = tv.getText();
        if (text == null || text.length() == 0) return false;

        // ---- ① 玻璃参数设在【官方 TextView 自己】身上 ----
        //   顺序严格照 HyperLight eb1.c（不是 eb1.f！eb1.c 是 View 级环境建立）：
        //     setMiViewMaterialType(1)
        //     setMiViewBlurMode(3)
        //     setMiCustomSurfaceColorType(16)      ★ 固定 16
        //     setMiGlassClip(0, 0, w, h)           ★ 不是 ±50 外扩！
        //     setMiGlass(42项)
        int blur = propInt("debug.hyperclassic.local.clock_p1_blur", 60);

        // ★ setMiGlassSdfMaxSize(w, h) —— 用 View 自己的尺寸
        if (propInt("debug.hyperclassic.local.clock_p1_sdf", 1) == 1
                && M_SET_SDF_MAX != null) {
            try {
                M_SET_SDF_MAX.invoke(tv, (float) w, (float) h);
            } catch (Throwable t) {
                onceLog("sdfMax", t);
            }
        }

        // ① setMiViewMaterialType(1) —— eb1.c 固定传 1
        int matType = propInt("debug.hyperclassic.local.clock_p1_mat", 1);
        if (matType > 0 && M_SET_MAT_TYPE != null) {
            try {
                M_SET_MAT_TYPE.invoke(tv, matType);
            } catch (Throwable t) {
                onceLog("matType", t);
            }
        }

        // ② setMiViewBlurMode(3)
        if (M_SET_BLUR_MODE != null) {
            try {
                M_SET_BLUR_MODE.invoke(tv, 3);
            } catch (Throwable t) {
                onceLog("blurMode", t);
            }
        }

        // ③ setMiCustomSurfaceColorType(16) —— eb1.c 固定传 0x10 = 16
        int surf = propInt("debug.hyperclassic.local.clock_p1_surf", 16);
        if (surf > 0 && M_SET_SURF_COLOR != null) {
            try {
                M_SET_SURF_COLOR.invoke(tv, surf);
            } catch (Throwable t) {
                onceLog("surfColor", t);
            }
        }

        // ④ setMiGlassClip(0, 0, w, h) —— eb1.c 实证是 (0,0,w,h)
        //   【注意】这与 TimeView 的 ±50 外扩不同！
        //   两种都是官方代码，但 eb1.c 是给 TextView 做 Path 玻璃用的。
        if (M_SET_GLASS_CLIP != null) {
            try {
                int mode2 = propInt("debug.hyperclassic.local.clock_p1_clipmode", 0);
                if (mode2 == 0) {
                    // eb1.c 风格：整块 View
                    M_SET_GLASS_CLIP.invoke(tv, 0f, 0f, (float) w, (float) h);
                } else {
                    // TimeView 风格：字形 bounds ± ext
                    int ext = propInt("debug.hyperclassic.local.clock_p1_clip", 50);
                    float l = tv.getTotalPaddingLeft();
                    float tp = tv.getTotalPaddingTop();
                    float r = w - tv.getTotalPaddingRight();
                    float b = h - tv.getTotalPaddingBottom();
                    M_SET_GLASS_CLIP.invoke(tv, l - ext, tp - ext, r + ext, b + ext);
                }
            } catch (Throwable t) {
                onceLog("clip", t);
            }
        }

float[] gd = glassData();

        // 【2026-11-26 修】sdfw/sdfh 传 0 时自动取 View 实际尺寸
        //
        // 【为什么必须跟随 View】
        //   我们命中的 B 套 shader 采样是 uSdfTex.eval(cornerPos)，
        //   cornerPos 是 View 内像素坐标，直接当 texel 索引使用（无缩放）。
        //   纹理小于 View 时，超出的字形被整体裁掉。
        //   实测：sdfw=200 而 View=357 → 最右侧的「8」右半边被削平。
        if (gd[21] <= 0f || gd[22] <= 0f) {
            float[] auto = gd.clone();
            if (auto[21] <= 0f) auto[21] = (float) w;
            if (auto[22] <= 0f) auto[22] = (float) h;
            gd = auto;   // 不改缓存，避免下次 View 尺寸变化后仍用旧值
        }

        // ⑤ setMiGlass(42项) —— eb1.c 里在最后
        if (M_SET_GLASS != null) {
            try {
                M_SET_GLASS.invoke(tv, gd);
            } catch (Throwable t) {
                onceLog("glass", t);
            }
        }

        if (M_SET_GLASS_BLUR_R != null) {
            try {
                M_SET_GLASS_BLUR_R.invoke(tv, blur, blur);
            } catch (Throwable t) {
                onceLog("glassBlurRadius", t);
            }
        }

        // ---- ③ 新建 Paint 副本，开玻璃 ----
        Paint g = new Paint(tv.getPaint());
        // ★★★ 字重（可变字体 wght 轴）★★★
        //
        // 【2026-11-26 关键发现：之前 wght 无效的真正原因】
        //
        //   /system/fonts/MiSansVF_Overlay.ttf 是一个【软链接】：
        //     MiSansVF_Overlay.ttf
        //       → /data/system/fonts/theme_webview/Roboto-Regular.ttf
        //       → /data/system/theme/fonts/Roboto-Regular.ttf
        //
        //   也就是说：系统 sans-serif 的 MiSansVF 槽位被【主题字体替换】
        //   成了一个【静态 Roboto-Regular】——它根本没有 wght 可变轴！
        //
        //   这解释了为什么 Typeface.create(weight) + setFontVariationSettings
        //   都返回 true 却毫无视觉变化：作用在一个没有 wght 轴的字体上，
        //   请求被静默忽略。
        //
        // 【正解】绕过主题替换，直接加载真正的可变字体：
        //   /product/fonts/MiSansLatinVF.ttf  483KB  数字/拉丁（时钟只用数字）
        //   /product/fonts/MiSansVF.ttf        20MB  全字符集
        //
        //   用 Typeface.Builder(File).setFontVariationSettings("'wght' N")
        //   直接生成带轴值的实例，得到【真正的 Bold 字形轮廓】。
        //
        //   注意 fonts.xml 里 system 注册的 weight↔stylevalue 是收缩映射
        //   （weight=700 → axis=450，weight=950 → axis=700），
        //   直接给 axis 值反而更可控：
        //     scene 默认约 330~450，Bold 需要 600~700。
        //
        // 开关：clock_p1_wght=0 不改 / 600 半粗 / 700 粗 / 800 很粗 / 900 特粗
        // 【2026-11-26 定版】默认 600（主人实测最合适的粗细）
        int wght = propInt("debug.hyperclassic.local.clock_p1_wght", 600);
        if (wght > 0) {
            Typeface vf = vfTypeface(wght);
            if (vf != null) {
                g.setTypeface(vf);
            } else {
                // 兜底：走旧的 Typeface.create 路径
                Typeface base = g.getTypeface();
                if (base == null) base = Typeface.DEFAULT;
                Typeface weighted = null;
                try {
                    Method create3 = Typeface.class.getMethod("create",
                            Typeface.class, int.class, boolean.class);
                    Object o = create3.invoke(null, base, wght, false);
                    if (o instanceof Typeface) weighted = (Typeface) o;
                } catch (Throwable ignored) { }
                if (weighted != null) g.setTypeface(weighted);
            }
            onceLog("wght" + wght, new Throwable("vf=" + (vf != null)
                    + " tf=" + (g.getTypeface() != null)));
        }


        if (mode == 0) {
            if (M_SET_GLASS_EFFECT != null) {
                try {
                    M_SET_GLASS_EFFECT.invoke(g, true);
                } catch (Throwable t) {
                    onceLog("setGlassEffect", t);
                }
            }
            if (M_SET_ATLAS_MAX_PATH != null) {
                try {
                    M_SET_ATLAS_MAX_PATH.invoke(g, 2048, 2048);
                } catch (Throwable t) {
                    onceLog("atlas", t);
                }
            }
            // 描边参数来自配方
            g.setStrokeWidth(gd[19]);
            g.setStrokeMiter(gd[20]);
        }

        // ---- ④ 逐字符 getTextPath + drawPath ----
        //
        // 【为什么必须逐字符】2026-11-26 实测：
        //   整段合并成一个 Path 只有 bounds 353x199 一次性 drawPath，
        //   但 MiDrawGlassPath 用的 SDF 纹理只有 glassData[21]x[22] = 200x400，
        //   横向精度只剩 60% → 曲线的弧度被量化抹平，
        //   表现为「直线还在、曲线消失」（实测：7 显示、8 整块消失、9 只剩下半）。
        //
        // HyperLight eb1.f 的循环体就是每段各自 canvas.drawPath(见 1945 行)，
        // 每段单独生成一次 SDF，精度足够。
        int chunk = propInt("debug.hyperclassic.local.clock_p1_chunk", 1);
        // 【2026-11-26 定版】default 0（关闭）。
        // 逐字符 SDF 会把一行拆成多段独立渲染，段与段之间的玻璃高光会断开；
        // 整行一次生成 SDF 更连贯，View 尺寸 357x199 完全放得下。
        // perCharSdf 关掉时用整行 SDF（精度足够）。
        boolean perCharSdf = propInt("debug.hyperclassic.local.clock_p1_perchar", 0) == 1;
        float perCharMargin = propFloat("debug.hyperclassic.local.clock_p1_margin", 6f);
        // 曲线离散化步长（px/段）。0=关闭。
        // 用于验证「SDF 文本通道不处理曲线指令」这个假设：
        //   clock_p1_flatten=1  → step=1.0px（最精细）
        //   clock_p1_flatten=2  → step=2.0px
        // 【2026-11-26 定版】default 1（开启）—— 曲线离散化。
        // flatten 后用 WINDING 规则重填，配合 simplifyPath 消除多笔画交界处的空白。
        //   clock_p1_flatten=0  → 关闭（对照）
        float flatStep = propFloat("debug.hyperclassic.local.clock_p1_flatten", 1f);

        // ---- 玻璃加粗：环形 stamping ----
        // 以半径 r 的圆环上均匀取 n 个点，把同一个 Path 各画一次。
        // 因为玻璃是半透明混合，重叠区 alpha 累加 → 视觉上笔画变粗变实，
        // 且不改变字形骨架比例（比 getFillPath 膨胀更自然）。
        //   clock_p1_stampr=0    关闭（默认）
        //   clock_p1_stampr=1.5  外扩 1.5px
        //   clock_p1_stampn=8    8 个方向
        float stampR = propFloat("debug.hyperclassic.local.clock_p1_stampr", 0f);
        int stampN = propInt("debug.hyperclassic.local.clock_p1_stampn", 8);
        // 【2026-11-26 定版】default 4。
        // 方向数 ≥8 时 UNION 后顶点数太多，SDF 纹理容纳不下会整条渲染失败；
        // 4 个方向即可覆盖正交加粗，且 grow=0 时本参数不生效。
        int morphDirs = propInt("debug.hyperclassic.local.clock_p1_morphdirs", 4);
        Path linePath = new Path();

        // ---- 加粗 ----
        //  bold=1 : g.setFakeBoldText(true) —— 影响 getTextPath 的 glyph 轮廓
        //  grow>0  : 形态学膨胀（morphGrow，Path.op UNION）
        //
        // 【2026-11-26 演进史，避免走回头路】
        //   ① getFillPath(FILL_AND_STROKE)：grow 越大反而越细。
        //      原因：描边轮廓自相交，WINDING 下环绕数抵消 → 镂空。
        //   ② canvas 多次 drawPath（环形 stamping）：粗细对了，
        //      但玻璃半透明叠加 → 边缘高光累积 N 次过曝。
        //   ③ 当前：morphGrow = 原 Path ∪ 沿圆环平移副本。
        //      UNION 内部处理重叠，粗细线性可控；最终只画 1 次，高光不叠加。
        // 【2026-11-26 定版】default 0（关闭）。
        //
        // setFakeBoldText(true) 会让 Skia 在字形轮廓外再扩一圈，
        // 与可变字体 wght 的加粗叠加后轮廓越界，顶部被 View 边界裁掉。
        // 现在的粗细完全由 wght 轴控制，不需要 FakeBold。
        boolean bold = propInt("debug.hyperclassic.local.clock_p1_bold", 0) == 1;
        if (bold) {
            try {
                g.setFakeBoldText(true);
            } catch (Throwable ignored) { }
        }

        // 【2026-11-26 定版】default 0（关闭）。
        // morphGrow 用 Path.op UNION 做形态学膨胀，方向数 ≥4 时顶点数暴增，
        // SDF 纹理容纳不下导致整条路径渲染失败（时钟整个消失）。
        // 粗细已由可变字体 wght 轴控制，这里保持关闭。
        float grow = propFloat("debug.hyperclassic.local.clock_p1_grow", 0f);

        int save = canvas.save();
        try {
            canvas.translate(tv.getTotalPaddingLeft() - tv.getScrollX(),
                    tv.getTotalPaddingTop() - tv.getScrollY());

            int lines = layout.getLineCount();
            String flat = text.toString();
            int drawn = 0;
            // ★★★ 水平自适应压缩（2026-11-26 修「边缘被裁」）★★★
            //
            // 【根因】TextView 的 Layout 是按【原始常规字重】排版的：
            //     layout.getPrimaryHorizontal(c) 给出的是原字形的推进宽度。
            //   Coding酱把 Paint 换成更粗的可变字体实例后，
            //   【字形本身变宽了，但字符位置没有重新排版】，
            //   于是整行实际宽度超出 Layout 测量值，
            //   最右侧的字被 View 边界 / clip 矩形裁掉。
            //
            //   实测证据：
            //     · wght=600 时最右字符右缘被削平
            //     · wght 降低后裁剪消失（字形变窄，正好回到边界内）
            //     · 放大 clip 矩形（±220）无效 —— 因为 Layout 宽度是死的
            //
            // 【第一版错在哪 · 2026-11-26 20:10 主人实测只有 3/4 宽】
            //   第一版用 g.measureText(整行) 当"实际宽度"。
            //   但 measureText 返回的是【advance 累加】（含字距、侧承），
            //   而实际绘制用的是 layout.getPrimaryHorizontal(c) 逐个定位，
            //   两者基准完全不同，前者明显偏大 →
            //   sx 被算成远小于 1 的值，整行被压到约 3/4。
            //
            // 【正解】按【真正要画的字形轮廓】量：
            //   用 getTextPath 生成整行 Path，computeBounds 得到实际墨迹范围，
            //   与可用宽度比较。这样只压真实溢出的那一点点。
            //     clock_p1_fit=0 关闭（对照）
            if (propInt("debug.hyperclassic.local.clock_p1_fit", 1) == 1) {
                try {
                    String line0 = flat.substring(
                            layout.getLineStart(0), layout.getLineEnd(0));
                    Path probe = new Path();
                    g.getTextPath(line0, 0, line0.length(),
                            layout.getLineLeft(0),
                            layout.getLineBaseline(0), probe);
                    RectF pb = new RectF();
                    probe.computeBounds(pb, true);
                    float avail = (float) w - tv.getTotalPaddingLeft()
                            - tv.getTotalPaddingRight();
                    // 只看右侧是否越界；墨迹从左边缘开始算宽度
                    float inkRight = pb.right;
                    float inkLeft = pb.left;
                    float inkWidth = inkRight - inkLeft;
                    if (inkWidth > 0f && avail > 0f && inkRight > avail) {
                        float sx = avail / inkRight;
                        // ★ 安全钳位：压缩幅度绝不超过 8%。
                        //
                        // 【为什么需要】2026-11-26 20:10 主人实测上一版压到了 3/4，
                        //   说明"测量方式一旦出错就会剧烈压缩"。
                        //   而这个功能的初衷只是【消除边缘那一点点裁剪】，
                        //   正常溢出量 < 3%。加钳位后即使测量再次出偏差，
                        //   最坏情况也只是压 8%，肉眼几乎无感，不会毁掉版式。
                        //   需要更大幅度时显式调 clock_p1_fit_max（默认 0.92）。
                        float minSx = propFloat(
                                "debug.hyperclassic.local.clock_p1_fit_max", 0.92f);
                        if (sx < minSx) sx = minSx;
                        if (sx < 0.999f) {
                            canvas.scale(sx, 1f);
                            onceLog("fit", new Throwable("ink=" + inkLeft
                                    + ".." + inkRight + " avail=" + avail
                                    + " sx=" + sx));
                        }
                    }
                } catch (Throwable ignored) { }
            }


            for (int i = 0; i < lines; i++) {
                int ls = layout.getLineStart(i);
                int le = layout.getLineEnd(i);
                if (ls < 0 || le > flat.length() || ls >= le) continue;

                float baseline = layout.getLineBaseline(i);

                if (chunk == 0) {
                    // 对照组：整行一次
                    linePath.rewind();
                    linePath.setFillType(Path.FillType.WINDING);
                    g.getTextPath(flat, ls, le, layout.getLineLeft(i), baseline, linePath);
                    if (pathSimplify(linePath, g) && !linePath.isEmpty()) {
                        // ★ 顺序：grow → flatten → 单次绘制
                        //   grow 用 morphGrow（UNION 形态学膨胀），
                        //   不用 pathGrow（getFillPath 会自相交镂空）
                        Path p = morphGrow(linePath, grow, morphDirs);
                        if (flatStep > 0) p = flattenPath(p, flatStep);
                        drawGlass(canvas, p, g, stampR, stampN);
                        drawn++;
                    }
                } else {
                    // 主路径：逐字符
                    for (int c = ls; c < le; c++) {
                        char ch = flat.charAt(c);
                        if (ch == ' ' || ch == '\u00A0' || ch == '\t') continue;

                        linePath.rewind();
                        linePath.setFillType(Path.FillType.WINDING);
                        g.getTextPath(flat, c, c + 1,
                                layout.getPrimaryHorizontal(c), baseline, linePath);
                        if (!pathSimplify(linePath, g) || linePath.isEmpty()) continue;

                        // ★★★ 逐字符同步 SDF 尺寸 / clip（2026-11-26 关键修复）★★★
                        //
                        // 【根因推导 · libhwui.so shader 实证】
                        //   我们用的这套 SDF glass shader（B 套）里：
                        //     half4 nmlData = uSdfTex.eval(cornerPos);
                        //     vec2 cornerPos = min(localPx, uGeoPxSize.xy - localPx);
                        //   采样时【没有任何缩放】—— 直接把 px 当 texel 索引。
                        //
                        //   对比 A 套（@89625）：
                        //     uSdfTex.eval(cornerPos * uRefractBlurScale_invEdgePow.zw)
                        //   它显式乘了 sdfScaleX/Y。
                        //
                        //   ⇒ B 套要求 uSdfTex 的尺寸与 Path 的像素尺寸严格 1:1。
                        //
                        //   而我们之前把 setMiGlassSdfMaxSize 设成【整个 View】
                        //   （360x199），实际每个字符 Path 只有 ~40x60，
                        //   于是采样坐标整体偏出纹理范围：
                        //     · 直线段恰好落在折叠轴附近 → 侥幸保留
                        //     · 曲线段的法线采样全错 → 整块丢弃
                        //   这正是「直线在、曲线消失」的成因。
                        //
                        //   修法：每画一个字符，就用【该字符自己的 bounds】
                        //   重设 SDF 尺寸与 clip，保证 1:1 对应。
                        if (perCharSdf) {
                            RectF pb = new RectF();
                            linePath.computeBounds(pb, true);
                            float padL = tv.getTotalPaddingLeft() - tv.getScrollX();
                            float padT = tv.getTotalPaddingTop() - tv.getScrollY();
                            float m = perCharMargin;

                            if (M_SET_SDF_MAX != null) {
                                try {
                                    M_SET_SDF_MAX.invoke(tv,
                                            Math.max(1f, pb.width() + 2 * m),
                                            Math.max(1f, pb.height() + 2 * m));
                                } catch (Throwable t) {
                                    onceLog("sdfMaxChar", t);
                                }
                            }
                            if (M_SET_GLASS_CLIP != null) {
                                try {
                                    M_SET_GLASS_CLIP.invoke(tv,
                                            pb.left + padL - m, pb.top + padT - m,
                                            pb.right + padL + m, pb.bottom + padT + m);
                                } catch (Throwable t) {
                                    onceLog("clipChar", t);
                                }
                            }
                        }
// ★ 顺序：grow → flatten → 单次绘制
                        Path dp = morphGrow(linePath, grow, morphDirs);
                        if (flatStep > 0) dp = flattenPath(dp, flatStep);
                        drawGlass(canvas, dp, g, stampR, stampN);
                        drawn++;
                    }
                }
            }

            if (drawn > 0) {
                hits.incrementAndGet();
                if (hits.get() <= 3) {
                    L.always(TAG, "draw #" + hits.get()
                            + " view=" + w + "x" + h
                            + " chunk=" + chunk
                            + " pieces=" + drawn
                            + " mode=" + mode);
                }
                return true;
            }
        } finally {
            canvas.restoreToCount(save);
        }
        return false;
    }

    /**
     * 加载真正的可变字体实例（绕过主题替换）。
     *
     * 【2026-11-26 实证】
     *   /system/fonts/MiSansVF_Overlay.ttf 是软链接：
     *     → /data/system/fonts/theme_webview/Roboto-Regular.ttf
     *     → /data/system/theme/fonts/Roboto-Regular.ttf
     *   即系统 sans-serif 的 MiSansVF 槽位被主题字体替换成了静态 Roboto，
     *   它没有 fvar/wght 轴 —— 这就是 Typeface.create(weight) 与
     *   setFontVariationSettings 都返回 true 却毫无视觉变化的根因。
     *
     * 这里直接打开真实可变字体文件，用 Typeface.Builder 生成带轴值的实例：
     *   /product/fonts/MiSansLatinVF.ttf  483KB  数字与拉丁（时钟只用数字）
     *   /product/fonts/MiSansVF.ttf        20MB  全字符集（兜底）
     *
     * @param axis 直接给 fvar wght 轴值（不是 Typeface weight）。
     *             经验区间：330~450 常规，600~700 粗，750+ 特粗。
     */
    private static Typeface vfTypeface(int axis) {
        if (VF_CACHE_AXIS == axis && VF_CACHE != null) return VF_CACHE;
        String[] paths = new String[]{
                "/product/fonts/MiSansLatinVF.ttf",
                "/product/fonts/MiSansVF.ttf",
                "/system/fonts/MiSansVF.ttf"
        };
        for (String path : paths) {
            try {
                java.io.File f = new java.io.File(path);
                if (!f.canRead() || f.length() <= 0) continue;
                Typeface tf = new Typeface.Builder(f)
                        .setFontVariationSettings("'wght' " + axis)
                        .build();
                if (tf != null) {
                    VF_CACHE = tf;
                    VF_CACHE_AXIS = axis;
                    onceLog("vfOK" + axis, new Throwable("path=" + path));
                    return tf;
                }
            } catch (Throwable t) {
                onceLog("vfFail" + path, t);
            }
        }
        return null;
    }

    private static Typeface VF_CACHE;
    private static int VF_CACHE_AXIS = Integer.MIN_VALUE;

    /**
     * 把含曲线的 Path 离散化（曲线 → 折线）。
     *   实测「只画直线，所有曲线整段消失」：
     *     18:31 → 显示「1 7 1」（1 完整、8 全无、3 只剩直线上半）
     *     13:29 → 显示「7」样（3 的上半直线）
     *   且换 SDF 尺寸（200x400 / 334x121 / 512x512）结果完全一致，
     *   说明与纹理分辨率无关。
     *
     *   libmisvg 里有两条线索指向本函数：
     *     · FontSdfGLSvg_fillPathData_inOP   （SVG path 数据解析入口）
     *     · SVG_DEBUG Draw can not draw misvg
     *   SDF 文本通道可能只处理折线（L/M/Z）指令，
     *   遇到 cubic/quad 曲线（C/Q）时整段跳过。
     *   Skia glyph 轮廓几乎全是 C/Q 指令 → 曲线全丢；
     *   而「1」这类纯直线字形 → 完全正常。
     *
     * 【实现】Skia 的 PathMeasure 只能采样点集，不产生线段。
     *   这里改用 DashPathEffect 反向技巧：
     *   用极短 on/off 的虚线把 Path 切割成大量小段，
     *   getFillPath 输出后这些曲线段会被近似成折线。
     *   更稳妥的做法是直接采样 PathMeasure 取多边形近似。
     *
     * 由 clock_p1_flatten=1 开启（默认关，先用 0 保持对照）。
     */
    private static Path flattenPath(Path src, float step) {
        if (step <= 0f) return src;
        try {
            PathMeasure pm = new PathMeasure(src, false);
            Path out = new Path();
            // ★ 填充规则：跟随源 Path（2026-11-26 修正）
            //
            // 【曾经的错误】之前硬编码 EVEN_ODD，理由是「PathMeasure 采样
            //   只保留点序、不保证轮廓方向，WINDING 下内洞挖不出来」。
            //   这个顾虑对「靠轮廓方向区分内外洞」成立，但代价太大：
            //
            //   可变字体在加粗时（wght 轴上调）会【故意让笔画轮廓重叠】，
            //   并依赖 NON_ZERO(WINDING) 把重叠区合并成一个整体。
            //   换成 EVEN_ODD 后，重叠区被算作「偶次穿越 = 外部」，
            //   于是 9、4 这类多笔画交界处出现【空白缺口】——
            //   正是主人截图里看到的现象。
            //
            // 【正解】沿用源 Path 的填充规则：
            //   · getTextPath 输出的字形是 WINDING（外轮廓顺时针、内洞逆时针）
            //   · 我们的采样按同一方向遍历每个 contour，方向信息其实保留了
            //     （PathMeasure 对每条 contour 都是从起点顺序走一圈，
            //       不反转方向，所以 WINDING 依然正确）
            //   · 开关 clock_p1_flatten_rule=0 强制 EVEN_ODD 做对照
            int rule = propInt("debug.hyperclassic.local.clock_p1_flatten_rule", 1);
            out.setFillType(rule == 0
                    ? Path.FillType.EVEN_ODD
                    : Path.FillType.WINDING);

            float[] pos = new float[2];
            float len = pm.getLength();
            if (len <= 0f) return src;

            int steps = Math.max(8, (int) (len / step));
            if (steps > 512) steps = 512;

            boolean started = false;
            for (int i = 0; i <= steps; i++) {
                float d = len * i / steps;
                if (!pm.getPosTan(d, pos, null)) break;
                if (!started) {
                    out.moveTo(pos[0], pos[1]);
                    started = true;
                } else {
                    out.lineTo(pos[0], pos[1]);
                }
            }
            // 处理多轮廓（如「8」的上下两个环、「0」的内外环）
            while (pm.nextContour()) {
                float l2 = pm.getLength();
                if (l2 <= 0f) continue;
                int s2 = Math.max(8, (int) (l2 / step));
                if (s2 > 512) s2 = 512;
                boolean st = false;
                for (int i = 0; i <= s2; i++) {
                    float d = l2 * i / s2;
                    if (!pm.getPosTan(d, pos, null)) break;
                    if (!st) {
                        out.moveTo(pos[0], pos[1]);
                        st = true;
                    } else {
                        out.lineTo(pos[0], pos[1]);
                    }
                }
            }
            out.close();
            return out.isEmpty() ? src : out;
        } catch (Throwable t) {
            onceLog("flatten", t);
            return src;
        }
    }

    /**
     * 形态学膨胀加粗（Path.op UNION 版）—— 当前主用方案。
     *
     * 【为什么放弃 getFillPath(FILL_AND_STROKE)】
     *   实测 grow 越大笔画反而越细。原因是 getFillPath 输出的是「描边轮廓」，
     *   strokeWidth 很大时轮廓大量自相交；在 WINDING 下环绕数互相抵消，
     *   部分区域被判为外部 → 镂空 / 变细。这个行为不可控。
     *
     * 【为什么放弃 canvas 多次 drawPath（stamping）】
     *   几何上能加粗，但玻璃是半透明混合，
     *   9 次叠加让边缘高光累积 9 次 → 过曝刺眼。
     *
     * 【本方案】形态学膨胀 = 原 Path ∪ 沿圆环平移的所有副本。
     *   · UNION 运算内部处理重叠，不依赖填充规则 → 粗细线性可控
     *   · 最终只 canvas.drawPath 一次 → 高光不叠加
     *
     * 参数：radius=外扩像素，dirs=方向数（8 已足够平滑，16 更圆滑但更慢）
     */
    private static Path morphGrow(Path src, float radius, int dirs) {
        if (radius <= 0f || dirs < 2) return src;
        try {
            Path acc = new Path(src);
            acc.setFillType(Path.FillType.WINDING);

            Path shifted = new Path();
            Path tmp = new Path();

            for (int i = 0; i < dirs; i++) {
                double a = 2.0 * Math.PI * i / dirs;
                float dx = (float) (Math.cos(a) * radius);
                float dy = (float) (Math.sin(a) * radius);

                shifted.rewind();
                shifted.setFillType(Path.FillType.WINDING);
                shifted.addPath(src, dx, dy);

                tmp.rewind();
                tmp.setFillType(Path.FillType.WINDING);
                boolean ok = false;
                try {
                    ok = acc.op(shifted, tmp, Path.Op.UNION);
                } catch (Throwable ignored) { }

                if (ok && !tmp.isEmpty()) {
                    acc.rewind();
                    acc.setFillType(Path.FillType.WINDING);
                    acc.addPath(tmp);
                } else {
                    // UNION 不可用（极复杂路径可能返回 false）→ 退化为直接叠加
                    acc.addPath(shifted);
                }
            }

            if (acc.isEmpty()) return src;
            acc.setFillType(Path.FillType.WINDING);
            return acc;
        } catch (Throwable t) {
            onceLog("morph", t);
            return src;
        }
    }

    /**
     * 统一的玻璃绘制出口（保留 stamping 供对照，默认走单次绘制）。
     */
    private static void drawGlass(Canvas canvas, Path p, Paint g,
                                  float stampR, int stampN) {
        if (stampR <= 0f || stampN < 2) {
            canvas.drawPath(p, g);
            return;
        }
        // 中心先画一次，保证核心区域 alpha 足够
        canvas.drawPath(p, g);
        for (int i = 0; i < stampN; i++) {
            double a = 2.0 * Math.PI * i / stampN;
            float dx = (float) (Math.cos(a) * stampR);
            float dy = (float) (Math.sin(a) * stampR);
            int sc = canvas.save();
            canvas.translate(dx, dy);
            canvas.drawPath(p, g);
            canvas.restoreToCount(sc);
        }
    }

    /** simplifyPath 可选（clock_p1_simp=0 关闭）。返回 true 表示可绘制。 */
    private static boolean pathSimplify(Path p, Paint paint) {
        if (propInt("debug.hyperclassic.local.clock_p1_simp", 1) == 0) return true;
        if (M_SIMPLIFY_PATH == null) return true;
        try {
            M_SIMPLIFY_PATH.invoke(null, p, paint);
            return true;
        } catch (Throwable t) {
            onceLog("simplify", t);
            return true;
        }
    }

    /**
     * 对字形 Path 做描边膨胀（加粗）。
     *
     * 【思路】Paint.getFillPath(src, dst) 在 FILL_AND_STROKE 模式下会把
     *   描边轮廓 + 填充区域一起输出到 dst，等效于把字形按 strokeWidth/2 向外扩。
     *
     * 【2026-11-26 重要修正】必须用 EVEN_ODD：
     *   膨胀后的 Path 是「原轮廓 + 一圈描边带」的叠加，
     *   在 WINDING 规则下描边带与填充区可能反向抵消，
     *   表现为 grow 越大字形反而越细甚至被挖空。
     *   EVEN_ODD 只看穿越次数，不受方向影响。
     *
     * growPaint == null 时原样返回 src（零开销）。
     */
    private static Path pathGrow(Path src, Paint growPaint, Path grownPath) {
        if (growPaint == null || grownPath == null) return src;
        try {
            grownPath.rewind();
            // ★ grow 阶段必须用 WINDING（非零环绕），不能用 EVEN_ODD
            //
            // 【原因】FILL_AND_STROKE 的 getFillPath 输出是
            //   「原字形填充区」 ∪ 「一圈描边带」。
            //   形状内部会被轮廓穿越 2 次（原轮廓 + 描边内缘）。
            //   · WINDING  : 环绕数 2 ≠ 0 → 判定为内部 ✅ 得到正确的并集
            //   · EVEN_ODD : 穿越 2 次是偶数 → 判定为外部 ❌ 中心被挖成空洞
            //   用 EVEN_ODD 就会出现「grow 越大字反而越细甚至镂空」的怪象。
            grownPath.setFillType(Path.FillType.WINDING);
            growPaint.getFillPath(src, grownPath);
            if (grownPath.isEmpty()) return src;
            grownPath.setFillType(Path.FillType.WINDING);
            return grownPath;
        } catch (Throwable t) {
            onceLog("grow", t);
            return src;
        }
    }

    private static final java.util.concurrent.atomic.AtomicInteger hits =
            new java.util.concurrent.atomic.AtomicInteger();
    private static final java.util.Set<String> LOGGED =
            java.util.Collections.synchronizedSet(new java.util.HashSet<String>());

    private static void onceLog(String what, Throwable t) {
        if (LOGGED.add(what)) L.always(TAG, what + " ERR: " + t);
    }

    /** 取第 i 行的文本（跨 API 兼容写法）。 */
    private static CharSequence lineText(Layout layout, CharSequence all, int line) {
        try {
            int start = layout.getLineStart(line);
            int end = layout.getLineEnd(line);
            if (start < 0 || end > all.length() || start >= end) return null;
            return all.subSequence(start, end);
        } catch (Throwable t) {
            return null;
        }
    }

    // ==================================================================
    private static void resolve(ClassLoader cl) {
        if (RESOLVED) return;
        RESOLVED = true;
        StringBuilder sb = new StringBuilder();

        Class<?> paintCls = null;
        try {
            paintCls = Class.forName("android.graphics.Paint", false, cl);
        } catch (Throwable ignored) { }

        if (paintCls != null) {
            try {
                M_SET_GLASS_EFFECT = paintCls.getDeclaredMethod("setGlassEffect", boolean.class);
                sb.append("effect=T ");
            } catch (Throwable t) { sb.append("effect=F "); }
            try {
                M_SET_ATLAS_MAX_PATH = paintCls.getDeclaredMethod(
                        "setAtlasMaxPathSize", int.class, int.class);
                sb.append("atlas=T ");
            } catch (Throwable t) { sb.append("atlas=F "); }
        }

        Class<?> viewCls = null;
        try {
            viewCls = Class.forName("android.view.View", false, cl);
        } catch (Throwable ignored) { }

        if (viewCls != null) {
            try {
                M_SET_MAT_TYPE = viewCls.getDeclaredMethod("setMiViewMaterialType", int.class);
                sb.append("mat=T ");
            } catch (Throwable t) { sb.append("mat=F "); }
            try {
                M_SET_GLASS = viewCls.getDeclaredMethod("setMiGlass", float[].class);
                sb.append("glass=T ");
            } catch (Throwable t) { sb.append("glass=F "); }
            try {
                M_SET_GLASS_CLIP = viewCls.getDeclaredMethod("setMiGlassClip",
                        float.class, float.class, float.class, float.class);
                sb.append("clip=T ");
            } catch (Throwable t) { sb.append("clip=F "); }
            try {
                M_SET_SDF_MAX = viewCls.getDeclaredMethod("setMiGlassSdfMaxSize",
                        float.class, float.class);
                sb.append("sdf=T ");
            } catch (Throwable t) { sb.append("sdf=F "); }
            try {
                M_SET_BLUR_MODE = viewCls.getDeclaredMethod("setMiViewBlurMode", int.class);
                sb.append("blurMode=T ");
            } catch (Throwable t) { sb.append("blurMode=F "); }
            try {
                M_SET_GLASS_BLUR_R = viewCls.getDeclaredMethod("setMiGlassBlurRadius",
                        int.class, int.class);
                sb.append("blurR=T ");
            } catch (Throwable t) { sb.append("blurR=F "); }
            try {
                M_SET_SURF_COLOR = viewCls.getDeclaredMethod(
                        "setMiCustomSurfaceColorType", int.class);
                sb.append("surf=T ");
            } catch (Throwable t) { sb.append("surf=F "); }
        }

        try {
            Class<?> blur = Class.forName("com.miui.clock.utils.MiuiBlurUtils", false, cl);
            for (Method m : blur.getDeclaredMethods()) {
                if ("simplifyPath".equals(m.getName()) && m.getParameterCount() == 2) {
                    M_SIMPLIFY_PATH = m;
                    break;
                }
            }
            sb.append("simplify=").append(M_SIMPLIFY_PATH != null);
        } catch (Throwable t) {
            sb.append("simplify=CLFAIL");
        }

        API_LOG = sb.toString().trim();
    }

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

    static String propStr(String key, String def) {
        try {
            Class<?> sp = Class.forName("android.os.SystemProperties");
            Method g = sp.getMethod("get", String.class);
            Object v = g.invoke(null, key);
            if (v instanceof String) {
                String s = (String) v;
                return s.length() > 0 ? s : def;
            }
        } catch (Throwable ignored) { }
        return def;
    }

    static float propFloat(String key, float def) {
        try {
            Class<?> sp = Class.forName("android.os.SystemProperties");
            Method g = sp.getMethod("getFloat", String.class, float.class);
            Object v = g.invoke(null, key, def);
            return v instanceof Float ? (Float) v : def;
        } catch (Throwable t) {
            // getFloat 在老版本可能不存在，退回 String 解析
            try {
                Class<?> sp = Class.forName("android.os.SystemProperties");
                Method g = sp.getMethod("get", String.class);
                Object v = g.invoke(null, key);
                if (v instanceof String && ((String) v).length() > 0) {
                    return Float.parseFloat((String) v);
                }
            } catch (Throwable ignored) { }
            return def;
        }
    }
}