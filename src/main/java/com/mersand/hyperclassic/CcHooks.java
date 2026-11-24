/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.app.NotificationManager
 *  android.content.ContentResolver
 *  android.content.Context
 *  android.content.res.ColorStateList
 *  android.content.res.Configuration
 *  android.content.res.Resources
 *  android.content.res.Resources$Theme
 *  android.graphics.Canvas
 *  android.graphics.Color
 *  android.graphics.Outline
 *  android.graphics.Paint
 *  android.graphics.Paint$Style
 *  android.graphics.Path
 *  android.graphics.Path$Direction
 *  android.graphics.PorterDuff$Mode
 *  android.graphics.Rect
 *  android.graphics.RectF
 *  android.graphics.drawable.ColorDrawable
 *  android.graphics.drawable.Drawable
 *  android.graphics.drawable.GradientDrawable
 *  android.graphics.drawable.LayerDrawable
 *  android.graphics.drawable.StateListDrawable
 *  android.os.Handler
 *  android.os.Looper
 *  android.os.SystemClock
 *  android.service.notification.StatusBarNotification
 *  android.text.Spannable
 *  android.text.style.ForegroundColorSpan
 *  android.util.Log
 *  android.view.MotionEvent
 *  android.view.View
 *  android.view.ViewGroup
 *  android.view.ViewGroup$LayoutParams
 *  android.view.ViewOutlineProvider
 *  android.view.ViewParent
 *  android.view.WindowManager
 *  android.view.WindowManager$LayoutParams
 *  android.widget.AbsSeekBar
 *  android.widget.FrameLayout$LayoutParams
 *  android.widget.ImageView
 *  android.widget.LinearLayout$LayoutParams
 *  android.widget.ProgressBar
 *  android.widget.SeekBar
 *  android.widget.TextView
 *  io.github.libxposed.api.XposedInterface$Chain
 *  io.github.libxposed.api.XposedInterface$ExceptionMode
 *  io.github.libxposed.api.XposedModule
 */
package com.mersand.hyperclassic;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.service.notification.StatusBarNotification;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.AbsSeekBar;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.mersand.hyperclassic.ViewGlassProbe;
import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public final class CcHooks {
    private static final String TAG = "HyperClassic";
    private static final String CLS_INTERACTOR = "com.android.systemui.statusbar.notification.style.domain.NotificationMaterialStateInteractor";
    private static final String CLS_LAMBDA = "com.android.systemui.statusbar.notification.style.domain.NotificationMaterialStateInteractor$materialTypeState$1";
    private static final String CLS_COMBINE = "com.android.systemui.statusbar.notification.style.domain.NotificationMaterialStateInteractor$special$$inlined$combine$1$3";
    private static final String CLS_MATERIAL_TYPE = "com.miui.systemui.material.MaterialType";
    private static final String CLS_GLASS_EFFECT = "com.android.systemui.statusbar.notification.style.vieweffect.NotificationRowGlassEffect";
    private static final String CLS_MI_BLUR = "com.miui.systemui.util.MiBlurCompat";
    private static final String CLS_MI_GLASS = "com.miui.systemui.util.MiGlassCompat";
    private static final String CLS_EXPANDABLE_ROW = "com.android.systemui.statusbar.notification.row.ExpandableNotificationRow";
    private static final String CLS_CC_CONTROLLER = "com.android.systemui.controlcenter.policy.ControlCenterControllerImpl";
    private static final String CLS_CC_MAP_2 = "com.miui.systemui.controlcenter.data.repository.ControlCenterSettingsRepositoryImpl$special$$inlined$map$1$2";
    private static final String M_IS_GLASS = "isGlassMaterialType";
    private static final String M_INVOKE_SUSPEND = "invokeSuspend";
    private static final String F_MATERIAL_TYPE = "materialType";
    private static final AtomicInteger L4_COUNT = new AtomicInteger();
    private static final AtomicInteger L4_ENTRY = new AtomicInteger();
    private static final AtomicInteger L5_COUNT = new AtomicInteger();
    private static final AtomicInteger GLASSP_COUNT = new AtomicInteger();
    private static final AtomicInteger BLURALPHA_COUNT = new AtomicInteger();
    private static final AtomicInteger VIEWPROBE_COUNT = new AtomicInteger();
    private static final AtomicInteger DIRECT_COUNT = new AtomicInteger();
    private static final AtomicInteger DUMPS_COUNT = new AtomicInteger();
    private static final AtomicInteger EFFECTFWD_COUNT = new AtomicInteger();
    private static final ThreadLocal<Boolean> IN_GLASS_APPLY = new ThreadLocal();
    private static final AtomicInteger BLENDLIGHT_COUNT = new AtomicInteger();
    private static final AtomicInteger MEDIABLUR_COUNT = new AtomicInteger();
    private static final AtomicInteger GLASSPARAM_COUNT = new AtomicInteger();
    private static final AtomicInteger BGPROBE_COUNT = new AtomicInteger();
    private static final AtomicInteger IDPROBE_COUNT = new AtomicInteger();
    private static final AtomicInteger QSTRAY_COUNT = new AtomicInteger();
    private static final AtomicInteger DUMPTREE_COUNT = new AtomicInteger();
    private static final AtomicInteger TILEDUMP_COUNT = new AtomicInteger();
    private static final AtomicInteger TILE_APPLY_COUNT = new AtomicInteger();
    private static final AtomicInteger TILEPROBE_COUNT = new AtomicInteger();
    private static final String PROP_USE_CC = "debug.sysui.use_control_center";
    private static final String KEY_CC_MODE = "hyperclassic_cc_mode";
    private static final AtomicInteger L41_HIT = new AtomicInteger();
    public static final int CC_MODE_FOLLOW = Integer.MIN_VALUE;
    private static volatile Object CACHED_CR = null;
    private static final Map<View, List<Consumer<View>>> DRAW_GUARDS = Collections.synchronizedMap(new WeakHashMap());
    private static volatile boolean DRAW_HOOK_INSTALLED = false;
    private static final Set<View> PARAM_GUARDED = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
    private static final Map<View, Object[]> PARAM_TARGETS = Collections.synchronizedMap(new WeakHashMap());
    private static volatile long PARAM_LAST_SWEEP = 0L;
    private static volatile long LAST_DIAG_AT = 0L;
    private static volatile long LAST_FOLD_CANCEL_AT = 0L;
    private static volatile boolean PARAM_WATCHER_STARTED = false;
    private static volatile Handler MAIN_HANDLER = null;
    private static final AtomicInteger L42_HIT = new AtomicInteger();
    private static final AtomicInteger MEDIAVIEW_COUNT = new AtomicInteger();
    private static final int ID_QS_CONTENT = 2131429767;
    private static final int ID_QUICK_QSPANEL = 2131429803;
    private static final int ID_QS_FOOTER = 2131429780;
    private static final int ID_QS_BACKGROUND = 2131429767;
    private static final int ID_PANEL_ROUND_BG = 2131236976;
    private static final int ID_BRIGHTNESS_SLIDER = 2131430146;
    private static final Map<Integer, Integer> QS_DARK_COLORS = new HashMap<Integer, Integer>(){
        {
            this.put(2131102869, -855638017);
            this.put(2131102870, -1);
            this.put(2131102871, -1);
            this.put(2131102872, 0x66FFFFFF);
            this.put(2131102866, 0x14FFFFFF);
            // ★ v1.0.3：磁贴启用蓝统一为浅色亮蓝 0xff0099ff
            //   （官方浅色 qs_tile_icon_bg_enabled_color=#ff0099ff，
            //     暗色=#ff0077cc 偏深；主人要求深浅色统一 → 浅色蓝）
            this.put(2131102867, -16737793);
            this.put(2131102868, 0x4DFFFFFF);
            this.put(2131102873, -419430401);
            this.put(2131102874, -1275068417);
            this.put(2131102863, 0x4DFFFFFF);
            this.put(2131102858, 0x73FFFFFF);
            this.put(2131102857, 0x1AFFFFFF);
            this.put(2131102860, -14540252);
            this.put(2131102856, 0x14FFFFFF);
            this.put(2131102865, 0x29FFFFFF);
            this.put(2131102859, -1);
            this.put(2131102861, 788200191);
            this.put(2131102703, -1);
            this.put(2131102710, -1);
            this.put(2131102648, -419430401);
            this.put(2131102718, -1711276033);
            this.put(2131102716, -2130706433);
            this.put(2131102721, -419430401);
            this.put(2131102717, -2130706433);
            this.put(2131102722, -419430401);
            this.put(2131102649, -149481705);
            this.put(2131102651, -16777216);
        }
    };
    private static final Set<String> COLOR_SEEN = Collections.synchronizedSet(new LinkedHashSet());
    // ★ v44：绘制瞬间采样的限流时间戳
    private static final java.util.concurrent.atomic.AtomicLong LAST_DRAWSAMPLE = new java.util.concurrent.atomic.AtomicLong(0L);
    // ★ v49：已处理过的 drawable 实例记忆（弱引用，防泄漏；identityHashCode 会被回收后复用，
    //   弱集合保证不阻止 GC——实例死了自然出集合，hash 复用也无碍）
    private static final java.util.Set<Integer> SEEN_DRAWABLES =
            java.util.Collections.newSetFromMap(new java.util.WeakHashMap<Integer, Boolean>());
    // ★ v50：我们修复过的 drawable 实例（弱引用键）——这些实例每 300ms 重采样，
    //   防替换者原地 setTint 暗色（实例不变只换颜色的手法）
    private static final java.util.Map<Drawable, Boolean> FIXED_DRAWABLES =
            new java.util.WeakHashMap<Drawable, Boolean>();
    // ★ v52：按实例的重采样时间戳（v51 全局 LAST_RESAMPLE 有配额抢夺 bug）
    private static final java.util.Map<Drawable, Long> FIXED_RESAMPLE_TS =
            new java.util.WeakHashMap<Drawable, Long>();
    // ★ v67：二级菜单玻璃去重（独立集合，避免与 applyUnifiedGlass 内部的
    //   UNIFIED_GLASS_APPLIED 判重冲突——v66 曾因此导致玻璃没挂上）
    private static final java.util.Map<View, Boolean> DETAIL_GLASS_DONE =
            new java.util.WeakHashMap<View, Boolean>();
    // ★ v73：条目背景日志限流计数
    private static final java.util.concurrent.atomic.AtomicInteger DETAIL_BG_LOG73 =
            new java.util.concurrent.atomic.AtomicInteger();
    // ★ v77：通知区滚动判定兜底日志限流
    private static final java.util.concurrent.atomic.AtomicInteger SCROLLREGION_LOG77 =
            new java.util.concurrent.atomic.AtomicInteger();
    // ★ v1.0.3j：陈旧绑定重绑的防递归闸（ThreadLocal，重绑调用自身 hook 时直接放行）
    private static final ThreadLocal<Boolean> REBIND_GUARD = new ThreadLocal<Boolean>();
    private static final int TAG_BRIGHTNESS_SLIDER = 2131689474;
    private static final AtomicInteger SLIDER_PROBE_COUNT = new AtomicInteger();
    private static final Map<Class<?>, Boolean> NOTIF_CLASS_CACHE = Collections.synchronizedMap(new WeakHashMap());
    private static final AtomicInteger NOTIF_ICON_FIX_COUNT = new AtomicInteger();
    private static final AtomicInteger NOTIF_FIX_COUNT = new AtomicInteger();
    // ★ release：调试探针总门控——默认关闭，设 debug.hyperclassic.probes=1 可重新开启
    // （探针代码全部保留在原位，仅由本开关禁用安装）
    private static final boolean PROBES_ON =
            "1".equals(CcHooks.getProp("debug.hyperclassic.probes", "0").trim());
    private static final boolean DEBUG_ICON_DUMP = false;
    private static final AtomicInteger ICON_DUMP_TIMES = new AtomicInteger();
    private static final AtomicInteger NOTIF_ICON_DUMP = new AtomicInteger();
    private static final AtomicInteger ICONPROBE_COUNT = new AtomicInteger();
    private static final AtomicInteger PAGEFIX_PROBE = new AtomicInteger();
    private static final Set<View> EXPBTN_GLASS_DONE = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
    private static final Set<View> EXPBTN_CIRCLE_DONE = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
    private static final Set<View> PILL_CIRCLE_DONE = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
    private static final float[] GLASS_PARAMS_FALLBACK = new float[]{0.67f, 0.16f, 0.09f, 0.0f, 0.24f, 1.4f, -0.02f, 0.3f, 0.6f, 1.0f, 0.03f, 1.0f, 1.0f, 1.0f, 0.1f, 0.2f, 0.3f, 1.0f, 1.0f, 72.0f, 3.8f, 80.0f, 800.0f, 1.2f, 1.0f, -0.4f, 0.6f, -0.8f, 1.4f, 0.7f, 0.8f, 1.15f, 4.0f, 2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    private static final String[] GLASS_TEMPLATE_NAMES = new String[]{"normal", "headsup", "headsup_dark", "keyguard", "keyguard_light", "dismiss", "hybrid"};
    /**
     * ★ v142：悬浮通知玻璃参数（= GLASS_TEMPLATES[1]，官方 HeadsUpNotificationGlassEffect :array_0 原文）。
     *
     *  【为什么单独抽出来】
     *    主人指出 Toast 的参考目标应是「悬浮通知」——两者都是
     *    在全新独立窗口里挂玻璃实例，处境一致。
     *    与媒体卡片配方的关键差异，正是「玻璃挂上但过于透明」的答案：
     *
     *      参数   媒体卡片   悬浮通知
     *      p0      0.67      0.5     反射强度
     *      p1      0.16      1.0 ★   不透明度（0.16 → 太透）
     *      p3      0.0       0.8 ★   底色强度
     *      p14     0.1       1.5     边缘增强
     *      p19     72        62      模糊半径
     *      p22     800       600     距离衰减
     */
    private static final float[] HEADS_UP_GLASS_PARAMS = new float[]{
            0.5f, 1.0f, 0.0f, 0.8f, 0.5f, 1.2f, 0.0f, 0.2f, 0.0f, 0.0f,
            0.03f, 1.0f, 1.0f, 1.0f, 1.5f, 0.0f, 0.6f, 0.6f, 1.0f, 62.0f,
            3.8f, 80.0f, 600.0f, 1.0f, 0.8f, -0.4f, 0.6f, -0.8f, 1.2f, 0.6f,
            0.8f, 1.15f, 3.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f
    };
    private static final float[][] GLASS_TEMPLATES = new float[][]{{0.67f, 0.16f, 0.09f, 0.0f, 0.24f, 1.4f, -0.02f, 0.3f, 0.6f, 1.0f, 0.03f, 1.0f, 1.0f, 1.0f, 0.1f, 0.2f, 0.3f, 1.0f, 1.0f, 72.0f, 3.8f, 80.0f, 800.0f, 1.2f, 1.0f, -0.4f, 0.6f, -0.8f, 1.4f, 0.7f, 0.8f, 1.15f, 4.0f, 2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, {0.5f, 1.0f, 0.0f, 0.8f, 0.5f, 1.2f, 0.0f, 0.2f, 0.0f, 0.0f, 0.03f, 1.0f, 1.0f, 1.0f, 1.5f, 0.0f, 0.6f, 0.6f, 1.0f, 62.0f, 3.8f, 80.0f, 600.0f, 1.0f, 0.8f, -0.4f, 0.6f, -0.8f, 1.2f, 0.6f, 0.8f, 1.15f, 3.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, {0.8f, 1.0f, 0.0f, 1.0f, 0.2f, 2.0f, 0.14f, 0.1f, 0.0f, 0.0f, 0.02f, 0.27f, 0.27f, 0.27f, 0.6f, 0.0f, 0.2f, 1.2f, 1.0f, 72.0f, 3.8f, 80.0f, 600.0f, 1.0f, 0.8f, -0.4f, 0.6f, -0.8f, 1.5f, 1.0f, 0.8f, 1.15f, 3.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, {0.0f, 2.0f, 0.3f, 0.4f, 0.15f, 1.4f, 0.08f, 0.2f, 0.6f, 1.0f, 0.03f, 1.0f, 1.0f, 1.0f, 0.15f, 0.15f, 0.4f, 1.2f, 1.0f, 72.0f, 3.8f, 80.0f, 1000.0f, 1.0f, 0.8f, -0.4f, 0.6f, -0.8f, 1.3f, 0.6f, 0.8f, 1.15f, 3.0f, 2.4f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, {0.0f, 2.0f, 0.5f, 0.8f, 0.15f, 1.4f, 0.08f, 0.2f, 0.6f, 1.0f, 0.05f, 1.0f, 1.0f, 1.0f, 0.15f, 0.15f, 0.4f, 1.2f, 1.0f, 60.0f, 4.0f, 60.0f, 1000.0f, 1.0f, 0.8f, -0.4f, 0.6f, -0.8f, 1.4f, 0.8f, 1.4f, 1.0f, 3.0f, 2.4f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, {0.42f, 0.22f, 0.24f, 0.0f, 0.45f, 1.4f, 0.28f, 0.4f, 0.6f, 1.0f, 0.03f, 1.0f, 1.0f, 1.0f, 0.1f, 0.2f, 0.3f, 1.0f, 1.0f, 62.0f, 4.0f, 80.0f, 1200.0f, 1.2f, 1.0f, -0.4f, 0.6f, -0.8f, 1.3f, 0.8f, 0.8f, 1.15f, 4.0f, 2.6f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, {0.5f, 1.0f, 0.0f, 0.8f, 0.5f, 1.2f, 0.0f, 0.2f, 0.0f, 0.0f, 0.03f, 1.0f, 1.0f, 1.0f, 1.5f, 0.0f, 0.6f, 0.6f, 1.0f, 62.0f, 3.8f, 80.0f, 600.0f, 1.0f, 0.8f, -0.4f, 0.6f, -0.8f, 1.2f, 0.6f, 0.8f, 1.15f, 3.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}};
    private static volatile Method MI_GLASS_SET_PARAMS = null;
    private static volatile Method MI_GLASS_SET_TYPE = null;
    private static volatile Method MI_BLUR_SET_BLEND = null;
    private static final Map<View, Integer> MEDIA_BLEND_APPLIED = Collections.synchronizedMap(new WeakHashMap());
    private static final AtomicInteger MEDIA_BLEND_COUNT = new AtomicInteger();
    private static final int LIGHTGLASS_FILL = 0xDFFFFFF;
    private static final int LIGHTGLASS_BLEND = 0x40FFFFFF;
    private static final int LIGHTGLASS_RADIUS = 80;
    private static final Set<View> LIGHTGLASS_APPLIED = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
    private static final Set<View> PILL_GLASS_APPLIED = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
    private static final Set<View> PILL_FALLBACK_DONE = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
    private static final Set<View> PILL_RETRY = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
    private static final Map<View, Integer> L17_VIEW_COUNT = Collections.synchronizedMap(new WeakHashMap());
    private static final Set<View> UNIFIED_GLASS_APPLIED = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
    private static final String PROP_G_CONTRAST = "debug.hyperclassic.glass_contrast";
    private static final String PROP_G_TINT = "debug.hyperclassic.glass_tint";
    private static final String PROP_G_BLUR = "debug.hyperclassic.glass_blur";
    private static volatile long GLASS_PARAM_AT = 0L;
    private static volatile int GC_CONTRAST = 50;
    private static volatile int GC_TINT = 25;
    private static volatile int GC_BLUR = 80;
    private static final AtomicInteger DUMP_SLIDER = new AtomicInteger();
    private static final AtomicInteger DUMP_OTHER = new AtomicInteger();
    private static final AtomicInteger BLUR_APPLY_COUNT = new AtomicInteger();
    private static volatile String gErr = null;
    private static volatile String bErr = null;
    private static final AtomicInteger L47_GLASS_COUNT = new AtomicInteger();
    private static final AtomicInteger L47_BG_COUNT = new AtomicInteger();
    private static final Set<View> TOAST_DONE = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
    private static final AtomicInteger TOAST_COUNT = new AtomicInteger();
    // ★ v9.92：Toast 未 attach 时的重试集合
    private static final Set<View> TOAST_RETRY = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
    private static final int TOAST_BLUR_BG = 2131238093;
    private static final Set<View> RADII_CLIP_GUARDED = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
    private static final AtomicInteger RADII_CLIP_COUNT = new AtomicInteger();
    private static final float CARD_CORNER_RADIUS = 110.0f;
    private static final AtomicInteger ROWSELF_COUNT = new AtomicInteger();
    private static final float[] GLASS_PARAMS_HEADSUP = new float[]{0.5f, 1.0f, 0.0f, 0.8f, 0.5f, 1.2f, 0.0f, 0.2f, 0.0f, 0.0f, 0.03f, 1.0f, 1.0f, 1.0f, 1.5f, 0.0f, 0.6f, 0.6f, 1.0f, 62.0f, 3.8f, 80.0f, 600.0f, 1.0f, 0.8f, -0.4f, 0.6f, -0.8f, 1.2f, 0.6f, 0.8f, 1.15f, 3.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    private static final int RES_HU_BLEND_C1 = 2131102658;
    private static final int RES_HU_BLEND_M1 = 2131493144;
    private static final int RES_HU_BLEND_C2 = 2131102659;
    private static final int RES_HU_BLEND_M2 = 2131493145;
    private static final Set<View> MEDIA_GLASS_DONE = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
    // ★ v79：正在等待布局重试的媒体视图（用于自愈巡检，避免重复排队）
    private static final Map<View, Boolean> MEDIA_RETRY_PENDING =
            Collections.synchronizedMap(new WeakHashMap<View, Boolean>());
    // ★ v79：自愈计数（日志限流）
    private static final AtomicInteger MEDIA_HEAL_COUNT = new AtomicInteger();
    // ★ v81：AOD/锁屏媒体 effect 接管计数
    private static final AtomicInteger MEDIA_EFFECT_CNT81 = new AtomicInteger();
    // ★ v83：mediaBg 高度改 MATCH_PARENT 的计数（日志限流）
    private static final AtomicInteger MEDIA_AOD_HEIGHT_CNT83 = new AtomicInteger();
    // ★ v85：finishForMediaNotificationAnim 的高度插值窗口期（期间禁止套玻璃）
    private static volatile boolean MEDIA_ANIM_GUARD85 = false;
    private static volatile long MEDIA_ANIM_GUARD_TS85 = 0L;
    // ★ v87：outline 重挂计数（日志限流）+ 官方设定的 mediaBg 目标高度
    private static final AtomicInteger MEDIA_OUTLINE_CNT87 = new AtomicInteger();
    private static final Map<View, Integer> MEDIA_BG_TARGET_H87 =
            Collections.synchronizedMap(new WeakHashMap<View, Integer>());
    // ★ v89：AOD 退出时补调媒体动画的计数（日志限流）
    private static final AtomicInteger MEDIA_AOD_TAIL_CNT89 = new AtomicInteger();
    // ★ v91：AOD 状态切换诊断计数
    private static final AtomicInteger MEDIA_DIAG_CNT91 = new AtomicInteger();
    // ★ v95：逐帧时间线诊断
    private static volatile long AOD_ENTER_TS112 = 0L; // v112 进入 AOD 时刻
    private static final AtomicInteger AOD_RESET_GUARD_CNT112 = new AtomicInteger(); // v112 跳过计数
    private static final AtomicInteger AOD_RESET_REAL_CNT112 = new AtomicInteger(); // v112 放行计数
    private static final AtomicInteger LINKAGE_GUARD_CNT114 = new AtomicInteger(); // v114 恢复计数
    private static final AtomicInteger LINKAGE_KEEP_CNT114 = new AtomicInteger(); // v114 保留计数
    private static final AtomicInteger JUMP_BLOCK_CNT115 = new AtomicInteger(); // v115 跳变拦截计数
    private static final AtomicInteger AOD_FALSE_CNT115 = new AtomicInteger(); // v115 false 诊断计数
    private static final AtomicInteger AOD_FALSE_BLOCK_CNT116 = new AtomicInteger(); // v116 false 拦截计数
    private static final AtomicInteger ENTER_GUARD_CNT118 = new AtomicInteger(); // v118 进AOD恢复计数
    private static final AtomicInteger LINKAGE_DIAG_CNT119 = new AtomicInteger(); // v119 linkage 诊断
    private static final AtomicInteger ENTER_KEEP_CNT120 = new AtomicInteger(); // v120 进AOD保持计数
    private static volatile long WAKEUP_TS121 = 0L; // v121 亮屏时刻（onDisplayStateOn）
    private static final AtomicInteger DISPLAY_ON_CNT121 = new AtomicInteger(); // v121 亮屏计数
    private static volatile int WAKEUP_STATE121 = -1; // v121 亮屏时的 wakeupAnimationState
    private static final AtomicInteger WAKEUP_FALSE_BLOCK_CNT123 = new AtomicInteger(); // v123 亮屏false拦截
    private static final AtomicInteger AOD_FALSE_STACK_CNT124 = new AtomicInteger(); // v124 调用栈诊断
    private static final AtomicInteger GEO_CLAMP_CNT125 = new AtomicInteger(); // v125 几何钳制计数
    private static final AtomicInteger SETAH_DIAG_CNT127 = new AtomicInteger(); // v127 setAH 现场诊断
    private static final AtomicInteger ANIM_HEIGHT_SYNC_CNT128 = new AtomicInteger(); // v128 aniH 同步计数
    private static final java.util.concurrent.atomic.AtomicBoolean IN_ANIM_HEIGHT_SYNC128 =
            new java.util.concurrent.atomic.AtomicBoolean(false); // v128 防重入
    private static final AtomicInteger LATE_CLIP_CNT130 = new AtomicInteger(); // v130 延迟裁剪计数
    private static final AtomicInteger LATE_CLIP_DONE_CNT130 = new AtomicInteger(); // v130 延迟裁剪完成
    // ★ v133：v131/v132 的字段已删除（self 高度追踪 + ah 对齐实验，课题已存档）
    private static final java.util.Set<String> TILE_DIFF_SEEN =
            java.util.Collections.synchronizedSet(new java.util.LinkedHashSet<>()); // v137 磁贴差异去重
    private static final AtomicInteger TILE_DIFF_CNT = new AtomicInteger(); // v137 磁贴差异计数
    private static final java.util.Set<View> TILE_BLEND_SEEN =
            java.util.Collections.synchronizedSet(java.util.Collections.newSetFromMap(new java.util.WeakHashMap<>())); // v137 已套 blend 的 iconFrame
    private static final AtomicInteger TILE_BG_BLOCK_CNT139 = new AtomicInteger(); // v139 拦截官方白底计数
    private static final AtomicInteger L111_DIAG_CNT = new AtomicInteger(); // v111 双击死区诊断
    private static final AtomicLong MEDIA_LAST_AH108 = new AtomicLong(0L); // v108 跳变检测
    private static final AtomicInteger MEDIA_JUMP_CNT108 = new AtomicInteger(); // v108
    private static final AtomicInteger MEDIA_LP_SYNC_CNT107 = new AtomicInteger(); // v107
    private static final AtomicInteger MEDIA_CALLER_CNT105 = new AtomicInteger(); // v105 调用来源诊断
    private static final AtomicInteger MEDIA_OUTLINE_FILL_CNT103 = new AtomicInteger(); // v103
    private static final AtomicInteger MEDIA_ANIM_KEEP_CNT99 = new AtomicInteger(); // v99 计数
    private static final AtomicInteger MEDIA_TL_CNT95 = new AtomicInteger();
    private static final AtomicLong MEDIA_TL_LAST95 = new AtomicLong(0L);
    // ★ v96：mediaBg 高度同步（防递归闸 + 计数）
    private static final ThreadLocal<Boolean> MEDIA_SYNC_GUARD96 = ThreadLocal.withInitial(() -> Boolean.FALSE);
    private static final AtomicInteger MEDIA_SYNC_CNT96 = new AtomicInteger();

    /** ★ v95：安全读 int 字段（父类链，失败返回 -1）。 */
    private static int getIntFieldSafe(Object obj, String name) {
        Object o = getFieldSafe(obj, name);
        return o instanceof Integer ? (Integer) o : -1;
    }

    /** ★ v95：安全读 boolean 字段（父类链，失败返回 false）。 */
    private static boolean getBoolFieldSafe(Object obj, String name) {
        Object o = getFieldSafe(obj, name);
        return o instanceof Boolean && (Boolean) o;
    }

    /** ★ v83：安全取字段（父类链查找，找不到返回 null）。 */
    private static Object getFieldSafe(Object obj, String name) {
        if (obj == null) return null;
        try {
            for (Class<?> c = obj.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
                try {
                    Field f = c.getDeclaredField(name);
                    f.setAccessible(true);
                    return f.get(obj);
                } catch (NoSuchFieldException ignored) {}
            }
        } catch (Throwable ignored) {}
        return null;
    }

    /**
     * ★ v80：判断视图是否正在动画/过渡中。
     *   AOD↔锁屏切换时官方会播放媒体卡片展开动画，若此时被我们重设背景/outline，
     *   动画会被直接打断（表现为「展开动画丢失，收起正常」）。
     *   检测手段：
     *     ① getAnimation() 非空 → 传统 View 动画进行中
     *     ② getTransitionAlpha() != getAlpha() → Transition 正在驱动透明度
     *     ③ isLayoutRequested() → 布局还没完成
     *     ④ translation/scale 非默认值 → 变换动画进行中
     */
    private static boolean isViewAnimating(View v) {
        try {
            // ★ v81 收敛：只判断"确实正在播放动画"的强信号。
            //   v80 把 translation/scale/alpha 变化都算作动画，误伤过多——
            //   官方媒体卡片常态就带轻微 scale/translation，导致自愈长期失效。
            if (v.getAnimation() != null) return true;
            if (v.isLayoutRequested()) return true;
            if (Math.abs(v.getTransitionAlpha() - v.getAlpha()) > 0.01f) return true;
            // ★ v85：finishForMediaNotificationAnim 的高度插值窗口期
            if (MEDIA_ANIM_GUARD85) return true;
            // ★ v85：Folme 的 AOD↔锁屏过渡不体现在 View 动画属性上——
            //   NotifiFullAodController$FullAodTransitionListener.onUpdate 逐帧驱动
            //   updateFullAodAnimState（改玻璃参数/transitionAlpha），
            //   且 onComplete 之后还会调 finishForMediaNotificationAnim() 做高度插值。
            //   这段时间里若套玻璃，观感就是「动画完成后又被裁剪一次」。
            //   因此额外读 mIsAnimating 这个全局标志。
            try {
                Class<?> ctl = Class.forName(
                        "com.android.systemui.statusbar.notification.fullaod.NotifiFullAodController",
                        false, v.getClass().getClassLoader());
                Field fm = ctl.getDeclaredField("mIsAnimating");
                fm.setAccessible(true);
                Object o = fm.get(null);
                if (o instanceof Boolean && (Boolean) o) return true;
            } catch (Throwable ignored) {}
        } catch (Throwable ignored) {}
        return false;
    }
    private static final int TAG_MEDIA_PARAMS_OWNED = 2131689504;
    private static volatile float[] MEDIA_GLASS_PARAMS_WANT = null;
    private static final AtomicInteger L17_SKIP = new AtomicInteger();
    private static final AtomicBoolean MEDIA_TREE_DUMP = new AtomicBoolean(false);
    private static final int RES_TRANSPARENT_ROUND_BG = 2131236940;
    private static final int TAG_MEDIA_BG_COLOR = 2131689505;
    private static final int GUARD_NO_BG = -1;
    private static final Map<View, Integer> MEDIA_BG_GUARDED = Collections.synchronizedMap(new WeakHashMap());
    private static volatile boolean MEDIA_BG_GUARD_INSTALLED = false;
    private static final Map<View, float[]> MEDIA_PARAMS_APPLIED = Collections.synchronizedMap(new WeakHashMap());
    private static volatile long MEDIA_PARAMS_CHECK_AT = 0L;
    private static volatile float[] MEDIA_PARAMS_SNAPSHOT = null;
    private static volatile boolean MEDIA_LIVE_POLL_STARTED = false;
    private static final AtomicInteger MEDIA_POLL_ERR = new AtomicInteger();
    private static final AtomicInteger MEDIA_GUARD_FIX = new AtomicInteger();
    private static final AtomicInteger MEDIA_LIVE_REFRESH = new AtomicInteger();
    private static volatile boolean MEDIA_SBG_TRACER_INSTALLED = false;
    private static final AtomicInteger SBG_TRACE_COUNT = new AtomicInteger();
    private static final Map<View, Paint> BACKING_PAINTS = Collections.synchronizedMap(new WeakHashMap());
    private static long LAST_PARAM_APPLY = 0L;
    private static volatile int PARAM_VERSION = 0;
    private static volatile int PARAM_VERSION_SEEN = -1;
    private static Resources SYSUI_RES;
    private static final Set<View> GLASS_RETRY;
    private static final int RES_GLASS_BG = 2131236940;
    private static final int RES_BLEND_NOTIF_C1 = 2131102662;
    private static final int RES_BLEND_NOTIF_M1 = 2131493148;
    private static final int RES_BLEND_NOTIF_C2 = 2131102663;
    private static final int RES_BLEND_NOTIF_M2 = 2131493149;
    private static final int RES_BLEND_MEDIA_C1 = 2131101050;
    private static final int RES_BLEND_MEDIA_M1 = 2131493098;
    private static final int RES_BLEND_MEDIA_C2 = 2131101051;
    private static final int RES_BLEND_MEDIA_M2 = 2131493097;
    private static final int RES_BLEND_MEDIA_C3 = 2131101052;
    private static final int RES_BLEND_MEDIA_M3 = 2131493099;
    private static final float[] GLASS_PARAMS_DISMISS;
    private static final AtomicInteger BLURONLY_COUNT;
    private static final AtomicInteger FAKEBG_COUNT;
    private static final Map<Class<?>, Field> BG_MBACKGROUND;
    private static final AtomicInteger FORCE_NULL_COUNT;
    private static final AtomicInteger PAGEDOT_PROBE;
    private static final Set<ViewGroup> PAGEDOT_GUARDED;
    private static final Set<ImageView> INDICATOR_GUARDED;
    private static final Set<ProgressBar> SLIDER_DRAWABLE_DONE;
    private static final AtomicInteger SLIDER_TREE_DUMP;
    private static final Set<View> SLIDER_GLASS_DONE;
    private static final int TAG_TINT_APPLIED = 2131689488;
    private static final Set<TextView> TEXT_GUARDED;
    private static final Set<ImageView> ICON_GUARDED;
    private static final AtomicInteger L39_COUNT;
    private static final Set<String> L39_SEEN;
    private static final Map<String, Integer> SEEN_HITS;
    private static volatile long CAPTURE_CHECK_AT;
    private static volatile boolean CAPTURE_ON;
    private static final long CAPTURE_TTL_MS = 600000L;
    private static volatile boolean CAPTURE_EXPIRED_LOGGED;
    private static final Map<View, Integer> TRAY_APPLIED;
    private static final Set<View> TRAY_GUARDED;
    private static final Set<View> SLIDER_GUARDED;
    private static final HashMap<String, Integer> LOG_COUNTS;

    private CcHooks() {
    }

    public static int readCcMode() {
        try {
            String p = CcHooks.getProp("debug.hyperclassic.cc_mode", "").trim();
            if (!p.isEmpty()) {
                if ("-1".equals(p) || "classic".equalsIgnoreCase(p)) {
                    return -1;
                }
                if ("1".equals(p) || "new".equalsIgnoreCase(p)) {
                    return 1;
                }
            }
        }
        catch (Throwable p) {
            // empty catch block
        }
        int v = CcHooks.readSystemInt(KEY_CC_MODE, Integer.MIN_VALUE);
        if (v == -1 || v == 1) {
            return v;
        }
        return Integer.MIN_VALUE;
    }

    private static Object getCr() {
        Object cr = CACHED_CR;
        if (cr != null) {
            return cr;
        }
        try {
            Object th;
            Class<?> at = Class.forName("android.app.ActivityThread");
            Object ctx = at.getMethod("currentApplication", new Class[0]).invoke(null, new Object[0]);
            if (ctx == null && (th = at.getMethod("currentActivityThread", new Class[0]).invoke(null, new Object[0])) != null) {
                ctx = at.getMethod("getSystemContext", new Class[0]).invoke(th, new Object[0]);
            }
            if (ctx != null && (cr = ctx.getClass().getMethod("getContentResolver", new Class[0]).invoke(ctx, new Object[0])) != null) {
                CACHED_CR = cr;
                return cr;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    public static int readSystemInt(String key, int def) {
        try {
            Object cr = CcHooks.getCr();
            if (cr == null) {
                return def;
            }
            Object v = Class.forName("android.provider.Settings$System").getMethod("getInt", ContentResolver.class, String.class, Integer.TYPE).invoke(null, cr, key, def);
            return ((Number)v).intValue();
        }
        catch (Throwable t) {
            return def;
        }
    }

    public static boolean hookIsUseControlCenter(XposedModule module, ClassLoader cl) {
        boolean want;
        int mode = CcHooks.readCcMode();
        if (mode == Integer.MIN_VALUE) {
            L.log(4, TAG, "L42 \u8ddf\u968f\u7cfb\u7edf\uff08\u4e0d\u5e72\u9884 StateFlow\uff09");
            return true;
        }
        boolean bl = want = mode != -1;
        if (!"1".equals(CcHooks.getProp("debug.hyperclassic.l42", "0").trim())) {
            L.log(4, TAG, "L42 \u5df2\u505c\u7528\uff08\u9632\u72b6\u6001\u9519\u4e71\uff09\u3002\u5982\u9700\u5c1d\u8bd5\uff1asetprop debug.hyperclassic.l42 1");
            return true;
        }
        try {
            Class<?> impl = Class.forName("com.miui.systemui.controlcenter.data.repository.ControlCenterSettingsRepositoryImpl", false, cl);
            Method getFlow = impl.getMethod("getUseControlCenter", new Class[0]);
            getFlow.setAccessible(true);
            module.hook((Executable)getFlow).setId("cc_l42_get_flow").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object flow = chain.proceed();
                if (flow == null) {
                    return null;
                }
                boolean ok = CcHooks.forceStateFlowValue(flow, want);
                int n = L42_HIT.incrementAndGet();
                if (n <= 6) {
                    L.log(4, TAG, "L42 forceUseControlCenter=" + want + " ok=" + ok);
                }
                return flow;
            });
            L.log(4, TAG, "L42 StateFlow \u63a5\u7ba1\u5df2\u88c5 \u2192 " + want + "\uff08\u5f3a\u5236" + (want ? "\u65b0\u7248" : "\u7ecf\u5178") + "\uff09");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L42 failed: " + t);
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void addDrawGuard(XposedModule module, View v, Consumer<View> guard) {
        if (v == null || guard == null) {
            return;
        }
        CcHooks.installDrawHookOnce(module);
        Map<View, List<Consumer<View>>> map = DRAW_GUARDS;
        synchronized (map) {
            List<Consumer<View>> list = DRAW_GUARDS.get(v);
            if (list == null) {
                list = new ArrayList<Consumer<View>>(2);
                DRAW_GUARDS.put(v, list);
            }
            list.add(guard);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void installDrawHookOnce(XposedModule module) {
        if (DRAW_HOOK_INSTALLED) {
            return;
        }
        Class<CcHooks> clazz = CcHooks.class;
        synchronized (CcHooks.class) {
            if (DRAW_HOOK_INSTALLED) {
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return;
            }
            try {
                Method draw = View.class.getDeclaredMethod("draw", Canvas.class);
                draw.setAccessible(true);
                module.hook((Executable)draw).setId("cc_draw_dispatch").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object self = chain.getThisObject();
                    if (self instanceof View) {
                        List<Consumer<View>> list;
                        Map<View, List<Consumer<View>>> map = DRAW_GUARDS;
                        synchronized (map) {
                            list = DRAW_GUARDS.get((View)self);
                            if (list != null) {
                                list = new ArrayList<Consumer<View>>(list);
                            }
                        }
                        if (list != null) {
                            for (Consumer consumer : list) {
                                try {
                                    consumer.accept((View)self);
                                }
                                catch (Throwable throwable) {}
                            }
                        }
                    }
                    return chain.proceed();
                });
                DRAW_HOOK_INSTALLED = true;
                L.log(4, TAG, "draw dispatcher installed (single hook)");
            }
            catch (Throwable t) {
                L.log(5, TAG, "draw dispatcher failed: " + t);
            }
            return;
        }
    }

    private static void installParamRefreshGuard(XposedModule module, View v, ClassLoader sysuiCl, String tag, int blurRadius, boolean doOutlineClip, boolean isRound) {
        PARAM_TARGETS.put(v, new Object[]{sysuiCl, tag, blurRadius, doOutlineClip, isRound});
        PARAM_GUARDED.add(v);
        CcHooks.startParamWatcher(module);
    }

    private static float safeCornerRadius(View v) {
        try {
            Drawable bg;
            int w = v.getWidth();
            int h = v.getHeight();
            if (w <= 0 || h <= 0) {
                w = v.getMeasuredWidth();
                h = v.getMeasuredHeight();
            }
            if ((bg = v.getBackground()) instanceof GradientDrawable) {
                try {
                    Field f = GradientDrawable.class.getDeclaredField("mRadius");
                    f.setAccessible(true);
                    Object o = f.get(bg);
                    if (o instanceof Float && ((Float)o).floatValue() > 0.0f) {
                        return ((Float)o).floatValue();
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            if (v.getClass().getName().contains("NotificationBackgroundView")) {
                return 28.0f * v.getResources().getDisplayMetrics().density;
            }
            if (w > 0 && h > 0) {
                return (float)Math.min(w, h) / 2.0f;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return 0.0f;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void startParamWatcher(XposedModule module) {
        if (PARAM_WATCHER_STARTED) {
            return;
        }
        Class<CcHooks> clazz = CcHooks.class;
        synchronized (CcHooks.class) {
            if (PARAM_WATCHER_STARTED) {
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return;
            }
            PARAM_WATCHER_STARTED = true;
            Thread th = new Thread(() -> {
                int lastVer = -1;
                while (true) {
                    try {
                        while (true) {
                            ArrayList<View> targets;
                            Thread.sleep(300L);
                            int cur = CcHooks.currentParamVersion();
                            if (cur == lastVer) continue;
                            lastVer = cur;
                            Map<View, Object[]> map = PARAM_TARGETS;
                            synchronized (map) {
                                targets = new ArrayList<View>(PARAM_TARGETS.keySet());
                            }
                            if (targets.isEmpty()) continue;
                            Handler h = MAIN_HANDLER;
                            if (h == null) {
                                MAIN_HANDLER = h = new Handler(Looper.getMainLooper());
                            }
                            h.post(() -> {
                                int ok = 0;
                                for (View v : targets) {
                                    try {
                                        Object[] p;
                                        if (v == null || !v.isAttachedToWindow()) continue;
                                        Map<View, Object[]> map2 = PARAM_TARGETS;
                                        synchronized (map2) {
                                            p = PARAM_TARGETS.get(v);
                                        }
                                        if (p == null) continue;
                                        v.setTag(2131689504, (Object)cur);
                                        boolean r = CcHooks.applyUnifiedGlass(module, v, (ClassLoader)p[0], (String)p[1], (Integer)p[2], (Boolean)p[3], (Boolean)p[4], true);
                                        if (!r) continue;
                                        v.invalidate();
                                        ++ok;
                                    }
                                    catch (Throwable throwable) {}
                                }
                                if (ok > 0) {
                                    L.log(4, TAG, "L43 param refresh ver=" + cur + " applied=" + ok + "/" + targets.size());
                                }
                                if (System.currentTimeMillis() - LAST_FOLD_CANCEL_AT > 5000L) {
                                    LAST_FOLD_CANCEL_AT = System.currentTimeMillis();
                                    try {
                                        CcHooks.cancelAggregateNotification(module);
                                    }
                                    catch (Throwable throwable) {
                                        // empty catch block
                                    }
                                }
                            });
                        }
                    }
                    catch (InterruptedException ie) {
                        return;
                    }
                    catch (Throwable throwable) {
                        continue;
                    }
                }
            }, "hyperclassic-param-watcher");
            th.setDaemon(true);
            th.start();
            L.log(4, TAG, "L43 param watcher started (poll 300ms)");
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    private static boolean forceStateFlowValue(Object readonlyFlow, boolean want) {
        try {
            Object mutable = null;
            Class<?> c = readonlyFlow.getClass();
            while (c != null && mutable == null) {
                for (Field f : c.getDeclaredFields()) {
                    if (!"flow".equals(f.getName()) && !"mutableStateFlow".equals(f.getName())) continue;
                    try {
                        f.setAccessible(true);
                        Object v = f.get(readonlyFlow);
                        if (v == null) continue;
                        mutable = v;
                        break;
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                }
                if (mutable != null) continue;
                c = c.getSuperclass();
            }
            if (mutable == null) {
                mutable = readonlyFlow;
            }
            Method getValue = mutable.getClass().getMethod("getValue", new Class[0]);
            getValue.setAccessible(true);
            Object cur = getValue.invoke(mutable, new Object[0]);
            if (cur instanceof Boolean && (Boolean)cur == want) {
                return true;
            }
            Method setValue = mutable.getClass().getMethod("setValue", Object.class);
            setValue.setAccessible(true);
            setValue.invoke(mutable, want);
            return true;
        }
        catch (Throwable t) {
            return false;
        }
    }

    public static boolean hookControlCenterFlow(XposedModule module, ClassLoader cl) {
        int mode = CcHooks.readCcMode();
        if (mode == Integer.MIN_VALUE) {
            L.log(4, TAG, "L41 \u8ddf\u968f\u7cfb\u7edf\u8bbe\u7f6e\uff08\u4e0d\u62e6\u622a use_control_panel\uff09");
            return true;
        }
        int want = mode == -1 ? 0 : 1;
        String KEY = "use_control_panel";
        int ok = 0;
        try {
            Class<?> settingsSystem = Class.forName("android.provider.Settings$System");
            Method getInt = settingsSystem.getMethod("getInt", ContentResolver.class, String.class, Integer.TYPE);
            module.hook((Executable)getInt).setId("cc_l41_getint").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object k = chain.getArg(1);
                if ("use_control_panel".equals(k)) {
                    return want;
                }
                return chain.proceed();
            });
            ++ok;
            try {
                Method getIntForUser = settingsSystem.getMethod("getIntForUser", ContentResolver.class, String.class, Integer.TYPE, Integer.TYPE);
                module.hook((Executable)getIntForUser).setId("cc_l41_getintforuser").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object k = chain.getArg(1);
                    if ("use_control_panel".equals(k)) {
                        return want;
                    }
                    return chain.proceed();
                });
                ++ok;
            }
            catch (Throwable getIntForUser) {
                // empty catch block
            }
            try {
                Method getString = settingsSystem.getMethod("getString", ContentResolver.class, String.class);
                module.hook((Executable)getString).setId("cc_l41_getstring").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object k = chain.getArg(1);
                    if ("use_control_panel".equals(k)) {
                        return String.valueOf(want);
                    }
                    return chain.proceed();
                });
                ++ok;
            }
            catch (Throwable getString) {
                // empty catch block
            }
            try {
                Method getStrForUser = settingsSystem.getMethod("getStringForUser", ContentResolver.class, String.class, Integer.TYPE);
                module.hook((Executable)getStrForUser).setId("cc_l41_getstringforuser").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object k = chain.getArg(1);
                    if ("use_control_panel".equals(k)) {
                        int n = L41_HIT.incrementAndGet();
                        if (n <= 8) {
                            L.log(4, TAG, "L41 HIT getStringForUser(use_control_panel) \u2192 " + want);
                        }
                        return String.valueOf(want);
                    }
                    return chain.proceed();
                });
                ++ok;
            }
            catch (Throwable t) {
                L.log(5, TAG, "L41 getStringForUser hook failed: " + t);
            }
            Method putInt = settingsSystem.getMethod("putInt", ContentResolver.class, String.class, Integer.TYPE);
            module.hook((Executable)putInt).setId("cc_l41_putint").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object k = chain.getArg(1);
                if ("use_control_panel".equals(k)) {
                    return Boolean.TRUE;
                }
                return chain.proceed();
            });
            ++ok;
            try {
                Method putString = settingsSystem.getMethod("putString", ContentResolver.class, String.class, String.class);
                module.hook((Executable)putString).setId("cc_l41_putstring").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object k = chain.getArg(1);
                    if ("use_control_panel".equals(k)) {
                        return Boolean.TRUE;
                    }
                    return chain.proceed();
                });
                ++ok;
            }
            catch (Throwable putString) {
                // empty catch block
            }
            try {
                Method putStrForUser = settingsSystem.getMethod("putStringForUser", ContentResolver.class, String.class, String.class, Integer.TYPE);
                module.hook((Executable)putStrForUser).setId("cc_l41_putstringforuser").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object k = chain.getArg(1);
                    if ("use_control_panel".equals(k)) {
                        return Boolean.TRUE;
                    }
                    return chain.proceed();
                });
                ++ok;
            }
            catch (Throwable putStrForUser) {
                // empty catch block
            }
            try {
                Method putIntForUser = settingsSystem.getMethod("putIntForUser", ContentResolver.class, String.class, Integer.TYPE, Integer.TYPE);
                module.hook((Executable)putIntForUser).setId("cc_l41_putintforuser").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object k = chain.getArg(1);
                    if ("use_control_panel".equals(k)) {
                        return Boolean.TRUE;
                    }
                    return chain.proceed();
                });
                ++ok;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            L.log(4, TAG, "L41 \u5df2\u63a5\u7ba1 use_control_panel=" + want + "\uff08\u5f3a\u5236" + (mode == -1 ? "\u7ecf\u5178" : "\u65b0\u7248") + "\uff0chook \u6570=" + ok + "\uff09");
            return ok > 0;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L41 failed: " + t);
            return false;
        }
    }

    public static boolean applyControlCenterMode(XposedModule module, ClassLoader cl) {
        int mode = CcHooks.readCcMode();
        if (mode == Integer.MIN_VALUE) {
            L.log(4, TAG, "L40 \u8ddf\u968f\u7cfb\u7edf\u8bbe\u7f6e\uff08\u4e0d\u5199\u5165 use_control_panel\uff09");
            return true;
        }
        int want = mode == -1 ? 0 : 1;
        try {
            Object cr = CcHooks.getCr();
            if (cr == null) {
                L.log(5, TAG, "L40 \u62ff\u4e0d\u5230 ContentResolver\uff0c\u65e0\u6cd5\u5199\u5165\u8bbe\u7f6e");
                return false;
            }
            Class<?> settingsSystem = Class.forName("android.provider.Settings$System");
            Object cur = settingsSystem.getMethod("getInt", ContentResolver.class, String.class, Integer.TYPE).invoke(null, cr, "use_control_panel", -1);
            int curInt = ((Number)cur).intValue();
            if (curInt == want) {
                L.log(4, TAG, "L40 use_control_panel \u5df2\u662f " + want + "\uff0c\u65e0\u9700\u5199\u5165");
                return true;
            }
            settingsSystem.getMethod("putInt", ContentResolver.class, String.class, Integer.TYPE).invoke(null, cr, "use_control_panel", want);
            Object after = settingsSystem.getMethod("getInt", ContentResolver.class, String.class, Integer.TYPE).invoke(null, cr, "use_control_panel", -1);
            L.log(4, TAG, "L40 use_control_panel " + curInt + " \u2192 " + ((Number)after).intValue() + "\uff08\u5f3a\u5236" + (want == 0 ? "\u7ecf\u5178" : "\u65b0\u7248") + "\uff09");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L40 \u5199 use_control_panel \u5931\u8d25: " + t);
            return false;
        }
    }

    public static boolean installControlCenterUnlock(XposedModule module, ClassLoader cl) {
        int mode = CcHooks.readCcMode();
        if (mode == Integer.MIN_VALUE) {
            L.log(4, TAG, "L40 \u8ddf\u968f\u7cfb\u7edf\u8bbe\u7f6e\uff08\u4e0d\u62e6\u622a\u63a7\u5236\u4e2d\u5fc3\u6837\u5f0f\uff09");
            return true;
        }
        boolean wantClassic = mode == -1;
        int ret = wantClassic ? -1 : 1;
        try {
            Class<?> sp = Class.forName("android.os.SystemProperties");
            Method m = null;
            for (Method cand : sp.getDeclaredMethods()) {
                if (!"getInt".equals(cand.getName()) || cand.getParameterCount() != 2 || cand.getParameterTypes()[0] != String.class || cand.getParameterTypes()[1] != Integer.TYPE) continue;
                m = cand;
                break;
            }
            if (m == null) {
                L.log(5, TAG, "L40 SystemProperties.getInt(String,int) \u672a\u627e\u5230");
                return false;
            }
            m.setAccessible(true);
            module.hook((Executable)m).setId("cc_l40_prop_getint").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object k = chain.getArg(0);
                if (k instanceof String && PROP_USE_CC.equals(k)) {
                    return ret;
                }
                return chain.proceed();
            });
            L.log(4, TAG, "L40 \u63a7\u5236\u4e2d\u5fc3\u6837\u5f0f\u9501\u5b9a\u4e3a " + (wantClassic ? "\u7ecf\u5178" : "\u65b0\u7248") + "\uff08hook SystemProperties.getInt\uff0c\u65e0\u9700 KernelSU\uff09");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L40 hook failed: " + t);
            return false;
        }
    }

    public static void installTracerOnly(XposedModule module, ClassLoader cl) {
        boolean ok = CcHooks.hookGlassCallTracer(module, cl);
        Health.begin();
        Health.report();
        L.log(4, TAG, "installTracerOnly: L39=" + ok + "\uff08\u4e0d\u5e72\u9884\u4efb\u4f55\u89c6\u89c9\uff0c\u4ec5\u91c7\u96c6\u53c2\u6570\uff09");
    }

    public static void install(XposedModule module, ClassLoader cl, boolean enableGlass) {
        Health.begin();
        boolean l42;
        boolean l41 = CcHooks.hookControlCenterFlow(module, cl);
        if (l41) {
            L.log(4, TAG, "L41 installed");
        }
        if (l42 = CcHooks.hookIsUseControlCenter(module, cl)) {
            L.log(4, TAG, "L42 installed");
        }
        if (CcHooks.isCaptureMode()) {
            boolean l39c = CcHooks.hookGlassCallTracer(module, cl);
            L.log(4, TAG, "\u2605\u2605 CAPTURE MODE \u2605\u2605 \u5df2\u8df3\u8fc7\u5168\u90e8\u8986\u5199\u5c42\uff0c\u53ea\u505a\u53c2\u6570\u91c7\u96c6 L39=" + l39c);
            return;
        }
        String mat = CcHooks.getProp("debug.hyperclassic.material", "glass").trim();
        boolean forceGlass = enableGlass && !"blur".equalsIgnoreCase(mat);
        // ★ v10.2：区域闸门。关掉的区域直接不装 hook（比运行时跳过更省）。
        boolean rNotif = CcHooks.regionOn(REGION_NOTIF);
        boolean rQs = CcHooks.regionOn(REGION_QS);
        boolean rSlider = CcHooks.regionOn(REGION_SLIDER);
        boolean l1 = rNotif ? CcHooks.hookIsGlassMaterialType(module, cl, forceGlass) : false;
        boolean l2 = rNotif ? CcHooks.hookMaterialTypeLambda(module, cl, forceGlass) : false;
        boolean l3 = rNotif ? CcHooks.hookCombineWrite(module, cl, forceGlass) : false;
        boolean l4 = rNotif ? CcHooks.hookGlassEffectApply(module, cl, enableGlass) : false;
        if (rNotif) {
            CcHooks.hookGlassEffectApplyHook(module, cl, enableGlass);
        }
        boolean l4b = rNotif ? CcHooks.hookRowBlurEffect(module, cl, enableGlass) : false;
        boolean l5 = CcHooks.hookOtherGlassEffects(module, cl, enableGlass);
        boolean l13 = rNotif ? CcHooks.hookEnsureOutlineProvider(module, cl, enableGlass) : false;
        boolean l14 = rNotif ? CcHooks.hookEffectMap(module, cl, forceGlass) : false;
        boolean l15 = rNotif ? CcHooks.hookBlendColors(module, cl, forceGlass) : false;
        boolean l16 = CcHooks.hookMediaBlurEffect(module, cl, enableGlass);
        boolean l17 = rNotif ? CcHooks.hookGlassParams(module, cl, enableGlass) : false;
        // v9.66 blur gate 已禁用（回滚到 v9.65 行为）// CcHooks.installRgBlurGuard(module, cl);
        // CcHooks.installRgClearGuard(module, cl);
        // ★ v9.83：通知背景 passWindowBlur 闸门（L17 VERIFY 锁定磨砂源后新增）
        boolean lpass = rNotif ? CcHooks.installNotifPassGate(module, cl) : false;
        boolean l18 = CcHooks.hookBackgroundProbe(module, cl, enableGlass);
        boolean l19 = rQs ? CcHooks.hookQsMaterial(module, cl, enableGlass) : false;
        boolean l20 = CcHooks.hookBlurCallProbe(module, cl);
        boolean l21 = rQs ? CcHooks.hookTileMaterial(module, cl) : false;
        boolean l22 = rQs ? CcHooks.hookQsDarkResources(module, cl) : false;
        boolean l23 = CcHooks.hookColorProbe(module, cl);
        boolean l24 = CcHooks.hookNotifText(module, cl);
        boolean l25 = CcHooks.hookNotifTextColor(module, cl);
        boolean l26 = rSlider ? CcHooks.hookSliderOnDraw(module, cl) : false;
        boolean l27 = rSlider ? (CcHooks.PROBES_ON && CcHooks.hookSliderProbe(module, cl)) : false;
        boolean l28 = rSlider ? CcHooks.hookSliderAllInstances(module, cl) : false;
        boolean l29 = CcHooks.hookNotifRowDraw(module, cl);
        boolean disableL30 = "1".equals(CcHooks.getProp("debug.hyperclassic.disable_l30", "0").trim());
        boolean disableL33 = "1".equals(CcHooks.getProp("debug.hyperclassic.disable_l33", "0").trim());
        boolean l30 = disableL30 ? false : CcHooks.hookNotifIconTint(module, cl);
        boolean l31 = CcHooks.hookTileIndicatorTint(module, cl);
        boolean l32 = CcHooks.hookPageIndicator(module, cl);
        boolean l33 = disableL33 ? false : CcHooks.hookNotifHeaderIcons(module, cl);
        boolean l34 = CcHooks.regionOn(REGION_BUTTON) ? CcHooks.hookExpandButtonGlass(module, cl) : false;
        boolean l35 = CcHooks.hookDismissIcon(module, cl);
        boolean l36 = CcHooks.regionOn(REGION_BUTTON) ? CcHooks.hookExpandButtonCircle(module, cl) : false;
        boolean l37 = CcHooks.regionOn(REGION_MEDIA) ? CcHooks.hookMediaViewGlass(module, cl, enableGlass) : false;
        // ★ v142：Toast 重新启用 —— 改走「反射玻璃」路线。
        //
        //  【v10.11.0 为什么回退】
        //    当时走的是「窗口模糊 PassBlur」路线：
        //      flags |= FLAG_BLUR_BEHIND + lp.blurBehindRadius = 80
        //    实测 TYPE_TOAST 窗口的 PassBlur 拿不到采样源
        //    （include 0 / mergeSnapshot 直接 return），必然失败。
        //    研究记录见 docs/TOAST_RESEARCH.md。
        //
        //  【v142 换的思路】
        //    不再依赖窗口级 PassBlur，改走 MiGlass 的 View 级材质，
        //    并把参数从「媒体卡片配方」换成「悬浮通知配方」。
        //    参考目标由主人指定：悬浮通知同样是独立窗口里的玻璃实例。
        //    两者关键参数差异（正是「挂上但过于透明」的原因）：
        //      p1  0.16 → 1.0   （不透明度）
        //      p3  0.0  → 0.8   （底色强度）
        //      p14 0.1  → 1.5   （边缘增强）
        //      p19 72   → 62    （模糊半径）
        boolean l44 = CcHooks.regionOn(REGION_TOAST) && CcHooks.hookToastGlass(module, cl, enableGlass);
        boolean l39 = CcHooks.hookGlassCallTracer(module, cl);
        boolean l45 = CcHooks.hookSuppressFold(module, cl);
        boolean l46 = CcHooks.hookNotifBgBacking(module, cl);
        boolean l47 = CcHooks.hookBlurRadiusOverride(module, cl);
        // ★ v166：hook 健康登记 —— 失败项写入 Settings.System，设置页渲染自检卡片。
        //   region 关闭导致的「未安装」不算失败；探针层（L18/L20/L23/L27/L39）不参与。
        if (rNotif) {
            Health.item("L1", l1);
            Health.item("L2", l2);
            Health.item("L3", l3);
            Health.item("L4", l4);
            Health.item("L4b", l4b);
            Health.item("L13", l13);
            Health.item("L14", l14);
            Health.item("L15", l15);
            Health.item("L17", l17);
            Health.item("Lpass", lpass);
        }
        Health.item("L5", l5);
        Health.item("L16", l16);
        if (rQs) {
            Health.item("L19", l19);
            Health.item("L21", l21);
            Health.item("L22", l22);
        }
        if (rSlider) {
            Health.item("L26", l26);
            Health.item("L28", l28);
        }
        Health.item("L24", l24);
        Health.item("L25", l25);
        Health.item("L29", l29);
        if (!disableL30) {
            Health.item("L30", l30);
        }
        Health.item("L31", l31);
        Health.item("L32", l32);
        if (!disableL33) {
            Health.item("L33", l33);
        }
        if (CcHooks.regionOn(REGION_BUTTON)) {
            // L34 已废弃（被 L36 接管，hookExpandButtonGlass 硬编码 return false），
            // 不是失败，不参与健康登记。
            Health.item("L36", l36);
        }
        Health.item("L35", l35);
        if (CcHooks.regionOn(REGION_MEDIA)) {
            Health.item("L37", l37);
        }
        if (CcHooks.regionOn(REGION_TOAST)) {
            Health.item("L44", l44);
        }
        Health.item("L41", l41);
        Health.item("L42", l42);
        Health.item("L45", l45);
        Health.item("L46", l46);
        Health.item("L47", l47);
        Health.report();
        L.log(4, TAG, "material=" + mat + " forceGlass=" + forceGlass);
        L.log(4, TAG, "regions: notif=" + regionOn(REGION_NOTIF) + " headsup=" + regionOn(REGION_HEADSUP)
                + " media=" + regionOn(REGION_MEDIA) + " button=" + regionOn(REGION_BUTTON)
                + " qs=" + regionOn(REGION_QS) + " slider=" + regionOn(REGION_SLIDER)
                + " toast=" + regionOn(REGION_TOAST));
        L.log(4, TAG, "install result: L1(isGlass)=" + l1 + " L2(stateFlow)=" + l2 + " L3(field)=" + l3 + " L4(rowBlur)=" + l4 + " L4b(rowBlurEffect)=" + l4b + " L5(others)=" + l5 + " L13(outline)=" + l13 + " L14(effectMap)=" + l14 + " L15(blendColor)=" + l15 + " L16(mediaBlur)=" + l16 + " L17(glassParams)=" + l17 + " L18(bgProbe)=" + l18 + " L19(qsMaterial)=" + l19 + " L20(blurProbe)=" + l20 + " L21(tileMaterial)=" + l21 + " L22(qsDarkRes)=" + l22 + " L23(colorProbe)=" + l23 + " L24(notifText)=" + l24 + " L25(notifColor)=" + l25 + " L26(sliderDraw)=" + l26 + " L27(sliderProbe)=" + l27 + " L28(sliderAll)=" + l28 + " L29(notifDraw)=" + l29 + " L30(notifIcon)=" + l30 + " L31(tileInd)=" + l31 + " L32(pageInd)=" + l32 + " L33(notifHdr)=" + l33 + " L34(expGlass)=" + l34 + " L35(dismiss)=" + l35 + " L36(expCircle)=" + l36 + " L37(mediaGlass)=" + l37 + " L44(toast)=" + l44 + " L45(noFold)=" + l45 + " L46(backing)=" + l46 + " L47(blurOverride)=" + l47 + " enableGlass=" + enableGlass);

        // ★ Split shade 层（v280）—— 全部默认开启，各自独立 prop 可运行时热关闭
        //
        //   L0 SplitForceFlag         split_force_flag        hook Resources.getBoolean(0x7f05008c)，
        //                                                      横屏返回 true。这是唯一总闸，同时覆盖
        //                                                      旧的 mSplitShadeEnabled 字段路径与
        //                                                      新的 ShadeMode Flow 路径。
        //   L1 SplitShadeActivation   split_enable            旋转后重跑官方 updateResources$1 链，
        //                                                      避免 mSplitShadeEnabled 固化在旋转前的值。
        //   L2 SplitScrollBridge      split_scroll            通知列表可滚动（触摸补丁）。
        //   L3 StackViewGuard         split_view_guard        拦默认变换写入。
        //   L4 SplitTopAlignBridge    split_top_align(+_extra) 通知列顶部对齐 QS 托盘。
        //   L5 ShadeQsHeightFix       split_qs_height_fix     mExpansionHeight 卡 0 修复。
        //
        //   证据链见 docs/SPLIT_SHADE_TRUTH_20261122.md
        CcHooks.installSplitLayers(module, cl);
    }

    /**
     * 安装 split shade 的 6 层修复（v280）。
     *
     * 每一层独立 try/catch，任一层失败不影响其他层，也不会拖垮整个模块。
     */
    private static void installSplitLayers(XposedModule module, ClassLoader cl) {
        // 全部用 L.always()：这几条必须出现在 logcat 里。
        // 历史上这里用 L.log(2, ...) = VERBOSE，一旦日志闸门关闭就完全静默，
        // 直接导致「SplitShadeActivation 到底装没装」这个问题排查了两天。
        int ok = 0;

        long t0 = android.os.SystemClock.elapsedRealtime();
        try {
            SplitForceFlag.install(module, cl);
            ok++;
            L.always(TAG, "L0 SplitForceFlag OK");
        } catch (Throwable t) {
            L.always(TAG, "L0 SplitForceFlag FAILED: " + t);
        }
        try {
            SplitShadeActivation.install(module, cl);
            ok++;
            L.always(TAG, "L1 SplitShadeActivation OK");
        } catch (Throwable t) {
            L.always(TAG, "L1 SplitShadeActivation FAILED: " + t);
        }
        try {
            SplitScrollBridge.install(module, cl);
            ok++;
            L.always(TAG, "L2 SplitScrollBridge OK");
        } catch (Throwable t) {
            L.always(TAG, "L2 SplitScrollBridge FAILED: " + t);
        }
        try {
            StackViewGuard.install(module, cl);
            ok++;
            L.always(TAG, "L3 StackViewGuard OK");
        } catch (Throwable t) {
            L.always(TAG, "L3 StackViewGuard FAILED: " + t);
        }
        try {
            SplitTopAlignBridge.install(module, cl);
            ok++;
            L.always(TAG, "L4 SplitTopAlignBridge OK");
        } catch (Throwable t) {
            L.always(TAG, "L4 SplitTopAlignBridge FAILED: " + t);
        }
        try {
            ShadeQsHeightFix.install(module, cl);
            ok++;
            L.always(TAG, "L5 ShadeQsHeightFix OK");
        } catch (Throwable t) {
            L.always(TAG, "L5 ShadeQsHeightFix FAILED: " + t);
        }
        // ★ v331：L2b SplitRowAlphaGuard 已彻底删除。
        //
        // 删除原因（主人 2026-11-24 实测定调）：
        //   这一层会强行把通知行的 alpha 拉到 1.0、把 ViewState.hidden 清成 false，
        //   于是「下拉手势第一帧就已经满亮且可见」——
        //   官方那套自洽的逐行淡入动画被整段跳过，
        //   表现为「横屏下拉时通知立刻全部弹出，然后播回弹动画」。
        //
        //   Coding酱用 `debug.hyperclassic.split_row_alpha=0` 单独停用它后：
        //     · 通知淡入动画完全正常；
        //     · 竖屏 QS 隐藏态转横屏的问题也不再复现。
        //
        //   历史背景：它当初（v281）是为了修「横屏右侧通知列整片空白」，
        //   而那个空白真正的原因是 classicQsExpansionAlpha=0
        //   （现已由 SplitSncTransitionAlpha 在数据源层修好），
        //   所以这一层从一开始就是对症错误的补丁。
        //
        //   【永久禁令】绝不再 hook View.setAlpha / ViewState.mAlpha / ViewState.hidden
        //   去「保证」通知可见。它们是逐帧动画的载体，改它们等于掐死动画。
        try {
            SplitClipBoundsFix.install(module, cl);
            ok++;
            L.always(TAG, "L2c SplitClipBoundsFix OK");
        } catch (Throwable t) {
            L.always(TAG, "L2c SplitClipBoundsFix FAILED: " + t);
        }

        // ★ v287 回滚：L1b SplitStateMigration 已移除。
        //
        // 移除原因（主人实测反馈）：
        //   v286 主动调用 QuickSettingsControllerImpl.updateExpansion() 重算 qsExpandFraction，
        //   虽然把"卡在 0.2285 长达 5 秒"缩短到了 2.5 秒，但引入了一个新问题：
        //   转竖屏后下拉 QS，托盘会先显示为完整态，然后立刻回弹到紧凑视图。
        //
        //   根因是 updateExpansion() 是同一个共享状态机的强制重算入口，
        //   而横竖屏本来就该有各自独立的状态。用"强制重算"去修补，
        //   等于把横屏的中间态硬塞进竖屏的状态机，反而制造了新的非法迁移。
        //
        // 正确的方向（待实现）：
        //   让 split 横屏拥有**独立**的 QS/通知状态机实例，
        //   而不是在共享状态机上做迁移修补。详见 docs/SPLIT_SHADE_TRUTH_20261122.md。
        //
        // 如需重新试验，把 debug.hyperclassic.split_state_migrate 设为 1 并
        // 取消下面这行的注释即可（代码保留在 SplitStateMigration.java 里）。
        //
        // try {
        //     SplitStateMigration.install(module, cl);
        //     ok++;
        //     L.always(TAG, "L1b SplitStateMigration OK");
        // } catch (Throwable t) {
        //     L.always(TAG, "L1b SplitStateMigration FAILED: " + t);
        // }

        // ★ v287：恢复 SplitLayoutBridge（状态栏/header 归属 + 清除按钮位置）
        //   这是主人确认的「独立安全补丁」。
        //   注意：该补丁内部有 useCC==false 门控（只在经典控制中心生效），
        //   新版控制中心下会自行跳过，不会造成副作用。
        try {
            SplitLayoutBridge.install(module, cl);
            ok++;
            L.always(TAG, "L6 SplitLayoutBridge OK");
        } catch (Throwable t) {
            L.always(TAG, "L6 SplitLayoutBridge FAILED: " + t);
        }

        // ★ v296：L6b SplitSncTransitionAlpha —— 修「横屏通知列完全空白」的最终根因。
        //
        //   probe_18（Frida 绘制链）证明：NSSL 正常、所有 ExpandableNotificationRow
        //   都被正常绘制（vis=0 a=1 tA=1），唯一异常是 SharedNotificationContainer
        //   自己的 transitionAlpha=0 → HWUI 合成这一层时整体乘 0 → 整片黑。
        //
        //   probe_19 把它强顶到 1 + OCR 截图 → 通知全部出现（根因 100% 确认）。
        //
        //   tA 的算法（SharedNotificationContainerViewModelInjector）：
        //       sncTransitionAlpha = min(keyguardInfoLayerAlpha, blurRatioAlpha,
        //                                keyguardPanelMoveAlpha, classicQsExpansionAlpha,
        //                                fullAodLinkageAlpha)
        //   实测只有 classicQsExpansionAlpha=0（其余都是 1.0），
        //   而它 = qsExpansion，在 split 横屏下被官方逻辑恒置 0。
        try {
            SplitSncTransitionAlpha.install(module, cl);
            ok++;
            L.always(TAG, "L6b SplitSncTransitionAlpha OK");
        } catch (Throwable t) {
            L.always(TAG, "L6b SplitSncTransitionAlpha FAILED: " + t);
        }
// ★ v299：L6c SplitRotationReset —— 旋转后立刻重算一次通知堆叠。
        //   主人反馈：竖横屏切换后通知布局严重错乱，必须滑动一下才恢复。
        //   根因是官方 onConfigurationChanged 不会重算每行的 ViewState，
        //   而 split 下我们改了 NSSL 父级几何，旋转瞬间不会传导到每一行。
        try {
            SplitRotationReset.install(module, cl);
            ok++;
            L.always(TAG, "L6c SplitRotationReset OK");
        } catch (Throwable t) {
            L.always(TAG, "L6c SplitRotationReset FAILED: " + t);
        }

        // ★ v338：L7 SplitClockInject —— 横屏 split 左栏注入状态栏小时钟。
        //
        // 主人需求：竖屏 QS 顶部有大时钟，横屏 split 左栏却什么都没有。
        //
        // 溯源（全部有 xml/smali 证据）：
        //   官方 miui_ns_qs_header_view.xml 里本来就有一个横屏小时钟
        //   @id/horizontal_date_time（13.45dp，= status_bar_clock_size），
        //   而且 MiuiQSHeaderView.updateCarrierVisibility() 会在横屏
        //   把它 setPolicyVisibility(VISIBLE)。
        //
        //   但 split shade 下整个 MiuiQSHeaderView 是 INVISIBLE，
        //   所以官方那条路径失效，时钟无论如何都看不见。
        //
        // 做法（主人定的方案 B）：新建一个 MiuiClock 实例，
        //   直接调用系统的时钟规则（onAttachedToWindow 会自动注册到
        //   MiuiStatusBarClockController，时间/深色全部自动跟随），
        //   挂到横屏左栏 QSContent 顶部。
        //
        // 注意：竖屏会主动摘掉，因为竖屏有官方大时钟，不能重复。
        try {
            SplitClockInject.install(module, cl);
            ok++;
            L.always(TAG, "L7 SplitClockInject OK");
        } catch (Throwable t) {
            L.always(TAG, "L7 SplitClockInject FAILED: " + t);
        }


        // ★ v287：恢复 SplitStackRefreshBridge，并修正 fraction 判断。
        //   原版依赖 mShadeExpandedFraction >= 0.90f，但 split 下该值恒为 0.0（V274 事实 B），
        //   导致整个 refresh 从未执行 —— 这是「通知不显示」的核心嫌疑之一。
        //   现已改为用 mExpansionHeight/mMaxExpansionHeight + mTransitioningToFullShadeProgress 判定。
        try {
            SplitStackRefreshBridge.install(module, cl);
            ok++;
            L.always(TAG, "L7 SplitStackRefreshBridge OK (split-aware expanded check)");
        } catch (Throwable t) {
            L.always(TAG, "L7 SplitStackRefreshBridge FAILED: " + t);
        }

        long dt = android.os.SystemClock.elapsedRealtime() - t0;
        L.always(TAG, "split layers installed: " + ok + "/12 in " + dt + "ms");
        L.always(TAG, "landscapeNow=" + diagnosticLandscapeNow()
                + " forceFlag=" + getProp("debug.hyperclassic.split_force_flag", "1")
                + " enable=" + getProp("debug.hyperclassic.split_enable", "1"));
    }

    /**
     * ★★★ 区域闸门（v10.2）★★★
     *
     * 【为什么需要】主人反馈「前端选项大部分是死代码」，要求改成
     *   「只控制哪个区域的玻璃是否开关」。
     *
     *   之前前端只有一个「解锁玻璃材质」开关，而且只写进 SharedPreferences，
     *   SystemUI 侧从来没人读 → 纯死代码。
     *
     * 【设计】每个区域一个 debug 属性，默认 1（开）。CcHooks.install 在装
     *   各层 hook 前先问一次，关掉就直接不装那一层的 hook（真正的关闭，
     *   不是运行时跳过 —— 少装 hook 也省性能）。
     *
     *   属性名：debug.hyperclassic.region_<name>
     *   取值：1 = 开（默认），0 = 关
     */
    public static boolean regionOn(String name) {
        return !"0".equals(CcHooks.getProp("debug.hyperclassic.region_" + name, "1").trim());
    }

    /** 区域清单（前端必须与此保持一致，见 ConfigStore.REGIONS）。 */
    public static final String REGION_NOTIF = "notif";       // 通知卡片
    public static final String REGION_HEADSUP = "headsup";   // 悬浮通知
    public static final String REGION_MEDIA = "media";       // 媒体卡片
    public static final String REGION_BUTTON = "button";     // 通知内按钮（展开/收起）
    public static final String REGION_QS = "qs";             // 控制中心开关磁贴
    public static final String REGION_SLIDER = "slider";     // 亮度条
    public static final String REGION_TOAST = "toast";       // Toast

    // ★ v1.0.2 性能修复：getProp 原实现每次 Class.forName + getMethod（反射三连），
    //   而 L20/L39 探针在每次 MiBlur/MiGlass API 调用时都会读属性 ——
    //   长按菜单动画期间每帧 blend × 多层探针 × 每次全反射 = 主线程被拖到 0-1 帧。
    //   修复：Method 查找结果静态缓存，调用只剩 invoke。
    private static volatile Method SP_GET = null;
    private static boolean SP_INITED = false;

    // ★ v78 功耗优化（方案A-1）：getProp 结果 TTL 缓存。
    //   【为什么】SystemProperties.get 走 JNI + Method.invoke 反射，全项目 114 处调用，
    //     其中媒体轮询/绘制热路径每轮要调 6+ 次 → 一分钟数百次反射。
    //     配置项不会亚秒级变化，1 秒 TTL 足以保证调试时改 prop 秒级生效。
    private static final long PROP_CACHE_TTL_MS = 1000L;
    private static final java.util.Map<String, Object[]> PROP_CACHE =
            new java.util.concurrent.ConcurrentHashMap<String, Object[]>(); // key -> {String value, Long ts}

    private static String getPropCached(String key, String def) {
        try {
            long now = System.currentTimeMillis();
            Object[] e = PROP_CACHE.get(key);
            if (e != null && (now - ((Long) e[1]).longValue()) < PROP_CACHE_TTL_MS) {
                return (String) e[0];
            }
            String v = getPropUncached(key, def);
            PROP_CACHE.put(key, new Object[]{v, Long.valueOf(now)});
            return v;
        } catch (Throwable t) {
            return def;
        }
    }

    private static String getPropUncached(String key, String def) {
        try {
            Method get = SP_GET;
            if (get == null) {
                synchronized (CcHooks.class) {
                    if (SP_GET == null) {
                        Class<?> sp = Class.forName("android.os.SystemProperties");
                        Method m = sp.getMethod("get", String.class, String.class);
                        m.setAccessible(true);
                        SP_GET = m;
                    }
                    get = SP_GET;
                }
            }
            Object o = get.invoke(null, key, def);
            return o == null ? def : o.toString();
        }
        catch (Throwable t) {
            return def;
        }
    }

    // ★ v78：原 getProp 改为带 TTL 缓存的入口（114 处调用自动受益，无需逐个改）
    static String getProp(String key, String def) {
        return getPropCached(key, def);
    }

    /**
     * ★ v1.0.2：探针专用属性开关（1.5s 缓存窗口）。
     *   探针拦截器每次调用不再走反射，只读一个 volatile。
     */
    private static volatile long PROBE_CHECK_AT = 0L;
    private static volatile boolean PROBE_ON = false;

    private static boolean probeOn() {
        long now = SystemClock.elapsedRealtime();
        if (now - PROBE_CHECK_AT > 1500L) {
            PROBE_CHECK_AT = now;
            PROBE_ON = "1".equals(getProp("debug.hyperclassic.blur_probe", "0").trim())
                    || "1".equals(getProp("debug.hyperclassic.capture", "0").trim());
        }
        return PROBE_ON;
    }

    private static boolean hookRowBlurEffect(XposedModule module, ClassLoader cl, boolean enableGlass) {
        try {
            Class<?> cls = Class.forName("com.android.systemui.statusbar.notification.style.vieweffect.NotificationRowBlurEffect", false, cl);
            Method target = null;
            for (Method m : cls.getDeclaredMethods()) {
                if (!"apply".equals(m.getName()) || m.getParameterCount() != 2 || m.isBridge()) continue;
                target = m;
                break;
            }
            if (target == null) {
                L.log(5, TAG, "L4b: apply not found");
                return false;
            }
            target.setAccessible(true);
            Class<?> rowCls = Class.forName(CLS_EXPANDABLE_ROW, false, cl);
            module.hook((Executable)target).setId("cc_l4b_blur_effect").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                String mat = CcHooks.getProp("debug.hyperclassic.material", "glass").trim();
                if ("glass".equalsIgnoreCase(mat)) {
                    return chain.proceed();
                }
                Object result = chain.proceed();
                if (!enableGlass) {
                    return result;
                }
                String lg = CcHooks.getProp("debug.hyperclassic.light_glass", "0").trim();
                try {
                    Object row = chain.getArg(0);
                    if (!rowCls.isInstance(row)) {
                        return result;
                    }
                    Object injector = CcHooks.invokeNoArg(row, "getInjector");
                    if (injector == null) {
                        return result;
                    }
                    Object bg = CcHooks.invokeNoArg(injector, "getBackgroundNormal");
                    if (!(bg instanceof View)) {
                        return result;
                    }
                    View v = (View)bg;
                    if ("blur".equalsIgnoreCase(mat) && !"0".equals(lg)) {
                        CcHooks.lightGlassOverlay(module, cl, v, lg);
                    } else {
                        CcHooks.applyBlurTo(v, CLS_GLASS_EFFECT, module, "RowBlurEff", cl);
                    }
                }
                catch (Throwable t) {
                    L.log(5, TAG, "L4b apply failed: " + t);
                }
                return result;
            });
            L.log(4, TAG, "L4b hooked NotificationRowBlurEffect.apply");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L4b hook failed: " + t);
            return false;
        }
    }

    private static boolean hookIsGlassMaterialType(XposedModule module, ClassLoader cl, boolean enableGlass) {
        try {
            Class<?> cls = Class.forName(CLS_INTERACTOR, false, cl);
            Method target = CcHooks.findNoArgBoolean(cls, M_IS_GLASS);
            if (target == null) {
                L.log(5, TAG, "L1: method not found: isGlassMaterialType");
                return false;
            }
            target.setAccessible(true);
            module.hook((Executable)target).setId("cc_l1_is_glass").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> enableGlass ? Boolean.TRUE : chain.proceed());
            L.log(4, TAG, "L1 hooked isGlassMaterialType");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L1 failed: " + t);
            return false;
        }
    }

    private static final AtomicInteger L2_COUNT = new AtomicInteger();
    private static boolean hookMaterialTypeLambda(XposedModule module, ClassLoader cl, boolean enableGlass) {
        try {
            Class<?> lambda = Class.forName(CLS_LAMBDA, false, cl);
            Class<?> mt = Class.forName(CLS_MATERIAL_TYPE, false, cl);
            Object GLASS = mt.getField("GLASS").get(null);
            Method target = CcHooks.findInvokeSuspend(lambda);
            if (target == null) {
                L.log(5, TAG, "L2: invokeSuspend not found");
                return false;
            }
            target.setAccessible(true);
            module.hook((Executable)target).setId("cc_l2_state_flow").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object result = chain.proceed();
                // ★★★ v9.79 诊断：确认 invokeSuspend 真的被调用、返回值是什么 ★★★
                int nn = L2_COUNT.incrementAndGet();
                if (nn <= 8) {
                    L.log(4, TAG, "L2 hit #" + nn + " result=" + result
                            + " cls=" + (result == null ? "null" : result.getClass().getName())
                            + " isMT=" + CcHooks.isMaterialType(result, mt));
                }
                if (enableGlass && CcHooks.isMaterialType(result, mt)) {
                    if (nn <= 8) {
                        L.log(4, TAG, "L2 force GLASS (was " + result + ")");
                    }
                    return GLASS;
                }
                return result;
            });
            L.log(4, TAG, "L2 hooked materialTypeState lambda");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L2 failed: " + t);
            return false;
        }
    }

    private static boolean hookEffectMap(XposedModule module, ClassLoader cl, boolean enableGlass) {
        try {
            Class<?> effectType = Class.forName("com.android.systemui.statusbar.notification.style.data.NotifEffectType", false, cl);
            Object GLASS_EFFECT_TYPE = effectType.getField("GLASS").get(null);
            // ★★★ v9.80 诊断：直接读 materialType 字段，确认 ViewBinder 看到的值 ★★★
            //   日志已知 L2 把 Flow 改成 GLASS，但 row glass 仍为 0。
            //   需要确认 ViewBinder 读的字段 materialType 到底是什么。
            // ★ release：探针默认禁用（debug.hyperclassic.probes=1 可重开）
            if (PROBES_ON) try {
                Class<?> interactor = Class.forName(CLS_INTERACTOR, false, cl);
                java.lang.reflect.Field fMat = interactor.getField("materialType");
                module.hook((Executable) Class.forName(
                                "com.android.systemui.statusbar.notification.style.view.ExpandableNotificationRowViewBinder", false, cl)
                        .getDeclaredMethod("bind", Class.forName("com.android.systemui.statusbar.notification.row.ExpandableNotificationRow", false, cl),
                                Class.forName("com.android.systemui.statusbar.notification.style.domain.NotificationMaterialStateInteractor$MaterialViewState", false, cl)))
                        .setId("cc_l14_bind_probe").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                        .intercept(chain -> {
                            try {
                                Object self = chain.getThisObject();
                                if (self != null) {
                                    java.lang.reflect.Field f = self.getClass().getDeclaredField("notificationMaterialStateInteractor");
                                    f.setAccessible(true);
                                    Object it = f.get(self);
                                    if (it != null) {
                                        int n2 = L2_COUNT.get();
                                        if (n2 <= 10) {
                                            L.log(4, TAG, "L14 PROBE materialType=" + fMat.get(it));
                                        }
                                    }
                                }
                            } catch (Throwable ignored) {
                            }
                            return chain.proceed();
                        });
                L.log(4, TAG, "L14 materialType probe installed");
            } catch (Throwable t2) {
                L.log(5, TAG, "L14 probe failed: " + t2);
            }
            return CcHooks.hookRowEffectResolve(module, cl, GLASS_EFFECT_TYPE, enableGlass);
        }
        catch (Throwable t) {
            L.log(5, TAG, "L14 failed: " + t);
            return false;
        }
    }

    private static boolean hookGlassEffectApplyHook(XposedModule module, ClassLoader cl, boolean enableGlass) {
        try {
            Class<?> glassEffect = Class.forName(CLS_GLASS_EFFECT, false, cl);
            Method target = null;
            for (Method m : glassEffect.getDeclaredMethods()) {
                if (!"apply".equals(m.getName()) || m.getParameterCount() != 2 || m.isBridge()) continue;
                target = m;
                break;
            }
            if (target == null) {
                L.log(5, TAG, "RG glass apply hook: not found");
                return false;
            }
            target.setAccessible(true);
            module.hook((Executable) target)
                    .setId("cc_rg_glass_apply_hook")
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        Object result = chain.proceed();
                        try {
                            if (!rgOn("rg_notif")) return result;
                            Object row = chain.getArg(0);
                            if (row == null) return result;
                            Object injector = CcHooks.invokeNoArg(row, "getInjector");
                            Object bg = injector == null ? null
                                    : CcHooks.invokeNoArg(injector, "getBackgroundNormal");
                            if (!(bg instanceof View)) return result;
                            View bgV = (View) bg;
                            if (bgV.getWidth() <= 0 || bgV.getHeight() <= 0) return result;
                            int n = RG_GLASS_APPLY_HIT.incrementAndGet();
                            if (n <= 20) {
                                L.log(4, TAG, "RG glass apply hook #" + n
                                        + " night=" + CcHooks.isNightMode(bgV)
                                        + " size=" + bgV.getWidth() + "x" + bgV.getHeight());
                            }
                            applyReflectiveGlass(module, bgV, bgV.getClass().getClassLoader(),
                                    "L4b notif RG", -1f);
                        } catch (Throwable t) {
                            L.log(5, TAG, "RG glass apply hook failed: " + t);
                        }
                        return result;
                    });
            L.log(4, TAG, "RG glass apply hook installed");
            return true;
        } catch (Throwable t) {
            L.log(5, TAG, "RG glass apply hook install failed: " + t);
            return false;
        }
    }

    private static final java.util.concurrent.atomic.AtomicInteger RG_GLASS_APPLY_HIT =
            new java.util.concurrent.atomic.AtomicInteger(0);

    private static boolean hookRowEffectResolve(XposedModule module, ClassLoader cl, Object glassEffectType, boolean enableGlass) {
        boolean eg = enableGlass;
        try {
            Class<?> blurEffect = Class.forName("com.android.systemui.statusbar.notification.style.vieweffect.NotificationRowBlurEffect", false, cl);
            Object fGlass = Class.forName(CLS_GLASS_EFFECT, false, cl).getField("INSTANCE").get(null);
            Method target = null;
            for (Method m : blurEffect.getDeclaredMethods()) {
                if (!"apply".equals(m.getName()) || m.getParameterCount() != 2 || m.isBridge()) continue;
                target = m;
                break;
            }
            if (target == null) {
                L.log(5, TAG, "L14: blur apply not found");
                return false;
            }
            target.setAccessible(true);
            Method glassApplyReal = Class.forName(CLS_GLASS_EFFECT, false, cl).getMethod("apply", Object.class, Context.class);
            glassApplyReal.setAccessible(true);
            module.hook((Executable)target).setId("cc_l14_forward").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                if (CcHooks.isCaptureMode()) {
                    return chain.proceed();
                }
                // ★★★ v9.78 方案B：materialType 已被 L2 改成 GLASS 时，
                //     官方根本不会调用 BlurEffect，这个 hook 也就不会进。
                //     如果还进来了，说明 L2 没生效（比如非经典 CC 场景），
                //     此时才需要 L14 转发兜底。
                //     开关 rg_l14_forward=0 可完全关闭（完全交给方案B）。
                if (!"1".equals(CcHooks.getProp("debug.hyperclassic.rg_l14_forward", "0").trim())) {
                    return chain.proceed();
                }
                if (!eg || fGlass == null) {
                    return chain.proceed();
                }
                if (Boolean.TRUE.equals(IN_GLASS_APPLY.get())) {
                    return chain.proceed();
                }
                String mat = CcHooks.getProp("debug.hyperclassic.material", "glass").trim();
                if ("blur".equalsIgnoreCase(mat)) {
                    return chain.proceed();
                }
                Object row = chain.getArg(0);
                Object ctx = chain.getArg(1);
                try {
                    int n;
                    Object bg;
                    IN_GLASS_APPLY.set(Boolean.TRUE);
                    glassApplyReal.invoke(fGlass, row, ctx);
                    if (CcHooks.isHeadsUpRow(row, module)) {
                        int nh = EFFECTFWD_COUNT.incrementAndGet();
                        if (nh <= 4) {
                            L.log(4, TAG, "L14 skip headsup (official glass)");
                        }
                        Object var10_12 = null;
                        return var10_12;
                    }
                    Object injector = CcHooks.invokeNoArg(row, "getInjector");
                    Object object = bg = injector == null ? null : CcHooks.invokeNoArg(injector, "getBackgroundNormal");
                    if (bg instanceof View) {
                        View bgV = (View)bg;
                        bgV.post(() -> {
                            int n2;
                            ViewOutlineProvider p;
                            int rowBlur = CcHooks.currentBlurRadius();
                            // ★★★ 2026-11-01 14:26 通知卡片改用「反射玻璃」配方（媒体 style=3 同款）★★★
                            //
                            //  【为什么】
                            //    主人：「现在的渲染模式正确」（media_style=3）
                            //    → 通知卡片套同一套 6 步配方（零模糊 + 资源数组 42 参数）。
                            //
                            //  【圆角】
                            //    从 bgV 自己的 outline 读（通知分组首/中/尾圆角不同）。
                            //    拿不到就用 88px（官方 notification_item_bg_radius）。
                            //  开关：debug.hyperclassic.rg_notif
                            if (rgOn("rg_notif")) {
                                // ★★★ v9.74：先预约打标，让后面的 applyBlurScaleOnly 直接跳过 ★★★
                                CcHooks.rgReserve(bgV);
                                // 圆角传 -1：保留官方 outline（通知分组首/中/尾形状不同）
                                applyReflectiveGlass(module, bgV, cl, "L14 notif RG", -1f);
                            }
                            // ★★★ v9.74：RG 接管时不再执行旧模糊链路（源头掐断）★★★
                            //
                            //  【官方源码实证】NotificationRowGlassEffect.apply ：
                            //    BlurEffect.apply（透明背景 + blend + setMiViewBlurModeCompat(1)）
                            //    → setMiViewMaterialTypeCompat(1)
                            //    → setMiGlassCompat(42项)
                            //    全程不碰 bgMode / bgRadius / passWindowBlur。
                            //
                            //  【为什么不在这里 return 而是让函数自己判断】
                            //    applyBlurScaleOnly 入口已有 rgOwned() 检查，
                            //    保持单一判断点，方便日后调试。
                            boolean doubleGlass = "1".equals(CcHooks.getProp("debug.hyperclassic.double_glass", "0").trim());
                            if (doubleGlass) {
                                CcHooks.applyUnifiedGlass(module, bgV, cl, "L14 bgGlass", rowBlur, true);
                            } else {
                                CcHooks.applyBlurScaleOnly(module, bgV, cl, "L14 bgGlass", rowBlur);
                            }
                            if ("1".equals(CcHooks.getProp("debug.hyperclassic.fake_bg", "0").trim())) {
                                try {
                                    try {
                                        Field f = null;
                                        for (Class<?> c = bgV.getClass(); c != null && f == null; c = c.getSuperclass()) {
                                            try {
                                                f = c.getDeclaredField("mBackground");
                                                continue;
                                            }
                                            catch (Throwable throwable) {
                                                // empty catch block
                                            }
                                        }
                                        if (f != null) {
                                            f.setAccessible(true);
                                            f.set(bgV, null);
                                            int nk = FAKEBG_COUNT.get();
                                            if (nk <= 6) {
                                                L.log(4, TAG, "L14 fakeBg mBackground=null OK");
                                            }
                                        } else {
                                            L.log(5, TAG, "L14 fakeBg mBackground field not found");
                                        }
                                    }
                                    catch (Throwable t) {
                                        L.log(5, TAG, "L14 fakeBg clear mBackground failed: " + t);
                                    }
                                    GradientDrawable gd = new GradientDrawable();
                                    gd.setShape(0);
                                    gd.setColor(0xDFFFFFF);
                                    float rad = 0.0f;
                                    try {
                                        Outline o = new Outline();
                                        p = bgV.getOutlineProvider();
                                        if (p != null) {
                                            p.getOutline(bgV, o);
                                            rad = o.getRadius();
                                        }
                                    }
                                    catch (Throwable o) {
                                        // empty catch block
                                    }
                                    if (rad <= 0.0f) {
                                        rad = 88.0f;
                                    }
                                    gd.setCornerRadius(rad);
                                    bgV.setBackground((Drawable)gd);
                                    bgV.invalidate();
                                    int nf = FAKEBG_COUNT.incrementAndGet();
                                    if (nf <= 6) {
                                        L.log(4, TAG, "L14 fakeBg r=" + (int)rad + " size=" + bgV.getWidth() + "x" + bgV.getHeight());
                                    }
                                    // ★ 2026-11-01 13:05 通知卡片改用「亮度条配方」
                                    //
                                    //  【为什么】
                                    //    主人：「亮度条只是偏鲜艳，但本身是真玻璃」
                                    //    → 通知卡片要跟亮度条一致，就得把亮度条那 6 步全做齐。
                                    //      尤其是 blend 单元素淡白 + 模糊链路四连（含 WinExc）。
                                    //
                                    //  开关：debug.hyperclassic.ur_notif
                                    if (urOn("ur_notif") && bgV.getWidth() > 0 && bgV.getHeight() > 0) {
                                        applySliderRecipeFromProps(module, bgV,
                                                bgV.getClass().getClassLoader(),
                                                "L14 notif UR", rad);
                                    }
                                }
                                catch (Throwable t) {
                                    L.log(5, TAG, "L14 fakeBg failed: " + t);
                                }
                            }
                            if ((n2 = ROWSELF_COUNT.incrementAndGet()) <= 6) {
                                Outline o = new Outline();
                                String oc = "?";
                                try {
                                    p = bgV.getOutlineProvider();
                                    if (p != null) {
                                        p.getOutline(bgV, o);
                                        oc = "r=" + o.getRadius() + " rect=" + o.getRect(new Rect());
                                    }
                                }
                                catch (Throwable throwable) {
                                    // empty catch block
                                }
                                L.log(4, TAG, "L14 bgGlass " + bgV.getWidth() + "x" + bgV.getHeight() + " ownOutline[" + oc + "]");
                            }
                        });
                    }
                    if ((n = EFFECTFWD_COUNT.incrementAndGet()) <= 4) {
                        L.log(4, TAG, "L14 row glass #" + n);
                    }
                    Object var12_18 = null;
                    return var12_18;
                }
                catch (Throwable t) {
                    Throwable c = t.getCause() != null ? t.getCause() : t;
                    L.log(5, TAG, "L14 forward failed: " + c);
                    Object object = chain.proceed();
                    return object;
                }
                finally {
                    IN_GLASS_APPLY.remove();
                }
            });
            L.log(4, TAG, "L14 hooked blur.apply -> glass.apply (official chain)");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L14 forward failed: " + t);
            return false;
        }
    }

    private static boolean hookBlendColors(XposedModule module, ClassLoader cl, boolean enableGlass) {
        try {
            Class<?> util = Class.forName("com.android.systemui.statusbar.notification.utils.NotificationUtil", false, cl);
            Method target = null;
            for (Method m : util.getDeclaredMethods()) {
                if (!"applyElementViewBlend".equals(m.getName()) || m.getParameterCount() != 8 || m.getParameterTypes()[3] != Integer.TYPE) continue;
                target = m;
                break;
            }
            if (target == null) {
                L.log(5, TAG, "L15: applyElementViewBlend(8 args) not found");
                return false;
            }
            target.setAccessible(true);
            Method m5 = null;
            for (Method m : util.getDeclaredMethods()) {
                if (!"applyElementViewBlend".equals(m.getName()) || m.getParameterCount() != 5 || m.getParameterTypes()[3] != int[].class) continue;
                m5 = m;
                break;
            }
            if (m5 == null) {
                L.log(5, TAG, "L15: blend5 not found");
                return false;
            }
            m5.setAccessible(true);
            Method blend5 = m5;
            module.hook((Executable)target).setId("cc_l15_blend_color").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                int mode = CcHooks.parseIntSafe(CcHooks.getProp("debug.hyperclassic.blend_light", "0"), 0);
                if (mode <= 0) {
                    return chain.proceed();
                }
                try {
                    String viewName;
                    Object view = chain.getArg(1);
                    boolean isNight = CcHooks.isNightMode(view);
                    boolean allowDark = "1".equals(CcHooks.getProp("debug.hyperclassic.blend_dark", "0").trim());
                    if (isNight && !allowDark) {
                        int nb = BLENDLIGHT_COUNT.incrementAndGet();
                        if (nb <= 3) {
                            L.log(4, TAG, "L15 skip override (night mode, keep official shade colors)");
                        }
                        return chain.proceed();
                    }
                    int color1 = (Integer)chain.getArg(3);
                    int mode1 = (Integer)chain.getArg(4);
                    int color2 = (Integer)chain.getArg(5);
                    int mode2 = (Integer)chain.getArg(6);
                    String string = viewName = view == null ? "null" : view.getClass().getSimpleName();
                    Integer lightColor = mode == 1 ? Integer.valueOf(-1275068417) : (mode == 2 ? Integer.valueOf(-419430401) : (mode == 3 ? Integer.valueOf(0x66FFFFFF) : Integer.valueOf(-1)));
                    boolean isBg = viewName.contains("Background");
                    int n = BLENDLIGHT_COUNT.incrementAndGet();
                    if (isBg || n <= 4) {
                        L.log(4, TAG, String.format("L15 blend8(light) [%08X/m%d %08X/m%d] -> %08X (mode=%d) view=%s", color1, mode1, color2, mode2, lightColor, mode, viewName));
                    }
                    return blend5.invoke(null, chain.getArg(0), view, chain.getArg(2), new int[]{lightColor, mode1, lightColor, mode2}, chain.getArg(7));
                }
                catch (Throwable t) {
                    Throwable c = t.getCause() != null ? t.getCause() : t;
                    L.log(5, TAG, "L15 blend8 override failed: " + c);
                    return chain.proceed();
                }
            });
            L.log(4, TAG, "L15 hooked applyElementViewBlend(8 args) light override");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L15 failed: " + t);
            return false;
        }
    }

    private static int parseIntSafe(String s, int def) {
        try {
            return Integer.parseInt(s.trim());
        }
        catch (Throwable t) {
            return def;
        }
    }

    /** 解析浮点数，失败返回默认值 */
    private static float parseFloatSafe(String s, float def) {
        try {
            return Float.parseFloat(s.trim());
        }
        catch (Throwable t) {
            return def;
        }
    }

    /** 解析十六进制（可带 0x 前缀，忽略 #），失败返回默认值 */
    private static int parseIntSafeHex(String s, int def) {
        try {
            String v = s.trim();
            if (v.startsWith("#")) v = v.substring(1);
            if (v.startsWith("0x") || v.startsWith("0X")) v = v.substring(2);
            if (v.isEmpty()) return def;
            long l = Long.parseLong(v, 16);
            return (int) l;
        }
        catch (Throwable t) {
            return def;
        }
    }

    /**
     * 递归把 View 树里的所有 TextView 强制设成指定颜色。
     *
     * 【为什么不能只调 setTextColor(int)】
     *   TextView 原本可能挂了 ColorStateList（不同 state 不同色），
     *   系统在 state 变化时会用 ColorStateList 重新覆盖颜色。
     *   所以必须 setTextColor(ColorStateList.valueOf(color))，
     *   把这个 single-state 的 ColorStateList 钉死。
     *
     * @return 处理的 TextView 数量
     */
    private static int forceTextColor(View root, int color) {
        if (root == null) return 0;
        int n = 0;
        try {
            if (root instanceof TextView) {
                ((TextView) root).setTextColor(ColorStateList.valueOf(color));
                ++n;
            }
            if (root instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) root;
                for (int i = 0; i < vg.getChildCount(); ++i) {
                    n += forceTextColor(vg.getChildAt(i), color);
                }
            }
        }
        catch (Throwable ignored) {
        }
        return n;
    }

    private static boolean isNightMode(Object view) {
        // ★★★ v9.74 修复：先读全局配置，再退回 View 的 Resources ★★★
        //
        //  【实证】日志里出现矛盾：
        //      L4 entry applyUiMode=0x10  ← 系统确实是深色
        //      RG glass apply hook night=false ← 但判断成浅色
        //  【原因】View.getResources().getConfiguration() 在切换瞬间
        //    可能还是旧配置（View 尚未重新 attach）。
        //  【修法】优先读 Resources.getSystem()（全局配置，最先更新），
        //    再退回 View 自己的 Resources。
        try {
            Configuration g = Resources.getSystem().getConfiguration();
            int m = g.uiMode & 0x30;
            if (m == 32 || m == 16) {
                return m == 32;
            }
        } catch (Throwable ignored) {
        }
        try {
            if (view instanceof View) {
                Configuration cfg = ((View)view).getResources().getConfiguration();
                return (cfg.uiMode & 0x30) == 32;
            }
        }
        catch (Throwable cfg) {
            // empty catch block
        }
        try {
            String v = CcHooks.getProp("debug.hyperclassic.night_override", "").trim();
            if (!v.isEmpty()) {
                return "1".equals(v);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return false;
    }

    private static boolean hookMediaViewGlass(XposedModule module, ClassLoader cl, boolean enableGlass) {
        boolean eg = enableGlass;
        try {
            Class<?> cls = Class.forName("com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaHeaderView", false, cl);
            Method m = null;
            try {
                m = cls.getDeclaredMethod("onAttachedToWindow", new Class[0]);
            }
            catch (NoSuchMethodException e) {
                try {
                    m = cls.getDeclaredMethod("onFinishInflate", new Class[0]);
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    // empty catch block
                }
            }
            if (m == null) {
                L.log(5, TAG, "L37: no attach hook point");
                return false;
            }
            m.setAccessible(true);
            Method getHolder = cls.getMethod("getMediaViewHolder", new Class[0]);
            getHolder.setAccessible(true);
            module.hook((Executable)m).setId("cc_l37_media_glass").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object r = chain.proceed();
                if (!eg) {
                    return r;
                }
                try {
                    Object self = chain.getThisObject();
                    Object holder = getHolder.invoke(self, new Object[0]);
                    if (holder == null) {
                        return r;
                    }
                    Object mediaBg = holder.getClass().getField("mediaBg").get(holder);
                    if (!(mediaBg instanceof View)) {
                        return r;
                    }
                    View v = (View)mediaBg;
                    v.post(() -> {
                        int mstyle = CcHooks.parseIntSafe(CcHooks.getProp("debug.hyperclassic.media_style", "3").trim(), 1);
                        // ★ v88：媒体卡片一律走「纯材质」路径。
                        //   applyMediaReflectiveGlass 会 setBackground + setClipToOutline(true)
                        //   + setOutlineProvider(roundRect)，把官方
                        //   finishForMediaNotificationAnim 里 setOutlineProvider(null) 的效果覆盖回去，
                        //   导致 MiuiMediaHeaderView.setActualHeight 逐帧 invalidateOutline()
                        //   → 高度插值时裁剪边界跳变（主人描述的"多余空间瞬间被裁掉"）。
                        //   材质路径只设 MiGlass 参数 + 透明背景，圆角由 SDF 自带，不碰几何。
                        if (mstyle == 0) {
                            CcHooks.applyOfficialMediaGlass(module, v, cl, "L37 media");
                        } else {
                            CcHooks.applyMediaGlassMaterialFull85(module, v, cl, true);
                        }
                        int n = MEDIAVIEW_COUNT.incrementAndGet();
                        if (n <= 5) {
                            L.log(4, TAG, "L37 media glass(material) #" + n + " w=" + v.getWidth() + " h=" + v.getHeight());
                        }
                    });
                }
                catch (Throwable t) {
                    L.log(5, TAG, "L37 media failed: " + t);
                }
                return r;
            });
            L.log(4, TAG, "L37 hooked MiuiMediaHeaderView." + m.getName());
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L37 hook failed: " + t);
            return false;
        }
    }

    private static boolean hookMediaBlurEffect(XposedModule module, ClassLoader cl, boolean enableGlass) {
        boolean eg = enableGlass;
        try {
            Class<?> cls = Class.forName("com.android.systemui.statusbar.notification.style.vieweffect.MediaViewBlurEffect", false, cl);
            Method target = null;
            for (Method m : cls.getDeclaredMethods()) {
                if (!"apply".equals(m.getName()) || m.getParameterCount() != 2 || m.isBridge()) continue;
                target = m;
                break;
            }
            if (target == null) {
                L.log(5, TAG, "L16: MediaViewBlurEffect.apply not found");
                return false;
            }
            target.setAccessible(true);
            Class<?> blurCompat = Class.forName(CLS_MI_BLUR, false, cl);
            Method setBlurMode = blurCompat.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class);
            Method setBlurRadius = blurCompat.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class);
            setBlurMode.setAccessible(true);
            setBlurRadius.setAccessible(true);
            module.hook((Executable)target).setId("cc_l16_media_blur").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object result = chain.proceed();
                if (!eg) {
                    return result;
                }
                try {
                    Object header = chain.getArg(0);
                    if (header == null) {
                        return result;
                    }
                    Object holder = header.getClass().getMethod("getMediaViewHolder", new Class[0]).invoke(header, new Object[0]);
                    if (holder == null) {
                        return result;
                    }
                    Field fBg = holder.getClass().getField("mediaBg");
                    Object mediaBg = fBg.get(holder);
                    if (!(mediaBg instanceof View)) {
                        return result;
                    }
                    View v = (View)mediaBg;
                    int mstyle = CcHooks.parseIntSafe(CcHooks.getProp("debug.hyperclassic.media_style", "3").trim(), 1);
                    // ★ v88：同 L37，媒体卡片只走材质路径（不碰 clip/outline）。
                    if (mstyle == 0) {
                        CcHooks.applyOfficialMediaGlass(module, v, cl, "L16 media");
                    } else {
                        CcHooks.applyMediaGlassMaterialFull85(module, v, cl, true);
                    }
                    int n = MEDIABLUR_COUNT.incrementAndGet();
                    if (n <= 6) {
                        L.log(4, TAG, "L16 media glass(material) #" + n + " w=" + v.getWidth() + " h=" + v.getHeight());
                    }
                }
                catch (Throwable t) {
                    Throwable c = t.getCause() != null ? t.getCause() : t;
                    L.log(5, TAG, "L16 media glass failed: " + c);
                }
                return result;
            });
            L.log(4, TAG, "L16 hooked MediaViewBlurEffect.apply (media blur fix)");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L16 failed: " + t);
            return false;
        }
    }

    private static boolean hookGlassParams(XposedModule module, ClassLoader cl, boolean enableGlass) {
        try {
            Class<?> glassCompat = Class.forName(CLS_MI_GLASS, false, cl);
            Method target = null;
            for (Method m : glassCompat.getDeclaredMethods()) {
                if (!"setMiGlassCompat".equals(m.getName()) || m.getParameterCount() != 2 || m.getParameterTypes()[1] != float[].class) continue;
                target = m;
                break;
            }
            if (target == null) {
                L.log(5, TAG, "L17: setMiGlassCompat not found");
                return false;
            }
            target.setAccessible(true);
            module.hook((Executable)target).setId("cc_l17_glass_params").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                block22: {
                    try {
                        Object view = chain.getArg(0);
                        Object arrObj = chain.getArg(1);
                        if (!(arrObj instanceof float[])) break block22;
                        // ★★★ 2026-11-01 14:58 关键修正：识别 tag=float[] 的情况 ★★★
                        //
                        //  【日志实证】
                        //    RG before-glass p0=0.67   ← 进门前正确
                        //    RG after-glass  p0=0.42   ← setMiGlassCompat 后被改写
                        //    L17 INJECT ... chg=[]     ← L17 日志说"没改"（因为它走的是另一个分支）
                        //
                        //  【为什么】
                        //    RG 现在把 tag 设成 garr(float[]) 而不是 Boolean.TRUE。
                        //    但 L17 这里的判断还是老代码：
                        //        Boolean.TRUE.equals(tag)  ← float[] 不满足！
                        //    → 掉进下面的普通 probe 分支，执行：
                        //        applyGuiToGlassParams(params);
                        //      而这个函数会**无条件原地改写**：
                        //        p[0] = 0.72f - c * 0.6f;     // c=GC_CONTRAST/100=0.5
                        //             = 0.72 - 0.3 = 0.42    ← 正是 0.42！
                        //        p[19] = 90.0f - t * 50.0f;   // t=GC_TINT/100=0.25 → 77.5
                        //
                        //  【修法】
                        //    ① tag 是 float[] → 用 tag 里的数组当 want（per-view 隔离）
                        //    ② tag 是 TRUE → 用全局 MEDIA_GLASS_PARAMS_WANT（兼容旧行为）
                        //    ③ 两者都没有 → 才走普通 probe 分支（applyGuiToGlassParams）
                        Object tagObj = view instanceof View
                                ? ((View) view).getTag(2131689504) : null;
                        float[] want = null;
                        if (tagObj instanceof float[]) {
                            want = (float[]) tagObj;
                        } else if (Boolean.TRUE.equals(tagObj)) {
                            want = MEDIA_GLASS_PARAMS_WANT;
                        }
                        if (want != null) {
                            if (arrObj instanceof float[]) {
                                float[] params = (float[])arrObj;
                                int n2 = Math.min(params.length, want.length);
                                StringBuilder chg = new StringBuilder();
                                boolean any = false;
                                for (int i = 0; i < n2; ++i) {
                                    if (params[i] == want[i]) continue;
                                    any = true;
                                    if (chg.length() < 120) {
                                        chg.append(i).append(':').append(params[i]).append("->").append(want[i]).append(' ');
                                    }
                                    params[i] = want[i];
                                }
                                int nn = L17_SKIP.incrementAndGet();
                                if (nn <= 8) {
                                    L.log(4, TAG, "L17 INJECT media params #" + nn + " n=" + n2
                                            + " p0=" + params[0]
                                            + " chg=" + (any ? chg.toString() : "none"));
                                }
                            }
                            return chain.proceed();
                        }
                        float[] params = (float[])arrObj;
                        String viewName = view == null ? "null" : view.getClass().getSimpleName();
                        int n = GLASSPARAM_COUNT.incrementAndGet();
                        boolean isNight = CcHooks.isNightMode(view);
                        boolean allowDark = "1".equals(CcHooks.getProp("debug.hyperclassic.glass_dark", "0").trim());
                        try {
                            if (view instanceof View) {
                                View vw = (View)view;
                                Integer c = L17_VIEW_COUNT.get(vw);
                                int cnt = c == null ? 1 : c + 1;
                                L17_VIEW_COUNT.put(vw, cnt);
                                if (cnt == 1 || cnt == 2 || cnt == 5 || cnt == 20) {
                                    L.log(4, TAG, "L17 view#" + cnt + " " + viewName + " size=" + vw.getWidth() + "x" + vw.getHeight());
                                }
                            }
                        }
                        catch (Throwable vw) {
                            // empty catch block
                        }
                        boolean isBg = viewName.contains("Background");
                        if (isBg || n <= 2) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < params.length; ++i) {
                                sb.append(i).append('=').append(params[i]);
                                if (i >= params.length - 1) continue;
                                sb.append(' ');
                            }
                            L.log(4, TAG, "L17 glass params (" + params.length + ") view=" + viewName + " night=" + isNight + " [" + sb + "]");
                            // ★★★ v9.82：官方参数挂上后 400ms 读回全状态（含父链） ★★★
                            //   背景：官方玻璃参数确实在设（L17 ×34，p0=0.67），
                            //   但观感仍磨砂。需要知道挂上之后谁又改了状态。
                            try {
                                final View fv = (view instanceof View) ? (View) view : null;
                                final ClassLoader fcl2 = view.getClass().getClassLoader();
                                if (fv != null) {
                                    fv.postDelayed(() -> {
                                        try {
                                            Class<?> mb = Class.forName(CLS_MI_BLUR, false, fcl2);
                                            Class<?> mg = Class.forName(CLS_MI_GLASS, false, fcl2);
                                            StringBuilder d = new StringBuilder();
                                            // ★ v9.87：旧 getter 名全是错的（读出来永远空）。
                                            //   直接反射遍历 View 上所有 getMi*/getPass* 方法，读真实状态。
                                            try {
                                                StringBuilder gs = new StringBuilder();
                                                for (java.lang.reflect.Method gm : android.view.View.class.getMethods()) {
                                                    String gn = gm.getName();
                                                    if (!(gn.startsWith("getMi") || gn.startsWith("getPass") || gn.startsWith("isMi"))) continue;
                                                    if (gm.getParameterTypes().length != 0) continue;
                                                    try {
                                                        Object rv = gm.invoke(fv);
                                                        if (rv == null || (rv instanceof Integer && ((Integer) rv) == 0)
                                                                || (rv instanceof Boolean && !((Boolean) rv))) continue;
                                                        gs.append(gn.replace("get", "").replace("Mi", "")).append('=').append(rv).append(' ');
                                                    } catch (Throwable ig2) {}
                                                }
                                                if (gs.length() > 0) {
                                                    d.append(" real=[").append(gs.toString().trim()).append(']');
                                                }
                                            } catch (Throwable ig0) {}
                                            // 父链模糊
                                            StringBuilder pc = new StringBuilder();
                                            android.view.View pv = fv;
                                            for (int i = 0; i < 4 && pv.getParent() instanceof android.view.View; i++) {
                                                android.view.View p2 = (android.view.View) pv.getParent();
                                                pc.append(" <- ").append(p2.getClass().getSimpleName());
                                                try {
                                                    Object pm = mb.getMethod("getMiBackgroundBlurModeCompat", View.class).invoke(null, p2);
                                                    Object pr = mb.getMethod("getMiBackgroundBlurRadiusCompat", View.class).invoke(null, p2);
                                                    Object pp = mb.getMethod("getPassWindowBlurEnabledCompat", View.class).invoke(null, p2);
                                                    pc.append("[m").append(pm).append(" r").append(pr).append(" p").append(pp).append("]");
                                                } catch (Throwable ig) {}
                                                pv = p2;
                                            }
                                            L.log(4, TAG, "L17 VERIFY after-glass" + d + " parent=" + pc);
                                        } catch (Throwable t3) {
                                            L.log(5, TAG, "L17 VERIFY failed: " + t3);
                                        }
                                    }, 400L);
                                }
                                // ★★★ v9.86 撤销 v9.85 全局静态字段改写 ★★★
                                //  【为什么撤】materialStyle 是全局静态，悬浮通知走 BLUR 流程，
                                //  被强设 GLASS 后悬浮通知背景变纯黑（v9.85 实测）。
                                //  改为：per-view 只切轮廓（保留），并补齐与展开按钮的配方差：
                                //    按钮 = clearMiBackgroundBlendColorCompat + viewBlurMode=1
                                //    通知 = setMiBackgroundBlendColors(...) ← 多了 blend
                                if ("1".equals(CcHooks.getProp("debug.hyperclassic.rg_outline_fix", "1").trim())
                                        && viewName.contains("NotificationBackground")) {
                                    // ★ v9.87 作用域守卫：只在通知栈（下拉面板）内生效。
                                    //   悬浮通知是独立窗口（默认就是玻璃），绝不修改它。
                                    final View fo0 = (View) view;
                                    boolean inStack = false;
                                    for (android.view.ViewParent pp0 = fo0.getParent(); pp0 instanceof View; pp0 = pp0.getParent()) {
                                        String pn = ((View) pp0).getClass().getSimpleName();
                                        if (pn.contains("NotificationStackScroll") || pn.contains("NotificationShade")) {
                                            inStack = true;
                                            break;
                                        }
                                    }
                                    if (!inStack) {
                                        L.log(4, TAG, "L17 scope skip (not in shade stack, likely heads-up)");
                                    } else {
                                    final View fo = (View) view;
                                    fo.postDelayed(() -> {
                                        try {
                                            Class<?> mbc = Class.forName(CLS_MI_BLUR, false, fo.getClass().getClassLoader());
                                            // per-view 轮廓切换（不碰全局静态）
                                            mbc.getMethod("setGlassOutlineEnable", View.class, Boolean.TYPE).invoke(null, fo, true);
                                            mbc.getMethod("setBlurOutlineEnable", View.class, Boolean.TYPE).invoke(null, fo, false);
                                            // 对齐展开按钮配方：清掉 blend
                                            try {
                                                mbc.getMethod("clearMiBackgroundBlendColorCompat", View.class).invoke(null, fo);
                                            } catch (Throwable ig) {
                                            }
                                            // ★ v9.88 补刀：直接把磨砂清零（real=[...] 实证 Mode=1 R=80）
                                            try {
                                                mbc.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class).invoke(null, 0, fo);
                                                mbc.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class).invoke(null, 0, fo);
                                            } catch (Throwable ig2) {
                                                L.log(5, TAG, "L17 v86 zero failed: " + ig2);
                                            }
                                            L.log(4, TAG, "L17 v86 fix: glassOutline=true blurOutline=false blendCleared bgZeroed");
                                        } catch (Throwable t5) {
                                            L.log(5, TAG, "L17 v86 fix failed: " + t5);
                                        }
                                    }, 600L);
                                    }
                                }
                            } catch (Throwable t2) {
                            }
                        }
                        // ★★★ v9.70 关键修复：applyGuiToGlassParams 默认关闭 ★★★
                        //
                        //  【观察日志实证】
                        //    L17 OBS LIGHT view=ImageView              p0=0.72 stk=MediaViewGlassEffect.apply
                        //    L17 OBS LIGHT view=BrightnessSliderView   p0=0.72 stk=(官方)
                        //    L17 OBS LIGHT view=CircleAndTickAnimView  p0=0.72 stk=NotificationDismissViewGlassEffect.apply
                        //    L17 OBS LIGHT view=View                   p0=0.72 stk=(托盘)
                        //  所有 View 都被改写成 0.72/90 —— 这正是「背景消失/变磨砂」的来源。
                        //
                        //  【为什么】
                        //    applyGuiToGlassParams 是老实验（glass_contrast/glass_tint）的产物，
                        //    会无条件把 p0 改成 0.72-c*0.6、p19 改成 90-t*50，
                        //    把官方每个 View 各自的正确玻璃参数全部破坏。
                        //
                        //  【修法】
                        //    默认不调用（glass_gui_override=0），保持官方参数原样。
                        //    需要旧实验时 setprop debug.hyperclassic.glass_gui_override 1
                        boolean guiOvr = "1".equals(CcHooks.getProp("debug.hyperclassic.glass_gui_override", "0").trim());
                        if (!guiOvr) {
                            return chain.proceed();
                        }
                        if (isNight && !allowDark) {
                            CcHooks.applyGuiToGlassParams(params);
                            if (n <= 6) {
                                L.log(4, TAG, "L17 OBS NIGHT view=" + viewName + " p0=" + params[0]
                                        + " p19=" + params[Math.min(19, params.length - 1)]
                                        + " p21=" + params[Math.min(21, params.length - 1)]
                                        + " stk=" + CcHooks.shortStack(5));
                            }
                        } else {
                            CcHooks.applyGuiToGlassParams(params);
                            StringBuilder changed = new StringBuilder();
                            int hitCount = 0;
                            for (int i = 0; i < params.length && i < 64; ++i) {
                                String p = CcHooks.getProp("debug.hyperclassic.glass_p" + i, "").trim();
                                if (p.isEmpty()) continue;
                                ++hitCount;
                                try {
                                    float v = Float.parseFloat(p);
                                    if (params[i] == v) continue;
                                    changed.append(i).append(':').append(params[i]).append("->").append(v).append(' ');
                                    params[i] = v;
                                    continue;
                                }
                                catch (Throwable throwable) {
                                    // empty catch block
                                }
                            }
                            if (n <= 6) {
                                L.log(4, TAG, "L17 OBS LIGHT view=" + viewName + " p0=" + params[0]
                                        + " p19=" + params[Math.min(19, params.length - 1)]
                                        + " p21=" + params[Math.min(21, params.length - 1)]
                                        + " manual=" + hitCount + " chg=[" + changed + "]"
                                        + " stk=" + CcHooks.shortStack(5));
                            }
                        }
                    }
                    catch (Throwable t) {
                        L.log(5, TAG, "L17 override failed: " + t);
                    }
                }
                return chain.proceed();
            });
            L.log(4, TAG, "L17 hooked setMiGlassCompat (param probe + override)");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L17 failed: " + t);
            return false;
        }
    }

    private static boolean hookBackgroundProbe(XposedModule module, ClassLoader cl, boolean enableGlass) {
        try {
            Class<View> viewCls = View.class;
            Method setBg = viewCls.getMethod("setBackground", Drawable.class);
            Method setBgColor = viewCls.getMethod("setBackgroundColor", Integer.TYPE);
            setBg.setAccessible(true);
            setBgColor.setAccessible(true);
            module.hook((Executable)setBg).setId("cc_l18_bg_drawable").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object result = chain.proceed();
                if (!"1".equals(CcHooks.getProp("debug.hyperclassic.bg_probe", "0").trim())) {
                    return result;
                }
                try {
                    int n;
                    String cls;
                    Object v = chain.getThisObject();
                    Object d = chain.getArg(0);
                    String string = cls = v == null ? "null" : v.getClass().getName();
                    if (CcHooks.isQsTarget(cls) && (n = BGPROBE_COUNT.incrementAndGet()) <= 60) {
                        L.log(4, TAG, "L18 bgDrawable " + CcHooks.simple(cls) + " <- " + (d == null ? "null" : d.getClass().getSimpleName()) + " hash=" + System.identityHashCode(v));
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                return result;
            });
            module.hook((Executable)setBgColor).setId("cc_l18_bg_color").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object result = chain.proceed();
                if (!"1".equals(CcHooks.getProp("debug.hyperclassic.bg_probe", "0").trim())) {
                    return result;
                }
                try {
                    int n;
                    String cls;
                    Object v = chain.getThisObject();
                    Object c = chain.getArg(0);
                    String string = cls = v == null ? "null" : v.getClass().getName();
                    if (CcHooks.isQsTarget(cls) && (n = BGPROBE_COUNT.incrementAndGet()) <= 60) {
                        L.log(4, TAG, String.format("L18 bgColor %s <- #%08X hash=%d", CcHooks.simple(cls), (Integer)c, System.identityHashCode(v)));
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                return result;
            });
            L.log(4, TAG, "L18 hooked View.setBackground probes (bg_probe=1 \u5f00\u542f)");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L18 failed: " + t);
            return false;
        }
    }

    private static boolean isQsTarget(String cls) {
        if (cls == null) {
            return false;
        }
        String l = cls.toLowerCase();
        return l.contains("qs") || l.contains("tile") || l.contains("shade") || l.contains("brightness") || l.contains("slider") || l.contains("notificationstack") || l.contains("scrim");
    }

    private static String simple(String cls) {
        int i = cls.lastIndexOf(46);
        return i >= 0 ? cls.substring(i + 1) : cls;
    }

    private static void applyTileMaterial(XposedModule module, View tile, int mode) {
        try {
            View secondLine;
            View label;
            int state = CcHooks.getTileState(tile);
            CcHooks.setIconColorsWhite(tile);
            View iconFrame = CcHooks.findFieldView(tile, "mIconFrame");
            if (iconFrame == null) {
                int n = TILE_APPLY_COUNT.incrementAndGet();
                if (n <= 3) {
                    L.log(5, TAG, "L21 mIconFrame not found on " + tile.getClass().getName());
                }
                return;
            }
            if (iconFrame.getWidth() == 0 || iconFrame.getHeight() == 0) {
                View tf = tile;
                int fm = mode;
                iconFrame.post(() -> CcHooks.applyTileMaterial(module, tf, fm));
                int n = TILE_APPLY_COUNT.incrementAndGet();
                if (n <= 5) {
                    L.log(4, TAG, "L21 defer (size 0), posted retry");
                }
                return;
            }
            if (state == 2) {
                CcHooks.setIconColorsWhite(tile);
                CcHooks.forceIconWhite(tile, state);
                CcHooks.updateIconGuardState(tile, state);
                int n = TILE_APPLY_COUNT.incrementAndGet();
                if (n <= 8) {
                    L.log(4, TAG, "L21 tile ON state=2, keep official fill, iconFrame w=" + iconFrame.getWidth() + " h=" + iconFrame.getHeight());
                }
                return;
            }
            CcHooks.applyTileGlass(module, iconFrame, mode);
            // ★ v137：磁贴颜色差异诊断——按「磁贴名 + state + 背景色」去重记录。
            //   【主人反馈】「顶部的截屏与热点两个磁贴，它们的颜色比其他磁贴浅一些。
            //    但理论上磁贴的玻璃化背景完全由 hook 替换，应该统一颜色才对。」
            //   【为什么需要这个】L21 PROBE 只打前 6 条，看不出是哪几个磁贴。
            //    这里改成**带 label 文本**的记录，就能直接对比
            //    「截屏 / 热点」与「屏幕录制 / 投屏」的实际字段差异。
            //   去重键 = label + state + bg 颜色，避免每帧刷屏。
            if ("1".equals(CcHooks.getProp("debug.hyperclassic.tile_diff", "1").trim())) {
                try {
                    String lbl137 = "";
                    View lv137 = CcHooks.findFieldView(tile, "mLabel");
                    if (lv137 instanceof TextView) {
                        CharSequence cs137 = ((TextView) lv137).getText();
                        lbl137 = cs137 == null ? "" : cs137.toString();
                    }
                    Drawable bg137 = iconFrame.getBackground();
                    String bgDesc137 = "null";
                    if (bg137 instanceof ColorDrawable) {
                        bgDesc137 = String.format("#%08X", ((ColorDrawable) bg137).getColor());
                    } else if (bg137 instanceof GradientDrawable) {
                        try {
                            Field f137 = GradientDrawable.class.getDeclaredField("mFillPaint");
                            f137.setAccessible(true);
                            Object p137 = f137.get(bg137);
                            bgDesc137 = (p137 instanceof Paint)
                                    ? String.format("#%08X", ((Paint) p137).getColor())
                                    : "GradientDrawable";
                        } catch (Throwable ignored) { bgDesc137 = "GradientDrawable"; }
                    } else {
                        bgDesc137 = bg137.getClass().getSimpleName();
                    }
                    String key137 = lbl137 + "|s" + state + "|" + bgDesc137
                            + "|a" + iconFrame.getAlpha();
                    if (TILE_DIFF_SEEN.add(key137) && TILE_DIFF_CNT.incrementAndGet() <= 60) {
                        L.log(4, TAG, "L137 tile [" + lbl137 + "] state=" + state
                                + " bg=" + bgDesc137
                                + " alpha=" + iconFrame.getAlpha()
                                + " cto=" + iconFrame.getClipToOutline()
                                + " blendOn=" + TILE_BLEND_SEEN.contains(iconFrame)
                                + " size=" + iconFrame.getWidth() + "x" + iconFrame.getHeight());
                    }
                } catch (Throwable ignored) {}
            }
            if ("1".equals(CcHooks.getProp("debug.hyperclassic.tile_probe", "1").trim()) && TILEPROBE_COUNT.get() < 6) {
                TILEPROBE_COUNT.incrementAndGet();
                Drawable bg = iconFrame.getBackground();
                Drawable fg = iconFrame.getForeground();
                StringBuilder sb = new StringBuilder();
                sb.append("L21 PROBE iconFrame cls=").append(iconFrame.getClass().getSimpleName()).append(" size=").append(iconFrame.getWidth()).append("x").append(iconFrame.getHeight()).append(" bg=").append(bg == null ? "null" : bg.getClass().getSimpleName());
                if (bg instanceof ColorDrawable) {
                    sb.append("(color=#").append(String.format("%08X", ((ColorDrawable)bg).getColor())).append(")");
                } else if (bg instanceof GradientDrawable) {
                    GradientDrawable gd = (GradientDrawable)bg;
                    try {
                        Field f = GradientDrawable.class.getDeclaredField("mFillPaint");
                        f.setAccessible(true);
                        Object paint = f.get(gd);
                        if (paint instanceof Paint) {
                            sb.append("(fill=#").append(String.format("%08X", ((Paint)paint).getColor())).append(")");
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                }
                sb.append(" fg=").append(fg == null ? "null" : fg.getClass().getSimpleName()).append(" outline=").append(iconFrame.getClipToOutline()).append(" state=").append(state).append(" alpha=").append(iconFrame.getAlpha());
                L.log(4, TAG, sb.toString());
            }
            if ((label = CcHooks.findFieldView(tile, "mLabel")) instanceof TextView) {
                CcHooks.forceLabelWhite((TextView)label, -419430401);
            }
            if ((secondLine = CcHooks.findFieldView(tile, "mSecondLine")) instanceof TextView) {
                CcHooks.forceLabelWhite((TextView)secondLine, -1275068417);
            }
            CcHooks.installTextGuard(module, tile);
            CcHooks.tintTileIndicators(module, tile);
            CcHooks.forceIconWhite(tile, state);
            CcHooks.installIconGuard(module, tile, state);
            int n = TILE_APPLY_COUNT.incrementAndGet();
            if (n <= 8) {
                L.log(4, TAG, "L21 tile applied state=" + state + " iconFrame=" + iconFrame.getWidth() + "x" + iconFrame.getHeight() + " bg=" + (iconFrame.getBackground() == null ? "null" : iconFrame.getBackground().getClass().getSimpleName()));
            }
        }
        catch (Throwable t) {
            L.log(5, TAG, "L21 applyTileMaterial failed: " + t);
        }
    }

    private static void setIconColorsWhite(View tile) {
        try {
            View iconView = CcHooks.findFieldView(tile, "mIcon");
            if (iconView == null) {
                return;
            }
            for (Class<?> c = iconView.getClass(); c != null; c = c.getSuperclass()) {
                try {
                    Field fDis = c.getDeclaredField("mIconColorDisabled");
                    fDis.setAccessible(true);
                    fDis.setInt(iconView, -1);
                }
                catch (NoSuchFieldException fDis) {
                    // empty catch block
                }
                try {
                    Field fRes = c.getDeclaredField("mIconColorRestricted");
                    fRes.setAccessible(true);
                    fRes.setInt(iconView, -1);
                }
                catch (NoSuchFieldException fRes) {
                    // empty catch block
                }
                try {
                    Field fUna = c.getDeclaredField("mIconColorUnavailable");
                    fUna.setAccessible(true);
                    fUna.setInt(iconView, 0x66FFFFFF);
                    continue;
                }
                catch (NoSuchFieldException noSuchFieldException) {
                    // empty catch block
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static boolean hookColorProbe(XposedModule module, ClassLoader cl) {
        try {
            Method m2;
            int hooked = 0;
            try {
                m2 = Resources.class.getMethod("getColor", Integer.TYPE);
                module.hook((Executable)m2).setId("cc_l23_res").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object r = chain.proceed();
                    if ("1".equals(CcHooks.getProp("debug.hyperclassic.color_probe", "0").trim())) {
                        try {
                            String key;
                            Object a0 = chain.getArgs().get(0);
                            if (a0 instanceof Integer && r instanceof Integer && COLOR_SEEN.add(key = String.format("RES 0x%08X=#%08X", (Integer)a0, (Integer)r)) && COLOR_SEEN.size() <= 200) {
                                L.log(4, TAG, "L23 " + key);
                            }
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                    }
                    return r;
                });
                ++hooked;
            }
            catch (Throwable thrown1) {
                // empty catch block
            }
            try {
                m2 = Context.class.getMethod("getColor", Integer.TYPE);
                module.hook((Executable)m2).setId("cc_l23_ctx").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object r = chain.proceed();
                    if ("1".equals(CcHooks.getProp("debug.hyperclassic.color_probe", "0").trim())) {
                        try {
                            String key;
                            Object a0 = chain.getArgs().get(0);
                            if (a0 instanceof Integer && r instanceof Integer && COLOR_SEEN.add(key = String.format("CTX 0x%08X=#%08X", (Integer)a0, (Integer)r)) && COLOR_SEEN.size() <= 200) {
                                L.log(4, TAG, "L23 " + key);
                            }
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                    }
                    return r;
                });
                ++hooked;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            L.log(4, TAG, "L23 color probe hooked=" + hooked);
            return hooked > 0;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L23 failed: " + t);
            return false;
        }
    }

    private static boolean hookNotifText(XposedModule module, ClassLoader cl) {
        try {
            Class<?> cls = Class.forName("com.android.systemui.statusbar.notification.row.wrapper.MiuiNotificationTemplateViewWrapper", false, cl);
            Method m = cls.getDeclaredMethod("updateTransparentBgAndTextColor", Class.forName("com.android.systemui.statusbar.notification.collection.NotificationEntry", false, cl), Boolean.TYPE);
            m.setAccessible(true);
            module.hook((Executable)m).setId("cc_l24_notiftext").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object r = chain.proceed();
                try {
                    Object self = chain.getThisObject();
                    CcHooks.forceNotifTextWhite(self);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                return r;
            });
            L.log(4, TAG, "L24 notif text hooked");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L24 notif text hook failed: " + t);
            return false;
        }
    }

    private static void forceNotifTextWhite(Object wrapper) {
        try {
            CcHooks.setFieldTextColor(wrapper, "mTitle", -1);
            CcHooks.setFieldTextColor(wrapper, "mText", -419430401);
            CcHooks.setFieldTextColor(wrapper, "mSubText", -1275068417);
            CcHooks.setFieldTextColor(wrapper, "mTime", -1275068417);
            CcHooks.setFieldTextColor(wrapper, "mBigTitle", -1);
            CcHooks.setFieldTextColor(wrapper, "mBigText", -419430401);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void setFieldTextColor(Object target, String field, int color) {
        try {
            for (Class<?> c = target.getClass(); c != null; c = c.getSuperclass()) {
                try {
                    Field f = c.getDeclaredField(field);
                    f.setAccessible(true);
                    Object v = f.get(target);
                    if (v instanceof TextView) {
                        ((TextView)v).setTextColor(color);
                    }
                    return;
                }
                catch (NoSuchFieldException noSuchFieldException) {
                    continue;
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static boolean hookNotifTextColor(XposedModule module, ClassLoader cl) {
        try {
            Method m = TextView.class.getDeclaredMethod("setTextColor", Integer.TYPE);
            m.setAccessible(true);
            module.hook((Executable)m).setId("cc_l25_textcolor").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                try {
                    int orig;
                    int forced;
                    Object self = chain.getThisObject();
                    Object a0 = chain.getArgs().get(0);
                    if (self instanceof TextView && a0 instanceof Integer && CcHooks.isInNotification((View)self) && (forced = CcHooks.pickNotifColor((TextView)self, orig = ((Integer)a0).intValue())) != orig) {
                        return chain.proceed(new Object[]{forced});
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                return chain.proceed();
            });
            L.log(4, TAG, "L25 notif textColor hooked");
            try {
                Method m2 = TextView.class.getDeclaredMethod("setTextColor", ColorStateList.class);
                m2.setAccessible(true);
                module.hook((Executable)m2).setId("cc_l25_textcolor_csl").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    try {
                        int orig;
                        int forced;
                        Object self = chain.getThisObject();
                        Object a0 = chain.getArgs().get(0);
                        if (self instanceof TextView && a0 instanceof ColorStateList && CcHooks.isInNotification((View)self) && (forced = CcHooks.pickNotifColor((TextView)self, orig = ((ColorStateList)a0).getDefaultColor())) != orig) {
                            return chain.proceed(new Object[]{ColorStateList.valueOf((int)forced)});
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    return chain.proceed();
                });
                L.log(4, TAG, "L25 notif textColor(CSL) hooked");
            }
            catch (Throwable t) {
                L.log(5, TAG, "L25 CSL hook failed: " + t);
            }
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L25 hook failed: " + t);
            return false;
        }
    }

    private static boolean isInNotification(View v) {
        try {
            View cur = v;
            for (int i = 0; i < 8 && cur != null; ++i) {
                String cn = cur.getClass().getName();
                if (cn.contains("StatusBarIconView") || cn.contains("MiuiStatusBarIcon") || cn.contains("NotificationIconArea") || cn.contains("NotificationShadeWindow") || cn.contains("MiuiNotificationSpeed") || cn.contains("SpeedView")) {
                    return false;
                }
                if (cn.contains("ExpandableNotificationRow") || cn.contains("NotificationContentView") || cn.contains("NotificationTemplateViewWrapper") || cn.contains("HybridNotificationView")) {
                    return true;
                }
                ViewParent p = cur.getParent();
                cur = p instanceof View ? (View)p : null;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return false;
    }

    private static int pickNotifColor(TextView tv, int orig) {
        try {
            int a = Color.alpha((int)orig);
            int lum = (Color.red((int)orig) + Color.green((int)orig) + Color.blue((int)orig)) / 3;
            int max = Math.max(Color.red((int)orig), Math.max(Color.green((int)orig), Color.blue((int)orig)));
            int min = Math.min(Color.red((int)orig), Math.min(Color.green((int)orig), Color.blue((int)orig)));
            if (max - min > 40) {
                return orig;
            }
            if (lum > 180 && a > 200) {
                return orig;
            }
            float size = tv.getTextSize();
            if (size >= 40.0f) {
                return -1;
            }
            return a < 128 ? -1275068417 : -419430401;
        }
        catch (Throwable t) {
            return orig;
        }
    }

    private static boolean hookSliderOnDraw(XposedModule module, ClassLoader cl) {
        try {
            Method m = ProgressBar.class.getDeclaredMethod("onDraw", Canvas.class);
            m.setAccessible(true);
            module.hook((Executable)m).setId("cc_l26_sliderdraw").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                try {
                    Object self = chain.getThisObject();
                    if (self instanceof View && ((View)self).getTag(2131689474) != null) {
                        CcHooks.tintSliderDrawable((ProgressBar)self);
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                return chain.proceed();
            });
            L.log(4, TAG, "L26 slider onDraw hooked");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L26 hook failed: " + t);
            return false;
        }
    }

    private static boolean hookSliderAllInstances(XposedModule module, ClassLoader cl) {
        try {
            Class<?> cls = Class.forName("com.android.systemui.settings.brightness.BrightnessSliderView", false, cl);
            Method m = cls.getDeclaredMethod("onFinishInflate", new Class[0]);
            m.setAccessible(true);
            module.hook((Executable)m).setId("cc_l28_sliderall").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object r = chain.proceed();
                try {
                    Object self = chain.getThisObject();
                    if (self instanceof View) {
                        View v = (View)self;
                        int mode = CcHooks.parseIntSafe(CcHooks.getProp("debug.hyperclassic.qs_material", "2"), 1);
                        v.post(() -> {
                            try {
                                int m2 = CcHooks.parseIntSafe(CcHooks.getProp("debug.hyperclassic.qs_material", "2"), 1);
                                CcHooks.applySliderMaterial(module, v, m2);
                                L.log(4, TAG, "L28 slider instance " + v.getWidth() + "x" + v.getHeight() + " applied mode=" + m2);
                            }
                            catch (Throwable throwable) {
                                // empty catch block
                            }
                        });
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                return r;
            });
            L.log(4, TAG, "L28 slider all-instances hooked");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L28 hook failed: " + t);
            return false;
        }
    }

    private static void tintSliderDrawable(ProgressBar pb) {
        try {
            Drawable pd = pb.getProgressDrawable();
            if (!(pd instanceof LayerDrawable)) {
                return;
            }
            LayerDrawable ld = (LayerDrawable)pd;
            Drawable bg = ld.findDrawableByLayerId(0x1020000);
            Drawable prog = ld.findDrawableByLayerId(16908301);
            for (int i = 0; i < ld.getNumberOfLayers(); ++i) {
                Drawable layer = ld.getDrawable(i);
                if (layer == null) continue;
                int want = layer == bg ? 0 : (layer == prog ? 0x3DFFFFFF : 0x2AFFFFFF);
                if (layer instanceof GradientDrawable) {
                    GradientDrawable gd = (GradientDrawable)layer;
                    try {
                        Field f = GradientDrawable.class.getDeclaredField("mFillPaint");
                        f.setAccessible(true);
                        Object p = f.get(gd);
                        if (p instanceof Paint && ((Paint)p).getColor() == want) {
                            continue;
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                }
                layer.mutate();
                layer.setTint(want);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static boolean hookSliderProbe(XposedModule module, ClassLoader cl) {
        try {
            Class<?> cls = Class.forName("com.android.systemui.settings.brightness.BrightnessSliderView", false, cl);
            Method m = cls.getDeclaredMethod("dispatchTouchEvent", MotionEvent.class);
            m.setAccessible(true);
            module.hook((Executable)m).setId("cc_l27_sliderprobe").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object r = chain.proceed();
                try {
                    Object self;
                    if ("1".equals(CcHooks.getProp("debug.hyperclassic.slider_probe", "0").trim()) && (self = chain.getThisObject()) instanceof View && SLIDER_PROBE_COUNT.get() < 8) {
                        SLIDER_PROBE_COUNT.incrementAndGet();
                        CcHooks.dumpSliderTree(module, (View)self);
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                return r;
            });
            L.log(4, TAG, "L27 slider probe hooked");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L27 failed: " + t);
            return false;
        }
    }

    private static void dumpSliderTree(XposedModule module, View root) {
        try {
            L.log(4, TAG, "L27 === slider tree ===");
            CcHooks.dumpSliderNode(module, root, 0);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void dumpSliderNode(XposedModule module, View v, int depth) {
        if (v == null || depth > 4) {
            return;
        }
        try {
            Drawable bg = v.getBackground();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < depth; ++i) {
                sb.append("  ");
            }
            sb.append(v.getClass().getSimpleName()).append(" ").append(v.getWidth()).append("x").append(v.getHeight()).append(" bg=").append(bg == null ? "null" : bg.getClass().getSimpleName());
            if (bg instanceof GradientDrawable) {
                try {
                    Field f = GradientDrawable.class.getDeclaredField("mFillPaint");
                    f.setAccessible(true);
                    Object p = f.get(bg);
                    if (p instanceof Paint) {
                        sb.append("(fill=#").append(String.format("%08X", ((Paint)p).getColor())).append(")");
                    }
                }
                catch (Throwable f) {}
            } else if (bg instanceof ColorDrawable) {
                sb.append("(color=#").append(String.format("%08X", ((ColorDrawable)bg).getColor())).append(")");
            }
            if (v instanceof ProgressBar) {
                Drawable pd = ((ProgressBar)v).getProgressDrawable();
                sb.append(" pd=").append(pd == null ? "null" : pd.getClass().getSimpleName());
                if (pd instanceof LayerDrawable) {
                    LayerDrawable ld = (LayerDrawable)pd;
                    sb.append("(").append(ld.getNumberOfLayers()).append(" layers)");
                }
            }
            if (v instanceof TextView) {
                sb.append(" text=#").append(String.format("%08X", ((TextView)v).getCurrentTextColor()));
            }
            L.log(4, TAG, "L27 " + sb);
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup)v;
                for (int i = 0; i < vg.getChildCount(); ++i) {
                    CcHooks.dumpSliderNode(module, vg.getChildAt(i), depth + 1);
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static boolean hookNotifRowDraw(XposedModule module, ClassLoader cl) {
        try {
            Method m;
            Class<?> c;
            String[] targets;
            int ok = 0;
            for (String cn : targets = new String[]{CLS_EXPANDABLE_ROW, "com.android.systemui.statusbar.notification.row.NotificationContentView"}) {
                try {
                    c = Class.forName(cn, false, cl);
                    m = c.getDeclaredMethod("onLayout", Boolean.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
                    m.setAccessible(true);
                    module.hook((Executable)m).setId("cc_l29_layout_" + c.getSimpleName()).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                        Object r = chain.proceed();
                        try {
                            Object self = chain.getThisObject();
                            if (self instanceof View) {
                                View v = (View)self;
                                CcHooks.fixNotificationTexts(v);
                                CcHooks.fixNotificationIcons(module, v);
                                if (ICON_DUMP_TIMES.incrementAndGet() <= 3) {
                                    NOTIF_ICON_DUMP.set(0);
                                    CcHooks.dumpNotifIcons(v, 0);
                                }
                            }
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                        return r;
                    });
                    ++ok;
                    L.log(4, TAG, "L29 hooked onLayout on " + c.getSimpleName());
                }
                catch (Throwable t) {
                    L.log(5, TAG, "L29 onLayout " + cn + " failed: " + t);
                }
            }
            for (String cn : targets) {
                try {
                    c = Class.forName(cn, false, cl);
                    m = c.getDeclaredMethod("dispatchDraw", Canvas.class);
                    m.setAccessible(true);
                    module.hook((Executable)m).setId("cc_l29_dispatch_" + c.getSimpleName()).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                        try {
                            Object self = chain.getThisObject();
                            if (self instanceof View) {
                                CcHooks.fixNotificationTexts((View)self);
                            }
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                        return chain.proceed();
                    });
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            L.log(4, TAG, "L29 notif draw hooked ok=" + ok);
            return ok > 0;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L29 hook failed: " + t);
            return false;
        }
    }

    private static void fixNotificationIcons(XposedModule module, View v) {
        try {
            if (v instanceof ImageView) {
                ImageView iv = (ImageView)v;
                if (iv.getVisibility() != 0) {
                    return;
                }
                if (!CcHooks.isSmallNotifIcon(iv)) {
                    return;
                }
                Drawable d = iv.getDrawable();
                if (d == null) {
                    return;
                }
                ColorStateList tint = null;
                try {
                    tint = iv.getImageTintList();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                if (tint != null && tint.getDefaultColor() == -1) {
                    return;
                }
                CcHooks.forceIconWhite(iv, -1);
                int n = NOTIF_ICON_FIX_COUNT.incrementAndGet();
                if (n <= 6) {
                    L.log(4, TAG, "L29 icon whitened: " + iv.getClass().getSimpleName() + " " + iv.getWidth() + "x" + iv.getHeight());
                }
                // ★ v141：详细诊断——记录被染白图标的完整身份信息。
                //   【为什么需要】v140 只加了「正方形排除」，但 L140 一次没触发，
                //    而 L29 icon whitened 仍然在染 53x53 / 60x60。
                //    说明这两个图标要么没进 isSmallNotifIcon 的正方形分支，
                //    要么染白来自别的地方。必须看清它们的 id / 父链 / drawable。
                if (NOTIF_ICON_FIX_COUNT.get() <= 12) {
                    try {
                        StringBuilder sb141 = new StringBuilder("L141 whitened detail ");
                        sb141.append(iv.getWidth()).append("x").append(iv.getHeight());
                        sb141.append(" id=0x").append(Integer.toHexString(iv.getId()).toUpperCase());
                        sb141.append(" d=").append(d.getClass().getName());
                        sb141.append(" tint=").append(iv.getImageTintList() == null ? "null"
                                : Integer.toHexString(iv.getImageTintList().getDefaultColor()));
                        sb141.append(" bg=").append(iv.getBackground() == null ? "null"
                                : iv.getBackground().getClass().getSimpleName());
                        sb141.append(" chain=");
                        ViewParent pp141 = iv.getParent();
                        for (int dd141 = 0; pp141 != null && dd141 < 5; pp141 = pp141.getParent(), ++dd141) {
                            if (pp141 instanceof View) {
                                View pv141 = (View) pp141;
                                sb141.append(pv141.getClass().getSimpleName());
                                try {
                                    String rn141 = iv.getResources().getResourceEntryName(pv141.getId());
                                    if (rn141 != null && !rn141.startsWith("NO_ID")) sb141.append("(").append(rn141).append(")");
                                } catch (Throwable ignored) {}
                                sb141.append(" < ");
                            }
                        }
                        L.log(4, TAG, sb141.toString());
                    } catch (Throwable ignored) {}
                }
                return;
            }
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup)v;
                for (int i = 0; i < vg.getChildCount(); ++i) {
                    View c = vg.getChildAt(i);
                    if (c == null) continue;
                    CcHooks.fixNotificationIcons(module, c);
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void fixNotificationTexts(View v) {
        try {
            ImageView iv;
            Drawable d;
            if (v instanceof TextView) {
                TextView tv = (TextView)v;
                int cur = tv.getCurrentTextColor();
                int forced = CcHooks.pickNotifColor(tv, cur);
                if (forced != cur) {
                    tv.setTextColor(forced);
                    int n = NOTIF_FIX_COUNT.incrementAndGet();
                    if (n <= 20) {
                        Log.i((String)TAG, (String)("L29 fix text " + tv.getClass().getSimpleName() + " #" + String.format("%08X", cur) + " -> #" + String.format("%08X", forced) + " size=" + tv.getTextSize() + " text=" + CcHooks.abbreviate(tv.getText())));
                    }
                }
                return;
            }
            if (v instanceof ImageView && CcHooks.isInNotificationDeep(v) && (d = (iv = (ImageView)v).getDrawable()) != null && CcHooks.isSmallNotifIcon(iv)) {
                CcHooks.forceIconWhite(iv, -855638017);
                int n = NOTIF_FIX_COUNT.incrementAndGet();
                if (n <= 30) {
                    Log.i((String)TAG, (String)("L29 fix icon id=0x" + Integer.toHexString(iv.getId()).toUpperCase()));
                }
            }
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup)v;
                int n = vg.getChildCount();
                for (int i = 0; i < n; ++i) {
                    CcHooks.fixNotificationTexts(vg.getChildAt(i));
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void dumpNotifIcons(View v, int depth) {
        try {
            int n = NOTIF_ICON_DUMP.incrementAndGet();
            if (n > 60) {
                return;
            }
            if (v instanceof ImageView) {
                ImageView iv = (ImageView)v;
                StringBuilder sb = new StringBuilder("L29 ICON ");
                for (int i = 0; i < depth && i < 12; ++i) {
                    sb.append("\u00b7");
                }
                sb.append(iv.getClass().getSimpleName()).append(" id=0x").append(Integer.toHexString(iv.getId()).toUpperCase()).append(" ").append(iv.getWidth()).append("x").append(iv.getHeight()).append(" bg=").append(iv.getBackground() == null ? "null" : iv.getBackground().getClass().getSimpleName()).append(" d=").append(iv.getDrawable() == null ? "null" : iv.getDrawable().getClass().getSimpleName()).append(" vis=").append(iv.getVisibility()).append(" parent=").append(iv.getParent() == null ? "null" : iv.getParent().getClass().getSimpleName());
                Log.i((String)TAG, (String)sb.toString());
            }
            if (v instanceof ViewGroup && depth < 10) {
                ViewGroup vg = (ViewGroup)v;
                for (int i = 0; i < vg.getChildCount(); ++i) {
                    CcHooks.dumpNotifIcons(vg.getChildAt(i), depth + 1);
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static boolean isNotifChromeView(View v) {
        try {
            Class<?> c = v.getClass();
            String own = c.getSimpleName();
            if (own.contains("NotificationMenuRow") || own.contains("IconRow") || own.contains("Footer") || own.contains("MiuiNotificationHeader")) {
                return true;
            }
            ViewParent p = v.getParent();
            for (int depth = 0; p != null && depth < 5; p = p.getParent(), ++depth) {
                View pv;
                String cn;
                if (!(p instanceof View) || !(cn = (pv = (View)p).getClass().getSimpleName()).contains("NotificationMenuRow") && !cn.contains("FooterView") && !cn.contains("NotificationFooter") && !cn.contains("NotificationHeaderView") && !cn.contains("MiuiNotificationHeader")) continue;
                return true;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return false;
    }

    private static boolean isSmallNotifIcon(ImageView iv) {
        try {
            int w = iv.getWidth();
            int h = iv.getHeight();
            if (iv.getId() == 16908828) {
                return true;
            }
            if (w <= 0 || h <= 0 || w > 130 || h > 130) {
                return false;
            }
            if (CcHooks.isNotifChromeView((View)iv)) {
                return false;
            }
            if (iv.getBackground() != null) {
                return false;
            }
            // ★ v140：排除「应用小图标（app icon）」——它是彩色的，不能染白。
            //
            //  【主人反馈】「我们的 hook 在浅色模式下改白文字的同时，
            //    也把紧凑型弹出通知的小图标改白了。」
            //    截图：爱国行政中心通知，左侧方形应用图标被染成纯白块。
            //
            //  【根因】isSmallNotifIcon 只按「尺寸 ≤130 + 无背景 + 父容器不是
            //    IconFrame/IconContainer」判断，应用小图标完全符合这些条件，
            //    于是被 forceIconWhite 染成纯白 → 彩色图标消失，只剩白块。
            //
            //  【怎么区分「应用小图标」和「系统模板小图标」】
            //    · 应用小图标：父链上有 IconView / NotificationIconContainer /
            //      MiuiNotificationIconArea / smallIcon / appIcon 等节点；
            //      或者是正方形的（宽高比接近 1:1）。
            //    · 系统模板小图标（短信/下载/播放等）：在 template 内容区里，
            //      通常是单色 vector。
            //  这里用「父链类名/资源名」+「正方形」双重判断，任一命中就排除。
            try {
                ViewParent p140 = iv.getParent();
                for (int d140 = 0; p140 != null && d140 < 6; p140 = p140.getParent(), ++d140) {
                    if (!(p140 instanceof View)) continue;
                    View pv140 = (View) p140;
                    String cn140 = pv140.getClass().getName();
                    if (cn140.contains("IconView") || cn140.contains("IconContainer")
                            || cn140.contains("NotificationIconArea") || cn140.contains("SmallIcon")
                            || cn140.contains("AppIcon")) {
                        return false;
                    }
                    // 资源名里带 icon 且不是 chrome 的，多半是图标容器
                    try {
                        String rn140 = iv.getResources().getResourceEntryName(pv140.getId());
                        if (rn140 != null && (rn140.contains("small_icon") || rn140.contains("app_icon")
                                || rn140.contains("icon_area") || rn140.contains("large_icon"))) {
                            return false;
                        }
                    } catch (Throwable ignored) {}
                }
                // 正方形 → 应用小图标
                //   【v140 失败的教训】
                //     上一版加了 `dp >= 32` 门槛，但实测被染白的是 53x53 和 60x60，
                //     在 density=3 的设备上只有 17.7dp / 20dp → 门槛把真正的
                //     应用图标放过去了，等于没生效（L140 一次都没触发）。
                //   【为什么正方形就够了】
                //     应用小图标（Notification.getSmallIcon）在通知模板里几乎总是
                //     按正方形排布（53x53、60x60、96x96…），
                //     而系统模板里真正需要染白的装饰性小图标（播放/下载/箭头）
                //     多数是**非正方形**的矢量。
                //     即使偶有正方形的模板图标被放过（不染白），
                //     在深色玻璃下也仍然可读 —— 这个方向的误伤代价远小于
                //     「彩色应用图标变白块」。
                if (Math.abs(w - h) <= Math.max(3, w / 8)) {
                    int n140 = ICONPROBE_COUNT.incrementAndGet();
                    if (n140 <= 20) {
                        Log.i(TAG, "L140 skip square icon (likely app icon) "
                                + w + "x" + h
                                + " id=0x" + Integer.toHexString(iv.getId()).toUpperCase()
                                + " parent=" + (iv.getParent() == null ? "null"
                                : iv.getParent().getClass().getSimpleName()));
                    }
                    return false;
                }
            } catch (Throwable ignored) {}
            ViewParent p = iv.getParent();
            for (int depth = 0; p != null && depth < 3; p = p.getParent(), ++depth) {
                if (!(p instanceof View)) continue;
                String cn = p.getClass().getSimpleName();
                int pid = ((View)p).getId();
                if (!cn.contains("IconFrame") && !cn.contains("IconContainer") && pid != 2131428719 && pid != 2131428714) continue;
                return false;
            }
            int n = ICONPROBE_COUNT.incrementAndGet();
            if (n <= 20) {
                Log.i((String)TAG, (String)("L29 small icon id=0x" + Integer.toHexString(iv.getId()).toUpperCase() + " " + w + "x" + h + " d=" + (iv.getDrawable() == null ? "null" : iv.getDrawable().getClass().getSimpleName())));
            }
            return true;
        }
        catch (Throwable t) {
            return false;
        }
    }

    private static String abbreviate(CharSequence cs) {
        if (cs == null) {
            return "null";
        }
        String s = cs.toString();
        return s.length() > 16 ? s.substring(0, 16) + "\u2026" : s;
    }

    private static boolean hookNotifIconTint(XposedModule module, ClassLoader cl) {
        try {
            Method m;
            int ok = 0;
            try {
                m = ImageView.class.getDeclaredMethod("setImageDrawable", Drawable.class);
                m.setAccessible(true);
                module.hook((Executable)m).setId("cc_l30_setimgdrawable").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object r = chain.proceed();
                    try {
                        Drawable d;
                        ImageView iv;
                        Object self = chain.getThisObject();
                        if (self instanceof ImageView && CcHooks.isInNotificationDeep((View)(iv = (ImageView)self)) && CcHooks.isSmallNotifIcon(iv) && (d = iv.getDrawable()) != null) {
                            CcHooks.forceIconWhite(iv, -855638017);
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    return r;
                });
                ++ok;
            }
            catch (Throwable t) {
                L.log(5, TAG, "L30 setImageDrawable failed: " + t);
            }
            try {
                m = ImageView.class.getDeclaredMethod("setImageResource", Integer.TYPE);
                m.setAccessible(true);
                module.hook((Executable)m).setId("cc_l30_setimgres").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object r = chain.proceed();
                    try {
                        Drawable d;
                        ImageView iv;
                        Object self = chain.getThisObject();
                        if (self instanceof ImageView && CcHooks.isInNotificationDeep((View)(iv = (ImageView)self)) && CcHooks.isSmallNotifIcon(iv) && (d = iv.getDrawable()) != null) {
                            CcHooks.forceIconWhite(iv, -855638017);
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    return r;
                });
                ++ok;
            }
            catch (Throwable t) {
                L.log(5, TAG, "L30 setImageResource failed: " + t);
            }
            L.log(4, TAG, "L30 notif icon tint hooked=" + ok);
            return ok > 0;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L30 hook failed: " + t);
            return false;
        }
    }

    private static boolean hookTileIndicatorTint(XposedModule module, ClassLoader cl) {
        try {
            int TILE_IND_COLOR = -855638017;
            Method m = ImageView.class.getDeclaredMethod("setImageDrawable", Drawable.class);
            m.setAccessible(true);
            module.hook((Executable)m).setId("cc_l31_tileind").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object r = chain.proceed();
                try {
                    ImageView iv;
                    int id2;
                    Object self = chain.getThisObject();
                    if (self instanceof ImageView && ((id2 = (iv = (ImageView)self).getId()) == 2131428414 || id2 == 2131429855)) {
                        CcHooks.tintImageViewForce(iv, -855638017);
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                return r;
            });
            L.log(4, TAG, "L31 tile indicator tint hooked");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L31 hook failed: " + t);
            return false;
        }
    }

    private static boolean hookPageIndicator(XposedModule module, ClassLoader cl) {
        try {
            Method m;
            Class<?> cls = Class.forName("com.android.systemui.qs.MiuiPageIndicator", false, cl);
            int ok = 0;
            try {
                m = cls.getDeclaredMethod("setIndex", Integer.TYPE);
                m.setAccessible(true);
                module.hook((Executable)m).setId("cc_l32_pageindex").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object r = chain.proceed();
                    try {
                        Object self = chain.getThisObject();
                        if (self instanceof ViewGroup) {
                            CcHooks.fixPageDots((ViewGroup)self, module, "setIndex");
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    return r;
                });
                ++ok;
            }
            catch (Throwable t) {
                L.log(5, TAG, "L32 setIndex failed: " + t);
            }
            try {
                m = cls.getDeclaredMethod("setNumPages", Integer.TYPE);
                m.setAccessible(true);
                module.hook((Executable)m).setId("cc_l32_pagenum").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object r = chain.proceed();
                    try {
                        Object self = chain.getThisObject();
                        if (self instanceof ViewGroup) {
                            CcHooks.fixPageDots((ViewGroup)self, module, "setNumPages");
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    return r;
                });
                ++ok;
            }
            catch (Throwable t) {
                L.log(5, TAG, "L32 setNumPages failed: " + t);
            }
            L.log(4, TAG, "L32 page indicator hooked=" + ok);
            return ok > 0;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L32 hook failed: " + t);
            return false;
        }
    }

    private static void fixPageDots(ViewGroup vg, XposedModule module, String from) {
        try {
            int n = vg.getChildCount();
            int tinted = 0;
            for (int i = 0; i < n; ++i) {
                View c = vg.getChildAt(i);
                if (!(c instanceof ImageView)) continue;
                ImageView iv = (ImageView)c;
                CcHooks.tintImageViewForce(iv, -1);
                float a = iv.getAlpha();
                iv.setAlpha(a < 0.5f ? 0.45f : 0.85f);
                ++tinted;
            }
            int p = PAGEFIX_PROBE.incrementAndGet();
            if (p <= 10) {
                L.log(4, TAG, "L32 " + from + " dots=" + tinted + "/" + n);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static boolean hookNotifHeaderIcons(XposedModule module, ClassLoader cl) {
        try {
            Method m;
            Method m2;
            int ok = 0;
            try {
                Class<?> btn = Class.forName("com.android.internal.widget.NotificationExpandButton", false, cl);
                try {
                    m2 = btn.getDeclaredMethod("setDefaultTextColor", Integer.TYPE);
                    m2.setAccessible(true);
                    module.hook((Executable)m2).setId("cc_l33_expbtn_text").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> chain.proceed(new Object[]{-419430401}));
                    ++ok;
                    L.log(4, TAG, "L33 expandButton textColor hooked");
                }
                catch (Throwable t) {
                    L.log(5, TAG, "L33 setDefaultTextColor failed: " + t);
                }
                try {
                    m2 = btn.getDeclaredMethod("setDefaultPillColor", Integer.TYPE);
                    m2.setAccessible(true);
                    module.hook((Executable)m2).setId("cc_l33_expbtn_pill").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> chain.proceed(new Object[]{0x33FFFFFF}));
                    ++ok;
                    L.log(4, TAG, "L33 expandButton pillColor hooked");
                }
                catch (Throwable t) {
                    L.log(5, TAG, "L33 setDefaultPillColor failed: " + t);
                }
            }
            catch (Throwable t) {
                L.log(5, TAG, "L33 NotificationExpandButton not found: " + t);
            }
            try {
                m = ImageView.class.getDeclaredMethod("setColorFilter", Integer.TYPE, PorterDuff.Mode.class);
                m.setAccessible(true);
                module.hook((Executable)m).setId("cc_l33_imgcolorfilter").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    try {
                        ImageView iv;
                        Object self = chain.getThisObject();
                        if (self instanceof ImageView && CcHooks.isInNotificationDeep((View)self) && CcHooks.isSmallNotifIcon(iv = (ImageView)self)) {
                            return chain.proceed(new Object[]{-855638017, PorterDuff.Mode.SRC_IN});
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    return chain.proceed();
                });
                ++ok;
                L.log(4, TAG, "L33 ImageView colorFilter hooked");
            }
            catch (Throwable t) {
                L.log(5, TAG, "L33 setColorFilter failed: " + t);
            }
            L.log(4, TAG, "L33 notif header icons hooked=" + ok);
            try {
                Class<?> wrapper = Class.forName("com.android.systemui.statusbar.notification.row.wrapper.NotificationHeaderViewWrapper", false, cl);
                m2 = wrapper.getDeclaredMethod("resolveHeaderViews", new Class[0]);
                m2.setAccessible(true);
                module.hook((Executable)m2).setId("cc_l33_resolvehdr").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object r = chain.proceed();
                    try {
                        Object self = chain.getThisObject();
                        Field f = self.getClass().getDeclaredField("mAudiblyAlertedIcon");
                        f.setAccessible(true);
                        Object icon = f.get(self);
                        if (icon instanceof ImageView) {
                            ImageView iv = (ImageView)icon;
                            CcHooks.forceIconWhite(iv, -855638017);
                            L.log(4, TAG, "L33 audibly icon tinted(resolve) " + iv.getWidth() + "x" + iv.getHeight());
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    return r;
                });
                ++ok;
                L.log(4, TAG, "L33 resolveHeaderViews hooked");
            }
            catch (Throwable t) {
                L.log(5, TAG, "L33 resolveHeaderViews failed: " + t);
            }
            try {
                m = ImageView.class.getDeclaredMethod("setImageDrawable", Drawable.class);
                m.setAccessible(true);
                module.hook((Executable)m).setId("cc_l33_audibly_byid").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object r = chain.proceed();
                    try {
                        ImageView iv;
                        Object self = chain.getThisObject();
                        if (self instanceof ImageView && (iv = (ImageView)self).getId() == 16908828) {
                            CcHooks.forceIconWhite(iv, -855638017);
                            L.log(4, TAG, "L33 audibly icon tinted(byid)");
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    return r;
                });
                ++ok;
                L.log(4, TAG, "L33 audibly byId hooked");
            }
            catch (Throwable t) {
                L.log(5, TAG, "L33 audibly byId failed: " + t);
            }
            L.log(4, TAG, "L33 notif header icons hooked=" + ok);
            return ok > 0;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L33 hook failed: " + t);
            return false;
        }
    }

    private static boolean hookExpandButtonGlass(XposedModule module, ClassLoader cl) {
        L.log(4, TAG, "L34 disabled (superseded by L36)");
        return false;
    }

    @Deprecated
    private static boolean hookExpandButtonGlassUnused(XposedModule module, ClassLoader cl) {
        try {
            Class<?> btn = Class.forName("com.android.internal.widget.NotificationExpandButton", false, cl);
            Method m = btn.getDeclaredMethod("setDefaultPillColor", Integer.TYPE);
            m.setAccessible(true);
            module.hook((Executable)m).setId("cc_l34_expbtn_glass").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object r = chain.proceed(new Object[]{0x33FFFFFF});
                try {
                    Object self = chain.getThisObject();
                    if (self instanceof View) {
                        CcHooks.applyExpandButtonGlass(module, (View)self, cl);
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                return r;
            });
            L.log(4, TAG, "L34 expand button glass hooked");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L34 hook failed: " + t);
            return false;
        }
    }

    private static void applyExpandButtonGlass(XposedModule module, View v, ClassLoader sysuiCl) {
        try {
            if (!EXPBTN_GLASS_DONE.add(v)) {
                return;
            }
            int w = v.getWidth();
            int h = v.getHeight();
            if (w <= 0 || h <= 0) {
                v.post(() -> {
                    EXPBTN_GLASS_DONE.remove(v);
                    CcHooks.applyExpandButtonGlass(module, v, sysuiCl);
                });
                return;
            }
            L.log(4, TAG, "L34 expand btn size=" + w + "x" + h + " cls=" + v.getClass().getSimpleName());
            if (v instanceof ImageView) {
                CcHooks.tintImageViewForce((ImageView)v, -419430401);
            }
        }
        catch (Throwable t) {
            L.log(5, TAG, "L34 applyExpandButtonGlass failed: " + t);
        }
    }

    private static boolean hookDismissIcon(XposedModule module, ClassLoader cl) {
        try {
            Method m;
            Class<?> cls = Class.forName("com.miui.systemui.widget.CircleAndTickAnimView", false, cl);
            int ok = 0;
            try {
                m = cls.getDeclaredMethod("setNormalDrawable", Integer.TYPE);
                m.setAccessible(true);
                module.hook((Executable)m).setId("cc_l35_normal_int").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object r = chain.proceed();
                    try {
                        Object self = chain.getThisObject();
                        Field f = cls.getDeclaredField("mNormalDrawable");
                        f.setAccessible(true);
                        Object d = f.get(self);
                        if (d instanceof Drawable) {
                            CcHooks.forceDrawableWhite((Drawable)d);
                            L.log(4, TAG, "L35 normal drawable tinted(int)");
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    return r;
                });
                ++ok;
                L.log(4, TAG, "L35 setNormalDrawable(int) hooked");
            }
            catch (Throwable t) {
                L.log(5, TAG, "L35 setNormalDrawable(int) failed: " + t);
            }
            try {
                m = cls.getDeclaredMethod("setNormalDrawable", Drawable.class);
                m.setAccessible(true);
                module.hook((Executable)m).setId("cc_l35_normal_drawable").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    try {
                        Object a0 = chain.getArgs().get(0);
                        if (a0 instanceof Drawable) {
                            CcHooks.forceDrawableWhite((Drawable)a0);
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    return chain.proceed();
                });
                ++ok;
                L.log(4, TAG, "L35 setNormalDrawable(Drawable) hooked");
            }
            catch (Throwable t) {
                L.log(5, TAG, "L35 setNormalDrawable(Drawable) failed: " + t);
            }
            try {
                m = cls.getDeclaredMethod("setDrawables", Integer.TYPE, Integer.TYPE);
                m.setAccessible(true);
                module.hook((Executable)m).setId("cc_l35_setdrawables").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object r = chain.proceed();
                    try {
                        Object self = chain.getThisObject();
                        Field f = cls.getDeclaredField("mNormalDrawable");
                        f.setAccessible(true);
                        Object d = f.get(self);
                        if (d instanceof Drawable) {
                            CcHooks.forceDrawableWhite((Drawable)d);
                            L.log(4, TAG, "L35 normal drawable tinted(setDrawables)");
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    return r;
                });
                ++ok;
                L.log(4, TAG, "L35 setDrawables hooked");
            }
            catch (Throwable t) {
                L.log(5, TAG, "L35 setDrawables failed: " + t);
            }
            try {
                m = cls.getDeclaredMethod("onDraw", Canvas.class);
                m.setAccessible(true);
                module.hook((Executable)m).setId("cc_l35_ondraw").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    try {
                        Object self = chain.getThisObject();
                        Field f = cls.getDeclaredField("mNormalDrawable");
                        f.setAccessible(true);
                        Object d = f.get(self);
                        if (d instanceof Drawable) {
                            CcHooks.forceDrawableWhite((Drawable)d);
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    return chain.proceed();
                });
                ++ok;
                L.log(4, TAG, "L35 onDraw guard hooked");
            }
            catch (Throwable t) {
                L.log(5, TAG, "L35 onDraw failed: " + t);
            }
            L.log(4, TAG, "L35 dismiss icon hooked=" + ok);
            return ok > 0;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L35 hook failed: " + t);
            return false;
        }
    }

    private static void forceDrawableWhite(Drawable d) {
        try {
            if (d == null) {
                return;
            }
            d.mutate();
            d.setColorFilter(-1, PorterDuff.Mode.SRC_IN);
            d.setTint(-1);
            d.setTintMode(PorterDuff.Mode.SRC_IN);
            if (d instanceof LayerDrawable) {
                LayerDrawable ld = (LayerDrawable)d;
                for (int i = 0; i < ld.getNumberOfLayers(); ++i) {
                    Drawable layer = ld.getDrawable(i);
                    if (layer == null) continue;
                    layer.mutate();
                    layer.setColorFilter(-1, PorterDuff.Mode.SRC_IN);
                    layer.setTint(-1);
                }
            }
            d.invalidateSelf();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static boolean hookExpandButtonCircle(XposedModule module, ClassLoader cl) {
        try {
            Method m;
            Class<?> btn = Class.forName("com.android.internal.widget.NotificationExpandButton", false, cl);
            StringBuilder sb = new StringBuilder("L36 NotificationExpandButton methods:");
            for (Method dm : btn.getDeclaredMethods()) {
                sb.append(" ").append(dm.getName());
            }
            L.log(4, TAG, sb.toString());
            StringBuilder fb = new StringBuilder("L36 fields:");
            for (Field df : btn.getDeclaredFields()) {
                fb.append(" ").append(df.getName()).append(":").append(df.getType().getSimpleName());
            }
            L.log(4, TAG, fb.toString());
            int ok = 0;
            try {
                m = btn.getDeclaredMethod("setDefaultPillColor", Integer.TYPE);
                m.setAccessible(true);
                module.hook((Executable)m).setId("cc_l36_pill_transparent").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object r = chain.proceed(new Object[]{0});
                    try {
                        Object self = chain.getThisObject();
                        if (self instanceof View) {
                            CcHooks.applyPillCircle(module, (View)self, cl);
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    return r;
                });
                ++ok;
                L.log(4, TAG, "L36 pillColor->transparent hooked");
            }
            catch (Throwable t) {
                L.log(5, TAG, "L36 pillColor hook failed: " + t);
            }
            try {
                m = btn.getDeclaredMethod("setDefaultTextColor", Integer.TYPE);
                m.setAccessible(true);
                module.hook((Executable)m).setId("cc_l36_textcolor").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> chain.proceed(new Object[]{-1}));
                ++ok;
            }
catch (Throwable thrown2) {
                 // empty catch block
             }
             for (String mn : new String[]{"updateColors", "updateExpandedState", "updatePadding"}) {
                try {
                    Method m3 = btn.getDeclaredMethod(mn, new Class[0]);
                    m3.setAccessible(true);
                    String name = mn;
                    module.hook((Executable)m3).setId("cc_l36_" + mn).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                        Object r = chain.proceed();
                        try {
                            View pill;
                            Object self = chain.getThisObject();
                            if (self instanceof View && (pill = CcHooks.findFieldView((View)self, "mPillView")) != null) {
                                CcHooks.applyPillTransparentBg(pill);
                            }
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                        return r;
                    });
                    ++ok;
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            L.log(4, TAG, "L36 expand circle hooked=" + ok);
            try {
                Method ol = btn.getDeclaredMethod("onLayout", Boolean.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
                ol.setAccessible(true);
                module.hook((Executable)ol).setId("cc_l36_onlayout").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object r = chain.proceed();
                    try {
                        Object self = chain.getThisObject();
                        if (self instanceof View) {
                            CcHooks.applyPillCircle(module, (View)self, cl);
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    return r;
                });
                ++ok;
                L.log(4, TAG, "L36 expand onLayout hooked");
            }
            catch (Throwable t) {
                L.log(5, TAG, "L36 onLayout hook failed: " + t);
            }
            return ok > 0;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L36 hook failed: " + t);
            return false;
        }
    }

    private static void applyPillCircle(XposedModule module, View btn, ClassLoader sysuiCl) {
        block13: {
            try {
                View pill = CcHooks.findFieldView(btn, "mPillView");
                if (pill == null) {
                    L.log(5, TAG, "L36 mPillView not found");
                    return;
                }
                if (PILL_CIRCLE_DONE.add(pill)) {
                    try {
                        pill.setMinimumWidth(0);
                        pill.setMinimumHeight(0);
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    int pv = pill.getPaddingTop();
                    pill.setPadding(pv, pv, pv, pv);
                    try {
                        ViewGroup.LayoutParams lp = pill.getLayoutParams();
                        if (lp instanceof FrameLayout.LayoutParams) {
                            ((FrameLayout.LayoutParams)lp).gravity = 17;
                            pill.setLayoutParams(lp);
                        } else if (lp instanceof LinearLayout.LayoutParams) {
                            ((LinearLayout.LayoutParams)lp).gravity = 17;
                            pill.setLayoutParams(lp);
                        }
                    }
                    catch (Throwable lp) {
                        // empty catch block
                    }
                    pill.setClipToOutline(true);
                    pill.setOutlineProvider(new ViewOutlineProvider(){

                        public void getOutline(View v, Outline outline) {
                            outline.setOval(0, 0, v.getWidth(), v.getHeight());
                        }
                    });
                    boolean gel = CcHooks.applyPillGlass(module, pill, sysuiCl);
                    int pvFinal = pv;
                    pill.post(() -> {
                        int w = pill.getWidth();
                        int h = pill.getHeight();
                        L.log(4, TAG, "L36 pill circle " + w + "x" + h + " pad=" + pvFinal + " btn=" + btn.getWidth() + "x" + btn.getHeight() + " glass=" + gel);
                        if (w > 0 && h > 0 && Math.abs(w - h) > 2) {
                            int d = Math.min(w, h);
                            pill.setMinimumWidth(d);
                            pill.setMinimumHeight(d);
                            pill.requestLayout();
                        }
                    });
                    if (!gel) {
                        CcHooks.applyPillTransparentBg(pill);
                    }
                    break block13;
                }
                if (!PILL_GLASS_APPLIED.contains(pill)) {
                    pill.post(() -> {
                        if (!CcHooks.applyPillGlass(module, pill, sysuiCl)) {
                            CcHooks.applyPillTransparentBg(pill);
                        }
                    });
                }
            }
            catch (Throwable t) {
                L.log(5, TAG, "L36 applyPillCircle failed: " + t);
            }
        }
    }

    private static void applyPillTransparentBg(View pill) {
        try {
            // ★★★ v9.89：官方已挂玻璃时不许清背景 ★★★
            //  【日志实证】
            //    L36 pill OFFICIAL size=68x68 params=42 blur=80 blend=ff5a5a5a
            //    ← 官方 GroupCollapseButtonGlassEffect 已挂完整玻璃
            //    L36 pill circle ... glass=false
            //    ← 但这里把背景设成透明 oval，把玻璃抹了
            //  修法：pill 上有玻璃参数就完全不碰背景。
            if (PILL_GLASS_APPLIED.contains(pill)) {
                return;
            }
            if (CcHooks.pillHasOfficialGlass(pill)) {
                return;
            }
            GradientDrawable oval = new GradientDrawable();
            oval.setShape(1);
            oval.setColor(0);
            pill.setBackground((Drawable)oval);
            pill.invalidateOutline();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    // ★ v9.89：pill 上是否已有官方玻璃（查 MiGlass 参数 tag / materialType）
    private static boolean pillHasOfficialGlass(View pill) {
        try {
            Object tag = pill.getTag(TAG_MEDIA_PARAMS_OWNED);
            if (tag instanceof float[] && ((float[]) tag).length >= 20) {
                return true;
            }
        } catch (Throwable ignored) {
        }
        try {
            // 官方 GroupCollapseButtonGlassEffect 会 setMiViewMaterialTypeCompat(1)
            Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, pill.getClass().getClassLoader());
            for (java.lang.reflect.Method mm : miGlass.getDeclaredMethods()) {
                if (!mm.getName().startsWith("getMiViewMaterialTypeCompat") || mm.getParameterTypes().length != 1) {
                    continue;
                }
                Object rv = mm.invoke(null, pill);
                if (rv instanceof Integer && ((Integer) rv) == 1) {
                    return true;
                }
            }
        } catch (Throwable ignored) {
        }
        return false;
    }
    private static float[] glassTemplate(int idx) {
        if (idx < 0 || idx >= GLASS_TEMPLATES.length) {
            idx = 1;
        }
        return (float[])GLASS_TEMPLATES[idx].clone();
    }

    private static void refreshGlassParams() {
        long now = SystemClock.elapsedRealtime();
        if (now - GLASS_PARAM_AT < 800L) {
            return;
        }
        GLASS_PARAM_AT = now;
        GC_CONTRAST = CcHooks.clampInt(CcHooks.parseInt(CcHooks.getProp(PROP_G_CONTRAST, "50"), 50), 0, 100);
        GC_TINT = CcHooks.clampInt(CcHooks.parseInt(CcHooks.getProp(PROP_G_TINT, "25"), 25), 0, 100);
        GC_BLUR = CcHooks.clampInt(CcHooks.parseInt(CcHooks.getProp(PROP_G_BLUR, "80"), 80), 0, 150);
    }

    private static int clampInt(int v, int lo, int hi) {
        return v < lo ? lo : (v > hi ? hi : v);
    }

    /** 取调用栈前 n 层（用于观察谁在改参数）。 */
    private static String shortStack(int depth) {
        try {
            StackTraceElement[] st = Thread.currentThread().getStackTrace();
            StringBuilder sb = new StringBuilder();
            int cnt = 0;
            for (StackTraceElement e : st) {
                String cn = e.getClassName();
                if (cn.startsWith("java.") || cn.startsWith("android.") || cn.startsWith("dalvik.")
                        || cn.startsWith("com.mersand.hyperclassic.CcHooks")) {
                    continue;
                }
                if (cnt >= depth) break;
                if (cnt > 0) sb.append(" <- ");
                String simple = cn.substring(cn.lastIndexOf('.') + 1);
                sb.append(simple).append('.').append(e.getMethodName());
                cnt++;
            }
            return sb.toString();
        } catch (Throwable t) {
            return "?";
        }
    }

    private static float clampF(float v, float lo, float hi) {
        return v < lo ? lo : (v > hi ? hi : v);
    }

    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s.trim());
        }
        catch (Throwable t) {
            return def;
        }
    }

    private static void applyGuiToGlassParams(float[] p) {
        CcHooks.refreshGlassParams();
        if (p == null || p.length < 3) {
            return;
        }
        int n = p.length;
        float c = (float)GC_CONTRAST / 100.0f;
        float t = (float)GC_TINT / 100.0f;
        p[0] = 0.72f - c * 0.6f;
        p[1] = 0.16f + t * 1.84f;
        if (n > 2) {
            p[2] = 0.03f + t * 0.27f;
        }
        if (n > 19) {
            p[19] = 90.0f - t * 50.0f;
        }
        if (n > 21 && GC_BLUR > 0) {
            p[21] = GC_BLUR;
        }
        if ("1".equals(CcHooks.getProp("debug.hyperclassic.no_refract", "0").trim())) {
            if (n > 25) {
                p[25] = 0.0f;
            }
            if (n > 26) {
                p[26] = 1.0f;
            }
            if (n > 27) {
                p[27] = 0.0f;
            }
            if (n > 28) {
                p[28] = 1.0f;
            }
        }
    }

    private static int sliderBlurRadius() {
        try {
            int b = CcHooks.currentBlurRadius();
            if (b <= 0) {
                return 80;
            }
            return Math.max(8, Math.min(150, b));
        }
        catch (Throwable ignored) {
            return 80;
        }
    }

    private static void dumpBlurState(XposedModule module, View v, String tag) {
        try {
            ClassLoader cl = v.getClass().getClassLoader();
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, cl);
            Object pass = null;
            Object mode = null;
            Object radius = null;
            Object viewMode = null;
            try {
                pass = miBlur.getMethod("getPassWindowBlurEnabledCompat", View.class).invoke(null, v);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                mode = miBlur.getMethod("getMiBackgroundBlurModeCompat", View.class).invoke(null, v);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                radius = miBlur.getMethod("getMiBackgroundBlurRadiusCompat", View.class).invoke(null, v);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            Drawable bg = null;
            try {
                bg = v.getBackground();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            String oc = "?";
            try {
                Outline o = new Outline();
                ViewOutlineProvider p = v.getOutlineProvider();
                if (p != null) {
                    p.getOutline(v, o);
                    oc = "r=" + (int)o.getRadius();
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            L.log(4, TAG, "DUMP[" + tag + "] " + v.getClass().getSimpleName() + " " + v.getWidth() + "x" + v.getHeight() + " pass=" + pass + " blurMode=" + mode + " radius=" + radius + " bg=" + (bg == null ? "null" : bg.getClass().getSimpleName()) + " clip=" + v.getClipToOutline() + " outline[" + oc + "] alpha=" + v.getAlpha());
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static int glassBlurRadiusDp() {
        try {
            int b = CcHooks.currentBlurRadius();
            return Math.max(0, Math.min(40, b));
        }
        catch (Throwable ignored) {
            return 20;
        }
    }

    private static void applyGlassBlurRadius(XposedModule module, ClassLoader sysuiCl, View v, int blurDp) {
        try {
            Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, sysuiCl);
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, sysuiCl);
            if (MI_GLASS_SET_PARAMS == null) {
                try {
                    MI_GLASS_SET_PARAMS = miGlass.getMethod("setMiGlassCompat", View.class, float[].class);
                    MI_GLASS_SET_TYPE = miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            if (MI_BLUR_SET_BLEND == null) {
                try {
                    MI_BLUR_SET_BLEND = miBlur.getMethod("setMiBackgroundBlendColors$default", View.class, int[].class);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            int r = CcHooks.sliderBlurRadius();
            boolean gOk = false;
            boolean bOk = false;
            try {
                miGlass.getMethod("setMiGlassBlurRadius", View.class, Integer.TYPE, Integer.TYPE).invoke(null, v, r, r);
                gOk = true;
            }
            catch (Throwable t) {
                gErr = String.valueOf(t);
            }
            try {
                miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class).invoke(null, r, v);
                miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
                bOk = true;
            }
            catch (Throwable t) {
                bErr = String.valueOf(t);
            }
            int n = BLUR_APPLY_COUNT.incrementAndGet();
            if (n <= 8) {
                L.log(4, TAG, "blurRadius r=" + r + " glass=" + gOk + " bg=" + bOk + " view=" + v.getClass().getSimpleName() + " " + v.getWidth() + "x" + v.getHeight() + (gErr != null ? " gErr=" + gErr : "") + (bErr != null ? " bErr=" + bErr : ""));
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static boolean hookBlurRadiusOverride(XposedModule module, ClassLoader cl) {
        boolean ok1 = false;
        boolean ok2 = false;
        try {
            Method m = null;
            for (Method dm : View.class.getDeclaredMethods()) {
                if (!"setMiGlassBlurRadius".equals(dm.getName()) || dm.getParameterCount() != 2 || dm.getParameterTypes()[0] != Integer.TYPE || dm.getParameterTypes()[1] != Integer.TYPE) continue;
                m = dm;
                break;
            }
            if (m != null) {
                m.setAccessible(true);
                Method fm = m;
                module.hook((Executable)fm).setId("cc_l47_glass_blur").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    try {
                        // ★★★ v9.74：RG 接管的 View 不许被改写玻璃半径 ★★★
                        //
                        //  【日志实证】L47 glassBlur override 6 次
                        //    → 把 RG 的 setMiGlassBlurRadius(0,0) 改成 80
                        //    → 玻璃折射层被磨砂糊住
                        //  【官方源码】MediaViewGlassEffect 从不调 setMiGlassBlurRadius，
                        //    折射半径在 42 项数组的 p21=80 里。所以这里必须放行原值。
                        Object selfV = chain.getThisObject();
                        // ★ static 方法取不到 thisObject，必须同时看参数
                        //   setMiGlassBlurRadius 实为 View 上的非静态方法，
                        //   但链式调用里可能来自不同入口，两处都判一次。
                        if (rgOwned(selfV instanceof View ? (View) selfV : null)) {
                            return chain.proceed();
                        }
                        // 兜底：若 thisObject 不是 View，尝试从参数里找不到的就直接放行原值
                        try {
                            java.lang.reflect.Method mm = null;
                            Object[] aa = chain.getArgs().toArray();
                            for (Object a : aa) {
                                if (a instanceof View && rgOwned((View) a)) {
                                    if (L47_GLASS_COUNT.incrementAndGet() <= 6) {
                                        L.log(4, TAG, "L47 glassBlur bypass (rg arg) " + chain.getArg(0));
                                    }
                                    return chain.proceed();
                                }
                            }
                        } catch (Throwable ignored2) {
                        }
                        if ("1".equals(CcHooks.getProp("debug.hyperclassic.force_blur", "1").trim())) {
                            String cn;
                            Object self = chain.getThisObject();
                            if (self instanceof View && ((cn = self.getClass().getSimpleName()).contains("Brightness") || cn.contains("SeekBar") || cn.contains("Slider"))) {
                                return chain.proceed();
                            }
                            int r = CcHooks.sliderBlurRadius();
                            int n = L47_GLASS_COUNT.incrementAndGet();
                            if (n <= 6) {
                                L.log(4, TAG, "L47 glassBlur override " + chain.getArg(0) + "->" + r + " arg1=" + chain.getArg(1) + " view=" + (self == null ? "?" : self.getClass().getSimpleName()));
                            }
                            return chain.proceed(new Object[]{r, r});
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    return chain.proceed();
                });
                ok1 = true;
                L.log(4, TAG, "L47 hooked View.setMiGlassBlurRadius");
            } else {
                L.log(5, TAG, "L47 setMiGlassBlurRadius not found");
            }
        }
        catch (Throwable t) {
            L.log(5, TAG, "L47 glass blur hook failed: " + t);
        }
        try {
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, cl);
            Method m = miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class);
            m.setAccessible(true);
            Method fm2 = m;
            module.hook((Executable)fm2).setId("cc_l47_bg_blur").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                try {
                    // ★★★ v9.74：RG 接管的 View 不许被强制改写背景模糊半径 ★★★
                    //
                    //  【日志实证】L47 bgBlur override 6 次
                    //    → 把 RG 的 setMiBackgroundBlurRadiusCompat(0) 改成 80
                    //    → 通知卡片立刻回到磨砂（托盘因 applyQSTray 被跳过而幸免）
                    Object vOwn = chain.getArg(1);
                    if (rgOwned(vOwn instanceof View ? (View) vOwn : null)) {
                        if (L47_BG_COUNT.incrementAndGet() <= 6) {
                            L.log(4, TAG, "L47 bgBlur bypass (rg owns) " + chain.getArg(0));
                        }
                        return chain.proceed();
                    }
                    if ("1".equals(CcHooks.getProp("debug.hyperclassic.force_blur", "1").trim())) {
                        String cn;
                        Object vObj = chain.getArg(1);
                        if (vObj instanceof View && ((cn = vObj.getClass().getSimpleName()).contains("Brightness") || cn.contains("SeekBar") || cn.contains("Slider"))) {
                            return chain.proceed();
                        }
                        // ★ v9.94：Toast 玻璃自己管理模糊，L47 不许改（会把 bgR 改回 80 盖住玻璃）
                        if (vObj instanceof View && ((View) vObj).getClass().getSimpleName().contains("Toast")) {
                            return chain.proceed();
                        }
                        int bgR = CcHooks.sliderBlurRadius();
                        int n = L47_BG_COUNT.incrementAndGet();
                        if (n <= 6) {
                            L.log(4, TAG, "L47 bgBlur override " + chain.getArg(0) + "->" + bgR + " view=" + (vObj == null ? "?" : vObj.getClass().getSimpleName()));
                        }
                        return chain.proceed(new Object[]{bgR, chain.getArg(1)});
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                return chain.proceed();
            });
            ok2 = true;
            L.log(4, TAG, "L47 hooked setMiBackgroundBlurRadiusCompat");
        }
        catch (Throwable t) {
            L.log(5, TAG, "L47 bg blur hook failed: " + t);
        }
        return ok1 || ok2;
    }

    private static int[] buildBlendColors() {
        int tintColor;
        CcHooks.refreshGlassParams();
        int contrast = GC_CONTRAST;
        int gray = 90 + contrast * 78 / 100;
        int base = 0xFF000000 | gray << 16 | gray << 8 | gray;
        if (GC_TINT < 50) {
            float r = (float)GC_TINT / 50.0f;
            tintColor = CcHooks.lerpColor(-10040065, -4980992, r);
        } else {
            float r = (float)(GC_TINT - 50) / 50.0f;
            tintColor = CcHooks.lerpColor(-4980992, -11699, r);
        }
        return new int[]{base, 107, tintColor, 106};
    }

    private static float buildBlendAlpha() {
        CcHooks.refreshGlassParams();
        return 0.15f + (float)GC_CONTRAST / 100.0f * 0.85f;
    }

    private static int lerpColor(int c1, int c2, float t) {
        int a1 = c1 >>> 24 & 0xFF;
        int r1 = c1 >> 16 & 0xFF;
        int g1 = c1 >> 8 & 0xFF;
        int b1 = c1 & 0xFF;
        int a2 = c2 >>> 24 & 0xFF;
        int r2 = c2 >> 16 & 0xFF;
        int g2 = c2 >> 8 & 0xFF;
        int b2 = c2 & 0xFF;
        int a = (int)((float)a1 + (float)(a2 - a1) * t);
        int r = (int)((float)r1 + (float)(r2 - r1) * t);
        int g = (int)((float)g1 + (float)(g2 - g1) * t);
        int b = (int)((float)b1 + (float)(b2 - b1) * t);
        return a << 24 | r << 16 | g << 8 | b;
    }

    private static int currentBlurRadius() {
        CcHooks.refreshGlassParams();
        return GC_BLUR;
    }

    private static void tryEnableToastWindowBlur(XposedModule module, View v) {
        try {
            View root = v.getRootView();
            if (root == null) {
                return;
            }
            if (!(root.getLayoutParams() instanceof WindowManager.LayoutParams)) {
                return;
            }
            WindowManager.LayoutParams lp = (WindowManager.LayoutParams)root.getLayoutParams();
            int before = lp.flags;
            lp.flags |= 4;
            int radius = CcHooks.currentBlurRadius();
            if (radius <= 0) {
                radius = 80;
            }
            try {
                Field f = lp.getClass().getField("blurBehindRadius");
                f.setAccessible(true);
                f.setInt(lp, radius);
            }
            catch (Throwable f) {
                // empty catch block
            }
            WindowManager wm = (WindowManager)v.getContext().getSystemService("window");
            if (wm == null) {
                return;
            }
            wm.updateViewLayout(root, (ViewGroup.LayoutParams)lp);
            L.log(4, TAG, "L44 toast window blur: flags 0x" + Integer.toHexString(before) + " -> 0x" + Integer.toHexString(lp.flags) + " radius=" + radius);
        }
        catch (Throwable t) {
            L.log(5, TAG, "L44 toast window blur failed: " + t);
        }
    }

    private static boolean hookToastGlass(XposedModule module, ClassLoader cl, boolean enableGlass) {
        boolean any = false;
        return any |= CcHooks.hookToastClass(module, cl, "com.miui.systemui.widget.ToastLinearLayout", enableGlass);
    }

    private static boolean hookToastClass(XposedModule module, ClassLoader cl, String cls, boolean enableGlass) {
        try {
            Class<?> c = Class.forName(cls, false, cl);
            Method target = null;
            for (Method m : c.getDeclaredMethods()) {
                if (!"onMeasure".equals(m.getName()) || m.getParameterCount() != 2) continue;
                target = m;
                break;
            }
            if (target == null) {
                L.log(5, TAG, "L44 onMeasure not found in " + cls);
                return false;
            }
            target.setAccessible(true);
            module.hook((Executable)target).setId("cc_l44_toast").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object r = chain.proceed();
                if (!enableGlass) {
                    return r;
                }
                try {
                    Object self = chain.getThisObject();
                    if (!(self instanceof View)) {
                        return r;
                    }
                    View v = (View)self;
                    if (TOAST_DONE.contains(v)) {
                        return r;
                    }
                    if (v.getMeasuredWidth() <= 0 || v.getMeasuredHeight() <= 0) {
                        return r;
                    }
                    TOAST_DONE.add(v);
                    v.post(() -> {
                        block6: {
                            try {
                                if (!CcHooks.regionOn(REGION_TOAST)) {
                                    return;
                                }
                                // ★★★ v143：只走一条路线，彻底删掉另外两套 ★★★
                                //
                                //  【v142 的教训：三套配方在抢同一个 View】
                                //    L44 toast window blur: flags 0x1000098 -> 0x100009c radius=80
                                //    L44 toast UR UR applied bg=#0DFFFFFF blend=#40FFFFFF
                                //    L44 toast glass HU p0=0.5 p1=1.0 p3=0.8
                                //    三条同时跑，后执行的覆盖前面的 → 观感混乱、
                                //    而且 UR 那条的 bg=#0DFFFFFF 就是「先灰」的来源。
                                //
                                //  【为什么删窗口模糊】
                                //    TYPE_TOAST 窗口的 PassBlur 拿不到采样源
                                //    （v10.x 已用 include 0 / mergeSnapshot 实证），
                                //    它只会给窗口加个 flag，然后把 lp.blurBehindRadius=80，
                                //    反而干扰 MiGlass 的采样。
                                //
                                //  【为什么删 UR 配方】
                                //    那是「亮度条」的磨砂配方（bg=#0DFFFFFF + blend=#40FFFFFF），
                                //    和玻璃完全无关，纯粹是历史残留，默认也不该跑。
                                //
                                //  【为什么在 post 里再套一层 post】
                                //    onMeasure 拦截时 v.post 只是「下一帧」，
                                //    此时 Toast 的进入动画（alpha 0→1, scale 0.75→1）刚开始。
                                //    ★ 主人观察到「先灰色，然后展开动画中被设透明」——
                                //      「先灰」= 我们在动画开始前就挂好了玻璃；
                                //      「动画中变透明」= 动画的 alpha 插值影响了
                                //      SurfaceControl 层的整体透明度，玻璃一起被淡出。
                                //    所以要在**动画结束后**再挂一次，确保材质在 alpha=1 时生效。
                                //
                                //  【延迟策略】
                                //    第一遍：post 立即挂（保证不闪白）
                                //    第二遍：动画时长 + 余量（默认 260ms，动画是 200ms）
                                //    可用 debug.hyperclassic.toast_delay 调第二遍延迟。
                                CcHooks.applyToastGlassV142(module, v, cl, "L44 toast first");
                                // ★ v145：恢复窗口模糊 —— 它是 pass=true 的唯一来源
                                //
                                //  【v143 的错误】
                                //    删掉 tryEnableToastWindowBlur 后，探针显示 pass=false，
                                //    玻璃彻底不渲染 → 主人看到"纯透明"。
                                //  【为什么它重要】
                                //    它给 Toast 窗口打 FLAG_BLUR_BEHIND + blurBehindRadius，
                                //    这是 View 侧 passWindowBlur 能成立的前提。
                                //    即便 SF 快照本身采样为空，这个 flag 也决定了
                                //    MiGlass 会不会走"取快照"这条渲染分支。
                                if (parseIntSafe(getProp("debug.hyperclassic.toast_winblur", "1").trim(), 1) != 0) {
                                    CcHooks.tryEnableToastWindowBlur(module, v);
                                }
                                int delayMs = parseIntSafe(getProp("debug.hyperclassic.toast_delay", "260").trim(), 260);
                                if (delayMs > 0) {
                                    v.postDelayed(() -> {
                                        try {
                                            if (v.isAttachedToWindow() && v.getAlpha() > 0.99f) {
                                                CcHooks.applyToastGlassV142(module, v, cl, "L44 toast animEnd");
                                            } else {
                                                L.log(4, TAG, "L44 toast animEnd skip alpha=" + v.getAlpha());
                                            }
                                        } catch (Throwable ignored) {
                                        }
                                    }, delayMs);
                                }
                                int n = TOAST_COUNT.incrementAndGet();
                                if (n <= 6) {
                                    L.log(4, TAG, "L44 toast glass #" + n + " " + v.getWidth() + "x" + v.getHeight()
                                            + " cls=" + v.getClass().getSimpleName() + " mode=HU-glass-only");
                                }
                            }
                            catch (Throwable t) {
                                L.log(5, TAG, "L44 toast apply failed: " + t);
                            }
                        }
                    });
                }
                catch (Throwable t) {
                    L.log(5, TAG, "L44 toast intercept failed: " + t);
                }
                return r;
            });
            L.log(4, TAG, "L44 hooked " + cls);
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L44 toast hook failed " + cls + ": " + t);
            return false;
        }
    }

    // ★★★ v9.89b：Toast 窗口模糊玻璃（官方路线，只调透明度）★★★
    //
    //  【官方资源实证】
    //    res/layout/text_toast.xml:
    //      ToastLinearLayout android:background="@android:drawable/toast_frame"
    //    res/drawable/toast_frame_new.xml:
    //      <corners android:radius="@dimen/toast_bg_radius" />
    //      <solid android:color="@color/toast_bg_color" />     ← 不透明实色
    //
    //  【为什么之前叠两层 + 圆角不一致】
    //    ensureToastBlurBg 先设官方 drawable（radius=toast_bg_radius），
    //    这里又 setBackground(GradientDrawable radius=h/2)，
    //    两个圆角不同 → 观感「叠了两层」。
    //
    //  【为什么没有背景模糊】
    //    官方背景是**不透明实色**，把窗口 blurBehind 完全遮住了。
    //    ToastLinearLayout.onMeasure 里那个 getBackgroundBlurOpened
    //    只是决定最大宽度，与渲染无关。
    //
    //  【正确修法】
    //    不替换背景，只把官方 drawable 的 solid 颜色改成半透明，
    //    圆角 / padding / size 全部保留 → 窗口模糊自然透出。
    //    alpha 用 debug.hyperclassic.toast_alpha 调（0~255，默认 96）。
/**
 * ★ v142：Toast 玻璃 —— 严格照搬「悬浮通知」的完整配方。
 *
 *  【为什么参考悬浮通知】
 *    主人指定：悬浮通知同样是「在一个全新窗口里创建玻璃实例」，
 *    和 Toast 的处境一致（都是 wrap_content 的独立窗口）。
 *
 *  【v10.x 为什么失败】
 *    当时走的是「窗口模糊 PassBlur」：flags |= FLAG_BLUR_BEHIND + blurBehindRadius=80。
 *    实测 TYPE_TOAST 窗口的 PassBlur 拿不到采样源
 *    （include 0 / mergeSnapshot 直接 return），必然失败。
 *
 *  【v142 的路线】
 *    不走窗口级 PassBlur，改走 MiGlass 的 View 级材质，
 *    但**参数和流程全部换成悬浮通知的**：
 *
 *    官方 HeadsUpNotificationGlassEffect.apply() 顺序：
 *      ① HeadsUpNotificationBlurEffect.apply():
 *           setMiViewMaterialTypeCompat(0, view)          ← 先归零
 *           setCustomBackground(notification_heads_up_transparent_bg)
 *           applyElementViewBlend(ctx, view, false, c1,m1,c2,m2, false)
 *      ② setMiGlassCompat(view, HEADS_UP_GLASS_PARAMS)
 *      ③ setMiViewMaterialTypeCompat(1, view)
 *
 *    注意：全程**不调用** setPassWindowBlurEnabledCompat(false)。
 *      而旧的 applyReflectiveGlass 第 ④ 步恰恰会把它关掉 ——
 *      这就是「玻璃挂上但过于透明」的元凶。
 */
private static void applyToastGlassV142(XposedModule module, View v, ClassLoader cl, String tag) {
    try {
        int w = v.getWidth() > 0 ? v.getWidth() : v.getMeasuredWidth();
        int h = v.getHeight() > 0 ? v.getHeight() : v.getMeasuredHeight();
        if (w <= 0 || h <= 0) {
            L.log(5, TAG, tag + " skip size=0");
            return;
        }
        Context ctx = v.getContext();
        Resources res = v.getResources();
        Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, cl);
        Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, cl);

        // ---------- ① 归零材质（官方 BlurEffect.apply 第一步）----------
        try {
            miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class)
                    .invoke(null, 0, v);
        } catch (Throwable ignored) {
        }

        // ---------- ② 背景：自绘圆角 shape（与玻璃半径完全一致）----------
        //  【v147 反馈】「玻璃渲染的圆角大于 toast 原本圆角，
        //    玻璃外部有白色色块残留，仅在背景为白色时可见」
        //
        //  【根因】两套圆角不一致：
        //    玻璃 = 我们设的 outline（radPx = toast_bg_radius = 16dp）
        //    背景 = toast_frame_new 这个 shape drawable 自带的 corners
        //    而 text_toast.xml 里官方背景其实是 @android:drawable/toast_frame
        //    （framework-res 里的 .9.png），根本不是 toast_frame_new。
        //    我们把 toast_frame_new 套上去，它的圆角/尺寸和玻璃对不上，
        //    于是玻璃（圆角更大）以外就露出了背景的白色块。
        //
        //  【修法】不再复用官方 drawable，改成「自己画」：
        //    ① 用 GradientDrawable，圆角半径**与玻璃 outline 用同一个值**
        //       → 两者形状 100% 重合，不可能露出色块
        //    ② padding 手动从官方 dimens 读（toast_padding_horizontal /
        //       toast_padding_portrait），保证文字不顶边
        //    ③ 颜色由 toast_backing 控制，同时可被 toast_radius 调圆角
        //
        //  【可调】
        //    debug.hyperclassic.toast_radius   圆角 dp（默认 16，与官方一致）
        //    debug.hyperclassic.toast_backing  底色 alpha（0~255，默认 26）
        int radDp = parseIntSafe(getProp("debug.hyperclassic.toast_radius", "16").trim(), 16);
        float radPx = radDp * res.getDisplayMetrics().density;
        // padding：从官方 dimens 读，读不到就用官方默认值
        int padH = Math.round(20f * res.getDisplayMetrics().density);
        int padV = Math.round(15.5f * res.getDisplayMetrics().density);
        try {
            int ridH = res.getIdentifier("toast_padding_horizontal", "dimen", "com.android.systemui");
            if (ridH > 0) padH = res.getDimensionPixelSize(ridH);
            int ridV = res.getIdentifier("toast_padding_portrait", "dimen", "com.android.systemui");
            if (ridV > 0) padV = res.getDimensionPixelSize(ridV);
        } catch (Throwable ignored) {
        }
// ★★★ v162：彻底消除白边 —— 背景内缩到玻璃内侧 + 默认透明 ★★★
        //
        //  【主人反馈】「效果可以了，但还需修复之前提到的白边」
        //
        //  【白边的成因（结合 v155 的实测）】
        //    主人之前把 backing 拉到 100，观感**毫无变化**
        //    → 证明背景 drawable 被玻璃层完全盖住，看不见。
        //    既然看不见，那白边只可能来自：
        //      「玻璃没覆盖到的角落」露出了一点点背景。
        //
        //    玻璃是 SDF 圆角矩形，背景是 GradientDrawable 圆角矩形。
        //    即使两者都设成 60px，它们的**抗锯齿边缘也不可能像素级重合**：
        //      · SDF 是着色器算的，边缘有 1~2px 过渡带
        //      · GradientDrawable 是 Canvas.drawRoundRect，边缘抗锯齿方式不同
        //    → 在圆角外沿形成一圈 1~2px 的缝隙，背景从缝里漏出来 → 白边
        //
        //  【修法（双保险）】
        //    ① 背景**内缩** insetPx 像素，圆角同步减小
        //       → 背景永远严格位于玻璃内侧，物理上不可能漏到外面
        //    ② 默认 alpha 改 0（全透明）
        //       → 即使还有 1px 缝隙，也是透明的，不会有白边
        //       反正 backing 本来就被玻璃盖住看不见，设 0 无损失
        //
        //  【可调】
        //    debug.hyperclassic.toast_backing     0~255（默认 0 全透明）
        //    debug.hyperclassic.toast_bg_inset    内缩像素（默认 2）
        //    debug.hyperclassic.toast_bgcolor     RRGGBB（默认 818181）
        int backAlpha = parseIntSafe(getProp("debug.hyperclassic.toast_backing", "0").trim(), 0);
        if (backAlpha < 0) backAlpha = 0;
        if (backAlpha > 255) backAlpha = 255;

        int backRgb = parseIntSafeHex(
                getProp("debug.hyperclassic.toast_bgcolor", "818181").trim(), 0x818181);
        backRgb &= 0xFFFFFF;

        int bgInset = parseIntSafe(
                getProp("debug.hyperclassic.toast_bg_inset", "2").trim(), 2);
        if (bgInset < 0) bgInset = 0;
        if (bgInset > Math.round(radPx)) bgInset = Math.round(radPx);

        try {
            GradientDrawable gd = new GradientDrawable();
            gd.setShape(GradientDrawable.RECTANGLE);
            // 圆角同步减小，保证内缩后形状与玻璃同心
            gd.setCornerRadius(Math.max(0f, radPx - bgInset));
            gd.setColor((backAlpha << 24) | backRgb);

            Drawable bgDrawable;
            if (bgInset > 0) {
                bgDrawable = new InsetDrawable(gd, bgInset, bgInset, bgInset, bgInset);
            } else {
                bgDrawable = gd;
            }
            v.setBackground(bgDrawable);
            // padding 必须手动设，因为我们不再依赖 drawable 自带的 <padding>
            v.setPadding(padH, padV, padH, padV);
            L.log(4, TAG, tag + " bg=self-drawn rad=" + radDp + "dp(" + Math.round(radPx)
                    + "px) pad=" + padH + "x" + padV
                    + " alpha=" + backAlpha + " rgb=#" + String.format("%06X", backRgb)
                    + " inset=" + bgInset);
        } catch (Throwable tb) {
            L.log(5, TAG, tag + " bg self-drawn failed: " + tb);
        }

        // ---------- ②b 文字锁定白色 ----------
        //  【主人要求】「把文字颜色锁定为白色」
        //
        //  【为什么需要】
        //    text_toast.xml 里 TextView 的 android:textColor="@null"，
        //    即跟随主题/系统 token，深色浅色模式下会自动变色。
        //    玻璃背景本身是半透明灰，文字如果跟着变成深色就完全看不清。
        //
        //  【做法】
        //    递归遍历 ToastLinearLayout 的所有子 View，
        //    把 TextView 一律设成纯白，同时清掉 ColorStateList
        //    （否则 setTextColor(int) 会被 state 覆盖回去）。
        //
        //  【可调】debug.hyperclassic.toast_text    RRGGBBAA（默认 FFFFFFFF 纯白）
        int textColor = parseIntSafeHex(
                getProp("debug.hyperclassic.toast_text", "FFFFFFFF").trim(), 0xFFFFFFFF);
        try {
            int cnt = forceTextColor(v, textColor);
            L.log(4, TAG, tag + " text locked #" + String.format("%08X", textColor)
                    + " n=" + cnt);
        } catch (Throwable tt) {
            L.log(5, TAG, tag + " text lock failed: " + tt);
        }

        // 圆角 outline（玻璃 SDF 按这个裁剪）
        try {
            final float fr = radPx;
            v.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View vv, Outline o) {
                    o.setRoundRect(0, 0, vv.getWidth(), vv.getHeight(), fr);
                }
            });
            v.setClipToOutline(true);
            v.invalidateOutline();
        } catch (Throwable ignored) {
        }

        // ---------- ③ blend：三组半透明配方（照抄 window_background_blend_colors_bionics）----------
        //  【主人问题】「查一下通知玻璃的系数是怎么设置的」
        //
        //  【Coding酱的查证结果】
        //    官方对 blend 系数（setMiBackgroundBlendColors 第三个 float）
        //    的处理方式：
        //      · NotificationRowBlurEffect / MediaViewBlurEffect / ModalWindowManager
        //        → 一律用 $default，即写死 1.0f
        //      · 只有 KeyguardMoveHelper（锁屏上滑渐显）
        //        → coerceIn(progress, 0f, 1f)，动态 0~1
        //    → 静态玻璃元素**不用系数调透度**，系数只是给动画用的乘数。
        //
        //  【那透度靠什么？】
        //    靠颜色自带的 alpha！官方托盘窗口背景用的是：
        //      0x7f030128  window_background_blend_colors_bionics
        //        #73212121 / 28    ← alpha 0x73 = 45% 深灰
        //        #247c7c7c / 120   ← alpha 0x24 = 14% 中灰
        //        #306e6e6e / 3     ← alpha 0x30 = 19% 灰
        //      0x7f030127  window_background_blend_colors（非玻璃模式）
        //        #52808080 / 28    ← alpha 0x52 = 32%
        //
        //  【为什么之前偏白/偏透】
        //    Coding酱用的 notification_element_blend_shade_* 两个颜色
        //    alpha 都是 **0xff（完全不透明）**，只能靠混合模式去运算。
        //    而玻璃真正需要的是「自带 alpha 的灰」，
        //    alpha 才直接决定「能透出多少底下的内容」。
        //
        //  【本版做法】
        //    直接读官方 bionics 数组（0x7f030128），原样传进去。
        //    每一组的 alpha 都可用 prop 现场缩放，不用重编译。
        //
        //  【可调】
        //    debug.hyperclassic.toast_blend_alpha  全局 alpha 乘数（默认 1.0）
        //    debug.hyperclassic.toast_blend_src    bionics(默认) / normal / shade
        try {
            int[] colors = null;
            // ★ 默认 shade：这是 v154 已确认「颜色正确」的配方，不能动
            //   bionics / normal 只是给主人做透度对照实验用的备选
            String src = getProp("debug.hyperclassic.toast_blend_src", "shade").trim();

            if ("shade".equals(src)) {
                // 退回 v154 的两组（不透明，只靠混合模式）
                int c1 = res.getColor(res.getIdentifier(
                        "notification_element_blend_shade_color_1", "color", "com.android.systemui"));
                int c2 = res.getColor(res.getIdentifier(
                        "notification_element_blend_shade_color_2", "color", "com.android.systemui"));
                colors = new int[]{c1, 107, c2, 106};
            } else {
                int arrId = "normal".equals(src)
                        ? res.getIdentifier("window_background_blend_colors", "array", "com.android.systemui")
                        : res.getIdentifier("window_background_blend_colors_bionics", "array", "com.android.systemui");
                if (arrId > 0) {
                    int[] a = res.getIntArray(arrId);
                    if (a != null && a.length >= 4) colors = a;
                }
                if (colors == null) {
                    // 兜底：官方 bionics 实测值
                    colors = new int[]{0x73212121, 28, 0x247C7C7C, 120, 0x306E6E6E, 3};
                }
            }

            // 全局 alpha 乘数：只缩放 alpha 通道，不改 RGB
            float blendAlpha = 1.0f;
            try {
                String bs = getProp("debug.hyperclassic.toast_blend_alpha", "1.0").trim();
                if (!bs.isEmpty()) blendAlpha = Float.parseFloat(bs);
            } catch (Throwable ignored) {
            }
            if (blendAlpha < 0f) blendAlpha = 0f;
            if (blendAlpha > 2f) blendAlpha = 2f;
            if (blendAlpha != 1.0f) {
                int[] scaled = new int[colors.length];
                for (int i = 0; i < colors.length; i += 2) {
                    int c = colors[i];
                    int a = (c >>> 24) & 0xFF;
                    int na = Math.round(a * blendAlpha);
                    if (na > 255) na = 255;
                    if (na < 0) na = 0;
                    scaled[i] = (na << 24) | (c & 0x00FFFFFF);
                    if (i + 1 < colors.length) scaled[i + 1] = colors[i + 1];
                }
                colors = scaled;
            }

            miBlur.getMethod("clearMiBackgroundBlendColorCompat", View.class).invoke(null, v);
            // ★ 直接调三参数版本：系数传 1.0（官方对静态元素就是这么做的）
            miBlur.getMethod("setMiBackgroundBlendColors", View.class, int[].class, Float.TYPE)
                    .invoke(null, v, colors, 1.0f);
            // 官方 applyElementViewBlend 内部还会设 viewBlurMode=1（玻璃必需）
            miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class)
                    .invoke(null, 1, v);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i + 1 < colors.length; i += 2) {
                sb.append(" #").append(String.format("%08X", colors[i]))
                        .append("/").append(colors[i + 1]);
            }
            L.log(4, TAG, tag + " blend src=" + src + " alpha=" + blendAlpha + sb);
        } catch (Throwable t) {
            L.log(5, TAG, tag + " blend failed: " + t);
        }

        // ---------- ④ 玻璃参数 + 材质类型（顺序与官方一致）----------
        // ★★★ v154：改用官方普通通知卡片的 string-array 参数 ★★★
        //
        //  【官方 NotificationRowGlassEffect 原文】
        //    读 Resources.getStringArray(0x7f0300ce)
        //      → notification_glass_params_normal
        //    内容：0.67 0.16 0.09 0.0 0.24 1.4 -0.02 0.30 0.6 1.0
        //          0.03 1.0 1.0 1.0 0.10 0.2 0.3 1.0 1.0 72.0
        //          3.8 80.0 800.0 1.2 1.0 -0.4 0.6 -0.8 1.4 0.7
        //          0.8 1.15 4.0 2.0 0×8
        //    与 GLASS_PARAMS_FALLBACK **逐项完全一致**。
        //
        //  【v153 的遗留问题】
        //    v152/v153 用的是 HEADS_UP_GLASS_PARAMS（悬浮通知配方）：
        //      p0=0.5 p1=1.0 p3=0.8 p14=1.5 p19=62
        //    这组参数比普通通知卡片「实」得多（p1 差 6 倍、p3 从 0 到 0.8），
        //    是偏白的另一个来源。
        //
        //  【本版做法】
        //    优先直接读官方 string-array，读不到才退回 GLASS_PARAMS_FALLBACK。
        //    这样 Toast 的玻璃参数与普通通知卡片 **100% 相同**。
        float[] garr = null;
        try {
            String[] sa = res.getStringArray(2130903246);   // 0x7f0300ce
            if (sa != null && sa.length > 0) {
                float[] tmp = new float[sa.length];
                int cnt = 0;
                for (String s : sa) {
                    if (s == null) continue;
                    try {
                        tmp[cnt++] = Float.parseFloat(s.trim());
                    } catch (Throwable ignored) {
                    }
                }
                if (cnt >= 20) {
                    garr = new float[cnt];
                    System.arraycopy(tmp, 0, garr, 0, cnt);
                }
            }
        } catch (Throwable ignored) {
        }
        if (garr == null || garr.length < 20) {
            garr = GLASS_PARAMS_FALLBACK.clone();
        }
        // 允许逐项覆盖：debug.hyperclassic.toast_pN
        for (int i = 0; i < garr.length; ++i) {
            String ps = getProp("debug.hyperclassic.toast_p" + i, "").trim();
            if (ps.isEmpty()) continue;
            try {
                garr[i] = Float.parseFloat(ps);
            } catch (Throwable ignored) {
            }
        }

        // ★★★ v157：p19 = 玻璃圆角半径，必须与背景圆角一致 ★★★
        //
        //  【主人反馈】「白边残留，玻璃圆角仍然大于背景一点」
        //    L44 toast first bg=self-drawn rad=16dp(60px)     ← 背景圆角 60px
        //    L44 toast first glass ... p19=72.0               ← 玻璃圆角 72px
        //    72 - 60 = 12px → 玻璃比背景大 12px
        //    → 玻璃外侧露出 12px 的背景（白色）→ 就是主人看到的白边！
        //
        //  【为什么之前一直有】
        //    Coding酱把圆角做成两个独立来源：
        //      背景 = toast_radius（默认 16dp = 60px）
        //      玻璃 = 官方数组里的 p19（72）
        //    两者从来没同步过。
        //
        //  【修法】
        //    让 p19 强制跟随 radPx，两者用**同一个值**，形状 100% 重合。
        //    想单独调玻璃圆角时用 debug.hyperclassic.toast_glass_radius（px）。
        int grOverride = parseIntSafe(
                getProp("debug.hyperclassic.toast_glass_radius", "-1").trim(), -1);
        if (grOverride > 0) {
            garr[19] = grOverride;
        } else if (garr.length > 19) {
            garr[19] = radPx;      // ★ 与背景圆角完全一致
        }
        L.log(4, TAG, tag + " radius sync: bg=" + Math.round(radPx)
                + "px glass p19=" + garr[19]
                + (grOverride > 0 ? " (override)" : ""));

        // ★★★ v157：透度总控 —— 直接改玻璃参数，不用背景 drawable ★★★
        //
        //  【主人反馈】「调参无效，我把 backing 拉到了 100 但没有观感变化」
        //
        //  【为什么 backing 无效】
        //    ToastLinearLayout 的绘制顺序是：
        //      ① View.drawBackground()   ← 我们设的 GradientDrawable
        //      ② MiGlass 材质层          ← 玻璃反射（不透明铺满）
        //      ③ 子 View（图标 + 文字）
        //    玻璃层把 ① 完全盖住了，
        //    所以 backing 是 0 还是 255，观感一样 → 主人实测确认。
        //
        //  【真正的透度旋钮是玻璃参数】
        //    p1  = 玻璃整体不透明度（官方 0.16，越大越实）
        //    p3  = 折射强度（官方 0.0）
        //    p19 = 圆角半径（已同步）
        //
        //  【预设】debug.hyperclassic.toast_solid
        //    off（默认）→ 官方值，最透
        //    1  → p1×1.5  轻微加实
        //    2  → p1×2.5  明显实感
        //    3  → p1×4    接近磨砂
        //    或直接写数字倍数，如 2.0
        String solid = getProp("debug.hyperclassic.toast_solid", "off").trim();
        if (!"off".equals(solid) && !solid.isEmpty()) {
            float mul;
            switch (solid) {
                case "1": mul = 1.5f; break;
                case "2": mul = 2.5f; break;
                case "3": mul = 4.0f; break;
                default:
                    mul = parseFloatSafe(solid, 1.0f);
                    break;
            }
            float baseP1 = garr[1];
            garr[1] = Math.min(1.0f, baseP1 * mul);
            if (garr.length > 3) {
                garr[3] = Math.min(1.0f, garr[3] + (mul - 1.0f) * 0.15f);
            }
            L.log(4, TAG, tag + " solid=" + solid + " mul=" + mul
                    + " p1 " + baseP1 + " -> " + garr[1] + " p3=" + garr[3]);
        }

        // ★★★ v159：模糊半径必须按元素尺寸比例算，不能用固定值 ★★★
        //
        //  【主人反馈】「设0是透明玻璃证明调参有效，
        //    但我设任何非0值都会出现厚磨砂效果，看起来没有差别」
        //
        //  【根因：尺寸比例】
        //    Toast 实测尺寸：702 x 206 px（高度只有 206）
        //    官方 modal_glass_blur_max_radius = 40dp = 120px
        //    Coding酱之前默认 80px
        //    → 80 已经接近元素高度的一半，
        //      整个 Toast 区域都在模糊范围内 → 全糊
        //    → 所以 1 和 80 看起来一样（都超过临界点）
        //
        //  【官方怎么算的】
        //    BlurUtilsImpl 用 blurRatio2Radius(ratio, maxRadius)：
        //      ratio 是 0~1 的动画进度，maxRadius 是配置上限
        //      radius = ratio × maxRadius
        //    → 半径是**相对于配置上限**的比例，不是绝对值
        //
        //  【本版做法】
        //    半径按 View 短边比例算，这样不同尺寸的元素观感一致：
        //      radius = min(w, h) × ratio
        //    Toast 高 206px，ratio=0.08 → 16px（只柔化边缘，中间清晰）
        //              ratio=0.15 → 31px
        //              ratio=0.30 → 62px（明显磨砂）
        //              ratio=0.50 → 103px（接近全糊）
        //
        //  【可调】
        //    debug.hyperclassic.toast_glass_blur_ratio   0.0~1.0（默认 0.10）
        //    debug.hyperclassic.toast_glass_blur         直接指定 px（覆盖 ratio）
        //      0 → 不调用（透明玻璃）
        //      -1 → 用 ratio 计算（默认）
        int blurPx = parseIntSafe(
                getProp("debug.hyperclassic.toast_glass_blur", "-1").trim(), -1);
        if (blurPx < 0) {
            float ratio = parseFloatSafe(
                    getProp("debug.hyperclassic.toast_glass_blur_ratio", "0.10").trim(), 0.10f);
            if (ratio < 0f) ratio = 0f;
            if (ratio > 1f) ratio = 1f;
            int shortSide = Math.min(w, h);
            blurPx = Math.round(shortSide * ratio);
            L.log(4, TAG, tag + " glassBlur ratio=" + ratio
                    + " shortSide=" + shortSide + " -> " + blurPx + "px");
        }
        // small / big 允许分开（默认相同）
        int blurSmall = parseIntSafe(
                getProp("debug.hyperclassic.toast_glass_blur_small", "-1").trim(), -1);
        int blurBig = parseIntSafe(
                getProp("debug.hyperclassic.toast_glass_blur_big", "-1").trim(), -1);
        if (blurSmall < 0) blurSmall = blurPx;
        if (blurBig < 0) blurBig = blurPx;

        // ★★★ v161：把「折射偏移」和「模糊强度」彻底分开 ★★★
        //
        //  【主人实测反馈（决定性）】
        //    「这个值控制的不是模糊度而是反射强度。
        //      当这个值拉高时，背景会出现同一个画面被多重反射的现象，
        //      值越低实际背景和反射层重合度越高。」
        //
        //  → setMiBackgroundBlurScaleRatioCompat 控制的是**折射采样偏移**，
        //    偏移越大，采样点离原位置越远，反射层和背景错位
        //    → 观感上就是「同一画面多重反射」的重影。
        //    它**不是**模糊度旋钮。Coding酱上一版判断错了。
        //
        //  【那模糊度在哪】
        //    setMiBackgroundBlurRadiusCompat(view, radius)
        //      → 这是真正的「高斯模糊半径」，决定糊不糊。
        //    但它受 setMiBackgroundBlurScaleRatioCompat 影响：
        //      smali 里 blurRatio2Radius(ratio, maxRadius) = lerp(0, maxRadius, ratio)
        //      → ratio 会**缩放** radius。
        //      所以之前 ratio=0.3 时，radius 被缩到 30%，
        //      调 toast_bgr 观感变化很小（被 ratio 压住了）。
        //
        //  【本版做法】
        //    ① 折射偏移独立控制：toast_refract（默认 0.1，小一点避免重影）
        //    ② 模糊半径独立控制：toast_bgr（默认 40，真正的糊度）
        //    两者互不覆盖。
        float refract = parseFloatSafe(
                getProp("debug.hyperclassic.toast_refract", "0.1").trim(), 0.1f);
        if (refract < 0f) refract = 0f;
        if (refract > 1f) refract = 1f;
        try {
            miBlur.getMethod("setMiBackgroundBlurScaleRatioCompat", Float.TYPE, View.class)
                    .invoke(null, refract, v);
            L.log(4, TAG, tag + " refract(scaleRatio)=" + refract);
        } catch (Throwable t) {
            L.log(5, TAG, tag + " refract failed: " + t);
        }

        if (blurPx > 0) {
            try {
                miGlass.getMethod("setMiGlassBlurRadius", View.class, Integer.TYPE, Integer.TYPE)
                        .invoke(null, v, blurSmall, blurBig);
                L.log(4, TAG, tag + " glassBlur small=" + blurSmall + " big=" + blurBig);
            } catch (Throwable t) {
                L.log(5, TAG, tag + " glassBlur failed: " + t);
            }
        }

        try {
            miGlass.getMethod("setMiGlassCompat", View.class, float[].class).invoke(null, v, garr);
            miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class)
                    .invoke(null, 1, v);
            L.log(4, TAG, tag + " glass official len=" + garr.length
                    + " p0=" + garr[0] + " p1=" + garr[1]
                    + " p3=" + garr[3] + " p14=" + garr[14] + " p19=" + garr[19]);
        } catch (Throwable t) {
            L.log(5, TAG, tag + " glass failed: " + t);
        }

        // ---------- ⑤ 采样开关 ----------
        //
        //  【v152 决定性结论】
        //    v150  p1=1.0 p3=0.8 bgMode=1 bgR=80 backing=26 → 玻璃有效（偏白）
        //    v152  p1=1.0 p3=0.8 bgMode=0 bgR=0  backing=26 → 全透明（玻璃没了）
        //    两个版本**只差 bgMode/bgR**，结果玻璃从有到无。
        //
        //    → **bgMode=1 是玻璃渲染管线的必需开关**，不能关。
        //      Toast 是裸 View（没有官方的材质上下文），
        //      只能靠 bgMode=1 把 MiGlass 的绘制分支拉起来；
        //      托盘/通知卡片能 bgMode=0 工作，是因为它们有官方给的完整上下文。
        //
        //  【现在的目标：在 bgMode=1 下减少白色】
        //    bgMode=1 会开一层「背景模糊」。Toast 窗口没有 SF 快照，
        //    这层很可能退化成一层半透明白（模糊的 fallback color）。
        //
        //  【v153 实验：bgMode=1 + bgR=0】
        //    保留管线开关（bgMode=1），但把模糊半径设为 0。
        //    如果半径 0 时不绘制模糊层 → 白色消失、玻璃保留；
        //    如果半径 0 时管线也不画 → 玻璃又没了。
        //    这一步能精确区分「管线开关」和「模糊层绘制」。
        //
        //  【可调】
        //    debug.hyperclassic.toast_pass     0/1   窗口模糊穿透（默认 1，玻璃必需）
        //    debug.hyperclassic.toast_bgmode   0/1   背景模糊模式（默认 1，★ 玻璃必需）
        //    debug.hyperclassic.toast_bgr      整数  背景模糊半径（★ 真正的糊度，默认 40）
        int pPass = parseIntSafe(getProp("debug.hyperclassic.toast_pass", "1").trim(), 1);
        int pBgMode = parseIntSafe(getProp("debug.hyperclassic.toast_bgmode", "1").trim(), 1);
        int pBgR = parseIntSafe(getProp("debug.hyperclassic.toast_bgr", "40").trim(), 40);
        int pOutline = parseIntSafe(getProp("debug.hyperclassic.toast_outline", "1").trim(), 1);
        int pBacking = parseIntSafe(getProp("debug.hyperclassic.toast_backing", "0").trim(), 0);

        if (pPass != 0) {
            try {
                miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE)
                        .invoke(null, v, Boolean.TRUE);
            } catch (Throwable t) {
                L.log(5, TAG, tag + " setPass failed: " + t);
            }
        }
        try {
            miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class)
                    .invoke(null, pBgMode, v);
        } catch (Throwable ignored) {
        }
        try {
            miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class)
                    .invoke(null, pBgR, v);
        } catch (Throwable ignored) {
        }

        // ★ v146：打开玻璃自己的 outline 裁剪（修「下部圆角被切直」）
        if (pOutline != 0) {
            // ★★★ v163：白边的真正元凶 —— 两个 outline 开关同时打开 ★★★
            //
            //  【主人反馈】「白边只在白色背景下显形，位置超出圆角边缘」
            //  截图显示是**柔和光晕状**，在圆角外侧 —— 典型的模糊层溢出。
            //
            //  【反编译 NotificationBackgroundViewInjectorImpl 原文】
            //    官方按材质类型**互斥**设置这两个开关：
            //
            //    MaterialType.ordinal() == 1（玻璃材质）
            //      setGlassOutlineEnable(view, true)
            //      setBlurOutlineEnable(view, false)     ← ★ 模糊 outline 关掉！
            //
            //    MaterialType.ordinal() == 2（混合材质）
            //      setBlurOutlineEnable(view, true)
            //      setGlassOutlineEnable(view, true)
            //
            //    MaterialType.ordinal() == 0（无材质）
            //      setBlurOutlineEnable(view, false)
            //      setGlassOutlineEnable(view, false)
            //
            //  【Coding酱之前错在哪】
            //    v146 里两个都设了 TRUE：
            //      setGlassOutlineEnable(v, true)   ← 玻璃按 outline 裁剪 ✓
            //      setBlurOutlineEnable(v, true)    ← 模糊层也参与，但它的裁剪
            //                                          跟玻璃不是同一套 SDF，
            //                                          于是在圆角外溢出 → 白边
            //
            //  【修法】
            //    玻璃材质模式下：玻璃 outline = ON，模糊 outline = OFF
            //    （与官方 ordinal()==1 完全一致）
            //
            //  【可调】debug.hyperclassic.toast_blur_outline
            //    0（默认）→ 关闭模糊 outline（官方玻璃模式）
            //    1        → 打开（混合材质模式，可能又出白边）
            try {
                miBlur.getMethod("setGlassOutlineEnable", View.class, Boolean.TYPE)
                        .invoke(null, v, Boolean.TRUE);
                L.log(4, TAG, tag + " glassOutline=on");
            } catch (Throwable t) {
                L.log(5, TAG, tag + " setGlassOutlineEnable failed: " + t);
            }
            int blurOutline = parseIntSafe(
                    getProp("debug.hyperclassic.toast_blur_outline", "0").trim(), 0);
            try {
                miBlur.getMethod("setBlurOutlineEnable", View.class, Boolean.TYPE)
                        .invoke(null, v, blurOutline != 0);
                L.log(4, TAG, tag + " blurOutline=" + (blurOutline != 0 ? "on" : "off"));
            } catch (Throwable ignored) {
            }
            // ★★ v146 关键：玻璃 SDF 的「最大尺寸」必须和 View 尺寸一致 ★★
            //
            //  【主人反馈】「下部圆角裁剪不正确」
            //
            //  【为什么】
            //    玻璃是用 SDF（有向距离场）画的圆角矩形，
            //    而 SDF 会按 setMiGlassSdfMaxSizeCompat 声明的尺寸预分配。
            //    如果声明值比实际 View 小，SDF 就会在下方「提前结束」，
            //    导致下半部分被裁成直边 —— 正好是主人看到的现象。
            //
            //  【修法】
            //    显式把 SDF 尺寸设成当前 View 的宽高。
            //    尺寸在动画中会变，所以延迟到动画结束那次再设一遍。
            try {
                miGlass.getMethod("setMiGlassSdfMaxSizeCompat", View.class, Float.TYPE, Float.TYPE)
                        .invoke(null, v, (float) w, (float) h);
                L.log(4, TAG, tag + " sdfMax=" + w + "x" + h);
            } catch (Throwable t) {
                L.log(5, TAG, tag + " setMiGlassSdfMaxSize failed: " + t);
            }
        }

        // ★ v148：backing 已在 ② 步自绘背景时处理（同一个 radPx 和 alpha），
        //   这里不再重复 tint —— 重复会覆盖掉 ② 设好的 GradientDrawable。
        //   保留开关语义：toast_backing=0 时 ② 会画成全透明。

        L.log(4, TAG, tag + " sampling pass=" + pPass + " bgMode=" + pBgMode
                + " bgR=" + pBgR + " outline=" + pOutline + " backing=" + pBacking);

        v.invalidate();

        int n = TOAST_COUNT.incrementAndGet();
        if (n <= 6) {
            L.log(4, TAG, tag + " applied #" + n + " " + w + "x" + h
                    + " rad=" + Math.round(radPx) + " cls=" + v.getClass().getSimpleName());
        }

        // ---------- ⑥ 多时间点探针：定位「什么时候变透明」----------
        //
        //  【v143 探针的教训】
        //    上一版读的是 MiBlurCompat.getMiBackgroundBlurModeCompat 等方法，
        //    但反编译确认 **MiBlurCompat 里根本没有这些 getter**：
        //      只有 getBackgroundBlurOpened(Context) 和 getPassWindowBlurEnabledCompat(View)
        //    所以 bgMode/bgR/viewMode/glassR 全是空 = 反射失败被吞。
        //    pass=false 也可能是 getPassWindowBlurEnabledCompat 内部
        //    反射失败时 return false 的默认值，未必是真的关了。
        //
        //  【本版改法】
        //    直接反射 View 本体上的方法（MiGlassCompat 内部就是这么干的），
        //    并把失败原因打出来，而不是静默吞掉。
        final View fv = v;
        final ClassLoader fcl = cl;
        final String ftag = tag;
        for (final long dly : new long[]{0L, 150L, 400L}) {
            fv.postDelayed(() -> {
                try {
                    StringBuilder sb = new StringBuilder();
                    sb.append(" t=").append(dly);
                    sb.append(" a=").append(String.format("%.2f", fv.getAlpha()));
                    sb.append(" vis=").append(fv.getVisibility());
                    sb.append(" w=").append(fv.getWidth()).append("x").append(fv.getHeight());

                    // ★ 直接反射 View 本体的方法，看它们到底存不存在
                    String[][] probes = new String[][]{
                            {"getMiViewMaterialType", ""},
                            {"getMiGlassBlurRadius", ""},
                            {"getMiBackgroundBlurMode", ""},
                            {"getMiBackgroundBlurRadius", ""},
                            {"getPassWindowBlurEnabled", ""},
                            {"getMiViewBlurMode", ""},
                    };
                    for (String[] pr : probes) {
                        String name = pr[0];
                        try {
                            java.lang.reflect.Method m = null;
                            for (Class<?> cc = fv.getClass(); cc != null && m == null; cc = cc.getSuperclass()) {
                                try {
                                    m = cc.getDeclaredMethod(name);
                                } catch (Throwable ignored) {
                                }
                            }
                            if (m == null) {
                                sb.append(" ").append(name).append("=<NO_METHOD>");
                            } else {
                                m.setAccessible(true);
                                sb.append(" ").append(name).append("=").append(m.invoke(fv));
                            }
                        } catch (Throwable te) {
                            sb.append(" ").append(name).append("=<ERR:").append(te.getClass().getSimpleName()).append(">");
                        }
                    }
                    sb.append(" bg=").append(fv.getBackground() == null ? "null"
                            : fv.getBackground().getClass().getSimpleName());
                    L.log(4, TAG, ftag + " PROBE2" + sb);
                } catch (Throwable ignored) {
                }
            }, dly);
        }
    } catch (Throwable t) {
        L.log(5, TAG, tag + " failed: " + t);
    }
}

    private static void applyToastWindowGlass(XposedModule module, View v, ClassLoader cl, String tag) {
        try {
            int w = v.getWidth() > 0 ? v.getWidth() : v.getMeasuredWidth();
            int h = v.getHeight() > 0 ? v.getHeight() : v.getMeasuredHeight();
            if (w <= 0 || h <= 0) {
                return;
            }
            int alpha = parseIntSafe(getProp("debug.hyperclassic.toast_alpha", "96").trim(), 96);
            if (alpha < 0) alpha = 0;
            if (alpha > 255) alpha = 255;

            // ① 背景：用官方玻璃 Effect 那个透明圆角 drawable
            //  ★★★ v9.98：这是最后一环 ★★★
            //
            //  【官方实证】所有 GlassEffect 的第一步都是同一个 drawable：
            //    NotificationRowBlurEffect / HeadsUpNotificationBlurEffect /
            //    GroupCollapseButtonGlassEffect / MediaViewBlurEffect 都调
            //      Context.getDrawable(0x7f08184c)  →  setBackground / setCustomBackground
            //
            //  【这个 drawable 是什么】res/values/public.xml:
            //    0x7f08184c = notification_heads_up_transparent_bg
            //    res/drawable/notification_heads_up_transparent_bg.xml:
            //      <shape android:shape="rectangle">
            //        <corners android:radius="@dimen/notification_item_bg_radius" />
            //        <solid android:color="@color/transparent" />   ← 纯透明！
            //      </shape>
            //
            //  【为什么之前一直是磨砂】
            //    Toast 用的是官方 toast_frame（LayerDrawable），
            //    我们又把它 tint 成 alpha=96/255 的半透明白。
            //    这层半透明白**盖在玻璃上面** → 玻璃被糊成磨砂。
            //    玻璃的观感必须来自 MiGlass 自己，背景必须是纯透明的。
            //
            //  【修法】换成官方那个纯透明圆角 drawable。
            //    圆角用 notification_item_bg_radius（官方值），
            //    不再用 toast_bg_radius，因为玻璃 SDF 会按这个 outline 裁剪。
            Drawable bgd = null;
            try {
                // ★★★ v10.4：换回官方 toast_frame_new，不要 notification_heads_up_transparent_bg ★★★
                //
                //  【主人反馈】「toast 的底衬有奇怪的外边角」
                //
                //  【根因】两个 drawable 的结构完全不同：
                //
                //    我们之前用的 notification_heads_up_transparent_bg:
                //      <shape>
                //        <corners android:radius="24dp"/>
                //        <solid android:color="transparent"/>
                //      </shape>
                //      ↑ 没有 <padding>，没有 <size>
                //
                //    官方 toast_frame_new（0x7f081ccd）:
                //      <shape>
                //        <size android:height="toast_min_height"
                //              android:width="toast_min_width"/>
                //        <padding android:left="20dp" android:top="15.5dp"
                //                 android:right="20dp" android:bottom="15.5dp"/>
                //        <corners android:radius="16dp"/>
                //        <solid android:color="#fafafa"/>
                //      </shape>
                //
                //  【为什么 padding 会丢】View 的 padding 是从 background
                //    drawable 的 <padding> 里取的（Drawable.getPadding）。
                //    换成没有 padding 的 drawable 后 padding=0，
                //    文字直接顶到边缘，再被圆角切掉 → 文字溢出到圆角外。
                //
                //  【修法】用官方 toast_frame_new，它自带：
                //    · 正确 padding（20dp / 15.5dp）
                //    · 正确圆角（toast_bg_radius = 16dp）
                //    · 正确最小尺寸（101dp x toast_min_height）
                //    只把它的 solid 从 #fafafa 改成 alpha 可调的白色。
                //
                //  【为什么不再怕「叠两层」】v9.89b 时期是 ensureToastBlurBg
                //    和 applyToastWindowGlass 两处都在设背景才冲突；
                //    现在只有这一处设背景。
                bgd = v.getResources().getDrawable(2131238093, null);   // 0x7f081ccd toast_frame_new
            } catch (Throwable tbg) {
                L.log(5, TAG, tag + " getDrawable toast_frame_new failed: " + tbg);
                // 退回 SystemUI 的 Resources 再试一次
                try {
                    Resources sr = getSysuiResources(module, v.getContext());
                    if (sr != null) {
                        bgd = sr.getDrawable(2131238093, null);
                    }
                } catch (Throwable tbg2) {
                    L.log(5, TAG, tag + " getDrawable sysui failed: " + tbg2);
                }
                // 还不行就退回透明圆角 shape（至少圆角对，但会丢 padding）
                if (bgd == null) {
                    try {
                        bgd = v.getContext().getDrawable(2131236940);
                        L.log(5, TAG, tag + " fallback to transparent_bg (padding lost)");
                    } catch (Throwable tbg3) {
                        L.log(5, TAG, tag + " all drawables failed: " + tbg3);
                    }
                }
            }
            if (bgd != null) {
                // ★★★ v10.5：纯色测试模式 ★★★
                //
                //  【主人指示】「先做纯色测试，确认数值真的能套用到 toast 视图上再调参」
                //
                //  【为什么需要】toast_bg_blur 0→100 都看不出变化，
                //    说明我们设的某个环节根本没画出来。纯色测试能一刀切开：
                //
                //    setprop debug.hyperclassic.toast_test_solid 1     → 大红不透明
                //    setprop debug.hyperclassic.toast_test_solid 2     → 大绿不透明
                //    setprop debug.hyperclassic.toast_test_solid 3     → 大蓝不透明
                //    setprop debug.hyperclassic.toast_test_solid 0     → 关闭测试
                //
                //  【判读】
                //    · Toast 变成纯红/绿/蓝
                //        → 我们的 background 是画出来的，
                //          说明玻璃材质是叠在它上面的，
                //          问题在玻璃参数，继续调参有意义
                //    · Toast 颜色不变
                //        → materialType=1 时框架接管渲染，
                //          我们设的 background 根本不画，
                //          调 background 类参数全是死路，只能调玻璃参数
                //
                //  【实现】直接 new GradientDrawable 画纯色，
                //    保留 16dp 圆角（toast_bg_radius），不依赖官方资源。
                int testSolid = parseIntSafe(getProp("debug.hyperclassic.toast_test_solid", "0").trim(), 0);
                // ★ v10.5b：支持任意 ARGB（8 位十六进制），验证半透明是否可见
                //   用法：setprop debug.hyperclassic.toast_test_color 60FFFFFF
                String testColorHex = getProp("debug.hyperclassic.toast_test_color", "").trim();
                if (testSolid > 0 || testColorHex.length() >= 6) {
                    int c;
                    if (testColorHex.length() >= 6) {
                        try {
                            long v2 = Long.parseLong(testColorHex, 16);
                            c = (int)(testColorHex.length() == 6 ? (0xFF000000L | v2) : v2);
                        } catch (Throwable tt) {
                            c = 0xFFFF0000;
                        }
                        L.log(4, TAG, tag + " TEST color argb=0x" + Integer.toHexString(c));
                    } else {
                        int[] colors = {0xFFFF0000, 0xFF00FF00, 0xFF0000FF};
                        c = colors[(testSolid - 1) % colors.length];
                        L.log(4, TAG, tag + " TEST solid bg=0x" + Integer.toHexString(c));
                    }
                    GradientDrawable gd = new GradientDrawable();
                    gd.setColor(c);
                    try {
                        int rid = v.getResources().getIdentifier("toast_bg_radius", "dimen", "com.android.systemui");
                        gd.setCornerRadius(rid > 0 ? v.getResources().getDimension(rid) : 16.0f * v.getResources().getDisplayMetrics().density);
                    } catch (Throwable ignored) {
                        gd.setCornerRadius(16.0f * v.getResources().getDisplayMetrics().density);
                    }
                    v.setBackground(gd);
                } else {
                try {
                    Drawable d2 = bgd.mutate();
                    // ★ v10.9b：pure 模式复刻 v9.91 的 0x1AFFFFFF（10% 白）
                    //   （pureMode 变量在后面才声明，这里直接读 prop）
                    boolean pureNow = "1".equals(getProp("debug.hyperclassic.toast_pure", "0").trim());
                    int effAlpha = pureNow ? 0x1A : alpha;
                    // ★ 注意：tintDrawableColor 的第 3 个参数是递归深度，不是 alpha。
                    //   alpha 必须编进 argb 本身（setColor 用的是完整 ARGB）。
                    int backingArgb = (effAlpha << 24) | 0xFFFFFF;
                    CcHooks.tintDrawableColor(d2, backingArgb, 0);
                    bgd = d2;
                } catch (Throwable ignored) {
                }
                v.setBackground(bgd.mutate());
                }
                // ★ v10.4：圆角统一 —— 只用官方 toast_bg_radius（16dp）
                //
                //  【主人反馈】「有奇怪的外边角」
                //  【根因】这里原来设了 24dp（notification_item_bg_radius），
                //    下面 ③ 又设了 16dp（toast_bg_radius）把它覆盖，
                //    背景 drawable 是 16dp、outline 先是 24dp 再变 16dp，
                //    玻璃 SDF 又按 16dp 裁 → 三层圆角错位。
                //  【修法】这里就统一用 toast_bg_radius，和 drawable 一致。
                try {
                    int rid = v.getResources().getIdentifier("toast_bg_radius", "dimen", "com.android.systemui");
                    float nr = rid > 0 ? v.getResources().getDimension(rid) : 16.0f * v.getResources().getDisplayMetrics().density;
                    final float fnr = nr;
                    v.setOutlineProvider(new ViewOutlineProvider() {
                        public void getOutline(View vv, Outline o) {
                            o.setRoundRect(0, 0, vv.getWidth(), vv.getHeight(), fnr);
                        }
                    });
                    v.invalidateOutline();
                } catch (Throwable ignored) {
                }
                L.log(4, TAG, tag + " bg=official toast_frame_new");
            } else {
                // 退化：只把官方背景调透明
                Drawable cur0 = v.getBackground();
                if (cur0 != null) {
                    Drawable d0 = cur0.mutate();
                    CcHooks.tintDrawableColor(d0, 0x00FFFFFF, 0);
                    v.setBackground(d0);
                }
            }

            // ② 按官方 applyModalContainerBlur 配方挂玻璃
            //  【官方实证】BlurUtilsImpl.applyModalContainerBlur:
            //    if (view.isAttachedToWindow()) {
            //      if (!getPassWindowBlurEnabledCompat(v)) {
            //        setPassWindowBlurEnabledCompat(v, true);
            //        setMiBackgroundBlurModeCompat(1, v);
            //      }
            //      setMiBlurWinExc(v, "NotificationShade");   ← 官方就用这个域名
            //    }
            //    setMiBackgroundBlurRadiusCompat(radius, v);
            //    glass ? modalMaxGlassBlurRadius : miBlurMaxRadius
            //
            //  【Toast 的难点】
            //    Toast 在独立窗口，必须先 attached 才能挂模糊域。
            //    没 attach 就延后重试，否则 setMiBlurWinExc 静默失败。
            boolean attached = v.isAttachedToWindow();
            // ★★★ v10.7：延迟重试拉长 —— 等 SurfaceFlinger 快照就绪 ★★★
            //
            //  【决定性证据（23:52:05 实时捕获）】
            //    PassBlur-VRI[Toast]: addTextureView ... texScale 0.25    ← 注册成功
            //    backgroundBlur: setBackgroundBlurRadius 30               ← 参数写入成功
            //    backgroundBlur: setMiViewBlurMode ... mode = 1           ← 模式写入成功
            //    E backgroundBlur: mergeSnapshot if(!mSnapshot &&
            //        mSurfaceTex). will return                            ← ★ 合成失败
            //
            //  【结论】View 状态全对，但 PassBlur 合成器需要
            //    「窗口背后内容的快照」（mSnapshot / mSurfaceTex）。
            //    Toast 弹出瞬间快照还没建立，mergeSnapshot 一直提前返回。
            //    通知为什么成功？Shade 窗口常驻，快照早就绪。
            //
            //  【修法】把 blur 挂载时机延后（默认 300ms），给 SF 时间
            //    建立快照。可用 debug.hyperclassic.toast_delay 调（ms）。
            //    注意：重试时 TOAST_DONE 已包含 v，需要先移除。
            if (!attached) {
                if (TOAST_RETRY.add(v)) {
                    v.postDelayed(() -> {
                        TOAST_RETRY.remove(v);
                        try {
                            CcHooks.applyToastWindowGlass(module, v, cl, tag);
                        } catch (Throwable ignored) {
                        }
                    }, 120L);
                }
                L.log(4, TAG, tag + " deferred (not attached) " + w + "x" + h);
                return;
            }
            int blurR = CcHooks.currentBlurRadius();
            if (blurR <= 0) {
                blurR = 80;
            }
            float[] garrLog = new float[0];   // v10.4：给末尾日志用（garr 在内层 try 里）
            try {
                Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, cl);
                Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, cl);
                // 窗口模糊 + 背景模糊
                // ★★★ v9.95：Toast 玻璃需要 bgMode=1 作为采样源，但半径要小 ★★★
                //
                //  【三轮实测数据】
                //    v9.92  bgMode=1 r=80 + 重blend → 暗色磨砂 + 微弱高光描边（玻璃在，被压住）
                //    v9.93  bgMode=1 r=80 + 淡blend → 仍是磨砂（blend 不是主因）
                //    v9.94  bgMode=0 r=0  + 淡blend → 官方高斯模糊（玻璃完全不渲染！）
                //
                //  【结论】Toast 与通知/托盘不同：
                //    通知/托盘在 Shade 窗口内，Shade 自身窗口模糊提供采样源，
                //    所以它们 bgMode=0 也能出玻璃。
                //    Toast 在独立窗口，没有那个采样源 →
                //    bgMode=1 是玻璃唯一的纹理来源，关掉就什么都不渲染。
                //
                //  【所以】保留 bgMode=1，但把半径从 80 降到 ~20：
                //    小半径 = 轻模糊 + 玻璃折射显形
                //    大半径 = 糊成一片 → 磨砂观感
                //  可用 debug.hyperclassic.toast_bg_blur 调（默认 20）。
                //
                //  ★ v9.97 补充：v9.96 补齐 SDF 尺寸后，玻璃已经能采样到壁纸色彩
                //    （主人截图：Toast 透出粉橙色）。既然玻璃管线通了，
                //    这层 bg 磨砂就不再是必需采样源，可以降到 0 让折射更清晰。
                //    默认改为 0；若发现玻璃完全不渲染，再调回 20。
                int toastBgBlur = parseIntSafe(getProp("debug.hyperclassic.toast_bg_blur", "0").trim(), 0);
                if (toastBgBlur < 0) toastBgBlur = 0;
                if (toastBgBlur > 100) toastBgBlur = 100;
                // ★★★ v10.8：view 级 bgBlur 默认不再挂 ★★★
                //
                //  【tc4 实时捕获的决定性对照】
                //    Launcher PassBlur: include 2 → SF 建快照 → 模糊渲染 ✅
                //    Toast   PassBlur: include 0 → SF 无快照 → mergeSnapshot return ❌
                //  【结论】TYPE_TOAST 窗口的 view 级 PassBlur 拿不到采样源，
                //    而它一旦挂上（bgMode=1）就**抢占**了模糊处理权，
                //    连窗口级 blurBehind（FLAG_BLUR_BEHIND, radius=80）都不画了
                //    → 透明玻璃。
                //  【历史证据】v9.91 只有窗口 blurBehind、不碰 view 级 → 模糊成功。
                //  【修法】debug.hyperclassic.toast_bgmode:
                //    0（默认）= 不挂 view 级 bgBlur，让窗口 blurBehind 工作
                //    1 = 挂 view 级（旧行为，供 A/B 对照）
                int toastBgMode = parseIntSafe(getProp("debug.hyperclassic.toast_bgmode", "0").trim(), 0);
                // ★ v10.9：v9.91 复刻开关（pureMode 声明提前，供 4287 行使用）
                boolean pureMode = "1".equals(getProp("debug.hyperclassic.toast_pure", "0").trim());
                // ★★★ v10.10：磨砂玻璃复刻模式（v9.88 成功版精确移植）★★★
                //
                //  【主人指示】「回退 toast 改动到使用磨砂玻璃的版本，暂时接受这个样式」
                //
                //  【v9.88 成功配方（备份 CcHooks_9.88.0_1123.java 反查实证）】
                //    applyUnifiedGlass 核心三件套：
                //      setMiGlassCompat(42项) + setMiViewMaterialTypeCompat(1)
                //      setMiGlassBlurRadius(gr, gr)           ← 官方"禁手"，磨砂的关键！
                //      setMiBackgroundBlurRadiusCompat(80) + setMiBackgroundBlurModeCompat(1)
                //    hook 侧：
                //      ensureToastBlurBg  → toast_frame tint 0x1AFFFFFF
                //      setupToastOutline  → 16dp roundRect + clipToOutline=true
                //      tryEnableToastWindowBlur → flags|=4 blurBehindRadius=80
                //      最后 setBgBlurMode(v, 0) 收尾
                boolean mattMode = "1".equals(getProp("debug.hyperclassic.toast_matt", "1").trim());
                if (pureMode) {
                    // ★ v10.9b：pure 模式 = 【什么都不调】★
                    //
                    //  【主人反馈】v10.9 仍「实心→透明」，透明化时刻 = apply 时刻
                    //  【根因】我加的 setMiBackgroundBlurModeCompat(0)「确保清掉」
                    //    本身就是破坏者：framework 收到 mode=0 会移除
                    //    BackgroundBlurDrawable / 干扰窗口 blurBehind 管线。
                    //    v9.91 成功版从头到尾没碰过 view 级 blur API。
                    //  【修法】pure 模式完全不调任何 MiBlurCompat setter。
                    L.log(4, TAG, tag + " PURE mode: touch nothing (v9.91 behavior)");
                } else if (mattMode) {
                    // ★★★ v10.10：matt 磨砂模式 = v9.88 精确复刻（默认）★★★
                    //
                    //  【流程】（照抄 v9.88 applyUnifiedGlass 的 Toast 路径）
                    try {
                        int gr = blurR > 0 ? blurR : 80;
                        float[] mgarr = (MEDIA_GLASS_PARAMS_WANT != null && MEDIA_GLASS_PARAMS_WANT.length >= 20)
                                ? MEDIA_GLASS_PARAMS_WANT.clone() : GLASS_PARAMS_FALLBACK.clone();
                        Class<?> mg = Class.forName(CLS_MI_GLASS, false, cl);
                        // ① 玻璃材质（与 v9.88 相同）
                        mg.getMethod("setMiGlassCompat", View.class, float[].class).invoke(null, v, mgarr);
                        mg.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
                        // ② 磨砂三件套：setMiGlassBlurRadius + bgRadius + bgMode
                        //   【v9.88 实证】这就是"官方禁手"，正是磨砂观感的来源
                        mg.getMethod("setMiGlassBlurRadius", View.class, Integer.TYPE, Integer.TYPE)
                                .invoke(null, v, gr, gr);
                        miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class).invoke(null, gr, v);
                        miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
                        // ③ outline + clipToOutline（v9.88 setupToastOutline）
                        //   【注意】v9.88 用的是 clipToOutline=true，这是磨砂清晰圆角的关键
                        float d60 = v.getResources().getDisplayMetrics().density;
                        float rr = 16.0f * d60;
                        if (rr > (float) h / 2.0f) rr = (float) h / 2.0f;
                        final float frr = rr;
                        v.setOutlineProvider(new ViewOutlineProvider() {
                            public void getOutline(View vv, Outline o) {
                                o.setRoundRect(0, 0, vv.getWidth(), vv.getHeight(), frr);
                            }
                        });
                        v.setClipToOutline(true);
                        v.invalidateOutline();
                        // ④ 收尾：setBgBlurMode(v, 0)（v9.88 原样）
                        //   ★ v10.10.1：当前 framework 已删除此方法（NoSuchMethodException 实锤），
                        //     独立 try 防止它中断后面的步骤
                        try {
                            miBlur.getMethod("setBgBlurMode", View.class, Integer.TYPE).invoke(null, v, 0);
                        } catch (Throwable ignored) {
                        }
                        // ⑤ 背景颜色（v9.88 ensureToastBlurBg 原样）
                        //   ★ v10.10.2：跟随暗色模式
                        //   【根因】系统暗色模式（ui_night_mode=3）下，
                        //     toast_frame_new 解析到暗色变体（solid=#1c1c1f 近黑），
                        //     10% 白 tint 叠上去 = 深灰。
                        //   【修法】暗色 → 低透明白（提亮 + 保持通透感）
                        //          浅色 → v9.88 原版 0x1AFFFFFF（10% 白）
                        try {
                            int night = v.getContext().getResources().getConfiguration().uiMode
                                    & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
                            int tint = (night == android.content.res.Configuration.UI_MODE_NIGHT_YES)
                                    ? 0x14FFFFFF      // 暗色：8% 白，轻提亮
                                    : 0x1AFFFFFF;     // 浅色：v9.88 原版 10% 白
                            Drawable df = v.getResources().getDrawable(2131238093, null);
                            if (df != null) {
                                Drawable dm = df.mutate();
                                CcHooks.tintDrawableColor(dm, tint, 0);
                                v.setBackground(dm);
                            }
                        } catch (Throwable ignored) {
                        }
                        L.log(4, TAG, tag + " MATT mode (v9.88 recipe) gr=" + gr);
                    } catch (Throwable tm) {
                        L.log(5, TAG, tag + " MATT failed: " + tm);
                    }
                } else {
                miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE).invoke(null, v, true);
                if (toastBgMode != 0) {
                    miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
                    miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class).invoke(null, toastBgBlur, v);
                } else {
                    // 确保清掉（v10.7 之前可能已经设过 1）
                    miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class).invoke(null, 0, v);
                    miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class).invoke(null, 0, v);
                }
                } // end else (!pureMode) — pure 模式不设 passWindowBlur/winExc
                // ★ 模糊域：官方 applyModalContainerBlur 固定用 "NotificationShade"
                //   （pure 模式也不设 WinExc，v9.91 没有）
                // ③ 玻璃材质 + 42 项参数
                // ★★★ v10.9：v9.91 复刻开关（纯 blurBehind 对照）★★★
                //
                //  【主人反馈】v10.8「实心 toast 弹出后变成透明」
                //    → blurBehind 也没渲染，view 级玻璃管线疑似
                //      把窗口切进了 PassBlur 模式（include=0 无采样）。
                //  【v9.91 成功配方（备份 CcHooks_9.88/9.91 实证）】
                //    · 窗口 blurBehind（flags+radius=80）
                //    · 背景 tint 0x1AFFFFFF（10% 白）
                //    · 【没有】materialType/viewMode/SDF/42项/blend
                //  【开关】toast_pure=1 时跳过整套 view 级玻璃链，
                //    只留窗口 blurBehind + tint 背景 = 精确复刻 v9.91。
                //    若 pure 模式模糊恢复 → 逐项加回玻璃定位抢占者。
                if (!pureMode) {
                float[] garr = MEDIA_GLASS_PARAMS_WANT;
                if (garr == null || garr.length < 20) {
                    garr = GLASS_PARAMS_FALLBACK.clone();
                } else {
                    garr = garr.clone();
                }
                // ★★★ v10.4：42 项参数可逐个覆盖，用于调「反射比」★★★
                //
                //  【为什么需要】
                //    主人反馈「玻璃效果仍然没变，过于透明」。
                //    我原以为调 background drawable 的 alpha 能加底衬，
                //    但 MiGlassCompat 只有 4 个方法（setMiGlassCompat /
                //    setMiGlassSdfMaxSize / setMiViewMaterialType /
                //    setMiGlassBlurRadius），**没有颜色控制**。
                //    也就是说 materialType=1 时，框架接管了 View 的渲染，
                //    我们设的 background drawable 只贡献 padding 和 outline，
                //    填充是由玻璃材质层画的 → 改 alpha 完全无效。
                //
                //  【所以唯一能调的是 42 项参数本身】
                //    用法：setprop debug.hyperclassic.toast_p0 0.95
                //          setprop debug.hyperclassic.toast_p19 1.0
                //    支持 p0 ~ p41，未设置的保持官方值。
                //
                //  【已知的官方值差异（可作起点）】
                //    index  0: 通知 0.67 / 悬浮 0.5 / 焦点 0.6
                //    index  1: 通知 0.16 / 悬浮 1.0 / 焦点 0.22
                //    index  5: 通知 1.4  / 悬浮 1.2 / 焦点 1.8
                //    index 20: 通知 72   / 悬浮 62  / 焦点 72
                for (int pi = 0; pi < garr.length && pi < 42; pi++) {
                    String pv = getProp("debug.hyperclassic.toast_p" + pi, "").trim();
                    if (pv.length() > 0) {
                        try {
                            garr[pi] = Float.parseFloat(pv);
                        } catch (Throwable ignored) {
                        }
                    }
                }
                // ★ v10.4：圆角已在 ① 统一设过（toast_bg_radius=16dp），这里不再重复设，
                //   避免 24dp/16dp 两层错位（主人反馈的「奇怪的外边角」）。
                miGlass.getMethod("setMiGlassCompat", View.class, float[].class).invoke(null, v, garr);
                garrLog = garr;   // v10.4：让末尾日志能看到实际下发的 p0/p19
                miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
                // ★★★ v9.96：显式补 SDF 尺寸 ★★★
                //  【官方实证】setMiViewMaterialTypeCompat(1, v) 内部：
                //    if (v.isLaidOut() && !v.isLayoutRequested())
                //        setMiGlassSdfMaxSizeCompat(v, w, h);     ← 立即设
                //    else
                //        v.addOnLayoutChangeListener(...)          ← 延后等布局
                //  我们在 onMeasure 里调用，此时 isLayoutRequested()==true，
                //  走延后分支；若回调没跑到，SDF 尺寸就是 0 → 玻璃渲染异常。
                //  显式补一次，保证 SDF 有正确的尺寸。
                try {
                    miGlass.getMethod("setMiGlassSdfMaxSizeCompat", View.class, Float.TYPE, Float.TYPE)
                            .invoke(null, v, (float) w, (float) h);
                } catch (Throwable ignored) {
                }
                // ★★★ v9.97：删掉 setMiGlassBlurRadius —— 它就是「模糊」的元凶 ★★★
                //
                //  【官方实证】所有官方 GlassEffect（NotificationRow / HeadsUp /
                //    GroupCollapseButton / MediaView）都只调 4 个 API：
                //      setMiViewBlurModeCompat(1)
                //      setMiBackgroundBlendColors(...)
                //      setMiViewMaterialTypeCompat(1)
                //      setMiGlassCompat(42项)
                //    ✗ 从不调用 setMiGlassBlurRadius
                //
                //  【我们代码里早就有这条注释】
                //    「✗ 不调 setMiGlassBlurRadius（style==3 明确跳过，否则折射层被模糊）」
                //    —— 但 Toast 这里还是调了 (80,80)，结果折射层被糊成磨砂。
                //
                //  【主人截图实证（v9.96）】
                //    Toast 能采样到壁纸的粉橙色 ✅ → 玻璃管线是通的
                //    但观感是模糊磨砂 ❌ → 折射层被 setMiGlassBlurRadius(80) 糊了
                //  删掉它，折射层就恢复清晰。
                //
                //  （如需保留一点柔化，可用 debug.hyperclassic.toast_glass_blur 设 0~80，默认 0）
                // ★ v9.99：blend 改回官方三色，与其他元素统一 ★★★
                //  【主人反馈（v9.98 截图）】
                //    「成功覆盖了玻璃。现在剩的问题就是让它的反射比和其他元素统一，
                //      现在的值太高导致底层完全透出」
                //  【原因】v9.93 把 blend 从官方三色改成单色 0x33FFFFFF（20% 白），
                //    着色太弱 → 玻璃没有实体感 → 底层文字完全透出。
                //    官方 MediaViewBlurEffect / HeadsUpNotificationBlurEffect 都用三色：
                //      c1=0x7f06057a m1=0x7f0c00ea (linear_light=100)
                //      c2=0x7f06057b m2=0x7f0c00e9 (lab=106)
                //      c3=0x7f06057c m3=0x7f0c00eb (pure=3)
                //    这套着色给玻璃提供"实体感"，是其他元素观感统一的关键。
                //  【开关】debug.hyperclassic.toast_blend=1 用官方三色（默认）
                //          设 0 则退回单色淡白（极度通透）
                try {
                    boolean useTri = !"0".equals(getProp("debug.hyperclassic.toast_blend", "1").trim());
                    miBlur.getMethod("clearMiBackgroundBlendColorCompat", View.class).invoke(null, v);
                    if (useTri) {
                        // ★★★ v10.1：用通知卡片的真实 blend 值 ★★★
                        //
                        //  【主人要求】「让它的反射比和其他元素统一」
                        //
                        //  【资源实证】res/values/colors.xml + integers.xml:
                        //    notification_element_blend_shade_color_1 = #ff818181  (灰, 完全不透明)
                        //    notification_element_blend_shade_color_2 = #ffb3ff00  (黄绿, 完全不透明)
                        //    notification_element_blend_shade_mode_1  = 107
                        //    notification_element_blend_shade_mode_2  = 106
                        //  【注意】两个颜色都是 ff 开头 —— 不透明！
                        //    它们不靠 alpha 压底层，而是靠 **mode 107/106 混合模式**。
                        //    这就是「反射比」的真正来源，也是和通知统一的唯一办法。
                        //
                        //  【对比 media 那套（不是通知用的）】
                        //    media: #ff999999/100 + #ffccff00/106 + #61ffffff/3
                        //    ↑ 偏鲜艳，是媒体卡片专用
                        //
                        //  【setMiBackgroundBlendColors 内部机制】
                        //    count = colors.length / 2
                        //    for i: addMiBackgroundBlendColorCompat(view,
                        //              colors[i*2], colors[i*2+1])
                        //    → 数组必须是 {颜色, 模式, 颜色, 模式, ...} 交替
                        Resources rr = v.getResources();
                        int c1 = rr.getColor(2131102662, null);   // 0x7f060bc6 #ff818181
                        int m1 = rr.getInteger(2131493148);      // 0x7f0c011c 107
                        int c2 = rr.getColor(2131102663, null);  // 0x7f060bc7 #ffb3ff00
                        int m2 = rr.getInteger(2131493149);      // 0x7f0c011d 106
                        miBlur.getMethod("setMiBackgroundBlendColors$default", View.class, int[].class)
                                .invoke(null, v, new int[]{c1, m1, c2, m2});
                        L.log(4, TAG, tag + " blend=notification #" + Integer.toHexString(c1) + "/" + m1
                                + " #" + Integer.toHexString(c2) + "/" + m2);
                    } else {
                        int blendColor = (int) Long.parseLong(
                                getProp("debug.hyperclassic.toast_blend_hex", "33FFFFFF").trim(), 16);
                        miBlur.getMethod("setMiBackgroundBlendColors$default", View.class, int[].class)
                                .invoke(null, v, new int[]{blendColor});
                        L.log(4, TAG, tag + " blend=single #" + Integer.toHexString(blendColor));
                    }
                } catch (Throwable tb) {
                    L.log(5, TAG, tag + " blend failed: " + tb);
                }
                // view 级模糊模式（玻璃必需）
                miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
                } // end if (!pureMode) — v10.9：pure 模式跳过整套 view 级玻璃
                // ★★★ v10.6：模糊通道读回诊断 ★★★
                //
                //  【主人反馈】「toast_bg_blur 设到 100 也没有明显变化」
                //    → 盲调无意义，必须确认系统是否真的收到了我们设的值。
                //  【读回项】
                //    bgMode    = setMiBackgroundBlurModeCompat 写的
                //    bgRadius  = setMiBackgroundBlurRadiusCompat 写的（toast_bg_blur）
                //    pass      = passWindowBlurEnabled
                //    viewMode  = setMiViewBlurModeCompat 写的
                //  【判读】
                //    若 bgRadius 读回 = toast_bg_blur 设的值 → 通道通，模糊是
                //      被 glass 材质盖住了（调玻璃参数）。
                //    若读回 ≠ 设的值（如永远 0）→ setMiBackgroundBlurRadiusCompat
                //      对 Toast 无效，可能被 framework 拒了（查 logcat MiBlurCompat）。
                try {
                    StringBuilder diag = new StringBuilder();
                    // ★ v10.6b：getter 是 View 的实例方法（framework 实证），
                    //   MiBlurCompat 没有封装 → 直接反射 View 方法
                    String[] names = {"getMiBackgroundBlurMode", "getMiBackgroundBlurRadius", "getMiViewBlurMode"};
                    String[] labels = {"bgMode", "bgRadius", "viewMode"};
                    for (int gi = 0; gi < names.length; gi++) {
                        try {
                            Method gm = View.class.getMethod(names[gi]);
                            gm.setAccessible(true);
                            diag.append(' ').append(labels[gi]).append('=').append(gm.invoke(v));
                        } catch (Throwable ig) {
                            diag.append(' ').append(labels[gi]).append("=?");
                        }
                    }
                    try {
                        diag.append(" pass=").append(miBlur.getMethod("getPassWindowBlurEnabledCompat", View.class).invoke(null, v));
                    } catch (Throwable ig) { diag.append(" pass=?"); }
                    L.log(4, TAG, tag + " VERIFY(want bgRadius=" + toastBgBlur + ")" + diag);
                } catch (Throwable ig0) {
                }
            } catch (Throwable t2) {
                L.log(5, TAG, tag + " glass chain failed: " + t2);
            }
            v.invalidate();
            L.log(4, TAG, tag + " applied " + w + "x" + h + " alpha=" + alpha + " blur=" + blurR
                    + " p0=" + (garrLog.length > 0 ? garrLog[0] : -1) + " p19=" + (garrLog.length > 19 ? garrLog[19] : -1)
                    + " bg=" + (v.getBackground() == null ? "null" : v.getBackground().getClass().getSimpleName()));
        } catch (Throwable t) {
            L.log(5, TAG, tag + " failed: " + t);
        }
    }

    private static void ensureToastBlurBg(XposedModule module, View v) {
        try {
            Object tag = v.getTag(2131238093);
            boolean hasBlurTag = tag instanceof Boolean && (Boolean)tag != false;
            Drawable cur = v.getBackground();
            if (hasBlurTag && cur != null) {
                return;
            }
            Drawable d = v.getResources().getDrawable(2131238093, null);
            if (d == null) {
                return;
            }
            boolean tinted = CcHooks.tintDrawableColor(d = d.mutate(), 0x1AFFFFFF, 0);
            if (!tinted) {
                L.log(5, TAG, "L44 toast bg tint failed, cls=" + d.getClass().getSimpleName());
            }
            v.setBackground(d);
            v.setTag(2131238093, (Object)Boolean.TRUE);
            L.log(4, TAG, "L44 toast bg applied: " + d.getClass().getSimpleName() + " tinted=" + tinted);
        }
        catch (Throwable t) {
            L.log(5, TAG, "L44 toast blur bg failed: " + t);
        }
    }

    private static void setupToastOutline(View v) {
        try {
            int h;
            float density = v.getResources().getDisplayMetrics().density;
            float r = 16.0f * density;
            int n = h = v.getHeight() > 0 ? v.getHeight() : v.getMeasuredHeight();
            if (h > 0 && r > (float)h / 2.0f) {
                r = (float)h / 2.0f;
            }
            final float radius = r;
            v.setOutlineProvider(new ViewOutlineProvider(){

                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
                }
            });
            v.setClipToOutline(true);
            v.invalidateOutline();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static boolean isHeadsUpRow(Object row, XposedModule module) {
        try {
            Method m = row.getClass().getMethod("isHeadsUp", new Class[0]);
            Object r = m.invoke(row, new Object[0]);
            if (r instanceof Boolean && ((Boolean)r).booleanValue()) {
                return true;
            }
        }
        catch (Throwable m) {
            // empty catch block
        }
        try {
            for (Class<?> c = row.getClass(); c != null; c = c.getSuperclass()) {
                try {
                    Field f = c.getDeclaredField("mIsHeadsUp");
                    f.setAccessible(true);
                    Object v = f.get(row);
                    if (v instanceof Boolean && ((Boolean)v).booleanValue()) {
                        return true;
                    }
                    break;
                }
                catch (NoSuchFieldException e) {
                    continue;
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return false;
    }

    private static float[] getOfficialCornerRadii(View bg) {
        try {
            Method m = bg.getClass().getMethod("getCornerRadii", new Class[0]);
            m.setAccessible(true);
            Object r = m.invoke(bg, new Object[0]);
            if (r instanceof float[] && ((float[])r).length >= 8) {
                return (float[])r;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    private static void applyOfficialCornerClip(View v, float[] radii) {
        try {
            if (radii == null || radii.length < 8) {
                return;
            }
            final float r = radii[0];
            v.setOutlineProvider(new ViewOutlineProvider(){

                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), r);
                }
            });
            v.setClipToOutline(true);
            v.invalidateOutline();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void installRadiiClipGuard(XposedModule module, View row, float[] radii) {
        try {
            if (radii == null || radii.length < 8) {
                return;
            }
            if (!RADII_CLIP_GUARDED.add(row)) {
                return;
            }
            Class<?> mb = Class.forName(CLS_MI_BLUR, false, row.getClass().getClassLoader());
            mb.getMethod("setGlassOutlineEnable", View.class, Boolean.TYPE).invoke(null, row, true);
            mb.getMethod("setBlurOutlineEnable", View.class, Boolean.TYPE).invoke(null, row, true);
            int n = RADII_CLIP_COUNT.incrementAndGet();
            if (n <= 6) {
                L.log(4, TAG, "L38 official outline clip on r=" + Arrays.toString(radii));
            }
        }
        catch (Throwable t) {
            L.log(5, TAG, "L38 glass/blur outline enable failed: " + t);
        }
    }

    private static boolean applyHeadsUpGlass(XposedModule module, View bg, ClassLoader sysuiCl, String tag) {
        try {
            if (bg.getWidth() <= 0 || bg.getHeight() <= 0) {
                return false;
            }
            Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, sysuiCl);
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, sysuiCl);
            Resources res = CcHooks.getSysuiResources(module, bg.getContext());
            Context ctx = bg.getContext();
            miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 0, bg);
            try {
                Drawable d = res.getDrawable(2131236940, null);
                if (d != null) {
                    Method scb = bg.getClass().getMethod("setCustomBackground", Drawable.class);
                    scb.setAccessible(true);
                    scb.invoke(bg, d);
                }
            }
            catch (Throwable t) {
                L.log(5, TAG, tag + " setCustomBackground failed: " + t);
            }
            int[] colors = new int[]{res.getColor(2131102658, null), res.getInteger(2131493144), res.getColor(2131102659, null), res.getInteger(2131493145)};
            try {
                miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, bg);
            }
            catch (Throwable scb) {
                // empty catch block
            }
            try {
                miBlur.getMethod("clearMiBackgroundBlendColorCompat", View.class).invoke(null, bg);
            }
            catch (Throwable scb) {
                // empty catch block
            }
            boolean done = false;
            try {
                miBlur.getMethod("setMiBackgroundBlendColors$default", View.class, int[].class).invoke(null, bg, colors);
                done = true;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (!done) {
                miBlur.getMethod("setMiBackgroundBlendColors", View.class, int[].class, Float.TYPE).invoke(null, bg, colors, Float.valueOf(1.0f));
            }
            miGlass.getMethod("setMiGlassCompat", View.class, float[].class).invoke(null, bg, GLASS_PARAMS_HEADSUP);
            miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 1, bg);
            int n = CcHooks.countAndLog(tag);
            if (n > 0) {
                L.log(4, TAG, tag + " headsupGlass " + bg.getWidth() + "x" + bg.getHeight() + " colors=" + Arrays.toString(colors));
            }
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, tag + " headsupGlass failed: " + t);
            return false;
        }
    }

    private static boolean applyGlassOverWindowBlur(XposedModule module, View v, ClassLoader sysuiCl, String tag) {
        return CcHooks.applyGlassOverWindowBlur(module, v, sysuiCl, tag, -1.0f);
    }

    private static boolean applyGlassOverWindowBlur(XposedModule module, View v, ClassLoader sysuiCl, String tag, float cornerRadius) {
        try {
            float[] params;
            boolean soft;
            if (v.getWidth() <= 0 || v.getHeight() <= 0) {
                return false;
            }
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, sysuiCl);
            Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, sysuiCl);
            if (cornerRadius >= 0.0f) {
                final float r = cornerRadius;
                v.setClipToOutline(true);
                v.setOutlineProvider(new ViewOutlineProvider(){

                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), r);
                    }
                });
                v.invalidateOutline();
            }
            try {
                miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 0, v);
            }
            catch (Throwable r) {
                // empty catch block
            }
            try {
                miBlur.getMethod("setBlurOutlineEnable", View.class, Boolean.TYPE).invoke(null, v, false);
            }
            catch (Throwable r) {
                // empty catch block
            }
            try {
                miBlur.getMethod("setGlassOutlineEnable", View.class, Boolean.TYPE).invoke(null, v, true);
            }
            catch (Throwable t) {
                L.log(5, TAG, tag + " glassOutlineEnable failed: " + t);
            }
            miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            try {
                miBlur.getMethod("clearMiBackgroundBlendColorCompat", View.class).invoke(null, v);
            }
            catch (Throwable t) {
                // empty catch block
            }
            try {
                miBlur.getMethod("addMiBackgroundBlendColorCompat", View.class, Integer.TYPE, Integer.TYPE).invoke(null, v, -8289919, 107);
                miBlur.getMethod("addMiBackgroundBlendColorCompat", View.class, Integer.TYPE, Integer.TYPE).invoke(null, v, -4980992, 106);
            }
            catch (Throwable t) {
                // empty catch block
            }
            int[] colors = new int[]{-8289919, 107, -4980992, 106};
            boolean bl = soft = !"0".equals(CcHooks.getProp("debug.hyperclassic.soft", "1").trim());
            if (soft) {
                colors = new int[]{-6645094, 74, -3539098, 74};
            }
            boolean done = false;
            try {
                miBlur.getMethod("setMiBackgroundBlendColors$default", View.class, int[].class).invoke(null, v, colors);
                done = true;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (!done) {
                try {
                    miBlur.getMethod("setMiBackgroundBlendColors", View.class, int[].class, Float.TYPE).invoke(null, v, colors, Float.valueOf(1.0f));
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            if ((params = CcHooks.loadGlassParamsFromRes(v)) == null || params.length < 34) {
                params = GLASS_PARAMS_FALLBACK;
            }
            miGlass.getMethod("setMiGlassCompat", View.class, float[].class).invoke(null, v, params);
            miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            try {
                miGlass.getMethod("setMiGlassSdfMaxSizeCompat", View.class, Float.TYPE, Float.TYPE).invoke(null, v, Float.valueOf(v.getWidth()), Float.valueOf(v.getHeight()));
            }
            catch (Throwable t) {
                L.log(5, TAG, tag + " sdfSize failed: " + t);
            }
            int n = CcHooks.countAndLog(tag);
            if (n > 0) {
                L.log(4, TAG, tag + " officialGlassSeq " + v.getWidth() + "x" + v.getHeight() + " sdf=" + v.getWidth() + "x" + v.getHeight() + " glassOutline=true blurOutline=false colors=" + Arrays.toString(colors));
            }
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, tag + " glassOverWindowBlur failed: " + t);
            return false;
        }
    }

    private static boolean applyGlassMaterialOnly(XposedModule module, View v, ClassLoader sysuiCl, String tag) {
        try {
            float[] garr;
            if (v.getWidth() <= 0 || v.getHeight() <= 0) {
                return false;
            }
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, sysuiCl);
            Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, sysuiCl);
            miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            try {
                miBlur.getMethod("clearMiBackgroundBlendColorCompat", View.class).invoke(null, v);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            int[] blend = new int[]{0x40FFFFFF};
            boolean done = false;
            try {
                miBlur.getMethod("setMiBackgroundBlendColorsNew$default", View.class, int[].class).invoke(null, v, blend);
                done = true;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (!done) {
                try {
                    miBlur.getMethod("setMiBackgroundBlendColors", View.class, int[].class, Float.TYPE).invoke(null, v, blend, Float.valueOf(1.0f));
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class).invoke(null, 80, v);
            miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE).invoke(null, v, true);
            if (v.isAttachedToWindow()) {
                CcHooks.callSetMiBlurWinExc(v, "NotificationShade", module);
            }
            if ((garr = CcHooks.loadGlassParamsFromRes(v)) == null || garr.length < 20) {
                garr = GLASS_PARAMS_FALLBACK;
            }
            miGlass.getMethod("setMiGlassCompat", View.class, float[].class).invoke(null, v, garr);
            miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            miGlass.getMethod("setMiGlassBlurRadius", View.class, Integer.TYPE, Integer.TYPE).invoke(null, v, 80, 80);
            int n = CcHooks.countAndLog(tag);
            if (n > 0) {
                L.log(4, TAG, tag + " materialOnly " + v.getWidth() + "x" + v.getHeight() + " blend=40FFFFFF");
            }
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, tag + " materialOnly failed: " + t);
            return false;
        }
    }

    private static void dumpMediaTree(XposedModule module, View v) {
        try {
            L.log(5, TAG, "===== MEDIA TREE DUMP BEGIN =====");
            View root = v;
            for (int depth = 0; root.getParent() instanceof View && depth < 20; ++depth) {
                root = (View)root.getParent();
            }
            L.log(5, TAG, "root=" + root.getClass().getSimpleName() + " " + root.getWidth() + "x" + root.getHeight());
            CcHooks.dumpMediaNode(module, root, 0, v);
            L.log(5, TAG, "===== MEDIA TREE DUMP END =====");
        }
        catch (Throwable t) {
            L.log(5, TAG, "dumpMediaTree failed: " + t);
        }
    }

    private static void dumpMediaNode(XposedModule module, View n, int depth, View target) {
        try {
            if (depth > 12) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < depth; ++i) {
                sb.append("\u00b7\u00b7");
            }
            String name = n.getClass().getSimpleName();
            sb.append(name);
            if (n == target) {
                sb.append(" \u2605TARGET");
            }
            sb.append(" ").append(n.getWidth()).append("x").append(n.getHeight());
            sb.append(" id=0x").append(Integer.toHexString(n.getId()));
            sb.append(" vis=").append(n.getVisibility());
            sb.append(" a=").append(n.getAlpha());
            Drawable bg = n.getBackground();
            sb.append(" bg=").append(bg == null ? "null" : bg.getClass().getSimpleName());
            if (bg instanceof GradientDrawable) {
                try {
                    Field f = GradientDrawable.class.getDeclaredField("mFillPaint");
                    f.setAccessible(true);
                    Object paint = f.get(bg);
                    if (paint instanceof Paint) {
                        sb.append("(fill=#").append(String.format("%08X", ((Paint)paint).getColor())).append(")");
                    }
                }
                catch (Throwable f) {
                    // empty catch block
                }
            }
            sb.append(" clip=").append(n.getClipToOutline());
            sb.append(" layer=").append(n.getLayerType());
            int[] loc = new int[2];
            n.getLocationOnScreen(loc);
            sb.append(" scr=").append(loc[0]).append(",").append(loc[1]);
            L.log(5, TAG, sb.toString());
            if (n instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup)n;
                for (int i = 0; i < vg.getChildCount(); ++i) {
                    CcHooks.dumpMediaNode(module, vg.getChildAt(i), depth + 1, target);
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static float[] computeMediaParamsWanted() {
        long now = SystemClock.elapsedRealtime();
        if (now - MEDIA_PARAMS_CHECK_AT < 500L) {
            return MEDIA_PARAMS_SNAPSHOT;
        }
        MEDIA_PARAMS_CHECK_AT = now;
        try {
            String liftS;
            String maskS;
            String opS;
            int idx = CcHooks.parseIntSafe(CcHooks.getProp("debug.hyperclassic.media_glass_params", "1").trim(), 1);
            float[] g = CcHooks.glassTemplate(idx);
            String satS = CcHooks.getProp("debug.hyperclassic.media_sat", "").trim();
            if (!satS.isEmpty()) {
                int sat = CcHooks.clampInt(CcHooks.parseInt(satS, 100), 0, 100);
                if (g.length > 1) {
                    g[1] = 0.05f + (float)sat / 100.0f * 0.95f;
                }
            }
            if (!(opS = CcHooks.getProp("debug.hyperclassic.media_op", "").trim()).isEmpty()) {
                int op = CcHooks.clampInt(CcHooks.parseInt(opS, 100), 0, 100);
                float v = 0.1f + (float)op / 100.0f * 0.9f;
                if (g.length > 13) {
                    g[11] = v;
                    g[12] = v;
                    g[13] = v;
                }
            }
            if (!(maskS = CcHooks.getProp("debug.hyperclassic.media_mask", "").trim()).isEmpty() && g.length > 13) {
                String[] parts = maskS.split(",");
                try {
                    if (parts.length >= 3) {
                        g[11] = CcHooks.clampF(Float.parseFloat(parts[0].trim()), 0.0f, 2.0f);
                        g[12] = CcHooks.clampF(Float.parseFloat(parts[1].trim()), 0.0f, 2.0f);
                        g[13] = CcHooks.clampF(Float.parseFloat(parts[2].trim()), 0.0f, 2.0f);
                    } else {
                        float mv;
                        g[11] = mv = CcHooks.clampF(Float.parseFloat(maskS), 0.0f, 2.0f);
                        g[12] = mv;
                        g[13] = mv;
                    }
                }
                catch (Throwable mv) {
                    // empty catch block
                }
            }
            if (!(liftS = CcHooks.getProp("debug.hyperclassic.media_lift", "").trim()).isEmpty()) {
                int lift = CcHooks.clampInt(CcHooks.parseInt(liftS, 0), 0, 100);
                if (g.length > 0) {
                    g[0] = 0.5f + (float)lift / 100.0f * 0.35f;
                }
            }
            // ★ v78 功耗优化（方案A-2）：原实现每轮无条件遍历 64 个 media_glass_pN，
            //   每项一次 getProp（反射）→ 400ms 轮询下约 9600 次反射/分钟。
            //   改为：单个开关 debug.hyperclassic.media_p_override=1 才进入逐项覆盖，
            //   默认零循环、零反射。需要逐项调参时把它置 1 即可（1s TTL 内生效）。
            if ("1".equals(CcHooks.getProp("debug.hyperclassic.media_p_override", "0").trim())) {
                for (int i = 0; i < g.length && i < 64; ++i) {
                    String ps = CcHooks.getProp("debug.hyperclassic.media_glass_p" + i, "").trim();
                    if (ps.isEmpty()) continue;
                    try {
                        g[i] = Float.parseFloat(ps);
                        continue;
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                }
            }
            MEDIA_PARAMS_SNAPSHOT = g;
            return g;
        }
        catch (Throwable t) {
            return MEDIA_PARAMS_SNAPSHOT;
        }
    }

    private static boolean sameParams(float[] a, float[] b) {
        if (a == null || b == null) {
            return a == b;
        }
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; ++i) {
            if (a[i] == b[i]) continue;
            return false;
        }
        return true;
    }

    private static void startMediaLivePoll(final XposedModule module) {
        if (MEDIA_LIVE_POLL_STARTED) {
            return;
        }
        MEDIA_LIVE_POLL_STARTED = true;
        final Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable(){

            @Override
            public void run() {
                // ★ v78 功耗优化（方案A-3）：400ms → 1000ms，且在无媒体视图时整轮跳过。
                //   原实现无论有无媒体都在跑，每轮还要 6+ 次 getProp 反射。
                try {
                    if (MEDIA_BG_GUARDED.isEmpty()) {
                        h.postDelayed((Runnable)this, 1000L);
                        return;
                    }
                } catch (Throwable ignored) {}
                // ★ v79 自愈：首次冷启动时若布局超时导致媒体卡片没套上玻璃，
                //   这里巡检补一次（视图已 attach 且有尺寸但不在 MEDIA_GLASS_DONE 中）。
                // ★ v80 关键修正：AOD↔锁屏切换时官方 clear() 会清空玻璃并播放展开动画，
                //   此时视图不在 MEDIA_GLASS_DONE 中 → 自愈会误判"没套玻璃"并重设背景，
                //   直接把正在跑的展开动画打断（表现为「展开动画丢失，收起正常」）。
                //   因此巡检必须跳过「正在动画中」的视图。
                try {
                    ArrayList<View> healList = new ArrayList<View>(MEDIA_BG_GUARDED.keySet());
                    for (View hv : healList) {
                        if (hv == null || !hv.isAttachedToWindow()) continue;
                        if (hv.getWidth() <= 0 || hv.getHeight() <= 0) continue;
                        if (MEDIA_GLASS_DONE.contains(hv) || MEDIA_RETRY_PENDING.containsKey(hv)) continue;
                        // ★ v80：跳过正在动画/过渡中的视图（否则会打断展开动画）
                        if (CcHooks.isViewAnimating(hv)) {
                            if (MEDIA_HEAL_COUNT.get() < 5) L.log(4, TAG, "L80 heal skip (animating)");
                            continue;
                        }
                        // ★ v80：跳过 alpha 未满的视图（动画淡入过程中）
                        if (hv.getAlpha() < 0.99f) continue;
                        int hs = CcHooks.parseIntSafe(CcHooks.getProp("debug.hyperclassic.media_style", "3").trim(), 3);
                        MEDIA_RETRY_PENDING.put(hv, Boolean.TRUE);
                        // ★ v88：媒体卡片（MiuiMediaHeaderView 的 mediaBg）只走「材质」路径，
                        //   绝不走 applyMediaReflectiveGlass 的重背景路径。
                        //   【为什么】smali 实证 MiuiMediaHeaderView.setActualHeight(IZ)：
                        //       super.setActualHeight(actualHeight, ...)   // 高度插值逐帧回调
                        //       if (getOutlineProvider() != null)
                        //           invalidateOutline();                   // 每帧重算裁剪
                        //   官方 finishForMediaNotificationAnim() 先 setOutlineProvider(null)
                        //   就是为了插值期间不裁剪。而 applyMediaReflectiveGlass 会
                        //   setBackground + setClipToOutline(true) + setOutlineProvider(roundRect)，
                        //   把官方的 null 覆盖回去 → 每帧裁剪 → "多余空间瞬间被裁掉"。
                        //   【对策】媒体卡片一律走 applyMediaGlassMaterialFull85（只设材质+透明背景），
                        //   圆角由 MiGlass SDF 参数自带，不依赖 clipToOutline。
                        boolean isMediaBg88 = CcHooks.isMediaBgView88(hv);
                        if (isMediaBg88) {
                            CcHooks.applyMediaGlassMaterialFull85(module, hv, hv.getClass().getClassLoader(), true);
                            MEDIA_GLASS_DONE.add(hv);
                            int hn0 = MEDIA_HEAL_COUNT.incrementAndGet();
                            if (hn0 <= 5) L.log(4, TAG, "L88 heal media material-only #" + hn0);
                            continue;
                        }
                        if (hs == 0) {
                            CcHooks.applyOfficialMediaGlass(module, hv, hv.getClass().getClassLoader(), "L79 heal");
                        } else {
                            CcHooks.applyMediaReflectiveGlass(module, hv, hv.getClass().getClassLoader(), "L79 heal", 1);
                        }
                        int hn = MEDIA_HEAL_COUNT.incrementAndGet();
                        if (hn <= 5) L.log(4, TAG, "L79 heal media #" + hn + " " + hv.getWidth() + "x" + hv.getHeight());
                    }
                } catch (Throwable ignoredHeal) {}
                block22: {
                    try {
                        float[] wantP;
                        ClassLoader cl;
                        if (MI_GLASS_SET_PARAMS == null) {
                            try {
                                cl = null;
                                for (View vv : MEDIA_BG_GUARDED.keySet()) {
                                    if (vv == null) continue;
                                    cl = vv.getClass().getClassLoader();
                                    break;
                                }
                                if (cl != null) {
                                    Class<?> miGlass = Class.forName(CcHooks.CLS_MI_GLASS, false, cl);
                                    MI_GLASS_SET_PARAMS = miGlass.getMethod("setMiGlassCompat", View.class, float[].class);
                                    MI_GLASS_SET_TYPE = miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class);
                                    L.log(4, CcHooks.TAG, "L37 live poll: miGlass cached lazily");
                                }
                            }
                            catch (Throwable t) {
                                L.log(5, CcHooks.TAG, "L37 live poll: cache miGlass failed " + t);
                            }
                        }
                        if (MI_BLUR_SET_BLEND == null) {
                            try {
                                cl = null;
                                for (View vv : MEDIA_BG_GUARDED.keySet()) {
                                    if (vv == null) continue;
                                    cl = vv.getClass().getClassLoader();
                                    break;
                                }
                                if (cl != null) {
                                    Class<?> miBlur = Class.forName(CcHooks.CLS_MI_BLUR, false, cl);
                                    MI_BLUR_SET_BLEND = miBlur.getMethod("setMiBackgroundBlendColors$default", View.class, int[].class);
                                    L.log(4, CcHooks.TAG, "L37 live poll: blend method cached");
                                }
                            }
                            catch (Throwable t) {
                                L.log(5, CcHooks.TAG, "L37 live poll: cache blend failed " + t);
                            }
                        }
                        if (!MEDIA_BG_GUARDED.isEmpty() && MI_GLASS_SET_PARAMS != null && (wantP = CcHooks.computeMediaParamsWanted()) != null) {
                            ArrayList<View> views = new ArrayList<View>(MEDIA_BG_GUARDED.keySet());
for (View vv : views) {
                                 if (vv == null || !vv.isAttachedToWindow()) continue;
                                 float[] applied = MEDIA_PARAMS_APPLIED.get(vv);
                                if (!CcHooks.sameParams(applied, wantP)) {
                                    MI_GLASS_SET_PARAMS.invoke(null, vv, wantP.clone());
                                    if (MI_GLASS_SET_TYPE != null) {
                                        MI_GLASS_SET_TYPE.invoke(null, 1, vv);
                                    }
                                    MEDIA_PARAMS_APPLIED.put(vv, (float[])wantP.clone());
                                    int rn = MEDIA_LIVE_REFRESH.incrementAndGet();
                                    if (rn <= 30) {
                                        L.log(4, CcHooks.TAG, "L37 LIVE poll #" + rn + " p0=" + wantP[0] + " p1=" + wantP[1] + " p11=" + wantP[11] + " p19=" + wantP[19]);
                                    }
                                    vv.invalidate();
                                }
                                try {
                                    int st4 = CcHooks.parseIntSafe(CcHooks.getProp("debug.hyperclassic.media_style", "3").trim(), 3);
                                    if (st4 != 4) continue;
                                    String bs = CcHooks.getProp("debug.hyperclassic.media_blend", "40").trim();
                                    int bp = CcHooks.clampInt(CcHooks.parseInt(bs, 40), 0, 100);
                                    int ba = (int)((float)bp * 2.55f);
                                    Integer lastBlend = MEDIA_BLEND_APPLIED.get(vv);
                                    if (lastBlend != null && lastBlend == ba || MI_BLUR_SET_BLEND == null) continue;
                                    if (ba <= 0) {
                                        MI_BLUR_SET_BLEND.invoke(null, vv, new int[0]);
                                    } else {
                                        MI_BLUR_SET_BLEND.invoke(null, vv, new int[]{ba << 24 | 0xFFFFFF});
                                    }
                                    MEDIA_BLEND_APPLIED.put(vv, ba);
                                    int bn = MEDIA_BLEND_COUNT.incrementAndGet();
                                    if (bn <= 10) {
                                        L.log(4, CcHooks.TAG, "L37 LIVE blend #" + bn + " pct=" + bp + " a=" + ba);
                                    }
                                    vv.invalidate();
                                }
                                catch (Throwable throwable) {}
                            }
                        }
                    }
                    catch (Throwable t) {
                        int ec = MEDIA_POLL_ERR.incrementAndGet();
                        if (ec > 3) break block22;
                        L.log(5, CcHooks.TAG, "L37 LIVE poll error: " + t);
                    }
                }
                h.postDelayed((Runnable)this, 1000L);
            }
        });
        L.log(4, TAG, "L37 media live poll started (1000ms, v78 idle-skip)");
    }

    private static void installMediaBgGuard(XposedModule module, View v) {
        Object tag = v.getTag(2131689505);
        MEDIA_BG_GUARDED.put(v, tag instanceof Integer ? (Integer)tag : -1);
        CcHooks.installMediaSetBgTracer(module);
        CcHooks.startMediaLivePoll(module);
        if (MEDIA_BG_GUARD_INSTALLED) {
            return;
        }
        MEDIA_BG_GUARD_INSTALLED = true;
        try {
            Method drawMethod = View.class.getDeclaredMethod("draw", Canvas.class);
            module.hook((Executable)drawMethod).setId("cc_media_bg_guard").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                block13: {
                    try {
                        boolean ok;
                        View vv;
                        Integer want;
                        Object self = chain.getThisObject();
                        if (!(self instanceof View) || (want = MEDIA_BG_GUARDED.get(vv = (View)self)) == null) break block13;
                        // ★ v80：动画/过渡中不干预背景——否则 AOD↔锁屏的展开动画会被打断
                        if (CcHooks.isViewAnimating(vv)) break block13;
                        boolean needBgGuard = want != -1;
                        Drawable bg = vv.getBackground();
                        boolean bl = ok = !needBgGuard;
                        if (bg instanceof GradientDrawable) {
                            try {
                                Field f = GradientDrawable.class.getDeclaredField("mFillPaint");
                                f.setAccessible(true);
                                Object paint = f.get(bg);
                                if (paint instanceof Paint && ((Paint)paint).getColor() == want.intValue()) {
                                    ok = true;
                                }
                            }
                            catch (Throwable f) {
                                // empty catch block
                            }
                        }
                        if (!ok) {
                            int a = want >>> 24 & 0xFF;
                            int r = want >> 16 & 0xFF;
                            int g = want >> 8 & 0xFF;
                            int b = want & 0xFF;
                            GradientDrawable gd = new GradientDrawable();
                            gd.setShape(0);
                            gd.setCornerRadius(24.0f * vv.getResources().getDisplayMetrics().density);
                            gd.setColor(a << 24 | r << 16 | g << 8 | b);
                            vv.setBackground((Drawable)gd);
                            int n = MEDIA_GUARD_FIX.incrementAndGet();
                            if (n <= 8) {
                                L.log(4, TAG, "L37 guard restored #" + n + " color=#" + String.format("%08X", want) + " oldBg=" + (bg == null ? "null" : bg.getClass().getSimpleName()));
                            }
                        }
                        try {
                            float[] applied;
                            float[] wantP = CcHooks.computeMediaParamsWanted();
                            if (wantP != null && MI_GLASS_SET_PARAMS != null && !CcHooks.sameParams(applied = MEDIA_PARAMS_APPLIED.get(vv), wantP)) {
                                MI_GLASS_SET_PARAMS.invoke(null, vv, wantP.clone());
                                if (MI_GLASS_SET_TYPE != null) {
                                    MI_GLASS_SET_TYPE.invoke(null, 1, vv);
                                }
                                MEDIA_PARAMS_APPLIED.put(vv, (float[])wantP.clone());
                                int rn = MEDIA_LIVE_REFRESH.incrementAndGet();
                                if (rn <= 10) {
                                    L.log(4, TAG, "L37 LIVE refresh #" + rn + " p0=" + wantP[0] + " p1=" + wantP[1] + " p11=" + wantP[11] + " p19=" + wantP[19]);
                                }
                                vv.invalidate();
                            }
                        }
                        catch (Throwable throwable) {}
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                }
                return chain.proceed();
            });
            L.log(4, TAG, "L37 media bg guard installed (global View.draw)");
        }
        catch (Throwable t) {
            L.log(5, TAG, "L37 guard install failed: " + t);
        }
    }

    private static void installMediaSetBgTracer(XposedModule module) {
        if (MEDIA_SBG_TRACER_INSTALLED) {
            return;
        }
        MEDIA_SBG_TRACER_INSTALLED = true;
        try {
            Method m = View.class.getDeclaredMethod("setBackground", Drawable.class);
            module.hook((Executable)m).setId("cc_media_sbg_trace").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                block8: {
                    try {
                        int n;
                        View vv;
                        Object self = chain.getThisObject();
                        if (!(self instanceof View) || !MEDIA_BG_GUARDED.containsKey(vv = (View)self) || (n = SBG_TRACE_COUNT.incrementAndGet()) > 6) break block8;
                        Object arg = chain.getArg(0);
                        String cls = arg == null ? "null" : arg.getClass().getSimpleName();
                        StringBuilder sb = new StringBuilder();
                        sb.append("L37 SBG#").append(n).append(" new=").append(cls);
                        if (arg instanceof GradientDrawable) {
                            try {
                                Field f = GradientDrawable.class.getDeclaredField("mFillPaint");
                                f.setAccessible(true);
                                Object paint = f.get(arg);
                                if (paint instanceof Paint) {
                                    sb.append("(fill=#").append(String.format("%08X", ((Paint)paint).getColor())).append(")");
                                }
                            }
                            catch (Throwable f) {
                                // empty catch block
                            }
                        }
                        StackTraceElement[] st = Thread.currentThread().getStackTrace();
                        int shown = 0;
                        for (StackTraceElement e : st) {
                            String c = e.getClassName();
                            if (c.startsWith("java.lang.Thread") || c.startsWith("de.robv.android.xposed") || c.startsWith("io.github.libxposed") || c.startsWith("org.lsposed") || c.contains("XposedBridge") || c.contains("XposedHelpers") || c.contains("$Proxy") || c.contains("CcHooks") || c.startsWith("LSPHooker_")) continue;
                            sb.append("\n    \u2190 ").append(c).append('.').append(e.getMethodName()).append(':').append(e.getLineNumber());
                            if (++shown >= 8) break;
                        }
                        if (shown == 0) {
                            sb.append("\n    \u2190 (stack empty after filter)");
                        }
                        L.log(5, TAG, sb.toString());
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                }
                return chain.proceed();
            });
            L.log(4, TAG, "L37 setBackground tracer installed");
        }
        catch (Throwable t) {
            L.log(5, TAG, "L37 sbg tracer failed: " + t);
        }
    }

    private static boolean applyOfficialMediaGlass(XposedModule module, View v, ClassLoader cl, String tag) {
        return CcHooks.applyOfficialMediaGlass(module, v, cl, tag, 0);
    }

    private static boolean applyOfficialMediaGlass(XposedModule module, View v, ClassLoader cl, String tag, int attempt) {
        try {
            if (v == null) {
                return false;
            }
            if (attempt == 0 && MEDIA_GLASS_DONE.contains(v)) {
                return true;
            }
            int w = v.getWidth();
            int h = v.getHeight();
            if (w <= 0 || h <= 0) {
                if (attempt >= 3) {
                    L.log(5, TAG, tag + " give up, still " + w + "x" + h);
                    return false;
                }
                View fv = v;
                ClassLoader fcl = cl;
                String ftag = tag;
                int na = attempt + 1;
                v.postDelayed(() -> CcHooks.applyOfficialMediaGlass(module, fv, fcl, ftag, na), 60L);
                L.log(4, TAG, tag + " defer(" + w + "x" + h + ") retry#" + na);
                return false;
            }
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, cl);
            Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, cl);
            Context ctx = v.getContext();
            try {
                Drawable bgd = ctx.getDrawable(2131236940);
                if (bgd != null) {
                    v.setBackground(bgd.mutate());
                }
            }
            catch (Throwable t) {
                L.log(5, TAG, tag + " setBackground failed: " + t);
            }
            int c1 = -6710887;
            int c2 = -3342592;
            int c3 = 0x61FFFFFF;
            int m1 = 100;
            int m2 = 106;
            int m3 = 3;
            try {
                Resources r = ctx.getResources();
                c1 = r.getColor(2131101050, null);
                c2 = r.getColor(2131101051, null);
                c3 = r.getColor(2131101052, null);
                m1 = r.getInteger(2131493098);
                m2 = r.getInteger(2131493097);
                m3 = r.getInteger(2131493099);
            }
            catch (Throwable t) {
                L.log(5, TAG, tag + " res read fallback: " + t);
            }
            miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 0, v);
            boolean blendOk = false;
            try {
                Class<?> notifUtil = Class.forName("com.android.systemui.statusbar.notification.utils.NotificationUtil", false, cl);
                Method m = notifUtil.getMethod("applyElementViewBlend", Context.class, View.class, Boolean.TYPE, int[].class, Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(null, ctx, v, false, new int[]{c1, m1, c2, m2, c3, m3}, false);
                blendOk = true;
            }
            catch (Throwable t) {
                try {
                    miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
                    try {
                        miBlur.getMethod("clearMiBackgroundBlendColorCompat", View.class).invoke(null, v);
                    }
                    catch (Throwable m) {
                        // empty catch block
                    }
                    miBlur.getMethod("setMiBackgroundBlendColors$default", View.class, int[].class).invoke(null, v, new int[]{c1, m1, c2, m2, c3, m3});
                    blendOk = true;
                }
                catch (Throwable t2) {
                    L.log(5, TAG, tag + " blend failed: " + t + " / " + t2);
                }
            }
            float[] garr = CcHooks.loadGlassParamsFromRes(v);
            if (garr == null || garr.length < 20) {
                garr = GLASS_PARAMS_FALLBACK;
            }
            miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            miGlass.getMethod("setMiGlassCompat", View.class, float[].class).invoke(null, v, garr);
            v.setTag(2131689504, (Object)Boolean.TRUE);
            try {
                miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class).invoke(null, CcHooks.currentBlurRadius(), v);
                miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
                miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE).invoke(null, v, true);
                miGlass.getMethod("setMiGlassBlurRadius", View.class, Integer.TYPE, Integer.TYPE).invoke(null, v, CcHooks.currentBlurRadius(), CcHooks.currentBlurRadius());
                if (v.isAttachedToWindow()) {
                    CcHooks.callSetMiBlurWinExc(v, "NotificationShade", module);
                }
            }
            catch (Throwable t) {
                L.log(5, TAG, tag + " blur chain failed: " + t);
            }
            MEDIA_GLASS_DONE.add(v);
            MEDIA_RETRY_PENDING.remove(v);
            L.log(4, TAG, tag + " OFFICIAL size=" + w + "x" + h + " blend=" + blendOk + " colors=[" + String.format("%08X", c1) + "," + m1 + "," + String.format("%08X", c2) + "," + m2 + "," + String.format("%08X", c3) + "," + m3 + "] blur=" + CcHooks.currentBlurRadius());
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, tag + " OFFICIAL failed: " + t);
            return false;
        }
    }

    private static boolean applyMediaReflectiveGlass(XposedModule module, View v, ClassLoader cl, String tag, int attempt) {
        try {
            String blendDesc;
            int[] colors;
            int style;
            Context ctx;
            Class<?> miGlass;
            Class<?> miBlur;
            int h;
            int w;
            block79: {
                if (v == null) {
                    return false;
                }
                if (attempt == 0 && MEDIA_GLASS_DONE.contains(v)) {
                    return true;
                }
                // ★ v84：撤销 v83 的 AOD 态重背景拦截（误判导致通知面板也丢背景）。
                w = v.getWidth();
                h = v.getHeight();
                if (w <= 0 || h <= 0) {
                    // ★ v79：首次冷启动时媒体卡片布局经常晚于 180ms（3×60ms），
                    //   一旦放弃就再无补应用机会 → 表现为「首次启动无背景，重启 SystemUI 才好」。
                    //   放宽到 12 次 × 80ms ≈ 1s，覆盖冷启动慢场景。
                    if (attempt >= 12) {
                        L.log(5, TAG, tag + " give up, still " + w + "x" + h);
                        MEDIA_RETRY_PENDING.remove(v);
                        return false;
                    }
                    View fv = v;
                    ClassLoader fcl = cl;
                    String ftag = tag;
                    int na = attempt + 1;
                    MEDIA_RETRY_PENDING.put(v, Boolean.TRUE);
                    v.postDelayed(() -> CcHooks.applyMediaReflectiveGlass(module, fv, fcl, ftag, na), 80L);
                    if (attempt < 3) L.log(4, TAG, tag + " defer(" + w + "x" + h + ") retry#" + na);
                    return false;
                }
                MEDIA_RETRY_PENDING.remove(v);
                miBlur = Class.forName(CLS_MI_BLUR, false, cl);
                miGlass = Class.forName(CLS_MI_GLASS, false, cl);
                ctx = v.getContext();
                style = CcHooks.parseIntSafe(CcHooks.getProp("debug.hyperclassic.media_style", "3").trim(), 1);
                int bgTint = CcHooks.clampInt(CcHooks.parseInt(CcHooks.getProp("debug.hyperclassic.media_bg_tint", "0").trim(), 0), 0, 100);
                int bgDark = CcHooks.clampInt(CcHooks.parseInt(CcHooks.getProp("debug.hyperclassic.media_bg_dark", "0").trim(), 0), 0, 100);
                try {
                    Drawable bgd = ctx.getDrawable(2131236940);
                    if (bgd == null) break block79;
                    bgd = bgd.mutate();
                    float radius = 24.0f * ctx.getResources().getDisplayMetrics().density;
                    try {
                        radius = ctx.getResources().getDimension(2131170594);
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    if (bgTint > 0 || bgDark > 0) {
                        int alphaW = (int)((float)bgTint * 2.55f);
                        int alphaB = (int)((float)bgDark * 2.55f);
                        int r = 255;
                        int g = 255;
                        int b = 255;
                        if (alphaB > 0) {
                            float k = (float)alphaB / 255.0f;
                            r = (int)((float)r * (1.0f - k));
                            g = (int)((float)g * (1.0f - k));
                            b = (int)((float)b * (1.0f - k));
                        }
                        int a = Math.max(alphaW, alphaB);
                        int color = a << 24 | r << 16 | g << 8 | b;
                        GradientDrawable gd = new GradientDrawable();
                        gd.setShape(0);
                        gd.setCornerRadius(radius);
                        gd.setColor(color);
                        bgd = gd;
                        L.log(4, TAG, tag + " bgTint tint=" + bgTint + " dark=" + bgDark + " color=#" + String.format("%08X", color) + " radius=" + radius + " viaGradient=true");
                    }
                    v.setBackground(bgd);
                    v.setClipToOutline(true);
                    final float fr = radius;
                    // ★ v94：媒体卡片不覆盖 outline provider！
                    //   【根因】官方 ExpandableViewInjector.setExtClipBottomAmount(int) 的逻辑：
                    //       if (amount != 0) { setOutlineProvider(stackingOutlineProvider); setClipToOutline(true); }
                    //       else if (getOutlineProvider() == stackingOutlineProvider) { setOutlineProvider(null); }
                    //   官方动画结束时会把 amount 归 0 并期望"清空 outline"。
                    //   但如果我们把 provider 换成自己的 roundRect（NotificationUtil$1 / 匿名类），
                    //   这个 `== stackingOutlineProvider` 判断就不成立 → 官方清不掉
                    //   → MiuiMediaHeaderView.setActualHeight 逐帧 invalidateOutline()
                    //   → 动画结束后立刻按新尺寸重裁一次。
                    //   所以媒体卡片一律不碰 outline，圆角交给官方 stackingOutlineProvider
                    //   （它用 getActualHeight() 实时算，天然跟随动画）。
                    if (!CcHooks.isMediaBgView88(v)) {
                        v.setOutlineProvider(new ViewOutlineProvider(){

                            public void getOutline(View vv, Outline o) {
                                o.setRoundRect(0, 0, vv.getWidth(), vv.getHeight(), fr);
                            }
                        });
                        v.invalidateOutline();
                    }
                    if (bgTint > 0 || bgDark > 0) {
                        int alphaW2 = (int)((float)bgTint * 2.55f);
                        int alphaB2 = (int)((float)bgDark * 2.55f);
                        int r2 = 255;
                        int g2 = 255;
                        int b2 = 255;
                        if (alphaB2 > 0) {
                            float k2 = (float)alphaB2 / 255.0f;
                            r2 = (int)((float)r2 * (1.0f - k2));
                            g2 = (int)((float)g2 * (1.0f - k2));
                            b2 = (int)((float)b2 * (1.0f - k2));
                        }
                        int a2 = Math.max(alphaW2, alphaB2);
                        int wantColor = a2 << 24 | r2 << 16 | g2 << 8 | b2;
                        v.setTag(2131689505, (Object)wantColor);
                    }
                    CcHooks.installMediaBgGuard(module, v);
                }
                catch (Throwable t) {
                    L.log(5, TAG, tag + " setBackground failed: " + t);
                }
            }
            int testColor = CcHooks.parseIntSafe(CcHooks.getProp("debug.hyperclassic.media_test_color", "0").trim(), 0);
            if (testColor > 0) {
                int tc = switch (testColor) {
                    case 1 -> -65536;
                    case 2 -> -16711936;
                    case 3 -> -16776961;
                    case 4 -> -65281;
                    default -> -256;
                };
                GradientDrawable gd = new GradientDrawable();
                gd.setShape(0);
                gd.setCornerRadius(24.0f * ctx.getResources().getDisplayMetrics().density);
                gd.setColor(tc);
                v.setBackground((Drawable)gd);
                v.setClipToOutline(false);
                v.invalidate();
                v.setTag(2131689505, (Object)tc);
                CcHooks.installMediaBgGuard(module, v);
                L.log(5, TAG, tag + " \u2605TEST-COLOR=" + testColor + " color=#" + String.format("%08X", tc) + " on " + v.getClass().getSimpleName() + "@0x" + Integer.toHexString(System.identityHashCode(v)) + " " + w + "x" + h);
            }
            if (testColor > 0 && MEDIA_TREE_DUMP.compareAndSet(false, true)) {
                CcHooks.dumpMediaTree(module, v);
            }
            miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 0, v);
            int solid = CcHooks.clampInt(CcHooks.parseInt(CcHooks.getProp("debug.hyperclassic.media_glass_solid", "0").trim(), 0), 0, 100);
            int lift = CcHooks.clampInt(CcHooks.parseInt(CcHooks.getProp("debug.hyperclassic.media_glass_lift", "0").trim(), 0), 0, 100);
            int desat = CcHooks.clampInt(CcHooks.parseInt(CcHooks.getProp("debug.hyperclassic.media_glass_desat", "0").trim(), 0), 0, 100);
            try {
                miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            }
            catch (Throwable alphaB2) {
                // empty catch block
            }
            try {
                miBlur.getMethod("clearMiBackgroundBlendColorCompat", View.class).invoke(null, v);
            }
            catch (Throwable alphaB2) {
                // empty catch block
            }
            String blendS = CcHooks.getProp("debug.hyperclassic.media_blend", "40").trim();
            int blendPct = CcHooks.clampInt(CcHooks.parseInt(blendS, 40), 0, 100);
            if (style == 4) {
                int a = (int)((float)blendPct * 2.55f);
                if (a <= 0) {
                    colors = new int[]{};
                    blendDesc = "style4-off";
                } else {
                    colors = new int[]{a << 24 | 0xFFFFFF};
                    blendDesc = "style4[" + String.format("%08X", colors[0]) + " pct=" + blendPct + " raw='" + blendS + "']";
                }
            } else if (style == 3) {
                colors = new int[]{};
                blendDesc = "none(no-blur)";
            } else if (style == 1) {
                colors = new int[]{0x40FFFFFF};
                blendDesc = "slider-style[40FFFFFF]";
            } else if (style == 2) {
                colors = new int[]{0x14FFFFFF, 100, 0x14FFFFFF, 106, 0x1FFFFFFF, 3};
                blendDesc = "faint[14FFFFFF,100,14FFFFFF,106,1FFFFFFF,3]";
            } else {
                int c1 = -6710887;
                int c2 = -3342592;
                int c3 = 0x61FFFFFF;
                int m1 = 100;
                int m2 = 106;
                int m3 = 3;
                try {
                    Resources r = ctx.getResources();
                    c1 = r.getColor(2131101050, null);
                    c2 = r.getColor(2131101051, null);
                    c3 = r.getColor(2131101052, null);
                    m1 = r.getInteger(2131493098);
                    m2 = r.getInteger(2131493097);
                    m3 = r.getInteger(2131493099);
                }
                catch (Throwable r) {
                    // empty catch block
                }
                colors = new int[]{c1, m1, c2, m2, c3, m3};
                blendDesc = "official";
            }
            boolean blendOk = false;
            if (style != 3) {
                try {
                    miBlur.getMethod("setMiBackgroundBlendColors$default", View.class, int[].class).invoke(null, v, colors);
                    blendOk = true;
                }
                catch (Throwable t) {
                    L.log(5, TAG, tag + " blend failed: " + t);
                }
            }
            if (style == 3 || style == 4) {
                try {
                    miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class).invoke(null, 0, v);
                }
                catch (Throwable t) {
                    // empty catch block
                }
                try {
                    miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class).invoke(null, 0, v);
                }
                catch (Throwable t) {
                    // empty catch block
                }
                try {
                    miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE).invoke(null, v, false);
                }
                catch (Throwable t) {
                    // empty catch block
                }
                try {
                    miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class).invoke(null, 0, v);
                }
                catch (Throwable t) {
                    // empty catch block
                }
                if (style == 3) {
                    try {
                        miBlur.getMethod("clearMiBackgroundBlendColorCompat", View.class).invoke(null, v);
                    }
                    catch (Throwable t) {}
                }
            } else {
                try {
                    miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class).invoke(null, CcHooks.currentBlurRadius(), v);
                    miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
                    miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE).invoke(null, v, true);
                    if (v.isAttachedToWindow()) {
                        CcHooks.callSetMiBlurWinExc(v, "NotificationShade", module);
                    }
                }
                catch (Throwable t) {
                    L.log(5, TAG, tag + " blur chain failed: " + t);
                }
            }
            boolean glassOff = "1".equals(CcHooks.getProp("debug.hyperclassic.media_glass_off", "0").trim());
            int paramSet = CcHooks.parseIntSafe(CcHooks.getProp("debug.hyperclassic.media_glass_params", "1").trim(), 1);
            // ★★★ 2026-11-01 14:14 从数据库还原的 v9.23 原始实现 ★★★
            //
            //  【数据库取证】
            //    messageId=17727（10:27:33，v9.23.0 安装）里存的 edit_file 记录
            //    完整保留了当时 applyMediaReflectiveGlass 的代码：
            //
            //        float[] garr = loadGlassParamsFromRes(v);
            //        if (garr == null || garr.length < 20) {
            //            garr = GLASS_PARAMS_FALLBACK;
            //        }
            //        miGlass.setMiViewMaterialTypeCompat(int.class, View.class)
            //                .invoke(null, 1, v);
            //        miGlass.setMiGlassCompat(View.class, float[].class)
            //                .invoke(null, v, garr);
            //
            //  【与 v9.41+ 的唯一实质差异】
            //    v9.23 读**资源数组** 2130903246（官方 miui 玻璃参数资源）
            //    v9.41+ 改成 computeMediaParamsWanted()（模板 + 全局快照污染）
            //    → 白底 / 观感不对的根源就在这里
            //
            //  【本次只改这一处】
            float[] garr = CcHooks.loadGlassParamsFromRes(v);
            if (garr == null || garr.length < 20) {
                garr = CcHooks.glassTemplate(paramSet);
            }
            garr = (float[])garr.clone();
            StringBuilder pchanged = new StringBuilder();
            for (int i = 0; i < garr.length && i < 64; ++i) {
                String ps = CcHooks.getProp("debug.hyperclassic.media_glass_p" + i, "").trim();
                if (ps.isEmpty()) continue;
                try {
                    float pv = Float.parseFloat(ps);
                    if (garr[i] == pv) continue;
                    pchanged.append(i).append(':').append(garr[i]).append("->").append(pv).append(' ');
                    garr[i] = pv;
                    continue;
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            if (style == 3 && (solid > 0 || lift > 0 || desat > 0)) {
                if (solid > 0 && garr.length > 0) {
                    garr[0] = 0.67f + (float)solid / 100.0f * 0.21f;
                }
                if (desat > 0 && garr.length > 1) {
                    garr[1] = 0.16f + (float)desat / 100.0f * 0.6f;
                }
                if (lift > 0 && garr.length > 2) {
                    garr[2] = 0.09f + (float)lift / 100.0f * 0.18f;
                }
            }
            if (glassOff) {
                miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 0, v);
                L.log(5, TAG, tag + " GLASS-OFF experiment: materialType=0");
            } else {
                MEDIA_GLASS_PARAMS_WANT = (float[])garr.clone();
                miGlass.getMethod("setMiGlassCompat", View.class, float[].class).invoke(null, v, garr);
                miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            }
            // v9.68 media: p0 异常时改用官方 42 项
            // 0.72 是 applyGuiToGlassParams 用 GC_CONTRAST=0 算出来的旧配方值，
            // 会导致白底/无背景。检测到就切回官方数组。
            if (garr.length >= 20 && garr[0] > 0.70f && garr[0] < 0.75f) {
                L.log(4, TAG, tag + " media params suspicious p0=" + garr[0] + " -> official 42");
                garr = GLASS_PARAMS_FALLBACK.clone();
                MEDIA_GLASS_PARAMS_WANT = (float[]) garr.clone();
                try {
                    miGlass.getMethod("setMiGlassCompat", View.class, float[].class).invoke(null, v, garr);
                    miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
                } catch (Throwable ignored) {
                }
            }
            v.setTag(2131689504, (Object)Boolean.TRUE);
            if (paramSet == 1 || pchanged.length() > 0 || style == 3 && (solid > 0 || lift > 0 || desat > 0)) {
                L.log(4, TAG, tag + " params set=" + paramSet + " p0=" + garr[0] + " p1=" + garr[1] + " p2=" + garr[2] + " p14=" + garr[Math.min(14, garr.length - 1)] + " p19=" + garr[Math.min(19, garr.length - 1)] + (pchanged.length() > 0 ? " manual=[" + pchanged + "]" : ""));
            }
            if (style != 3) {
                try {
                    miGlass.getMethod("setMiGlassBlurRadius", View.class, Integer.TYPE, Integer.TYPE).invoke(null, v, CcHooks.currentBlurRadius(), CcHooks.currentBlurRadius());
                }
                catch (Throwable i) {
                    // empty catch block
                }
            }
            StringBuilder diag = new StringBuilder();
            try {
                Object bm = miBlur.getMethod("getMiBackgroundBlurModeCompat", View.class).invoke(null, v);
                diag.append(" bgMode=").append(bm);
            }
            catch (Throwable bm) {
                // empty catch block
            }
            try {
                Object br = miBlur.getMethod("getMiBackgroundBlurRadiusCompat", View.class).invoke(null, v);
                diag.append(" bgRadius=").append(br);
            }
            catch (Throwable br) {
                // empty catch block
            }
            try {
                Object pw = miBlur.getMethod("getPassWindowBlurEnabledCompat", View.class).invoke(null, v);
                diag.append(" pass=").append(pw);
            }
            catch (Throwable pw) {
                // empty catch block
            }
            try {
                Object vm = miBlur.getMethod("getMiViewBlurModeCompat", View.class).invoke(null, v);
                diag.append(" viewBlurMode=").append(vm);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            MEDIA_GLASS_DONE.add(v);
            MEDIA_RETRY_PENDING.remove(v);
            L.log(4, TAG, tag + " REFLECTIVE size=" + w + "x" + h + " style=" + style + " blend=" + blendOk + ":" + blendDesc + " blur=" + CcHooks.currentBlurRadius() + " id=0x" + Integer.toHexString(v.getId()) + " cls=" + v.getClass().getSimpleName() + " attached=" + v.isAttachedToWindow() + " vis=" + v.getVisibility() + " hw=" + v.isHardwareAccelerated() + " layer=" + v.getLayerType() + " alpha=" + v.getAlpha() + diag);
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, tag + " REFLECTIVE failed: " + t);
            return false;
        }
    }

    // ==================================================================
    // ★★★ 2026-11-01 14:20 通用「反射玻璃」配方（媒体卡片 style=3 的通用版）★★★
    //
    //  【来源】
    //    媒体卡片 media_style=3 在 v9.51.0 实测「渲染模式正确」，
    //    主人在 v9.23（数据库 messageId=17727 取证）也是这套。
    //    现在把**完全相同的 6 步**抽成通用函数，供托盘/通知/Toast/清除按钮复用。
    //
    //  【配方（逐字对照 applyMediaReflectiveGlass 的 style==3 分支）】
    //    ① 背景：ctx.getDrawable(0x7f08184c) 透明圆角 shape
    //       （bgTint/bgDark 都为 0，所以不着色，纯透明）
    //       + setClipToOutline(true) + 自装 roundRect outlineProvider
    //    ② setMiViewMaterialTypeCompat(0, v)         归零
    //    ③ clearMiBackgroundBlendColorCompat(v)      清 blend
    //       （style==3 不调 setMiBackgroundBlendColors）
    //    ④ 四个模糊开关全置 0：
    //         setMiBackgroundBlurModeCompat(0)
    //         setMiBackgroundBlurRadiusCompat(0)
    //         setPassWindowBlurEnabledCompat(false)
    //         setMiViewBlurModeCompat(0)
    //    ⑤ setMiGlassCompat(v, loadGlassParamsFromRes(v))
    //       ★ 资源数组 2130903246，不是模板！这是 v9.23 成功的关键
    //    ⑥ setMiViewMaterialTypeCompat(1, v)         挂玻璃
    //       + v.setTag(2131689504, TRUE)             供 L17 拦截器识别
    //    ✗ 不调 setMiGlassBlurRadius（style==3 明确跳过，否则折射层被模糊）
    //
    //  【开关】debug.hyperclassic.rg_tray / rg_notif / rg_toast / rg_dismiss
    // ==================================================================
    // ==================================================================
    // ★★★ 2026-11-01 15:30 模糊 API 全局闸门 ★★★
    //
    //  【主人实证】
    //    「中间那条'系统界面'标题的通知在刚重启完系统界面后是有玻璃效果的，
    //      但在随后就变回磨砂」
    //    → RG 挂好玻璃后，官方异步回调又把模糊打开，玻璃被"糊住"。
    //
    //  【日志实证】
    //    RG applied  →  OK
    //    RG VERIFY pass=true  ← 350ms 后 pass 被改回 true（官方刷的）
    //
    //  【为什么定时清零不够】
    //    我们只能事后补，官方随时可能再改。必须"事前拦截"。
    //
    //  【修法】
    //    hook MiBlurCompat 的 4 个 setter，凡是 RG 接管过的 View，
    //    一律强制成"关模糊"的值：
    //      setMiBackgroundBlurModeCompat(v, x)      → 强制 0
    //      setMiBackgroundBlurRadiusCompat(v, x)    → 强制 0
    //      setPassWindowBlurEnabledCompat(v, x)     → 强制 false
    //      setMiViewBlurModeCompat(v, x)            → 强制 1（官方 blend 用 1）
    //    setMiGlassBlurRadius 不拦截（那是玻璃折射半径，必须保留 p21=80）。
    //
    //  【开关】debug.hyperclassic.rg_guard=1（默认 1）
    // ==================================================================
    private static volatile boolean RG_GUARD_INSTALLED = false;

    private static void installRgBlurGuard(XposedModule module, ClassLoader cl) {
        if (RG_GUARD_INSTALLED) {
            return;
        }
        synchronized (CcHooks.class) {
            if (RG_GUARD_INSTALLED) {
                return;
            }
            try {
                Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, cl);
                int hooked = 0;

                hooked += hookBlurSetter(module, miBlur, "setMiBackgroundBlurModeCompat");
                hooked += hookBlurSetter(module, miBlur, "setMiBackgroundBlurRadiusCompat");
                hooked += hookBlurSetter(module, miBlur, "setPassWindowBlurEnabledCompat");
                hooked += hookBlurSetter(module, miBlur, "setMiViewBlurModeCompat");

                L.log(4, TAG, "RG blur guard installed hooked=" + hooked);
                RG_GUARD_INSTALLED = hooked > 0;
            } catch (Throwable t) {
                L.log(5, TAG, "RG blur guard failed: " + t);
            }
        }
    }

    /**
     * 拦截单个 blur setter。RG 接管过的 View 一律改用"关模糊"的值。
     *
     * @param zeroMode true=mode类setter强制0，radius类强制0，pass强制false
     */
    private static int hookBlurSetter(XposedModule module, Class<?> miBlur, String name) {
        try {
            Method target = null;
            for (Method m : miBlur.getDeclaredMethods()) {
                if (!name.equals(m.getName())) continue;
                Class<?>[] ps = m.getParameterTypes();
                if (ps.length != 2) continue;
                // ★ 2026-11-01 15:35 修正：View 可能在 index 0 或 index 1
                if (ps[0] != View.class && ps[1] != View.class) continue;
                target = m;
                break;
            }
            if (target == null) {
                L.log(5, TAG, "RG gate " + name + " signature not found");
                return 0;
            }
            target.setAccessible(true);
            final Method mt = target;
            final Class<?>[] pts = mt.getParameterTypes();
            final int viewIdx = (pts[0] == View.class) ? 0 : 1;
            final int valIdx = 1 - viewIdx;
            L.log(4, TAG, "RG gate hook " + name + " viewIdx=" + viewIdx);
            module.hook((Executable) target)
                    .setId("cc_rg_blur_gate_" + name)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        try {
                            Object vo = chain.getArg(viewIdx);
                            if (vo instanceof View) {
                                View vv = (View) vo;
                                if (vv.getTag(2131689506) == Boolean.TRUE
                                        && "1".equals(getProp("debug.hyperclassic.rg_guard", "0").trim())) {
                                    Object forced;
                                    if (pts[valIdx] == Boolean.TYPE) {
                                        forced = Boolean.FALSE;
                                    } else if (pts[valIdx] == Integer.TYPE) {
                                        forced = Integer.valueOf(0);
                                    } else {
                                        return chain.proceed();
                                    }
                                    int n = RG_GATE_HIT.incrementAndGet();
                                    if (n <= 20) {
                                        L.log(4, TAG, "RG gate " + name + " -> " + forced
                                                + " on " + vv.getClass().getSimpleName());
                                    }
                                    Object[] na = chain.getArgs().toArray();
                                    na[valIdx] = forced;
                                    na[viewIdx] = vv;
                                    return chain.proceed(na);
                                }
                            }
                        } catch (Throwable ignored) {
                        }
                        return chain.proceed();
                    });
            return 1;
        } catch (Throwable t) {
            L.log(5, TAG, "RG gate " + name + " hook failed: " + t);
            return 0;
        }
    }
    private static final java.util.concurrent.atomic.AtomicInteger RG_GATE_HIT = new java.util.concurrent.atomic.AtomicInteger(0);

    // ★ v9.88：View 是否在下拉面板通知栈内（悬浮通知不算）
    private static boolean isInShadeStack(View v) {
        try {
            for (android.view.ViewParent p = v.getParent(); p instanceof View; p = p.getParent()) {
                String n = ((View) p).getClass().getSimpleName();
                if (n.contains("NotificationStackScroll") || n.contains("NotificationShade")) {
                    return true;
                }
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    // ==================================================================
    // ★★★ v9.83：通知背景 passWindowBlur 闸门 ★★★
    //
    //  【日志实证（L17 VERIFY after-glass）】
    //    NotificationBackgroundView: pass=true
    //    父链（Row/Stack/Container/Shade）全部无模糊
    //  【结论】
    //    磨砂观感来自 NotificationBackgroundView 自己的 passWindowBlur=true
    //    （官方 GlassEffect 第一步 BlurEffect.apply 打底设的），
    //    SurfaceFlinger 对壁纸做窗口模糊，把玻璃折射垫住。
    //  【修法】
    //    hook setPassWindowBlurEnabledCompat：
    //    目标 View 是 NotificationBackgroundView 时强制 false。
    //    玻璃参数/matType 不动（那是官方挂好的折射）。
    //  【开关】debug.hyperclassic.rg_notif_pass_off（默认 1）
    // ==================================================================
    private static volatile boolean RG_PASS_GATE_INSTALLED = false;
    private static final java.util.concurrent.atomic.AtomicInteger RG_PASS_GATE_HIT = new java.util.concurrent.atomic.AtomicInteger(0);
    // ==================================================================
    // ★★★ v9.88：通知背景磨砂零值闸门 ★★★
    // ==================================================================
    private static final java.util.concurrent.atomic.AtomicInteger L48_ZERO_HIT = new java.util.concurrent.atomic.AtomicInteger(0);

    private static void installNotifBgZeroGate(XposedModule module, ClassLoader cl) {
        try {
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, cl);
            String[] names = {"setMiBackgroundBlurModeCompat", "setMiBackgroundBlurRadiusCompat"};
            for (String name : names) {
                Method target = null;
                for (Method m : miBlur.getDeclaredMethods()) {
                    if (!name.equals(m.getName())) continue;
                    Class<?>[] ps = m.getParameterTypes();
                    if (ps.length == 2 && ps[0] == Integer.TYPE && ps[1] == View.class) {
                        target = m;
                        break;
                    }
                }
                if (target == null) {
                    L.log(5, TAG, "L48 zero gate: " + name + " not found");
                    continue;
                }
                target.setAccessible(true);
                final boolean isMode = name.endsWith("ModeCompat");
                module.hook((Executable) target)
                        .setId("cc_l48_zero_" + name)
                        .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                        .intercept(chain -> {
                            try {
                                Object vo = chain.getArg(1);
                                if (!(vo instanceof View)) {
                                    return chain.proceed();
                                }
                                View vv = (View) vo;
                                String cn = vv.getClass().getSimpleName();
                                if (!(cn.contains("NotificationBackground") || cn.contains("NotificationBg"))) {
                                    return chain.proceed();
                                }
                                if (!CcHooks.isInShadeStack(vv)) {
                                    return chain.proceed();
                                }
                                if (!"1".equals(getProp("debug.hyperclassic.rg_notif_zero", "1").trim())) {
                                    return chain.proceed();
                                }
                                int n = L48_ZERO_HIT.incrementAndGet();
                                if (n <= 12) {
                                    L.log(4, TAG, "L48 zero gate: " + name + " " + chain.getArg(0) + "->0 on " + cn);
                                }
                                return chain.proceed(new Object[]{0, vv});
                            } catch (Throwable ignored) {
                            }
                            return chain.proceed();
                        });
                L.log(4, TAG, "L48 zero gate installed: " + name);
            }
        } catch (Throwable t) {
            L.log(5, TAG, "L48 zero gate failed: " + t);
        }
    }

    private static boolean installNotifPassGate(XposedModule module, ClassLoader cl) {
        if (RG_PASS_GATE_INSTALLED) {
            return true;
        }
        synchronized (CcHooks.class) {
            if (RG_PASS_GATE_INSTALLED) {
                return true;
            }
            try {
                Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, cl);
                Method target = null;
                for (Method m : miBlur.getDeclaredMethods()) {
                    if (!"setPassWindowBlurEnabledCompat".equals(m.getName())) continue;
                    Class<?>[] ps = m.getParameterTypes();
                    if (ps.length == 2 && ps[0] == View.class && ps[1] == Boolean.TYPE) {
                        target = m;
                        break;
                    }
                }
                if (target == null) {
                    L.log(5, TAG, "L20 pass gate: signature not found");
                    return false;
                }
                target.setAccessible(true);
                module.hook((Executable) target)
                        .setId("cc_rg_pass_gate")
                        .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                        .intercept(chain -> {
                            try {
                                Object vo = chain.getArg(0);
                                if (!(vo instanceof View)) {
                                    return chain.proceed();
                                }
                                View vv = (View) vo;
                                String cn = vv.getClass().getSimpleName();
                                boolean isNotifBg = cn.contains("NotificationBackground") || cn.contains("NotificationBg");
                                // ★ v9.88 作用域守卫：只在通知栈内强制 pass=false。
                                //   悬浮通知（heads-up）的行也是 NotificationBackgroundView，
                                //   但它默认就是玻璃，绝不能碰。
                                boolean isNotifBgInStack = isNotifBg && CcHooks.isInShadeStack(vv);
                                boolean enabled = "1".equals(getProp("debug.hyperclassic.rg_notif_pass_off", "1").trim());
                                if (isNotifBgInStack && enabled) {
                                    // 只要玻璃参数在（L17 已挂），pass 一律关
                                    int n = RG_PASS_GATE_HIT.incrementAndGet();
                                    if (n <= 12) {
                                        L.log(4, TAG, "L20 pass gate -> false on " + cn + " (was " + chain.getArg(1) + ")");
                                    }
                                    return chain.proceed(new Object[]{vv, Boolean.FALSE});
                                }
                            } catch (Throwable ignored) {
                            }
                            return chain.proceed();
                        });
                RG_PASS_GATE_INSTALLED = true;
                L.log(4, TAG, "L20 pass gate installed (NotificationBackgroundView -> pass=false)");

                // ★★★ v9.88：通知背景磨砂零值闸门 ★★★
                //  【real=[...] 实证（v9.87）】
                //    NotificationBackgroundView: BackgroundBlurMode=1 Radius=80
                //    BlendColors=[Point(-8289919,107), Point(-4980992,106)] ← 42% 着色
                //  磨砂 = 官方 GlassEffect 第一步 BlurEffect.apply 设的打底模糊，
                //  把 42 项玻璃折射参数垫住了。掐断它：
                //    setMiBackgroundBlurModeCompat / setMiBackgroundBlurRadiusCompat
                //    → 目标是通知栈内 NotificationBackgroundView 时强制写 0。
                //  悬浮通知不在栈内，不受影响。
                installNotifBgZeroGate(module, cl);
                return true;
            } catch (Throwable t) {
                L.log(5, TAG, "L20 pass gate failed: " + t);
                return false;
            }
        }
    }
    //  【主人实证】
    //    「qs 与通知在首次重启后是玻璃，但一旦发生某种更新（比如切深色模式），
    //      效果就会变回磨砂」
    //
    //  【官方源码】NotificationRowGlassEffect.clear(Object)
    //    bg = row.getInjector().getBackgroundNormal();
    //    MiBlurCompat.setMiViewBlurModeCompat(0, bg);
    //    MiBlurCompat.clearMiBackgroundBlendColorCompat(bg);
    //    float[42] zeros = {...全 0...}
    //    MiGlassCompat.setMiGlassCompat(bg, zeros);              ← 玻璃参数清零！
    //    MiGlassCompat.setMiViewMaterialTypeCompat(0, bg);        ← 材质归零！
    //
    //  【什么时候调用】
    //    配置变化（深色切换）、Effect 类型切换、通知复用等，
    //    都会先 clear 再 apply。clear 把玻璃抹掉后，
    //    如果之后没有再 apply，就停留在"磨砂/无背景"状态。
    //
    //  【修法】
    //    hook 所有 *GlassEffect 的 clear(Object)，对 RG 接管过的 View
    //    （tag 2131689506 == TRUE）直接跳过，不做任何清理。
    //
    //  【开关】debug.hyperclassic.rg_clear_guard=1（默认 1）
    // ==================================================================
    private static volatile boolean RG_CLEAR_GUARD_INSTALLED = false;

    private static void installRgClearGuard(XposedModule module, ClassLoader cl) {
        if (RG_CLEAR_GUARD_INSTALLED) {
            return;
        }
        synchronized (CcHooks.class) {
            if (RG_CLEAR_GUARD_INSTALLED) {
                return;
            }
            int hooked = 0;
            String[] effectClasses = new String[]{
                    // 通知
                    "com.android.systemui.statusbar.notification.style.vieweffect.NotificationRowGlassEffect",
                    "com.android.systemui.statusbar.notification.style.vieweffect.NotificationRowGlassFullAodEffect",
                    "com.android.systemui.statusbar.notification.style.vieweffect.NotificationRowGlassOnKeyguardEffect",
                    "com.android.systemui.statusbar.notification.style.vieweffect.NotificationRowGlassOnKeyguardLightWallPaperEffect",
                    // 媒体
                    "com.android.systemui.statusbar.notification.style.vieweffect.MediaViewGlassEffect",
                    "com.android.systemui.statusbar.notification.style.vieweffect.MediaViewGlassFullAodEffect",
                    "com.android.systemui.statusbar.notification.style.vieweffect.MediaViewGlassOnKeyguardEffect",
                    // 清除按钮
                    "com.android.systemui.statusbar.notification.style.vieweffect.NotificationDismissViewGlassEffect",
                    // 其他
                    "com.android.systemui.statusbar.notification.style.vieweffect.HeadsUpNotificationGlassEffect",
                    "com.android.systemui.statusbar.notification.style.vieweffect.HeadsUpNotificationGlassDarkEffect",
                    "com.android.systemui.statusbar.notification.style.vieweffect.FocusNotificationGlassEffect",
                    "com.android.systemui.statusbar.notification.style.vieweffect.GroupCollapseButtonGlassEffect",
                    "com.android.systemui.statusbar.notification.style.vieweffect.FlipRowGlassEffect",
                    "com.android.systemui.statusbar.notification.style.vieweffect.ModalDialogGlassEffect",
                    "com.android.systemui.statusbar.notification.style.vieweffect.ModalMenuGlassEffect",
                    "com.android.systemui.statusbar.notification.style.vieweffect.MediaDeleteMenuGlassEffect",
            };
            for (String clsName : effectClasses) {
                hooked += hookGlassClear(module, cl, clsName);
            }
            L.log(4, TAG, "RG clear guard installed hooked=" + hooked);
            RG_CLEAR_GUARD_INSTALLED = true;
        }
    }

    private static int hookGlassClear(XposedModule module, ClassLoader cl, String clsName) {
        try {
            Class<?> cls = Class.forName(clsName, false, cl);
            Method target = null;
            for (Method m : cls.getDeclaredMethods()) {
                if (!"clear".equals(m.getName())) continue;
                if (m.getParameterCount() != 1) continue;
                target = m;
                break;
            }
            if (target == null) {
                return 0;
            }
            target.setAccessible(true);
            final String shortName = clsName.substring(clsName.lastIndexOf('.') + 1);
            module.hook((Executable) target)
                    .setId("cc_rg_clear_gate_" + shortName)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        try {
                            Object row = chain.getArg(0);
                            if (row != null
                                    && "1".equals(getProp("debug.hyperclassic.rg_clear_guard", "0").trim())) {
                                Object injector = CcHooks.invokeNoArg(row, "getInjector");
                                Object bg = injector == null ? null
                                        : CcHooks.invokeNoArg(injector, "getBackgroundNormal");
                                if (bg instanceof View
                                        && ((View) bg).getTag(2131689506) == Boolean.TRUE) {
                                    int n = RG_CLEAR_SKIP.incrementAndGet();
                                    if (n <= 12) {
                                        L.log(4, TAG, "RG clear SKIP " + shortName
                                                + " on " + bg.getClass().getSimpleName());
                                    }
                                    return null;   // 直接跳过，不清玻璃
                                }
                            }
                        } catch (Throwable ignored) {
                        }
                        return chain.proceed();
                    });
            return 1;
        } catch (Throwable t) {
            return 0;
        }
    }

    private static final java.util.concurrent.atomic.AtomicInteger RG_CLEAR_SKIP =
            new java.util.concurrent.atomic.AtomicInteger(0);
    private static final java.util.Set<View> RG_APPLIED =
            java.util.Collections.synchronizedSet(java.util.Collections.newSetFromMap(new java.util.WeakHashMap<>()));

    /**
     * ★★★ v9.71 关键修复：RG 去重改为「按深浅色模式」★★★
     *
     *  【主人实证】
     *    「通知与 qs 切深色后还是变磨砂」
     *
     *  【为什么】
     *    旧逻辑：RG_APPLIED.add(v) 失败就 return true（跳过）。
     *    → 同一个 View 一辈子只套一次玻璃。
     *    → 切深色时系统 clear() 清空参数 + 重新 apply 官方效果，
     *      RG 想重跑却被去重拦住 → 停留在磨砂。
     *
     *  【修法】
     *    按 (View, isNight) 做 key：
     *      · 深色↔浅色切换 → key 变化 → RG 重新套一遍
     *      · 同模式内重复调用 → 仍然去重（避免每帧重刷）
     */
    private static final java.util.Map<View, Boolean> RG_APPLIED_NIGHT =
            java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

    private static boolean rgShouldApply(View v, boolean night) {
        Boolean prev = RG_APPLIED_NIGHT.get(v);
        if (prev != null && prev.booleanValue() == night) {
            return false;   // 同模式已套过
        }
        RG_APPLIED_NIGHT.put(v, Boolean.valueOf(night));
        return true;        // 首次，或深浅色切换
    }

    /**
     * ★★★ v9.73 关键修复：记录所有 RG View，配置变化时主动重套 ★★★
     *
     *  【实证】
     *    「通知与 qs 切深色后还是变磨砂」
     *    L4 entry applyUiMode=0x10 (深色) 但 RG apply hook night=false
     *    → 切深色时系统**根本没有重新调用 GlassEffect.apply**
     *    → 只能靠我们自己主动重套
     *
     *  【做法】
     *    1. 每次 applyReflectiveGlass 成功，把 View 记进 RG_VIEWS
     *    2. 注册 ComponentCallbacks.onConfigurationChanged
     *    3. 配置一变（深色切换），遍历所有 RG_VIEWS 重套一遍
     *
     *  【开关】debug.hyperclassic.rg_reapply=1（默认开）
     */
    private static final java.util.Set<View> RG_VIEWS =
            java.util.Collections.synchronizedSet(java.util.Collections.newSetFromMap(new java.util.WeakHashMap<>()));

    private static volatile boolean RG_CONFIG_CB_INSTALLED = false;

    private static void registerRgView(View v, XposedModule module, ClassLoader cl, String tag, float cornerRadius) {
        if (v == null) {
            return;
        }
        RG_VIEWS.add(v);
        RG_REAPPLY_META.put(v, new Object[]{module, cl, tag, Float.valueOf(cornerRadius)});
        installRgConfigCallback(module);
    }

    private static final java.util.Map<View, Object[]> RG_REAPPLY_META =
            java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

    private static void installRgConfigCallback(XposedModule module) {
        if (RG_CONFIG_CB_INSTALLED) {
            return;
        }
        synchronized (CcHooks.class) {
            if (RG_CONFIG_CB_INSTALLED) {
                return;
            }
            try {
                Class<?> atCls = Class.forName("android.app.ActivityThread");
                Method curApp = atCls.getDeclaredMethod("currentApplication");
                curApp.setAccessible(true);
                Object app = curApp.invoke(null);
                if (!(app instanceof android.app.Application)) {
                    L.log(5, TAG, "RG config cb: no Application");
                    return;
                }
                ((android.app.Application) app).registerComponentCallbacks(new android.content.ComponentCallbacks() {
                    @Override
                    public void onConfigurationChanged(Configuration newConfig) {
                        try {
                            boolean night = (newConfig.uiMode & 0x30) == 32;
                            if (!"1".equals(getProp("debug.hyperclassic.rg_reapply", "1").trim())) {
                                return;
                            }
                            int cnt = 0;
                            java.util.List<View> snapshot;
                            synchronized (RG_VIEWS) {
                                snapshot = new java.util.ArrayList<>(RG_VIEWS);
                            }
                            for (View v : snapshot) {
                                try {
                                    if (!v.isAttachedToWindow()) continue;
                                    Object[] meta = RG_REAPPLY_META.get(v);
                                    if (meta == null) continue;
                                    // 清掉去重记录，强制重套
                                    RG_APPLIED_NIGHT.remove(v);
                                    XposedModule m = (XposedModule) meta[0];
                                    ClassLoader cl2 = (ClassLoader) meta[1];
                                    String tg = (String) meta[2];
                                    float cr = ((Float) meta[3]).floatValue();
                                    applyReflectiveGlass(m, v, cl2, tg + " cfgChg", cr);
                                    cnt++;
                                } catch (Throwable ignored) {
                                }
                            }
                            L.log(4, TAG, "RG config changed night=" + night + " reapplied=" + cnt);
                        } catch (Throwable t) {
                            L.log(5, TAG, "RG config cb failed: " + t);
                        }
                    }

                    @Override
                    public void onLowMemory() {
                    }
                });
                RG_CONFIG_CB_INSTALLED = true;
                L.log(4, TAG, "RG config callback registered");
            } catch (Throwable t) {
                L.log(5, TAG, "RG config cb install failed: " + t);
            }
        }
    }

    // ==================================================================
    // ★★★ v9.74 代码整理：RG 独占模糊通道（从源头不设，不再事后拦截）★★★
    //
    //  【主人的判断】
    //    「模糊层大概率是我们自己设的，不应该在套效果后再拦截它」
    //
    //  【审计结果】旧代码 125 处直接调 setMi*Blur*/setPassWindowBlur，
    //    分散在 5 条链路里互相打架：
    //      ① applyReflectiveGlass  → 四个开关置 0（玻璃）
    //      ② applyQSTray           → bgMode=1 bgRadius=100 pass=true glassR=100
    //      ③ applyUnifiedGlass     → bgRadius=blur bgMode=1 pass=true
    //      ④ applyBlurScaleOnly    → bgRadius=80 bgMode=1 pass=true
    //      ⑤ zeroAllBlur 定时守卫  → 200/600/1200/2500ms 反复清零
    //    套完玻璃再靠定时器压回去，本质是「自己打自己」。
    //
    //  【新架构】单一原则：
    //    RG 一旦接管某个 View（prop 开 或 打过 tag），
    //    其他所有模糊函数【在入口就 return】，不写任何模糊属性。
    //    → 没有竞争，就不需要守卫、不需要重试、不需要 zeroAllBlur 循环。
    // ==================================================================

    /** RG 接管标记（与 setMiGlassCompat 的 tag 同一个 key）。 */
    private static final int TAG_RG_OWNED = 2131689506;

    /**
     * ★★★ v9.77：RG 接管的 View 强引用登记表 ★★★
     *
     *  【为什么不能只靠 View tag】
     *    日志实证：
     *      L47 bgBlur override 48->80 view=NotificationBackgroundView
     *    → 那一刻 rgOwned() 返回 false，说明 tag 读不到。
     *    原因：View tag 会在以下情况丢失
     *      ① View 被复用到别的通知（RecyclerView 池化）
     *      ② NotificationBackgroundView.setCustomBackground 内部
     *         可能重建 drawable 并清 tag
     *      ③ 某些代码 setTag(id, null)
     *    而 WeakHashMap 登记表不受这些影响，同一个 View 对象
     *    只要还活着就一定能查到。
     */
    private static final java.util.Set<View> RG_OWNED_VIEWS =
            java.util.Collections.synchronizedSet(
                    java.util.Collections.newSetFromMap(new java.util.WeakHashMap<View, Boolean>()));

    /** 该 View 是否已被 RG 接管（tag 或登记表命中皆可）。 */
    private static boolean rgOwned(View v) {
        if (v == null) {
            return false;
        }
        try {
            if (v.getTag(TAG_RG_OWNED) == Boolean.TRUE) {
                return true;
            }
        } catch (Throwable ignored) {
        }
        try {
            return RG_OWNED_VIEWS.contains(v);
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * RG 是否独占该通道的模糊。
     * 打标优先；未打标时按元素属性和调用方给的开关判断
     * （因为 RG 往往延后两帧执行，同步链路跑的时候还没 tag）。
     */
    private static boolean rgOwnsBlur(View v, boolean rgSwitchOn) {
        return rgOwned(v) || rgSwitchOn;
    }

    /** 统一清零：RG 配方唯一的模糊写入口。 */
    private static void setBlurOff(XposedModule module, View v, ClassLoader cl, String tag) {
        try {
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, cl);
            try {
                miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class)
                        .invoke(null, 0, v);
            } catch (Throwable ignored) {
            }
            try {
                miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class)
                        .invoke(null, 0, v);
            } catch (Throwable ignored) {
            }
            try {
                miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE)
                        .invoke(null, v, false);
            } catch (Throwable ignored) {
            }
            L.log(4, TAG, tag + " blurOff");
        } catch (Throwable t) {
            L.log(5, TAG, tag + " blurOff failed: " + t);
        }
    }

    /**
     * ★ 预约打标：在同步链路跑之前就声明「这个 View 归 RG 管」。
     *
     *  【为什么必须预约】
     *    RG 往往延后两帧执行（要等测量完成）。但同一条同步链路里
     *    applyQSTray / applyBlurScaleOnly 会先把磨砂开起来。
     *    所以必须在调度 RG 的那一刻就打标，
     *    让后面的磨砂函数在入口就能看到「已被接管」。
     */
    private static void rgReserve(View v) {
        if (v == null) {
            return;
        }
        try {
            v.setTag(TAG_RG_OWNED, Boolean.TRUE);
        } catch (Throwable ignored) {
        }
        try {
            RG_OWNED_VIEWS.add(v);
        } catch (Throwable ignored) {
        }
    }

    private static boolean rgOn(String key) {
        return "1".equals(getProp("debug.hyperclassic." + key, "0").trim());
    }

    /**
     * 把媒体卡片 style=3 的反射玻璃配方套到任意 View 上。
     *
     * @param cornerRadius 圆角半径（px）；&lt;0 表示读官方 dimension
     */
    private static boolean applyReflectiveGlass(XposedModule module, View v, ClassLoader cl,
                                                String tag, float cornerRadius) {
        try {
            if (v == null || v.getWidth() <= 0 || v.getHeight() <= 0) {
                return false;
            }
            boolean nightNow = false;
            try {
                nightNow = isNightMode(v);
            } catch (Throwable ignored) {
            }
            if (!rgShouldApply(v, nightNow)) {
                return true;
            }
            L.log(4, TAG, tag + " RG start night=" + nightNow);
            registerRgView(v, module, cl, tag, cornerRadius);
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, cl);
            Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, cl);
            Context ctx = v.getContext();

            // ---------- ① 背景：官方透明圆角 shape ----------
            float radius = cornerRadius > 0 ? cornerRadius : 24.0f * ctx.getResources()
                    .getDisplayMetrics().density;
            // ★★★ 2026-11-01 14:34 修正：不覆盖官方 outline（通知形状会坏）★★★
            //
            //  【主人反馈】
            //    「通知有形变问题」——截图里通知卡片下方有凸出的弧形。
            //
            //  【原因】
            //    上一版无条件 setOutlineProvider(roundRect)，
            //    把官方通知卡片的分组形状（首/中/尾不同圆角）抹平了。
            //
// 【修法】
            //    只在「原 provider 拿不到有效 outline」时才装我们自己的。
            //    ★ 2026-11-01 14:46 托盘特殊处理：如果传了明确圆角（>=0），
            //      就强制用自己的 roundRect（主人说圆角不用对齐，统一即可）
            boolean needOutline = true;
            if (cornerRadius >= 0) {
                // 明确指定了圆角 → 强制覆盖（托盘/Toast/清除按钮走这里）
                needOutline = true;
            } else {
                try {
                    ViewOutlineProvider oldP = v.getOutlineProvider();
                    if (oldP != null) {
                        Outline probe = new Outline();
                        oldP.getOutline(v, probe);
                        android.graphics.Rect pr = new android.graphics.Rect();
                        if (probe.getRect(pr) || probe.getRadius() > 0f) {
                            needOutline = false;
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
            try {
                Drawable bgd = ctx.getDrawable(2131236940);
                if (bgd != null) {
                    // ★★★ 2026-11-01 15:20 关键修正：NotificationBackgroundView 必须用 setCustomBackground ★★★
                    //
                    //  【官方源码实证】NotificationRowBlurEffect.smali
                    //    const p0, 0x7f08184c                     ← 2131236940，同一个 drawable
                    //    invoke Context;->getDrawable(I)
                    //    invoke NotificationBackgroundView;->setCustomBackground(Drawable)
                    //    ↑ 官方用的是 setCustomBackground，不是 setBackground！
                    //
                    //  【为什么 setBackground 无效】
                    //    NotificationBackgroundView 是自绘 View，它 override 了 onDraw：
                    //      onDraw() → 画 mCustomBackground / mBackground 字段
                    //    系统 View.setBackground() 只改 mBackgroundView 属性，不参与这个自绘流程。
                    //    所以 Coding酱之前 setBackground 完全被它盖住了 → 观感没变化。
                    //
                    //  【修法】
                    //    优先走 setCustomBackground（官方路径），失败再退回 setBackground。
                    boolean customOk = false;
                    try {
                        java.lang.reflect.Method mCustom = null;
                        for (Class<?> c = v.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
                            try {
                                mCustom = c.getDeclaredMethod("setCustomBackground", Drawable.class);
                                break;
                            } catch (Throwable ignored2) {
                            }
                        }
                        if (mCustom != null) {
                            mCustom.setAccessible(true);
                            mCustom.invoke(v, bgd.mutate());
                            customOk = true;
                            L.log(4, TAG, tag + " RG setCustomBackground OK");
                        }
                    } catch (Throwable t2) {
                        L.log(5, TAG, tag + " RG setCustomBackground failed: " + t2);
                    }
                    if (!customOk) {
                        v.setBackground(bgd.mutate());
                    }
                    v.setClipToOutline(true);
                    if (needOutline) {
                        final float fr = radius;
                        v.setOutlineProvider(new ViewOutlineProvider() {
                            public void getOutline(View vv, Outline o) {
                                o.setRoundRect(0, 0, vv.getWidth(), vv.getHeight(), fr);
                            }
                        });
                    }
                    v.invalidateOutline();
                }
            } catch (Throwable t) {
                L.log(5, TAG, tag + " RG bg failed: " + t);
            }

            // ---------- ② 归零材质 ----------
            try {
                miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class)
                        .invoke(null, 0, v);
            } catch (Throwable ignored) {
            }

            // ---------- ③ blend 着色（照抄 MediaViewBlurEffect）----------
            // 官方 MediaViewBlurEffect.apply():
            //   colors = {c1,m1,c2,m2,c3,m3}
            //   c1=0x7f06057a  m1=0x7f0c00ea (linear_light = 100)
            //   c2=0x7f06057b  m2=0x7f0c00e9 (lab = 106)
            //   c3=0x7f06057c  m3=0x7f0c00eb (pure = 3)
            // 然后 NotificationUtil.applyElementViewBlend(ctx, v, false, colors, false)
            //    内部: setRoundRect + setMiViewBlurModeCompat(1) + clearBlend + setMiBackgroundBlendColors
            // 说明: 之前 RG 只 clear blend 不设，玻璃只剩一层淡模糊 → 主人反馈"很淡的模糊背景"
            try {
                Resources r = v.getResources();
                int c1 = r.getColor(2131101050, null);
                int c2 = r.getColor(2131101051, null);
                int c3 = r.getColor(2131101052, null);
                int m1 = r.getInteger(2131493098);
                int m2 = r.getInteger(2131493097);
                int m3 = r.getInteger(2131493099);
                int[] blendColors = new int[]{c1, m1, c2, m2, c3, m3};
                miBlur.getMethod("clearMiBackgroundBlendColorCompat", View.class).invoke(null, v);
                miBlur.getMethod("setMiBackgroundBlendColors$default", View.class, int[].class)
                        .invoke(null, v, blendColors);
                L.log(4, TAG, tag + " RG blend set "
                        + String.format("#%08X/%d #%08X/%d #%08X/%d", c1, m1, c2, m2, c3, m3));
            } catch (Throwable t) {
                L.log(5, TAG, tag + " RG blend failed: " + t);
            }

            // ---------- ④ 四个模糊开关全置 0 ----------
            try {
                miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class)
                        .invoke(null, 0, v);
            } catch (Throwable ignored) {
            }
            try {
                miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class)
                        .invoke(null, 0, v);
            } catch (Throwable ignored) {
            }
            try {
                miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE)
                        .invoke(null, v, false);
            } catch (Throwable ignored) {
            }
            try {
                miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class)
                        .invoke(null, 1, v);  // 官方 applyElementViewBlend 置 1
            } catch (Throwable ignored) {
            }

            // ---------- ⑤ 玻璃参数：资源数组（v9.23 成功的关键） ----------
            // ★★★ 2026-11-01 14:40 改用「媒体卡片缓存参数」★★★
            //
            //  【日志实证（两轮验证）】
            //    媒体卡片（L16）：p0=0.67 p19=72.0  ← 每次都对
            //    RG 元素（托盘/通知/清除）：p0=0.42 p19=90.0  ← 即使改用
            //      getSysuiResources() 也还是 0.42
            //
            //  【为什么两个办法都不行】
            //    loadGlassParamsFromRes(v) 用 v.getResources()
            //    getSysuiResources() 创建了 com.android.systemui 的 package context
            //    两者都受**当前进程资源配置（夜间模式/深色）**影响
            //    而媒体卡片之所以对，是因为它拿到的是 MiuiMediaHeaderView
            //    自己 inflater 时的 Context（可能带不同的 Configuration）
            //
            //  【最可靠的做法】
            //    媒体卡片每帧都在算 p0=0.67，说明这个 View 拿到的资源是对的。
            //    → 直接把媒体卡片的参数**缓存**下来（MEDIA_PARAMS_SNAPSHOT 已有），
            //      其他元素复用它，保证数值 100% 一致。
            //    没有缓存时才退回资源读取。
            int fallbackSet = parseIntSafe(getProp("debug.hyperclassic.media_glass_params", "1").trim(), 1);
            float[] garr = null;
            // ① 优先用媒体卡片缓存的参数（唯一实测正确的来源）
            float[] cached = MEDIA_GLASS_PARAMS_WANT;
            if (cached != null && cached.length >= 20) {
                garr = cached.clone();
                L.log(4, TAG, tag + " RG use media cached params p0=" + garr[0]
                        + " p19=" + garr[Math.min(19, garr.length - 1)]);
            }
            // ② 退回资源数组
            if (garr == null) {
                try {
                    Resources sysuiRes = getSysuiResources(module, v.getContext());
                    if (sysuiRes != null) {
                        String[] sa = sysuiRes.getStringArray(2130903246);
                        if (sa != null && sa.length > 0) {
                            float[] out = new float[sa.length];
                            int cnt = 0;
                            for (String s : sa) {
                                if (s == null) continue;
                                try {
                                    out[cnt++] = Float.parseFloat(s.trim());
                                } catch (Throwable ignored) {
                                }
                            }
                            if (cnt == out.length) {
                                garr = out;
                            } else if (cnt > 0) {
                                float[] trimmed = new float[cnt];
                                System.arraycopy(out, 0, trimmed, 0, cnt);
                                garr = trimmed;
                            }
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
            // ③ 再退回 View 自己的资源
            if (garr == null || garr.length < 20) {
                garr = loadGlassParamsFromRes(v);
            }
            // ④ 最后退回模板
            if (garr == null || garr.length < 20) {
                // ★★★ 2026-11-01 15:40 用官方硬编码 42 项（MediaViewGlassEffect 原文）★★★
                //  GLASS_PARAMS_FALLBACK 与官方 :array_0 完全一致：
                //  0.67 0.16 0.09 0.0 0.24 1.4 -0.02 0.3 0.6 1.0 0.03 1.0 1.0 1.0
                //  0.1 0.2 0.3 1.0 1.0 72.0 3.8 80.0 800.0 1.2 1.0 -0.4 0.6 -0.8
                //  1.4 0.7 0.8 1.15 4.0 2.0 0*8
                garr = GLASS_PARAMS_FALLBACK.clone();
            }
            garr = garr.clone();
            // ★ 兜底：p0 明显不像玻璃（<0.3）时改用官方数组
            if (garr.length >= 20 && garr[0] < 0.3f) {
                L.log(4, TAG, tag + " RG params suspicious p0=" + garr[0] + " -> use official 42");
                garr = GLASS_PARAMS_FALLBACK.clone();
            }
            // 逐元素覆盖（rg_pN）
            for (int i = 0; i < garr.length && i < 64; ++i) {
                String ps = getProp("debug.hyperclassic.rg_p" + i, "").trim();
                if (ps.isEmpty()) continue;
                try {
                    garr[i] = Float.parseFloat(ps);
                } catch (Throwable ignored) {
                }
            }

            // ---------- ⑥ 挂玻璃材质 ----------
            // ★ 2026-11-01 15:30 给 View 打 RG 接管标记（供后续 hook 判断）
            try {
                v.setTag(2131689506, Boolean.TRUE);
            } catch (Throwable ignored) {
            }
            try {
                RG_OWNED_VIEWS.add(v);
            } catch (Throwable ignored) {
            }
            L.log(4, TAG, tag + " RG before-glass p0=" + garr[0]
                    + " len=" + garr.length + " tag=" + v.getTag(TAG_MEDIA_PARAMS_OWNED));
            try {
                v.setTag(TAG_MEDIA_PARAMS_OWNED, garr);
                MEDIA_GLASS_PARAMS_WANT = garr.clone();
                miGlass.getMethod("setMiGlassCompat", View.class, float[].class).invoke(null, v, garr);
                miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class)
                        .invoke(null, 1, v);
            } catch (Throwable t) {
                L.log(5, TAG, tag + " RG glass failed: " + t);
            }
            // ✗ 不再 setTag(TRUE)：tag 已经在上面设成 garr(float[])，
            //   改成 TRUE 会让 L17 退回用全局 MEDIA_GLASS_PARAMS_WANT
            v.invalidate();

            //  【为什么需要】
            //    主人：「托盘正常存在，但效果还是磨砂玻璃」
            //    → zeroAllBlur done=5 说明 API 都调成功了
            //    → 但观感仍磨砂，说明：
            //       (a) 属性被后续代码改回来了，或
            //       (b) 磨砂来自我们没处理的通道（窗口模糊 / 父容器 / 自绘）
            //
            //  读回全部模糊属性 + 父链，一次拿够证据。
            final View fv2 = v;
            final ClassLoader fcl2 = cl;
            final String ftag2 = tag;
            v.postDelayed(() -> {
                try {
                    Class<?> mb2 = Class.forName(CLS_MI_BLUR, false, fcl2);
                    Class<?> mg2 = Class.forName(CLS_MI_GLASS, false, fcl2);
                    StringBuilder sb = new StringBuilder();
                    Object o;
                    try { o = mb2.getMethod("getMiBackgroundBlurModeCompat", View.class).invoke(null, fv2); sb.append(" bgMode=").append(o); } catch (Throwable ignored) {}
                    try { o = mb2.getMethod("getMiBackgroundBlurRadiusCompat", View.class).invoke(null, fv2); sb.append(" bgR=").append(o); } catch (Throwable ignored) {}
                    try { o = mb2.getMethod("getPassWindowBlurEnabledCompat", View.class).invoke(null, fv2); sb.append(" pass=").append(o); } catch (Throwable ignored) {}
                    try { o = mb2.getMethod("getMiViewBlurModeCompat", View.class).invoke(null, fv2); sb.append(" viewMode=").append(o); } catch (Throwable ignored) {}
                    try {
                        java.lang.reflect.Method m = mg2.getMethod("getMiGlassBlurRadius", View.class);
                        o = m.invoke(null, fv2); sb.append(" glassR=").append(o);
                    } catch (Throwable ignored) {}
                    try {
                        java.lang.reflect.Method m = null;
                        for (java.lang.reflect.Method mm : mg2.getDeclaredMethods()) {
                            if (mm.getName().startsWith("getMiViewMaterialTypeCompat")) { m = mm; break; }
                        }
                        if (m != null) { sb.append(" matType=").append(m.invoke(null, fv2)); }
                    } catch (Throwable ignored) {}
                    // 背景 drawable 类型
                    sb.append(" bg=").append(fv2.getBackground() == null ? "null" : fv2.getBackground().getClass().getSimpleName());
                    // 父链 + 每层父级的模糊状态（定位磨砂到底来自哪一层）
                    StringBuilder chain = new StringBuilder();
                    android.view.View pv = fv2;
                    for (int i = 0; i < 5 && pv.getParent() instanceof android.view.View; i++) {
                        android.view.View p2 = (android.view.View) pv.getParent();
                        chain.append(" <- ").append(p2.getClass().getSimpleName());
                        if (p2.getId() != android.view.View.NO_ID) {
                            chain.append("@0x").append(Integer.toHexString(p2.getId()));
                        }
                        // ★ v9.80b：读父层的 bgMode/bgRadius/pass，磨砂源现形
                        try {
                            Object pm = mb2.getMethod("getMiBackgroundBlurModeCompat", View.class).invoke(null, p2);
                            Object pr = mb2.getMethod("getMiBackgroundBlurRadiusCompat", View.class).invoke(null, p2);
                            Object pp = mb2.getMethod("getPassWindowBlurEnabledCompat", View.class).invoke(null, p2);
                            chain.append("[m").append(pm).append(" r").append(pr).append(" p").append(pp).append("]");
                        } catch (Throwable ignored) {
                        }
                        pv = p2;
                    }
                    L.log(4, TAG, ftag2 + " RG VERIFY" + sb + " parent=" + chain);
                } catch (Throwable t) {
                    L.log(5, TAG, ftag2 + " RG VERIFY failed: " + t);
                }
            }, 350L);

            // ★★★ 2026-11-01 15:35 持续守卫：RG 后周期性把模糊压回 0 ★★★
            //
            //  【诊断实证】RG VERIFY pass=true（350ms 后读回）
            //    zeroAllBlur 明明设了 false，350ms 后变成 true。
            //    原因是 applyQSTray / applyElementViewBlend / 动画回调
            //    都会 setPassWindowBlurEnabledCompat(true)。
            //
            //  【修法】
            //    RG 接管后，在 200/600/1200/2500ms 各补一次清零，
            //    把最后一次设置权抢回来。
            // ★★★ v9.75 恢复守卫（针对 L47 这条外部改写）★★★
            //
            //  【为什么必须恢复】
            //    v9.74 删掉守卫后，通知退回纯磨砂。日志证据：
            //      L47 glassBlur override 0->80   × 3  ← 把 RG 的 (0,0) 改成 (80,80)
            //      L47 bgBlur override 48->80     × 1  ← 背景模糊也改回 80
            //      RG VERIFY pass=true                 ← 窗口模糊仍是开
            //    这些 override 来自 hookBlurRadiusOverride（force_blur 默认 1），
            //    它在 setMiGlassBlurRadius / setMiBackgroundBlurRadiusCompat 层面
            //    拦截并强制改写，applyReflectiveGlass 内部设的值全被覆盖。
            //
            //  【与 v9.74 的区别】
            //    v9.74 的 rgOwned 判断在 L47 里只挡住了 bgBlur 一次，
            //    glassBlur 因为 getThisObject() 在静态方法里拿不到 View 而失效。
            //    所以这里【两个层面都做】：
            //      ① L47 里用 getArg 正确取 View（已修，见 hookBlurRadiusOverride）
            //      ② 这里恢复定时守卫做兜底（防止还有别人 override）
            if (!"1".equals(CcHooks.getProp("debug.hyperclassic.rg_guard_off", "0").trim())) {
                for (long dly0 : new long[]{200L, 600L, 1200L, 2500L}) {
                    final long dly = dly0;
                    v.postDelayed(() -> {
                        try {
                            // ★ v9.77：改用 rgOwned（tag + 登记表双查），
                            //   避免 tag 丢失后守卫失效
                            if (!rgOwned(v)) return;
                            zeroAllBlur(module, v, cl, tag + " RG guard+" + dly);
                        } catch (Throwable ignored) {
                        }
                    }, dly);
                }
            }

            L.log(4, TAG, tag + " RG applied size=" + v.getWidth() + "x" + v.getHeight()
                    + " p0=" + (garr.length > 0 ? garr[0] : -1)
                    + " p19=" + (garr.length > 19 ? garr[19] : -1)
                    + " radius=" + Math.round(radius)
                    + " cls=" + v.getClass().getSimpleName());
            return true;
        } catch (Throwable t) {
            L.log(5, TAG, tag + " RG failed: " + t);
            return false;
        }
    }

/** 把所有模糊相关参数清零（RG 配方专用）。 */
    private static void zeroAllBlur(XposedModule module, View v, ClassLoader cl, String tag) {
        try {
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, cl);
            int done = 0;
            try {
                miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class)
                        .invoke(null, 0, v);
                done++;
            } catch (Throwable ignored) {
            }
            try {
                miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class)
                        .invoke(null, 0, v);
                done++;
            } catch (Throwable ignored) {
            }
            try {
                miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE)
                        .invoke(null, v, false);
                done++;
            } catch (Throwable ignored) {
            }
            L.log(4, TAG, tag + " zeroAllBlur done=" + done);
        } catch (Throwable t) {
            L.log(5, TAG, tag + " zeroAllBlur failed: " + t);
        }
    }

    private static void applyBackingTint(View v, int argb) {
        try {
            Drawable target = null;
            try {
                Field f = CcHooks.findFieldDeep(v.getClass(), "mBackground");
                if (f != null) {
                    f.setAccessible(true);
                    Object o = f.get(v);
                    if (o instanceof Drawable) {
                        target = (Drawable)o;
                    }
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (target == null) {
                target = v.getBackground();
            }
            if (target == null) {
                return;
            }
            CcHooks.tintDrawableColor(target, argb, 0);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static Field findFieldDeep(Class<?> c, String name) {
        Class<?> cur = c;
        for (int depth = 0; cur != null && depth < 8; cur = cur.getSuperclass(), ++depth) {
            try {
                Field f = cur.getDeclaredField(name);
                return f;
            }
            catch (Throwable throwable) {
                continue;
            }
        }
        return null;
    }

    private static boolean hookNotifBgBacking(XposedModule module, ClassLoader cl) {
        try {
            Class<?> cls = Class.forName("com.android.systemui.statusbar.notification.row.NotificationBackgroundView", false, cl);
            Method onDraw = cls.getDeclaredMethod("onDraw", Canvas.class);
            onDraw.setAccessible(true);
            module.hook((Executable)onDraw).setId("cc_l46_backing").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                try {
                    Object self0;
                    if ("1".equals(CcHooks.getProp("debug.hyperclassic.fake_bg", "0").trim()) && (self0 = chain.getThisObject()) instanceof View) {
                        View v0 = (View)self0;
                        Field f = BG_MBACKGROUND.get(v0.getClass());
                        if (f == null) {
                            for (Class<?> c = v0.getClass(); c != null && f == null; c = c.getSuperclass()) {
                                try {
                                    f = c.getDeclaredField("mBackground");
                                    f.setAccessible(true);
                                    continue;
                                }
                                catch (Throwable throwable) {
                                    // empty catch block
                                }
                            }
                            if (f != null) {
                                BG_MBACKGROUND.put(v0.getClass(), f);
                            }
                        }
                        if (f != null) {
                            f.set(v0, null);
                            int nc = FORCE_NULL_COUNT.incrementAndGet();
                            if (nc <= 5) {
                                L.log(4, TAG, "L46 force mBackground=null before onDraw");
                            }
                        }
                    }
                }
                catch (Throwable self0) {
                    // empty catch block
                }
                Object r = chain.proceed();
                try {
                    int argb = CcHooks.buildBackingColor();
                    if (argb != 0) {
                        Object self = chain.getThisObject();
                        Object canvas = chain.getArg(0);
                        if (self instanceof View && canvas instanceof Canvas) {
                            View v = (View)self;
                            Paint paint = BACKING_PAINTS.get(v);
                            if (paint == null) {
                                paint = new Paint(1);
                                BACKING_PAINTS.put(v, paint);
                            }
                            CcHooks.drawBackingOverlay(v, (Canvas)canvas, argb, paint);
                        }
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                return r;
            });
            L.log(4, TAG, "L46 notif backing hooked (onDraw overlay)");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L46 notif backing failed: " + t);
            return false;
        }
    }

    private static void drawBackingOverlay(View v, Canvas c, int argb, Paint paint) {
        block12: {
            try {
                Object o;
                if (argb == 0 || c == null) {
                    return;
                }
                int w = v.getWidth();
                int h = v.getHeight();
                if (w <= 0 || h <= 0) {
                    return;
                }
                float[] radii = null;
                try {
                    float[] r;
                    Method m = v.getClass().getMethod("getCornerRadii", new Class[0]);
                    m.setAccessible(true);
                    o = m.invoke(v, new Object[0]);
                    if (o instanceof float[] && (r = (float[])o).length >= 8) {
                        radii = r;
                    }
                }
                catch (Throwable m) {
                    // empty catch block
                }
                paint.setColor(argb);
                paint.setStyle(Paint.Style.FILL);
                paint.setAntiAlias(true);
                if (radii != null) {
                    Path path = new Path();
                    RectF rect = new RectF(0.0f, 0.0f, (float)w, (float)h);
                    path.addRoundRect(rect, radii, Path.Direction.CW);
                    c.drawPath(path, paint);
                    break block12;
                }
                float r = 0.0f;
                try {
                    Outline o2 = new Outline();
                    ViewOutlineProvider p = v.getOutlineProvider();
                    if (p != null) {
                        p.getOutline(v, o2);
                        r = o2.getRadius();
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                if (r <= 0.0f) {
                    r = (float)Math.min(w, h) * 0.1f;
                }
                c.drawRoundRect(0.0f, 0.0f, (float)w, (float)h, r, r, paint);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    private static boolean tintDrawableColor(Drawable d, int argb, int depth) {
        if (d == null || depth > 3) {
            return false;
        }
        try {
            if (d instanceof GradientDrawable) {
                GradientDrawable gd = (GradientDrawable)d;
                gd.mutate();
                gd.setColor(argb);
                gd.invalidateSelf();
                return true;
            }
            if (d instanceof LayerDrawable) {
                LayerDrawable ld = (LayerDrawable)d;
                boolean any = false;
                for (int i = 0; i < ld.getNumberOfLayers(); ++i) {
                    if (!CcHooks.tintDrawableColor(ld.getDrawable(i), argb, depth + 1)) continue;
                    any = true;
                }
                return any;
            }
            if (d instanceof StateListDrawable) {
                return false;
            }
            d.mutate();
            d.setTint(argb);
            d.invalidateSelf();
            return true;
        }
        catch (Throwable t) {
            return false;
        }
    }

    private static int buildBackingColor() {
        CcHooks.refreshGlassParams();
        int pct = CcHooks.parseInt(CcHooks.getProp("debug.hyperclassic.glass_backing", "0"), 0);
        pct = CcHooks.clampInt(pct, 0, 100);
        if (pct == 0) {
            return 0;
        }
        int alpha = pct * 255 / 100;
        return alpha << 24 | 0xFFFFFF;
    }

    private static boolean isTruePill(View v, int w, int h) {
        if (w <= 0 || h <= 0) {
            return false;
        }
        float ratio = (float)Math.max(w, h) / (float)Math.min(w, h);
        if (ratio <= 1.6f) {
            return true;
        }
        return h > 0 && (float)w / (float)h >= 6.0f && h <= 200;
    }

    private static boolean applySliderStyleGlass(XposedModule module, View v, ClassLoader sysuiCl, String tag, int blurRadius, boolean isRound) {
        try {
            float[] garr;
            boolean canReplaceBg;
            int w = v.getWidth();
            int h = v.getHeight();
            if (w <= 0 || h <= 0) {
                return false;
            }
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, sysuiCl);
            Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, sysuiCl);
            boolean bl = canReplaceBg = isRound && CcHooks.isTruePill(v, w, h);
            if (canReplaceBg) {
                GradientDrawable round = new GradientDrawable();
                round.setShape(0);
                round.setColor(0xDFFFFFF);
                round.setCornerRadius((float)h / 2.0f);
                v.setBackground((Drawable)round);
                v.setClipToOutline(true);
                v.setOutlineProvider(new ViewOutlineProvider(){

                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), (float)view.getHeight() / 2.0f);
                    }
                });
                v.invalidateOutline();
            }
            miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            int br = blurRadius > 0 ? blurRadius : 80;
            miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class).invoke(null, br, v);
            miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE).invoke(null, v, true);
            if (v.isAttachedToWindow()) {
                CcHooks.callSetMiBlurWinExc(v, "NotificationShade", module);
            }
            garr = (garr = CcHooks.loadGlassParamsFromRes(v)) == null || garr.length < 34 ? (float[])GLASS_PARAMS_FALLBACK.clone() : (float[])garr.clone();
            CcHooks.applyGuiToGlassParams(garr);
            miGlass.getMethod("setMiGlassCompat", View.class, float[].class).invoke(null, v, garr);
            miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            miGlass.getMethod("setMiGlassBlurRadius", View.class, Integer.TYPE, Integer.TYPE).invoke(null, v, br, br);
            int n = CcHooks.countAndLog(tag + "_sliderstyle");
            if (n > 0) {
                L.log(4, TAG, tag + " sliderStyle " + w + "x" + h + " bg=0DFFFFFF blur=" + br + " glass[0=" + garr[0] + " 1=" + garr[1] + "]");
            }
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, tag + " sliderStyle failed: " + t);
            return false;
        }
    }

    private static int currentParamVersion() {
        CcHooks.refreshGlassParamsForce();
        return GC_CONTRAST << 16 | GC_TINT << 8 | GC_BLUR;
    }

    private static void refreshGlassParamsForce() {
        try {
            GLASS_PARAM_AT = 0L;
            CcHooks.refreshGlassParams();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static boolean paramChangedFor(View v) {
        try {
            int cur = CcHooks.currentParamVersion();
            Object tag = v.getTag(2131689504);
            if (tag instanceof Integer && (Integer)tag == cur) {
                return false;
            }
            v.setTag(2131689504, (Object)cur);
            return true;
        }
        catch (Throwable t) {
            return false;
        }
    }

    private static boolean applyUnifiedGlass(XposedModule module, View v, ClassLoader sysuiCl, String tag, int blurRadius, boolean doOutlineClip) {
        return CcHooks.applyUnifiedGlass(module, v, sysuiCl, tag, blurRadius, doOutlineClip, false);
    }

    private static boolean applyUnifiedGlass(XposedModule module, View v, ClassLoader sysuiCl, String tag, int blurRadius, boolean doOutlineClip, boolean isRound) {
        return CcHooks.applyUnifiedGlass(module, v, sysuiCl, tag, blurRadius, doOutlineClip, isRound, false);
    }

    private static boolean applyUnifiedGlass(XposedModule module, View v, ClassLoader sysuiCl, String tag, int blurRadius, boolean doOutlineClip, boolean isRound, boolean silent) {
        try {
            // ★★★ v9.74：RG 接管时，磨砂链路整条不执行 ★★★
            //
            //  【为什么】applyUnifiedGlass 会设：
            //    setPassWindowBlurEnabledCompat(true)   ← 6065
            //    setMiBackgroundBlurRadiusCompat(blur)  ← 内部
            //    setMiBackgroundBlurModeCompat(1)       ← 内部
            //    这些都是磨砂链路，官方玻璃配方一个都不碰。
            //  【唯一例外】applyReflectiveGlass 自己不用本函数，
            //    所以拦这里不影响玻璃本体。
            if (rgOwned(v)) {
                L.log(4, TAG, tag + " unifiedGlass skip (rg owns)");
                return true;
            }
            int n;
            float[] params;
            int w = v.getWidth();
            int h = v.getHeight();
            if (w <= 0 || h <= 0) {
                w = v.getMeasuredWidth();
                h = v.getMeasuredHeight();
            }
            if (w <= 0 || h <= 0) {
                return false;
            }
            if (!silent && !UNIFIED_GLASS_APPLIED.add(v)) {
                return true;
            }
            CcHooks.installParamRefreshGuard(module, v, sysuiCl, tag, blurRadius, doOutlineClip, isRound);
            if ("1".equals(CcHooks.getProp("debug.hyperclassic.slider_style", "0").trim())) {
                return CcHooks.applySliderStyleGlass(module, v, sysuiCl, tag, blurRadius, isRound);
            }
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, sysuiCl);
            Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, sysuiCl);
            try {
                miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 0, v);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                miBlur.getMethod("setBlurOutlineEnable", View.class, Boolean.TYPE).invoke(null, v, false);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                miBlur.getMethod("setGlassOutlineEnable", View.class, Boolean.TYPE).invoke(null, v, doOutlineClip);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            int backing = CcHooks.buildBackingColor();
            if (backing != 0) {
                CcHooks.applyBackingTint(v, backing);
            }
            try {
                miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE).invoke(null, v, true);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                if (v.isAttachedToWindow()) {
                    CcHooks.callSetMiBlurWinExc(v, "NotificationShade", module);
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            CcHooks.applyGlassBlurRadius(module, sysuiCl, v, CcHooks.sliderBlurRadius());
            int[] colors = CcHooks.buildBlendColors();
            boolean roundOk = true;
            try {
                miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                miBlur.getMethod("clearMiBackgroundBlendColorCompat", View.class).invoke(null, v);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            boolean blendOk = true;
            String blendColorProp = CcHooks.getProp("debug.hyperclassic.blend_color", "").trim();
            if (!blendColorProp.isEmpty()) {
                try {
                    int bc = (int)Long.parseLong(blendColorProp.replace("#", "").replace("0x", ""), 16);
                    int bm = CcHooks.parseIntSafe(CcHooks.getProp("debug.hyperclassic.blend_mode", "100"), 100);
                    miBlur.getMethod("setMiBackgroundBlendColors", View.class, int[].class, Float.TYPE).invoke(null, v, new int[]{bc, bm}, Float.valueOf(1.0f));
                    blendOk = true;
                }
                catch (Throwable bc) {
                    // empty catch block
                }
            }
            params = (params = CcHooks.loadGlassParamsFromRes(v)) == null || params.length < 34 ? (float[])GLASS_PARAMS_FALLBACK.clone() : (float[])params.clone();
            try {
                CcHooks.applyGuiToGlassParams(params);
            }
            catch (Throwable bm) {
                // empty catch block
            }
            miGlass.getMethod("setMiGlassCompat", View.class, float[].class).invoke(null, v, params);
            miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            try {
                int gr = blurRadius > 0 ? blurRadius : 80;
                miGlass.getMethod("setMiGlassBlurRadius", View.class, Integer.TYPE, Integer.TYPE).invoke(null, v, gr, gr);
            }
            catch (Throwable gr) {
                // empty catch block
            }
            try {
                int gr = CcHooks.sliderBlurRadius();
                miGlass.getMethod("setMiGlassBlurRadius", View.class, Integer.TYPE, Integer.TYPE).invoke(null, v, gr, gr);
                miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class).invoke(null, gr, v);
                miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            }
            catch (Throwable gr) {
                // empty catch block
            }
            int n2 = n = silent ? 0 : CcHooks.countAndLog(tag);
            if (DUMP_OTHER.incrementAndGet() <= 6) {
                CcHooks.dumpBlurState(module, v, "OTHER:" + tag);
            }
            if (n > 0) {
                L.log(4, TAG, tag + " unifiedGlass " + v.getWidth() + "x" + v.getHeight() + " blur=" + blurRadius + " outline=" + doOutlineClip + " blend=" + Integer.toHexString(colors[0]) + "/" + colors[1]);
            }
            if (silent) {
                long lastLog = LAST_DIAG_AT;
                long nowMs = SystemClock.elapsedRealtime();
                if (nowMs - lastLog > 1000L) {
                    LAST_DIAG_AT = nowMs;
                    boolean blurOpened = false;
                    try {
                        blurOpened = (Boolean)miBlur.getMethod("getBackgroundBlurOpened", Context.class).invoke(null, v.getContext());
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    L.log(4, TAG, "L43 diag " + tag + " c=" + GC_CONTRAST + " t=" + GC_TINT + " b=" + GC_BLUR + " blurOpen=" + blurOpened + " round=" + roundOk + " blend=" + blendOk + " colors=[0x" + Integer.toHexString(colors[0]) + ",0x" + Integer.toHexString(colors[1]) + ",0x" + Integer.toHexString(colors[2]) + ",0x" + Integer.toHexString(colors[3]) + "]");
                }
            }
            try {
                v.invalidateOutline();
                ViewParent vp = v.getParent();
                if (vp instanceof View) {
                    ((View)vp).invalidate();
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                v.setTag(2131689504, (Object)CcHooks.currentParamVersion());
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, tag + " unifiedGlass failed: " + t);
            return false;
        }
    }

    private static boolean applyLightGlass(XposedModule module, View v, ClassLoader sysuiCl, String tag, float cornerRadius) {
        try {
            float[] garr;
            if (v.getWidth() <= 0 || v.getHeight() <= 0) {
                if (!GLASS_RETRY.contains(v)) {
                    GLASS_RETRY.add(v);
                    v.post(() -> {
                        GLASS_RETRY.remove(v);
                        CcHooks.applyLightGlass(module, v, sysuiCl, tag, cornerRadius);
                    });
                }
                return false;
            }
            if (!LIGHTGLASS_APPLIED.add(v)) {
                return true;
            }
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, sysuiCl);
            Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, sysuiCl);
            final float r = cornerRadius < 0.0f ? (float)v.getHeight() / 2.0f : cornerRadius;
            GradientDrawable round = new GradientDrawable();
            round.setShape(0);
            round.setColor(0xDFFFFFF);
            round.setCornerRadius(r);
            v.setBackground((Drawable)round);
            v.setClipToOutline(true);
            v.setOutlineProvider(new ViewOutlineProvider(){

                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), r);
                }
            });
            v.invalidateOutline();
            miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            int[] blend = new int[]{0x40FFFFFF};
            boolean done = false;
            try {
                miBlur.getMethod("setMiBackgroundBlendColorsNew$default", View.class, int[].class).invoke(null, v, blend);
                done = true;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (!done) {
                try {
                    miBlur.getMethod("setMiBackgroundBlendColors", View.class, int[].class, Float.TYPE).invoke(null, v, blend, Float.valueOf(1.0f));
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class).invoke(null, 80, v);
            miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE).invoke(null, v, true);
            if (v.isAttachedToWindow()) {
                CcHooks.callSetMiBlurWinExc(v, "NotificationShade", module);
            }
            if ((garr = CcHooks.loadGlassParamsFromRes(v)) == null || garr.length < 20) {
                garr = GLASS_PARAMS_FALLBACK;
            }
            miGlass.getMethod("setMiGlassCompat", View.class, float[].class).invoke(null, v, garr);
            miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            miGlass.getMethod("setMiGlassBlurRadius", View.class, Integer.TYPE, Integer.TYPE).invoke(null, v, 80, 80);
            L.log(4, TAG, tag + " lightGlass applied " + v.getWidth() + "x" + v.getHeight() + " r=" + r);
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, tag + " lightGlass failed: " + t);
            return false;
        }
    }

    private static Resources getSysuiResources(XposedModule module, Context ctx) {
        if (SYSUI_RES != null) {
            return SYSUI_RES;
        }
        try {
            Context sc = ctx.createPackageContext("com.android.systemui", 2);
            SYSUI_RES = sc.getResources();
            L.log(4, TAG, "sysui resources acquired");
        }
        catch (Throwable t) {
            L.log(5, TAG, "getSysuiResources failed: " + t);
            SYSUI_RES = ctx.getResources();
        }
        return SYSUI_RES;
    }

    private static boolean applyOfficialGlass(XposedModule module, View v, ClassLoader sysuiCl, boolean mediaStyle, String tag) {
        return CcHooks.applyOfficialGlass(module, v, sysuiCl, mediaStyle, tag, true);
    }

    private static boolean applyOfficialGlass(XposedModule module, View v, ClassLoader sysuiCl, boolean mediaStyle, String tag, boolean setGlassBg) {
        float cr = mediaStyle ? 84.0f : -1.0f;
        return CcHooks.applyLightGlass(module, v, sysuiCl, tag, cr);
    }

    private static boolean applyDismissGlass(XposedModule module, View v, ClassLoader sysuiCl) {
        try {
            if (v.getWidth() <= 0 || v.getHeight() <= 0) {
                if (!GLASS_RETRY.contains(v)) {
                    GLASS_RETRY.add(v);
                    v.post(() -> {
                        GLASS_RETRY.remove(v);
                        CcHooks.applyDismissGlass(module, v, sysuiCl);
                    });
                }
                return false;
            }
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, sysuiCl);
            Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, sysuiCl);
            Resources res = CcHooks.getSysuiResources(module, v.getContext());
            try {
                miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 0, v);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                Drawable glassBg = CcHooks.getSysuiResources(module, v.getContext()).getDrawable(2131236940, null);
                if (glassBg != null) {
                    v.setBackground(glassBg);
                }
            }
            catch (Throwable t) {
                L.log(5, TAG, "Dismiss setGlassBg failed: " + t);
            }
            v.setOutlineProvider(new ViewOutlineProvider(){

                public void getOutline(View view, Outline outline) {
                    int d = Math.min(view.getWidth(), view.getHeight());
                    int ox = (view.getWidth() - d) / 2;
                    int oy = (view.getHeight() - d) / 2;
                    outline.setOval(ox, oy, ox + d, oy + d);
                }
            });
            v.setClipToOutline(true);
            v.invalidateOutline();
            try {
                miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            }
            catch (Throwable t) {
                // empty catch block
            }
            try {
                miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE).invoke(null, v, true);
            }
            catch (Throwable t) {
                // empty catch block
            }
            try {
                CcHooks.callSetMiBlurWinExc(v, "NotificationShade", module);
            }
            catch (Throwable t) {
                // empty catch block
            }
            miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            miBlur.getMethod("clearMiBackgroundBlendColorCompat", View.class).invoke(null, v);
            int[] colors4 = CcHooks.buildBlendColors();
            int[] colors = new int[]{colors4[0], colors4[1], colors4[2], colors4[3]};
            try {
                miBlur.getMethod("setMiBackgroundBlendColors$default", View.class, int[].class).invoke(null, v, new int[]{colors4[0], colors4[1], colors4[2], colors4[3], colors4[0], colors4[1] / 2});
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            miGlass.getMethod("setMiGlassCompat", View.class, float[].class).invoke(null, v, GLASS_PARAMS_DISMISS);
            miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            try {
                miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class).invoke(null, CcHooks.currentBlurRadius(), v);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                float d = Math.min((float)v.getWidth(), (float)v.getHeight());
                miGlass.getMethod("setMiGlassSdfMaxSizeCompat", View.class, Float.TYPE, Float.TYPE).invoke(null, v, Float.valueOf(d), Float.valueOf(d));
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                miBlur.getMethod("setBlurOutlineEnable", View.class, Boolean.TYPE).invoke(null, v, false);
                miBlur.getMethod("setGlassOutlineEnable", View.class, Boolean.TYPE).invoke(null, v, true);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            L.log(4, TAG, "Dismiss glass applied " + v.getWidth() + "x" + v.getHeight() + " blur=" + CcHooks.currentBlurRadius() + " blend=" + Integer.toHexString(colors[0]) + "/" + colors[1]);
            // ★★★ 2026-11-01 14:28 清除按钮改用「反射玻璃」配方（媒体 style=3 同款）★★★
            //
            //  【为什么】
            //    主人：「现在的渲染模式正确」（media_style=3）
            //    → 清除按钮套同一套 6 步配方。
            //
            //  【圆角】
            //    清除按钮是圆形。-1 表示 height/2；
            //    但它是正方形，height/2 = 直径/2 → 正好是圆。
            //  开关：debug.hyperclassic.rg_dismiss
            if (rgOn("rg_dismiss")) {
                applyReflectiveGlass(module, v, sysuiCl, "L5 dismiss RG", -1f);
            }
            // 旧的亮度条配方（保留，默认关闭）
            if (urOn("ur_dismiss")) {
                applySliderRecipeFromProps(module, v, sysuiCl, "L5 dismiss UR", -1f);
            }
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "Dismiss glass failed: " + t);
            return false;
        }
    }

    private static boolean applyPillBlurOnly(XposedModule module, View pill, ClassLoader sysuiCl) {
        try {
            // ★★★ v9.89：官方已挂玻璃时不再走磨砂 ★★★
            //  【日志实证】
            //    L36 pill OFFICIAL params=42 blur=80 blend=ff5a5a5a  ← 官方玻璃在
            //    L36 pill circle glass=false                          ← 这里把它抹了
            //  官方 GroupCollapseButtonGlassEffect 已挂完整玻璃参数时，
            //  任何磨砂写入都会把玻璃盖掉（和通知磨砂同一类问题）。
            if (CcHooks.pillHasOfficialGlass(pill)) {
                L.log(4, TAG, "L36 pill blur-only skip (official glass present)");
                return true;
            }
            int w = pill.getWidth();
            int h = pill.getHeight();
            if (w <= 0 || h <= 0) {
                w = pill.getMeasuredWidth();
                h = pill.getMeasuredHeight();
            }
            if (w <= 0 || h <= 0) {
                return false;
            }
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, sysuiCl);
            Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, sysuiCl);
            try {
                miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 0, pill);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                GradientDrawable oval = new GradientDrawable();
                oval.setShape(1);
                oval.setColor(0);
                pill.setBackground((Drawable)oval);
                pill.invalidate();
            }
            catch (Throwable oval) {
                // empty catch block
            }
            try {
                pill.setOutlineProvider(new ViewOutlineProvider(){

                    public void getOutline(View v, Outline outline) {
                        outline.setOval(0, 0, v.getWidth(), v.getHeight());
                    }
                });
                pill.setClipToOutline(true);
                pill.invalidateOutline();
            }
            catch (Throwable oval) {
                // empty catch block
            }
            try {
                miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, pill);
            }
            catch (Throwable oval) {
                // empty catch block
            }
            try {
                miBlur.getMethod("clearMiBackgroundBlendColorCompat", View.class).invoke(null, pill);
            }
            catch (Throwable oval) {
                // empty catch block
            }
            int[] colors = CcHooks.buildBlendColors();
            try {
                miBlur.getMethod("setMiBackgroundBlendColors$default", View.class, int[].class).invoke(null, pill, colors);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            boolean blurOnly = "1".equals(CcHooks.getProp("debug.hyperclassic.pill_blur_only", "0").trim());
            if (blurOnly) {
                L.log(4, TAG, "L36 pill BLUR-ONLY size=" + w + "x" + h + " blend=" + Integer.toHexString(colors[0]) + " attached=" + pill.isAttachedToWindow());
                return true;
            }
            try {
                GradientDrawable frost = new GradientDrawable();
                frost.setShape(1);
                frost.setColor(0x33FFFFFF);
                pill.setBackground((Drawable)frost);
                pill.invalidate();
            }
            catch (Throwable frost) {
                // empty catch block
            }
            float[] p = CcHooks.loadGlassParamsFromRes(pill);
            p = p == null || p.length < 29 ? (float[])GLASS_PARAMS_FALLBACK.clone() : (float[])p.clone();
            if (p.length > 21) {
                p[21] = Math.max(20, CcHooks.currentBlurRadius());
            }
            String glassErr = null;
            try {
                miGlass.getMethod("setMiGlassCompat", View.class, float[].class).invoke(null, pill, p);
            }
            catch (Throwable t) {
                glassErr = String.valueOf(t);
            }
            try {
                miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 1, pill);
            }
            catch (Throwable t) {
                glassErr = (glassErr == null ? "" : glassErr + "; ") + "type=" + t;
            }
            L.log(4, TAG, "L36 pill OFFICIAL size=" + w + "x" + h + " params=" + p.length + " blur=" + CcHooks.currentBlurRadius() + " blend=" + Integer.toHexString(colors[0]) + " attached=" + pill.isAttachedToWindow() + " err=" + glassErr);
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L36 pill OFFICIAL failed: " + t);
            return false;
        }
    }

    private static boolean applyPillGlass(XposedModule module, View pill, ClassLoader sysuiCl) {
        try {
            if (pill.getWidth() <= 0 || pill.getHeight() <= 0) {
                if (PILL_RETRY.add(pill)) {
                    pill.post(() -> {
                        PILL_RETRY.remove(pill);
                        try {
                            CcHooks.applyPillGlass(module, pill, sysuiCl);
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                    });
                }
                return false;
            }
            if (!PILL_GLASS_APPLIED.add(pill)) {
                return true;
            }
            if (!"1".equals(CcHooks.getProp("debug.hyperclassic.pill_glass", "0").trim())) {
                return CcHooks.applyPillBlurOnly(module, pill, sysuiCl);
            }
            pill.setClipToOutline(true);
            pill.setOutlineProvider(new ViewOutlineProvider(){

                public void getOutline(View v, Outline outline) {
                    outline.setOval(0, 0, v.getWidth(), v.getHeight());
                }
            });
            pill.invalidateOutline();
            int pillBlur = Math.max(20, (int)((float)CcHooks.currentBlurRadius() * 0.7f));
            boolean ok = CcHooks.applyUnifiedGlass(module, pill, sysuiCl, "L36 pill", pillBlur, true, true);
            L.log(4, TAG, "L36 pill unifiedGlass result=" + ok + " size=" + pill.getWidth() + "x" + pill.getHeight() + " blur=" + pillBlur);
            if (ok) {
                PILL_GLASS_APPLIED.add(pill);
                return true;
            }
            if (!PILL_FALLBACK_DONE.add(pill)) {
                return true;
            }
            boolean fbOk = CcHooks.applyOfficialGlass(module, pill, sysuiCl, false, "L36 pill", false);
            if (fbOk) {
                PILL_GLASS_APPLIED.add(pill);
            }
            return fbOk;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L36 pill glass failed: " + t);
            return false;
        }
    }

    private static void applyBlurScaleOnly(XposedModule module, View v, ClassLoader sysuiCl, String tag, int blurRadius) {
        try {
            // ★★★ v9.74：RG 接管时，磨砂链路整条不执行 ★★★
            //
            //  【主人的判断】「模糊层大概率是我们自己设的，不应该在套效果后再拦截」
            //  【实证】日志里连续出现：
            //      L14 notif RG RG applied p0=0.67 p19=72.0   ← 玻璃挂上了
            //      L14 bgGlass blurFull r=80 view=NotificationBackgroundView
            //      ↑ 紧接着又设 bgRadius=80 + bgMode=1 + pass=true
            //  【官方源码】NotificationRowGlassEffect.apply 从头到尾
            //    只调 setMiViewBlurModeCompat(1)，其余三个磨砂 API 一个都不碰。
            //  【修法】源头 return，不再事后用 zeroAllBlur 压制。
            if (rgOwned(v)) {
                L.log(4, TAG, tag + " blurredSkip (rg owns)");
                return;
            }
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, sysuiCl);
            int r = CcHooks.sliderBlurRadius();
            try {
                miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE).invoke(null, v, true);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                if (v.isAttachedToWindow()) {
                    CcHooks.callSetMiBlurWinExc(v, "NotificationShade", module);
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class).invoke(null, r, v);
                miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            }
            catch (Throwable t) {
                L.log(5, TAG, tag + " bgBlur failed: " + t);
            }
            int n = BLURONLY_COUNT.incrementAndGet();
            if (n <= 6) {
                L.log(4, TAG, tag + " blurFull r=" + r + " view=" + v.getClass().getSimpleName() + " " + v.getWidth() + "x" + v.getHeight());
            }
        }
        catch (Throwable t) {
            L.log(5, TAG, tag + " blurScaleOnly failed: " + t);
        }
    }

    private static boolean isInNotificationDeep(View v) {
        try {
            ViewParent p = v.getParent();
            for (int depth = 0; p != null && depth < 12; p = p.getParent(), ++depth) {
                if (!(p instanceof View)) continue;
                String cn = p.getClass().getName();
                if (cn.contains("StatusBarIconView") || cn.contains("MiuiStatusBarIcon") || cn.contains("NotificationIconArea") || cn.contains("NotificationShadeWindow")) {
                    return false;
                }
                if (!cn.contains("ExpandableNotificationRow") && !cn.contains("NotificationContentView") && !cn.contains("NotificationTemplateViewWrapper") && !cn.contains("HybridNotificationView") && !cn.contains("NotificationBigPictureTemplate") && !cn.contains("NotificationConversationTemplate") && !cn.contains("NotificationMediaTemplate")) continue;
                return true;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return false;
    }

    private static boolean hookQsDarkResources(XposedModule module, ClassLoader cl) {
        try {
            Method m;
            Method m22;
            Class<Resources> resCls = Resources.class;
            int hooked = 0;
            try {
                m22 = resCls.getMethod("getColor", Integer.TYPE);
                module.hook((Executable)m22).setId("cc_l22_getcolor").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Integer forced;
                    Object a0 = chain.getArgs().get(0);
                    if (a0 instanceof Integer && (forced = QS_DARK_COLORS.get((Integer)a0)) != null) {
                        return forced;
                    }
                    return chain.proceed();
                });
                ++hooked;
            }
            catch (Throwable thrown3) {
                // empty catch block
            }
            try {
                m22 = resCls.getMethod("getColor", Integer.TYPE, Resources.Theme.class);
                module.hook((Executable)m22).setId("cc_l22_getcolor_theme").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Integer forced;
                    Object a0 = chain.getArgs().get(0);
                    if (a0 instanceof Integer && (forced = QS_DARK_COLORS.get((Integer)a0)) != null) {
                        return forced;
                    }
                    return chain.proceed();
                });
                ++hooked;
            }
            catch (Throwable m3) {
                // empty catch block
            }
            for (int variant = 0; variant < 2; ++variant) {
                try {
                    m = variant == 0 ? resCls.getMethod("getColorStateList", Integer.TYPE) : resCls.getMethod("getColorStateList", Integer.TYPE, Resources.Theme.class);
                    String id2 = variant == 0 ? "cc_l22_csl" : "cc_l22_csl_theme";
                    module.hook((Executable)m).setId(id2).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                        Integer forced;
                        Object a0 = chain.getArgs().get(0);
                        if (a0 instanceof Integer && (forced = QS_DARK_COLORS.get((Integer)a0)) != null) {
                            return ColorStateList.valueOf((int)forced);
                        }
                        return chain.proceed();
                    });
                    ++hooked;
                    continue;
                }
                catch (Throwable m4) {
                    // empty catch block
                }
            }
            try {
                Class<Context> ctxCls = Context.class;
                m = ctxCls.getMethod("getColor", Integer.TYPE);
                module.hook((Executable)m).setId("cc_l22_ctx_getcolor").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Integer forced;
                    Object a0 = chain.getArgs().get(0);
                    if (a0 instanceof Integer && (forced = QS_DARK_COLORS.get((Integer)a0)) != null) {
                        return forced;
                    }
                    return chain.proceed();
                });
                ++hooked;
            }
            catch (Throwable ctxCls) {
                // empty catch block
            }
            try {
                Method m5 = Context.class.getMethod("getColorStateList", Integer.TYPE);
                module.hook((Executable)m5).setId("cc_l22_ctx_csl").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Integer forced;
                    Object a0 = chain.getArgs().get(0);
                    if (a0 instanceof Integer && (forced = QS_DARK_COLORS.get((Integer)a0)) != null) {
                        return ColorStateList.valueOf((int)forced);
                    }
                    return chain.proceed();
                });
                ++hooked;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            // ★ v1.0.3b：磁贴启用蓝的真正来源 —— drawable 路径。
            //   MiuiQSIconViewImpl.updateIcon 用 Resources.getDrawable(0x7f08112c)
            //   拿 ic_qs_bg_enabled.xml（vector，fillColor=@color/qs_tile_icon_bg_enabled_color），
            //   颜色在 inflate 时按 uiMode 配置解析，不走 getColor → 上面 5 个 hook 全拦不到。
            //   修复：拦截 getDrawable，命中蓝底 drawable 时 mutate + setTint(亮蓝)，
            //   让深浅色下磁贴启用蓝统一为官方浅色值 0xff0099ff。
            //   （disabled 底 0x7f08112b 是灰玻璃底，主人要保留官方暗色观感，不动。）
                        try {
                m = resCls.getMethod("getDrawable", Integer.TYPE);
                module.hook((Executable)m).setId("cc_l22_getdrawable_tilebg").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    try {
                        Object a0 = chain.getArgs().get(0);
                        if (a0 instanceof Integer) {
                            int id = (Integer)a0;
                            // ★ 调试探针：记录所有 getDrawable id（去重，每个 id 只记一次）
                            if (COLOR_SEEN.add("gd:" + id)) {
                                L.log(4, TAG, "L22 getDrawable id=" + id + " hex=0x" + Integer.toHexString(id));
                            }
                            if (id == 0x7f08112c) {
                                Object r = chain.proceed();
                                if (r instanceof Drawable) {
                                    ((Drawable)r).mutate().setTint(-16737793); // 0xff0099ff
                                    L.log(4, TAG, "L22 GD-I hit ret=0x" + Integer.toHexString(System.identityHashCode(r)) + " tinted");
                                }
                                return r;
                            }
                        }
                    }
                    catch (Throwable t22) {
                        L.log(5, TAG, "L22 getDrawable tint failed: " + t22);
                    }
                    return chain.proceed();
                });
                ++hooked;
            }
            catch (Throwable throwable2) {
                // empty catch block
            }
            // ★ v1.0.3b：getDrawable(int, Theme) 重载同样处理
            try {
                m = resCls.getMethod("getDrawable", Integer.TYPE, Resources.Theme.class);
                module.hook((Executable)m).setId("cc_l22_getdrawable_theme").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    try {
                        Object a0 = chain.getArgs().get(0);
                        if (a0 instanceof Integer) {
                            int id = (Integer)a0;
                            if (COLOR_SEEN.add("gdt:" + id)) {
                                L.log(4, TAG, "L22 getDrawable(theme) id=" + id + " hex=0x" + Integer.toHexString(id));
                            }
                            if (id == 0x7f08112c) {
                                Object r = chain.proceed();
                                if (r instanceof Drawable) {
                                    ((Drawable)r).mutate().setTint(-16737793);
                                    L.log(4, TAG, "L22 GD-T hit ret=0x" + Integer.toHexString(System.identityHashCode(r)) + " tinted");
                                }
                                return r;
                            }
                        }
                    }
                    catch (Throwable t22) {
                        L.log(5, TAG, "L22 getDrawable theme failed: " + t22);
                    }
                    return chain.proceed();
                });
                ++hooked;
            }
            catch (Throwable throwable3) {
                // empty catch block
            }
            // ★ v1.0.3o 纯色标记实验已撤销（8050-8122）：View.draw hook 每帧给 icon 上品红/绿，
            //   屏幕无反应（icon drawable 不是屏幕磁贴的绘制源），纯浪费性能 + 污染日志。
            // ★ v1.0.3c 调试探针：hook MiuiQSIconViewImpl.updateIcon，
            //   记录每个磁贴图标的 state/activeBgColor/最终 drawable+tint，
            //   定位「wifi 图标亮白 vs 移动数据图标暗色」的差异来源。
            // ★ v1.0.3r 写入口埋伏：绑定后 drawable 被换掉 → hook ImageView 三个写入 API，
            //   父链有 QS/Tile 视图时打全调用栈，抓「谁在绑定后覆盖颜色」。
            try {
                Class<?> ivCls = cl.loadClass("android.widget.ImageView");
                String[] writeApis = {"setImageDrawable", "setImageBitmap", "setImageTintList", "setBackground", "setBackgroundDrawable"};
                for (String api : writeApis) {
                    java.util.List<java.lang.reflect.Method> ms = new java.util.ArrayList<>();
                    for (java.lang.reflect.Method wm : ivCls.getDeclaredMethods()) {
                        if (api.equals(wm.getName()) && wm.getParameterTypes().length == 1) ms.add(wm);
                    }
                    for (java.lang.reflect.Method wm : ms) {
                        final String apiName = api;
                        module.hook((Executable)wm).setId("cc_l22_iv_write_" + apiName).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                            try {
                                Object self = chain.getThisObject();
                                // 父链扫描：找 QS 相关视图
                                android.view.View v = (self instanceof android.view.View) ? (android.view.View) self : null;
                                boolean qsRelated = false;
                                int depth = 0;
                                StringBuilder path = new StringBuilder();
                                Object o = self;
                                while (o instanceof android.view.View && depth < 12) {
                                    String cn = o.getClass().getName();
                                    if (depth < 6) path.insert(0, cn.substring(cn.lastIndexOf('.') + 1) + "<");
                                    if (cn.contains("qs") || cn.contains("QS") || cn.contains("controlcenter") || cn.contains("Tile")) { qsRelated = true; break; }
                                    if (!(o instanceof android.view.View)) break;
                                    o = ((android.view.View) o).getParent();
                                    ++depth;
                                }
                                if (qsRelated) {
                                    Object arg0 = chain.getArgs().isEmpty() ? null : chain.getArgs().get(0);
                                    // ★ v41 决定性修复（smali 铁证）：启用态磁贴的蓝圆底根本不是 background！
                                    //   MiuiQSIconViewImpl.updateIcon 在 state==2 时构造 3 层 LayerDrawable：
                                    //   [0]=ic_qs_bg_disabled(0x7f08112b) [1]=ic_qs_bg_enabled(0x7f08112c).mutate() [2]=icon
                                    //   然后 setImageDrawable 整体塞进 icon ImageView。
                                    //   0x7f08112c 是 VectorDrawable，fillColor=@color/qs_tile_icon_bg_enabled_color
                                    //   —— 深浅色差异的真正来源（smali 全库 0 引用是误判：引用在 drawable XML 里）。
                                    //   修复：命中 3 层 LayerDrawable 时直接 setTint 第 1 层为 0xFF0099FF。
                                    //   图标层([2])在 smali 里已单独 setTint(mIconColorEnabled)，不受影响。
                                    if ("setImageDrawable".equals(apiName) && arg0 instanceof android.graphics.drawable.LayerDrawable) {
                                        android.graphics.drawable.LayerDrawable ld2 = (android.graphics.drawable.LayerDrawable) arg0;
                                        int nl = ld2.getNumberOfLayers();
                                        // ★ v42：两条路径全覆盖。
                                        //   3 层路径（可见+动画+状态变化）：[0]=ic_qs_bg_disabled [1]=ic_qs_bg_enabled(蓝底) [2]=icon
                                        //   2 层路径（离屏预绑定，屏幕实际显示的！cond_18）：state==2 时 [0]=ic_qs_bg_enabled [1]=icon
                                        //   用采样判蓝（disabled 是灰玻璃，不会误伤），蓝底层 setTint(0xFF0099FF)。
                                        if (nl == 3) {
                                            try {
                                                ld2.getDrawable(1).setTint(0xFF0099FF);
                                                L.log(4, TAG, "L22 LAYERFIX 3层 第1层 tint -> 0xFF0099FF");
                                            } catch (Throwable tl1) { L.log(5, TAG, "L22 LAYERFIX err " + tl1); }
                                        } else if (nl == 2) {
                                            try {
                                                android.graphics.drawable.Drawable bgL = ld2.getDrawable(0);
                                                int sp = 0;
                                                android.graphics.Bitmap bb6 = android.graphics.Bitmap.createBitmap(8, 8, android.graphics.Bitmap.Config.ARGB_8888);
                                                android.graphics.Canvas cc6 = new android.graphics.Canvas(bb6);
                                                android.graphics.Rect ob6 = bgL.copyBounds();
                                                bgL.setBounds(0, 0, 8, 8);
                                                bgL.draw(cc6);
                                                bgL.setBounds(ob6);
                                                sp = bb6.getPixel(4, 4);
                                                bb6.recycle();
                                                int rr = (sp >> 16) & 0xFF, gg = (sp >> 8) & 0xFF, bbv = sp & 0xFF;
                                                boolean blue = ((sp >>> 24) > 0x80) && (bbv > 0x60) && (bbv > rr + 0x20) && (bbv > gg + 0x20);
                                                if (blue) {
                                                    bgL.setTint(0xFF0099FF);
                                                    L.log(4, TAG, "L22 LAYERFIX 2层 第0层 sampled=0x" + Integer.toHexString(sp) + " tint -> 0xFF0099FF");
                                                }
                                            } catch (Throwable tl2) { L.log(5, TAG, "L22 LAYERFIX2 err " + tl2); }
                                        }
                                    }
                                    String dk = arg0 == null ? "null" : String.valueOf(System.identityHashCode(arg0));
                                    if (COLOR_SEEN.add("ivw:" + System.identityHashCode(self) + ":" + apiName + ":" + dk)) {
                                        java.io.StringWriter sw = new java.io.StringWriter();
                                        new Throwable("who").printStackTrace(new java.io.PrintWriter(sw));
                                        String[] lines = sw.toString().split("\n");
                                        StringBuilder st = new StringBuilder();
                                        // 跳过框架/合成层，取官方调用帧
                                        int taken = 0;
                                        for (int li = 1; li < lines.length && taken < 10; ++li) {
                                            String ln = lines[li].trim();
                                            if (ln.contains("CcHooks") || ln.contains("D8$$") || ln.contains("r8-map") || ln.contains("LSPosed")) continue;
                                            st.append(ln).append(" | ");
                                            ++taken;
                                        }
                                        L.log(4, TAG, "L22 IVWRITE [" + apiName + " d=" + dk + "] view=" + path + " stack=" + st);
                                    }
                                }
                            } catch (Throwable t30) { /* 静默 */ }
                            return chain.proceed();
                        });
                        ++hooked;
                    }
                }
                L.log(4, TAG, "L22 IVWRITE hooks installed (" + hooked + " total)");
                // ★ v43 大杀器：hook ResourcesImpl.loadDrawable —— 所有 getDrawable 的必经之路！
                //   Resources.getDrawable 会被 MIUI 的 MiuiResources 等子类覆写绕过 hook（GD-T 有触发
                //   但 updateIcon 的调用没被拦到的原因），但无论子类怎么覆写，最终都调
                //   ResourcesImpl.loadDrawable(TypedValue, id, theme, ...)。
                //   命中 ic_qs_bg_enabled(0x7f08112c) → mutate+setTint(0xFF0099FF)。
                //   该 drawable 在 smali 全库唯一引用者就是 updateIcon 的蓝底层，安全。
                try {
                    Class<?> implCls = cl.loadClass("android.content.res.ResourcesImpl");
                    for (java.lang.reflect.Method lm : implCls.getDeclaredMethods()) {
                        if (!"loadDrawable".equals(lm.getName())) continue;
                        boolean hasTypedValue = false;
                        for (Class<?> pt : lm.getParameterTypes()) {
                            if (pt == android.util.TypedValue.class) { hasTypedValue = true; break; }
                        }
                        if (!hasTypedValue) continue;
                        module.hook((Executable)lm).setId("cc_l43_implloaddrawable").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                            try {
                                java.util.List<Object> as43 = chain.getArgs();
                                boolean hasTv43 = false, isTarget43 = false;
                                for (Object ao : as43) {
                                    if (ao instanceof android.util.TypedValue) hasTv43 = true;
                                    if (ao instanceof Integer && (Integer) ao == 0x7f08112c) isTarget43 = true;
                                }
                                if (hasTv43 && isTarget43) {
                                    // ★ v58 方案B：不再 tint 官方矢量（v53 实证 setTint 对 fill=暗色的
                                    //   VectorDrawable 无效），直接返回自产亮蓝圆——官方 night 矢量
                                    //   根本不出场。该实例会被 ResourcesImpl 缓存，日夜切换重解析时
                                    //   再次命中本 hook，永远拿到的都是亮蓝。
                                    android.graphics.drawable.GradientDrawable ngd58 = new android.graphics.drawable.GradientDrawable();
                                    ngd58.setShape(android.graphics.drawable.GradientDrawable.OVAL);
                                    ngd58.setColor(-16737793); // 0xFF0099FF
                                    if (COLOR_SEEN.add("implt58")) {
                                        L.log(4, TAG, "L22 IMPL-T58 0x7f08112c -> RETURN own bright OVAL (bypass official vector)");
                                    }
                                    return ngd58;
                                }
                            } catch (Throwable t43) { L.log(5, TAG, "L22 IMPL-T err " + t43); }
                            return chain.proceed();
                        });
                        ++hooked;
                    }
                } catch (Throwable t42b) { L.log(5, TAG, "L22 ResourcesImpl hook failed: " + t42b); }
                // ★ v63 按主人指示撤销全部文字/图标 hook（v59 TILETEXCOLOR / v60-62 TILESPANTEXT）：
                //   官方在各自模式下对文字的着色本来就是统一的，我们的修复只覆盖部分实例
                //   反而制造了不统一。恢复官方原样，深浅色各自范围内自然一致。
                //   （蓝底 v58 自产实例方案与 DSFIX 哨兵保留——已确认成功。）
                // ★ v64 主人纠偏：v63 撤过头了——updateResources 钉白是「浅色玻璃下文字白色」
                //   的既有自定义功能（淡灰玻璃上需要白字+压暗背景才可读），恢复它。
                //   图标深色两种颜色与文字无关，单独排查。
                try {
                    Class<?> mtv64 = cl.loadClass("com.android.systemui.qs.tileimpl.MiuiQSTileView");
                    final java.lang.reflect.Field ffLabel64 = mtv64.getField("mLabel");
                    final java.lang.reflect.Field ffSecond64 = mtv64.getField("mSecondLine");
                    final int LABEL_COLOR64 = 0xE6FFFFFF;  // 白 90%（既有功能值）
                    final int SECOND_COLOR64 = 0x4DFFFFFF; // 白 30%（既有功能值）
                    for (java.lang.reflect.Method um64 : mtv64.getDeclaredMethods()) {
                        if (!"updateResources".equals(um64.getName())) continue;
                        module.hook((Executable) um64).setId("cc_l64_tiletexcolor").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                            Object self64 = chain.getThisObject();
                            Object rr64 = null;
                            try { rr64 = chain.proceed(); } catch (Throwable tp64) { throw tp64; }
                            try {
                                Object lb64 = ffLabel64.get(self64);
                                if (lb64 instanceof TextView) ((TextView) lb64).setTextColor(LABEL_COLOR64);
                                Object sc64 = ffSecond64.get(self64);
                                if (sc64 instanceof TextView) ((TextView) sc64).setTextColor(SECOND_COLOR64);
                            } catch (Throwable tf64a) { /* 静默 */ }
                            return rr64;
                        });
                        L.log(4, TAG, "L64 TILETEXCOLOR(restored) hook installed on updateResources");
                        break;
                    }
                } catch (Throwable t64) { L.log(5, TAG, "L64 TILETEXCOLOR install failed: " + t64); }
                // ★ v66 二级菜单玻璃：磁贴展开的详情面板 = MiuiQSDetail（qs_detail.xml，
                //   根背景 qs_detail_background、容器 qs_detail_bg、内容区实心色）。
                //   hook View.setVisibility 过滤 MiuiQSDetail：可见时清实心背景 +
                //   applyUnifiedGlass（与托盘同款，日夜 blend 由模式决定）。
                try {
                    Class<?> qsd66 = cl.loadClass("com.android.systemui.qs.MiuiQSDetail");
                    java.lang.reflect.Method svm66 = View.class.getMethod("setVisibility", Integer.TYPE);
                    module.hook((Executable) svm66).setId("cc_l66_qsdetail").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                        try {
                            Object self66 = chain.getThisObject();
                            // ★ v69：去重门撤掉——浅色下官方会重设实心背景，每次 VISIBLE
                            //   都重新清背景+重挂玻璃（applyUnifiedGlass 用 silent=true）。
                            if (self66 instanceof View && qsd66.isInstance(self66) && chain.getArgs().size() == 1
                                    && Integer.valueOf(View.VISIBLE).equals(chain.getArgs().get(0))) {
                                final View dv66 = (View) self66;
                                dv66.post(new Runnable() {
                                        public void run() {
                                            try {
                                                // ① 清根节点和容器、内容区的实心背景
                                                CcHooks.transparentPanelBg(dv66);
                                                View inner66 = dv66.findViewById(dv66.getResources().getIdentifier("qs_detail_container", "id", dv66.getContext().getPackageName()));
                                                // ★ v68：玻璃套在内层容器（真正的菜单盒子，自带圆角），
                                                //   根节点只是全屏透明占位——v66/67 套根节点导致玻璃全屏。
                                                View glassTarget66 = (inner66 != null) ? inner66 : dv66;
                                                if (glassTarget66 != dv66) {
                                                    CcHooks.transparentPanelBg(glassTarget66);
                                                    // ★ v69：圆角对齐通知卡片（托盘已对齐 notification_item_bg_radius=24dp，
                                                    //   v68 用的 stack_scroller=12dp 观感不齐）
                                                    try {
                                                        float rad69 = glassTarget66.getResources().getDimension(
                                                                glassTarget66.getResources().getIdentifier("notification_item_bg_radius", "dimen", glassTarget66.getContext().getPackageName()));
                                                        final float fr69 = rad69;
                                                        glassTarget66.setOutlineProvider(new android.view.ViewOutlineProvider() {
                                                            public void getOutline(View vv, android.graphics.Outline o) {
                                                                o.setRoundRect(0, 0, vv.getWidth(), vv.getHeight(), fr69);
                                                            }
                                                        });
                                                        glassTarget66.setClipToOutline(true);
                                                        glassTarget66.invalidateOutline();
                                                    } catch (Throwable tr68) { /* 静默，用默认 outline */ }
                                                }
                                                View content66 = dv66.findViewById(android.view.View.generateViewId()); // 占位，无副作用
                                                // 内容区按 id 查（android:id/content）
                                                View cf66 = null;
                                                try {
                                                    cf66 = dv66.findViewById(16908290); // android.R.id.content
                                                } catch (Throwable ignored) {}
                                                if (cf66 != null) CcHooks.transparentPanelBg(cf66);
                                                // ② 玻璃：与托盘同款链路，目标是内层容器
                                                // ★ v71：磨砂→反射玻璃（主人指示参考 qs 托盘/通知卡片）：
                                                //   applyUnifiedGlass 是磨砂链路（blurMode=1+passWindowBlur），
                                                //   反射玻璃走 applyReflectiveGlass（同托盘），内部有日夜门控。
                                                float radRg71 = 24f * glassTarget66.getResources().getDisplayMetrics().density;
                                                try {
                                                    radRg71 = glassTarget66.getResources().getDimension(
                                                            glassTarget66.getResources().getIdentifier("notification_item_bg_radius", "dimen", glassTarget66.getContext().getPackageName()));
                                                } catch (Throwable ignored) {}
                                                CcHooks.rgReserve(glassTarget66);
                                                boolean ok66 = CcHooks.applyReflectiveGlass(module, glassTarget66, cl, "L66 detailRG", radRg71);
                                                L.log(4, TAG, "L66 detailRG applied=" + ok66 + " radius=" + ((int) radRg71) + " target=" + glassTarget66.getClass().getSimpleName() + " size=" + glassTarget66.getWidth() + "x" + glassTarget66.getHeight());
                                                // ★ v70：白字——与上层（v64 托盘钉白）同款：玻璃下黑字不可读，
                                                //   递归遍历 detail 树把 TextView 钉白 90%；延迟 400ms 再走一遍，
                                                //   兜住 wifi 列表等异步填充的内容。
                                                try {
                                                    final View walk70 = glassTarget66;
                                                    java.util.ArrayDeque<View> q70 = new java.util.ArrayDeque<>();
                                                    q70.push(walk70);
                                                    int n70 = 0;
                                                    while (!q70.isEmpty()) {
                                                        View cv = q70.pop();
                                                        if (cv instanceof android.widget.TextView) { ((android.widget.TextView) cv).setTextColor(0xE6FFFFFF); ++n70; }
                                                        if (cv instanceof android.view.ViewGroup) {
                                                            android.view.ViewGroup g70 = (android.view.ViewGroup) cv;
                                                            for (int i70 = 0; i70 < g70.getChildCount(); ++i70) q70.push(g70.getChildAt(i70));
                                                        }
                                                    }
                                                    walk70.postDelayed(new Runnable() { public void run() {
                                                        try {
                                                            java.util.ArrayDeque<View> q70b = new java.util.ArrayDeque<>();
                                                            q70b.push(walk70);
                                                            while (!q70b.isEmpty()) {
                                                                View cv = q70b.pop();
                                                                if (cv instanceof android.widget.TextView) ((android.widget.TextView) cv).setTextColor(0xE6FFFFFF);
                                                                if (cv instanceof android.view.ViewGroup) {
                                                                    android.view.ViewGroup g70b = (android.view.ViewGroup) cv;
                                                                    for (int i70 = 0; i70 < g70b.getChildCount(); ++i70) q70b.push(g70b.getChildAt(i70));
                                                                }
                                                            }
                                                        } catch (Throwable ignored) {}
                                                    }}, 400);
                                                    L.log(4, TAG, "L66 detailWhite pinned=" + n70);
                                                } catch (Throwable tw70) { L.log(5, TAG, "L66 detailWhite err " + tw70); }
                                            } catch (Throwable tf66b) {
                                                L.log(5, TAG, "L66 detailGlass err " + tf66b);
                                            }
                                        }
                                    });
                            }
                        } catch (Throwable tf66) { /* 静默 */ }
                        return chain.proceed();
                    });
L.log(4, TAG, "L66 QSDETAIL glass hook installed");
                  } catch (Throwable t66) { L.log(5, TAG, "L66 QSDETAIL install failed: " + t66); }
                  // ★ v133：磁贴托盘「编辑页」玻璃背景（MiuiQSCustomizer）。
                  //
                  //  【主人需求】「接下来给磁贴托盘的编辑页加玻璃背景，
                  //    方法参考之前给 wifi 与蓝牙加的过程就行」
                  //
                  //  【布局结构（smali 实证）】
                  //    MiuiQSCustomizer extends LinearLayout
                  //      onFinishInflate() → inflate(miui_qs_customize_panel_content, this)
                  //        └ customize_container (AlphaOptimizedLinearLayout, id=0x7f0b032d)
                  //            background = @drawable/qs_customizer_background（transition，无实色）
                  //            └ content (LinearLayout, id=0x7f0b02c6)
                  //                background = @drawable/qs_customizer_bg
                  //                             = shape + solid qs_panel_background_color
                  //                             + corners notification_stack_scroller_bg_radius
                  //                └ RecyclerView(@android:id/list) 磁贴网格
                  //                └ qs_divider + qs_detail_buttons（更多设置/完成）
                  //            └ nav_bar_background (View)
                  //
                  //  【为什么套 content 而不是 customize_container】
                  //    content 自带圆角 shape（notification_stack_scroller_bg_radius）
                  //    和官方 outlineProvider（MiuiQSCustomizer$outlineProvider$1
                  //    用 customizeContentRadius = 0x7f07157f 同一个 dimen）。
                  //    套内层 = 玻璃形状天然对齐官方圆角。
                  //
                  //  【挂载时机】show(II) 在 **MiuiQSCustomizerController** 上，不在 Customizer 上：
                  //      if (isShown) return;
                  //      isShown = true; isOpening = true;
                  //      setVisibility(VISIBLE);            ← 我们拦这个
                  //      clipper.animateCircularClip(...)   ← 圆形展开动画
                  //    → 与 L66 同款：hook View.setVisibility，过滤 MiuiQSCustomizer + VISIBLE。
                  //      动画期间宽高为 0，必须 post + 重试。
                  try {
                      Class<?> cust133 = cl.loadClass("com.android.systemui.qs.customize.MiuiQSCustomizer");
                      java.lang.reflect.Method svm133 = View.class.getMethod("setVisibility", Integer.TYPE);
                      module.hook((Executable) svm133).setId("cc_l133_customizer_glass")
                              .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                              .intercept(chain -> {
                                  try {
                                      Object self133 = chain.getThisObject();
                                      if (self133 instanceof View && cust133.isInstance(self133)
                                              && chain.getArgs().size() == 1
                                              && Integer.valueOf(View.VISIBLE).equals(chain.getArgs().get(0))) {
                                          final View cv133 = (View) self133;
                                          final int[] tries133 = new int[]{0};
                                          final Runnable[] rr133 = new Runnable[1];
                                          rr133[0] = new Runnable() { public void run() {
                                              try {
                                                  // ① 找内容容器（id=content）
                                                  View content133 = cv133.findViewById(
                                                          cv133.getResources().getIdentifier("content", "id",
                                                                  cv133.getContext().getPackageName()));
                                                  if (content133 == null) content133 = cv133;
                                                  // ② 圆形展开动画期间宽高为 0 → 重试
                                                  if ((content133.getWidth() <= 0 || content133.getHeight() <= 0)
                                                          && ++tries133[0] <= 8) {
                                                      cv133.postDelayed(rr133[0], 80);
                                                      return;
                                                  }
                                                  // ③ 不再做「白天跳过」门控。
                                                  //
                                                  //  【v134 踩的坑】上一版这里写了
                                                  //      if (!nightNow) { skip; return; }
                                                  //    结果白天打开编辑页就是纯白背景（官方实色）。
                                                  //  【为什么错】对比 wifi/蓝牙详情页（L66）：
                                                  //      L66 直接调 applyReflectiveGlass，**没有任何日夜门控**。
                                                  //      日夜差异由 applyReflectiveGlass 内部的 blend 颜色处理：
                                                  //        c1 = getColor(2131101050)  ← 白天/夜间资源自动不同
                                                  //        c2 = getColor(2131101051)
                                                  //        c3 = getColor(2131101052)
                                                  //      → 白天是一套浅色 blend，夜间是另一套，玻璃都能挂。
                                                  //    所以这里**不做任何门控**，把日夜判断完全交给
                                                  //    applyReflectiveGlass 自己（与 L66 保持一致）。
                                                  // ④ 清实色背景（container 的 transition + content 的 solid）
                                                  CcHooks.transparentPanelBg(cv133);
                                                  if (content133 != cv133) CcHooks.transparentPanelBg(content133);
                                                  // ⑤ 圆角对齐托盘（notification_item_bg_radius，与 L66/L69 一致）
                                                  float rad133 = 24f * content133.getResources().getDisplayMetrics().density;
                                                  try {
                                                      rad133 = content133.getResources().getDimension(
                                                              content133.getResources().getIdentifier("notification_item_bg_radius",
                                                                      "dimen", content133.getContext().getPackageName()));
                                                  } catch (Throwable ignored) {}
                                                  final float fr133 = rad133;
                                                  content133.setOutlineProvider(new android.view.ViewOutlineProvider() {
                                                      public void getOutline(View vv, android.graphics.Outline o) {
                                                          o.setRoundRect(0, 0, vv.getWidth(), vv.getHeight(), fr133);
                                                      }
                                                  });
                                                  content133.setClipToOutline(true);
                                                  content133.invalidateOutline();
                                                  // ⑥ 挂玻璃（与 wifi/蓝牙详情页同款 applyReflectiveGlass）
                                                  CcHooks.rgReserve(content133);
                                                  boolean ok133 = CcHooks.applyReflectiveGlass(module, content133, cl,
                                                          "L133 customizerRG", fr133);
                                                  L.log(4, TAG, "L133 customizerRG applied=" + ok133
                                                          + " tries=" + tries133[0]
                                                          + " radius=" + ((int) fr133)
                                                          + " size=" + content133.getWidth() + "x" + content133.getHeight());
                                                  // ⑦ 白字——玻璃下黑字不可读，与 L66/L70 同款递归钉白
                                                  final View walk133 = cv133;
                                                  java.util.ArrayDeque<View> q133 = new java.util.ArrayDeque<>();
                                                  q133.push(walk133);
                                                  int n133 = 0;
                                                  while (!q133.isEmpty()) {
                                                      View c133 = q133.pop();
                                                      if (c133 instanceof android.widget.TextView) {
                                                          ((android.widget.TextView) c133).setTextColor(0xE6FFFFFF);
                                                          ++n133;
                                                      }
                                                      if (c133 instanceof android.view.ViewGroup) {
                                                          android.view.ViewGroup g133 = (android.view.ViewGroup) c133;
                                                          for (int i133 = 0; i133 < g133.getChildCount(); ++i133)
                                                              q133.push(g133.getChildAt(i133));
                                                      }
                                                  }
                                                  L.log(4, TAG, "L133 customizerWhite pinned=" + n133);
                                              } catch (Throwable tf133) {
                                                  L.log(5, TAG, "L133 customizerGlass err " + tf133);
                                              }
                                          }};
                                          cv133.post(rr133[0]);
                                      }
                                  } catch (Throwable tf133b) { /* 静默 */ }
                                  return chain.proceed();
                              });
                      L.log(4, TAG, "L133 CUSTOMIZER glass hook installed");
                  } catch (Throwable t133) { L.log(5, TAG, "L133 CUSTOMIZER install failed: " + t133); }
                  // ★ v136：编辑页「下半区纯色遮罩」——找到并消除。
                  //
                  //  【主人反馈】「编辑页有一部分有第二层纯色遮罩需要找出来消除，
                  //    这个遮罩在浅色模式是半透明，深色模式是实色。」
                  //    截图：上半区（已添加磁贴）透出玻璃，下半区（可用磁贴）被一块
                  //    纯色块盖住，分界线正好在「已添加 / 可用」之间。
                  //
                  //  【根因（smali 实证）】
                  //    它不是某个子 View 的 background，而是 **RecyclerView 的 ItemDecoration**
                  //    直接往 Canvas 上画的：
                  //
                  //      MiuiTileAdapter$TileItemDecoration.onDraw(Canvas, RecyclerView):
                  //        // ① 找到 mEditIndex 对应的那一行（分隔线位置）
                  //        for each child:
                  //            if (getBindingAdapterPosition() == mEditIndex) break;
                  //        // ② 从那一行的 top 画到 RecyclerView 的 bottom
                  //        mDrawable.setBounds(0, childTop + translationY, rvWidth, rvBottom);
                  //        mDrawable.draw(canvas);
                  //
                  //    mDrawable 来自：
                  //      const v1, 0x7f0818d2
                  //      → @drawable/qs_customize_tile_decoration
                  //        = <color android:color="@color/qs_customize_content_background_color" />
                  //      → values/colors.xml       #14000000  ← 浅色：半透明黑（alpha=0x14）
                  //      → values-night/colors.xml #171719    ← 深色：实色
                  //    **完美对应主人描述的「浅色半透明、深色实色」**。
                  //
                  //  【为什么 transparentPanelBg 清不掉】
                  //    ItemDecoration 不参与 View 树，它是 RecyclerView 的绘制回调，
                  //    没有 background 属性可清。只能 hook onDraw 本身。
                  //
                  //  【修法】直接跳过 onDraw —— 这个方法的唯一职责就是画这块遮罩，
                  //    跳过它不会有任何副作用（分隔线另有 qs_customize_tile_divider 负责）。
                  try {
                      Class<?> dec136 = cl.loadClass("com.android.systemui.qs.customize.MiuiTileAdapter$TileItemDecoration");
                      java.lang.reflect.Method od136 = dec136.getDeclaredMethod("onDraw",
                              android.graphics.Canvas.class,
                              Class.forName("androidx.recyclerview.widget.RecyclerView", false, cl));
                      final java.util.concurrent.atomic.AtomicInteger cnt136 = new java.util.concurrent.atomic.AtomicInteger();
                      module.hook((Executable) od136).setId("cc_l136_customizer_mask")
                              .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                              .intercept(chain -> {
                                  // 不调 chain.proceed() = 不画这块遮罩
                                  int n136 = cnt136.incrementAndGet();
                                  if (n136 <= 3 || n136 % 20 == 0)
                                      L.log(4, TAG, "L136 customizer mask skipped #" + n136);
                                  return null;
                              });
                      L.log(4, TAG, "L136 CUSTOMIZER mask hook installed");
                  } catch (Throwable t136) { L.log(5, TAG, "L136 CUSTOMIZER mask install failed: " + t136); }
                 // ★ v72：v71 只 hook 了 bridge 重载无效——按方法名全量 hook 所有
                 //   onBindViewHolder 重载（bridge/具体/3参 payload 版），并加触发日志验证。
                 try {
                     Class<?> ad71 = cl.loadClass("com.android.systemui.qs.QSDetailContent$Adapter");
                     Class<?> vh71 = cl.loadClass("androidx.recyclerview.widget.RecyclerView$ViewHolder");
                     final java.util.concurrent.atomic.AtomicInteger cnt71 = new java.util.concurrent.atomic.AtomicInteger();
                     final java.lang.reflect.Field fIt71 = vh71.getField("itemView");
                     int hooked71 = 0;
                     for (java.lang.reflect.Method m71 : ad71.getDeclaredMethods()) {
                         if (!"onBindViewHolder".equals(m71.getName())) continue;
                         final boolean isBridge71 = m71.isBridge();
                         module.hook((Executable) m71).setId("cc_l71_detailbind_" + m71.toString().hashCode()).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                             Object r71 = chain.proceed();
                             try {
                                 Object vh = chain.getArgs().get(0);
                                 if (vh != null) {
                                     Object iv71 = fIt71.get(vh);
                                     if (iv71 instanceof View) {
                                         java.util.ArrayDeque<View> q71 = new java.util.ArrayDeque<>();
                                         q71.push((View) iv71);
                                         while (!q71.isEmpty()) {
                                             View cv71 = q71.pop();
                                             if (cv71 instanceof android.widget.TextView) ((android.widget.TextView) cv71).setTextColor(0xE6FFFFFF);
                                             if (cv71 instanceof android.widget.ImageView) {
                                                 ((android.widget.ImageView) cv71).setImageTintList(android.content.res.ColorStateList.valueOf(0xFFFFFFFF));
                                             }
                                             if (cv71 instanceof android.view.ViewGroup) {
                                                 android.view.ViewGroup g71 = (android.view.ViewGroup) cv71;
                                                 for (int i71 = 0; i71 < g71.getChildCount(); ++i71) q71.push(g71.getChildAt(i71));
                                             }
                                         }
                                         int n = cnt71.incrementAndGet();
                                         if (n <= 5 || n % 50 == 0) L.log(4, TAG, "L71 bind fired#" + n + " bridge=" + isBridge71);
                                     }
                                 }
                             } catch (Throwable tb71) { /* 静默 */ }
                             return r71;
                         });
                         ++hooked71;
                     }
                     L.log(4, TAG, "L71 detailBind white hook installed overloads=" + hooked71);
                 } catch (Throwable t71) { L.log(5, TAG, "L71 detailBind install failed: " + t71); }
                 // ★ v73：条目背景分级——主人指示：已连接 wifi（selected=true）背景改纯色，
                 //   已保存 wifi（selectable 未选中）背景改磨砂玻璃。
                 //   染色节点：Adapter.updateSelectableItemBackground(int position, View)，
                 //   官方按 Item.selected 分状态设背景，我们在其后覆盖。
                 try {
                     Class<?> ad73 = cl.loadClass("com.android.systemui.qs.QSDetailContent$Adapter");
                     java.lang.reflect.Method usb73 = ad73.getDeclaredMethod("updateSelectableItemBackground", Integer.TYPE, View.class);
                     final java.lang.reflect.Field fOuter73 = ad73.getDeclaredField("this$0");
                     fOuter73.setAccessible(true);
                     final Class<?> content73 = cl.loadClass("com.android.systemui.qs.QSDetailContent");
                     final java.lang.reflect.Method accItems73 = content73.getDeclaredMethod("access$getItems$p", content73);
                     accItems73.setAccessible(true);
                     final Class<?> sel73 = cl.loadClass("com.android.systemui.qs.QSDetailContent$SelectableItem");
                     final java.lang.reflect.Field fSel73 = sel73.getField("selected");
                     module.hook((Executable) usb73).setId("cc_l73_itembg").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                         Object r73 = chain.proceed();
                         try {
                             Object adInst = chain.getThisObject();
                             int pos73 = (Integer) chain.getArgs().get(0);
                             View row73 = (View) chain.getArgs().get(1);
                             // 读 items[pos].selected
                             boolean isSel73 = false;
                             Object contentInst = fOuter73.get(adInst);
                             Object[] items73 = (Object[]) accItems73.invoke(null, contentInst);
                             if (items73 != null && pos73 >= 0 && pos73 < items73.length && sel73.isInstance(items73[pos73])) {
                                 isSel73 = fSel73.getBoolean(items73[pos73]);
                             }
                             View bg73 = row73; // ★ v74：官方药丸在条目根节点上（0x7f0b098e 是文字容器，
                             //   挂它上面会盖住文字、形状也不对）
if (isSel73) {
                                  // ★ v75：纯色——官方选中色资源 qs_control_expand_tile_detail_selected_color_plugin
                                  //   本身带 alpha（深色 #59155bc2），取色强制 alpha=FF；圆角同官方 18dp
                                  try {
                                      int cid75 = bg73.getResources().getIdentifier("qs_control_expand_tile_detail_selected_color_plugin", "color", bg73.getContext().getPackageName());
                                      int pill75 = bg73.getResources().getColor(cid75, bg73.getContext().getTheme());
                                      pill75 = 0xFF000000 | (pill75 & 0x00FFFFFF);
                                      android.graphics.drawable.GradientDrawable ng75 = new android.graphics.drawable.GradientDrawable();
                                      ng75.setCornerRadius(18f * bg73.getResources().getDisplayMetrics().density); // 官方圆角
                                      ng75.setColor(pill75);
                                      bg73.setBackground(ng75);
                                      // ★ v133：已连接项（蓝色胶囊）——官方行为，**不要玻璃**。
                                      //   【主人反馈】「最顶端有蓝色半透明背景的这个胶囊把玻璃背景删除，
                                      //     因为 wifi 页面是异步加载的而蓝色胶囊常驻，
                                      //     会导致玻璃延迟盖上去，显得不统一」
                                      //   【根因】v76 的玻璃是 post 到布局完成后再挂的，
                                      //     而 wifi 列表也是异步填充；当同一行从「未选中」变「已选中」时，
                                      //     先前的 post 玻璃仍然会盖到新的蓝色胶囊上 → 观感不统一。
                                      //   这里在设纯色的同时主动清掉玻璃，双保险。
                                      CcHooks.clearOurGlass(bg73, cl, "L133 selPillNoGlass");
                                      if (DETAIL_BG_LOG73.get() < 8)
                                          L.log(4, TAG, "L75 selPill pos=" + pos73 + " color=" + Integer.toHexString(pill75));
                                  } catch (Throwable tx75) { if (DETAIL_BG_LOG73.get() < 5) L.log(5, TAG, "L75 selPill err " + tx75); }
                              } else {
                                  // 已保存/附近：磨砂玻璃——挂条目根节点（整行药丸）
                                  // ★ v76：列表项玻璃不一致的根因：onBind 时部分行尚未完成布局
                                  //   （宽高=0），applyUnifiedGlass 开头 w<=0 直接 return false
                                  //   → 那几行永远没玻璃。改为 post 到布局后再挂，未布局则重试。
                                  final View fb76 = bg73;
                                  final int[] tries76 = new int[]{0};
                                  final Runnable[] rr76 = new Runnable[1];
                                  rr76[0] = new Runnable() { public void run() {
                                      try {
                                          if ((fb76.getWidth() <= 0 || fb76.getHeight() <= 0) && ++tries76[0] <= 4) {
                                              fb76.postDelayed(rr76[0], 120);
                                              return;
                                          }
                                          // ★ v133：post 执行前状态可能已经变了（wifi 异步填充）。
                                          //   重新读一次「这一行现在是不是已连接」，
                                          //   如果是，就**不要再挂玻璃**（官方蓝色胶囊优先）。
                                          boolean stillUnsel = true;
                                          try {
                                              Object contentInst2 = fOuter73.get(adInst);
                                              Object[] items2 = (Object[]) accItems73.invoke(null, contentInst2);
                                              if (items2 != null && pos73 >= 0 && pos73 < items2.length
                                                      && sel73.isInstance(items2[pos73])) {
                                                  stillUnsel = !fSel73.getBoolean(items2[pos73]);
                                              }
                                          } catch (Throwable ignored) {}
                                          if (!stillUnsel) {
                                              // 状态已变成「已连接」→ 摘掉玻璃，交回官方蓝色胶囊
                                              CcHooks.clearOurGlass(fb76, cl, "L133 lateSelNoGlass");
                                              if (DETAIL_BG_LOG73.get() < 16)
                                                  L.log(4, TAG, "L133 skip frost (became selected) pos=" + pos73);
                                              return;
                                          }
                                          android.graphics.drawable.GradientDrawable gd73 = new android.graphics.drawable.GradientDrawable();
                                          gd73.setCornerRadius(24f * fb76.getResources().getDisplayMetrics().density);
                                          gd73.setColor(0x14FFFFFF);
                                          fb76.setBackground(gd73);
                                          android.view.ViewOutlineProvider op73 = new android.view.ViewOutlineProvider() {
                                              public void getOutline(View vv, android.graphics.Outline o) {
                                                  o.setRoundRect(0, 0, vv.getWidth(), vv.getHeight(), 24f * vv.getResources().getDisplayMetrics().density);
                                              }
                                          };
                                          fb76.setOutlineProvider(op73);
                                          fb76.setClipToOutline(true);
                                          CcHooks.applyUnifiedGlass(module, fb76, cl, "L73 itemFrost", CcHooks.currentBlurRadius(), true);
                                          if (DETAIL_BG_LOG73.get() < 16)
                                              L.log(4, TAG, "L76 frostApplied tries=" + tries76[0] + " size=" + fb76.getWidth() + "x" + fb76.getHeight());
                                      } catch (Throwable ignored) {}
                                  }};
                                  bg73.post(rr76[0]);
                              }
                             if (DETAIL_BG_LOG73.incrementAndGet() <= 8)
                                 L.log(4, TAG, "L73 itemBg pos=" + pos73 + " selected=" + isSel73);
                         } catch (Throwable te73) { if (DETAIL_BG_LOG73.incrementAndGet() <= 5) L.log(5, TAG, "L73 itemBg err " + te73); }
                         return r73;
                     });
                     L.log(4, TAG, "L73 itemBg hook installed");
                 } catch (Throwable t73) { L.log(5, TAG, "L73 itemBg install failed: " + t73); }
                 // ★ v77：修复系统 bug——通知堆叠时经典控制中心偶发无法下滑。
                 //   机制实证（smali）：
                 //     NotificationStackScrollLayout.onInterceptTouchEventScroll：
                 //       DOWN 时算一次 isInScrollableRegion → 缓存 isDownInScrollableRegion
                 //       if (!isDownInScrollableRegion) return false;  ← 整个手势都不拦截 = 滑不动
                 //     NotificationStackScrollLayoutInjector.isInScrollableRegion：
                 //       if (region.first + scrimPadding >= region.second) return false;  ← region 失效
                 //       否则判断 region.first <= y <= region.second
                 //     scrollableRegion 来源 NotificationContainerViewBinder.updateScrollableRegion：
                 //       hasNotif ? (notifTop, bounds.bottom) : (bounds.top, bounds.bottom)
                 //   → 通知堆叠/新增时 notifTop 与 bounds.bottom 可能交错，region 变成空区间，
                 //     整片通知区被判为「不可滚动」，直到下次 DOWN 重算。
                 //   修复：官方返回 false 时，若 Y 仍落在通知区（NSSL 自身范围内）则兜底放行，
                 //         让后续 touch slop / 方向判断正常接管（不会误触发，仍需滑动超阈值）。
                 try {
                     Class<?> inj77 = cl.loadClass("com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutInjector");
                     final java.lang.reflect.Field fLayout77 = inj77.getField("layout");
                     final java.lang.reflect.Field fRegion77 = inj77.getField("scrollableRegion");
                     java.lang.reflect.Method mIsIn77 = inj77.getDeclaredMethod("isInScrollableRegion", android.view.MotionEvent.class);
                     module.hook((Executable) mIsIn77).setId("cc_l77_scrollregion").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                         Object r77 = chain.proceed();
                         boolean ok77 = Boolean.TRUE.equals(r77);
                         try {
android.view.MotionEvent ev77 = (android.view.MotionEvent) chain.getArgs().get(0);
                              if (!ok77 && ev77 != null) {
                                  Object injInst = chain.getThisObject();
                                  View nsslView = (View) fLayout77.get(injInst);
                                  float y77 = ev77.getY();
                                  Object reg = fRegion77.get(injInst);
                                  // ★ v111：诊断——锁屏双击息屏的死区排查。
                                          //   【主人反馈】「锁屏媒体卡片上方无法双击息屏，
                                          //     下拉控制中心再双击可以正常息屏，
                                          //     说明死区叠加在锁屏而不是控制中心」
                                          //   【官方判定（smali 实证）】KeyguardPanelViewInjector:
                                          //       if (isDoubleTapToSleep && SUPPORT_GESTURE_WAKEUP && !inEditor) {
                                          //           x >= doubleTapMinimumWidthThreshold(40.5dp)
                                          //           && x <= screenWidth - 40.5dp
                                          //           && y >= doubleTapMinimumValidThresholdTop(64dp)
                                          //           && y <= screenHeight - 54dp
                                          //           && doubleTapHelper.onTouchEvent(ev);
                                          //       }
                                          //   【疑点】v77 hook 只把 false→true（多放行给 NSSL），
                                          //     理论上不会阻止双击；但若 NSSL 因此拦截了整段手势，
                                          //     双击事件就到不了 keyguard。
                                          //   【诊断目标】
                                          //     ① 锁屏双击时 isInScrollableRegion 的官方结果 / region 值
                                          //     ② v77 兜底是否被触发（触发则说明 NSSL 抢了触摸）
                                          try {
                                              int c111 = L111_DIAG_CNT.incrementAndGet();
                                              if (c111 <= 40) {
                                                  Object injInst111 = chain.getThisObject();
                                                  View nssl111 = (View) fLayout77.get(injInst111);
                                                  Object reg111 = fRegion77.get(injInst111);
                                                  String rs111 = "null";
                                                  if (reg111 != null) {
                                                      try {
                                                          Object a111 = reg111.getClass().getMethod("getFirst").invoke(reg111);
                                                          Object b111 = reg111.getClass().getMethod("getSecond").invoke(reg111);
                                                          rs111 = a111 + ".." + b111;
                                                      } catch (Throwable ig111) { rs111 = reg111.getClass().getSimpleName(); }
                                                  }
                                                  float y111 = ev77.getY();
                                                  float rawY111 = ev77.getRawY();
                                                  L.log(4, TAG, "L111 scrollregion y=" + (int) y111 + " rawY=" + (int) rawY111
                                                          + " official=" + ok77 + " region=" + rs111
                                                          + " nsslH=" + (nssl111 == null ? -1 : nssl111.getHeight())
                                                          + " act=" + ev77.getActionMasked());
                                              }
                                          } catch (Throwable ignored111) {}
                                  //   【主人反馈】「双击息屏在磁贴上半部分无效」
                                  //   【原因】v77 的兜底条件是 y ∈ [0, nssl.getHeight()]，
                                  //     而 NSSL 高度达 3200px（覆盖整屏）→ 磁贴区域也被判为
                                  //     "可滚动" → NSSL 拦截触摸 → 磁贴收不到双击。
                                  //   【正确判据】v77 要修的是「通知堆叠时 region 变空区间」
                                  //     （first + scrimPadding >= second）。
                                  //     region 非空时官方的 false 是**正确的**（Y 确实不在通知区），
                                  //     此时不该兜底，否则会抢走磁贴/其他区域的触摸。
                                  boolean regionEmpty77 = false;
                                  if (reg == null) {
                                      regionEmpty77 = true;
                                  } else {
                                      try {
                                          Object a = reg.getClass().getMethod("getFirst").invoke(reg);
                                          Object b = reg.getClass().getMethod("getSecond").invoke(reg);
                                          if (a instanceof Integer && b instanceof Integer) {
                                              regionEmpty77 = ((Integer) a) >= ((Integer) b);
                                          }
                                      } catch (Throwable ignored3) { regionEmpty77 = false; }
                                  }
                                  boolean inArea77 = nsslView != null && y77 >= 0f && y77 <= nsslView.getHeight();
                                  if (inArea77 && regionEmpty77) {
                                      if (SCROLLREGION_LOG77.incrementAndGet() <= 12) {
                                          String rs = "null";
                                          if (reg != null) {
                                              try {
                                                  Object a = reg.getClass().getMethod("getFirst").invoke(reg);
                                                  Object b = reg.getClass().getMethod("getSecond").invoke(reg);
                                                  rs = a + ".." + b;
                                              } catch (Throwable ignored2) { rs = reg.getClass().getSimpleName(); }
                                          }
                                          L.log(4, TAG, "L77 scrollRegion FIX y=" + (int) y77 + " h=" + nsslView.getHeight() + " region=" + rs + " -> allow");
                                      }
                                      return Boolean.TRUE;
                                  }
                              }
                         } catch (Throwable t77) { /* 静默，退回官方结果 */ }
                         return r77;
                     });
                     L.log(4, TAG, "L77 scrollRegion fix installed");
                 } catch (Throwable t77i) { L.log(5, TAG, "L77 scrollRegion install failed: " + t77i); }
                // ★ v87：v84 的 height hook 撤销。
                 //   【为什么】smali 实证 MiuiMediaHeaderView.setActualHeight(IZ)：
                 //       super.setActualHeight(...)          // 高度动画的每帧回调
                 //       if (getOutlineProvider() != null)   // ★ 官方在这里做判断
                 //           invalidateOutline();            //   每帧重算裁剪
                 //   官方 finishForMediaNotificationAnim() 特意先 setOutlineProvider(null)，
                 //   就是为了让高度插值期间**不**每帧 invalidateOutline。
                 //   而我们的 applyMediaReflectiveGlass 会 setOutlineProvider(roundRect)，
                 //   把官方的 null 覆盖回去 → 每帧裁剪 → 观感"多余空间瞬间被裁掉"。
                 //   v84 又把 mediaBg 高度改成 0dp（约束驱动），等于每帧跟随，
                 //   进一步放大了这个问题。
                 //   【v87 对策】见下方 installMediaOutlineGuard87：
                 //     动画期间（MEDIA_ANIM_GUARD85 / mIsAnimating）强制 outline=null，
                 //     动画结束后再恢复圆角。
                 try {
                     Class<?> ctl83 = Class.forName(
                             "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaViewControllerImpl",
                             false, cl);
                     Method fa83 = ctl83.getDeclaredMethod("onFullAodStateChanged", Boolean.TYPE);
                     fa83.setAccessible(true);
                     module.hook((Executable) fa83).setId("cc_l90_media_aodheight")
                             .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                             .intercept(chain -> {
Object self83 = chain.getThisObject();
                                  Object holder83 = CcHooks.getFieldSafe(self83, "holder");
                                  Object arg83 = chain.getArgs().isEmpty() ? null : chain.getArgs().get(0);
                                  // ★ v112：记录「进入 AOD」的时刻，供 resetRowFullAodStyle 守卫使用。
                                  //   【L108 实证】亮屏→AOD 时 AOD 状态被整体回滚：
                                  //     ah 506->683 / par 683 / mbg 678 / seek VIS
                                  //   回滚源头：NotifiFullAodController.resetRowFullAodStyle()
                                  //     进去第一件事 if (mEnableFullAod) { mEnableFullAod = false; ... }
                                  //   触发链：MiuiFullAodManager.updateFullAodEnable()
                                  //           → onFullAodEnableChanged(false)
                                  //           → resetRowFullAodStyle()
                                  //   v109 尝试 hook onFullAodEnableChanged 失败（反射拿不到实际调用点）。
                                  //   v112 改为 hook resetRowFullAodStyle 本身（public final，必定可 hook），
                                  //     用「刚进 AOD 的时间戳」判定是否为误触发。
                                  if (Boolean.TRUE.equals(arg83)) {
                                      AOD_ENTER_TS112 = System.currentTimeMillis();
                                  }
                                  // ★ v115：诊断——抓 onFullAodStateChanged(false) 的真实调用栈。
                                  //   【L95 时序铁证（v114 日志）】
                                  //     TL#181~182  ah=506 mbg=506 lp=506 top=0  ← AOD 已正确到位
                                  //     TL#183 dt=600  ah=683 mbg=678 lp=678 top=3  ← 600ms 后整体弹回
                                  //     TL#186~220    ah 681→508 平滑              ← 再播一遍收缩（=下沉）
                                  //   说明「淡入完成后 600ms，有人调了 onFullAodStateChanged(false)」，
                                  //   把整套展开态样式重新应用了一次。
                                  //   这里直接记录调用栈，一次定位真正的调用者。
                                  if (Boolean.FALSE.equals(arg83)) {
                                      long nowD115 = System.currentTimeMillis();
                                      long enterD115 = AOD_ENTER_TS112;
                                      // ★ v124：撤销 v123 的时间窗拦截。
                                      //   【主人反馈】「亮屏>aod 不变，同时 aod>锁屏 变为瞬间裁剪复原后跳变。
                                      //   两者行为似乎统一了」
                                      //   【日志】L123 skip wakeup false (2ms since display-on, state=2) #18
                                      //   【为什么错】onDisplayStateOn 在**每一次亮屏**都会触发，
                                      //   包括「AOD→锁屏」时（state 也常是 2）。
                                      //   于是 AOD→锁屏 时紧跟亮屏的 onFullAodStateChanged(true)
                                      //   被我拦掉了 → mediaBg 停在 506，父容器已 683
                                      //   → 「瞬间裁剪」，随后系统再补一次 → 「复原后跳变」。
                                      //   【为什么亮屏→AOD 没变】那条路径的 false 来自更晚（879ms），
                                      //   不在 100ms 窗口内，所以根本没拦到。
                                      //   【结论】时间窗（无论多短）都会误伤，因为两条路径都从 onDisplayStateOn 开始。
                                      //   真正需要的是「区分 AOD 状态机的方向」，不是时间。
                                      //
                                      // ★ v124 新增：抓完整调用栈（不过滤任何包名），
                                      //   一次定位「谁在调这个 false」。
                                      int cS124 = AOD_FALSE_STACK_CNT124.incrementAndGet();
                                      if (cS124 <= 12) {
                                          StringBuilder sb124 = new StringBuilder();
                                          try {
                                              StackTraceElement[] st124 = Thread.currentThread().getStackTrace();
                                              int n124 = 0;
                                              for (StackTraceElement e124 : st124) {
                                                  String cn124 = e124.getClassName();
                                                  if (cn124.startsWith("java.lang.Thread")) continue;
                                                  sb124.append(cn124).append("#").append(e124.getMethodName())
                                                          .append(": ").append(e124.getLineNumber()).append(" | ");
                                                  if (++n124 >= 14) break;
                                              }
                                          } catch (Throwable ignored) {}
                                          L.log(4, TAG, "L124 FALSE stack #" + cS124
                                                  + " enter=" + (enterD115 > 0 ? (nowD115 - enterD115) + "ms" : "no-ts")
                                                  + " :: " + sb124);
                                      }
                                      // ★ v116：核心修复——拦截「进 AOD 途中的误触发 false」。
                                      //   【L95 铁证（本轮日志）】
                                      //     成功段 TL#182~212：ah 683→506 平滑，mbg=506 恒定 ✅
                                      //     失败段 TL#102~132：ah 683→506 平滑，但 mbg=678 恒定 ❌
                                      //   【为什么】mbg.height 只在本方法里被设置：
                                      //     if (inFullAod) h = qs_media_session_height_expanded_fullAod (138dp=506)
                                      //     else           h = qs_media_session_height_expanded        (185dp=683)
                                      //     mediaBg.setLayoutParams(h)
                                      //   进 AOD 途中有人调了一次 false → mbg 被设回 678，
                                      //   而父容器仍按 ah 平滑收缩到 506 →
                                      //   mediaBg 超出父容器被 clipChildren 裁掉 = 「瞬间裁剪」。
                                      //   【判据】距 AOD 进入 < 1500ms **且** ah 还在收缩途中（> 520）。
                                      //     此时用户不可能已经主动退出 AOD，所以这个 false 必是中间态抖动。
                                      //     真正退出 AOD 时 ah 已到 506，不满足 > 520，不会被误拦。
                                      if (enterD115 > 0 && nowD115 - enterD115 < 2000L) {
                                          // ★ v117：撤销 v116 的「拦掉迟到 false」。
                                          //   【主人反馈】「aod>锁屏的动画现在在动画结束后，
                                          //   卡片才展开成完整卡片，而亮屏>aod 没有变化」
                                          //   【为什么错】判据 mbgH < 560 在「退出 AOD」时同样成立
                                          //   （退出时 mbg 本来就是 506，正要被设成 678），
                                          //   于是正常退出也被拦 → mbg 停在 506，
                                          //   父容器已展开到 683 → 视觉上卡片「动画结束后才补齐」。
                                          //   TL#194 ah=506 mbg=506  →  TL#195 dt=2029 ah=683 mbg=678
                                          //   这就是被拦了 2 秒的铁证。
                                          //   【结论】不能用「mbg 当前高度」区分「迟到回滚」和「正常退出」，
                                          //   两者在调用那一刻的状态完全相同。
                                          //   【v118 思路】改为在源头拦 mEnableFullAod 的写入（见下方 hook）。
                                          //   这里只保留轻量诊断。
                                          int cD115 = AOD_FALSE_CNT115.incrementAndGet();
                                          if (cD115 <= 12) {
                                              int mbgH117 = -1;
                                              int ah117 = -1;
                                              try {
                                                  if (holder83 != null) {
                                                      Object bg117 = holder83.getClass().getField("mediaBg").get(holder83);
                                                      if (bg117 instanceof View) {
                                                          ViewGroup.LayoutParams l117 =
                                                                  ((View) bg117).getLayoutParams();
                                                          if (l117 != null) mbgH117 = l117.height;
                                                          Object par117 = ((View) bg117).getParent();
                                                          int d117 = 0;
                                                          while (par117 instanceof View && d117++ < 6) {
                                                              if (par117.getClass().getName().contains("MiuiMediaHeaderView")) {
                                                                  ah117 = getIntFieldSafe(par117, "mActualHeight");
                                                                  break;
                                                              }
                                                              par117 = ((View) par117).getParent();
                                                          }
                                                      }
                                                  }
                                              } catch (Throwable ignored) {}
                                              L.log(4, TAG, "L117 aod FALSE at "
                                                      + (nowD115 - enterD115) + "ms mbgH=" + mbgH117
                                                      + " ah=" + ah117 + " #" + cD115);
                                          }
                                      }
                                      if (enterD115 > 0 && nowD115 - enterD115 < 2000L) {
                                          int cD115 = AOD_FALSE_CNT115.incrementAndGet();
                                          if (cD115 <= 10) {
                                              StringBuilder sbD115 = new StringBuilder();
                                              try {
                                                  StackTraceElement[] stD115 = Thread.currentThread().getStackTrace();
                                                  int addedD115 = 0;
                                                  for (StackTraceElement eD115 : stD115) {
                                                      String cnD115 = eD115.getClassName();
                                                      if (cnD115.startsWith("com.mersand.hyperclassic")) continue;
                                                      if (cnD115.startsWith("java.lang.Thread")) continue;
                                                      if (cnD115.startsWith("android.os.")) continue;
                                                      if (cnD115.startsWith("de.robv.android.xposed")) continue;
                                                      sbD115.append(cnD115.substring(cnD115.lastIndexOf('.') + 1))
                                                              .append('#').append(eD115.getMethodName()).append(" <- ");
                                                      if (++addedD115 >= 6) break;
                                                  }
                                              } catch (Throwable ignored) {}
                                              L.log(4, TAG, "L115 aod FALSE at "
                                                      + (nowD115 - enterD115) + "ms via " + sbD115);
                                          }
                                      }
                                  }
                                 int hBefore = -999;
                                 View mb83 = null;
                                 try {
                                     if (holder83 != null) {
                                         Object bg = holder83.getClass().getField("mediaBg").get(holder83);
                                         if (bg instanceof View) {
                                             mb83 = (View) bg;
                                             ViewGroup.LayoutParams lpb = mb83.getLayoutParams();
                                             if (lpb != null) hBefore = lpb.height;
                                         }
                                     }
                                 } catch (Throwable ignored) {}
                                 Object r83 = chain.proceed();
                                 // ★ v99：v98 的「mediaBg 改回 0dp」已撤销。
                                 //   【主人反馈】「又回到之前的瞬间裁剪行为，锁屏>aod 正常动画但结束时同样被裁剪底部」
                                 //   【为什么无效】mediaBg 本来就是 0dp 约束驱动（铺满 mi_media_controls），
                                 //     改 0dp 只是"保持原状"，解决不了父容器高度塌陷。
                                 //   【真正根因】父容器 mi_media_controls 的高度由 MiuiMediaHeaderView
                                 //     的 getIntrinsicHeight() 决定：
                                 //       if (mAnimateHeight != 0) return mAnimateHeight;  ← 动画中 683
                                 //       return super.getIntrinsicHeight();               ← 结束后 474（塌陷）
                                 //     修复在 finishForMediaNotificationAnim 的 hook 里（见 L99）。
                                 // ★ v91：v90 的「改 0dp」已撤销——主人实测任何 LayoutParams 写入
                                 //   都会破坏 锁屏→息屏 的动画（动画后补一次裁剪）。
                                 //   v88 的「只记录、不写」才是正确基线。
                                 //   现在改为纯诊断：记录每次调用时的高度变化与视图尺寸，
                                 //   用来判断 亮屏→AOD / AOD→亮屏 两条路径到底走了哪条链路。
                                 try {
                                     if (mb83 != null) {
                                         final View fmb = mb83;
                                         final int hb = hBefore;
                                         final Object a83 = arg83;
                                         // ★ v93：诊断升级——记录 header 的实际高度、outline 状态、
                                         //   以及 mIsAnimating，用来判断三条路径到底差在哪。
                                         int ahBefore = -1;
                                         boolean hasOl = false;
                                         boolean animating = false;
                                         try {
                                             Object par = fmb.getParent();
                                             int d = 0;
                                             while (par instanceof View && d++ < 6) {
                                                 if (par.getClass().getName().contains("MiuiMediaHeaderView")) {
                                                     Object ah = CcHooks.invokeNoArg(par, "getActualHeight");
                                                     if (ah instanceof Integer) ahBefore = (Integer) ah;
                                                     hasOl = ((View) par).getOutlineProvider() != null;
                                                     break;
                                                 }
                                                 par = ((View) par).getParent();
                                             }
                                             Class<?> ctl = Class.forName(
                                                     "com.android.systemui.statusbar.notification.fullaod.NotifiFullAodController",
                                                     false, fmb.getClass().getClassLoader());
                                             Field fmA = ctl.getDeclaredField("mIsAnimating");
                                             fmA.setAccessible(true);
                                             Object oA = fmA.get(null);
                                             animating = oA instanceof Boolean && (Boolean) oA;
                                         } catch (Throwable ignored) {}
                                         final int fAh = ahBefore;
                                         final boolean fOl = hasOl;
                                         final boolean fAnim = animating;
                                         fmb.postDelayed(() -> {
                                             try {
                                                 ViewGroup.LayoutParams lpa = fmb.getLayoutParams();
                                                 int ha = lpa != null ? lpa.height : -999;
                                                 int ahAfter = -1;
                                                 boolean olAfter = false;
                                                 try {
                                                     Object par = fmb.getParent();
                                                     int d = 0;
                                                     while (par instanceof View && d++ < 6) {
                                                         if (par.getClass().getName().contains("MiuiMediaHeaderView")) {
                                                             Object ah = CcHooks.invokeNoArg(par, "getActualHeight");
                                                             if (ah instanceof Integer) ahAfter = (Integer) ah;
                                                             olAfter = ((View) par).getOutlineProvider() != null;
                                                             break;
                                                         }
                                                         par = ((View) par).getParent();
                                                     }
                                                 } catch (Throwable ignored) {}
                                                 int n91 = MEDIA_DIAG_CNT91.incrementAndGet();
                                                 if (n91 <= 40) {
                                                     L.log(4, TAG, "L93 aod arg=" + a83
                                                             + " lp.h " + hb + "->" + ha
                                                             + " bg=" + fmb.getWidth() + "x" + fmb.getHeight()
                                                             + " hdrAH " + fAh + "->" + ahAfter
                                                             + " outline=" + fOl + "->" + olAfter
                                                             + " anim=" + fAnim);
                                                 }
                                             } catch (Throwable ignored) {}
                                         }, 250L);
                                     }
                                 } catch (Throwable t83) { L.log(5, TAG, "L93 diag err " + t83); }
                                 return r83;
                             });
                     L.log(4, TAG, "L91 media AOD diag hook installed");
                 } catch (Throwable t83i) { L.log(5, TAG, "L91 diag hook failed: " + t83i); }
                 // ★ v112：拦截 resetRowFullAodStyle 的误触发（亮屏→AOD 的"瞬间裁剪+下沉"根因）。
                 //   【L108 实证】亮屏→AOD 出现：
                 //     L108 JUMP ah 506->683  h=683  mbg=1334x678  lp=678
                 //               par=ConstraintLayout:683  seek=VIS  op=null
                 //   每一项都回滚到「退出 AOD」状态 → 说明整个 AOD 状态被撤销了一次。
                 //   【smali 实证】NotifiFullAodController.resetRowFullAodStyle():
                 //       if (mEnableFullAod) {
                 //           mEnableFullAod = false;        ← ★ 强制清 0
                 //           ...applyBgColor()              ← 用非 AOD 配色
                 //           ...listeners.updateFullAodAnimState(...)
                 //           updateNotificationStyle()
                 //           completeFullAodAnim()
                 //           updateMediaNotificationStyle()  ← mediaBg 设回 185dp
                 //           finishForMediaNotificationAnim()← outline=null + setAnimateHeight(0)
                 //       }
                 //       mIsAnimating = false; mWillDoAodAnim = false; mFromDesktop = false;
                 //   触发链：MiuiFullAodManager.updateFullAodEnable()
                 //           → onFullAodEnableChanged(false)   ← dozing 切换的中间态
                 //           → resetRowFullAodStyle()
                 //   【为什么 v109 失败】onFullAodEnableChanged 是接口方法，
                 //     反射拿到的 Method 与运行时实际调用点不一致，hook 挂不上（L109 零日志）。
                 //   【v112 修法】hook resetRowFullAodStyle 本身（public final，必定可 hook）。
                 //     若调用发生在「进入 AOD 后 1500ms 内」→ 判定为中间态抖动，
                 //     跳过整个方法（不让它清 mEnableFullAod、不改样式）。
                 //     但必须补上方法末尾的三个状态复位，避免 mIsAnimating 卡住。
                 try {
                     Class<?> ctl112 = Class.forName(
                             "com.android.systemui.statusbar.notification.fullaod.NotifiFullAodController",
                             false, cl);
                     Method rst112 = ctl112.getDeclaredMethod("resetRowFullAodStyle");
                     rst112.setAccessible(true);
                     final java.lang.reflect.Field fAnimating112 = ctl112.getDeclaredField("mIsAnimating");
                     final java.lang.reflect.Field fWillDo112 = ctl112.getDeclaredField("mWillDoAodAnim");
                     final java.lang.reflect.Field fFromDesk112 = ctl112.getDeclaredField("mFromDesktop");
                     fAnimating112.setAccessible(true);
                     fWillDo112.setAccessible(true);
                     fFromDesk112.setAccessible(true);
                     module.hook((Executable) rst112).setId("cc_l112_reset_aod_guard")
                             .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                             .intercept(chain -> {
                                 long now112 = System.currentTimeMillis();
                                 long enter112 = AOD_ENTER_TS112;
                                 if (enter112 > 0 && now112 - enter112 < 1500L) {
                                     int c112 = AOD_RESET_GUARD_CNT112.incrementAndGet();
                                     if (c112 <= 20) L.log(4, TAG, "L112 skip reset ("
                                             + (now112 - enter112) + "ms after AOD enter) #" + c112);
                                     // 只补状态复位，不改样式、不清 mEnableFullAod
                                     try {
                                         Object self112 = chain.getThisObject();
                                         if (self112 != null) {
                                             fAnimating112.setBoolean(self112, false);
                                             fWillDo112.setBoolean(self112, false);
                                             fFromDesk112.setBoolean(self112, false);
                                         }
                                     } catch (Throwable ignored) {}
                                     return null;
                                 }
                                 int c112b = AOD_RESET_REAL_CNT112.incrementAndGet();
                                 if (c112b <= 10) L.log(4, TAG, "L112 allow reset ("
                                         + (enter112 > 0 ? (now112 - enter112) + "ms" : "no-enter-ts") + ") #" + c112b);
                                 return chain.proceed();
                             });
                     L.log(4, TAG, "L112 reset aod guard installed");
                 } catch (Throwable t112) { L.log(5, TAG, "L112 guard failed: " + t112); }
                 // ★ v119：撤销 v118 的「恢复 mEnableFullAod」。
                 //   【主人反馈】「依然导致 aod>锁屏的动画在卡片完成展开后被裁切，
                 //   裁切后再变成完整卡片」
                 //   【日志】L118 restore enter-aod (278ms) #2
                 //   【为什么错】mEnableFullAod 被恢复成 true 后是**粘性**的：
                 //   退出 AOD 时系统读到 true 就认为「还在 AOD」，
                 //   于是把 mediaBg 又设回 506，而父容器已展开到 683 → 裁切。
                 //   等系统后续再把 mEnableFullAod 清成 false，mbg 才补齐 678
                 //   → 视觉就是「展开完被裁，裁完又变完整」。
                 //   【结论】mEnableFullAod 不能碰，它影响的下游太多。
                 //   改用 v119 的纯诊断：确认 linkageViewAnim 到底在什么时候写 false。
                 //   【L119 实证（决定性）】
                 //     p1=false enable false->true  (no-ts)   ← 退出AOD，正常
                 //     p1=true  enable true->false  (1114ms)  ← ★ 进AOD，就是这个 bug
                 //     p1=false enable false->true  (2593ms)  ← 退出AOD，正常
                 //   【p1 语义确认】p1=true 进 AOD；p1=false 退出 AOD。
                 //   【v114/v118 为什么错】proceed 后无条件恢复 true，
                 //     把 p1=false（退出AOD）的正常写入也覆盖了。
                 //   【v120 修法】只在 p1=true 时把 mEnableFullAod 保持 true。
                 //     p1=false 完全不动（那是退出 AOD 的正常逻辑）。
                 try {
                     Class<?> kpvc119 = null;
                     try {
                         kpvc119 = Class.forName("com.android.keyguard.panel.KeyguardPanelViewController", false, cl);
                     } catch (Throwable ig119a) {
                         kpvc119 = Class.forName("com.android.keyguard.panel.KeyguardPanelViewController");
                     }
                     Method lva119 = kpvc119.getDeclaredMethod("linkageViewAnim$default",
                             kpvc119, boolean.class, String.class, int.class);
                     lva119.setAccessible(true);
                     Class<?> nfac119 = Class.forName(
                             "com.android.systemui.statusbar.notification.fullaod.NotifiFullAodController", false, cl);
                     final java.lang.reflect.Field fEnable119 = nfac119.getDeclaredField("mEnableFullAod");
                     fEnable119.setAccessible(true);
                     module.hook((Executable) lva119).setId("cc_l120_linkage_enter_keep")
                             .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                             .intercept(chain -> {
                                 boolean enter119 = false;
                                 try {
                                     Object a119 = chain.getArgs().size() > 1 ? chain.getArgs().get(1) : null;
                                     if (a119 instanceof Boolean) enter119 = (Boolean) a119;
                                 } catch (Throwable ignored) {}
                                 boolean before119 = false;
                                 try { before119 = fEnable119.getBoolean(null); } catch (Throwable ignored) {}
                                 long now119 = System.currentTimeMillis();
                                 long ts119 = AOD_ENTER_TS112;
                                 Object r119 = chain.proceed();
                                 boolean after119 = false;
                                 try { after119 = fEnable119.getBoolean(null); } catch (Throwable ignored) {}
                                 int c119 = LINKAGE_DIAG_CNT119.incrementAndGet();
                                 if (c119 <= 25) L.log(4, TAG, "L119 linkage p1=" + enter119
                                         + " enable " + before119 + "->" + after119
                                         + " (" + (ts119 > 0 ? (now119 - ts119) + "ms" : "no-ts") + ") #" + c119);
                                 // ★ v121：撤销 v120 的写入，恢复纯诊断。
                                 //   【主人反馈】「这次修改和之前一样落到了 aod 进锁屏的动画上，
                                 //   卡片被裁切再恢复」
                                 //   【为什么又打错】mEnableFullAod 是全局静态字段，被多处下游读取：
                                 //     · MiuiMediaViewControllerImpl.attach()
                                 //     · NotifiFullAodController.updateMediaNotificationStyle()
                                 //     · KeyguardPanelViewController.linkageViewAnim$default()
                                 //     · DozeServiceHostInjector.mFullAodEnable
                                 //   把它在进 AOD 时改成 true 后，这个 true 会一直粘住，
                                 //   退出 AOD 时官方写的 mEnableFullAod = mShowNotification 仍是 true
                                 //   → 系统继续认为「在 AOD」→ mediaBg 保持 506，父容器已 683 → 裁切。
                                 //   【结论】mEnableFullAod 不能改。转「只改视图几何」的方案。
                                 return r119;
                             });
                     L.log(4, TAG, "L120 linkage enter keep installed");
                 } catch (Throwable t119) { L.log(5, TAG, "L120 diag failed: " + t119); }
                 // ★ v121：路径判定诊断——记录 onDisplayStateOn 时刻，
                 //   用来区分「亮屏→AOD」和「锁屏→AOD」。
                 //   【为什么要区分】主人反馈「第一次进 aod 时直接就是紧凑卡片」，
                 //   说明系统本身有「跳过过渡动画」的判定。
                 //   【假设】onDisplayStateOn 只在「亮屏」路径被调用，
                 //   「锁屏→AOD」不走它。若成立，就能用它作为判别器。
                 try {
                     Class<?> cb121 = null;
                     try {
                         cb121 = Class.forName(
                                 "com.android.keyguard.panel.KeyguardPanelViewController$keyguardUpdateMonitorInjectorCallback$1",
                                 false, cl);
                     } catch (Throwable ig121a) {
                         cb121 = Class.forName(
                                 "com.android.keyguard.panel.KeyguardPanelViewController$keyguardUpdateMonitorInjectorCallback$1");
                     }
                     Method dso121 = cb121.getDeclaredMethod("onDisplayStateOn");
                     dso121.setAccessible(true);
                     module.hook((Executable) dso121).setId("cc_l121_display_on_ts")
                             .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                             .intercept(chain -> {
                                 WAKEUP_TS121 = System.currentTimeMillis();
                                 int st121 = -1;
                                 try {
                                     Object self121 = chain.getThisObject();
                                     Object outer121 = CcHooks.getFieldSafe(self121, "this$0");
                                     Object st121o = CcHooks.invokeNoArg(outer121, "wakeupAnimationState");
                                     if (st121o instanceof Integer) st121 = (Integer) st121o;
                                 } catch (Throwable ignored) {}
                                 WAKEUP_STATE121 = st121;
                                 int c121 = DISPLAY_ON_CNT121.incrementAndGet();
                                 if (c121 <= 15) L.log(4, TAG, "L121 display ON state=" + st121 + " #" + c121);
                                 return chain.proceed();
                             });
                     L.log(4, TAG, "L121 display-on ts hook installed");
                 } catch (Throwable t121) { L.log(5, TAG, "L121 hook failed: " + t121); }
                 // ★ v115：撤销 v114 的 linkageViewAnim$default 拦截。
                 //   【主人反馈】「aod>锁屏的动画出现了错误，在卡片完整展开后出现了裁切」
                 //   【日志】L114 restore enableAod (703ms after AOD enter) ← 这是「退出 AOD」被误拦，
                 //   把 mEnableFullAod 又拉回 true，导致展开完成后状态不一致 → 裁切。
                 //   【更关键的发现】v114 让 enableAod 保持 true 之后，
                 //   L108 ah 506->683 仍然发生（enableAod=true isAnimating=false seek=VIS），
                 //   说明 mEnableFullAod 根本不是回滚的因，只是果。
                 //   真正的信号是 seek=VIS —— 说明 onFullAodStateChanged(false) 被调用了。
                 //   下一步：给 onFullAodStateChanged 的 false 分支加调用栈，直接抓调用者。
                 // ★ v88：v87 的 setActualHeight outline guard 撤销。
                 //   【主人反馈】「方向不对」——v87 引入"剥离→恢复"两次裁剪时机：
                 //     锁屏>息屏：先下沉，完成后延迟裁剪（原本正常，被搞坏）
                 //     亮屏>息屏：动画完成后裁剪并下沉
                 //     息屏>亮屏：立刻补上裁剪部分和上浮
                 //   【结论】官方本来就让 outline=null（finishForMediaNotificationAnim 里
                 //     setOutlineProvider(null)），我们不该"恢复"它。
                 //     真正要做的只有一件事：**别给它设 outline**。
                 //   v88 对策见 applyMediaGlassMaterialFull85：effect 路径下
                 //     显式 setOutlineProvider(null) + setClipToOutline(false)，
                 //     圆角交给 MiGlass 的 SDF 参数（setMiGlassCompat 自带圆角）。
                 // ★ v91：v89 的「补调媒体动画」已撤销。
                 //   【为什么】主人实测补调无效（L89 supplement 触发了但动画仍缺失），
                 //   而且补调本身会在官方动画之后再加一次 updateMediaNotificationStyle，
                 //   反而可能引入额外裁剪时机。
                 //   现在改为纯诊断（见上方 L91），先搞清楚三条路径的真实差异再动手。
                 // ★ v85：finishForMediaNotificationAnim 期间禁止套玻璃。
                 //   【主人反馈】「唯一正常的锁屏→息屏也出现了问题，卡片在动画完成后再被裁剪一次」
                 //   【smali 实证】FullAodTransitionListener.onComplete() 顺序：
                 //       completeFullAodAnim()            → transitionAlpha = 1
                 //       updateNotificationStyle()
                 //       updateMediaNotificationStyle()   → mediaBg 高度瞬设
                 //       finishForMediaNotificationAnim() → setOutlineProvider(null)
                 //                                          + setAnimateHeight(h)  ← 高度插值动画开始！
                 //       mIsAnimating = false             ← 标志已清
                 //   所以 mIsAnimating 覆盖不到这段高度动画，材质会插进去 → 二次裁剪。
                 //   对策：hook 该方法，进入时置 MEDIA_ANIM_GUARD85=true，
                 //         延迟 450ms（高度插值约 300-400ms）后解除。
                 try {
                     Class<?> fctl85 = Class.forName(
                             "com.android.systemui.statusbar.notification.fullaod.NotifiFullAodController",
                             false, cl);
                     Method fma85 = fctl85.getDeclaredMethod("finishForMediaNotificationAnim");
                     fma85.setAccessible(true);
                     module.hook((Executable) fma85).setId("cc_l85_finish_media_anim")
                             .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                              .intercept(chain -> {
                                  MEDIA_ANIM_GUARD85 = true;
                                  MEDIA_ANIM_GUARD_TS85 = System.currentTimeMillis();
                                  try {
                                      return chain.proceed();
                                  } finally {
                                      // ★ v99：核心修复——把 mAnimateHeight 恢复成 mActualHeight。
                                      //   【机制（smali 实证）】
                                      //     MiuiMediaHeaderView.getIntrinsicHeight():
                                      //       if (visible && mAnimateHeight != 0) return mAnimateHeight;
                                      //       return super.getIntrinsicHeight();   // ← 内容实际高度
                                      //     finishForMediaNotificationAnim() 调 setAnimateHeight(0)
                                      //     → mAnimateHeight=0 → 行高度回退到 super 的实现，
                                      //       即 mi_media_controls 的 wrap_content 实测高度。
                                      //   【L95 实证】AOD 态 seekBar 被 setVisibility(8) 隐藏，
                                      //     内容实测高度 = 474px，而目标 AOD 高度 = 506px（138dp），
                                      //     差 32px → 动画结束瞬间"卡片被裁掉底部一截"。
                                      //     TL#205 ah=506 mbg=683（动画末帧，靠 mAnimateHeight=683 撑着）
                                      //     TL#206 ah=506 mbg=474（setAnimateHeight(0) 后塌陷）
                                      //   【修法】proceed() 之后立刻把 mAnimateHeight 设回当前
                                      //     mActualHeight（动画终点值 = 目标 AOD 高度），
                                      //     让 getIntrinsicHeight() 继续返回正确高度，
                                      //     行布局高度落在 506 而不是 474。
                                      //   【注意】必须调 setAnimateHeight(int) 而不是直接写字段，
                                      //     它内部会 notifyHeightChanged 触发重新布局。
                                      try {
                                          Object self99 = chain.getThisObject();
                                          if (self99 != null) {
                                              // 方法在 NotifiFullAodController 上，需要遍历 NSSL 找 header
                                              Object ssl99 = getFieldSafe(self99, "mStackScrollLayout");
                                              Object nssl99 = ssl99;
                                              if (nssl99 != null && !(nssl99 instanceof android.view.ViewGroup)) {
                                                  try {
                                                      Method get99 = nssl99.getClass().getMethod("get");
                                                      get99.setAccessible(true);
                                                      nssl99 = get99.invoke(nssl99);
                                                  } catch (Throwable ignored) {}
                                              }
                                              if (nssl99 instanceof android.view.ViewGroup) {
                                                  android.view.ViewGroup vg99 = (android.view.ViewGroup) nssl99;
                                                  int cnt99 = vg99.getChildCount();
                                                  for (int i99 = 0; i99 < cnt99; i99++) {
                                                      android.view.View ch99 = vg99.getChildAt(i99);
                                                      if (ch99 == null || ch99.getVisibility() == android.view.View.GONE) continue;
                                                      if (!"MiuiMediaHeaderView".equals(ch99.getClass().getSimpleName())) continue;
                                                      int ah99 = getIntFieldSafe(ch99, "mActualHeight");
                                                      if (ah99 <= 0) break;
                                                      // ★ v103：撤销 v99/v101/v102 的全部高度写入，回到 v88 基线。
                                                      //   【L95 逐帧实证（本轮）】
                                                      //     mbg=1334x678 全程恒定 —— 官方本来就靠 outline 裁剪
                                                      //     实现视觉收缩，mbg 高度不变是**设计如此**，不是 bug。
                                                      //     我们之前所有改 mbg / mActualHeight / mAnimateHeight
                                                      //     的动作都在打断官方的平滑插值。
                                                      //   【真正的差异（本轮决定性发现）】
                                                      //     展开路径(AOD→锁屏)：op=空  ← provider 缺失！
                                                      //     收缩路径(锁屏→AOD)：op=NotifiFullAodController$2 ✅
                                                      //   MiuiMediaHeaderView.setActualHeight():
                                                      //       if (getOutlineProvider() != null) invalidateOutline();
                                                      //   provider 为空 → 从不 invalidateOutline → outline 不跟随
                                                      //   mActualHeight 更新 → 裁剪跳变 = "缺裁剪过渡"。
                                                      //   【v103 修法】不写任何高度，只在动画期间**补上缺失的
                                                      //   outline provider**（复刻 NotifiFullAodController$2 的行为）。
                                                      //   见下方 cc_l103_outline_fill hook。
                                                      // ★ v100：动画结束后 1.4s 采样，确认布局高度是否真的收缩。
                                                      //   【为什么需要】主人反馈「不再下沉，但底部仍被裁剪」。
                                                      //   怀疑：mAnimateHeight 改了，但 header 的**布局高度**没跟着变
                                                      //   （notifyHeightChanged 可能被 NSSL 的动画状态忽略），
                                                      //   于是 outline(getActualHeight=506) 把 683 高的 view 裁掉 177px。
                                                      final android.view.View fhdr100 = ch99;
                                                      final android.os.Handler h100 =
                                                              new android.os.Handler(android.os.Looper.getMainLooper());
                                                      for (int s100 = 0; s100 < 14; s100++) {
                                                          final int si100 = s100;
                                                          h100.postDelayed(() -> {
                                                              try {
                                                                  int hh100 = fhdr100.getHeight();
                                                                  int ah100 = getIntFieldSafe(fhdr100, "mActualHeight");
                                                                  int anih100 = getIntFieldSafe(fhdr100, "mAnimateHeight");
                                                                  String mbg100 = "-";
                                                                  String top100 = "-";
                                                                  String par100 = "-";
                                                                  String sb100 = "-";
                                                                  Object vh100 = invokeNoArg(fhdr100, "getMediaViewHolder");
                                                                  if (vh100 != null) {
                                                                      Object bg100 = vh100.getClass().getField("mediaBg").get(vh100);
                                                                      if (bg100 instanceof android.view.View) {
                                                                          android.view.View b100 = (android.view.View) bg100;
                                                                          mbg100 = b100.getWidth() + "x" + b100.getHeight();
                                                                          top100 = String.valueOf(b100.getTop());
                                                                          if (b100.getParent() instanceof android.view.View) {
                                                                              android.view.View p100 = (android.view.View) b100.getParent();
                                                                              par100 = p100.getClass().getSimpleName() + ":" + p100.getHeight();
                                                                          }
                                                                      }
                                                                      Object sbObj100 = vh100.getClass().getField("seekBar").get(vh100);
                                                                      if (sbObj100 instanceof android.view.View) {
                                                                          android.view.View s100v = (android.view.View) sbObj100;
                                                                          sb100 = (s100v.getVisibility() == android.view.View.GONE ? "GONE" : "VIS")
                                                                                  + "@" + s100v.getTop() + "+" + s100v.getHeight();
                                                                      }
                                                                  }
                                                                  L.log(4, TAG, "L100 post t=" + (si100 * 100)
                                                                          + " hdrH=" + hh100 + " ah=" + ah100 + " aniH=" + anih100
                                                                          + " par=" + par100 + " mbg=" + mbg100 + " top=" + top100
                                                                          + " seek=" + sb100
                                                                          + " cto=" + fhdr100.getClipToOutline()
                                                                          + " op=" + (fhdr100.getOutlineProvider() == null ? "-"
                                                                          : fhdr100.getOutlineProvider().getClass().getSimpleName()));
                                                              } catch (Throwable ignored) {}
                                                          }, s100 * 100L);
                                                      }
                                                      break;
                                                  }
                                              }
                                          }
                                      } catch (Throwable t99) { L.log(5, TAG, "L99 restore err " + t99); }
                                      // 高度插值动画通常 300-400ms，留足余量到 700ms。
                                      // 用时间戳而不是纯 boolean，避免多个动画叠加时
                                      // 前一个的延迟回调把后一个的 guard 提前清掉。
                                      final long ts85 = MEDIA_ANIM_GUARD_TS85;
                                      new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                                          if (MEDIA_ANIM_GUARD_TS85 == ts85) MEDIA_ANIM_GUARD85 = false;
                                      }, 700L);
                                  }
                              });
                     L.log(4, TAG, "L85 finish media anim guard installed");
                 } catch (Throwable t85f) { L.log(5, TAG, "L85 finish guard failed: " + t85f); }
                 // ★ v110：撤销 v109 的 onFullAodEnableChanged guard（回退到 v108 基线）。
                 //   【主人反馈】v109 实测无效（L109 无日志），且新增问题：
                 //     「双击息屏在磁贴上半部分无效」
                 //   【为什么无效】KeyguardPanelViewController 的 onFullAodEnableChanged
                 //     可能是接口默认实现或 Kotlin 合成方法，反射拿到的 Method
                 //     与运行时实际调用点不一致，hook 挂不上。
                 //   【结论】回退到 v108，重新找真正的回滚入口。
                 //   v108 保留：L108 跳变诊断（能在 ah 跳变时抓完整快照）。
                 // ★ v95：逐帧时间线诊断——记录 header 高度 / 裁剪 / mediaBg 尺寸的真实变化。
                 //   目的：对比「锁屏→息屏（正常）」与「亮屏→息屏 / 息屏→锁屏（异常）」
                 //   三条路径在动画期间的逐帧差异，找出到底哪一项不同步。
                 try {
                     Class<?> mhv95 = Class.forName(
                             "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaHeaderView",
                             false, cl);
                     Method sah95 = mhv95.getDeclaredMethod("setActualHeight", Integer.TYPE, Boolean.TYPE);
                     sah95.setAccessible(true);
                     module.hook((Executable) sah95).setId("cc_l95_hdr_timeline")
                             .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                             .intercept(chain -> {
// ★ v115：诊断版——不拦截，只在「回滚跳变」时记录。
                                 //   【为什么放弃拦截】
                                 //   TL#183 是 ah=683 mbg=1334x678 lp=678 top=3，
                                 //   说明**布局本身**被改回展开态（mediaBg 和 lp 都是 678），
                                 //   不只是 ah 字段。拦 setActualHeight 只能让 ah 停在 506，
                                 //   布局仍是 683 → 视觉变成「卡片被裁剪」（主人最反感的效果）。
                                 //   所以这里只记录，真正的修法是拦住「回滚的触发源」。
                                 Object r95 = chain.proceed();
                                 // ★ v133：已删除 v131/v132 的 self 高度追踪与 ah 对齐实验。
                                 //   【删除原因】主人指示停止该课题，且 v132 的 L132 计数为 0
                                 //   （对齐分支从未进入，反射 getDeclaredField("mActualHeight")
                                 //    大概率抛 NoSuchFieldException——字段声明在父类）。
                                 //   完整机制与结论已存档：
                                 //   archive/媒体卡片AOD动画问题_存档_v132.md
                                 // ★ v127：现场快照诊断——在 setActualHeight 执行的那一刻
                                 //   直接读 ah / self / parent，确认判据为什么没命中。
                                 //   【v125/v126 都是 0 次触发】说明那一刻
                                 //   ah 没有超出 base 80px，或 base 读不到。
                                 //   L100 的 506 是 postDelayed 延迟采样，
                                 //   与 setActualHeight 现场不是同一时刻。
                                 //   必须先看清「现场值」，才能设计正确判据。
                                 try {
                                     Object self127 = chain.getThisObject();
                                     if (self127 instanceof View) {
                                         View hv127 = (View) self127;
                                         int ah127 = getIntFieldSafe(hv127, "mActualHeight");
                                         int s127 = hv127.getHeight();
                                         int p127 = -1;
                                         String pn127 = "-";
                                         try {
                                             android.view.ViewParent vp127 = hv127.getParent();
                                             if (vp127 instanceof View) {
                                                 p127 = ((View) vp127).getHeight();
                                                 pn127 = vp127.getClass().getSimpleName();
                                             }
                                         } catch (Throwable ignored) {}
                                         // 只在 ah 与容器差异较大时记录，避免刷屏
                                         if (ah127 > 0 && p127 > 0 && Math.abs(ah127 - p127) > 50) {
                                             int c127 = SETAH_DIAG_CNT127.incrementAndGet();
                                             if (c127 <= 30) L.log(4, TAG, "L127 setAH ah=" + ah127
                                                     + " self=" + s127 + " par=" + pn127 + ":" + p127
                                                     + " cto=" + hv127.getClipToOutline()
                                                     + " op=" + (hv127.getOutlineProvider() == null ? "null" : "set")
                                                     + " #" + c127);
                                         }
                                     }
                                 } catch (Throwable ignored) {}
                                 // ★ v126：几何自洽钳制（改用父容器高度作为基准）。
                                 //   【v125 为什么偶发】L125 一次都没触发（6 份日志全 0），
                                 //   因为 getHeight() 在 setActualHeight 被调用的那一刻
                                 //   还没更新（布局在下一帧才生效），
                                 //   而 L100 采样是延迟读取，读到的是更新后的 506。
                                 //   两者不是同一时刻的值 → 判据失效。
                                 //   【L100 实证（本轮）】进入 AOD 段：
                                 //     t=0    hdrH=506  ah=683  mbg=1334x506  ← 容器已 506，内容 ah 还 683
                                 //     t=100  hdrH=506  ah=639  mbg=1334x506
                                 //     t=200  hdrH=506  ah=539  mbg=1334x506
                                 //     t=400  hdrH=506  ah=506  mbg=1334x506  ← 400ms 后才追上
                                 //   容器(hdrH/par)和背景(mbg)都是**瞬间**到 506，
                                 //   只有 mActualHeight 还在 683 → 平滑降。
                                 //   cto=true 且 outline 用 ah 计算 →
                                 //   用 683 裁一个只有 506 高的 view = 底部 177px 被切，
                                 //   视觉就是「瞬间裁剪 + 内容随后下沉」。
                                 //   【为什么只在亮屏→AOD 出现】
                                 //     锁屏→AOD 由 mAnimateHeight 驱动布局，hdrH 与 ah 同步插值；
                                 //     亮屏→AOD 是 hdrH 瞬间到位、ah 滞后，差达 177px。
                                 //   【v126 判据】用「父容器当前高度」而不是自身 getHeight()：
                                 //     父容器高度在 layout 后立即可读，且比自身更早更新。
                                 //     若 ah 超出父容器高度 80px 以上 → 钳制到父容器高度。
                                 try {
                                     Object self125 = chain.getThisObject();
                                     if (self125 instanceof View) {
                                         View hv125 = (View) self125;
                                         int base126 = -1;
                                         // 优先取父容器高度，其次取自身高度
                                         try {
                                             android.view.ViewParent p126 = hv125.getParent();
                                             if (p126 instanceof View) {
                                                 base126 = ((View) p126).getHeight();
                                             }
                                         } catch (Throwable ignored) {}
                                         int self126 = hv125.getHeight();
                                         if (base126 <= 0 || (self126 > 0 && self126 < base126)) {
                                             base126 = self126;
                                         }
                                         int ah126 = getIntFieldSafe(hv125, "mActualHeight");
                                         if (base126 > 0 && ah126 > 0 && ah126 > base126 + 80) {
                                             try {
                                                 java.lang.reflect.Field fAh126 = hv125.getClass()
                                                         .getDeclaredField("mActualHeight");
                                                 fAh126.setAccessible(true);
                                                 fAh126.setInt(hv125, base126);
                                                 if (hv125.getOutlineProvider() != null) {
                                                     hv125.invalidateOutline();
                                                 }
                                                 int c126 = GEO_CLAMP_CNT125.incrementAndGet();
                                                 if (c126 <= 20) L.log(4, TAG, "L126 geo clamp ah "
                                                         + ah126 + "->" + base126
                                                         + " (self=" + self126 + ") #" + c126);
                                             } catch (Throwable ig126) {}
                                         }
                                     }
                                 } catch (Throwable ignored) {}
                                  try {
                                      Object self95 = chain.getThisObject();
                                      if (self95 instanceof View) {
                                          View hv95 = (View) self95;
                                          // ★ v103：核心修复——补上缺失的 outline provider。
                                          //   【L95 逐帧实证】展开路径 op=空，收缩路径 op=NotifiFullAodController$2。
                                          //   【机制】MiuiMediaHeaderView.setActualHeight():
                                          //       if (getOutlineProvider() != null) invalidateOutline();
                                          //   provider 为空时**从不 invalidateOutline**，
                                          //   于是 outline 不跟随 mActualHeight 更新 → 裁剪跳变。
                                          //   【为什么 provider 会缺失】
                                          //     KeyguardPanelViewController 进 AOD 时设了 provider，
                                          //     但 finishForMediaNotificationAnim() 会 setOutlineProvider(null)，
                                          //     退出 AOD 后没有重新设置，provider 就一直为空。
                                          // ★ v105：撤销 v104 的「无条件接管 + 用 h 裁剪」。
                                          //   【主人反馈】「亮屏到 aod 路径无变化，同时锁屏和 aod 互切的
                                          //   动画失效，变回生硬的过渡」
                                          //   【为什么错】v104 用 v.getHeight() 裁剪，但动画期间布局高度
                                          //   不变（683），裁剪恒为 683 → 视觉上什么都不动；
                                          //   同时把原本靠 ah 插值产生平滑裁剪的两条正常路径也干掉了。
                                          //   【结论】官方 NotifiFullAodController$2 用 ah 裁剪是**正确设计**，
                                          //   ah 有插值时裁剪就平滑（锁屏↔AOD 就是这样）。
                                          //   亮屏→AOD 的问题是 ah **没有插值**（直接跳变），
                                          //   要去修 ah 的插值来源，而不是改裁剪公式。
                                          //   【修法 v106】每帧检测 provider，缺失就立刻补。
                                          //   【L95/L103 实证（v105 日志）】
                                          //     TL#201~219  ah 528→506 平滑（插值正常！）
                                          //                 但 op=（空）  mbg=1334x506
                                          //     L103 #1 ah=506 h=683  ← 我们补过一次
                                          //     但后续 op 全空 → 被清掉了
                                          //   【谁清的】ExpandableView.setExtClipBottomAmount(int):
                                          //       else { if (getOutlineProvider() == stacking)
                                          //                  setOutlineProvider(null); }
                                          //     + NotifiFullAodController.finishForMediaNotificationAnim():
                                          //           setOutlineProvider(null)
                                          //   两处都会清空 provider。
                                          //   【为什么锁屏↔AOD 正常】那条路径 KeyguardPanelViewController
                                          //     在动画开始前设了 NotifiFullAodController$2，
                                          //     且期间没有代码清它 → ah 插值 + provider 存在 = 平滑裁剪。
                                          //   【v106 修法】不再"只在 null 时补一次"，改成**每帧都检查**：
                                          //     只要 provider 不是我们自己的、且 ah≠h（正在裁剪动画），
                                          //     就立刻挂上等价 provider。这样即使被清，下一帧就补回来。
                                          try {
                                              android.view.ViewOutlineProvider op106 = hv95.getOutlineProvider();
                                              int ah106 = getIntFieldSafe(hv95, "mActualHeight");
                                              int h106 = hv95.getHeight();
                                              boolean ours106 = op106 != null
                                                      && op106.getClass().getName().startsWith("com.mersand.hyperclassic");
                                              if (!ours106 && ah106 > 0 && ah106 != h106) {
                                                      // 圆角半径：官方用 mMediaRadius（0x7f071522）。
                                                      // 我们从 resources 取同名字符串兜底，取不到就用 24dp。
                                                      final float rad103;
                                                      {
                                                          android.content.Context c103x = hv95.getContext();
                                                          int rid103 = c103x.getResources().getIdentifier(
                                                                  "miui_media_notification_radius", "dimen", "com.android.systemui");
                                                          rad103 = rid103 != 0
                                                                  ? c103x.getResources().getDimension(rid103)
                                                                  : 24f * c103x.getResources().getDisplayMetrics().density;
                                                      }
                                                      final java.lang.ref.WeakReference<View> wr103 =
                                                              new java.lang.ref.WeakReference<>(hv95);
                                                      android.view.ViewOutlineProvider prov103 =
                                                              new android.view.ViewOutlineProvider() {
                                                                  @Override
                                                                  public void getOutline(View v103, android.graphics.Outline o103) {
                                                                      try {
                                                                          // ★ v105：恢复用 mActualHeight 裁剪（官方设计）。
                                                                          //   ah 有 Folme 插值时裁剪就平滑（锁屏↔AOD 已验证正常）。
                                                                          View hv103 = wr103.get();
                                                                          int ahh103 = hv103 != null
                                                                                  ? getIntFieldSafe(hv103, "mActualHeight")
                                                                                  : v103.getHeight();
                                                                          if (ahh103 <= 0) ahh103 = v103.getHeight();
                                                                          o103.setRoundRect(0, 0, v103.getWidth(), ahh103, rad103);
                                                                          v103.setClipToOutline(true);
                                                                      } catch (Throwable ignored) {}
                                                                  }
                                                              };
                                                      hv95.setOutlineProvider(prov103);
                                                      hv95.setClipToOutline(true);
                                                      hv95.invalidateOutline();
                                                      int c103 = MEDIA_OUTLINE_FILL_CNT103.incrementAndGet();
                                                      if (c103 <= 12) L.log(4, TAG, "L106 outline refilled #" + c103
                                                              + " ah=" + ah106 + " h=" + h106 + " r=" + rad103);
                                              }
                                          } catch (Throwable t103) { L.log(5, TAG, "L103 fill err " + t103); }
// ★ v108：诊断——只在"ah 跳变"时记录完整状态。
                                          //   【为什么要跳变检测】L95 限流 220 条、L105 限流 60 条，
                                          //   中间大量帧没记录，看不到跳变瞬间的真实状态。
                                          //   【主人描述】「淡入动画结束后瞬间裁剪，然后播下沉动画，
                                          //   下沉后位置是对齐之后的 aod 与锁屏切换的」
                                          //   → 说明最终状态正确，中间多了一次"重复应用"。
                                          //   需要抓住那一帧的：ah / h / mbg / lp / op / 父容器高度 / seekBar。
                                          try {
                                              int ah108 = getIntFieldSafe(hv95, "mActualHeight");
                                              int last108 = (int) MEDIA_LAST_AH108.getAndSet(ah108);
                                              if (last108 > 0 && Math.abs(ah108 - last108) > 20) {
                                                  int c108 = MEDIA_JUMP_CNT108.incrementAndGet();
                                                  if (c108 <= 30) {
                                                      // ★ v113：加入 mEnableFullAod / mIsAnimating 真实值，
                                                      //   判断「AOD 状态是否真被清掉」还是「仅布局被推回」。
                                                      String flag113 = "-";
                                                      try {
                                                          Class<?> c113 = Class.forName(
                                                                  "com.android.systemui.statusbar.notification.fullaod.NotifiFullAodController",
                                                                  false, cl);
                                                          java.lang.reflect.Field fEnable113 = c113.getDeclaredField("mEnableFullAod");
                                                          java.lang.reflect.Field fAnim113 = c113.getDeclaredField("mIsAnimating");
                                                          fEnable113.setAccessible(true);
                                                          fAnim113.setAccessible(true);
                                                          Object o113 = fEnable113.get(null);
                                                          Object a113 = fAnim113.get(null);
                                                          flag113 = "enableAod=" + o113 + " isAnimating=" + a113;
                                                      } catch (Throwable ig113) {}
                                                      String mbg108 = "-", lp108 = "-", par108 = "-", sb108 = "-";
                                                      Object vh108 = invokeNoArg(hv95, "getMediaViewHolder");
                                                      if (vh108 != null) {
                                                          Object bg108 = vh108.getClass().getField("mediaBg").get(vh108);
                                                          if (bg108 instanceof View) {
                                                              View b108 = (View) bg108;
                                                              mbg108 = b108.getWidth() + "x" + b108.getHeight();
                                                              ViewGroup.LayoutParams l108 = b108.getLayoutParams();
                                                              lp108 = l108 == null ? "null" : String.valueOf(l108.height);
                                                              if (b108.getParent() instanceof View) {
                                                                  View p108 = (View) b108.getParent();
                                                                  par108 = p108.getClass().getSimpleName() + ":" + p108.getHeight();
                                                              }
                                                          }
                                                          Object s108 = vh108.getClass().getField("seekBar").get(vh108);
                                                          if (s108 instanceof View) {
                                                              sb108 = ((View) s108).getVisibility() == View.GONE ? "GONE" : "VIS";
                                                          }
                                                      }
                                                      L.log(4, TAG, "L108 JUMP ah " + last108 + "->" + ah108
                                                              + " h=" + hv95.getHeight()
                                                              + " mbg=" + mbg108 + " lp=" + lp108
                                                              + " par=" + par108 + " seek=" + sb108
                                                              + " " + flag113
                                                              + " op=" + (hv95.getOutlineProvider() == null ? "null"
                                                              : "set") + " #" + c108);
                                                  }
                                              }
                                          } catch (Throwable ignored) {}
                                          // ★ v109：核心修复——拦截 onFullAodEnableChanged(false) 的误触发。
                                          //   【L108 实证】亮屏→AOD 时出现三帧异常：
                                          //     L108 JUMP ah 506->683  h=683  par=683  seek=VIS  op=null
                                          //   ah 弹回 683 + 父容器 683 + seekBar 重新 VIS
                                          //   = 整个 AOD 状态被回滚了一次。
                                          //   【smali 实证·完整因果链】
                                          //     MiuiFullAodManager.updateFullAodEnable():
                                          //         boolean now = fullAodEnable();
                                          //         if (lastEnable != now) {
                                          //             for (l : mStateListeners) l.onFullAodEnableChanged(now);
                                          //             if (!now && isDozing) { ... }
                                          //         }
                                          //     KeyguardPanelViewController$fullAodBrightnessListener$1
                                          //         .onFullAodEnableChanged(false):
                                          //             notifiFullAodController.resetRowFullAodStyle();
                                          //             // 且把状态栏 alpha 设回 1f
                                          //     NotifiFullAodController.resetRowFullAodStyle():
                                          //         if (mEnableFullAod) {
                                          //             mEnableFullAod = false;   ← 强制清 0！
                                          //             ... 走"退出 AOD"分支：mbg=683、seek VIS
                                          //         }
                                          //   【结论】亮屏→AOD 过程中 fullAodEnable() 出现**瞬时 false**
                                          //     （dozing 状态切换的中间态），触发整条回滚链。
                                          //     之后真正进 AOD 又重设一次 → 视觉"瞬间裁剪后下沉"。
                                          //   【修法】hook onFullAodEnableChanged，
                                          //     若参数为 false 但短时间内（1200ms）刚发生过 true，
                                          //     判定为瞬时抖动，**跳过**这次回调（不进 resetRowFullAodStyle）。
                                          //   （hook 安装见方法外的 cc_l109_aod_enable_guard）
                                          // ★ v105：诊断——记录每次 setActualHeight 的调用来源。
                                          //   【目的】锁屏↔AOD 的 ah 有 Folme 逐帧插值（平滑），
                                          //   亮屏→AOD 的 ah 直接跳变（无插值）。
                                          //   查清楚这两条路径分别由谁调 setActualHeight，
                                          //   就能定位为什么亮屏→AOD 缺插值。
                                          try {
                                              int c105 = MEDIA_CALLER_CNT105.incrementAndGet();
                                              if (c105 <= 60) {
                                                  StackTraceElement[] st105 = Thread.currentThread().getStackTrace();
                                                  StringBuilder sb105 = new StringBuilder();
                                                  int added105 = 0;
                                                  for (StackTraceElement e105 : st105) {
                                                      String cn105 = e105.getClassName();
                                                      if (cn105.startsWith("com.mersand.hyperclassic")
                                                              || cn105.startsWith("java.lang.Thread")) continue;
                                                      if (cn105.startsWith("android.os.")) continue;
                                                      sb105.append(cn105.substring(cn105.lastIndexOf('.') + 1))
                                                              .append('#').append(e105.getMethodName()).append(" <- ");
                                                      if (++added105 >= 3) break;
                                                  }
                                                  int ah105 = getIntFieldSafe(hv95, "mActualHeight");
                                                  L.log(4, TAG, "L105 ah=" + ah105 + " h=" + hv95.getHeight()
                                                          + " via " + sb105);
                                              }
                                          } catch (Throwable ignored) {}
                                         // ★ v96：核心修复——让 mediaBg 高度每帧跟随 header 的 mActualHeight。
                                         //   【L95 时间线实证（进 AOD 路径）】
                                         //     TL#91  dt=6     ah=683  mbg=1334x678  lp=678  top=3
                                         //     TL#92  dt=4410  ah=683  mbg=1334x506  lp=506  top=0   ← 瞬间跳变！
                                         //     TL#93~#137      ah 683→506 平滑（46 帧 ≈350ms）但 mbg 恒为 506
                                         //   结论：header 高度有插值，mediaBg 高度是瞬设的（onFullAodStateChanged
                                         //   直接 setLayoutParams），两者脱节 → 背景瞬间缩小、卡片随后平滑，
                                         //   观感就是"多余空间被瞬间裁掉 + 卡片下沉"。
                                         //   【修法】在 setActualHeight 的每一帧把 mediaBg 的 LayoutParams.height
                                         //   同步为当前 mActualHeight，让背景与内容走同一条曲线。
                                         //   锁屏→息屏路径下 mbg 本来就和 ah 同步，此处同步不产生变化，安全。
int ahNow96 = CcHooks.getIntFieldSafe(hv95, "mActualHeight");
                                          // ★ v98：撤销 v96/v97 对 mediaBg 的所有写入！
                                          //   【主人反馈】「亮屏>AOD 应该是卡片缩小，而不是卡片形状不变并裁剪进度条」
                                          //   【结构实证】miui_media_session.xml：
                                          //     mi_media_controls (ConstraintLayout, wrap_content, clipChildren=true)
                                          //       ├── media_bg (0dp×0dp, 四边约束)   ← 只是背景层
                                          //       ├── album_art / icon / media_seamless
                                          //       ├── media_elapsed_time / media_total_time
                                          //       └── media_progress_bar             ← 进度条（media_bg 的兄弟）
                                          //   强行把 media_bg 高度同步成 header 高度，等于只缩放背景层，
                                          //   内容层（进度条等）仍在原位 → 背景盖不住 → "裁剪进度条"。
                                          //   【正确做法】media_bg 保持 0dp 约束驱动（自然铺满父容器），
                                          //   父容器 mi_media_controls 的 wrap_content 会跟随 header 高度。
                                          //   我们完全不碰 mediaBg 的 LayoutParams / translation / clip。
                                          if (ahNow96 < 0) { /* 仅保留读取，不做任何写入 */ }
                                      }
                                  } catch (Throwable t96) { /* v98: 同步逻辑已禁用 */ }
                                  try {
                                      Object self95 = chain.getThisObject();
                                      if (self95 instanceof View) {
                                          View hv95 = (View) self95;
                                          int n95 = MEDIA_TL_CNT95.incrementAndGet();
                                         if (n95 <= 220) {
                                             long now95 = System.currentTimeMillis();
                                             long last95 = MEDIA_TL_LAST95.getAndSet(now95);
                                             long dt95 = last95 == 0 ? 0 : (now95 - last95);
                                             int ah95 = CcHooks.getIntFieldSafe(hv95, "mActualHeight");
                                             int clipB95 = CcHooks.getIntFieldSafe(hv95, "mClipBottomAmount");
                                             int ext95 = CcHooks.getIntFieldSafe(hv95, "mExtClipBottomAmount");
                                             boolean cta95 = CcHooks.getBoolFieldSafe(hv95, "mClipToActualHeight");
                                             Object op95 = hv95.getOutlineProvider();
                                             String opName95 = op95 == null ? "null" : op95.getClass().getSimpleName();
                                             String mbg95 = "-";
                                             int mbgH95 = -1;
                                             int mbgLp95 = -999;
                                             int mbgTop95 = -1;
                                             try {
                                                 Object holder95 = CcHooks.invokeNoArg(hv95, "getMediaViewHolder");
                                                 if (holder95 != null) {
                                                     Object bg95 = holder95.getClass().getField("mediaBg").get(holder95);
                                                     if (bg95 instanceof View) {
                                                         View bv95 = (View) bg95;
                                                         mbgH95 = bv95.getHeight();
                                                         mbgTop95 = bv95.getTop();
                                                         ViewGroup.LayoutParams lp95 = bv95.getLayoutParams();
                                                         if (lp95 != null) mbgLp95 = lp95.height;
                                                         mbg95 = bv95.getWidth() + "x" + bv95.getHeight();
                                                     }
                                                 }
                                             } catch (Throwable ignored) {}
                                             L.log(4, TAG, "L95 TL#" + n95 + " dt=" + dt95
                                                     + " ah=" + ah95
                                                     + " clipB=" + clipB95 + " ext=" + ext95 + " cta=" + cta95
                                                     + " op=" + opName95
                                                     + " mbg=" + mbg95 + " lp=" + mbgLp95 + " top=" + mbgTop95);
                                         }
                                     }
                                 } catch (Throwable t95) { L.log(5, TAG, "L95 tl err " + t95); }
                                 return r95;
                             });
                     L.log(4, TAG, "L95 header timeline hook installed");
                 } catch (Throwable t95i) { L.log(5, TAG, "L95 install failed: " + t95i); }
                 // ★ v81：媒体卡片的 AOD / 锁屏 effect 单独接管——统一成反射玻璃。
                 //   【为什么】官方按 NotifEffectType 分发：
                 //     GLASS_VIEW_FULL_AOD   → MediaViewGlassFullAodEffect（磨砂）
                 //     GLASS_VIEW_ON_KEYGUARD→ MediaViewGlassOnKeyguardEffect（磨砂）
                 //   我们只在 onAttachedToWindow 时套过一次反射玻璃，之后官方 effect
                 //   apply 又把 mediaBg 改回磨砂 → 锁屏媒体卡片是磨砂的。
                 //   解法（同通知行 hookRowLikeGlass 的思路）：hook 这两个 effect 的 apply，
                 //   官方套完磨砂后立即换成反射玻璃。
                 //   【动画】只在 apply 完成后（动画起始态已定）再改材质，不碰 alpha/translation，
                 //   因此不会打断 AOD↔锁屏的展开/收起过渡。
// ★ v86：接管锁屏/AOD 专用的媒体 effect——之前只接了 2 个，
                  //   锁屏走的是 MediaViewBlurOnKeyguardEffect（磨砂），所以一直是磨砂玻璃。
                  //   【官方分发表（NotificationViewEffectHelper，smali 实证）】
                  //     BLUR                            → MediaViewBlurEffect          （通用，通知栏也用，不碰）
                  //     BLUR_MEDIA_ON_KEYGUARD          → MediaViewBlurOnKeyguardEffect ← 锁屏磨砂真身
                  //     GLASS                           → MediaViewGlassEffect          （通用，不碰）
                  //     GLASS_MEDIA_ON_KEYGUARD         → MediaViewGlassOnKeyguardEffect
                  //     GLASS_MEDIA_ON_KEYGUARD_LIGHT_WALL_PAPER → MediaViewGlassOnKeyguardLightWallPaperEffect
                  //     GLASS_MEDIA_FULL_AOD            → MediaViewGlassFullAodEffect
                  //   ★ 只接「_ON_KEYGUARD / _FULL_AOD」这 4 个专用类，
                  //     通用的 MediaViewBlurEffect / MediaViewGlassEffect 保持原样——
                  //     通知栏媒体卡片（主人确认正常）走的正是通用链路，接管会破坏它。
                  try {
                      String[] mediaEffectCls81 = new String[]{
                              "com.android.systemui.statusbar.notification.style.vieweffect.MediaViewBlurOnKeyguardEffect",
                              "com.android.systemui.statusbar.notification.style.vieweffect.MediaViewGlassOnKeyguardEffect",
                              "com.android.systemui.statusbar.notification.style.vieweffect.MediaViewGlassOnKeyguardLightWallPaperEffect",
                              "com.android.systemui.statusbar.notification.style.vieweffect.MediaViewGlassFullAodEffect"
                      };
                     int hooked81 = 0;
                     for (String cn81 : mediaEffectCls81) {
                         try {
                             Class<?> cls81 = Class.forName(cn81, false, cl);
                             Method ap81 = CcHooks.findApply2(cls81);
                             if (ap81 == null) { L.log(5, TAG, "L81 no apply on " + cn81); continue; }
                             ap81.setAccessible(true);
                             final String short81 = cn81.substring(cn81.lastIndexOf('.') + 1);
                             module.hook((Executable) ap81).setId("cc_l81_media_" + short81)
                                     .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                                     .intercept(chain -> {
Object r81 = chain.proceed();
                                          try {
                                              Object header81 = chain.getArgs().toArray()[0];
                                              if (header81 != null) {
                                                  Object holder81 = CcHooks.invokeNoArg(header81, "getMediaViewHolder");
                                                  if (holder81 != null) {
                                                      Object bg81 = holder81.getClass().getField("mediaBg").get(holder81);
                                                      if (bg81 instanceof View) {
                                                          final View mb81 = (View) bg81;
                                                          final String ftag81 = "L81 " + short81;
                                                          // ★ v83：官方 effect 的 apply 已在 chain.proceed() 里跑完
                                                          //   （MediaViewBlurEffect 会 setBackground 0x7f08184c）。
                                                          //   我们只补材质，且必须等 Folme 过渡动画结束——
                                                          //   否则每次进 AOD/锁屏都会被自愈打断，表现为高度瞬切。
                                                          //   applyMediaGlassWhenIdle82 内部已带 isViewAnimating 轮询，
                                                          //   并在 AOD/锁屏态只走 material-only（不碰几何）。
                                                          mb81.post(() -> {
                                                              try {
                                                                  CcHooks.applyMediaGlassWhenIdle82(module, mb81, cl, ftag81, 0);
                                                                  int n81 = MEDIA_EFFECT_CNT81.incrementAndGet();
                                                                  if (n81 <= 8) L.log(4, TAG, ftag81 + " scheduled #" + n81 + " " + mb81.getWidth() + "x" + mb81.getHeight());
                                                              } catch (Throwable ignored) {}
                                                          });
                                                      }
                                                  }
                                              }
                                          } catch (Throwable t81) { L.log(5, TAG, "L81 apply err " + t81); }
                                          return r81;
                                     });
                             ++hooked81;
                         } catch (Throwable e81) { L.log(5, TAG, "L81 hook failed " + cn81 + ": " + e81); }
                     }
                     L.log(4, TAG, "L81 media AOD/keyguard reflective hooked=" + hooked81);
                 } catch (Throwable t81o) { L.log(5, TAG, "L81 install failed: " + t81o); }
                //   绑定时 LAYERFIX 采样恒为亮蓝，但屏幕显示暗色 → 在 View.draw 时刻再采一次：
                //   若绘制时 drawable 已变暗 → 有二次覆盖（Master 假说成立，栈里就是真凶）；
                //   若绘制时仍是亮蓝 → 颜色在渲染管线被调（玻璃代理）。
                //   每 500ms 最多记一条，附带坐标/isShown/drawable hash/alpha/颜色过滤器。
// ★ release：DRAWSAMPLE 探针 + DSFIX 兜底默认禁用（v58 源头方案已覆盖其功能，
                 //   debug.hyperclassic.probes=1 可重开兜底与采样日志）
                 if (PROBES_ON) try {
                     java.lang.reflect.Method dm44 = View.class.getDeclaredMethod("draw", android.graphics.Canvas.class);
                    module.hook((Executable)dm44).setId("cc_l44_drawsample").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                        try {
                            long now44 = System.currentTimeMillis();
                            // ★ v47 修正：限流 return 只影响日志，不影响修复！
                            //   v46 把限流放在修复之前 → 全局每 500ms 只修一个磁贴（所以只有 wifi 变蓝）。
                            boolean logGate47 = (now44 - LAST_DRAWSAMPLE.get() >= 500);
                            Object self44 = chain.getThisObject();
                            if (!(self44 instanceof ImageView)) return chain.proceed();
                            ImageView iv44 = (ImageView) self44;
                            // ★ 修正：真正绘制的是 MiuiQSIconViewImpl 的子 ImageView（不是它本身），
                            //   类名过滤改为父链检查
                            boolean isQsIcon44 = false;
                            Object pp44 = iv44.getParent();
                            int gd44 = 0;
                            while (pp44 instanceof android.view.View && gd44 < 4) {
                                if (pp44.getClass().getSimpleName().contains("MiuiQSIconViewImpl")) { isQsIcon44 = true; break; }
                                pp44 = ((android.view.View) pp44).getParent();
                                ++gd44;
                            }
                            if (!isQsIcon44) return chain.proceed();
                            // ★ v46 反击：绘制瞬间发现暗蓝 → 当场 setTint 亮蓝。
                            //   v45 已实锤：绑定后 drawable 被二次替换为 night 配置重载的暗蓝实例
                            //   （绑定采样 0xff0099ff → 绘制采样 0xff0077cc，dh 不同）。
                            //   在 draw 里持续检查并修复，替换者改多少次我们就修多少次。
                            // ★ v47：每次 draw 都执行修复（去掉日志限流对修复的拦截）。
                            // ★ v48：逐层检查——2 层路径蓝底在 L0，3 层路径蓝底在 L1。
                            // ★ v49 性能重构：SEEN_DRAWABLES 实例记忆。
                            // ★ v50 修正：替换者有两种手法——①换新实例（v49 能抓）②对同一实例
                            //   原地 setTint 暗色（v49 的 seen 记忆放过了它！深浅色切换后磁贴停在
                            //   暗色、滑动才恢复即此）。策略：FIXED_DRAWABLES 记录我们修复过的实例，
                            //   这类实例每 300ms 重采样一次（数量仅几个磁贴，开销可忽略）；
                            //   未修复过的实例维持 seen 直通。
                            android.graphics.drawable.Drawable d44 = iv44.getDrawable();
                            if (d44 == null) return chain.proceed();
                            int dhKey50 = System.identityHashCode(d44);
                            boolean seen50 = SEEN_DRAWABLES.contains(Integer.valueOf(dhKey50));
                            boolean fixed50 = FIXED_DRAWABLES.containsKey(d44);
                            long now50 = System.currentTimeMillis();
                            boolean doCheck50;
                            if (!seen50) {
                                SEEN_DRAWABLES.add(Integer.valueOf(dhKey50));
                                doCheck50 = true;
                                                        } else if (fixed50) {
                                // ★ v52：重采样时间戳从全局改为按实例记录——v51 的全局
                                //   LAST_RESAMPLE 被第一个磁贴消耗后其它磁贴全部跳过 300ms 窗口，
                                //   导致深浅色切换后只有零星磁贴被修。
                                Long lastTs = FIXED_RESAMPLE_TS.get(d44);
                                doCheck50 = (lastTs == null) || (now50 - lastTs >= 300);
                                if (doCheck50) FIXED_RESAMPLE_TS.put(d44, Long.valueOf(now50));
                            } else {
                                doCheck50 = false;
                            }
                            android.graphics.drawable.LayerDrawable ld44 = (d44 instanceof android.graphics.drawable.LayerDrawable) ? (android.graphics.drawable.LayerDrawable) d44 : null;
                            if (doCheck50) {
                            int[] locFix = new int[2];
                            iv44.getLocationOnScreen(locFix);
                            int cnt49 = (ld44 != null) ? Math.min(ld44.getNumberOfLayers(), 4) : 1;
                            for (int li48 = 0; li48 < cnt49; ++li48) {
                                try {
                                    Drawable ly48 = (ld44 != null) ? ld44.getDrawable(li48) : d44;
                                    if (ly48 == null) continue;
                                    android.graphics.Bitmap bp48 = android.graphics.Bitmap.createBitmap(4, 4, android.graphics.Bitmap.Config.ARGB_8888);
                                    android.graphics.Canvas cp48 = new android.graphics.Canvas(bp48);
                                    android.graphics.Rect rp48 = ly48.copyBounds();
                                    ly48.setBounds(0, 0, 4, 4);
                                    ly48.draw(cp48);
                                    ly48.setBounds(rp48);
                                    int px48 = bp48.getPixel(2, 2);
                                    bp48.recycle();
                                    int rr48 = (px48 >> 16) & 0xFF, gg48 = (px48 >> 8) & 0xFF, bb48 = px48 & 0xFF;
                                    boolean darkBlue48 = ((px48 >>> 24) > 0x80) && (bb48 > 0x60) && (bb48 > rr48 + 0x20) && (bb48 > gg48 + 0x20)
                                            && px48 != -16737793; // 不是 0xFF0099FF 本身
                                                                        if (darkBlue48) {
                                        // ★ v53：setTint 对 fill=暗色的 VectorDrawable 无效（tint 只是叠加，
                                        //   fill 本色不变）——直接物理替换该层为全新的亮蓝圆形！
                                        android.graphics.drawable.GradientDrawable ngd53 = new android.graphics.drawable.GradientDrawable();
                                        ngd53.setShape(android.graphics.drawable.GradientDrawable.OVAL);
                                        ngd53.setColor(-16737793); // 0xFF0099FF
                                        boolean replaced53 = false;
                                        try {
                                            if (ld44 != null) {
                                                ld44.setDrawable(li48, ngd53);
                                                replaced53 = true;
                                            }
                                        } catch (Throwable tr53a) { /* 某些实现不支持 setDrawable */ }
                                        // ★ v55：draw 流程内的 invalidate() 不可靠——改 postInvalidate() 排队下一帧。
                                        iv44.postInvalidate();
                                        // ★ v56：ld44 本身登记进 FIXED_DRAWABLES——否则后续 draw 的
                                        //   fixed50=false → doCheck50=false，永不复查，系统再覆盖就没人管了。
                                        FIXED_DRAWABLES.put(ld44 != null ? ld44 : d44, Boolean.TRUE);
                                        FIXED_DRAWABLES.put(ngd53, Boolean.TRUE);
                                        if (COLOR_SEEN.add("dsfix:" + System.identityHashCode(ly48))) {
                                            L.log(4, TAG, "L22 DSFIX loc=[" + locFix[0] + ',' + locFix[1] + "] dh="
                                                    + Integer.toHexString(dhKey50)
                                                    + " L" + li48 + " 0x" + Integer.toHexString(px48)
                                                    + " -> REPLACED with bright OVAL (replaced=" + replaced53 + ")");
                                        }
                                    }
                                    // ★ v54 白色图标统一：启用态磁贴的图标层若不是纯白 → setTint 白。
                                    //   官方 updateIcon 对图标层 setTint(mIconColorEnabled) 且屏幕认账
                                    //   （禁用灰图标就是这条路），所以图标 tint 是官方有效路径。
                                    if (ld44 != null && (darkBlue48 || px48 == -16737793)) {
                                        int iconIdx54 = ld44.getNumberOfLayers() - 1;
                                        if (iconIdx54 != li48 && iconIdx54 >= 0) {
                                            try {
                                                Drawable ic54 = ld44.getDrawable(iconIdx54);
                                                if (ic54 != null) {
                                                    android.graphics.Bitmap bi54 = android.graphics.Bitmap.createBitmap(16, 16, android.graphics.Bitmap.Config.ARGB_8888);
                                                    android.graphics.Canvas ci54 = new android.graphics.Canvas(bi54);
                                                    android.graphics.Rect ri54 = ic54.copyBounds();
                                                    ic54.setBounds(0, 0, 16, 16);
                                                    ic54.draw(ci54);
                                                    ic54.setBounds(ri54);
                                                    int bestA = 0, bestC = 0;
                                                    for (int yy = 2; yy < 14; ++yy) {
                                                        for (int xx = 2; xx < 14; ++xx) {
                                                            int pc = bi54.getPixel(xx, yy);
                                                            int pa = (pc >>> 24);
                                                            if (pa > bestA) { bestA = pa; bestC = pc; }
                                                        }
                                                    }
                                                    bi54.recycle();
                                                    // ★ v63 撤销图标染白（ICONFIX62）：官方模式下图标着色本就统一，
                                                    //   部分修复反而制造不统一；保持官方原样。蓝底修复（DSFIX）保留。
                                                }
                                            } catch (Throwable ti54) { /* 静默 */ }
                                        }
                                    }
                                } catch (Throwable tf48) { /* 静默 */ }
                            }
                            // ★ v47：修复块闭合，日志段用 logGate47 门控，禁止提前 return（会跳过真实绘制！）
                            }
                            if (logGate47) {
                            LAST_DRAWSAMPLE.set(now44);
                            StringBuilder sb44 = new StringBuilder("L22 DRAWSAMPLE layers=");
                            sb44.append(ld44.getNumberOfLayers());
                            for (int i44 = 0; i44 < ld44.getNumberOfLayers() && i44 < 3; ++i44) {
                                try {
                                    android.graphics.drawable.Drawable ly44 = ld44.getDrawable(i44);
                                    android.graphics.Bitmap bm44 = android.graphics.Bitmap.createBitmap(6, 6, android.graphics.Bitmap.Config.ARGB_8888);
                                    android.graphics.Canvas c44 = new android.graphics.Canvas(bm44);
                                    android.graphics.Rect rr44 = ly44.copyBounds();
                                    ly44.setBounds(0, 0, 6, 6);
                                    ly44.draw(c44);
                                    ly44.setBounds(rr44);
                                    int px44 = bm44.getPixel(3, 3);
                                    bm44.recycle();
                                    sb44.append(" L").append(i44).append("=0x").append(Integer.toHexString(px44));
                                } catch (Throwable tl44) { sb44.append(" L").append(i44).append("=err"); }
                            }
                            int[] loc44 = new int[2];
                            iv44.getLocationOnScreen(loc44);
                            sb44.append(" loc=[").append(loc44[0]).append(',').append(loc44[1]).append(']');
                            sb44.append(" shown=").append(iv44.isShown());
                            sb44.append(" dh=").append(Integer.toHexString(System.identityHashCode(d44)));
                            sb44.append(" vAlpha=").append(iv44.getAlpha()).append(" imgAlpha=").append(iv44.getImageAlpha());
                            sb44.append(" hasCF=").append(iv44.getColorFilter() != null);
                            L.log(4, TAG, sb44.toString());
                            } // end logGate47
                        } catch (Throwable t44) { /* 静默 */ }
                        return chain.proceed();
                    });
                    ++hooked;
                    L.log(4, TAG, "L22 DRAWSAMPLE hook installed");
                } catch (Throwable t44b) { L.log(5, TAG, "L22 DRAWSAMPLE install failed: " + t44b); }
                 // ★ v63 撤销 v57 UPDATEICON hook（mIconColorEnabled 钉白会改图标色，
                 //   与「保持官方原样」方针冲突；且蓝底已在 v58 loadDrawable 源头解决）
                 // ★ v57 终极总闸：hook MiuiQSIconViewImpl.updateIcon——磁贴状态变化（含开关）、
                 //   初次绑定必经之路。smali 实证：
                 //   - 图标色 = mIconColorEnabled 字段（构造函数/updateResources 用
                 //     Resources.getColor(0x7f060c96) 赋值），updateIcon 内 state==2 时
                 //     iget 后 Drawable.setTint(I)；
                 //   - 方法后（官方刚绑好新 drawable）立刻检查修复——在 draw 之外，
                 //     postInvalidate 必然排队下一帧，开关磁贴后不再停暗色。
                 /* v63 已撤销：
                 try {
                     Class<?> qiv57 = cl.loadClass("com.android.systemui.qs.tileimpl.MiuiQSIconViewImpl");
                     for (java.lang.reflect.Method mm57 : qiv57.getDeclaredMethods()) {
                         if (!"updateIcon".equals(mm57.getName())) continue;
                         final Class<?> fqiv57 = qiv57;
                         module.hook((Executable) mm57).setId("cc_l57_updateicon").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                             Object self57 = chain.getThisObject();
                             try {
                                 // 方法前：把 enabled 图标色钉成纯白（官方随后自己 setTint 就是白）
                                 java.lang.reflect.Field fe57 = fqiv57.getField("mIconColorEnabled");
                                 fe57.setInt(self57, 0xFFFFFFFF);
                             } catch (Throwable tf57a) { /* 静默 * }
                             Object rr57 = chain.proceed();
                             try {
                                 // 方法后：官方刚 setImageDrawable 的新 drawable 立即体检+修复
                                 java.lang.reflect.Field fiv57 = fqiv57.getField("mIcon");
                                 View iv57 = (View) fiv57.get(self57);
                                 if (iv57 instanceof ImageView) {
                                     ImageView ivx57 = (ImageView) iv57;
                                     Drawable d57 = ivx57.getDrawable();
                                     if (d57 instanceof android.graphics.drawable.LayerDrawable) {
                                         android.graphics.drawable.LayerDrawable ld57 = (android.graphics.drawable.LayerDrawable) d57;
                                         int n57 = ld57.getNumberOfLayers();
                                         // 背景层（L0 或 L1，最后一个之前的层）逐个查暗蓝
                                         for (int bi57 = 0; bi57 < n57 - 1; ++bi57) {
                                             Drawable ly57 = ld57.getDrawable(bi57);
                                             if (ly57 == null) continue;
                                             android.graphics.Bitmap bp57 = android.graphics.Bitmap.createBitmap(4, 4, android.graphics.Bitmap.Config.ARGB_8888);
                                             android.graphics.Canvas cp57 = new android.graphics.Canvas(bp57);
                                             android.graphics.Rect rp57 = ly57.copyBounds();
                                             ly57.setBounds(0, 0, 4, 4);
                                             ly57.draw(cp57);
                                             ly57.setBounds(rp57);
                                             int px57 = bp57.getPixel(2, 2);
                                             bp57.recycle();
                                             int bb57 = px57 & 0xFF, gg57 = (px57 >> 8) & 0xFF, rr57c = (px57 >> 16) & 0xFF;
                                             boolean dark57 = ((px57 >>> 24) > 0x80) && (bb57 > 0x60) && (bb57 > rr57c + 0x20) && (bb57 > gg57 + 0x20) && px57 != -16737793;
                                             if (dark57) {
                                                 android.graphics.drawable.GradientDrawable ngd57 = new android.graphics.drawable.GradientDrawable();
                                                 ngd57.setShape(android.graphics.drawable.GradientDrawable.OVAL);
                                                 ngd57.setColor(-16737793);
                                                 ld57.setDrawable(bi57, ngd57);
                                                 FIXED_DRAWABLES.put(ld57, Boolean.TRUE);
                                             }
                                         }
                                         // 图标层（最后一层）：启用态（背景是蓝色系）时若偏灰→染白
                                         if (n57 >= 2) {
                                             Drawable ic57 = ld57.getDrawable(n57 - 1);
                                             if (ic57 != null) {
                                                 android.graphics.Bitmap bp57b = android.graphics.Bitmap.createBitmap(16, 16, android.graphics.Bitmap.Config.ARGB_8888);
                                                 android.graphics.Canvas cp57b = new android.graphics.Canvas(bp57b);
                                                 android.graphics.Rect rp57b = ic57.copyBounds();
                                                 ic57.setBounds(0, 0, 16, 16);
                                                 ic57.draw(cp57b);
                                                 ic57.setBounds(rp57b);
                                                 int bestA57 = 0;
                                                 for (int yy57 = 2; yy57 < 14; ++yy57)
                                                     for (int xx57 = 2; xx57 < 14; ++xx57) {
                                                         int pa57 = (bp57b.getPixel(xx57, yy57) >>> 24);
                                                         if (pa57 > bestA57) bestA57 = pa57;
                                                     }
                                                 bp57b.recycle();
                                                 if (bestA57 > 0x40 && bestA57 < 0xFF) {
                                                     ic57.setTint(0xFFFFFFFF);
                                                     ic57.setAlpha(255);
                                                 }
                                             }
                                         }
                                         ivx57.postInvalidate();
                                     }
                                 }
                             } catch (Throwable tf57b) { /* 静默 * }
                             return rr57;
                         });
                         ++hooked;
                         L.log(4, TAG, "L22 UPDATEICON hook installed: " + mm57);
                         break;
                     }
                 } catch (Throwable t57) { L.log(5, TAG, "L22 UPDATEICON install failed: " + t57); }
                 v63 撤销结束 */
                // ★ v51 根源歼灭：night 暗蓝是资源解析出来的（fillColor=@color/qs_tile_icon_bg_enabled_color
                //   的 night 变体 #ff0077cc）。深浅色切换时 ResourcesImpl 缓存清空重新解析，
                //   替换者拿到的就是暗色矢量，且重载后磁贴可能不再 draw → 绘制哨兵永远修不到。
                //   对策：hook ResourcesImpl.loadColorStateList，命中 0x7f060c93 一律返回
                //   亮蓝 0xFF0099FF 的 ColorStateList——任何 uiMode、任何时刻 inflate 出来的
                //   磁贴蓝底层都是亮的。
                try {
                    Class<?> implCls51 = cl.loadClass("android.content.res.ResourcesImpl");
                    for (java.lang.reflect.Method lm51 : implCls51.getDeclaredMethods()) {
                        if (!"loadColorStateList".equals(lm51.getName())) continue;
                        boolean hasTv51 = false;
                        for (Class<?> pt51 : lm51.getParameterTypes()) {
                            if (pt51 == android.util.TypedValue.class) { hasTv51 = true; break; }
                        }
                        if (!hasTv51) continue;
                        module.hook((Executable)lm51).setId("cc_l51_loadcsl").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                            try {
                                for (Object ao51 : chain.getArgs()) {
                                    if (ao51 instanceof Integer && (Integer) ao51 == 0x7f060c93) {
                                        int[][] states51 = new int[][]{new int[0]};
                                        int[] colors51 = new int[]{-16737793}; // 0xFF0099FF
                                        if (COLOR_SEEN.add("csl51")) {
                                            L.log(4, TAG, "L22 CSL51 0x7f060c93 -> forced 0xFF0099FF ColorStateList");
                                        }
                                        return new android.content.res.ColorStateList(states51, colors51);
                                    }
                                }
                            } catch (Throwable t51) { L.log(5, TAG, "L22 CSL51 err " + t51); }
                            return chain.proceed();
                        });
                        ++hooked;
                        L.log(4, TAG, "L22 CSL51 hook installed");
                    }
                } catch (Throwable t51b) { L.log(5, TAG, "L22 CSL51 install failed: " + t51b); }
                // ★ v1.0.3u：蓝圆底在 MiuiLaunchableFrameLayout 上，且 updateIcon 时 bg=null，
                //   说明背景是稍后由别的代码设置的。hook View.setBackground + GradientDrawable.setColor
                //   抓设置者（只记录 MiuiLaunchableFrameLayout 相关的调用）。
                try {
                    Class<?> viewCls2 = cl.loadClass("android.view.View");
                    for (java.lang.reflect.Method sb : viewCls2.getDeclaredMethods()) {
                        if (!"setBackground".equals(sb.getName()) || sb.getParameterTypes().length != 1) continue;
module.hook((Executable)sb).setId("cc_l22_setbg").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                             try {
                                 Object self = chain.getThisObject();
                                 if (self != null && self.getClass().getSimpleName().contains("MiuiLaunchableFrameLayout")) {
                                     // ★ v139：官方在 onAttachedToWindow / handleStateChanged 里会反复
                                     //   setBackground(#1AFFFFFF)，把已挂好的玻璃重新铺上一层白底 → 变浅。
                                     //   【L22 SETBG 调用栈实证】
                                     //     TrierkZairk.setBackground(...)              ← MiuiLaunchableFrameLayout
                                     //       at TrierkZairk.onAttachedToWindow(...)
                                     //       at MiuiQSTileBaseView.onAttachedToWindow(...)
                                     //       at MiuiQSTileView.onAttachedToWindow(...)
                                     //       at View.dispatchAttachedToWindow(...)
                                     //   【为什么第一页统一、第二页有深有浅】
                                     //     第一页的 iconFrame 早已 attach 完毕、玻璃稳定；
                                     //     第二页是滚动复用时才 attach，官方 setBackground 与我们的
                                     //     applyTileGlass 交错执行 —— 谁的 setBackground 后到谁决定外观。
                                     //   【修法】已套过玻璃（TILE_BLEND_SEEN）且官方这次铺的是白色底
                                     //     （非启用态蓝色）→ 跳过，保住玻璃。蓝色仍让官方生效。
                                     if (TILE_BLEND_SEEN.contains(self)) {
                                         boolean isBlue139 = false;
                                         try {
                                             Object a139 = chain.getArgs().get(0);
                                             if (a139 instanceof android.graphics.drawable.Drawable) {
                                                 android.graphics.drawable.Drawable dd139 = (android.graphics.drawable.Drawable) a139;
                                                 android.graphics.Bitmap bb139 = android.graphics.Bitmap.createBitmap(8, 8, android.graphics.Bitmap.Config.ARGB_8888);
                                                 android.graphics.Canvas cc139 = new android.graphics.Canvas(bb139);
                                                 android.graphics.Rect ob139 = dd139.copyBounds();
                                                 dd139.setBounds(0, 0, 8, 8);
                                                 dd139.draw(cc139);
                                                 dd139.setBounds(ob139);
                                                 int s139 = bb139.getPixel(4, 4);
                                                 bb139.recycle();
                                                 int rr139 = (s139 >> 16) & 0xFF, gg139 = (s139 >> 8) & 0xFF, bl139 = s139 & 0xFF;
                                                 isBlue139 = ((s139 >>> 24) > 0x80) && (bl139 > 0x60)
                                                         && (bl139 > rr139 + 0x20) && (bl139 > gg139 + 0x20);
                                             }
                                         } catch (Throwable ignored) {}
                                         if (!isBlue139) {
                                             int n139 = TILE_BG_BLOCK_CNT139.incrementAndGet();
                                             if (n139 <= 8 || n139 % 30 == 0)
                                                 L.log(4, TAG, "L139 skip official setBackground on glass tile #" + n139);
                                             return null;
                                         }
                                     }
                                     Object arg = chain.getArgs().get(0);
                                    String dk = arg == null ? "null" : arg.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(arg));
                                    int sampled = 0;
                                    // ★ v1.0.3v 修复：背景由 handleStateChanged 设置，这里是唯一写入点。
                                    //   采样它的实际渲染色，若是蓝色系（启用态）→ 统一改成亮蓝 0xFF0099FF。
                                    if (arg instanceof android.graphics.drawable.Drawable) {
                                        android.graphics.drawable.Drawable dd = (android.graphics.drawable.Drawable) arg;
                                        boolean isBlue = false;
                                        try {
                                            android.graphics.Bitmap bb = android.graphics.Bitmap.createBitmap(8, 8, android.graphics.Bitmap.Config.ARGB_8888);
                                            android.graphics.Canvas cc3 = new android.graphics.Canvas(bb);
                                            android.graphics.Rect ob3 = dd.copyBounds();
                                            dd.setBounds(0, 0, 8, 8);
                                            dd.draw(cc3);
                                            dd.setBounds(ob3);
                                            sampled = bb.getPixel(4, 4);
                                            bb.recycle();
                                            int rr = (sampled >> 16) & 0xFF, gg = (sampled >> 8) & 0xFF, bbb = sampled & 0xFF;
                                            // 蓝色系判定：蓝通道明显高于红绿，且不透明
                                            isBlue = ((sampled >>> 24) > 0x80) && (bbb > 0x60) && (bbb > rr + 0x20) && (bbb > gg + 0x20);
                                        } catch (Throwable t35) { /* 采样失败 */ }
                                        if (isBlue && arg instanceof android.graphics.drawable.GradientDrawable) {
                                            android.graphics.drawable.GradientDrawable gd2 = (android.graphics.drawable.GradientDrawable) dd.mutate();
                                            gd2.setColor(0xFF0099FF);
                                            if (COLOR_SEEN.add("tilefix:" + System.identityHashCode(self))) {
                                                L.log(4, TAG, "L22 TILEFIX [" + dk + "] sampled=0x" + String.format("%08X", sampled) + " -> 0xFF0099FF");
                                            }
                                            return chain.proceed(new Object[]{gd2});
                                        }
                                    }
                                    // ★ v1.0.3x：二分测试确认 L1 background 就是屏幕圆底（动作型磁贴变品红）。
                                    //   状态型磁贴的蓝色来自 View.backgroundTintList（drawable 本身是白色 0x1AFFFFFF）。
                                    //   这里不再无条件刷色，只记录渲染色。
                                    if (COLOR_SEEN.add("setbg:" + System.identityHashCode(self) + ":" + dk)) {
                                        java.io.StringWriter sw = new java.io.StringWriter();
                                        new Throwable("who").printStackTrace(new java.io.PrintWriter(sw));
                                        String[] lines = sw.toString().split("\n");
                                        StringBuilder st = new StringBuilder();
                                        int taken = 0;
                                        for (int li = 1; li < lines.length && taken < 8; ++li) {
                                            String ln = lines[li].trim();
                                            if (ln.contains("CcHooks") || ln.contains("D8$$") || ln.contains("r8-map") || ln.contains("LSPosed")) continue;
                                            st.append(ln).append(" | ");
                                            ++taken;
                                        }
                                        L.log(4, TAG, "L22 SETBG [" + dk + "] sampled=0x" + String.format("%08X", sampled) + " stack=" + st);
                                    }
                                }
                            } catch (Throwable t33) { /* 静默 */ }
                            return chain.proceed();
                        });
                        ++hooked;
                    }
Class<?> gdCls = cl.loadClass("android.graphics.drawable.GradientDrawable");
                    java.lang.reflect.Method sc = gdCls.getMethod("setColor", Integer.TYPE);
                    module.hook((Executable)sc).setId("cc_l22_gd_setcolor").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                        try {
                            Object color = chain.getArgs().get(0);
                            if (color instanceof Integer) {
                                int cv = (Integer) color;
                                // 只关心接近官方磁贴蓝/灰的写入
                                int rgb = cv & 0x00FFFFFF;
                                boolean interesting = ((cv >>> 24) > 0) && (rgb == 0x0077CC || rgb == 0x0099FF || rgb == 0xFFFFFF || rgb == 0x5C5D5E);
                                if (interesting && COLOR_SEEN.add("gdset:" + String.format("%08X", cv))) {
                                    java.io.StringWriter sw = new java.io.StringWriter();
                                    new Throwable("who").printStackTrace(new java.io.PrintWriter(sw));
                                    String[] lines = sw.toString().split("\n");
                                    StringBuilder st = new StringBuilder();
                                    int taken = 0;
                                    for (int li = 1; li < lines.length && taken < 8; ++li) {
                                        String ln = lines[li].trim();
                                        if (ln.contains("CcHooks") || ln.contains("D8$$") || ln.contains("r8-map") || ln.contains("LSPosed")) continue;
                                        st.append(ln).append(" | ");
                                        ++taken;
                                    }
                                    L.log(4, TAG, "L22 GDSETCOLOR 0x" + String.format("%08X", cv) + " stack=" + st);
                                }
                            }
                        } catch (Throwable t34) { /* 静默 */ }
                        return chain.proceed();
                    });
                    ++hooked;
                    // ★ v1.0.3x 核心修复：状态型磁贴的蓝圆底 = View.backgroundTintList（drawable 本身是白色）。
                                    //   拦截 MiuiLaunchableFrameLayout 的 tint 设置，蓝色系统一改为亮蓝 0xFF0099FF。
                                    for (java.lang.reflect.Method st : viewCls2.getDeclaredMethods()) {
                                        if (!"setBackgroundTintList".equals(st.getName()) || st.getParameterTypes().length != 1) continue;
                                        module.hook((Executable)st).setId("cc_l22_bgtint").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                                            try {
                                                Object self = chain.getThisObject();
                                                if (self != null && self.getClass().getSimpleName().contains("MiuiLaunchableFrameLayout")) {
                                                    Object arg = chain.getArgs().get(0);
                                                    if (arg instanceof android.content.res.ColorStateList) {
                                                        android.content.res.ColorStateList csl = (android.content.res.ColorStateList) arg;
                                                        int def = csl.getDefaultColor();
                                                        int rr = (def >> 16) & 0xFF, gg = (def >> 8) & 0xFF, bbb = def & 0xFF;
                                                        boolean isBlue = (bbb > 0x60) && (bbb > rr + 0x20) && (bbb > gg + 0x20);
                                                        if (COLOR_SEEN.add("bgtint:" + Integer.toHexString(def))) {
                                                            L.log(4, TAG, "L22 BGTINT 0x" + String.format("%08X", def) + " blue=" + isBlue);
                                                        }
                                                        if (isBlue) {
                                                            return chain.proceed(new Object[]{android.content.res.ColorStateList.valueOf(0xFF0099FF)});
                                                        }
                                                    }
                                                }
                                            } catch (Throwable t38) { /* 静默 */ }
                                            return chain.proceed();
                                        });
                                        ++hooked;
                                    }
                    L.log(4, TAG, "L22 BGTINT hooks installed");
                    // ★ v1.0.3v：蓝圆底可能在前景 TintDrawable 上，hook setForeground 抓设置者+采样
                    for (java.lang.reflect.Method sf : viewCls2.getDeclaredMethods()) {
                        if (!"setForeground".equals(sf.getName()) || sf.getParameterTypes().length != 1) continue;
                        module.hook((Executable)sf).setId("cc_l22_setfg").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                            try {
                                Object self = chain.getThisObject();
                                if (self != null && self.getClass().getSimpleName().contains("MiuiLaunchableFrameLayout")) {
                                    Object arg = chain.getArgs().get(0);
                                    if (arg instanceof android.graphics.drawable.Drawable) {
                                        android.graphics.drawable.Drawable dd = (android.graphics.drawable.Drawable) arg;
                                        int sampled = 0;
                                        try {
                                            android.graphics.Bitmap bb = android.graphics.Bitmap.createBitmap(8, 8, android.graphics.Bitmap.Config.ARGB_8888);
                                            android.graphics.Canvas cc4 = new android.graphics.Canvas(bb);
                                            android.graphics.Rect ob4 = dd.copyBounds();
                                            dd.setBounds(0, 0, 8, 8);
                                            dd.draw(cc4);
                                            dd.setBounds(ob4);
                                            sampled = bb.getPixel(4, 4);
                                            bb.recycle();
                                        } catch (Throwable t36) { /* 采样失败 */ }
                                        if (COLOR_SEEN.add("setfg:" + System.identityHashCode(self) + ":" + Integer.toHexString(sampled))) {
                                            java.io.StringWriter sw = new java.io.StringWriter();
                                            new Throwable("who").printStackTrace(new java.io.PrintWriter(sw));
                                            String[] lines = sw.toString().split("\n");
                                            StringBuilder st = new StringBuilder();
                                            int taken = 0;
                                            for (int li = 1; li < lines.length && taken < 8; ++li) {
                                                String ln = lines[li].trim();
                                                if (ln.contains("CcHooks") || ln.contains("D8$$") || ln.contains("r8-map") || ln.contains("LSPosed")) continue;
                                                st.append(ln).append(" | ");
                                                ++taken;
                                            }
                                            L.log(4, TAG, "L22 SETFG cls=" + dd.getClass().getSimpleName() + " sampled=0x" + String.format("%08X", sampled) + " stack=" + st);
                                        }
                                    }
                                }
                            } catch (Throwable t37) { /* 静默 */ }
                            return chain.proceed();
                        });
                        ++hooked;
                    }
                } catch (Throwable throwable31) {
                    L.log(5, TAG, "L22 SETBG install failed: " + throwable31);
                }
            } catch (Throwable throwable30) {
                L.log(5, TAG, "L22 IVWRITE install failed: " + throwable30);
            }
            try {
                Class<?> iconViewCls = cl.loadClass("com.android.systemui.qs.tileimpl.MiuiQSIconViewImpl");
                for (java.lang.reflect.Method um : iconViewCls.getDeclaredMethods()) {
                    if (!"updateIcon".equals(um.getName())) continue;
                    Class<?>[] ps = um.getParameterTypes();
                    if (ps.length != 4) continue;
                    final java.lang.reflect.Method uMethod = um;
                    module.hook((Executable)uMethod).setId("cc_l22_updateicon_probe").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                        Object r = chain.proceed();
                        try {
                            Object self = chain.getThisObject();
                            java.util.List<Object> args = chain.getArgs();
                            Object st = args.get(1);
                            Object iv = args.get(0);
                            int state = -99, activeBg = -99;
                            String label = "";
                            try {
                                java.lang.reflect.Field f1 = st.getClass().getField("state");
                                state = f1.getInt(st);
                                java.lang.reflect.Field f2 = st.getClass().getField("activeBgColor");
                                activeBg = f2.getInt(st);
                                java.lang.reflect.Field f3 = st.getClass().getField("label");
                                Object lo = f3.get(st);
                                label = lo == null ? "" : String.valueOf(lo);
                            }
                            catch (Throwable ignored26) { }
                            if (iv instanceof android.widget.ImageView) {
                                android.widget.ImageView imv = (android.widget.ImageView)iv;
                                // ★ v1.0.3q：纯色盲写实验 —— 不读不判，绑定后直接给每层上色：
                                //   第0层→品红，中间层→青色，最后一层→绿色。
                                //   屏幕若变色=视图链正确；不变=显示的 drawable 不是这个。
                                try {
                                    Drawable d0 = imv.getDrawable();
                                    if (d0 instanceof LayerDrawable) {
                                        LayerDrawable ldx = (LayerDrawable)d0;
                                        int nn = ldx.getNumberOfLayers();
                                        for (int li = 0; li < nn; ++li) {
                                            Drawable l = ldx.getDrawable(li);
                                            if (l == null) continue;
                                            if (li == 0) l.setTint(0xFFFF00FF);           // 品红
                                            else if (li == nn - 1) l.setTint(0xFF00FF00); // 绿
                                            else l.setTint(0xFF00FFFF);                   // 青
                                        }
                                    } else if (d0 != null) {
                                        d0.setTint(0xFFFF00FF); // 非层叠单 drawable → 品红
                                    }
                                }
                                catch (Throwable t30) {
                                    L.log(5, TAG, "L22 colorwrite err: " + t30);
                                }
                                Drawable d = imv.getDrawable();
                                if (d == null || imv.getWidth() <= 0 || imv.getHeight() <= 0) return r;
                                int w = Math.min(96, imv.getWidth()), h = Math.min(96, imv.getHeight());
                                if (w <= 0 || h <= 0) return r;
                                // ★ 渲染取样：把 drawable 画进 Bitmap，统计实际渲染色
                                android.graphics.Bitmap bm = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888);
                                android.graphics.Canvas cv = new android.graphics.Canvas(bm);
                                int ob = d.getBounds().bottom, or2 = d.getBounds().right;
                                d.setBounds(0, 0, w, h);
                                d.draw(cv);
                                d.setBounds(0, 0, or2, ob);
                                // 采样 5x5 网格，统计颜色
                                java.util.Map<Integer, Integer> cnt = new java.util.LinkedHashMap<Integer, Integer>();
                                boolean hasBlue = false;
                                for (int sy = 0; sy < 5; ++sy) {
                                    for (int sx = 0; sx < 5; ++sx) {
                                        int px = bm.getPixel(sx * (w - 1) / 4, sy * (h - 1) / 4);
                                        if ((px & 0x00FFFFFF) == 0x000099FF) hasBlue = true;
                                        Integer k = px;
                                        Integer c = cnt.get(k);
                                        cnt.put(k, c == null ? 1 : c + 1);
                                    }
                                }
                                bm.recycle();
                                // ★ v1.0.3s 磁贴全家福：dump 磁贴视图链（最多5层）的 background/foreground + 子视图，
                                //   找蓝圆底的真实载体（它不在 icon ImageView 的 drawable 里）
                                try {
                                    StringBuilder fam = new StringBuilder("L22 FAMILY [" + label + "]");
                                    Object o2 = iv instanceof android.view.View ? ((android.view.View) iv).getParent() : null;
                                    int lvl = 0;
                                    while (o2 instanceof android.view.ViewGroup && lvl < 5) {
                                        android.view.ViewGroup g = (android.view.ViewGroup) o2;
                                        fam.append(" || L").append(lvl).append('=').append(g.getClass().getSimpleName());
                                        android.graphics.drawable.Drawable tb = g.getBackground();
                                        fam.append(" bg=").append(tb == null ? "null" : tb.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(tb)));
                                        // ★ 读 GradientDrawable 的实际填充色（蓝圆底真身）
                                        if (tb instanceof android.graphics.drawable.GradientDrawable) {
                                            try {
                                                android.graphics.drawable.GradientDrawable gd = (android.graphics.drawable.GradientDrawable) tb;
                                                java.lang.reflect.Field cf = android.graphics.drawable.GradientDrawable.class.getDeclaredField("mFillColor");
                                                cf.setAccessible(true);
                                                Object fv = cf.get(gd);
                                                fam.append(" fill=").append(fv instanceof int[] ? String.format("0x%08X", ((int[]) fv)[0]) : String.valueOf(fv));
                                                fam.append(" alpha=").append(gd.getAlpha());
                                            } catch (Throwable tf2) {
                                                // 反射失败则尝试 drawable 采样
                                                try {
                                                    android.graphics.Bitmap bb = android.graphics.Bitmap.createBitmap(8, 8, android.graphics.Bitmap.Config.ARGB_8888);
                                                    android.graphics.Canvas cc2 = new android.graphics.Canvas(bb);
                                                    tb.setBounds(0, 0, 8, 8);
                                                    tb.draw(cc2);
                                                    fam.append(" pix=").append(String.format("0x%08X", bb.getPixel(4, 4)));
                                                    bb.recycle();
                                                } catch (Throwable tf3) { fam.append(" fillErr"); }
                                            }
                                        }
                                        android.graphics.drawable.Drawable tf = g.getForeground();
                                        fam.append(" fg=").append(tf == null ? "null" : tf.getClass().getSimpleName());
                                        // ★ 读 foreground 的实际渲染色（蓝圆底很可能是 TintDrawable 的 tint）
                                        if (tf != null) {
                                            try {
                                                java.lang.reflect.Method gt = tf.getClass().getMethod("getTint");
                                                gt.setAccessible(true);
                                                Object tv = gt.invoke(tf);
                                                fam.append(" fgTint=").append(tv == null ? "null" : String.format("0x%08X", (Integer) tv));
                                            } catch (Throwable tg1) {
                                                fam.append(" fgTint=n/a");
                                            }
                                            try {
                                                android.graphics.Bitmap fb = android.graphics.Bitmap.createBitmap(16, 16, android.graphics.Bitmap.Config.ARGB_8888);
                                                android.graphics.Canvas fc = new android.graphics.Canvas(fb);
                                                int ofw = tf.getBounds().width();
                                                tf.setBounds(0, 0, 16, 16);
                                                tf.draw(fc);
                                                tf.setBounds(0, 0, ofw, ofw);
                                                fam.append(" fgPix=").append(String.format("0x%08X", fb.getPixel(8, 8)));
                                                fb.recycle();
                                            } catch (Throwable tg2) { fam.append(" fgPixErr"); }
                                        }
                                        fam.append(" n=").append(g.getChildCount());
                                        for (int ci = 0; ci < g.getChildCount() && ci < 6; ++ci) {
                                            android.view.View cv2 = g.getChildAt(ci);
                                            fam.append(" c").append(ci).append('=').append(cv2.getClass().getSimpleName());
                                            if (!(cv2 instanceof android.view.ViewGroup)) {
                                                android.graphics.drawable.Drawable cb = cv2.getBackground();
                                                if (cb != null) fam.append("(bg=").append(cb.getClass().getSimpleName()).append(')');
                                            }
                                        }
                                        o2 = g.getParent();
                                        ++lvl;
                                    }
                                    L.log(4, TAG, fam.toString());
                                } catch (Throwable t31) { /* 静默 */ }
                                // ★ v1.0.3u 修复尝试：蓝圆底在 MiuiLaunchableFrameLayout 层。
                                //   启用态（state==2）把它的 background GradientDrawable 直接染成亮蓝，
                                //   同时尝试染 foreground TintDrawable（哪个生效由屏幕说话）。
                                try {
                                    if (state == 2) {
                                        android.view.View frameV = null;
                                        Object oo = ((android.view.View) iv).getParent();
                                        int guard = 0;
                                        while (oo instanceof android.view.View && guard < 5) {
                                            android.view.View vv = (android.view.View) oo;
                                            if (vv.getClass().getSimpleName().contains("MiuiLaunchableFrameLayout")) { frameV = vv; break; }
                                            oo = vv.getParent();
                                            ++guard;
                                        }
                                        if (frameV != null) {
                                            // ★ v1.0.3y 终极修复：状态型磁贴的蓝 tint 在 XML inflate 时设置，绕过所有 setter。
                                            //   主动读取 backgroundTintList，蓝色系 → 强制写亮蓝（公开 API，不依赖拦截）。
                                            android.content.res.ColorStateList cur = frameV.getBackgroundTintList();
                                            if (cur != null) {
                                                int dc = cur.getDefaultColor();
                                                int rr2 = (dc >> 16) & 0xFF, gg2 = (dc >> 8) & 0xFF, bb2 = dc & 0xFF;
                                                boolean blueish = (bb2 > 0x60) && (bb2 > rr2 + 0x20) && (bb2 > gg2 + 0x20) && ((dc >>> 24) > 0);
                                                if (blueish && dc != 0xFF0099FF) {
                                                    frameV.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF0099FF));
                                                    if (COLOR_SEEN.add("tintfix:" + Integer.toHexString(dc))) {
                                                        L.log(4, TAG, "L22 TINTFIX 0x" + String.format("%08X", dc) + " -> 0xFF0099FF [" + label + "]");
                                                    }
                                                } else if (blueish) {
                                                    if (COLOR_SEEN.add("tintok:" + Integer.toHexString(dc))) {
                                                        L.log(4, TAG, "L22 TINTOK 0x" + String.format("%08X", dc) + " [" + label + "]");
                                                    }
                                                }
                                            }
                                            android.graphics.drawable.Drawable bd = frameV.getBackground();
                                            String cls = bd == null ? "null" : bd.getClass().getSimpleName();
                                            // 注意：不再盲写 setColor —— 灰玻璃底也是 GradientDrawable，
                                            // 盲写会把玻璃也刷蓝（v1.0.3w/t 的教训）。颜色统一只走 backgroundTintList。
                                            // ★ v1.0.3z：drawable 自身 tint 诊断（Drawable 级 tint 与 View 级是两个系统）
                                            String dTint = "n/a";
                                            int dPix = 0;
                                            if (bd != null) {
                                                try {
                                                    Object dt = bd.getClass().getMethod("getTintList").invoke(bd);
                                                    if (dt instanceof android.content.res.ColorStateList) {
                                                        dTint = "0x" + Integer.toHexString(((android.content.res.ColorStateList) dt).getDefaultColor());
                                                    } else {
                                                        dTint = "null";
                                                    }
                                                } catch (Throwable td1) {
                                                    try {
                                                        Object dt2 = bd.getClass().getMethod("getTint").invoke(bd);
                                                        dTint = dt2 == null ? "null(int)" : "0x" + Integer.toHexString((Integer) dt2);
                                                    } catch (Throwable td2) { dTint = "no-access"; }
                                                }
                                                try {
                                                    android.graphics.Bitmap bb2 = android.graphics.Bitmap.createBitmap(8, 8, android.graphics.Bitmap.Config.ARGB_8888);
                                                    android.graphics.Canvas cc5 = new android.graphics.Canvas(bb2);
                                                    android.graphics.Rect ob5 = bd.copyBounds();
                                                    bd.setBounds(0, 0, 8, 8);
                                                    bd.draw(cc5);
                                                    bd.setBounds(ob5);
                                                    dPix = bb2.getPixel(4, 4);
                                                    bb2.recycle();
                                                } catch (Throwable td3) { dPix = -1; }
                                            }
                                            if (COLOR_SEEN.add("tilebg:" + System.identityHashCode(frameV))) {
                                                L.log(4, TAG, "L22 TILEBG [" + label + "] bgCls=" + cls + " viewTint="
                                                        + (frameV.getBackgroundTintList() == null ? "null" : "0x" + Integer.toHexString(frameV.getBackgroundTintList().getDefaultColor()))
                                                        + " dTint=" + dTint + " pix=0x" + Integer.toHexString(dPix));
                                            }
                                            // ★ v40 的 setBackground(新亮蓝圆) 已撤销（v41）：
                                            //   smali 证实蓝圆底不在 frameV.background，而是 icon ImageView 的
                                            //   LayerDrawable 第 1 层（见 v41 LAYERFIX 注释）。frameV 的
                                            //   0x1AFFFFFF 渐变是官方玻璃底，不应触碰。
                                        }
                                    }
                                } catch (Throwable t32) { L.log(5, TAG, "L22 TILEBG err " + t32); }
                                StringBuilder sb = new StringBuilder("L22 PIX [" + label + "] st=" + state + " abg=" + activeBg + " colors=");
                                for (java.util.Map.Entry<Integer, Integer> e : cnt.entrySet()) {
                                    sb.append(String.format("0x%08X", e.getKey())).append('x').append(e.getValue()).append(' ');
                                }
                                // ★ watch 磁贴完全不去重，每次绑定都记录
                                boolean watch = label.contains("移动数据") || label.contains("HUAWEI")
                                        || label.contains("WLAN") || label.contains("Wi");
                                if (watch) {
                                    android.content.res.ColorStateList itl = imv.getImageTintList();
                                    L.log(4, TAG, "L22 WATCH [" + label + "] st=" + state + " imgTint="
                                            + (itl == null ? "null" : String.format("0x%08X", itl.getDefaultColor()))
                                            + " colors=" + sb.substring(sb.indexOf("colors=")));
                                } else if (COLOR_SEEN.add("pix:" + label + ":" + state + ":" + activeBg)) {
                                    L.log(4, TAG, sb.toString());
                                }
                            }
                        }
                        catch (Throwable t23) {
                            L.log(5, TAG, "L22 updateIcon probe err: " + t23);
                        }
                        return r;
                    });
                    ++hooked;
                    break;
                }
            }
            catch (Throwable throwable4) {
                L.log(5, TAG, "L22 updateIcon probe install failed: " + throwable4);
            }
            L.log(4, TAG, "L22 qs dark colors hooked=" + hooked);
            return hooked > 0;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L22 failed: " + t);
            return false;
        }
    }

    private static void tintPageIndicator(XposedModule module, View root) {
        try {
            View pi = CcHooks.findByClass(root, "com.android.systemui.qs.MiuiPageIndicator");
            if (!(pi instanceof ViewGroup)) {
                int n = PAGEDOT_PROBE.incrementAndGet();
                if (n <= 5) {
                    L.log(5, TAG, "L19 page indicator NOT FOUND, pi=" + (pi == null ? "null" : pi.getClass().getName()));
                }
                return;
            }
            ViewGroup vg = (ViewGroup)pi;
            int cnt = 0;
            for (int i = 0; i < vg.getChildCount(); ++i) {
                View c = vg.getChildAt(i);
                if (!(c instanceof ImageView)) continue;
                ImageView iv = (ImageView)c;
                CcHooks.tintImageViewForce(iv, -1);
                if (iv.getAlpha() < 0.5f) {
                    iv.setAlpha(0.45f);
                }
                ++cnt;
            }
            int n = PAGEDOT_PROBE.incrementAndGet();
            if (n <= 8) {
                StringBuilder sb = new StringBuilder("L19 page dots tinted=" + cnt + " children=" + vg.getChildCount() + " cls=" + vg.getClass().getName() + " size=" + vg.getWidth() + "x" + vg.getHeight());
                for (int i = 0; i < vg.getChildCount(); ++i) {
                    View c = vg.getChildAt(i);
                    sb.append(" [").append(i).append("]").append(c.getClass().getSimpleName()).append(" ").append(c.getWidth()).append("x").append(c.getHeight()).append(" a=").append(c.getAlpha());
                    if (!(c instanceof ImageView)) continue;
                    Drawable d = ((ImageView)c).getDrawable();
                    sb.append(" d=").append(d == null ? "null" : d.getClass().getSimpleName());
                }
                L.log(4, TAG, sb.toString());
            }
            CcHooks.installPageIndicatorGuard(module, vg);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void installPageIndicatorGuard(XposedModule module, ViewGroup vg) {
        try {
            if (!PAGEDOT_GUARDED.add(vg)) {
                return;
            }
            CcHooks.addDrawGuard(module, (View)vg, view -> {
                try {
                    for (int i = 0; i < vg.getChildCount(); ++i) {
                        View c = vg.getChildAt(i);
                        if (!(c instanceof ImageView)) continue;
                        ImageView iv = (ImageView)c;
                        CcHooks.tintImageView(iv, -1);
                        if (!(iv.getAlpha() < 0.5f)) continue;
                        iv.setAlpha(0.45f);
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            });
            L.log(4, TAG, "L19 page dot guard installed");
        }
        catch (Throwable t) {
            L.log(5, TAG, "L19 page dot guard failed: " + t);
        }
    }

    private static void tintTileIndicators(XposedModule module, View tile) {
        try {
            View labelGroup;
            View padLock;
            View byId;
            ImageView expand = null;
            View v = CcHooks.findFieldView(tile, "mExpandIndicator");
            if (v instanceof ImageView) {
                expand = (ImageView)v;
            }
            if (expand == null && (byId = tile.findViewById(2131428414)) instanceof ImageView) {
                expand = (ImageView)byId;
            }
            if (expand != null) {
                CcHooks.tintImageView(expand, -855638017);
                CcHooks.installIndicatorGuard(module, expand, -855638017, "expand");
            }
            if (!((padLock = CcHooks.findFieldView(tile, "mPadLock")) instanceof ImageView)) {
                padLock = tile.findViewById(2131429855);
            }
            if (padLock instanceof ImageView) {
                ImageView pIv = (ImageView)padLock;
                CcHooks.tintImageView(pIv, -855638017);
                CcHooks.installIndicatorGuard(module, pIv, -855638017, "padlock");
            }
            if ((labelGroup = tile.findViewById(2131428897)) instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup)labelGroup;
                for (int i = 0; i < vg.getChildCount(); ++i) {
                    View c = vg.getChildAt(i);
                    if (!"View".equals(c.getClass().getSimpleName())) continue;
                    c.setBackgroundColor(0x33FFFFFF);
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void installIndicatorGuard(XposedModule module, ImageView iv, int color, String tagName) {
        try {
            if (!INDICATOR_GUARDED.add(iv)) {
                return;
            }
            CcHooks.addDrawGuard(module, (View)iv, view -> {
                try {
                    Object lastDrawable;
                    Drawable d = iv.getDrawable();
                    if (d != null && (lastDrawable = iv.getTag(2131689489)) != d) {
                        iv.setTag(2131689489, (Object)d);
                        CcHooks.tintImageViewForce(iv, color);
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            });
            L.log(4, TAG, "L21 indicator guard installed: " + tagName);
        }
        catch (Throwable t) {
            L.log(5, TAG, "L21 indicator guard failed " + tagName + ": " + t);
        }
    }

    private static void applySliderMaterial(XposedModule module, View slider, int mode) {
        try {
            View container;
            View parent;
            Drawable thumb;
            View found;
            View real = slider;
            if (!(slider instanceof SeekBar) && (found = slider.findViewById(2131430146)) != null) {
                real = found;
            }
            if (!(real instanceof ProgressBar)) {
                return;
            }
            ProgressBar pb = (ProgressBar)real;
            pb.setTag(2131689474, (Object)Boolean.TRUE);
            Drawable pd = pb.getProgressDrawable();
            if (pd instanceof LayerDrawable) {
                Drawable progLayer;
                LayerDrawable ld = (LayerDrawable)pd;
                Drawable bgLayer = ld.findDrawableByLayerId(0x1020000);
                if (bgLayer != null) {
                    CcHooks.tintDrawable(bgLayer, 0);
                }
                if ((progLayer = ld.findDrawableByLayerId(16908301)) != null) {
                    CcHooks.tintDrawable(progLayer, 0x3DFFFFFF);
                }
                for (int i = 0; i < ld.getNumberOfLayers(); ++i) {
                    Drawable layer = ld.getDrawable(i);
                    if (layer == bgLayer || layer == progLayer || layer == null) continue;
                    CcHooks.tintDrawable(layer, 0x2AFFFFFF);
                }
            }
            if (pb instanceof AbsSeekBar && (thumb = ((AbsSeekBar)pb).getThumb()) != null) {
                CcHooks.tintDrawable(thumb, -1);
            }
            if ((parent = (View)slider.getParent()) instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup)parent;
                for (int i = 0; i < vg.getChildCount(); ++i) {
                    View c = vg.getChildAt(i);
                    if (!(c instanceof ImageView) || c == slider) continue;
                    CcHooks.tintImageView((ImageView)c, -1);
                }
            }
            if ((container = slider).getWidth() > 0 && container.getHeight() > 0) {
                if ("1".equals(CcHooks.getProp("debug.hyperclassic.slider_legacy", "1").trim())) {
                    CcHooks.applySliderGlass(module, container, mode);
                } else {
                    CcHooks.applySliderFrostedGlass(module, container, mode);
                }
            }
        }
        catch (Throwable t) {
            L.log(5, TAG, "L19 slider material failed: " + t);
        }
    }

    private static void refreshSliderColors(ProgressBar pb) {
        try {
            Drawable sec;
            Drawable prog;
            Drawable d = pb.getProgressDrawable();
            if (!(d instanceof LayerDrawable)) {
                return;
            }
            LayerDrawable ld = (LayerDrawable)d;
            Drawable bg = ld.findDrawableByLayerId(0x1020000);
            if (bg instanceof GradientDrawable) {
                ((GradientDrawable)bg).setColor(0x1FFFFFFF);
            }
            if ((prog = ld.findDrawableByLayerId(16908301)) instanceof GradientDrawable) {
                ((GradientDrawable)prog).setColor(0x3DFFFFFF);
            }
            if ((sec = ld.findDrawableByLayerId(16908303)) instanceof GradientDrawable) {
                ((GradientDrawable)sec).setColor(0x2AFFFFFF);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static LayerDrawable buildSliderDrawable(ProgressBar pb) {
        try {
            int h = pb.getHeight();
            if (h <= 0) {
                h = pb.getMeasuredHeight();
            }
            if (h <= 0) {
                h = 60;
            }
            float radius = (float)h / 2.0f;
            GradientDrawable bg = new GradientDrawable();
            bg.setShape(0);
            bg.setCornerRadius(radius);
            bg.setColor(0x1FFFFFFF);
            GradientDrawable prog = new GradientDrawable();
            prog.setShape(0);
            prog.setCornerRadius(radius);
            prog.setColor(0x3DFFFFFF);
            GradientDrawable sec = new GradientDrawable();
            sec.setShape(0);
            sec.setCornerRadius(radius);
            sec.setColor(0x2AFFFFFF);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{bg, sec, prog});
            ld.setId(0, 0x1020000);
            ld.setId(1, 16908303);
            ld.setId(2, 16908301);
            return ld;
        }
        catch (Throwable t) {
            return null;
        }
    }

    private static void dumpSliderTree(XposedModule module, View v, int depth) {
        try {
            if (depth > 4) {
                return;
            }
            StringBuilder sb = new StringBuilder("SLIDER-TREE ");
            for (int i = 0; i < depth; ++i) {
                sb.append("\u00b7\u00b7");
            }
            sb.append(v.getClass().getSimpleName()).append(" id=0x").append(Integer.toHexString(v.getId()).toUpperCase()).append(" ").append(v.getWidth()).append("x").append(v.getHeight()).append(" vis=").append(v.getVisibility()).append(" bg=").append(v.getBackground() == null ? "null" : v.getBackground().getClass().getSimpleName()).append(" clip=").append(v.getClipToOutline());
            try {
                Outline o = new Outline();
                ViewOutlineProvider p = v.getOutlineProvider();
                if (p != null) {
                    p.getOutline(v, o);
                    sb.append(" outline[r=").append(o.getRadius()).append(" rect=").append(o.canClip()).append("]");
                }
            }
            catch (Throwable o) {
                // empty catch block
            }
            L.log(4, TAG, sb.toString());
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup)v;
                for (int i = 0; i < vg.getChildCount(); ++i) {
                    CcHooks.dumpSliderTree(module, vg.getChildAt(i), depth + 1);
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void applySliderFrostedGlass(XposedModule module, View v, int mode) {
        try {
            if (!SLIDER_GLASS_DONE.add(v)) {
                return;
            }
            int w = v.getWidth();
            int h = v.getHeight();
            if (w <= 0 || h <= 0) {
                w = v.getMeasuredWidth();
                h = v.getMeasuredHeight();
            }
            if (w <= 0 || h <= 0) {
                SLIDER_GLASS_DONE.remove(v);
                return;
            }
            ClassLoader cl = v.getClass().getClassLoader();
            CcHooks.applySliderGlass(module, v, mode);
            boolean ok = true;
            if (!ok) {
                SLIDER_GLASS_DONE.remove(v);
            }
        }
        catch (Throwable t) {
            L.log(5, TAG, "L19 slider frostedGlass failed: " + t);
        }
    }

    private static boolean applySliderGlassNoReset(XposedModule module, View v, ClassLoader cl, String tag) {
        try {
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, cl);
            Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, cl);
            try {
                miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 0, v);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            int bgMode = CcHooks.parseIntSafe(CcHooks.getProp("debug.hyperclassic.slider_bg_mode", "4").trim(), 0);
            int bgColor = bgMode == 4 ? 0 : (bgMode == 3 ? -1728053248 : (bgMode == 2 ? -65536 : (bgMode == 1 ? 0x4DFFFFFF : 0)));
            try {
                GradientDrawable round = new GradientDrawable();
                round.setShape(0);
                round.setColor(bgColor);
                round.setCornerRadius((float)v.getHeight() / 2.0f);
                v.setBackground((Drawable)round);
                v.setClipToOutline(true);
                v.setOutlineProvider(new ViewOutlineProvider(){

                    public void getOutline(View view, Outline outline) {
                        float r = (float)view.getHeight() / 2.0f;
                        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), r);
                    }
                });
                v.invalidateOutline();
                if (bgMode != 0) {
                    L.log(5, TAG, "L19 slider BG-MODE=" + bgMode + " color=0x" + Integer.toHexString(bgColor) + " cls=" + v.getClass().getSimpleName() + " " + v.getWidth() + "x" + v.getHeight());
                }
            }
            catch (Throwable round) {
                // empty catch block
            }
            try {
                miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            }
            catch (Throwable round) {
                // empty catch block
            }
            try {
                miBlur.getMethod("clearMiBackgroundBlendColorCompat", View.class).invoke(null, v);
            }
            catch (Throwable round) {
                // empty catch block
            }
            int[] colors = CcHooks.buildBlendColors();
            float alphaScale = CcHooks.buildBlendAlpha();
            try {
                miBlur.getMethod("setMiBackgroundBlendColors", View.class, int[].class, Float.TYPE).invoke(null, v, colors, Float.valueOf(alphaScale));
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            L.log(4, TAG, "L19 slider blend colors=[0x" + Integer.toHexString(colors[0]) + ",0x" + Integer.toHexString(colors[2]) + "] scale=" + alphaScale);
            try {
                miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class).invoke(null, CcHooks.sliderBlurRadius(), v);
                miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
                miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE).invoke(null, v, true);
                if (v.isAttachedToWindow()) {
                    CcHooks.callSetMiBlurWinExc(v, "NotificationShade", module);
                }
            }
            catch (Throwable t) {
                L.log(5, TAG, "L19 slider blur chain failed: " + t);
            }
            float[] garr = CcHooks.loadGlassParamsFromRes(v);
            garr = garr == null || garr.length < 29 ? (float[])GLASS_PARAMS_FALLBACK.clone() : (float[])garr.clone();
            if (garr.length > 21) {
                garr[21] = Math.max(20, CcHooks.currentBlurRadius());
            }
            String err = null;
            try {
                miGlass.getMethod("setMiGlassCompat", View.class, float[].class).invoke(null, v, garr);
            }
            catch (Throwable t) {
                err = String.valueOf(t);
            }
            try {
                miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            }
            catch (Throwable t) {
                err = (err == null ? "" : err + "; ") + "type=" + t;
            }
            L.log(4, TAG, "L19 slider OFFICIAL size=" + v.getWidth() + "x" + v.getHeight() + " params=" + garr.length + " blend=" + Integer.toHexString(colors[0]) + " attached=" + v.isAttachedToWindow() + " err=" + err);
            try {
                miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE).invoke(null, v, true);
                Object pass = miBlur.getMethod("getPassWindowBlurEnabledCompat", View.class).invoke(null, v);
                L.log(4, TAG, "L19 slider PROBE passWindowBlur=" + pass + " (true=\u5f15\u64ce\u8ba4\uff0cfalse=\u5f15\u64ce\u4e22\u5f03)");
            }
            catch (Throwable t) {
                L.log(5, TAG, "L19 slider PROBE failed: " + t);
            }
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L19 slider OFFICIAL failed: " + t);
            return false;
        }
    }

    private static void applySliderGlass(XposedModule module, View v, int mode) {
        try {
            if (!SLIDER_GLASS_DONE.add(v)) {
                return;
            }
            ClassLoader cl = v.getClass().getClassLoader();
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, cl);
            Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, cl);
            GradientDrawable round = new GradientDrawable();
            round.setShape(0);
            round.setColor(0xDFFFFFF);
            round.setCornerRadius((float)v.getHeight() / 2.0f);
            v.setBackground((Drawable)round);
            v.setClipToOutline(true);
            v.setOutlineProvider(new ViewOutlineProvider(){

                public void getOutline(View view, Outline outline) {
                    float r = (float)view.getHeight() / 2.0f;
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), r);
                }
            });
            v.invalidateOutline();
            miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            int[] blend = new int[]{0x40FFFFFF};
            boolean done = false;
            try {
                miBlur.getMethod("setMiBackgroundBlendColorsNew$default", View.class, int[].class).invoke(null, v, blend);
                done = true;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (!done) {
                miBlur.getMethod("setMiBackgroundBlendColors", View.class, int[].class, Float.TYPE).invoke(null, v, blend, Float.valueOf(1.0f));
            }
            miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class).invoke(null, CcHooks.sliderBlurRadius(), v);
            miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE).invoke(null, v, true);
            if (v.isAttachedToWindow()) {
                CcHooks.callSetMiBlurWinExc(v, "NotificationShade", module);
            }
            if (mode >= 2) {
                float[] garr = CcHooks.loadGlassParamsFromRes(v);
                if (garr != null && garr.length >= 20) {
                    miGlass.getMethod("setMiGlassCompat", View.class, float[].class).invoke(null, v, garr);
                    miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
                }
                miGlass.getMethod("setMiGlassBlurRadius", View.class, Integer.TYPE, Integer.TYPE).invoke(null, v, CcHooks.sliderBlurRadius(), CcHooks.sliderBlurRadius());
            }
            L.log(4, TAG, "L19 slider glass applied mode=" + mode + " size=" + v.getWidth() + "x" + v.getHeight());
            if (DUMP_SLIDER.incrementAndGet() <= 3) {
                CcHooks.dumpBlurState(module, v, "SLIDER");
            }
        }
        catch (Throwable t) {
            L.log(5, TAG, "L19 slider glass failed: " + t);
        }
    }

    private static void forceIconWhite(ImageView iv, int color) {
        try {
            Drawable d = iv.getDrawable();
            if (d == null) {
                return;
            }
            d.mutate();
            d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            d.setTint(color);
            d.setTintMode(PorterDuff.Mode.SRC_IN);
            if (d instanceof LayerDrawable) {
                LayerDrawable ld = (LayerDrawable)d;
                int n = ld.getNumberOfLayers();
                for (int i = 0; i < n; ++i) {
                    Drawable layer = ld.getDrawable(i);
                    if (layer == null) continue;
                    layer.mutate();
                    layer.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    layer.setTint(color);
                    layer.setTintMode(PorterDuff.Mode.SRC_IN);
                }
            }
            d.invalidateSelf();
            iv.setTag(2131689488, (Object)color);
            iv.invalidate();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void tintImageViewForce(ImageView iv, int color) {
        try {
            Drawable d = iv.getDrawable();
            if (d == null) {
                return;
            }
            d.mutate();
            d.setTint(color);
            d.setTintMode(PorterDuff.Mode.SRC_IN);
            d.invalidateSelf();
            iv.setTag(2131689488, (Object)color);
            iv.invalidate();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static boolean isTintedWith(ImageView iv, int color) {
        try {
            Object t = iv.getTag(2131689488);
            return t instanceof Integer && (Integer)t == color;
        }
        catch (Throwable t) {
            return false;
        }
    }

    private static void tintImageView(ImageView iv, int color) {
        try {
            Drawable d = iv.getDrawable();
            if (d == null) {
                return;
            }
            d.mutate();
            d.setTint(color);
            d.invalidateSelf();
            iv.invalidate();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void forceLabelWhite(TextView tv, int color) {
        try {
            CharSequence cs = tv.getText();
            if (cs instanceof Spannable) {
                ForegroundColorSpan[] spans;
                Spannable sp = (Spannable)cs;
                for (ForegroundColorSpan s : spans = (ForegroundColorSpan[])sp.getSpans(0, sp.length(), ForegroundColorSpan.class)) {
                    sp.removeSpan((Object)s);
                }
                sp.setSpan((Object)new ForegroundColorSpan(color), 0, sp.length(), 33);
            }
            tv.setTextColor(color);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void installTextGuard(XposedModule module, View tile) {
        try {
            CcHooks.installOneTextGuard(module, CcHooks.findFieldView(tile, "mLabel"), -419430401);
            CcHooks.installOneTextGuard(module, CcHooks.findFieldView(tile, "mSecondLine"), -1275068417);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void installOneTextGuard(XposedModule module, View v, int color) {
        try {
            if (!(v instanceof TextView)) {
                return;
            }
            TextView tv = (TextView)v;
            if (!TEXT_GUARDED.add(tv)) {
                return;
            }
            CcHooks.addDrawGuard(module, (View)tv, view -> {
                try {
                    boolean need;
                    CharSequence cs = tv.getText();
                    boolean bl = need = !(cs instanceof Spannable);
                    if (!need) {
                        Spannable sp = (Spannable)cs;
                        boolean bl2 = need = ((ForegroundColorSpan[])sp.getSpans(0, sp.length(), ForegroundColorSpan.class)).length > 0 || tv.getCurrentTextColor() != color;
                    }
                    if (need) {
                        CcHooks.forceLabelWhite(tv, color);
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            });
            L.log(4, TAG, "L21 text guard installed");
        }
        catch (Throwable t) {
            L.log(5, TAG, "L21 text guard failed: " + t);
        }
    }

    private static void updateIconGuardState(View tile, int state) {
        try {
            View iconView = CcHooks.findFieldView(tile, "mIcon");
            if (!(iconView instanceof ViewGroup)) {
                return;
            }
            View iv = CcHooks.findChildImageView((ViewGroup)iconView);
            if (iv instanceof ImageView) {
                iv.setTag(2131689473, (Object)state);
                ((ImageView)iv).invalidate();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void installIconGuard(XposedModule module, View tile, int state) {
        try {
            View iconView = CcHooks.findFieldView(tile, "mIcon");
            if (!(iconView instanceof ViewGroup)) {
                return;
            }
            View iv = CcHooks.findChildImageView((ViewGroup)iconView);
            if (!(iv instanceof ImageView)) {
                return;
            }
            ImageView imageView = (ImageView)iv;
            imageView.setTag(2131689473, (Object)state);
            if (!ICON_GUARDED.add(imageView)) {
                return;
            }
            CcHooks.addDrawGuard(module, (View)imageView, view -> {
                try {
                    Object t = imageView.getTag(2131689473);
                    int st = t instanceof Integer ? (Integer)t : 1;
                    CcHooks.applyIconLayers(imageView, st);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            });
            L.log(4, TAG, "L21 icon guard installed");
        }
        catch (Throwable t) {
            L.log(5, TAG, "L21 icon guard failed: " + t);
        }
    }

    private static int getStateInt(Object state) {
        try {
            Field f = state.getClass().getField("state");
            f.setAccessible(true);
            return f.getInt(state);
        }
        catch (Throwable t) {
            return -1;
        }
    }

    private static void applyIconLayers(ImageView imageView, int state) {
        try {
            int bgColor;
            Drawable d = imageView.getDrawable();
            if (d == null) {
                return;
            }
            int iconColor = state == 0 ? -2130706433 : -1;
            int n = bgColor = state == 2 ? -1 : 0x33FFFFFF;
            if (d instanceof LayerDrawable) {
                LayerDrawable ld = (LayerDrawable)d;
                int n2 = ld.getNumberOfLayers();
                for (int i = 0; i < n2; ++i) {
                    Drawable layer = ld.getDrawable(i);
                    if (layer == null) continue;
                    layer.mutate();
                    if (i == n2 - 1) {
                        layer.setTint(iconColor);
                    } else if (bgColor != -1) {
                        layer.setTint(bgColor);
                    } else {
                        layer.setTintList(null);
                    }
                    layer.invalidateSelf();
                }
                imageView.invalidate();
            } else {
                d.mutate();
                d.setTint(iconColor);
                d.invalidateSelf();
                imageView.invalidate();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static boolean hookSuppressFold(XposedModule module, ClassLoader cl) {
        try {
            if ("1".equals(CcHooks.getProp("debug.hyperclassic.disable_l45", "0").trim())) {
                L.log(4, TAG, "L45 skipped (disable_l45=1)");
                return false;
            }
            Class<?> util = Class.forName("com.miui.systemui.notification.MiuiBaseNotifUtil", false, cl);
            module.hook((Executable)util.getDeclaredMethod("shouldSuppressFold", new Class[0])).setId("cc_l45_suppress_fold").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> true);
            Class<?> foldCtl = Class.forName("com.android.systemui.statusbar.notification.history.FoldNotifControllerImpl", false, cl);
            int hooked = 0;
            for (Method m : foldCtl.getDeclaredMethods()) {
                if (!m.getName().equals("sendFoldNotification")) continue;
                module.hook((Executable)m).setId("cc_l45_send_fold_" + hooked).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> null);
                ++hooked;
            }
            try {
                CcHooks.cancelAggregateNotification(module);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            L.log(4, TAG, "L45 fold suppressed: shouldSuppressFold=true, sendFoldNotification x" + hooked + " blocked");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L45 suppressFold failed: " + t);
            return false;
        }
    }

    private static void cancelAggregateNotification(XposedModule module) {
        try {
            Class<?> at = Class.forName("android.app.ActivityThread");
            Object app = at.getMethod("currentApplication", new Class[0]).invoke(null, new Object[0]);
            if (app == null) {
                return;
            }
            NotificationManager nm = (NotificationManager)((Context)app).getSystemService("notification");
            if (nm == null) {
                return;
            }
            for (StatusBarNotification sbn : nm.getActiveNotifications()) {
                if (!"id_aggregate".equals(sbn.getTag()) && !"UNIMPORTANT".equals(sbn.getTag())) continue;
                try {
                    nm.cancel(sbn.getTag(), sbn.getId());
                    L.log(4, TAG, "L45 cancelled aggregate notif tag=" + sbn.getTag() + " id=" + sbn.getId());
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void forceIconWhite(View tile, int state) {
        try {
            View iconView = CcHooks.findFieldView(tile, "mIcon");
            if (!(iconView instanceof ViewGroup)) {
                return;
            }
            View iv = CcHooks.findChildImageView((ViewGroup)iconView);
            if (iv instanceof ImageView) {
                CcHooks.applyIconLayers((ImageView)iv, state);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static View findChildImageView(ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); ++i) {
            View r;
            View c = vg.getChildAt(i);
            if (c instanceof ImageView) {
                return c;
            }
            if (!(c instanceof ViewGroup) || (r = CcHooks.findChildImageView((ViewGroup)c)) == null) continue;
            return r;
        }
        return null;
    }

    private static void applyTileGlass(XposedModule module, View iconFrame, int mode) {
        try {
            // ★ v138：attach 前的套用不算数。
            //   【主人反馈】「截屏与热点两个磁贴，它们的颜色比其他磁贴浅一些。」
            //   【补充线索】「蓝牙、锁屏、手电筒三个未启用磁贴，
            //     在托盘收起时是浅色，在展开动画结束后会变成深色。」
            //   【根因】applyTileGlass 里最关键的一步是
            //       if (iconFrame.isAttachedToWindow()) callSetMiBlurWinExc(...)
            //     setMiBlurWinExc 是「玻璃透出窗口背景」的前提（配方注释里标注「最关键」）。
            //     但 applyTileMaterial 会被 onLayout 触发，那时 view 常常还没 attached
            //     → 这一步被跳过，只剩 #1AFFFFFF 底色（浅）。
            //     而 TRAY_APPLIED 已经记下「已套过」，之后 attach 了也不会重套 → 永远浅。
            //   【L137 实证】所有磁贴的 state/bg/alpha/cto/blendOn 完全一致，
            //     说明差异不在静态字段，只在「窗口模糊有没有接上」。
            //   【修法】未 attached 时**不写去重表**，让 attach 后能再套一次。
            boolean attached138 = iconFrame.isAttachedToWindow();
            Integer prev = TRAY_APPLIED.get(iconFrame);
            if (prev != null && prev == mode && attached138) {
                return;
            }
            if (attached138) {
                TRAY_APPLIED.put(iconFrame, mode);
            } else {
                // 未 attach：不记录，等 attach 后重来
                TRAY_APPLIED.remove(iconFrame);
            }
            TILE_BLEND_SEEN.add(iconFrame); // v137：标记该 iconFrame 已走过玻璃链路
            ClassLoader cl = iconFrame.getClass().getClassLoader();
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, cl);
            Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, cl);
            GradientDrawable oval = new GradientDrawable();
            oval.setShape(1);
            oval.setColor(0x1AFFFFFF);
            iconFrame.setBackground((Drawable)oval);
            iconFrame.setClipToOutline(true);
            iconFrame.setOutlineProvider(new ViewOutlineProvider(){

                public void getOutline(View v, Outline outline) {
                    outline.setOval(0, 0, v.getWidth(), v.getHeight());
                }
            });
            iconFrame.invalidateOutline();
            miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, iconFrame);
            int[] blend = new int[]{0x33FFFFFF};
            boolean done = false;
            try {
                miBlur.getMethod("setMiBackgroundBlendColorsNew$default", View.class, int[].class).invoke(null, iconFrame, blend);
                done = true;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (!done) {
                miBlur.getMethod("setMiBackgroundBlendColors", View.class, int[].class, Float.TYPE).invoke(null, iconFrame, blend, Float.valueOf(1.0f));
            }
            miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class).invoke(null, 60, iconFrame);
            miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, iconFrame);
            miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE).invoke(null, iconFrame, true);
            if (iconFrame.isAttachedToWindow()) {
                CcHooks.callSetMiBlurWinExc(iconFrame, "NotificationShade", module);
            } else {
                // ★ v138：attach 前无法接窗口模糊 → 等 attached 后再套一次。
                //   用 post 兜底（onAttachedToWindow hook 也会触发，双保险）。
                final View iv138 = iconFrame;
                final XposedModule m138 = module;
                final int md138 = mode;
                iconFrame.post(new Runnable() { public void run() {
                    try {
                        if (iv138.isAttachedToWindow()) {
                            CcHooks.applyTileGlass(m138, iv138, md138);
                        }
                    } catch (Throwable ignored) {}
                }});
            }
            if (mode >= 2) {
                float[] garr = CcHooks.loadGlassParamsFromRes(iconFrame);
                if (garr != null && garr.length >= 20) {
                    miGlass.getMethod("setMiGlassCompat", View.class, float[].class).invoke(null, iconFrame, garr);
                    miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 1, iconFrame);
                }
                miGlass.getMethod("setMiGlassBlurRadius", View.class, Integer.TYPE, Integer.TYPE).invoke(null, iconFrame, 60, 60);
            }
        }
        catch (Throwable t) {
            L.log(5, TAG, "L21 applyTileGlass failed: " + t);
        }
    }

    private static View findFieldView(View obj, String fieldName) {
        try {
            for (Class<?> c = obj.getClass(); c != null; c = c.getSuperclass()) {
                try {
                    Field f = c.getDeclaredField(fieldName);
                    f.setAccessible(true);
                    Object o = f.get(obj);
                    if (o instanceof View) {
                        return (View)o;
                    }
                    return null;
                }
                catch (NoSuchFieldException e) {
                    continue;
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    private static int getTileState(View tile) {
        try {
            Field f = null;
            Class<?> c = tile.getClass();
            while (c != null && f == null) {
                try {
                    f = c.getDeclaredField("mState");
                }
                catch (NoSuchFieldException e) {
                    c = c.getSuperclass();
                }
            }
            if (f != null) {
                f.setAccessible(true);
                Object o = f.get(tile);
                if (o instanceof Integer) {
                    return (Integer)o;
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return -1;
    }

    private static boolean hookTileMaterial(XposedModule module, ClassLoader cl) {
        boolean ok = false;
        try {
            Class<?> tileCls = Class.forName("com.android.systemui.qs.tileimpl.MiuiQSTileView", false, cl);
            try {
                Method m1 = tileCls.getMethod("handleStateChanged", Class.forName("com.android.systemui.plugins.qs.QSTile$State", false, cl));
                module.hook((Executable)m1).setId("cc_l21_state").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object r = chain.proceed();
                    CcHooks.onTileHook(module, chain, "stateChanged");
                    return r;
                });
                ok = true;
            }
            catch (Throwable t) {
                L.log(5, TAG, "L21 stateChanged hook failed: " + t);
            }
            try {
                Method m2 = View.class.getDeclaredMethod("onAttachedToWindow", new Class[0]);
                m2.setAccessible(true);
                module.hook((Executable)m2).setId("cc_l21_attach").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object r = chain.proceed();
                    try {
                        Object self = chain.getThisObject();
                        if (self instanceof View && tileCls.isInstance(self)) {
                            CcHooks.onTileHook(module, chain, "attach");
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    return r;
                });
                ok = true;
            }
            catch (Throwable t) {
                L.log(5, TAG, "L21 attach hook failed: " + t);
            }
            try {
                Method m3 = View.class.getDeclaredMethod("onLayout", Boolean.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
                m3.setAccessible(true);
                module.hook((Executable)m3).setId("cc_l21_layout").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object r = chain.proceed();
                    try {
                        Object self = chain.getThisObject();
                        if (self instanceof View && tileCls.isInstance(self)) {
                            CcHooks.onTileHook(module, chain, "layout");
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    return r;
                });
                ok = true;
            }
            catch (Throwable t) {
                L.log(5, TAG, "L21 layout hook failed: " + t);
            }
            try {
                Method m4 = View.class.getDeclaredMethod("onConfigurationChanged", Configuration.class);
                m4.setAccessible(true);
                module.hook((Executable)m4).setId("cc_l21_config").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object r = chain.proceed();
                    try {
                        Object self = chain.getThisObject();
                        if (self instanceof View && tileCls.isInstance(self)) {
                            View tv = (View)self;
                            tv.post(() -> CcHooks.onTileHook(module, chain, "config"));
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    return r;
                });
                ok = true;
            }
            catch (Throwable t) {
                L.log(5, TAG, "L21 config hook failed: " + t);
            }
            try {
                Class<?> iconImpl = Class.forName("com.android.systemui.qs.tileimpl.MiuiQSIconViewImpl", false, cl);
                Method m4 = iconImpl.getDeclaredMethod("updateIcon", ImageView.class, Class.forName("com.android.systemui.plugins.qs.QSTile$State", false, cl), Boolean.TYPE, Boolean.TYPE);
                m4.setAccessible(true);
                module.hook((Executable)m4).setId("cc_l21_updateicon").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object r = chain.proceed();
                    try {
                        Object[] args = chain.getArgs().toArray();
                        if (args.length >= 2 && args[0] instanceof ImageView) {
                            ImageView iv = (ImageView)args[0];
                            int st = CcHooks.getStateInt(args[1]);
                            CcHooks.applyIconLayers(iv, st);
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    return r;
                });
                ok = true;
                L.log(4, TAG, "L21 hooked MiuiQSIconViewImpl.updateIcon");
            }
            catch (Throwable t) {
                L.log(5, TAG, "L21 updateIcon hook failed: " + t);
            }
            L.log(4, TAG, "L21 hooked tile entries ok=" + ok);
            return ok;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L21 hook failed: " + t);
            return false;
        }
    }

    private static void onTileHook(XposedModule module, XposedInterface.Chain chain, String entry) {
        try {
            Object self = chain.getThisObject();
            if (!(self instanceof View)) {
                return;
            }
            View tile = (View)self;
            int mode = CcHooks.parseIntSafe(CcHooks.getProp("debug.hyperclassic.tile_material", "2"), 1);
            // ★ v138：attach 事件 = 窗口模糊链路可用，强制重套一次。
            //   【为什么】applyTileGlass 里 setMiBlurWinExc 必须在 attached 后才能生效。
            //     onLayout 阶段套的那次很可能 attach 前就跑了，TRAY_APPLIED 记了「已套过」
            //     → 之后 attach 了也不会重套 → 磁贴永远是浅色（只有 #1AFFFFFF 底色）。
            //   这里在 attach 时清掉去重记录，让下面的 applyTileMaterial 重新走一遍。
            if ("attach".equals(entry)) {
                View iconFrame138 = CcHooks.findFieldView(tile, "mIconFrame");
                if (iconFrame138 != null) {
                    TRAY_APPLIED.remove(iconFrame138);
                }
            }
            if ("1".equals(CcHooks.getProp("debug.hyperclassic.tile_dump", "0").trim()) && TILEDUMP_COUNT.get() < 3) {
                TILEDUMP_COUNT.incrementAndGet();
                L.log(4, TAG, "L21 DUMP entry=" + entry);
                CcHooks.dumpFullTree(tile, module, 0);
            }
            if (mode > 0) {
                CcHooks.applyTileMaterial(module, tile, mode);
            }
        }
        catch (Throwable t) {
            L.log(5, TAG, "L21 onTileHook(" + entry + ") failed: " + t);
        }
    }

    private static boolean isCaptureMode() {
        // ★ v1.0.2：与 probeOn 共用 1.5s 缓存窗口，拦截器内不再每次反射读属性
        return CcHooks.probeOn();
    }

    private static String fmtGlassArgs(Object[] args) {
        StringBuilder sb = new StringBuilder();
        for (Object a : args) {
            int i;
            if (a instanceof View) {
                View v = (View)a;
                sb.append(" view=").append(v.getClass().getSimpleName()).append("#0x").append(Integer.toHexString(v.getId()).toUpperCase()).append("[").append(v.getWidth()).append("x").append(v.getHeight()).append("]");
                continue;
            }
            if (a instanceof float[]) {
                float[] fa = (float[])a;
                sb.append(" floats[").append(fa.length).append("]=[");
                for (i = 0; i < fa.length; ++i) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    sb.append(fa[i]);
                }
                sb.append("]");
                continue;
            }
            if (a instanceof int[]) {
                int[] ia = (int[])a;
                sb.append(" ints[").append(ia.length).append("]=[");
                for (i = 0; i < ia.length; ++i) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    sb.append(String.format("0x%08X", ia[i]));
                }
                sb.append("]");
                continue;
            }
            sb.append(" ").append(a);
        }
        return sb.toString();
    }

    private static String callerOf() {
        try {
            StackTraceElement[] st;
            for (StackTraceElement e : st = new Throwable().getStackTrace()) {
                String cn = e.getClassName();
                if (cn == null || cn.startsWith("com.mersand.hyperclassic") || cn.contains("XposedBridge") || cn.contains("LSPosed") || cn.contains("de.robv")) continue;
                return cn + "." + e.getMethodName() + ":" + e.getLineNumber();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return "?";
    }

    private static boolean hookGlassCallTracer(XposedModule module, ClassLoader cl) {
        int hooked = 0;
        hooked += CcHooks.hookTracerClass(module, cl, CLS_MI_BLUR, "L39 blur");
        L.log(4, TAG, "L39 tracer hooked=" + (hooked += CcHooks.hookTracerClass(module, cl, CLS_MI_GLASS, "L39 glass")) + " capture=" + CcHooks.isCaptureMode() + "\uff08setprop debug.hyperclassic.capture 1 \u5f00\u542f\u7eaf\u89c2\u6d4b\uff09");
        return hooked > 0;
    }

    private static int hookTracerClass(XposedModule module, ClassLoader cl, String clsName, String label) {
        int n = 0;
        try {
            Class<?> c = Class.forName(clsName, false, cl);
            for (Method m : c.getDeclaredMethods()) {
                String mn;
                if (!Modifier.isStatic(m.getModifiers()) || !Modifier.isPublic(m.getModifiers()) || m.getReturnType() != Void.TYPE || (mn = m.getName()).contains("$")) continue;
                m.setAccessible(true);
                try {
                    module.hook((Executable)m).setId("cc_l39_" + label.substring(4) + "_" + mn + "_" + m.getParameterCount()).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                        Object ret = chain.proceed();
                        if (!CcHooks.isCaptureMode()) {
                            return ret;
                        }
                        try {
                            String key;
                            int hit;
                            Object[] args = chain.getArgs().toArray();
                            String caller = CcHooks.callerOf();
                            String viewKey = "";
                            for (Object a : args) {
                                if (!(a instanceof View)) continue;
                                viewKey = a.getClass().getSimpleName();
                                break;
                            }
                            if ((hit = SEEN_HITS.merge(key = mn + "|" + viewKey, 1, Integer::sum).intValue()) > 3) {
                                return ret;
                            }
                            int i = L39_COUNT.incrementAndGet();
                            if (i > 1200) {
                                return ret;
                            }
                            L.log(4, TAG, label + "." + mn + " <" + caller + ">" + CcHooks.fmtGlassArgs(args));
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                        return ret;
                    });
                    ++n;
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
        }
        catch (Throwable t) {
            L.log(5, TAG, label + " tracer failed: " + t);
        }
        return n;
    }

    private static boolean hookBlurCallProbe(XposedModule module, ClassLoader cl) {
        try {
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, cl);
            Set seen = Collections.synchronizedSet(new LinkedHashSet());
            String[] names = new String[]{"setMiBackgroundBlurModeCompat", "setMiBackgroundBlurRadiusCompat", "setPassWindowBlurEnabledCompat", "setMiViewBlurModeCompat", "setMiBackgroundBlendColors", "addMiBackgroundBlendColorCompat", "setMiGlassCompat"};
            int hooked = 0;
            for (Method m : miBlur.getDeclaredMethods()) {
                String mn = m.getName();
                boolean wanted = false;
                for (String n : names) {
                    if (!n.equals(mn)) continue;
                    wanted = true;
                    break;
                }
                if (!wanted) continue;
                String fname = mn;
                m.setAccessible(true);
                try {
                    module.hook((Executable)m).setId("cc_l20_" + mn + "_" + m.getParameterCount()).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                        Object ret = chain.proceed();
                        if (!CcHooks.probeOn()) {
                            return ret;
                        }
                        try {
                            String key;
                            Object[] args = chain.getArgs().toArray();
                            View target = null;
                            StringBuilder sb = new StringBuilder();
                            for (Object a : args) {
                                if (a instanceof View) {
                                    target = (View)a;
                                    continue;
                                }
                                if (a instanceof int[]) {
                                    int[] ia = (int[])a;
                                    sb.append(" ints=[");
                                    for (int x : ia) {
                                        sb.append(String.format("%08X ", x));
                                    }
                                    sb.append("]");
                                    continue;
                                }
                                sb.append(" ").append(a);
                            }
                            if (target != null && seen.add(key = fname + "|" + target.getClass().getSimpleName()) && seen.size() <= 150) {
                                L.log(4, TAG, "L20 CALL " + fname + " -> " + target.getClass().getSimpleName() + " id=0x" + Integer.toHexString(target.getId()).toUpperCase() + sb + " h=" + target.getHeight() + " elev=" + target.getElevation() + " bg=" + (target.getBackground() == null ? "null" : target.getBackground().getClass().getSimpleName()));
                            }
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                        return ret;
                    });
                    ++hooked;
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            L.log(4, TAG, "L20 blur call probe hooked=" + hooked);
            return hooked > 0;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L20 failed: " + t);
            return false;
        }
    }

    private static boolean hookQsMaterial(XposedModule module, ClassLoader cl, boolean enableGlass) {
        try {
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, cl);
            Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, cl);
            Method setPassWindowBlur = miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE);
            Method setBgBlurMode = miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class);
            Method setViewBlurMode = miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class);
            Method setBgBlurRadius = miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class);
            Method setBlendColors = miBlur.getMethod("setMiBackgroundBlendColors", View.class, int[].class, Float.TYPE);
            Method setGlass = miGlass.getMethod("setMiGlassCompat", View.class, float[].class);
            Method setMaterialType = miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class);
            Field fGlassParams = Class.forName(CLS_GLASS_EFFECT, false, cl).getDeclaredField("glassParamsArray");
            fGlassParams.setAccessible(true);
            Map done = Collections.synchronizedMap(new WeakHashMap());
            Method setId = View.class.getMethod("setId", Integer.TYPE);
            module.hook((Executable)setId).setId("cc_l19_setid").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object result = chain.proceed();
                try {
                    int pn;
                    String cls0;
                    int id2 = (Integer)chain.getArg(0);
                    Object vObj0 = chain.getThisObject();
                    String string = cls0 = vObj0 == null ? "null" : vObj0.getClass().getSimpleName();
                    if ("1".equals(CcHooks.getProp("debug.hyperclassic.id_probe", "0").trim()) && CcHooks.isQsTarget(cls0) && (pn = IDPROBE_COUNT.incrementAndGet()) <= 80) {
                        L.log(4, TAG, String.format("L19 idProbe %s id=0x%08X", cls0, id2));
                    }
                    if (id2 != 2131429767 && id2 != 2131430146) {
                        return result;
                    }
                    Object vObj = chain.getThisObject();
                    if (!(vObj instanceof View)) {
                        return result;
                    }
                    View v = (View)vObj;
                    if (done.containsKey(v)) {
                        return result;
                    }
                    int mode = CcHooks.parseIntSafe(CcHooks.getProp("debug.hyperclassic.qs_material", "2"), 1);
                    if (mode <= 0) {
                        return result;
                    }
                    done.put(v, Boolean.TRUE);
                    if (id2 == 2131429767) {
                        Object arr;
                        int[] nArray;
                        boolean night = CcHooks.isNightMode(v);
                        CcHooks.transparentPanelBg(v);
                        setPassWindowBlur.invoke(null, v, true);
                        setBgBlurMode.invoke(null, 1, v);
                        setViewBlurMode.invoke(null, 0, v);
                        setBgBlurRadius.invoke(null, 100, v);
                        if (night) {
                            int[] nArray2 = new int[1];
                            nArray = nArray2;
                            nArray2[0] = -1944446436;
                        } else {
                            int[] nArray3 = new int[1];
                            nArray = nArray3;
                            nArray3[0] = 0x66FFFFFF;
                        }
                        int[] blend = nArray;
                        setBlendColors.invoke(null, v, blend, Float.valueOf(1.0f));
                        if (mode == 2 && (arr = fGlassParams.get(null)) instanceof float[]) {
                            setGlass.invoke(null, v, (float[])arr);
                            setMaterialType.invoke(null, 1, v);
                        }
                        L.log(4, TAG, "L19 tray materialized mode=" + mode + " night=" + night + " cls=" + v.getClass().getSimpleName());
                    } else {
                        boolean ok = CcHooks.transparentSliderTrack(v);
                        L.log(4, TAG, "L19 brightness track " + (ok ? "transparent" : "NOT FOUND") + " cls=" + v.getClass().getSimpleName());
                    }
                }
                catch (Throwable t) {
                    Throwable c = t.getCause() != null ? t.getCause() : t;
                    L.log(5, TAG, "L19 setId apply failed: " + c);
                }
                return result;
            });
            Class<?> qsPanelCls = null;
            for (String cn : new String[]{"com.android.systemui.qs.MiuiQSPanel", "com.android.systemui.qs.QSPanel"}) {
                try {
                    qsPanelCls = Class.forName(cn, false, cl);
                    break;
                }
                catch (Throwable throwable) {
                }
            }
            if (qsPanelCls == null) {
                L.log(5, TAG, "L19: QSPanel class not found");
                return true;
            }
            Method finishInflate = qsPanelCls.getMethod("onFinishInflate", new Class[0]);
            module.hook((Executable)finishInflate).setId("cc_l19_finishinflate").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object result = chain.proceed();
                try {
                    Object self = chain.getThisObject();
                    if (!(self instanceof View)) {
                        return result;
                    }
                    View root = (View)self;
                    int mode = CcHooks.parseIntSafe(CcHooks.getProp("debug.hyperclassic.qs_material", "2"), 1);
                    root.post(() -> {
                        try {
                            View rootView = root.getRootView();
                            View qsContent = CcHooks.findByClass(rootView, "com.android.systemui.qs.QSContent");
                            View tray = null;
                            if (qsContent != null) {
                                tray = CcHooks.findTrayByStructure(qsContent, module);
                            }
                            if (tray == null) {
                                L.log(5, TAG, "L19 tray not found by structure");
                                return;
                            }
                            L.log(4, TAG, "L19 shade scanned, tray=" + tray.getClass().getSimpleName() + "@0x" + Integer.toHexString(tray.getId()) + " bg=" + (tray.getBackground() == null ? "null" : tray.getBackground().getClass().getSimpleName()));
                            if ("1".equals(CcHooks.getProp("debug.hyperclassic.qs_dump", "0").trim())) {
                                DUMPTREE_COUNT.set(0);
                                CcHooks.dumpFullTree(rootView, module, 0);
                                return;
                            }
                            // ★★★ 2026-11-01 14:22 QS 托盘改用「反射玻璃」配方（媒体 style=3 同款）★★★
                            //
                            //  【为什么换】
                            //    主人：「现在的渲染模式正确」（指 media_style=3）
                            //    → 把同一套 6 步配方用到托盘上。
                            //
                            //  【与上一版（UR 亮度条配方）的区别】
                            //    UR 会开 bgRadius=80 + passWindowBlur + WinExc，
                            //    而 ZF/RG 是「四个模糊开关全置 0」的零模糊版本。
                            //    媒体卡片实测：零模糊才是真玻璃。
                            //
                            //  【位置】
                            //    必须在 applyQSTray 之后，否则被它覆盖。
                            //    所以这里先记下 tray，后面再套。
                            final View rgTray = tray;
                            // ★ v11.0.1：托盘反射玻璃默认开启（v9.74 起为正式配方，
                            //   属性改名迁移时误将其默认值带成 0 导致回退磨砂）
                            boolean trayRgOn = !"0".equals(getProp("debug.hyperclassic.rg_tray", "1").trim());
                            if (trayRgOn && tray.getWidth() > 0 && tray.getHeight() > 0) {
                                // ★★★ v9.74 关键：先预约打标 ★★★
                                //   applyQSTray（下方 8089 行）是【同步】执行的，
                                //   而 RG 是 post 延后两帧。
                                //   不先打标的话，applyQSTray 看不到接管标记，
                                //   又会把 bgRadius=100/bgMode=1/pass=true 写一遍。
                                CcHooks.rgReserve(rgTray);
                                // ★ 2026-11-01 14:48 主人：「圆角是否对齐都可以，托盘统一就行」
                                //   → 固定 24dp 统一圆角，不再从 outline 读（避免继承到怪值）
                                float trayR = 24.0f * tray.getResources()
                                        .getDisplayMetrics().density;
                                final float fTrayR = trayR;
                                final View fTrayV = rgTray;
                                final ClassLoader fTrayCl = cl;
                                final XposedModule fTrayMod = module;
                                // ★★ 延后两帧，确保在 applyQSTray 之后（否则被覆盖）★★
                                //
                                // ★★★ 2026-11-01 15:06 托盘玻璃模糊归零 ★★★
                                //
                                //  【日志实证】
                                //    L19 trayGlass unifiedGlass 1334x325 blur=80 outline=true
                                //    L43 diag ... b=80 blurOpen=true
                                //    L19 tray applied #1 mode=2 ... ← applyQSTray 里
                                //        setMiGlassBlurRadius(v, 100, 100)  (mode==2)
                                //        setMiBackgroundBlurRadius(v, 100)
                                //
                                //  所以即使 RG 的 6 步配方把四个开关置 0，
                                //  applyQSTray / applyUnifiedGlass 依然会再把模糊开回来。
                                //
                                //  修法：RG 之前先显式把所有模糊相关参数置 0，
                                //  RG 之后再重复一次（防止 RG 期间被重新设回）。
// ★★★ v9.74 简化：不再 pre/post zeroAllBlur ★★★
                                 //
                                 //  【为什么去掉】
                                 //    旧代码在 RG 前后各补一次 zeroAllBlur，
                                 //    是"套完再拦截"的典型。现在源头已经掐断：
                                 //      applyQSTray        → rgTray 时跳过 4 个磨砂写入
                                 //      applyUnifiedGlass  → rgTray 时整个不执行
                                 //      applyBlurScaleOnly → rg_notif 时整个不执行
                                 //    没有任何人再开磨砂，自然不需要事后清理。
                                 fTrayV.post(() -> fTrayV.post(() -> {
                                     applyReflectiveGlass(fTrayMod, fTrayV, fTrayCl, "L19 tray RG", fTrayR);
                                 }));
                            }
                            // 旧的亮度条配方（保留，但默认关闭）
                            if (urOn("ur_tray") && tray.getWidth() > 0 && tray.getHeight() > 0) {
                                float trayR = (float) (24.0 * tray.getResources()
                                        .getDisplayMetrics().density);
                                try {
                                    Outline o = new Outline();
                                    ViewOutlineProvider op = tray.getOutlineProvider();
                                    if (op != null) {
                                        op.getOutline(tray, o);
                                        if (o.getRadius() > 0) {
                                            trayR = o.getRadius();
                                        }
                                    }
                                } catch (Throwable ignored) {
                                }
                                applySliderRecipeFromProps(module, tray, cl, "L19 tray UR", trayR);
                            }
                            CcHooks.dumpParents(tray, module);
                            if (mode > 0) {
                                View slider;
                                CcHooks.applyQSTray(module, tray, mode, setPassWindowBlur, setBgBlurMode, setViewBlurMode, setBgBlurRadius, setBlendColors, setGlass, setMaterialType, fGlassParams);
                                View footer = tray.getRootView().findViewById(2131429780);
                                if (footer != null) {
                                    CcHooks.transparentPanelBg(footer);
                                }
                                if ((slider = tray.getRootView().findViewById(2131430146)) != null) {
                                    boolean ok = CcHooks.transparentSliderTrack(slider);
                                    CcHooks.applySliderMaterial(module, slider, mode);
                                    CcHooks.installSliderGuard(module, slider, mode);
                                    L.log(4, TAG, "L19 brightness track " + (ok ? "tinted" : "NOT FOUND") + " cls=" + slider.getClass().getSimpleName());
                                }
                                CcHooks.tintPageIndicator(module, tray.getRootView());
                                View piView = CcHooks.findByClass(tray.getRootView(), "com.android.systemui.qs.MiuiPageIndicator");
                                if (piView instanceof ViewGroup) {
                                    CcHooks.installPageIndicatorGuard(module, (ViewGroup)piView);
                                }
                            }
                        }
                        catch (Throwable t) {
                            L.log(5, TAG, "L19 shade apply failed: " + t);
                        }
                    });
                }
                catch (Throwable t) {
                    Throwable c = t.getCause() != null ? t.getCause() : t;
                    L.log(5, TAG, "L19 finishInflate failed: " + c);
                }
                return result;
            });
            L.log(4, TAG, "L19 hooked QSPanel onFinishInflate");
            try {
                Class<?> qsPanelCls2 = Class.forName("com.android.systemui.qs.MiuiQSPanel", false, cl);
                Method cfg = qsPanelCls2.getDeclaredMethod("onConfigurationChanged", Configuration.class);
                cfg.setAccessible(true);
                module.hook((Executable)cfg).setId("cc_l19_config").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object r = chain.proceed();
                    try {
                        Object self = chain.getThisObject();
                        if (self instanceof View) {
                            View root = (View)self;
                            root.postDelayed(() -> CcHooks.applyTrayByStructure(module, root, setPassWindowBlur, setBgBlurMode, setViewBlurMode, setBgBlurRadius, setBlendColors, setGlass, setMaterialType, fGlassParams), 300L);
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    return r;
                });
                L.log(4, TAG, "L19 hooked QSPanel onConfigurationChanged");
            }
            catch (Throwable t) {
                L.log(5, TAG, "L19 config hook failed: " + t);
            }
            L.log(4, TAG, "L19 hooked View.setId + onAttachedToWindow (QS material)");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L19 failed: " + t);
            return false;
        }
    }

    private static void applyTrayByStructure(XposedModule module, View root, Method setPassWindowBlur, Method setBgBlurMode, Method setViewBlurMode, Method setBgBlurRadius, Method setBlendColors, Method setGlass, Method setMaterialType, Field fGlassParams) {
        try {
            View slider;
            View tray;
            int mode = CcHooks.parseIntSafe(CcHooks.getProp("debug.hyperclassic.qs_material", "2"), 1);
            if (mode <= 0) {
                return;
            }
            View rootView = root.getRootView();
            View qsContent = CcHooks.findByClass(rootView, "com.android.systemui.qs.QSContent");
            View view = tray = qsContent == null ? null : CcHooks.findTrayByStructure(qsContent, module);
            if (tray == null) {
                L.log(5, TAG, "L19 re-apply: tray not found");
                return;
            }
            // ★★★ v9.74 关键：这是「切深色变磨砂」的真正元凶 ★★★
            //
            //  【触发链】
            //    MiuiQSPanel.onConfigurationChanged（被我们 hook，8247 行）
            //      → 300ms 后 → applyTrayByStructure（本函数）
            //      → applyQSTray → bgRadius=100/bgMode=1/pass=true  ← 磨砂回来了
            //
            //  【为什么之前没抓到】
            //    RG 的去重是「同 View 同 night 只套一次」，
            //    配置变化后 night 变了，但这里是 applyQSTray 抢先跑，
            //    RG 根本没被重新调用（日志 21 次 apply hook 全是 night=false）。
            //
            //  【修法】
// RG 接管托盘时，这里不再走 applyQSTray 磨砂，
            //    而是重新套一次反射玻璃。
            // ★ v11.0.1：默认开启（与 9311 行主路径同步）
            if (!"0".equals(getProp("debug.hyperclassic.rg_tray", "1").trim())) {
                final View tv = tray;
                final ClassLoader tcl = setGlass.getDeclaringClass().getClassLoader();
                final XposedModule mod2 = module;
                float cr = 24.0f * tray.getResources().getDisplayMetrics().density;
                L.log(4, TAG, "L19 cfgChg re-apply RG night=" + CcHooks.isNightMode(tray));
                CcHooks.rgReserve(tv);
                tray.post(() -> {
                    try {
                        applyReflectiveGlass(mod2, tv, tcl, "L19 tray RG cfgChg", cr);
                    } catch (Throwable ignored) {
                    }
                });
            } else {
                L.log(4, TAG, "L19 re-apply night=" + CcHooks.isNightMode(tray));
                CcHooks.applyQSTray(module, tray, mode, setPassWindowBlur, setBgBlurMode, setViewBlurMode, setBgBlurRadius, setBlendColors, setGlass, setMaterialType, fGlassParams);
            }
            View footer = rootView.findViewById(2131429780);
            if (footer != null) {
                CcHooks.transparentPanelBg(footer);
            }
            if ((slider = rootView.findViewById(2131430146)) != null) {
                CcHooks.transparentSliderTrack(slider);
                int m2 = CcHooks.parseIntSafe(CcHooks.getProp("debug.hyperclassic.qs_material", "2"), 1);
                CcHooks.applySliderMaterial(module, slider, m2);
                CcHooks.installSliderGuard(module, slider, m2);
            }
        }
        catch (Throwable t) {
            L.log(5, TAG, "L19 re-apply failed: " + t);
        }
    }

    private static View findTrayByStructure(View qsContent, XposedModule module) {
        if (!(qsContent instanceof ViewGroup)) {
            return null;
        }
        ViewGroup vg = (ViewGroup)qsContent;
        for (int i = 0; i < vg.getChildCount(); ++i) {
            View c = vg.getChildAt(i);
            Drawable bg = c.getBackground();
            if (!(bg instanceof GradientDrawable) || c.getHeight() <= 0) continue;
            L.log(4, TAG, "L19 structure match: " + c.getClass().getSimpleName() + " id=0x" + Integer.toHexString(c.getId()).toUpperCase() + " h=" + c.getHeight() + " w=" + c.getWidth());
            return c;
        }
        return null;
    }

    private static View findByClass(View root, String className) {
        if (root == null) {
            return null;
        }
        String cn = root.getClass().getName();
        if (className.equals(cn) || cn.contains(className)) {
            return root;
        }
        if (root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup)root;
            for (int i = 0; i < vg.getChildCount(); ++i) {
                View r = CcHooks.findByClass(vg.getChildAt(i), className);
                if (r == null) continue;
                return r;
            }
        }
        return null;
    }

    private static void dumpFullTree(View v, XposedModule module, int depth) {
        if (v == null || depth > 14) {
            return;
        }
        try {
            int n = DUMPTREE_COUNT.incrementAndGet();
            if (n <= 90) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < depth; ++i) {
                    sb.append("\u00b7");
                }
                sb.append(v.getClass().getSimpleName()).append(" id=0x").append(Integer.toHexString(v.getId()).toUpperCase());
                Drawable bg = v.getBackground();
                if (bg == null) {
                    sb.append(" bg=null");
                } else if (bg instanceof ColorDrawable) {
                    sb.append(" bgColor=#").append(String.format("%08X", ((ColorDrawable)bg).getColor()));
                } else if (bg instanceof GradientDrawable) {
                    sb.append(" bgGradient");
                } else {
                    sb.append(" bg=").append(bg.getClass().getSimpleName());
                }
                int[] loc = new int[2];
                v.getLocationOnScreen(loc);
                sb.append(" scrY=").append(loc[1]).append("..").append(loc[1] + v.getHeight()).append(" h=").append(v.getHeight()).append(" w=").append(v.getWidth()).append(" vis=").append(v.getVisibility());
                L.log(4, TAG, "L19 TREE " + sb);
            }
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup)v;
                for (int i = 0; i < vg.getChildCount(); ++i) {
                    CcHooks.dumpFullTree(vg.getChildAt(i), module, depth + 1);
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void dumpParents(View v, XposedModule module) {
        try {
            View cur = v;
            for (int depth = 0; cur != null && depth < 12; ++depth) {
                Drawable bg = cur.getBackground();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < depth; ++i) {
                    sb.append("  ");
                }
                sb.append("^").append(depth).append(" ").append(cur.getClass().getName()).append(" id=0x").append(Integer.toHexString(cur.getId()).toUpperCase()).append(" bg=").append(bg == null ? "null" : bg.getClass().getSimpleName()).append(" w=").append(cur.getWidth()).append(" h=").append(cur.getHeight()).append(" y=").append(cur.getTop());
                L.log(4, TAG, "L19 parent " + sb);
                ViewParent p = cur.getParent();
                cur = p instanceof View ? (View)p : null;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static View findTrayByWalking(View start, XposedModule module) {
        ViewParent p;
        View v = start.findViewById(2131429767);
        if (v != null) {
            return v;
        }
        View cur = start;
        for (int i = 0; i < 8 && cur != null && (p = cur.getParent()) instanceof View; ++i) {
            View pv = (View)p;
            View f = pv.findViewById(2131429767);
            if (f != null) {
                return f;
            }
            cur = pv;
        }
        return null;
    }

    private static void dumpSubtree(View v, XposedModule module, int depth) {
        if (v == null || depth > 6) {
            return;
        }
        try {
            int n;
            Drawable bg = v.getBackground();
            boolean hasBg = bg != null;
            boolean isQs = CcHooks.isQsTarget(v.getClass().getName());
            if ((hasBg || isQs) && (n = DUMPTREE_COUNT.incrementAndGet()) <= 60) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < depth; ++i) {
                    sb.append("  ");
                }
                sb.append(v.getClass().getSimpleName()).append(" id=0x").append(Integer.toHexString(v.getId()).toUpperCase()).append(" bg=").append(bg == null ? "null" : bg.getClass().getSimpleName()).append(" w=").append(v.getWidth()).append(" h=").append(v.getHeight());
                L.log(4, TAG, "L19 tree " + sb);
            }
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup)v;
                for (int i = 0; i < vg.getChildCount() && i < 20; ++i) {
                    CcHooks.dumpSubtree(vg.getChildAt(i), module, depth + 1);
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static float[] loadGlassParamsFromRes(View v) {
        try {
            Resources res = v.getResources();
            String[] sa = res.getStringArray(2130903246);
            if (sa == null || sa.length == 0) {
                return null;
            }
            float[] out = new float[sa.length];
            int n = 0;
            for (String s : sa) {
                if (s == null) continue;
                try {
                    out[n++] = Float.parseFloat(s.trim());
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            if (n == 0) {
                return null;
            }
            if (n == out.length) {
                return out;
            }
            float[] trimmed = new float[n];
            System.arraycopy(out, 0, trimmed, 0, n);
            return trimmed;
        }
        catch (Throwable t) {
            return null;
        }
    }

    private static void callSetMiBlurWinExc(View v, String winName, XposedModule module) {
        try {
            Method m = View.class.getMethod("setMiBlurWinExc", String.class);
            m.setAccessible(true);
            m.invoke(v, winName);
        }
        catch (Throwable t) {
            Throwable c = t.getCause() != null ? t.getCause() : t;
            L.log(5, TAG, "L19 setMiBlurWinExc failed: " + c);
        }
    }

    // ==================================================================
    // ★★★ 2026-11-01 12:55 【亮度条配方】通用化 ★★★
    //
    //  【主人的核心判断】
    //    「亮度条的参数只是偏鲜艳，但本身是真玻璃」
    //    → 亮度条是全系统唯一**真正跑出反射玻璃**的控件，
    //      所以它的配方就是「标准答案」。
    //      目标：把这套配方统一到 通知 / QS托盘 / 清除按钮 / Toast。
    //
    //  【为什么亮度条能成，其他不行（关键差异）】
    //    亮度条不是官方 Effect 管的对象，是我们「手搓」的，
    //    所以它必须自己把每个前置条件都显式设好：
    //
    //      ① 背景：GradientDrawable 5% 白（0x0DFFFFFF）+ 圆角
    //         ★ 不能是透明 shape —— 透明 shape 底下什么都没有，
    //           玻璃层没有东西可以"折射"，看起来就没有质感。
    //           5% 白提供了一个极淡的底，让玻璃有"实体感"。
    //
    //      ② setMiViewBlurModeCompat(1)   view 级模糊开关
    //      ③ blend = [0x40FFFFFF] 单元素 25% 白
    //         ★ 单元素而不是官方的 3 对 6 元素。
    //           官方 6 元素（灰+黄绿+白）是给模糊层上重色 → 磨砂感。
    //           单元素淡白不会盖住壁纸色彩 → 保持通透。
    //
    //      ④ 模糊链路四连（官方 Effect 从不显式调）：
    //           setMiBackgroundBlurRadiusCompat(80)
    //           setMiBackgroundBlurModeCompat(1)
    //           setPassWindowBlurEnabledCompat(true)
    //           setMiBlurWinExc("NotificationShade")   ← ★ 最关键
    //         WinExc 是告诉引擎「这个 View 属于哪个窗口的模糊域」，
    //         不设的话 View 根本拿不到窗口模糊 → 只能拿到预制属性。
    //
    //      ⑤ setMiGlassCompat(42参数) + setMiViewMaterialTypeCompat(1)
    //      ⑥ setMiGlassBlurRadius(80, 80)
    //
    //  【与媒体卡片当前实现（style=3）的差异】
    //    媒体卡片为了"通透"把所有模糊都关了（bgMode=0/bgRadius=0/pass=false），
    //    结果玻璃层没有模糊支撑 → 只剩下白色遮罩 → 白板感 + 偏鲜艳。
    //    而亮度条是"开着模糊但用淡blend"，这才是真玻璃。
    //
    //  【开关】
    //    debug.hyperclassic.unified_slider_recipe = 1  启用统一配方
    //    debug.hyperclassic.ur_radius   0..40   白底浓度（默认 5，即 0x0DFFFFFF）
    //    debug.hyperclassic.ur_blend    0..100  blend 白浓度（默认 25）
    //    debug.hyperclassic.ur_blur     0..150  模糊半径（默认 80）
    // ==================================================================

    /** 已应用「亮度条配方」的 View */
    private static final java.util.Set<View> SLIDER_RECIPE_APPLIED =
            Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));

    /**
     * 把「亮度条配方」套到一个 View 上。任何 View 都能用。
     *
     * @param cornerRadius 圆角半径（px）；传 -1 表示用高度的一半（胶囊形）
     * @param radiusPct    白底浓度 0..40（对应 alpha = pct*255/100）
     * @param blendPct     blend 白浓度 0..100
     * @param blurRadius   模糊半径
     */
    private static boolean applySliderRecipe(XposedModule module, View v, ClassLoader cl,
                                             String tag, float cornerRadius,
                                             int radiusPct, int blendPct, int blurRadius) {
        try {
            if (v.getWidth() <= 0 || v.getHeight() <= 0) {
                return false;
            }
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, cl);
            Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, cl);

            // ---------- ① 背景：极淡白底 + 圆角 ----------
            int bgAlpha = Math.round(clampInt(radiusPct, 0, 40) * 2.55f);
            float r = cornerRadius >= 0 ? cornerRadius : v.getHeight() / 2.0f;
            GradientDrawable round = new GradientDrawable();
            round.setShape(GradientDrawable.RECTANGLE);
            round.setColor((bgAlpha << 24) | 0x00FFFFFF);
            round.setCornerRadius(r);
            v.setBackground(round);
            v.setClipToOutline(true);
            final float fr = r;
            v.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), fr);
                }
            });
            v.invalidateOutline();

            // ---------- ② view 级模糊开关 ----------
            try {
                miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class)
                        .invoke(null, 1, v);
            } catch (Throwable ignored) {
            }

            // ---------- ③ blend：单元素淡白 ----------
            int blendAlpha = Math.round(clampInt(blendPct, 0, 100) * 2.55f);
            int[] blend = new int[]{(blendAlpha << 24) | 0x00FFFFFF};
            boolean blendOk = false;
            try {
                miBlur.getMethod("setMiBackgroundBlendColorsNew$default", View.class, int[].class)
                        .invoke(null, v, blend);
                blendOk = true;
            } catch (Throwable ignored) {
            }
            if (!blendOk) {
                try {
                    miBlur.getMethod("setMiBackgroundBlendColors", View.class, int[].class, Float.TYPE)
                            .invoke(null, v, blend, Float.valueOf(1.0f));
                    blendOk = true;
                } catch (Throwable ignored) {
                }
            }

            // ---------- ④ 模糊链路四连（★ 最关键） ----------
            try {
                miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class)
                        .invoke(null, blurRadius, v);
                miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class)
                        .invoke(null, 1, v);
                miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE)
                        .invoke(null, v, true);
                if (v.isAttachedToWindow()) {
                    callSetMiBlurWinExc(v, "NotificationShade", module);
                }
            } catch (Throwable t) {
                L.log(5, TAG, tag + " UR blur chain failed: " + t);
            }

            // ---------- ⑤ 玻璃材质 ----------
            float[] garr = loadGlassParamsFromRes(v);
            if (garr == null || garr.length < 20) {
                garr = (float[]) GLASS_PARAMS_FALLBACK.clone();
            } else {
                garr = (float[]) garr.clone();
            }
            if (garr.length > 21) {
                garr[21] = Math.max(20, blurRadius);
            }
            try {
                miGlass.getMethod("setMiGlassCompat", View.class, float[].class)
                        .invoke(null, v, garr);
                miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class)
                        .invoke(null, 1, v);
            } catch (Throwable t) {
                L.log(5, TAG, tag + " UR glass failed: " + t);
            }

            // ---------- ⑥ 玻璃模糊半径 ----------
            try {
                miGlass.getMethod("setMiGlassBlurRadius", View.class, Integer.TYPE, Integer.TYPE)
                        .invoke(null, v, blurRadius, blurRadius);
            } catch (Throwable ignored) {
            }

            v.invalidate();
            L.log(4, TAG, tag + " UR applied size=" + v.getWidth() + "x" + v.getHeight()
                    + " bg=#" + String.format("%08X", (bgAlpha << 24) | 0x00FFFFFF)
                    + " blend=" + (blendOk ? String.format("#%08X", blend[0]) : "failed")
                    + " blur=" + blurRadius + " radius=" + Math.round(fr)
                    + " cls=" + v.getClass().getSimpleName());
            return true;
        } catch (Throwable t) {
            L.log(5, TAG, tag + " UR failed: " + t);
            return false;
        }
    }

    /** 用属性开关读取参数，套用亮度条配方（带去重）。 */
    private static void applySliderRecipeFromProps(XposedModule module, View v, ClassLoader cl,
                                                   String tag, float cornerRadius) {
        // ★ 2026-11-01 13:00 总开关（默认关，主人验证后再开）
        if (!"1".equals(getProp("debug.hyperclassic.unified_slider_recipe", "1").trim())) {
            return;
        }
        if (!SLIDER_RECIPE_APPLIED.add(v)) {
            return;
        }
        int rp = clampInt(parseInt(getProp("debug.hyperclassic.ur_radius", "5").trim(), 5), 0, 40);
        int bp = clampInt(parseInt(getProp("debug.hyperclassic.ur_blend", "25").trim(), 25), 0, 100);
        int br = clampInt(parseInt(getProp("debug.hyperclassic.ur_blur", "0").trim(), 80), 0, 150);
        if (!applySliderRecipe(module, v, cl, tag, cornerRadius, rp, bp, br)) {
            SLIDER_RECIPE_APPLIED.remove(v);
        }
    }

    /** 每个元素单独开关，方便主人逐个验证。 */
    private static boolean urOn(String key) {
        return "1".equals(getProp("debug.hyperclassic." + key, "1").trim());
    }

    private static void installTrayBgGuard(XposedModule module, View tray) {
        try {
            if (!TRAY_GUARDED.add(tray)) {
                return;
            }
            CcHooks.addDrawGuard(module, tray, view -> {
                try {
                    Drawable bg = tray.getBackground();
                    if (bg != null && !CcHooks.isDrawableFullyTransparent(bg)) {
                        CcHooks.transparentPanelBg(tray);
                        L.log(4, TAG, "L19 guard: bg re-transparent " + bg.getClass().getSimpleName());
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            });
            L.log(4, TAG, "L19 draw guard installed");
        }
        catch (Throwable t) {
            L.log(5, TAG, "L19 draw guard failed: " + t);
        }
    }

    private static boolean isDrawableFullyTransparent(Drawable d) {
        if (d == null) {
            return true;
        }
        try {
            if (d instanceof ColorDrawable) {
                return Color.alpha((int)((ColorDrawable)d).getColor()) == 0;
            }
            if (d instanceof GradientDrawable) {
                try {
                    Field f = GradientDrawable.class.getDeclaredField("mFillPaint");
                    f.setAccessible(true);
                    Object p = f.get(d);
                    if (p instanceof Paint) {
                        return Color.alpha((int)((Paint)p).getColor()) == 0;
                    }
                }
                catch (Throwable f) {
                    // empty catch block
                }
                return false;
            }
            if (d instanceof LayerDrawable) {
                LayerDrawable ld = (LayerDrawable)d;
                for (int i = 0; i < ld.getNumberOfLayers(); ++i) {
                    if (CcHooks.isDrawableFullyTransparent(ld.getDrawable(i))) continue;
                    return false;
                }
                return true;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return false;
    }

    private static void applyQSTray(XposedModule module, View v, int mode, Method setPassWindowBlur, Method setBgBlurMode, Method setViewBlurMode, Method setBgBlurRadius, Method setBlendColors, Method setGlass, Method setMaterialType, Field fGlassParams) {
        block28: {
            try {
                // ★★★ v9.74：RG 接管托盘时，只跳过「磨砂写入」，其余步骤照常 ★★★
                //
                //  【官方源码实证】MediaViewGlassEffect / NotificationRowGlassEffect
                //    全程不调用：
                //      setMiBackgroundBlurModeCompat   ← 磨砂开关
                //      setMiBackgroundBlurRadiusCompat ← 磨砂半径
                //      setPassWindowBlurEnabledCompat  ← 窗口模糊
                //      setMiGlassBlurRadius            ← 折射半径（在 42 项数组里）
                //    只调用 setMiViewBlurModeCompat(1) —— 这个才是玻璃自带的。
                //
                //  【旧行为的危害】
                //    这里无条件设 bgRadius=100 / bgMode=1 / pass=true / glassR=100，
                //    而 L19 的 RG 又延后两帧执行 → 顺序变成
                //      「先开磨砂 → 再套玻璃 → 再靠定时器压回去」
                //    自己打自己，永远压不干净。
                //
                //  【修法】保留透明背景/blend 等必要步骤，只把 4 个磨砂写入跳过。
                final boolean rgTray = rgOwned(v);
                if (rgTray) {
                    L.log(4, TAG, "L19 trayMac blur-skip (rg owns)");
                }
                int n;
                boolean night = CcHooks.isNightMode(v);
                int key = mode * 10 + (night ? 1 : 0);
                Integer prev = TRAY_APPLIED.get(v);
                if (prev != null && prev == key) {
                    return;
                }
                TRAY_APPLIED.put(v, key);
                if ("1".equals(CcHooks.getProp("debug.hyperclassic.qs_red", "0").trim())) {
                    v.setBackgroundColor(-65536);
                    L.log(4, TAG, "L19 RED MARK on " + v.getClass().getSimpleName() + " id=0x" + Integer.toHexString(v.getId()).toUpperCase() + " w=" + v.getWidth() + " h=" + v.getHeight());
                    return;
                }
                // ★ v65：blend 硬编码 0x66FFFFFF（40% 白）在深色模式下把收起托盘玻璃
                //   刷成浅色（展开态走官方链路是深色 → 主人报告「收起浅、展开深」）。
                //   修复：按日夜模式选 blend——深色用官方深色 blend 色（黑白反转）。
                boolean nightBlend65 = CcHooks.isNightMode(v);
                int[] blend = new int[]{nightBlend65 ? 0x59000000 : 0x66FFFFFF};
                CcHooks.transparentPanelBg(v);
                setViewBlurMode.invoke(null, 1, v);
                boolean blendDone = false;
                try {
                    Method blendNew = Class.forName(CLS_MI_BLUR, false, setBlendColors.getDeclaringClass().getClassLoader()).getMethod("setMiBackgroundBlendColorsNew$default", View.class, int[].class);
                    blendNew.setAccessible(true);
                    blendNew.invoke(null, v, blend);
                    blendDone = true;
                }
                catch (Throwable blendNew) {
                    // empty catch block
                }
                if (!blendDone) {
                    setBlendColors.invoke(null, v, blend, Float.valueOf(1.0f));
                }
                if (!rgTray) {
                    setBgBlurRadius.invoke(null, 100, v);
                    setBgBlurMode.invoke(null, 1, v);
                    setPassWindowBlur.invoke(null, v, true);
                    try {
                        Method setGlassRadius = Class.forName(CLS_MI_GLASS, false, setGlass.getDeclaringClass().getClassLoader()).getMethod("setMiGlassBlurRadius", View.class, Integer.TYPE, Integer.TYPE);
                        setGlassRadius.invoke(null, v, mode == 2 ? 100 : 0, mode == 2 ? 100 : 0);
                    }
                    catch (Throwable setGlassRadius) {
                        // empty catch block
                    }
                }
                if (v.isAttachedToWindow()) {
                    CcHooks.callSetMiBlurWinExc(v, "NotificationShade", module);
                }
                if (mode == 2) {
                    float[] garr = null;
                    Object arr = null;
                    try {
                        arr = fGlassParams.get(null);
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    if (arr instanceof float[]) {
                        garr = (float[])arr;
                    }
                    if (garr == null || garr.length < 20) {
                        garr = CcHooks.loadGlassParamsFromRes(v);
                    }
                    if (garr != null && garr.length >= 20) {
                        setGlass.invoke(null, v, garr);
                        setMaterialType.invoke(null, 1, v);
                        L.log(4, TAG, "L19 glass params len=" + garr.length + " src=" + (arr instanceof float[] ? "field" : "res") + " p19=" + garr[19]);
                    } else {
                        L.log(5, TAG, "L19 glass params unavailable");
                    }
                }
                if ((n = QSTRAY_COUNT.incrementAndGet()) <= 6) {
                    L.log(4, TAG, "L19 tray applied #" + n + " mode=" + mode + " night=" + night + " w=" + v.getWidth() + " h=" + v.getHeight() + " bg=" + (v.getBackground() == null ? "null" : v.getBackground().getClass().getSimpleName()));
                }
                if (!rgTray && v.getWidth() > 0 && v.getHeight() > 0) {
                    CcHooks.applyUnifiedGlass(module, v, setGlass.getDeclaringClass().getClassLoader(), "L19 trayGlass", CcHooks.currentBlurRadius(), true);
                }
                try {
                    boolean allowSelf = "1".equals(CcHooks.getProp("debug.hyperclassic.tray_selfblur", "0").trim());
                    Class<?> miBlurCls = Class.forName(CLS_MI_BLUR, false, setGlass.getDeclaringClass().getClassLoader());
                    miBlurCls.getMethod("setMiSelfBlurTypeCompat", Integer.TYPE, View.class).invoke(null, allowSelf ? 0 : -1, v);
                    if (!rgTray) {
                        miBlurCls.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE).invoke(null, v, true);
                    }
                    int nn = QSTRAY_COUNT.get();
                    if (nn <= 6) {
                        L.log(4, TAG, "L19 selfBlurType=" + (allowSelf ? 0 : -1) + " passWindow=true");
                    }
                }
                catch (Throwable t) {
                    L.log(5, TAG, "L19 selfBlurType failed: " + t);
                }
                CcHooks.installTrayBgGuard(module, v);
                if (!"1".equals(CcHooks.getProp("debug.hyperclassic.qs_verify", "1").trim())) break block28;
                try {
                    Class<?> mb = setPassWindowBlur.getDeclaringClass();
                    Object pass = mb.getMethod("getPassWindowBlurEnabledCompat", View.class).invoke(null, v);
                    Object bmode = null;
                    Object radius = null;
                    try {
                        bmode = mb.getMethod("getMiBackgroundBlurModeCompat", View.class).invoke(null, v);
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    try {
                        radius = mb.getMethod("getMiBackgroundBlurRadiusCompat", View.class).invoke(null, v);
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    L.log(4, TAG, "L19 VERIFY pass=" + pass + " bgMode=" + bmode + " radius=" + radius + " attached=" + v.isAttachedToWindow() + " hwAccel=" + v.isHardwareAccelerated() + " layerType=" + v.getLayerType() + " elevation=" + v.getElevation() + " clipToOutline=" + v.getClipToOutline());
                }
                catch (Throwable t) {
                    L.log(5, TAG, "L19 VERIFY failed: " + t);
                }
            }
            catch (Throwable t) {
                Throwable c = t.getCause() != null ? t.getCause() : t;
                L.log(5, TAG, "L19 applyQSTray failed: " + c);
            }
        }
    }

    private static void transparentPanelBg(View v) {
        try {
            Drawable bg = v.getBackground();
            if (bg == null) {
                return;
            }
            if (bg instanceof GradientDrawable) {
                ((GradientDrawable)bg).setColor(0);
                return;
            }
            if (bg instanceof ColorDrawable) {
                v.setBackgroundColor(0);
                return;
            }
            if (bg instanceof LayerDrawable) {
                LayerDrawable ld = (LayerDrawable)bg;
                boolean allTransparent = true;
                for (int i = 0; i < ld.getNumberOfLayers(); ++i) {
                    Drawable layer = ld.getDrawable(i);
                    if (layer instanceof GradientDrawable) {
                        ((GradientDrawable)layer).setColor(0);
                        continue;
                    }
                    if (layer instanceof ColorDrawable) {
                        ColorDrawable cd = (ColorDrawable)layer;
                        if (Color.alpha((int)cd.getColor()) == 0) continue;
                        allTransparent = false;
                        continue;
                    }
                    if (layer == null || CcHooks.forceDrawableTransparent(layer)) continue;
                    allTransparent = false;
                }
                if (!allTransparent) {
                    v.setBackgroundColor(0);
                }
                return;
            }
            if (!CcHooks.forceDrawableTransparent(bg)) {
                v.setBackgroundColor(0);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    // ★ v133：清掉一个 View 上「我们挂的玻璃」，但保留官方背景。
    //
    //  【使用场景】WLAN 详情页顶部的蓝色胶囊（已连接项）。
    //    主人指示：那个蓝色半透明胶囊是**官方行为**，玻璃是 hook 盖上去的，
    //    而 wifi 页面是异步加载的、蓝色胶囊常驻，导致玻璃延迟盖上去、观感不统一。
    //    → 所以要把「玻璃」摘掉，但**不能**把官方胶囊背景也一起清掉。
    //
    //  【MiuiX 玻璃的挂载方式（本模块所有 apply*Glass 的共识）】
    //    ① miGlass.setMiViewMaterialTypeCompat(1, v)   ← 1 = 玻璃材质（开关）
    //    ② miBlur.setMiViewBlurModeCompat(1, v)        ← 1 = view 级模糊
    //    ③ miBlur.setPassWindowBlurEnabledCompat(v, true)
    //    ④ miGlass.setMiGlassCompat(v, params)         ← 42 项玻璃参数
    //    ⑤ miBlur.setMiBackgroundBlendColors(...)      ← 染色
    //    ⑥ setOutlineProvider + setClipToOutline(true) ← 我们自己的圆角裁剪
    //
    //  【清玻璃的正确顺序】先关材质类型（②→①），再关 blurMode，
    //    最后把 outline 恢复成官方那个（或干脆不裁）。
    //    注意：**不要**调 setBackground(null)**，那会把官方蓝色胶囊也干掉。
    private static void clearOurGlass(View v, ClassLoader cl, String tag) {
        if (v == null) return;
        try {
            Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, cl);
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, cl);
            // ① 材质类型 → 0（普通），这是「关玻璃」的总开关
            try {
                miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class)
                        .invoke(null, 0, v);
            } catch (Throwable ignored) {}
            // ② view 级模糊模式 → 0
            try {
                miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class)
                        .invoke(null, 0, v);
            } catch (Throwable ignored) {}
            // ③ 关掉窗口模糊透传
            try {
                miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE)
                        .invoke(null, v, false);
            } catch (Throwable ignored) {}
            // ④ 清染色（否则残留色调）
            try {
                miBlur.getMethod("clearMiBackgroundBlendColorCompat", View.class).invoke(null, v);
            } catch (Throwable ignored) {}
            // ⑤ 关玻璃 outline
            try {
                miBlur.getMethod("setGlassOutlineEnable", View.class, Boolean.TYPE)
                        .invoke(null, v, false);
            } catch (Throwable ignored) {}
            // ⑥ 不再用我们的圆角裁剪（官方胶囊自己会画圆角）
            try {
                v.setClipToOutline(false);
            } catch (Throwable ignored) {}
            v.invalidate();
            L.log(4, TAG, tag + " clearGlass done");
        } catch (Throwable t) {
            L.log(5, TAG, tag + " clearGlass failed: " + t);
        }
    }

    private static boolean forceDrawableTransparent(Drawable d) {
        if (d == null) {
            return false;
        }
        try {
            Method m = d.getClass().getMethod("setColor", Integer.TYPE);
            m.setAccessible(true);
            m.invoke(d, 0);
            d.invalidateSelf();
            return true;
        }
        catch (Throwable throwable) {
            try {
                d.mutate();
                d.setTint(0);
                d.invalidateSelf();
                return true;
            }
            catch (Throwable throwable2) {
                return false;
            }
        }
    }

    private static boolean transparentSliderTrack(View slider) {
        try {
            Drawable thumb;
            Drawable progLayer;
            View found;
            View real = slider;
            if (!(slider instanceof SeekBar) && (found = slider.findViewById(2131430146)) != null) {
                real = found;
            }
            Drawable pd = null;
            if (real instanceof ProgressBar) {
                pd = ((ProgressBar)real).getProgressDrawable();
            }
            if (pd == null) {
                pd = real.getBackground();
            }
            if (!(pd instanceof LayerDrawable)) {
                return false;
            }
            LayerDrawable ld = (LayerDrawable)pd;
            boolean changed = false;
            Drawable bgLayer = ld.findDrawableByLayerId(0x1020000);
            if (bgLayer != null) {
                changed |= CcHooks.tintDrawable(bgLayer, 0x33FFFFFF);
            }
            if ((progLayer = ld.findDrawableByLayerId(16908301)) != null) {
                changed |= CcHooks.tintDrawable(progLayer, -419430401);
            }
            for (int i = 0; i < ld.getNumberOfLayers(); ++i) {
                Drawable layer = ld.getDrawable(i);
                if (layer == bgLayer || layer == progLayer || layer == null) continue;
                changed |= CcHooks.tintDrawable(layer, 0x4DFFFFFF);
            }
            if (real instanceof AbsSeekBar && (thumb = ((AbsSeekBar)real).getThumb()) != null) {
                changed |= CcHooks.tintDrawable(thumb, -1);
            }
            if (changed) {
                real.invalidate();
            }
            return changed;
        }
        catch (Throwable t) {
            return false;
        }
    }

    private static void installSliderGuard(XposedModule module, View slider, int mode) {
        try {
            if (!SLIDER_GUARDED.add(slider)) {
                return;
            }
            CcHooks.addDrawGuard(module, slider, view -> {
                try {
                    View f;
                    View rv = slider;
                    if (!(rv instanceof SeekBar) && (f = rv.findViewById(2131430146)) != null) {
                        rv = f;
                    }
                    if (rv instanceof ProgressBar) {
                        ProgressBar pb2 = (ProgressBar)rv;
                        if (SLIDER_DRAWABLE_DONE.contains(pb2)) {
                            CcHooks.refreshSliderColors(pb2);
                        } else {
                            CcHooks.applySliderMaterial(module, slider, mode);
                        }
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            });
            L.log(4, TAG, "L19 slider guard installed");
        }
        catch (Throwable t) {
            L.log(5, TAG, "L19 slider guard failed: " + t);
        }
    }

    private static boolean tintDrawable(Drawable d, int color) {
        if (d == null) {
            return false;
        }
        try {
            if (d instanceof GradientDrawable) {
                ((GradientDrawable)d).setColor(color);
                return true;
            }
            d.mutate();
            d.setTint(color);
            d.invalidateSelf();
            return true;
        }
        catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean hookCombineWrite(XposedModule module, ClassLoader cl, boolean enableGlass) {
        try {
            Class<?> combine = Class.forName(CLS_COMBINE, false, cl);
            Class<?> mt = Class.forName(CLS_MATERIAL_TYPE, false, cl);
            Object GLASS = mt.getField("GLASS").get(null);
            Field fMaterialType = Class.forName(CLS_INTERACTOR, false, cl).getDeclaredField(F_MATERIAL_TYPE);
            fMaterialType.setAccessible(true);
            Method target = CcHooks.findInvokeSuspend(combine);
            if (target == null) {
                L.log(5, TAG, "L3: invokeSuspend not found in combine");
                return false;
            }
            target.setAccessible(true);
            module.hook((Executable)target).setId("cc_l3_material_field").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object result = chain.proceed();
                if (!enableGlass) {
                    return result;
                }
                try {
                    Object self = chain.getThisObject();
                    if (self != null) {
                        Field fThis = combine.getDeclaredField("this$0");
                        fThis.setAccessible(true);
                        Object interactor = fThis.get(self);
                        if (interactor != null) {
                            fMaterialType.set(interactor, GLASS);
                        }
                    }
                }
                catch (Throwable t) {
                    L.log(5, TAG, "L3 write field failed: " + t);
                }
                if (CcHooks.isMaterialType(result, mt)) {
                    return GLASS;
                }
                return result;
            });
            L.log(4, TAG, "L3 hooked combine -> materialType field = GLASS");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L3 failed: " + t);
            return false;
        }
    }

    private static boolean hookEnsureOutlineProvider(XposedModule module, ClassLoader cl, boolean enableGlass) {
        try {
            Class<?> inj = Class.forName("com.android.systemui.statusbar.notification.row.NotificationBackgroundViewInjectorImpl", false, cl);
            Method target = inj.getDeclaredMethod("ensureOutlineProvider", new Class[0]);
            target.setAccessible(true);
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, cl);
            Method setGlassOutline = miBlur.getMethod("setGlassOutlineEnable", View.class, Boolean.TYPE);
            Method setBlurOutline = miBlur.getMethod("setBlurOutlineEnable", View.class, Boolean.TYPE);
            Field fView = inj.getField("view");
            fView.setAccessible(true);
            Field fLastType = inj.getField("lastMaterialType");
            fLastType.setAccessible(true);
            AtomicInteger cnt = new AtomicInteger();
            module.hook((Executable)target).setId("cc_l13_outline").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                if (!enableGlass) {
                    return chain.proceed();
                }
                String lg = CcHooks.getProp("debug.hyperclassic.light_glass", "1").trim();
                if ("0".equals(lg)) {
                    return chain.proceed();
                }
                try {
                    CcHooks.applyMaterialStyle(cl, 2);
                    Object self0 = chain.getThisObject();
                    fLastType.set(self0, null);
                }
                catch (Throwable self0) {
                    // empty catch block
                }
                Object result = chain.proceed();
                try {
                    Object self = chain.getThisObject();
                    Object v = fView.get(self);
                    if (v instanceof View) {
                        setBlurOutline.invoke(null, v, false);
                        setGlassOutline.invoke(null, v, true);
                        int n = cnt.incrementAndGet();
                        if (n <= 6) {
                            L.log(4, TAG, "L13 glass outline forced #" + n + " view=" + ((View)v).getClass().getSimpleName() + " w=" + ((View)v).getWidth() + " h=" + ((View)v).getHeight());
                        }
                    }
                }
                catch (Throwable t) {
                    L.log(5, TAG, "L13 outline failed: " + t);
                }
                return result;
            });
            L.log(4, TAG, "L13 hooked ensureOutlineProvider (glass outline)");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L13 failed: " + t);
            return false;
        }
    }

    private static boolean hookGlassEffectApply(XposedModule module, ClassLoader cl, boolean enableGlass) {
        try {
            Class<?> glassEffect = Class.forName(CLS_GLASS_EFFECT, false, cl);
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, cl);
            Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, cl);
            Method setBgBlurRadius = miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class);
            Method setBgBlurMode = miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class);
            Method setPassWindowBlur = miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE);
            Method setGlassBlurRadius = miGlass.getMethod("setMiGlassBlurRadius", View.class, Integer.TYPE, Integer.TYPE);
            Method target = null;
            for (Method m : glassEffect.getDeclaredMethods()) {
                if (!"apply".equals(m.getName()) || m.getParameterCount() != 2) continue;
                target = m;
                break;
            }
            if (target == null) {
                L.log(5, TAG, "L4: apply not found");
                return false;
            }
            target.setAccessible(true);
            Class<?> rowCls = Class.forName(CLS_EXPANDABLE_ROW, false, cl);
            module.hook((Executable)target).setId("cc_l4_blur").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                if (Boolean.TRUE.equals(IN_GLASS_APPLY.get())) {
                    return chain.proceed();
                }
                Object result = chain.proceed();
                if (!enableGlass) {
                    return result;
                }
                int entry = L4_ENTRY.incrementAndGet();
                if (entry <= 3) {
                    try {
                        Object[] a = chain.getArgs().toArray();
                        Context c0 = a.length > 1 && a[1] instanceof Context ? (Context)a[1] : null;
                        int applyUi = c0 == null ? -1 : c0.getResources().getConfiguration().uiMode & 0x30;
                        int rowUi = -1;
                        try {
                            View rv = (View)a[0];
                            rowUi = rv.getContext().getResources().getConfiguration().uiMode & 0x30;
                        }
                        catch (Throwable rv) {
                            // empty catch block
                        }
                        L.log(4, TAG, "L4 entry #" + entry + " arg0=" + (a.length > 0 && a[0] != null ? a[0].getClass().getName() : "null") + " applyUiMode=0x" + Integer.toHexString(applyUi) + " rowUiMode=0x" + Integer.toHexString(rowUi));
                    }
                    catch (Throwable t) {
                        L.log(5, TAG, "L4 entry log failed: " + t);
                    }
                }
                try {
                    Object[] args = chain.getArgs().toArray();
                    Object row = args[0];
                    Context ctx = (Context)args[1];
                    if (!rowCls.isInstance(row) || ctx == null) {
                        return result;
                    }
                    Object injector = CcHooks.invokeNoArg(row, "getInjector");
                    if (injector == null) {
                        return result;
                    }
                    Object bg = CcHooks.invokeNoArg(injector, "getBackgroundNormal");
                    if (!(bg instanceof View)) {
                        return result;
                    }
                    View v = (View)bg;
                    int radius = 0;
                    try {
                        radius = v.getResources().getDimensionPixelSize(2131170502);
                    }
                    catch (Throwable ignored) {
                        radius = 0;
                    }
                    if (radius <= 0) {
                        radius = 60;
                    }
                    setBgBlurRadius.invoke(null, radius, v);
                    setGlassBlurRadius.invoke(null, v, radius, radius);
                    setBgBlurMode.invoke(null, 1, v);
                    setPassWindowBlur.invoke(null, v, true);
                    boolean blurRoute = "blur".equalsIgnoreCase(CcHooks.getProp("debug.hyperclassic.material", "glass").trim());
                    if (!blurRoute) {
                        Object glassParams = CcHooks.readGlassParamsWithOverride(CLS_GLASS_EFFECT, cl, v);
                        if (glassParams != null) {
                            Method setGlass = Class.forName(CLS_MI_GLASS, false, cl).getMethod("setMiGlassCompat", View.class, float[].class);
                            setGlass.invoke(null, v, glassParams);
                            int np = GLASSP_COUNT.incrementAndGet();
                            if (np <= 3 && glassParams instanceof float[]) {
                                float[] fp = (float[])glassParams;
                                StringBuilder sb = new StringBuilder("glassParams:");
                                for (int i = 0; i < 6 && i < fp.length; ++i) {
                                    sb.append(" p").append(i).append('=').append(fp[i]);
                                }
                                L.log(4, TAG, sb.toString());
                            }
                        }
                        Method setMat = Class.forName(CLS_MI_GLASS, false, cl).getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class);
                        setMat.invoke(null, 1, v);
                    } else {
                        String lg = CcHooks.getProp("debug.hyperclassic.light_glass", "1").trim();
                        CcHooks.lightGlassOverlay(module, cl, v, lg);
                    }
                    int n = L4_COUNT.incrementAndGet();
                    if (n <= 3) {
                        L.log(4, TAG, "L4 applied #" + n + " radius=" + radius + " view=" + v.getClass().getSimpleName() + " w=" + v.getWidth() + " h=" + v.getHeight());
                    }
                }
                catch (Throwable t) {
                    L.log(5, TAG, "L4 apply failed: " + t);
                }
                return result;
            });
            L.log(4, TAG, "L4 hooked glass apply -> blur enabled");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L4 failed: " + t);
            return false;
        }
    }

    private static boolean hookOtherGlassEffects(XposedModule module, ClassLoader cl, boolean enableGlass) {
        boolean a = CcHooks.regionOn(REGION_HEADSUP) ? CcHooks.hookHeadsUpGlass(module, cl, enableGlass) : false;
        boolean b = CcHooks.hookFlipRowGlass(module, cl, enableGlass);
        boolean c = CcHooks.hookDismissGlass(module, cl, enableGlass);
        boolean d = CcHooks.hookRowLikeGlass(module, cl, enableGlass, "com.android.systemui.statusbar.notification.style.vieweffect.FocusNotificationGlassEffect", "Focus");
        boolean e = CcHooks.hookRowLikeGlass(module, cl, enableGlass, "com.android.systemui.statusbar.notification.style.vieweffect.FocusNotificationGlassCustomBgEffect", "FocusCustomBg");
        boolean f = CcHooks.hookRowLikeGlass(module, cl, enableGlass, "com.android.systemui.statusbar.notification.style.vieweffect.NotificationRowGlassFullAodEffect", "RowFullAod");
        boolean g = CcHooks.hookRowLikeGlass(module, cl, enableGlass, "com.android.systemui.statusbar.notification.style.vieweffect.NotificationRowGlassOnKeyguardEffect", "RowKeyguard");
        boolean h = CcHooks.hookModalDialogEffects(module, cl, enableGlass);
        boolean i = CcHooks.hookModalAnimProbe(module, cl, enableGlass);
        boolean j = CcHooks.hookRowUpdateBackground(module, cl, enableGlass);
        return a || b || c || d || e || f || g || h || i || j;
    }

    private static boolean hookRowUpdateBackground(XposedModule module, ClassLoader cl, boolean enableGlass) {
        try {
            Class<?> injectorCls = Class.forName("com.android.systemui.statusbar.notification.row.ExpandableNotificationRowInjector", false, cl);
            Method target = null;
            for (Method m : injectorCls.getDeclaredMethods()) {
                if (!"updateBackground$1".equals(m.getName()) || m.getParameterCount() != 0 || m.getReturnType() != Void.TYPE) continue;
                target = m;
                break;
            }
            if (target == null) {
                for (Method m : injectorCls.getDeclaredMethods()) {
                    if (!"updateBackground".equals(m.getName()) || m.getParameterCount() != 0 || m.getReturnType() != Void.TYPE) continue;
                    target = m;
                    break;
                }
            }
            if (target == null) {
                L.log(5, TAG, "L7: updateBackground not found");
                return false;
            }
            target.setAccessible(true);
            Class<?> interactorCls = Class.forName(CLS_INTERACTOR, false, cl);
            Class<?> mt = Class.forName(CLS_MATERIAL_TYPE, false, cl);
            Object GLASS = mt.getField("GLASS").get(null);
            Field fMaterialType = interactorCls.getDeclaredField(F_MATERIAL_TYPE);
            fMaterialType.setAccessible(true);
            module.hook((Executable)target).setId("cc_l7_update_bg").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object result = chain.proceed();
                if (!enableGlass) {
                    return result;
                }
                int n0 = CcHooks.countAndLog("l7_entry");
                if (n0 > 0) {
                    L.log(4, TAG, "L7 entry #" + n0);
                }
                try {
                    Object bg;
                    Object self = chain.getThisObject();
                    if (self == null) {
                        L.log(5, TAG, "L7: self=null");
                        return result;
                    }
                    Object row = null;
                    try {
                        Field fView = self.getClass().getSuperclass().getDeclaredField("view");
                        fView.setAccessible(true);
                        row = fView.get(self);
                    }
                    catch (Throwable t1) {
                        for (Class<?> c = self.getClass(); c != null && row == null; c = c.getSuperclass()) {
                            try {
                                Field f = c.getDeclaredField("view");
                                f.setAccessible(true);
                                row = f.get(self);
                                continue;
                            }
                            catch (Throwable f) {
                                // empty catch block
                            }
                        }
                    }
                    if (row == null) {
                        L.log(5, TAG, "L7: row not resolved");
                        return result;
                    }
                    if (n0 > 0) {
                        L.log(4, TAG, "L7 row resolved: " + row.getClass().getSimpleName());
                    }
                    Object interactor = null;
                    try {
                        interactor = row.getClass().getMethod("getMaterialInteractor", new Class[0]).invoke(row, new Object[0]);
                    }
                    catch (Throwable c) {
                        // empty catch block
                    }
                    boolean isGlass = false;
                    if (interactor != null) {
                        isGlass = GLASS.equals(fMaterialType.get(interactor));
                    } else {
                        Field fStyle = Class.forName(CLS_MI_BLUR, false, cl).getDeclaredField("materialStyle");
                        fStyle.setAccessible(true);
                        isGlass = GLASS.equals(fStyle.get(null));
                    }
                    if (n0 > 0) {
                        L.log(4, TAG, "L7 isGlass=" + isGlass);
                    }
                    if (!isGlass) {
                        return result;
                    }
                    Object injector = CcHooks.invokeNoArg(row, "getInjector");
                    Object object = bg = injector == null ? null : CcHooks.invokeNoArg(injector, "getBackgroundNormal");
                    if (bg instanceof View) {
                        View bgv = (View)bg;
                        int n1 = n0;
                        if (bgv.isAttachedToWindow() && bgv.getWidth() > 0) {
                            CcHooks.applyBlurTo(bgv, CLS_GLASS_EFFECT, module, "AnimBg", cl);
                        } else {
                            Runnable[] task = new Runnable[1];
                            AtomicInteger retry = new AtomicInteger();
                            task[0] = () -> {
                                if (enableGlass && bgv.isAttachedToWindow() && bgv.getWidth() > 0) {
                                    CcHooks.applyBlurTo(bgv, CLS_GLASS_EFFECT, module, "AnimBg", cl);
                                } else if (retry.incrementAndGet() <= 5) {
                                    bgv.postDelayed(task[0], 32L);
                                } else if (n1 > 0) {
                                    L.log(5, TAG, "L7 bg never attached, giving up");
                                }
                            };
                            bgv.postDelayed(task[0], 32L);
                        }
                    } else if (n0 > 0) {
                        L.log(5, TAG, "L7 bg not a View: " + (bg == null ? "null" : bg.getClass().getName()));
                    }
                }
                catch (Throwable t) {
                    L.log(5, TAG, "L7 apply failed: " + t);
                }
                return result;
            });
            L.log(4, TAG, "L7 hooked updateBackground (anim fix)");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L7 failed: " + t);
            return false;
        }
    }

    private static boolean hookModalAnimProbe(XposedModule module, ClassLoader cl, boolean enableGlass) {
        try {
            Class<?> cls = Class.forName("com.android.systemui.statusbar.notification.modal.ModalControllerImpl", false, cl);
            boolean ok = false;
            block2: for (String name : new String[]{"tryAnimEnterModal", "animExitModal"}) {
                for (Method m : cls.getDeclaredMethods()) {
                    if (!name.equals(m.getName()) || m.isBridge() || m.isSynthetic()) continue;
                    String mName = name;
                    m.setAccessible(true);
                    module.hook((Executable)m).setId("cc_probe_" + mName).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                        int n = CcHooks.countAndLog("probe_" + mName);
                        if (n > 0) {
                            L.log(4, TAG, "probe " + mName + " #" + n);
                        }
                        return chain.proceed();
                    });
                    ok = true;
                    continue block2;
                }
            }
            if (ok) {
                L.log(4, TAG, "probe hooked ModalControllerImpl anim methods");
            }
            return ok;
        }
        catch (Throwable t) {
            L.log(5, TAG, "probe hook failed: " + t);
            return false;
        }
    }

    private static boolean hookModalDialogEffects(XposedModule module, ClassLoader cl, boolean enableGlass) {
        String clsName = "com.android.systemui.statusbar.notification.style.vieweffect.ModalDialogGlassEffect";
        try {
            Method fm;
            Class<?> cls = Class.forName("com.android.systemui.statusbar.notification.style.vieweffect.ModalDialogGlassEffect", false, cl);
            boolean ok = false;
            for (Method m : cls.getDeclaredMethods()) {
                if (!"apply".equals(m.getName()) || m.getParameterCount() != 2 || m.isBridge()) continue;
                fm = m;
                fm.setAccessible(true);
                module.hook((Executable)fm).setId("cc_l6_modal_apply").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object result = chain.proceed();
                    if (!enableGlass) {
                        return result;
                    }
                    try {
                        Object v = chain.getArgs().toArray()[0];
                        if (v instanceof View) {
                            CcHooks.applyBlurTo((View)v, "com.android.systemui.statusbar.notification.style.vieweffect.ModalDialogGlassEffect", module, "ModalApply", cl);
                        }
                    }
                    catch (Throwable t) {
                        L.log(5, TAG, "L6 modal apply failed: " + t);
                    }
                    return result;
                });
                L.log(4, TAG, "L6 hooked ModalDialogGlassEffect.apply");
                ok = true;
                break;
            }
            for (Method m : cls.getDeclaredMethods()) {
                if (!"clear".equals(m.getName()) || m.getParameterCount() != 1 || m.isBridge()) continue;
                fm = m;
                fm.setAccessible(true);
                module.hook((Executable)fm).setId("cc_l6_modal_clear").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object result = chain.proceed();
                    if (!enableGlass) {
                        return result;
                    }
                    try {
                        Object v = chain.getArgs().toArray()[0];
                        if (v instanceof View) {
                            CcHooks.applyBlurTo((View)v, "com.android.systemui.statusbar.notification.style.vieweffect.ModalDialogGlassEffect", module, "ModalClear", cl);
                        }
                    }
                    catch (Throwable t) {
                        L.log(5, TAG, "L6 modal clear failed: " + t);
                    }
                    return result;
                });
                L.log(4, TAG, "L6 hooked ModalDialogGlassEffect.clear");
                ok = true;
                break;
            }
            return ok;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L6 hook failed: " + t);
            return false;
        }
    }

    /**
     * ★ v85：媒体卡片玻璃材质（对齐官方 MediaViewBlurEffect + GlassEffect 完整链路）。
     *   【主人反馈】「背景正常了，但仍然是磨砂玻璃」
     *   【根因】v81 只调了 setMiViewMaterialTypeCompat(1) + setMiGlassCompat，
     *     缺了官方的两步前置：
     *       ① mediaBg.setBackground(ctx.getDrawable(0x7f08184c))
     *          = notification_heads_up_transparent_bg（**透明**）
     *       ② NotificationUtil.applyElementViewBlend(ctx, mediaBg, false, blend, false)
     *          → 内部 setRoundRect + setMiViewBlurModeCompat(1) + setMiBackgroundBlendColors
     *     没有透明背景打底，玻璃直接渲染在专辑封面图上 → 观感就是磨砂。
     *   【官方完整链路（smali 实证）】
     *     MediaViewBlurEffect.apply():
     *       setBackground(0x7f08184c 透明)
     *       setMiViewMaterialTypeCompat(0, mediaBg)
     *       applyElementViewBlend(...)  // blend 色来自 0x7f06057a/b/c + 0x7f0c00ea/e9/eb
     *     MediaViewGlassFullAodEffect.apply() 接着:
     *       setMiViewMaterialTypeCompat(1, mediaBg)
     *       setMiGlassCompat(mediaBg, glassParamsArray)   // 0x7f0300cd
     *   本函数完整复刻这条链，但**不碰 clipToOutline / outlineProvider / alpha / transform**
     *   （这些是过渡动画的几何属性，改了会丢动画）。
     *
     * @param setTransparentBg true=重设透明背景（官方行为）；
     *                         false=保留当前背景（自愈场景，避免反复重设导致闪）
     */
    private static void applyMediaGlassMaterialOnly81(XposedModule module, View v, ClassLoader cl) {
        CcHooks.applyMediaGlassMaterialFull85(module, v, cl, true);
    }

    private static void applyMediaGlassMaterialFull85(XposedModule module, View v, ClassLoader cl, boolean setTransparentBg) {
        try {
            Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, cl);
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, cl);
            Context ctx = v.getContext();

            // ---------- ① 透明背景打底（官方 MediaViewBlurEffect 第一步）----------
            //   没有这层透明背景，玻璃会直接渲染在专辑封面图上 → 磨砂观感。
            if (setTransparentBg) {
                try {
                    Drawable tb = ctx.getDrawable(0x7f08184c);   // notification_heads_up_transparent_bg
                    if (tb != null) v.setBackground(tb.mutate());
                } catch (Throwable ignored) {}
            }
            // ★ v93：撤销 v92 的「清 outline」——主人实测清掉后玻璃圆角消失。
            //   说明 outline provider 是圆角的必要来源（setClipToOutline 依赖它）。
            //   同时撤销 v92 的「绕过 applyElementViewBlend」——它内部的 setRoundRect
            //   正是圆角的提供者。恢复调用以保住圆角。

            // ---------- ② materialType=0 + blend（官方第二步）----------
            try {
                miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 0, v);
            } catch (Throwable ignored) {}
            // ★ v94：**不调 applyElementViewBlend**，改用官方 stackingOutlineProvider。
            //   【完整根因（smali 实证）】
            //     applyElementViewBlend → NotificationUtil.setRoundRect(v,false,false)
            //       → v.setOutlineProvider(new NotificationUtil$1(isTiny))   // 覆盖！
            //   而官方动画结束后会走：
            //     NotificationStackingInteractor → ExpandableView.setExtClipBottomAmount(0)
            //       → if (getOutlineProvider() == stackingOutlineProvider) { setOutlineProvider(null); }
            //     因为 provider 已被我们换成 NotificationUtil$1，**这个 != 判断不成立**，
            //     所以官方的"清空 outline"被跳过 → outline 保持非空
            //     → MiuiMediaHeaderView.setActualHeight 逐帧 invalidateOutline()
            //     → 动画结束后立刻按新尺寸重裁一次（主人描述的"动画完成后被裁剪"）。
            //   【修法】完全不碰 header 的 outline：只设 blend 颜色 + blurMode，
            //     圆角交给官方的 stackingOutlineProvider（它在 setActualHeight 里
            //     用 getActualHeight() 实时计算，天然跟随动画）。
            try {
                Resources r = v.getResources();
                int c1 = r.getColor(0x7f06057a, null);
                int c2 = r.getColor(0x7f06057b, null);
                int c3 = r.getColor(0x7f06057c, null);
                int m1 = r.getInteger(0x7f0c00ea);
                int m2 = r.getInteger(0x7f0c00e9);
                int m3 = r.getInteger(0x7f0c00eb);
                int[] blend = new int[]{c1, m1, c2, m2, c3, m3};
                miBlur.getMethod("clearMiBackgroundBlendColorCompat", View.class).invoke(null, v);
                miBlur.getMethod("setMiBackgroundBlendColors$default", View.class, int[].class)
                        .invoke(null, v, blend);
                miBlur.getMethod("setMiViewBlurModeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
                L.log(4, TAG, "L94 blend set (no outline override)");
            } catch (Throwable t85a) {
                L.log(5, TAG, "L94 blend failed: " + t85a);
            }

            // ---------- ③ 玻璃材质（官方 MediaViewGlassFullAodEffect 后半段）----------
            float[] garr = CcHooks.loadGlassParamsFromRes(v);
            if (garr == null || garr.length < 20) garr = GLASS_PARAMS_FALLBACK;
            miGlass.getMethod("setMiViewMaterialTypeCompat", Integer.TYPE, View.class).invoke(null, 1, v);
            miGlass.getMethod("setMiGlassCompat", View.class, float[].class).invoke(null, v, garr);

            // ---------- ④ 反射玻璃要求：背景模糊全关 ----------
            //   setMiViewBlurModeCompat(1) 由 applyElementViewBlend 设（官方行为），保留。
            try {
                miBlur.getMethod("setMiBackgroundBlurModeCompat", Integer.TYPE, View.class).invoke(null, 0, v);
            } catch (Throwable ignored) {}
            try {
                miBlur.getMethod("setMiBackgroundBlurRadiusCompat", Integer.TYPE, View.class).invoke(null, 0, v);
            } catch (Throwable ignored) {}
            try {
                miBlur.getMethod("setPassWindowBlurEnabledCompat", View.class, Boolean.TYPE).invoke(null, v, false);
            } catch (Throwable ignored) {}

            v.invalidate();
        } catch (Throwable t) {
            L.log(5, TAG, "L85 materialOnly failed: " + t);
        }
    }

    /**
     * ★ v88：判断 View 是不是媒体卡片的 mediaBg（MiuiMediaViewHolder.mediaBg）。
     *   用于把媒体卡片和普通通知/磁贴区分开——
     *   媒体卡片只能走「纯材质」路径（不碰 clip/outline），
     *   否则会破坏 AOD↔锁屏的高度插值过渡。
     */
    private static boolean isMediaBgView88(View v) {
        try {
            if (v == null) return false;
            // ① 自身的 id 是 media_bg（0x7f0b0709）
            int id = v.getId();
            if (id == 0x7f0b0709) return true;
            // ② 父链里出现 MiuiMediaHeaderView（mediaBg 的直接宿主）
            android.view.ViewParent p = v.getParent();
            int d = 0;
            while (p != null && d++ < 6) {
                String cn = p.getClass().getName();
                if (cn.contains("MiuiMediaHeaderView") || cn.contains("MiuiMediaViewHolder")) return true;
                p = p.getParent();
            }
        } catch (Throwable ignored) {}
        return false;
    }

    /**
     * ★ v83：判断媒体视图当前是否处于 AOD / 锁屏态。
     *   【为什么】官方在 AOD↔锁屏切换时用 NotifiFullAodController 驱动高度动画，
     *     并在 finishForMediaNotificationAnim() 里显式 setOutlineProvider(null)，
     *     让 ExpandableView 的高度插值不被裁掉。此时任何 setBackground/
     *     setClipToOutline/setOutlineProvider 都会破坏动画。
     *   【判断依据】
     *     ① 反射读 NotifiFullAodController.mEnableFullAod（AOD 开启态）
     *     ② 反射读 NotifiFullAodController.mIsAnimating（过渡动画进行中）
     *     ③ 视图自身在 keyguard 容器里（父链类名含 keyguard/Keyguard）
     *   任一命中即视为「不要碰几何属性」。
     */
    private static boolean isMediaInAodOrKeyguard(View v) {
        try {
            if (v == null) return false;
            // ★ v84 修正：不能读 static mEnableFullAod（全局开关，AOD 开启时恒 true，
            //   会把通知面板里的媒体卡片也误判成 AOD 态 → 失去背景）。
            //   改为只判断「真正在过渡动画中」的强信号：
            //     NotifiFullAodController.mIsAnimating（static，仅在 Folme 过渡期间为 true）
            try {
                Class<?> ctl = Class.forName(
                        "com.android.systemui.statusbar.notification.fullaod.NotifiFullAodController",
                        false, v.getClass().getClassLoader());
                Field fm = ctl.getDeclaredField("mIsAnimating");
                fm.setAccessible(true);
                Object o = fm.get(null);
                if (o instanceof Boolean && (Boolean) o) return true;
            } catch (Throwable ignored) {}
        } catch (Throwable ignored) {}
        return false;
    }

    /**
     * ★ v82：等动画空闲后再套玻璃材质。
     *   【背景】AOD↔锁屏的媒体卡片过渡由 miuix Folme 驱动：
     *     NotifiFullAodController$FullAodTransitionListener.onUpdate(fraction)
     *       → 遍历 INotifiFullAodListener.updateFullAodAnimState(...)
     *       → 各 view 的 transitionAlpha / 尺寸 / 玻璃参数逐帧插值
     *   期间任何 setBackground / setClipToOutline / setOutlineProvider 都会让
     *   Folme 的插值目标失效 → 表现为「过渡动画丢失」（收起正常、展开丢失）。
     *   所以这里轮询等待「transitionAlpha 稳定 + 无 pending 布局」后再动手。
     */
    private static void applyMediaGlassWhenIdle82(XposedModule module, View v, ClassLoader cl, String tag, int attempt) {
        try {
            if (v == null || !v.isAttachedToWindow()) return;
            boolean idle = !CcHooks.isViewAnimating(v);
            // ★ v85：等待上限从 12 提到 20（≈1.6s），覆盖 finishForMediaNotificationAnim
            //   的 500ms guard + 高度插值尾段，避免"等不到就硬套"导致二次裁剪。
            if (!idle && attempt < 20) {
                final View fv = v;
                final ClassLoader fcl = cl;
                final String ftag = tag;
                final int na = attempt + 1;
                v.postDelayed(() -> CcHooks.applyMediaGlassWhenIdle82(module, fv, fcl, ftag, na), 80L);
                if (attempt == 0) L.log(4, TAG, tag + " wait idle (attempt " + na + ")");
                return;
            }
            if (v.getWidth() <= 0 || v.getHeight() <= 0) {
                if (attempt < 12) {
                    final View fv = v;
                    final ClassLoader fcl = cl;
                    final String ftag = tag;
                    final int na = attempt + 1;
                    v.postDelayed(() -> CcHooks.applyMediaGlassWhenIdle82(module, fv, fcl, ftag, na), 80L);
                }
                return;
            }
            CcHooks.applyMediaGlassMaterialOnly81(module, v, cl);
            MEDIA_GLASS_DONE.add(v);
            L.log(4, TAG, tag + " reflective applied after idle(attempt=" + attempt + ") " + v.getWidth() + "x" + v.getHeight());
        } catch (Throwable t) {
            L.log(5, TAG, "applyMediaGlassWhenIdle82 failed: " + t);
        }
    }

    private static boolean hookRowLikeGlass(XposedModule module, ClassLoader cl, boolean enableGlass, String clsName, String tagName) {
        try {
            Class<?> cls = Class.forName(clsName, false, cl);
            Class<?> rowCls = Class.forName(CLS_EXPANDABLE_ROW, false, cl);
            Method target = CcHooks.findApply2(cls);
            if (target == null) {
                return false;
            }
            target.setAccessible(true);
            module.hook((Executable)target).setId("cc_l5_" + tagName).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object result = chain.proceed();
                if (!enableGlass) {
                    return result;
                }
                try {
                    Object row = chain.getArgs().toArray()[0];
                    if (rowCls.isInstance(row)) {
                        Object bg;
                        Object injector = CcHooks.invokeNoArg(row, "getInjector");
                        Object object = bg = injector == null ? null : CcHooks.invokeNoArg(injector, "getBackgroundNormal");
                        if (bg instanceof View) {
                            CcHooks.applyBlurTo((View)bg, clsName, module, tagName, cl);
                        }
                    }
                }
                catch (Throwable t) {
                    L.log(5, TAG, "L5 " + tagName + " failed: " + t);
                }
                return result;
            });
            L.log(4, TAG, "L5 hooked " + tagName);
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L5 " + tagName + " hook failed: " + t);
            return false;
        }
    }

    private static boolean hookHeadsUpGlass(XposedModule module, ClassLoader cl, boolean enableGlass) {
        String[] candidates = new String[]{"com.android.systemui.statusbar.notification.style.vieweffect.HeadsUpNotificationGlassDarkEffect", "com.android.systemui.statusbar.notification.style.vieweffect.HeadsUpNotificationGlassEffect"};
        boolean any = false;
        for (String clsName : candidates) {
            any |= CcHooks.hookHeadsUpOne(module, cl, enableGlass, clsName);
        }
        return any;
    }

    private static boolean hookHeadsUpOne(XposedModule module, ClassLoader cl, boolean enableGlass, String clsName) {
        try {
            Class<?> cls = Class.forName(clsName, false, cl);
            Class<?> rowCls = Class.forName(CLS_EXPANDABLE_ROW, false, cl);
            Method target = CcHooks.findApply2(cls);
            if (target == null) {
                return false;
            }
            target.setAccessible(true);
            String shortName = clsName.substring(clsName.lastIndexOf(46) + 1);
            module.hook((Executable)target).setId("cc_l5_headsup_" + shortName).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object result = chain.proceed();
                if (!enableGlass) {
                    return result;
                }
                try {
                    Object row = chain.getArgs().toArray()[0];
                    if (rowCls.isInstance(row)) {
                        Object bg;
                        Object injector = CcHooks.invokeNoArg(row, "getInjector");
                        Object object = bg = injector == null ? null : CcHooks.invokeNoArg(injector, "getBackgroundNormal");
                        if (bg instanceof View) {
                            CcHooks.applyBlurTo((View)bg, clsName, module, "HeadsUp", cl);
                        }
                    }
                }
                catch (Throwable t) {
                    L.log(5, TAG, "L5 headsup failed: " + t);
                }
                return result;
            });
            L.log(4, TAG, "L5 hooked " + shortName);
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L5 headsup hook failed [" + clsName + "]: " + t);
            return false;
        }
    }

    private static boolean hookFlipRowGlass(XposedModule module, ClassLoader cl, boolean enableGlass) {
        String clsName = "com.android.systemui.statusbar.notification.style.vieweffect.FlipRowGlassEffect";
        try {
            Class<?> cls = Class.forName("com.android.systemui.statusbar.notification.style.vieweffect.FlipRowGlassEffect", false, cl);
            Method target = null;
            for (Method m : cls.getDeclaredMethods()) {
                if (!"apply".equals(m.getName()) || m.getParameterCount() != 2 || m.getParameterTypes()[1] != View.class) continue;
                target = m;
                break;
            }
            if (target == null) {
                return false;
            }
            target.setAccessible(true);
            module.hook((Executable)target).setId("cc_l5_fliprow").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object result = chain.proceed();
                if (!enableGlass) {
                    return result;
                }
                try {
                    Object v = chain.getArgs().toArray()[1];
                    if (v instanceof View) {
                        CcHooks.applyBlurTo((View)v, "com.android.systemui.statusbar.notification.style.vieweffect.FlipRowGlassEffect", module, "FlipRow", cl);
                    }
                }
                catch (Throwable t) {
                    L.log(5, TAG, "L5 fliprow failed: " + t);
                }
                return result;
            });
            L.log(4, TAG, "L5 hooked FlipRowGlassEffect");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L5 fliprow hook failed: " + t);
            return false;
        }
    }

    private static boolean hookDismissGlass(XposedModule module, ClassLoader cl, boolean enableGlass) {
        String clsName = "com.android.systemui.statusbar.notification.style.vieweffect.NotificationDismissViewGlassEffect";
        try {
            Class<?> cls = Class.forName("com.android.systemui.statusbar.notification.style.vieweffect.NotificationDismissViewGlassEffect", false, cl);
            Method target = CcHooks.findApply2(cls);
            if (target == null) {
                return false;
            }
            target.setAccessible(true);
            module.hook((Executable)target).setId("cc_l5_dismiss").setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object result = chain.proceed();
                if (!enableGlass) {
                    return result;
                }
                try {
                    Object v = chain.getArgs().toArray()[0];
                    if (v instanceof View) {
                        CcHooks.applyDismissGlass(module, (View)v, cl);
                    }
                }
                catch (Throwable t) {
                    L.log(5, TAG, "L5 dismiss failed: " + t);
                }
                return result;
            });
            L.log(4, TAG, "L5 hooked NotificationDismissViewGlassEffect");
            return true;
        }
        catch (Throwable t) {
            L.log(5, TAG, "L5 dismiss hook failed: " + t);
            return false;
        }
    }

    private static void applyBlurTo(View v, String glassClsName, XposedModule module, String tagName, ClassLoader cl) {
        try {
            String mat = CcHooks.getProp("debug.hyperclassic.material", "glass").trim();
            boolean blurMode = "blur".equalsIgnoreCase(mat);
            if (blurMode) {
                String lg = CcHooks.getProp("debug.hyperclassic.light_glass", "1").trim();
                CcHooks.lightGlassOverlay(module, cl, v, lg);
            } else {
                CcHooks.applyGlassMaterialOnly(module, v, cl, tagName);
            }
            int n = CcHooks.countAndLog(tagName);
            if (n > 0) {
                L.log(4, TAG, "official glass[" + tagName + "] #" + n + " view=" + v.getClass().getSimpleName() + " w=" + v.getWidth() + " h=" + v.getHeight() + " mat=" + mat + " attached=" + v.isAttachedToWindow());
            }
        }
        catch (Throwable t) {
            L.log(5, TAG, "applyBlurTo[" + tagName + "] failed: " + t);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static int countAndLog(String tag) {
        HashMap<String, Integer> hashMap = LOG_COUNTS;
        synchronized (hashMap) {
            Integer c = LOG_COUNTS.get(tag);
            int n = (c == null ? 0 : c) + 1;
            LOG_COUNTS.put(tag, n);
            return n <= 10 ? n : 0;
        }
    }

    private static Method findApply2(Class<?> cls) {
        for (Method m : cls.getDeclaredMethods()) {
            if (!"apply".equals(m.getName()) || m.getParameterCount() != 2 || m.isBridge()) continue;
            return m;
        }
        return null;
    }

    private static Object readGlassParams(String className, ClassLoader cl) {
        try {
            Class<?> c = Class.forName(className, false, cl);
            Field f = c.getDeclaredField("glassParamsArray");
            f.setAccessible(true);
            return f.get(null);
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private static Object readGlassParamsWithOverride(String className, ClassLoader cl, View v) {
        Object base = CcHooks.readGlassParams(className, cl);
        if (!(base instanceof float[])) {
            return base;
        }
        float[] src = (float[])base;
        float[] out = (float[])src.clone();
        try {
            String preset;
            Class<?> sp = Class.forName("android.os.SystemProperties");
            Method get = sp.getMethod("get", String.class, String.class);
            Object presetObj = get.invoke(null, "debug.hyperclassic.glass_preset", "0");
            String string = preset = presetObj == null ? "0" : presetObj.toString();
            if (!"0".equals(preset)) {
                int resId = "1".equals(preset) ? 2130903247 : 2130903245;
                try {
                    String[] arr = v.getResources().getStringArray(resId);
                    if (arr != null && arr.length == out.length) {
                        for (int i = 0; i < arr.length; ++i) {
                            out[i] = Float.parseFloat(arr[i]);
                        }
                    }
                }
                catch (Throwable arr) {
                    // empty catch block
                }
            }
            for (int i = 0; i < 6 && i < out.length; ++i) {
                String s;
                Object o = get.invoke(null, "debug.hyperclassic.g_" + i, "-1");
                String string2 = s = o == null ? "-1" : o.toString();
                if ("-1".equals(s)) continue;
                try {
                    out[i] = Float.parseFloat(s);
                    continue;
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return out;
    }

    private static Object invokeNoArg(Object target, String name) {
        try {
            Method m = target.getClass().getMethod(name, new Class[0]);
            m.setAccessible(true);
            return m.invoke(target, new Object[0]);
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private static Method findNoArgBoolean(Class<?> cls, String name) {
        for (Method m : cls.getDeclaredMethods()) {
            if (!name.equals(m.getName()) || m.getParameterCount() != 0 || m.getReturnType() != Boolean.TYPE) continue;
            return m;
        }
        return null;
    }

    private static Method findInvokeSuspend(Class<?> cls) {
        for (Method m : cls.getDeclaredMethods()) {
            if (!M_INVOKE_SUSPEND.equals(m.getName()) || m.getParameterCount() != 1) continue;
            return m;
        }
        return null;
    }

    private static boolean isMaterialType(Object o, Class<?> mt) {
        return o != null && o.getClass() == mt;
    }

    private static int applyMaterialStyle(ClassLoader cl, int ordinal) {
        Object target = null;
        try {
            Class<?> mtCls = Class.forName(CLS_MATERIAL_TYPE, false, cl);
            Object[] consts = mtCls.getEnumConstants();
            if (consts == null || ordinal < 0 || ordinal >= consts.length) {
                return -1;
            }
            target = consts[ordinal];
        }
        catch (Throwable t) {
            return -1;
        }
        try {
            Class<?> blurCls = Class.forName(CLS_MI_BLUR, false, cl);
            Field f = blurCls.getDeclaredField("materialStyle");
            f.setAccessible(true);
            f.set(null, target);
            return ordinal;
        }
        catch (Throwable blurCls) {
            try {
                Class<?> blurCls2 = Class.forName(CLS_MI_BLUR, false, cl);
                for (Field f : blurCls2.getDeclaredFields()) {
                    if (!f.getType().getName().equals(CLS_MATERIAL_TYPE) || !Modifier.isStatic(f.getModifiers())) continue;
                    f.setAccessible(true);
                    f.set(null, target);
                    return ordinal;
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            return -1;
        }
    }

    private static String directGlassProbe(View v, Object glassParams) {
        Throwable c;
        Method m;
        StringBuilder sb = new StringBuilder();
        try {
            m = View.class.getMethod("setMiViewMaterialType", Integer.TYPE);
            m.setAccessible(true);
            m.invoke(v, 1);
            sb.append("matType(1)=OK ");
        }
        catch (Throwable t) {
            c = t.getCause() != null ? t.getCause() : t;
            sb.append("matType=FAIL(").append(c.getClass().getSimpleName()).append(":").append(c.getMessage()).append(") ");
        }
        if (glassParams instanceof float[]) {
            try {
                m = View.class.getMethod("setMiGlass", float[].class);
                m.setAccessible(true);
                m.invoke(v, glassParams);
                sb.append("glass=OK ");
            }
            catch (Throwable t) {
                c = t.getCause() != null ? t.getCause() : t;
                sb.append("glass=FAIL(").append(c.getClass().getSimpleName()).append(":").append(c.getMessage()).append(") ");
            }
        }
        try {
            m = View.class.getMethod("setMiGlassSdfMaxSize", Float.TYPE, Float.TYPE);
            m.setAccessible(true);
            m.invoke(v, Float.valueOf(Math.max(1, v.getWidth())), Float.valueOf(Math.max(1, v.getHeight())));
            sb.append("sdf=OK ");
        }
        catch (Throwable t) {
            c = t.getCause() != null ? t.getCause() : t;
            sb.append("sdf=FAIL(").append(c.getClass().getSimpleName()).append(":").append(c.getMessage()).append(") ");
        }
        sb.append("size=").append(v.getWidth()).append("x").append(v.getHeight());
        sb.append(" globalStyle=").append(CcHooks.readMaterialStyleOrdinalSafe());
        return sb.toString();
    }

    private static int readMaterialStyleOrdinalSafe() {
        try {
            Class<?> blurCls = Class.forName(CLS_MI_BLUR);
            Field f = blurCls.getField("materialStyle");
            f.setAccessible(true);
            Object o = f.get(null);
            if (o instanceof Enum) {
                return ((Enum)o).ordinal();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return -1;
    }

    private static String dumpFlags(ClassLoader cl) {
        StringBuilder sb = new StringBuilder();
        try {
            Class<?> blurCls = Class.forName(CLS_MI_BLUR, false, cl);
            String[] names = new String[]{"GLASS_MATERIAL_SUPPORTED", "BACKGROUND_BLUR_SUPPORTED"};
            for (String n : names) {
                try {
                    Field f = blurCls.getDeclaredField(n);
                    f.setAccessible(true);
                    sb.append(n).append('=').append(f.get(null)).append(' ');
                }
                catch (Throwable t) {
                    sb.append(n).append("=ERR ");
                }
            }
            for (Field f : blurCls.getDeclaredFields()) {
                if (!Modifier.isStatic(f.getModifiers()) || f.getType() != Boolean.TYPE || "GLASS_MATERIAL_SUPPORTED".equals(f.getName()) || "BACKGROUND_BLUR_SUPPORTED".equals(f.getName())) continue;
                try {
                    f.setAccessible(true);
                    sb.append(f.getName()).append('=').append(f.get(null)).append(' ');
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
        }
        catch (Throwable t) {
            sb.append("ERR ").append(t);
        }
        return sb.toString();
    }

    private static void lightGlassOverlay(XposedModule module, ClassLoader cl, View v, String mode) {
        try {
            Class<?> miGlass = Class.forName(CLS_MI_GLASS, false, cl);
            Class<?> miBlur = Class.forName(CLS_MI_BLUR, false, cl);
            int ord = CcHooks.applyMaterialStyle(cl, 2);
            if (ord < 0 && DUMPS_COUNT.incrementAndGet() <= 2) {
                L.log(5, TAG, "applyMaterialStyle failed. flags: " + CcHooks.dumpFlags(cl));
            }
            Object glassParams = CcHooks.readGlassParamsWithOverride(CLS_GLASS_EFFECT, cl, v);
            int n = BLURALPHA_COUNT.incrementAndGet();
            StringBuilder log = new StringBuilder("lightGlass mode=").append(mode).append(" #").append(n).append(" w=").append(v.getWidth()).append(" h=").append(v.getHeight());
            int dp = VIEWPROBE_COUNT.incrementAndGet();
            if (dp <= 2) {
                L.log(4, TAG, "ViewProbe: " + ViewGlassProbe.probe());
                L.log(4, TAG, "Flags: " + CcHooks.dumpFlags(cl));
            }
            if ("1".equals(mode) || "2".equals(mode) || "3".equals(mode) || "4".equals(mode)) {
                if (glassParams instanceof float[]) {
                    miGlass.getMethod("setMiGlassCompat", View.class, float[].class).invoke(null, v, glassParams);
                }
                log.append(" glassParams");
            }
            if ("2".equals(mode) || "4".equals(mode)) {
                int alpha = 170;
                try {
                    alpha = Integer.parseInt(CcHooks.getProp("debug.hyperclassic.blur_alpha", "170").trim());
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                alpha = Math.max(0, Math.min(255, alpha));
                Method setAlpha = v.getClass().getMethod("setDrawableAlpha", Integer.TYPE);
                setAlpha.setAccessible(true);
                setAlpha.invoke(v, alpha);
                log.append(" alpha=").append(alpha);
            }
            if ("3".equals(mode) && v.getWidth() > 0 && v.getHeight() > 0) {
                miGlass.getMethod("setMiGlassSdfMaxSizeCompat", View.class, Float.TYPE, Float.TYPE).invoke(null, v, Float.valueOf(v.getWidth()), Float.valueOf(v.getHeight()));
                log.append(" sdf=").append(v.getWidth()).append("x").append(v.getHeight());
            }
            if ("4".equals(mode)) {
                try {
                    Resources r = v.getResources();
                    int c1 = r.getColor(2131102661);
                    int i1 = r.getInteger(2131493148);
                    int c2 = r.getColor(2131102662);
                    int i2 = r.getInteger(2131493149);
                    miBlur.getMethod("setMiBackgroundBlendColors", View.class, int[].class, Float.TYPE).invoke(null, v, new int[]{c1, i1, c2, i2}, Float.valueOf(1.0f));
                    log.append(" blend");
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            if (n <= 6) {
                log.append(" globalStyle=").append(CcHooks.readMaterialStyleOrdinalSafe());
                L.log(4, TAG, log.toString());
            }
        }
        catch (Throwable t) {
            L.log(5, TAG, "lightGlassOverlay failed: " + t);
        }
    }

    static {
        GLASS_RETRY = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
        GLASS_PARAMS_DISMISS = new float[]{0.42f, 0.22f, 0.24f, 0.0f, 0.45f, 1.4f, 0.28f, 0.4f, 0.6f, 1.0f, 0.03f, 1.0f, 1.0f, 1.0f, 0.1f, 0.2f, 0.3f, 1.0f, 1.0f, 62.0f, 4.0f, 80.0f, 1200.0f, 1.2f, 1.0f, -0.4f, 0.6f, -0.8f, 1.4f, 0.7f, 0.8f, 1.15f, 4.0f, 2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        BLURONLY_COUNT = new AtomicInteger();
        FAKEBG_COUNT = new AtomicInteger();
        BG_MBACKGROUND = Collections.synchronizedMap(new HashMap());
        FORCE_NULL_COUNT = new AtomicInteger();
        PAGEDOT_PROBE = new AtomicInteger();
        PAGEDOT_GUARDED = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
        INDICATOR_GUARDED = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
        SLIDER_DRAWABLE_DONE = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
        SLIDER_TREE_DUMP = new AtomicInteger();
        SLIDER_GLASS_DONE = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
        TEXT_GUARDED = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
        ICON_GUARDED = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
        L39_COUNT = new AtomicInteger();
        L39_SEEN = Collections.synchronizedSet(new LinkedHashSet());
        SEEN_HITS = new ConcurrentHashMap<String, Integer>();
        CAPTURE_CHECK_AT = 0L;
        CAPTURE_ON = false;
        CAPTURE_EXPIRED_LOGGED = false;
        TRAY_APPLIED = Collections.synchronizedMap(new WeakHashMap());
        TRAY_GUARDED = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
        SLIDER_GUARDED = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
        LOG_COUNTS = new HashMap();
    }

    // ==================================================================
    // Split shade 层的公共工具（v280）
    // ==================================================================

    /**
     * split 修复层共用的「当前是否真实横屏」判定。
     *
     * 为什么不用 Resources.getConfiguration()：
     *   {@link SplitForceFlag} hook 的是 Resources.getBoolean(int)，与此处的
     *   getConfiguration() 是不同方法，不存在递归。但为了更稳，这里走
     *   ActivityThread 的 systemContext，不受调用方 Resources 缓存影响。
     *
     * 只读，绝不写任何 SystemUI 状态；任何异常一律返回 false（fail-closed），
     * 让所有 split 修复层在不确定时保持关闭。
     */
    static boolean diagnosticLandscapeNow() {
        return isLandscapeNow();
    }

    private static boolean isLandscapeNow() {
        try {
            Object at;
            Class<?> atCls = Class.forName("android.app.ActivityThread");
            at = atCls.getMethod("currentActivityThread").invoke(null);
            if (at == null) return false;
            Object sysCtx = atCls.getMethod("getSystemContext").invoke(at);
            if (sysCtx == null) return false;
            Object res = sysCtx.getClass().getMethod("getResources").invoke(sysCtx);
            if (res == null) return false;
            Object conf = res.getClass().getMethod("getConfiguration").invoke(res);
            if (conf == null) return false;
            java.lang.reflect.Field fo = conf.getClass().getField("orientation");
            return fo.getInt(conf) == Configuration.ORIENTATION_LANDSCAPE;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
