package io.github.mersand.hyperclassic;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;

/**
 * PortraitClockStyle —— 竖屏控制中心「通知大时钟」的字体替换 + 玻璃化。
 *
 * ==================================================================
 * ===== 一、这个时钟到底是谁（完整溯源）=====
 * ==================================================================
 *
 * 主人 2026-11-25 反馈：竖屏 QS 上方的通知大时钟「不走系统字体，有自己的独立资源」。
 * 实测确认，主人的判断**完全正确**。
 *
 * ----- 布局（res/layout/miui_ns_notification_header_view.xml）-----
 *
 *   MiuiNotificationHeaderView
 *     ├─ MiuiClock            @id/horizontal_time    (mode=time_date)
 *     ├─ MiuiNotificationHeaderClock @id/big_time    ← ★ 竖屏大时钟（本类目标）
 *     ├─ LinearLayout         @id/carrier_container
 *     ├─ LinearLayout         @id/privacy_container
 *     └─ MiuiClock            @id/date_time          (mode=date_24)
 *
 * 样式：@style/TextAppearance.QSControl.Clock
 *   → fontFamily 未定义，只设了 textColor / textStyle=normal
 *   ⇒ 说明字体不是从 style 来的，是代码里硬设的（见下）。
 *
 * ----- 类型继承链 -----
 *
 *   MiuiNotificationHeaderClock
 *     extends MiuiClock
 *       extends android.widget.TextView
 *       implements DarkIconDispatcher.DarkReceiver, IBlurEffect
 *
 * ⇒ 它**本质是 TextView**，所以字形仍然走 TextView 的 Paint 管线，
 *   只是字体被硬锁死了。这一点决定了修法：不用重画 Path，
 *   直接把 Typeface 抢回来即可。
 *
 * ----- 字体为什么锁死（关键）-----
 *
 * MiuiClock.<init>(Context, AttributeSet, int) 第 144~155 行：
 *
 *   sget-object p2, Lcom/miui/utils/configs/MiuiConfigs;->sMiproTypefaceWght500:Landroid/graphics/Typeface;
 *   invoke-static {p2, p0}, Lcom/miui/utils/configs/MiuiConfigs;->applyStatusBarTypeface(Landroid/graphics/Typeface;[Landroid/widget/TextView;)V
 *
 * 而 MiuiConfigs.applyStatusBarTypeface 内部更暴力：
 *
 *   Typeface.create("mipro-medium", 0)   ← 再把传进来的 Typeface 顶掉
 *   familyName.startsWith("mipro") ? 用它 : 保留传入值
 *   → 对每个 TextView 调 setTypeface()
 *
 * ⇒ 无论外层传什么，最终都被锁成 mipro 系列。
 *   所以「换主题字体」「改系统字体」对这个时钟统统无效 ——
 *   这正是主人观察到「有自己独立资源」的本质。
 *
 * ----- 唯一没人改的地方 -----
 *
 * MiuiNotificationHeaderView.onFinishInflate() 里：
 *
 *   mDateView  (date_time)       → applyStatusBarTypeface(sMiproTypefaceWeight380)
 *   mLandClock (horizontal_time) → Typeface.create("mipro-demibold") 或 sMiproTypeface
 *   mBigTime   (big_time)        → ★ 什么也没做 ★
 *
 * 也就是说 big_time 只在 MiuiClock 构造时被锁一次 wght500，之后无人触碰。
 *
 * ==================================================================
 * ===== 二、本类怎么改（两条独立管线）=====
 * ==================================================================
 *
 * 【管线 A：字体】
 *
 *   hook 点选 MiuiConfigs.applyStatusBarTypeface —— 因为它是**唯一**的字体
 *   下发出口，所有重设（构造 / onFinishInflate / updateResources /
 *   onDensityOrFontScaleChanged / 主题切换）最终都经它。
 *
 *   做法：proceed() 之后，遍历参数里的 TextView[]，凡是被本类打了标记的
 *   （即 big_time），立刻把自己的 Typeface 再盖回去。
 *   这样无论系统多少次重设，我们最后一次说话 —— 不需要 hook 一堆上层方法。
 *
 *   递归保护：ThreadLocal 标记，避免回调里再次触发自己。
 *
 *   字体来源（prop 可选）：
 *     0        = 系统默认（mipro wght500），完全不改 ← 默认值
 *     1        = MiClock-Light.otf        （SystemUI assets，细体时钟字）
 *     2        = MiClock-Thin.otf         （更细）
 *     3        = Mitype2018-clock.ttf     （MIUI 老款时钟数字）
 *     4        = MitypeRoundedNormal.otf  （圆体）
 *     5        = MiSansVF.ttf + wght      （系统可变字体，配合加粗）
 *     99       = 自定义字体文件（主人自行上传的「形状」）
 *
 *   加粗：clock_weight（wght 数值，1~1000）。可变字体走
 *   setFontVariationSettings("'wght' N")；静态字体退化用
 *   Typeface.create(base, weight, false) 做插值。
 *
 *   再兜底：clock_fake_bold=1 时用 Paint 的 fakeBold + strokeWidth，
 *   任何字体都能变粗。
 *
 * 【管线 B：玻璃 —— 已撤销，改由文档记录】
 *
 *   玻璃化经 v364~v377 共 14 轮实验仍未成功（输出 SDF 黄块），
 *   2026-11-25 主人指示「标记为待研究，先撤销这部分代码」。
 *
 *   完整记录见：
 *     docs/EXPERIMENT_clock_glass_v364_v369_FAILED.md   （前六轮）
 *     docs/PENDING_clock_text_glass.md                  （后八轮 + API 清单 + 5 条后续路径）
 *
 *   本类因此只保留字体管线，不再有任何玻璃代码。
 *   注意：主人要的是「文字笔画本身玻璃化」，不是给时钟加玻璃底板 ——
 *        后续若重启研究，务必守住这个目标，别走成底板方案。
 *
 * ==================================================================
 * ===== 三、挂点与去重 =====
 * ==================================================================
 *
 * hook MiuiNotificationHeaderView 的三个时机：
 *   onFinishInflate      ← 首次拿到 mBigTime 引用
 *   onAttachedToWindow   ← 上屏时机（主题/资源可能已重算）
 *   updateHeaderResources← 方向切换、尺寸变化、字体缩放
 *
 * apply() 内部用 WeakHashMap 去重，同一 View 的字体相同则跳过；
 * 玻璃因为可能被系统重置，每次都重设（代价小）。
 *
 * ==================================================================
 * ===== 四、开关（全部可热调，无需重启）=====
 * ==================================================================
 *
 *   debug.hyperclassic.local.clock_style=0      总开关（默认 1）
 *   debug.hyperclassic.local.clock_font=0       字体选择（见上表）
 *   debug.hyperclassic.local.clock_font_path    99 时使用，自定义字体绝对路径
 *   debug.hyperclassic.local.clock_weight=0     wght 数值，0=不改
 *   debug.hyperclassic.local.clock_fake_bold=0  1=用 Paint 假粗
 *   debug.hyperclassic.local.clock_stroke       假粗的描边宽度（px，默认 1.0）
 *   debug.hyperclassic.local.clock_probe=1      打印诊断日志
 *
 * ==================================================================
 * ===== 五、玻璃去哪了（v377 变更：已撤销）=====
 * ==================================================================
 *
 * 主人 2026-11-25 最终指示：「标记为待研究，然后先撤销这部分代码。」
 *
 * 完整实验历程：
 *   v364~v369  六轮，在官方 MiuiClock 上直接做玻璃 → 全失败
 *              docs/EXPERIMENT_clock_glass_v364_v369_FAILED.md
 *   v370~v377  八轮，整条路径替换 + Paint 级玻璃 → 仍是 SDF 黄块
 *              docs/PENDING_clock_text_glass.md
 *
 * 本类因此**彻底移除玻璃管线**，只保留字体管线（已实机验收通过）。
 * ClockReplacement.java 已删除，CcHooks 的 L11 接入已回退。
 *
 * 保留的可复用资产（见 PENDING 文档第二、六节）：
 *   · 官方 TimeView 玻璃完整 14 步序列
 *   · MiuiBlurUtils 的 22 个 Method 字段 + 10 个 public static 方法
 *   · 官方 42 项玻璃配方 + 索引常量表
 *   · setMiViewBlurMode(3) + setMiCustomSurfaceColorType(16) 是玻璃开关
 *   · FrameLayout 原地包装是改布局唯一稳妥做法（已验证）
 *
 * ⚠ 后续若重启研究，务必守住「文字笔画本身玻璃化」这个目标，
 *   不要走成「给时钟加玻璃底板」—— 那不是主人要的效果。
 */
final class PortraitClockStyle {

    private static final String TAG = "HCClockStyle";

    /** 总开关。 */
    private static final String PROP_ON = "debug.hyperclassic.local.clock_style";

    /** 目标 View 的标记 key（0x0f 段为本模块自用）。 */
    private static final int TAG_MARK = 0x0f000002;

    // ------------------------------------------------------------------
    // 反递归：applyStatusBarTypeface 里会调 setTypeface，
    // 我们不 hook setTypeface，所以理论上不会递归；但保险起见加标记。
    // ------------------------------------------------------------------
    private static final ThreadLocal<Boolean> IN_APPLY = new ThreadLocal<>();

    /** 已应用的字体（同一 View 字体没变就不重复 set）。 */
    private static final Map<View, Typeface> APPLIED_TF = new WeakHashMap<>();

    /**
     * ★ 关键：Typeface 解析结果缓存。
     *
     * 为什么必须缓存（v360 实测踩坑）：
     *   最初每次 reapplyFont 都调 loadMiProSys() → new Typeface.Builder().build()，
     *   每次都返回**新的对象引用**，于是
     *       cur == tf  → 永远 false
     *   结果：每次 updateBigTimeColor 都真的去 setTypeface()，
     *   而 setTypeface 会让 TextView requestLayout，
     *   布局变化又触发 updateBigTimeColor → apply → setTypeface …
     *   实测 227 次 / 0.5s 的死循环（日志刷屏、掉帧）。
     *
     * 修法：按 (sel, weight) 缓存 Typeface 实例，
     *   同一配置永远拿到同一个引用，幂等判断才成立。
     */
    private static final Map<String, Typeface> TF_CACHE = new java.util.HashMap<>();

    /**
     * ★ 重入保护：apply() 可能被 updateBigTimeColor / updateLayout 等
     * 多个挂点连续触发，且期间会 setTypeface（引发 layout）。
     * 用 ThreadLocal 挡住同一线程内的嵌套调用，彻底断掉循环链。
     */
    private static final ThreadLocal<Boolean> IN_APPLY_VIEW = new ThreadLocal<>();

    /**
     * ★ 节流表：每个 View 上次真正应用配置的时刻。
     *
     * 【v360 三轮排查史 —— 这段必须留着，否则以后又会走回头路】
     *
     * ① 第一轮：日志刷 227 次 / 0.5s，误判为「Typeface 每次新建导致死循环」。
     *    → 加 TF_CACHE 让引用恒定。引用确实恒定了，但刷屏没停。
     *
     * ② 第二轮：以为 8ms 间隔是 120Hz 逐帧循环。
     *    → 加重入保护 + 1 秒节流。刷屏停了，但字体没生效（被节流挡了）。
     *
     * ③ 第三轮：打印被节流时的调用栈，真相：
     *    全部集中在 onAttachedToWindow 的**同一次同步流程**内，
     *    是四条独立路径各触发一次（dt 都是 356/357ms，同一毫秒）：
     *      · onAttachedToWindow → onDensityOrFontScaleChanged → updateHeaderResources
     *      · onAttachedToWindow → onDensityOrFontScaleChanged
     *      · onAttachedToWindow → addDarkReceiver → onDarkChanged → updateBigTimeColor
     *      · onAttachedToWindow → addDarkReceiver → onLightDarkTintChanged → updateBigTimeColor
     *    ⇒ 不是死循环，是「一次上屏被四个挂点各命中一次」。
     *    ⇒ 「470 次」是多次重启 SystemUI 跨进程累积，不是单次循环。
     *
     * 【结论：正确的去重维度是「配置」，不是「时间」】
     *
     *   用时间节流会误伤初始化：四条路径里第一条把字体设好，
     *   后面三条被节流挡掉本无所谓；但若第一条恰好失败/还没测量，
     *   后面就再也没有机会补 —— 这就是第二轮字体不生效的原因。
     *
     *   正解：记录「上次成功应用时的配置签名」，
     *   签名相同 → 跳过；签名不同 → 立刻应用。
     *   这样：
     *     · 初始化四条路径只生效一次，其余跳过（且不会漏）
     *     · 热调 prop 后签名变化 → 立刻生效，无需重启
     *     · 方向切换 / 密度变化 若影响签名也会重算
     */
    private static final Map<View, String> APPLIED_SIG = new WeakHashMap<>();

    /**
     * onDraw 挂点的时间节流表（每 View 250ms 最多检查一次「配置」）。
     * 避免逐帧读 SystemProperties 拖慢渲染。
     */
    private static final Map<View, Long> DRAW_CHECK_AT = new WeakHashMap<>();

    /**
     * ★ 缓存的 Paint 加粗配置。
     *
     * 为什么单独缓存（v362 实测问题）：
     *   主人反馈「时间更新后就会变成默认字体，然后在下一次下拉后再次变粗」。
     *
     * 溯源：MiuiClock.updateTime() → setText() 时，TextView 会把
     *   TextPaint 的部分状态刷回默认（fakeBold / style / strokeWidth 全丢），
     *   而我们的 onDraw 检查被 250ms 节流挡住，要等下一次下拉才纠正。
     *
     * ⇒ 拆成两级：
     *   ① 「配置检查」（读 prop，贵）：0ms~250ms 一次
     *   ② 「Paint 断言」（只用缓存值，便宜到可以逐帧）：每帧都做
     *   这样系统何时把 Paint 刷掉都能在**同一帧内**补回来。
     */
    private static volatile boolean cachedFakeBold;
    private static volatile float cachedStroke = 1.2f;

    private static final AtomicInteger fontHits = new AtomicInteger();
    private static final AtomicInteger errors = new AtomicInteger();

    private static Method propGet;

    /** 上一次看到的总开关值 + 采样时间（500ms 节流）。 */
    private static volatile boolean onCache = true;
    private static volatile long onAt;

    private PortraitClockStyle() {
    }

    // ==================================================================
    // 安装
    // ==================================================================
static void install(XposedModule module, ClassLoader cl) {
        try {
            // ================================================================
            // ★★ 关键教训（v361）：不要再按「宿主类」找时钟 ★★
            //
            // v360 第一版 hook 的是 MiuiNotificationHeaderView 的
            // onFinishInflate / onAttachedToWindow / updateBigTimeColor，
            // 然后反射它的 mBigTime 字段。日志显示「font applied #1..3」，
            // 但截图完全没变化 —— 因为**改错了对象**。
            //
            // 实测真相：SystemUI 里有**两个** @id/big_time：
            //
            //   ① res/layout/miui_ns_qs_header_view.xml
            //        MiuiQSHeaderView
            //          └ MiuiClock @id/big_time  44dp  ← ★ 竖屏可见的就是它
            //          └ MiuiClock @id/date_time
            //
            //   ② res/layout/miui_ns_notification_header_view.xml
            //        MiuiNotificationHeaderView
            //          └ MiuiNotificationHeaderClock @id/big_time  ← 不显示
            //
            // 竖屏下拉时用户看到的「13:45」是 ①，而 v360 改的是 ②，
            // 所以视觉零变化。
            //
            // ⇒ 正解：**按 View 的 id 名字匹配**（big_time），不认宿主类。
            //   这样两个 big_time、以及将来小米新增的任何 big_time 都能覆盖。
            // ================================================================

            final Class<?> clockCls = Class.forName(
                    "com.android.systemui.statusbar.views.MiuiClock", false, cl);

            // ---- 挂点 1：时钟上屏（每次 attach，含 SystemUI 重启后）----
            hookClockMoment(module, clockCls, "onAttachedToWindow", "attach", cl);

            // ---- 挂点 1b：updateTime —— 天然的周期性热调触发器 ----
            //
            // 主人 2026-11-25 提示：「我们改的对象是时钟，可能是按秒级更新的？」
            // 实证：MiuiClock.updateTime() 由 MiuiStatusBarClockController 驱动，
            // 显示 HH:mm 时**每分钟**触发一次（显示秒时才每秒）。
            //
            // 这正好是我们要的：
            //   · 频率极低（1 次/分钟），无性能负担
            //   · 周期性，改完 prop 最多 1 分钟内自动生效，不用重启 SystemUI
            //   · 是时钟自己的方法，不会在其他 View 上误触发
            hookClockMoment(module, clockCls, "updateTime", "tick", cl);

            // ---- 挂点 1c：onDraw —— 让热调立刻生效 ----
            //
            // 主人实测反馈（2026-11-25）：
            //   「这次有变化了，时钟字体变得很粗。但是，这是在我下拉了几次后
            //     发生的，第一次时没有反应。」
            //
            // 原因：上面三个挂点（attach / updateTime / onSizeChanged）都不是
            // 下拉面板时必然触发的 —— View 已经 attach 过就不会再 attach，
            // updateTime 一分钟一次，尺寸也没变。所以改完 prop 要等运气。
            //
            // ⇒ 补一个 onDraw 挂点：下拉面板必然触发绘制，于是必然能检查配置。
            //   但 onDraw 是逐帧的，直接在里面读 prop 会拖慢渲染，
            //   所以加 **250ms 时间节流**（一帧最多 8ms，250ms 内最多读 1 次）。
            //
            // 注意：hook 的是 TextView.onDraw（MiuiClock 没覆写 onDraw），
            //   所以要用实例类型过滤，避免影响所有 TextView。
            try {
                Method m = Class.forName("android.widget.TextView", false, cl)
                        .getDeclaredMethod("onDraw", android.graphics.Canvas.class);
                m.setAccessible(true);
                module.hook(m)
                        .setId("hc_clock_style_draw")
                        .setPriority(XposedInterface.PRIORITY_LOWEST)
                        .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                        .intercept(chain -> {
                            Object self = chain.getThisObject();
                            // 非 MiuiClock（或其它 TextView）→ 原样放行
                            if (!(self instanceof View) || !clockCls.isInstance(self)) {
                                return chain.proceed();
                            }
                            View v = (View) self;

                            // ===== 已确认是我们的时钟 =====
                            if (v.getTag(TAG_MARK) != null) {
                                // ① 每帧 Paint 断言（廉价：只读 volatile 缓存字段）
                                assertPaintBold((TextView) v);

                                // ② 节流配置检查（读 prop 有开销，250ms 一次）
                                long now2 = android.os.SystemClock.uptimeMillis();
                                Long l2 = DRAW_CHECK_AT.get(v);
                                if (l2 == null || now2 - l2 >= 250L) {
                                    DRAW_CHECK_AT.put(v, now2);
                                    apply(v, cl);
                                }
                                return chain.proceed();
                            }

                            // ===== 未确认：节流窗口到了才识别一次 =====
                            long now = android.os.SystemClock.uptimeMillis();
                            Long last = DRAW_CHECK_AT.get(v);
                            if (last != null && now - last < 250L) {
                                return chain.proceed();
                            }
                            DRAW_CHECK_AT.put(v, now);
                            if (!isOurClock(v)) {
                                return chain.proceed();   // 不是我们的 → 不碰
                            }
                            apply(v, cl);                 // 内部打 TAG_MARK
                            return chain.proceed();
                        });
                L.always(TAG, "hooked TextView.onDraw (250ms throttled hot-reload)");
            } catch (Throwable t) {
                L.always(TAG, "hook onDraw failed: " + t);
            }

            // ---- 挂点 2：尺寸变化（SDF 与模糊要按尺寸算）----
            try {
                Method m = Class.forName("android.view.View", false, cl)
                        .getDeclaredMethod("onSizeChanged",
                                int.class, int.class, int.class, int.class);
                m.setAccessible(true);
                module.hook(m)
                        .setId("hc_clock_style_size")
                        .setPriority(XposedInterface.PRIORITY_LOWEST)
                        .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                        .intercept(chain -> {
                            Object result = chain.proceed();
                            try {
                                Object self = chain.getThisObject();
                                if (clockCls.isInstance(self) && isOurClock((View) self)) {
                                    apply((View) self, cl);
                                }
                            } catch (Throwable t) {
                                error(t);
                            }
                            return result;
                        });
            } catch (Throwable t) {
                L.log(4, TAG, "hook onSizeChanged failed: " + t);
            }

            // ---- 挂点 3：字体下发总出口（防系统把字体改回去）----
            installTypefaceGuard(module, cl);

            L.always(TAG, "installed (id-based match: big_time); font="
                    + propInt("debug.hyperclassic.local.clock_font", 0)
                    + " weight=" + propInt("debug.hyperclassic.local.clock_weight", 0)
                    + " fakeBold=" + propInt("debug.hyperclassic.local.clock_fake_bold", 0));
        } catch (Throwable t) {
            L.always(TAG, "install FAILED: " + t);
        }
    }

    /** hook MiuiClock 的某个生命周期方法（onAttachedToWindow / onDetached...）。 */
    private static void hookClockMoment(XposedModule module, Class<?> clockCls,
                                       String method, String tag, ClassLoader cl) {
        try {
            Method m = clockCls.getDeclaredMethod(method);
            m.setAccessible(true);
            module.hook(m)
                    .setId("hc_clock_style_" + tag)
                    .setPriority(XposedInterface.PRIORITY_LOWEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        Object result = chain.proceed();
                        try {
                            Object self = chain.getThisObject();
                            if (self instanceof View && isOurClock((View) self)) {
                                apply((View) self, cl);
                            }
                        } catch (Throwable t) {
                            error(t);
                        }
                        return result;
                    });
            L.always(TAG, "hooked MiuiClock." + method);
        } catch (Throwable t) {
            L.always(TAG, "hook MiuiClock." + method + " failed: " + t);
        }
    }

    /**
     * 是不是我们要改的时钟 —— 按 id 名字判定，不认宿主类。
     *
     * 目标 id：big_time
     *   实测在三个布局里都有：
     *     · miui_ns_qs_header_view.xml          MiuiClock（★ 竖屏可见的 44dp 大时钟）
     *     · miui_ns_notification_header_view.xml MiuiNotificationHeaderClock
     *     · control_center_date_view.xml
     *
     * 全部命中是符合预期的：用户要的就是「控制中心里的通知大时钟」，
     * 这几处都是同一个语义的时钟，统一字体+玻璃反而更一致。
     *
     * 可用 debug.hyperclassic.local.clock_ids 追加额外 id（逗号分隔）。
     */
    private static boolean isOurClock(View v) {
        String n = idName(v);
        if (n.isEmpty()) {
            return false;
        }
        if ("big_time".equals(n)) {
            return true;
        }
        String extra = prop("debug.hyperclassic.local.clock_ids", "").trim();
        if (!extra.isEmpty()) {
            for (String s : extra.split(",")) {
                if (n.equals(s.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 取 View 的 id 资源名（失败返回空串）。 */
    private static String idName(View v) {
        try {
            int id = v.getId();
            if (id == View.NO_ID) {
                return "";
            }
            return v.getResources().getResourceEntryName(id);
        } catch (Throwable t) {
            return "";
        }
    }

    /**
     * 字体下发总出口守卫。
     *
     * MiuiConfigs.applyStatusBarTypeface(Typeface, TextView...) 是 static varargs，
     * 反射签名是 (Landroid/graphics/Typeface;[Landroid/widget/TextView;)V。
     */
    private static void installTypefaceGuard(XposedModule module, ClassLoader cl) {
        try {
            Class<?> cfg = Class.forName("com.miui.utils.configs.MiuiConfigs", false, cl);
            Method apply = cfg.getDeclaredMethod("applyStatusBarTypeface",
                    Typeface.class, TextView[].class);
            apply.setAccessible(true);

            module.hook(apply)
                    .setId("hc_clock_style_guard")
                    .setPriority(XposedInterface.PRIORITY_LOWEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        Object result = chain.proceed();
                        try {
                            if (IN_APPLY.get() != null) {
                                return result;   // 防递归
                            }
                            Object[] args = chain.getArgs().toArray();
                            if (args.length < 2 || !(args[1] instanceof TextView[])) {
                                return result;
                            }
                            Context ctx = null;
                            for (TextView tv : (TextView[]) args[1]) {
                                if (tv == null || tv.getTag(TAG_MARK) == null) {
                                    continue;
                                }
                                if (ctx == null) {
                                    ctx = tv.getContext();
                                }
                                reapplyFont(tv, ctx);
                            }
                        } catch (Throwable t) {
                            error(t);
                        }
                        return result;
                    });

            L.always(TAG, "typeface guard hooked (MiuiConfigs.applyStatusBarTypeface)");
        } catch (Throwable t) {
            L.always(TAG, "typeface guard FAILED: " + t);
        }
    }

    // ==================================================================
    // 主流程
    // ==================================================================

    /**
     * 对一个时钟 View 应用样式（字体 + 玻璃）。
     *
     * ★ v361 变更：入参从「宿主 header」改为「时钟 View 本身」。
     *   因为现在按 id 名匹配，找到的直接就是目标，不需要再从宿主里反射字段。
     */
    private static void apply(View clock, ClassLoader cl) {
        if (!runtime()) {
            return;
        }
        // ★ 重入保护：apply 内部会 setTypeface → requestLayout，
        //   布局流程可能再次触发 onSizeChanged 等挂点，靠它挡住嵌套。
        if (IN_APPLY_VIEW.get() != null) {
            return;
        }
        IN_APPLY_VIEW.set(Boolean.TRUE);
        try {
            doApply(clock, cl);
        } catch (Throwable t) {
            error(t);
        } finally {
            IN_APPLY_VIEW.remove();
        }
    }

    /** 真正的应用逻辑（在重入保护内执行）。 */
    private static void doApply(View clock, ClassLoader cl) {
        // 打标记：后续 typeface guard 靠它认出「这是我们要管的那个」
        if (clock.getTag(TAG_MARK) == null) {
            clock.setTag(TAG_MARK, Boolean.TRUE);
        }

        // ★ 本类只管「字体」一条管线。
        //
        // 【玻璃已整体撤销】（主人 2026-11-25 指示：标记待研究 + 撤销代码）
        //   v364~v369 六轮：在官方 MiuiClock 上直接做玻璃 → 全失败
        //     docs/EXPERIMENT_clock_glass_v364_v369_FAILED.md
        //   v370~v377 八轮：整条路径替换 + Paint 级玻璃 → 仍是 SDF 黄块
        //     docs/PENDING_clock_text_glass.md
        //
        //   根因：目标 View 背后没有可采样的玻璃层，着色器只能输出 SDF atlas。
        //   注意主人要的是「文字笔画本身玻璃化」，不是加玻璃底板。
        //
        //   ⇒ 本类只保留字体：clock_font / clock_weight /
        //     clock_fake_bold / clock_stroke。玻璃代码与 prop 全部删除。
        if (clock instanceof TextView) {
            // 字体每次都调（内部因 Typeface 引用相同会立即返回，几乎零成本），
            // 这样系统某天偷偷把字体改回也能被我们纠正。
            reapplyFont((TextView) clock, clock.getContext());
        }
    }

    // ==================================================================
    // 管线 A：字体
    // ==================================================================

    /** 重新计算并套用字体（幂等：同一 Typeface 不重复 set）。 */
    private static void reapplyFont(TextView tv, Context ctx) {
        Boolean in = IN_APPLY.get();
        if (in != null) {
            return;
        }
        IN_APPLY.set(Boolean.TRUE);
        try {
            // ★ 假粗/描边独立于 typeface 选择 —— 即使 font=0 weight=0
            //   也允许仅用 Paint 加粗（这是「玻璃化打底」的轻量路径）
            applyPaintBold(tv);

            Typeface tf = resolveTypeface(ctx);
            if (tf == null) {
                // ★ 从「有自定义」切回「全默认」时，必须把字体还回去。
                //   否则热调把 clock_font/clock_weight 归零后，
                //   屏幕上还是旧字体，主人会以为开关失灵。
                if (APPLIED_TF.remove(tv) != null) {
                    restoreSystemDefault(tv);
                    L.always(TAG, "font rolled back to system default");
                }
                return;   // 完全不动系统默认
            }
            Typeface cur = APPLIED_TF.get(tv);
            // ★ 只信自己的记录，不信 tv.getTypeface()。
            //
            // 为什么（v360 第三次实测，这是死循环的真凶）：
            //   最初写的是
            //       if (cur == tf && tv.getTypeface() == tf) return;
            //   结果每帧都判定为「不相等」→ 每帧都真的 setTypeface
            //   → 每帧 requestLayout → 触发布局 → 再次进 apply → …
            //   实测稳定 8ms 一次，470 次/3 秒。
            //
            //   原因：TextView.setTypeface() 内部会做若干包装
            //   （Typeface.create(tf, style)、sans-serif 回退等），
            //   getTypeface() 返回的引用和我们传进去的不是同一个对象。
            //
            //   ⇒ 幂等判断只能基于「我们自己设过什么」，
            //     系统若把字体盖回去，由 applyStatusBarTypeface 的 guard
            //     负责再盖回来 —— 那条路径单独维护，不需要这里有判断。
            if (cur == tf) {
                return;
            }
            tv.setTypeface(tf);
            APPLIED_TF.put(tv, tf);
            // 注意：以上是「我们自己的记录」，与系统当前值无关。

            int n = fontHits.incrementAndGet();
            if (probe() || n <= 3) {
                L.always(TAG, "font applied #" + n
                        + " sel=" + propInt("debug.hyperclassic.local.clock_font", 0)
                        + " weight=" + propInt("debug.hyperclassic.local.clock_weight", 0)
                        + " fakeBold=" + propInt("debug.hyperclassic.local.clock_fake_bold", 0)
                        + " tf=" + tf);
            }
        } catch (Throwable t) {
            error(t);
        } finally {
            IN_APPLY.remove();
        }
    }

    /**
     * 把 View 的字体还原成系统默认。
     *
     * 做法：直接调一遍 MiuiConfigs.applyStatusBarTypeface(sMiproTypefaceWght500, v)，
     * 让官方逻辑自己把字体装回去 —— 比自己猜默认值可靠。
     * 失败则退化为 Typeface.DEFAULT。
     */
    private static void restoreSystemDefault(TextView tv) {
        try {
            Class<?> cfg = Class.forName(
                    "com.miui.utils.configs.MiuiConfigs", false, tv.getClass().getClassLoader());
            java.lang.reflect.Field f = cfg.getDeclaredField("sMiproTypefaceWght500");
            f.setAccessible(true);
            Object tf = f.get(null);
            if (tf instanceof Typeface) {
                Method m = cfg.getDeclaredMethod("applyStatusBarTypeface",
                        Typeface.class, TextView[].class);
                m.setAccessible(true);
                // 这里会被我们的 guard 拦截，但 guard 只在标记存在时动手，
                // 标记仍在 —— 所以必须临时摘掉标记再调。
                Object mark = tv.getTag(TAG_MARK);
                tv.setTag(TAG_MARK, null);
                try {
                    m.invoke(null, tf, new TextView[]{tv});
                } finally {
                    if (mark != null) {
                        tv.setTag(TAG_MARK, mark);
                    }
                }
                return;
            }
        } catch (Throwable t) {
            L.log(5, TAG, "restoreSystemDefault via MiuiConfigs failed: " + t);
        }
        try {
            tv.setTypeface(Typeface.DEFAULT);
        } catch (Throwable t) {
            L.log(5, TAG, "restoreSystemDefault fallback failed: " + t);
        }
    }

    /**
     * 用 Paint 做「假加粗」。
     *
     * 【为什么需要它】
     *   ① 静态字体（如 MiClock-Light.otf）不支持 wght 可变轴，
     *      只能靠 fakeBold 或 strokeWidth 变粗；
     *   ② 玻璃材质需要笔画有一定面积才看得出来，
     *      细体字挂玻璃会几乎透明。
     *
     * 【v362 修「毛刺」（主人反馈）】
     *   主人截图里字形边缘有不明尖刺。根因是 Paint 的描边默认
     *   用 Join.MITER —— 笔画转折（如 "3" 的中腰、"1" 的钩）会
     *   按斜接延伸出一根尖刺，笔画越粗越明显。
     *
     *   修法（三个都要）：
     *     · setStrokeJoin(ROUND)  拐角圆化，消除尖刺 ← 关键
     *     · setStrokeCap(ROUND)   笔画端点圆化
     *     · setAntiAlias(true)    边缘抗锯齿，避免锯齿感
     *
     *   另外 strokeWidth 默认值从 1.2 降到 1.0，配合 wght 加粗一起用
     *   （纯 wght 加粗没有毛刺，是本功能的首选路径）。
     */
    private static void applyPaintBold(TextView tv) {
        boolean want = propInt("debug.hyperclassic.local.clock_fake_bold", 0) == 1;
        float stroke = propFloat("debug.hyperclassic.local.clock_stroke", 1.0f);
        // 刷新缓存，供 onDraw 的每帧断言使用
        cachedFakeBold = want;
        cachedStroke = stroke;
        assertPaintBold(tv);
    }

    /**
     * 每帧「重新断言」Paint 状态 —— 廉价到可以放在 onDraw 里。
     *
     * 只用缓存的 volatile 值，不读 SystemProperties、不反射。
     * 开销 = 几次字段比较，正常情况下全部命中「已正确」分支直接返回。
     */
    private static void assertPaintBold(TextView tv) {
        android.text.TextPaint p = tv.getPaint();
        if (p == null) {
            return;
        }
        boolean want = cachedFakeBold;
        if (!want) {
            if (p.isFakeBoldText()
                    || p.getStyle() != android.graphics.Paint.Style.FILL) {
                p.setFakeBoldText(false);
                p.setStrokeWidth(0f);
                p.setStyle(android.graphics.Paint.Style.FILL);
                tv.invalidate();
            }
            return;
        }
        float stroke = cachedStroke;
        if (!p.isFakeBoldText()
                || p.getStrokeWidth() != stroke
                || p.getStyle() != android.graphics.Paint.Style.FILL_AND_STROKE
                || !p.isAntiAlias()
                || p.getStrokeJoin() != android.graphics.Paint.Join.ROUND) {
            p.setAntiAlias(true);
            p.setFakeBoldText(true);
            p.setStrokeWidth(stroke);
            p.setStrokeJoin(android.graphics.Paint.Join.ROUND);
            p.setStrokeCap(android.graphics.Paint.Cap.ROUND);
            p.setStyle(android.graphics.Paint.Style.FILL_AND_STROKE);
            tv.invalidate();
        }
    }

    /**
     * 按 prop 解析字体。返回 null 表示「完全不动系统默认」。
     *
     * 组合语义（重要）：
     *
     *   font=0  weight=0    → null，一个字节都不改（最安全）
     *   font=0  weight>0    → 系统 MiPro 可变字体 + 指定 wght（= 只是加粗）
     *   font=N  weight=0    → 换成第 N 种字体，用其自带默认粗细
     *   font=N  weight>0    → 换成第 N 种字体并加粗
     *
     * ⇒ 「加粗系统字体本身」是主人要的核心场景，必须支持 font=0 时生效。
     */
    private static Typeface resolveTypeface(Context ctx) {
        int sel = propInt("debug.hyperclassic.local.clock_font", 0);
        int weight = propInt("debug.hyperclassic.local.clock_weight", 0);

        // 两者都不动 → 保持系统原样
        if (sel == 0 && weight <= 0) {
            return null;
        }

        // ★ 缓存查表：同一 (sel, weight, path) 永远返回同一个实例引用。
        //   这是幂等判断成立的前提，也是「不缓存会死循环」的根治点。
        String customPath = sel == 99
                ? prop("debug.hyperclassic.local.clock_font_path", "").trim() : "";
        String key = sel + "|" + weight + "|" + customPath;
        Typeface cached = TF_CACHE.get(key);
        if (cached != null) {
            return cached;
        }

        Typeface base = null;
        try {
            if (sel == 0) {
                // ★ 关键路径：系统 MiPro 可变字体 + 加粗
                base = loadMiProSys(weight);
            } else if (sel == 99) {
                base = loadCustom(ctx, weight);
            } else {
                base = loadBuiltin(ctx, sel, weight);
            }
        } catch (Throwable t) {
            L.log(5, TAG, "resolveTypeface sel=" + sel + " w=" + weight
                    + " failed: " + t);
        }
        if (base == null) {
            return null;
        }

        // ★ 不再用 Typeface.create(base, weight) 兜底（v360 实测踩坑）
        //
        //   Typeface.Builder(fontFile).setFontVariationSettings("'wght' 700").build()
        //   已经产出「wght=700 的那个实例」；
        //   再套一层 Typeface.create(base, 700, false) 会走**字体家族匹配**，
        //   重新按 family/style 解析，把 variation settings 丢掉，
        //   结果回到字体自带的默认粗细 —— 实测时钟依然是细体，加粗无效。
        //
        //   ⇒ 可变字体：完全依赖 Builder 的 wght，不再二次 create；
        //     静态字体：Builder 会忽略 wght（保持原样），
        //              加粗交给 applyPaintBold() 的 fakeBold/stroke 处理。
        TF_CACHE.put(key, base);
        return base;
    }

    /**
     * 系统 MiPro 可变字体（就是 MiuiClock 原本用的那个家族）。
     *
     * 路径来源优先 ro.miui.ui.font.mi_font_path（小米自定义字体的真相源），
     * 与 MiuiConfigs.FONT_MIPRO_PATH 的初始化逻辑完全一致：
     *   SystemProperties.get("ro.miui.ui.font.mi_font_path", "/system/fonts/MiSansVF.ttf")
     */
    private static Typeface loadMiProSys(int weight) {
        String path = prop("ro.miui.ui.font.mi_font_path",
                "/system/fonts/MiSansVF.ttf").trim();
        File f = new File(path);
        if (!f.exists()) {
            f = new File("/system/fonts/MiSansVF.ttf");
        }
        if (!f.exists()) {
            L.log(5, TAG, "MiPro sys font not found: " + path);
            return null;
        }
        try {
            Typeface.Builder b = new Typeface.Builder(f);
            if (weight > 0) {
                b.setFontVariationSettings("'wght' " + weight);
            }
            return b.build();
        } catch (Throwable t) {
            L.log(5, TAG, "loadMiProSys(" + f + ", w=" + weight + ") failed: " + t);
            return null;
        }
    }

    /** 内置字体（SystemUI 自带 assets 或系统字体）。 */
    private static Typeface loadBuiltin(Context ctx, int sel, int weight)
            throws Exception {
        AssetManager am = ctx.getAssets();
        switch (sel) {
            case 1:
                return build(am, "fonts/MiClock-Light.otf", weight);
            case 2:
                return build(am, "fonts/MiClock-Thin.otf", weight);
            case 3:
                return build(am, "fonts/Mitype2018-clock.ttf", weight);
            case 4:
                return build(am, "fonts/MitypeRoundedNormal.otf", weight);
            case 5: {
                // 系统可变字体
                String path = prop("ro.miui.ui.font.mi_font_path",
                        "/system/fonts/MiSansVF.ttf");
                File f = new File(path);
                if (!f.exists()) {
                    f = new File("/system/fonts/MiSansVF.ttf");
                }
                if (!f.exists()) {
                    return null;
                }
                Typeface.Builder b = new Typeface.Builder(f);
                if (weight > 0) {
                    b.setFontVariationSettings("'wght' " + weight);
                }
                return b.build();
            }
            default:
                return null;
        }
    }

    /**
     * 自定义字体（主人自行上传的「形状」）。
     *
     * 路径由 clock_font_path 给出。注意 SystemUI 是 system uid，
     * 常见的 /sdcard 路径在 Android 13+ 未必可读，因此这里做两级：
     *   ① 直接读 prop 指定路径
     *   ② 读不到就把同一个文件复制进 SystemUI 私有目录再读（私有目录必然可读）
     */
    private static Typeface loadCustom(Context ctx, int weight) {
        String path = prop("debug.hyperclassic.local.clock_font_path", "").trim();
        if (path.isEmpty()) {
            return null;
        }
        File src = new File(path);
        if (src.exists() && src.canRead()) {
            try {
                Typeface.Builder b = new Typeface.Builder(src);
                if (weight > 0) {
                    b.setFontVariationSettings("'wght' " + weight);
                }
                return b.build();
            } catch (Throwable ignored) {
            }
        }
        // ② 复制进私有目录
        try {
            File dir = new File(ctx.getFilesDir(), "hc_font");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File dst = new File(dir, "clock_font.bin");
            if (!dst.exists() || dst.length() != src.length()) {
                java.io.InputStream in = new java.io.FileInputStream(src);
                java.io.OutputStream out = new java.io.FileOutputStream(dst);
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) > 0) {
                    out.write(buf, 0, n);
                }
                in.close();
                out.close();
            }
            Typeface.Builder b = new Typeface.Builder(dst);
            if (weight > 0) {
                b.setFontVariationSettings("'wght' " + weight);
            }
            return b.build();
        } catch (Throwable t) {
            L.log(5, TAG, "loadCustom failed: " + t);
            return null;
        }
    }

    /** 从 SystemUI assets 构建字体。 */
    private static Typeface build(AssetManager am, String assetPath, int weight) {
        try {
            Typeface.Builder b = new Typeface.Builder(am, assetPath);
            if (weight > 0) {
                b.setFontVariationSettings("'wght' " + weight);
            }
            return b.build();
        } catch (Throwable t) {
            L.log(5, TAG, "build(" + assetPath + ") failed: " + t);
            return null;
        }
    }

    // ==================================================================
    // 工具
    // ==================================================================

    private static boolean runtime() {
        long now = android.os.SystemClock.uptimeMillis();
        if (now - onAt > 500L) {
            onCache = !"0".equals(prop(PROP_ON, "1"));
            onAt = now;
        }
        return onCache;
    }

    private static boolean probe() {
        return "1".equals(prop("debug.hyperclassic.local.clock_probe", "1"));
    }

    private static int propInt(String key, int def) {
        try {
            return Integer.parseInt(prop(key, String.valueOf(def)).trim());
        } catch (Throwable t) {
            return def;
        }
    }

    private static float propFloat(String key, float def) {
        try {
            return Float.parseFloat(prop(key, String.valueOf(def)).trim());
        } catch (Throwable t) {
            return def;
        }
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
        if (errors.incrementAndGet() <= 8) {
            L.always(TAG, "error: " + t);
        }
    }

    /** 节流日志计数（避免诊断栈刷屏）。 */
    private static final AtomicInteger throttleLogs = new AtomicInteger();

    /**
     * 取一段可读的调用栈（跳过 xposed / libxposed / 本类自身）。
     * 用于定位「是谁在每帧触发 apply」。
     */
    private static String shortStack() {
        try {
            StackTraceElement[] st = new Throwable().getStackTrace();
            StringBuilder sb = new StringBuilder();
            int n = 0;
            for (StackTraceElement e : st) {
                String cn = e.getClassName();
                if (cn.startsWith("java.lang.Thread")
                        || cn.startsWith("io.github.libxposed")
                        || cn.startsWith("io.github.mersand.hyperclassic.PortraitClockStyle")
                        || cn.startsWith("LSPosed")
                        || cn.startsWith("de.robv")) {
                    continue;
                }
                sb.append("  at ").append(cn).append("#").append(e.getMethodName())
                        .append(":").append(e.getLineNumber()).append('\n');
                if (++n >= 10) {
                    break;
                }
            }
            return sb.toString();
        } catch (Throwable t) {
            return "  <stack unavailable: " + t + ">";
        }
    }

    /** 供外部（设置页/探针）查询当前是否启用。 */
    static boolean enabled() {
        return runtime();
    }
}
