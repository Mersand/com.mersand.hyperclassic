package io.github.mersand.hyperclassic;

import android.graphics.Insets;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;

/** v290: v214 confirmed header/footer-only bridge + shade_header_container narrowing.
 *
 * ============ 为什么回到 v214 的形态（重要） ============
 *
 * v214（2026-11-11）是主人实测确认过的版本：
 *   - 横屏下拉正常
 *   - 状态栏（header）左对齐到 QS 列
 *   - 清除按钮（footer）移到通知列底部
 *   - 通知堆叠正常
 *
 * v289 退化到「横屏无法下拉」，根因是重新引入了 calculateNotificationsTopPadding hook：
 *   v213 证据：mStackY154.56 = 161 * .96（padding 被改成 161），而 v212 基线是 0。
 *   一旦覆盖 padding，通知堆叠几何被改写 → 整条手势/堆叠链路退化。
 *   所以本版把这个 hook 物理删除，绝不覆盖 NSSL padding / 状态 / scroll / translation。
 *
 * 同时纠正一个此前的错误结论：
 *   OnPreDrawListener 并不是「布局抖动」的元凶。v214 保留了它且工作正常，
 *   因为 apply() 里的 place() 是幂等的 —— 参数没变就直接 return，不调 setLayoutParams()，
 *   因此不会每帧产生 requestLayout。真正的问题是 padding hook。
 *
 * ============ 本版新增（相对 v214） ============
 *
 * SHADE_HEADER (shade_header_container, 0x7f0b0ac8) 收窄：
 *   实测 dumpsys：ShadeHeaderContainer{e685c4f V.E...... 0,0-3200,1440}
 *   它撑满整屏 3200 宽，而 split 下 QS 只占左半屏 → 状态栏内容按 3200 居中。
 *   修法：横屏 split 下把它约束到 QS 半屏宽度（与 qsFrame 对齐）。
 *
 * 绝不 reparent NSSL、不改它的触摸 viewport、不写行 translation/scrollY。
 */
final class SplitLayoutBridge {
    private static final int QS_FRAME=0x7f0b0999, NSSL=0x7f0b0877;
    private static final int QS_HEADER=0x7f0b099d, NOTIF_HEADER=0x7f0b0858, DISMISS=0x7f0b0866;
    /** shade_header_container：v290 新增，用于修「状态栏位置居中」。 */
    private static final int SHADE_HEADER=0x7f0b0ac8;

    private static final Map<View, LayoutSnapshot> ORIGINAL = new WeakHashMap<>();
    /** ★ v358：clipChildren / clipToPadding 的原始值（横屏写入后必须能在竖屏复原）。 */
    private static final Map<View, boolean[]> CLIP_STATE = new WeakHashMap<>();
    private static final AtomicInteger LOGS = new AtomicInteger();
    /** v291：performAnimComplete 强制可见的日志计数（独立计数，避免被 LOGS 上限吞掉）。 */
    private static final AtomicInteger FIX_LOGS = new AtomicInteger();
    /** ★ v351：dismiss 触摸直通的日志限流。 */
    private static final AtomicInteger DISMISS_TAP_LOGS = new AtomicInteger();
    /** ★ v351：当前手势是否由我们从 DOWN 认领（用于 UP/CANCEL 判定）。 */
    private static volatile boolean dismissTapDown;
    private static WeakReference<View> root = new WeakReference<>(null);
    private static WeakReference<View> qsFrame = new WeakReference<>(null);
    private static WeakReference<View> nssl = new WeakReference<>(null);
    private static WeakReference<Object> qsc = new WeakReference<>(null);
    private static WeakReference<View> watchedRoot = new WeakReference<>(null);
    private static Field split, barState, fraction, qs, ccLazy, qsContainerField;
    private static Method lazyGet, useCC;
    private static volatile long lastProp;
    private static volatile boolean enabled;
    private static boolean inFrame;
    /** v295：竖屏已恢复过一次 LP 的标记，避免每帧 restore 造成布局抖动。 */
    private static boolean restoredForPortrait;

    static void install(XposedModule module, ClassLoader cl) {
        try {
            Class<?> q=Class.forName("com.android.systemui.shade.QuickSettingsControllerImpl",false,cl);
            Class<?> f=Class.forName("com.android.systemui.qs.MiuiQSFragment",false,cl);
            Class<?> c=Class.forName("com.android.systemui.qs.MiuiQSContainer",false,cl);
            Class<?> cc=Class.forName("com.android.systemui.controlcenter.policy.ControlCenterControllerImpl",false,cl);
            lazyGet=Class.forName("dagger.Lazy",false,cl).getMethod("get");
            useCC=cc.getDeclaredMethod("isUseControlCenter"); useCC.setAccessible(true);
            split=field(q,"mSplitShadeEnabled");barState=field(q,"mBarState");qs=field(q,"mQs");
            ccLazy=field(q,"mControlCenterController");qsContainerField=field(f,"qsContainer");
            fraction=field(q,"mShadeExpandedFraction");

            // ---- 关键：这里【没有】calculateNotificationsTopPadding hook ----
            // v213 的事故教训：覆盖通知顶部 padding 会让通知堆叠几何错乱，
            // 进而导致横屏下拉失败（mStackY 从 0 变成 161*.96）。
            // 本桥只读取 NSSL 几何作为右栏基准，绝不写它。

            // Read-only owner capture. Does not hook or alter any notification-padding getter.
            Method update=q.getDeclaredMethod("updateExpansion");
            module.hook(update).setId("hc_split_chrome_capture")
                    .setExceptionMode(XposedInterface.ExceptionMode.DEFAULT).intercept(chain -> {
                        Object result=chain.proceed();
                        try {
                            Object owner=chain.getThisObject();
                            if(qsc.get()!=owner)qsc=new WeakReference<>(owner);
                            Object fragment=qs.get(owner);
                            if(fragment!=null)bind((View)qsContainerField.get(fragment));
                        }catch(Throwable t) { error(t); }
                        return result;
                    });

            Method layout=c.getDeclaredMethod("onLayout",boolean.class,int.class,int.class,int.class,int.class);
            module.hook(layout).setId("hc_split_chrome_qs_layout")
                    .setExceptionMode(XposedInterface.ExceptionMode.DEFAULT).intercept(chain -> {
                        Object result=chain.proceed();
                        try { bind((View)chain.getThisObject()); apply(); }
                        catch (Throwable t) { error(t); }
                        return result;
                    });

            Class<?> dismiss=Class.forName("com.android.systemui.shade.NotificationDismissViewController",false,cl);
            Method orient=dismiss.getDeclaredMethod("updateOrientation");
            module.hook(orient).setId("hc_split_chrome_dismiss")
                    .setExceptionMode(XposedInterface.ExceptionMode.DEFAULT).intercept(chain -> {
                        // Restore OUR full LP first: official updateOrientation only updates a subset.
                        View r=root.get();
                        View button=r==null?null:r.findViewById(DISMISS);
                        if(button!=null)restoreOne(button);
                        Object result=chain.proceed();
                        try { apply(); } catch(Throwable t) { error(t); }
                        return result;
                    });

            // ★ v291 修复问题一（横屏终态消失）+ 问题二（竖屏紧凑态不可点）。
            //
            // 官方链路（smali NotificationDismissViewController）：
            //   performAnimComplete():
            //       if (appearance == false) setVisibility(INVISIBLE)   ← 两个问题的共同源头
            //   updateAppearance$1():
            //       appearance = show && !disabled
            //   updateShow():
            //       show = (statusBarState != 1)
            //           && !isShowingKeyguardNotification()
            //           && !mHasPinnedNotification
            //           && !isHeadsUpAnimatingAwayValue()
            //           && hasClearableNotification          ← split 下语义错位
            //           && ambientState.panelAppeared        ← split 下语义错位
            //           && !qs.isShowingDetail()
            //           && !changeToModal
            //
            //   isInDismissViewBounds(FF)Z:
            //       if (dismissView.getVisibility() != VISIBLE) threshold = 0
            //       → 比较 y < 0 恒 false → 【按钮不可点击】
            //
            // 所以只要 appearance 判错导致 INVISIBLE，就同时触发：
            //   ① 横屏终态消失  ② 任何方向下不可点击
            //
            // 修法：hook performAnimComplete，在【运行开关开启 + 确实有可清除通知 + shade 已展开】
            // 时把 INVISIBLE 改回 VISIBLE 并恢复 clickable。
            //
            // 必须校验 hasClearableNotification，否则「通知全部清除后按钮本该消失」
            // 的场景也会被我们强行显示出来。
            Method anim;
            try { anim=dismiss.getDeclaredMethod("performAnimComplete"); }
            catch(NoSuchMethodException e) { anim=null; }
            if(anim!=null) {
                final Field clearable=fieldOpt(dismiss,"hasClearableNotification");
                module.hook(anim).setId("hc_split_chrome_anim_complete")
                        .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                            Object result=chain.proceed();
                            try {
                                View r=root.get();
                                if(r!=null && runtimeOn()) {
                                    Object self=chain.getThisObject();
                                    // 没有可清除通知时按钮本来就该消失，尊重官方判定。
                                    boolean hasClearable = clearable!=null && self!=null
                                            && Boolean.TRUE.equals(clearable.get(self));
                                    View button=r.findViewById(DISMISS);
                                    if(hasClearable && button!=null && button.getVisibility()!=View.VISIBLE) {
                                        button.setVisibility(View.VISIBLE);
                                        button.setClickable(true);
                                        if(FIX_LOGS.getAndIncrement()<8) android.util.Log.i(
                                                "HCShadeLayout","dismiss restored VISIBLE after performAnimComplete");
                                    }
                                }
                            } catch(Throwable t) { error(t); }
                            return result;
                        });
            }

            android.util.Log.i("HCShadeLayout","installed v291 header/footer-only + shade_header narrow + dismiss-visibility-guard; notification padding untouched; runtime prop=debug.hyperclassic.split_chrome default=1 (v359: was 0, caused split fixes to vanish after reboot when props reset)");

            // ★ v351：清除按钮触摸直通
            //
            // ===== 问题（v350 之后）=====
            //
            // v350 让按钮画出来了（parent 撑到 3200 + clipChildren=false），
            // 但主人实测「触摸无反应，等同于点击底部的空白」。
            //
            // ===== 为什么点不到 =====
            //
            // 三个独立的拦截层：
            //
            // ① ViewGroup 命中测试：
            //    ShadeHeaderContainer 仍只有 1577 宽（v290 收窄，不能回退），
            //    触摸点 (2402,1303) 不在它的 bounds 内
            //    → isTransformedTouchPointInView 返回 false
            //    → 事件根本不会分发给它的任何子 View（含 dismiss）
            //    → 这就是「等同点击空白」的直接原因
            //
            // ② isInDismissViewBounds(FF)Z（NotificationPanelViewControllerInjector:1770）：
            //    scrollRange > 0 && !isInContentBounds && y >= topPadding
            //      && dismissView.visibility == VISIBLE
            //      && orientation == PORTRAIT   ← ★ 只有竖屏才取 getBottom()
            //    横屏时 threshold = 0 → 判 y < 0 → 恒 false
            //    → 官方横屏根本没给 dismiss 提供触摸支持
            //
            // ③ NotificationShadeWindowView:1148 用它来决定 allowParentInterceptSwitchEvent：
            //    if (!isInDismissViewBounds(x,y)) allowParentInterceptSwitchEvent = true;
            //    → 返回 false → 面板抢走事件
            //
            // ===== 修法：完全绕开命中测试，自己做点按识别 =====
            //
            // 在窗口根 View 的 dispatchTouchEvent 入口：
            //   ACTION_DOWN：算 dismiss 按钮的屏幕绝对矩形，记下是否命中
            //   ACTION_UP  ：如果 DOWN 命中且 UP 仍在矩形内，调 performClick() 并消费事件
            //   其它动作    ：消费（避免事件漏到底部空白区触发误操作）
            //
            // 为什么用 dispatchTouchEvent 而不是 onInterceptTouchEvent：
            //   dispatchTouchEvent 是窗口根的第一入口，比任何拦截层都早，
            //   而且它拿得到原始坐标（相对根 View，根 View 就是全屏）。
            //
            // 为什么自己做矩形判定而不是让 View 自己处理：
            //   ViewGroup 的命中测试受 parent bounds 与 clipChildren 双重限制，
            //   我们无法在不改变 ShadeHeaderContainer 宽度（会破坏状态栏左对齐）的
            //   前提下让官方分发链路认领这个点。
            try {
                final Class<?> rootCls = Class.forName(
                        "com.android.systemui.shade.NotificationShadeWindowView", false, cl);
                Method dispatch = rootCls.getDeclaredMethod(
                        "dispatchTouchEvent", android.view.MotionEvent.class);
                dispatch.setAccessible(true);
                module.hook(dispatch).setId("hc_split_dismiss_touch_passthrough")
                        .setPriority(XposedInterface.PRIORITY_HIGHEST)
                        .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                        .intercept(chain -> {
                            try {
                                Object self = chain.getThisObject();
                                Object arg = chain.getArgs().isEmpty()
                                        ? null : chain.getArgs().get(0);
                                if (!(self instanceof View)
                                        || !(arg instanceof android.view.MotionEvent)) {
                                    return chain.proceed();
                                }
                                if (!runtimeOn() || !CcHooks.diagnosticLandscapeNow()) {
                                    return chain.proceed();
                                }
                                View rootView = (View) self;
                                android.view.MotionEvent ev = (android.view.MotionEvent) arg;
                                View btn = rootView.findViewById(DISMISS);
                                if (btn == null || btn.getVisibility() != View.VISIBLE
                                        || !btn.isClickable()) {
                                    return chain.proceed();
                                }
                                // 算按钮在本 View 坐标系内的矩形
                                int[] loc = new int[2];
                                btn.getLocationInWindow(loc);
                                int[] rootLoc = new int[2];
                                rootView.getLocationInWindow(rootLoc);
                                float l = loc[0] - rootLoc[0];
                                float t = loc[1] - rootLoc[1];
                                float r = l + btn.getWidth();
                                float b = t + btn.getHeight();
                                float x = ev.getX();
                                float y = ev.getY();
                                boolean inside = x >= l && x <= r && y >= t && y <= b;

                                int action = ev.getActionMasked();
                                if (action == android.view.MotionEvent.ACTION_DOWN) {
                                    dismissTapDown = inside;
                                    if (inside) {
                                        if (DISMISS_TAP_LOGS.getAndIncrement() < 6) {
                                            android.util.Log.i("HCShadeLayout",
                                                    "dismiss DOWN hit rect="
                                                    + (int) l + "," + (int) t + "-"
                                                    + (int) r + "," + (int) b
                                                    + " touch=" + (int) x + "," + (int) y);
                                        }
                                        return Boolean.TRUE;   // 消费，防止漏到底部区
                                    }
                                } else if (action == android.view.MotionEvent.ACTION_UP) {
                                    if (dismissTapDown) {
                                        dismissTapDown = false;
                                        if (inside) {
                                            btn.performClick();
                                            if (DISMISS_TAP_LOGS.getAndIncrement() < 12) {
                                                android.util.Log.i("HCShadeLayout",
                                                        "dismiss UP -> performClick");
                                            }
                                        }
                                        return Boolean.TRUE;
                                    }
                                } else if (action == android.view.MotionEvent.ACTION_CANCEL) {
                                    boolean was = dismissTapDown;
                                    dismissTapDown = false;
                                    if (was) {
                                        return Boolean.TRUE;
                                    }
                                } else if (dismissTapDown) {
                                    // MOVE 等：只要还处于我们认领的手势里就继续吃掉
                                    return Boolean.TRUE;
                                }
                            } catch (Throwable t) {
                                error(t);
                            }
                            return chain.proceed();
                        });
                android.util.Log.i("HCShadeLayout","installed v351 dismiss touch passthrough");
            } catch(Throwable t) {
                android.util.Log.w("HCShadeLayout","dismiss passthrough fail",t);
            }
        } catch(Throwable t) { android.util.Log.w("HCShadeLayout","install fail-closed",t); }
    }

    private static Field field(Class<?> c,String name)throws Exception {
        Field f=c.getDeclaredField(name); f.setAccessible(true); return f;
    }
    /** 可选字段：不存在时返回 null（不同 ROM 版本字段可能缺失）。 */
    private static Field fieldOpt(Class<?> c,String name) {
        try { return field(c,name); } catch(Throwable t) { return null; }
    }
    private static boolean runtimeOn() {
        long now=SystemClock.uptimeMillis();
        if(now-lastProp>500) {
            try { enabled="1".equals(Class.forName("android.os.SystemProperties")
                    .getMethod("get",String.class,String.class).invoke(null,"debug.hyperclassic.split_chrome","1")); }
            catch(Throwable ignored) { enabled=false; }
            lastProp=now;
        }
        return enabled;
    }
    private static boolean eligible(Object owner)throws Exception {
        return owner!=null && Looper.myLooper()==Looper.getMainLooper() && runtimeOn()
                && CcHooks.diagnosticLandscapeNow() && split.getBoolean(owner) && barState.getInt(owner)==0
                && Boolean.FALSE.equals(useCC.invoke(lazyGet.invoke(ccLazy.get(owner))));
    }
    private static void bind(View container) {
        if(container==null)return;
        View r=container.getRootView();
        if(root.get()!=r || qsFrame.get()==null || nssl.get()==null) {
            root=new WeakReference<>(r);qsFrame=new WeakReference<>(r.findViewById(QS_FRAME));
            nssl=new WeakReference<>(r.findViewById(NSSL));
        }
        if(watchedRoot.get()!=r) {
            watchedRoot=new WeakReference<>(r);
            // Listener changes only our idempotent LPs. No cancellation of draw or per-frame layout request.
            WeakReference<View> weakRoot=new WeakReference<>(r);
            r.getViewTreeObserver().addOnPreDrawListener(() -> {
                if(weakRoot.get()!=root.get())return true;
                try { apply(); }catch(Throwable t){error(t);}
                return true;
            });
        }
    }
    private static void apply()throws Exception {
        if(inFrame)return;
        inFrame=true;
        try {
            View r=root.get(), frame=qsFrame.get(), stack=nssl.get();
            if(r==null)return;

            // ★ v291 关键修复：竖屏（或非 split/非 classic）时【不写任何 LayoutParams】。
            //
            // 原因（实测回归）：
            //   v290 在竖屏下走的是 restore() 分支，而 restore() 会调
            //   view.setLayoutParams(lp) → 触发 requestLayout()。
            //   竖屏时 OnPreDraw 每帧都会执行 apply() → 每帧 requestLayout()
            //   → 布局反复重排，打断官方 NotificationDismissViewController
            //   .performAnimComplete() 的时序。
            //
            //   结果是 appearance 字段被判定为 false，于是：
            //     performAnimComplete(): if (appearance==false) setVisibility(INVISIBLE)
            //   → 清除按钮 INVISIBLE
            //   → isInDismissViewBounds() 里 `if (visibility != VISIBLE) threshold=0`
            //   → 比较 p2 < 0 恒 false
            //   → 竖屏紧凑态下按钮【无法点击】。
            //
            // 实测对照（竖屏，坐标相同 624,2861-815,3052）：
            //   split_chrome=1 → CircleAndTickAnimView{... IFED..... ......I.}  INVISIBLE 不可点
            //   split_chrome=0 → CircleAndTickAnimView{... VFED..C.. ........}  VISIBLE   可点
            //
            // ★ v295 修复 v291 的副作用：竖屏也必须【恢复一次】横屏写入的 LP。
            //
            //   v291 直接 return 导致横屏写入的 LP 永远不撤销，实测症状：
            //     MiuiNotificationHeaderView{a99122d G.E...... 811,0-2388,161}
            //                                                       ↑ 宽 1577（横屏值）
            //     竖屏只有 1440 宽 → 通知栏右侧超出屏幕 948px。
            //
            //   现在改成：竖屏时如果 ORIGINAL 里还有我们的写入记录，就执行【一次】
            //   恢复并清空，之后不再重复（restoredForPortrait 标志位），既恢复干净
            //   又不会每帧 requestLayout。
            if(!CcHooks.diagnosticLandscapeNow()) {
                // ★ v358：CLIP_STATE 也要参与判定，避免「只有裁剪状态残留但 ORIGINAL 已空」
                //   的极端情况让横屏裁剪泄漏到竖屏。
                if(!restoredForPortrait && (!ORIGINAL.isEmpty() || !CLIP_STATE.isEmpty())) {
                    restoredForPortrait=true;
                    restore();
                    if(LOGS.getAndIncrement()<6) android.util.Log.i("HCShadeLayout",
                            "portrait: restored original LPs + clip state once");
                }
                return;
            }
            restoredForPortrait=false;

            // v214 的关键门控：只有 shade 真正展开（fraction>=0.90）才动 LP。
            if(!eligible(qsc.get()) || fraction.getFloat(qsc.get())<0.90f) {restore();return;}
            // Configuration and measured geometry must both have settled to landscape.
            if(frame==null||stack==null||frame.getWidth()<=0||stack.getWidth()<=0
                    ||r.getWidth()<=r.getHeight()
                    ||r.getResources().getConfiguration().orientation!=2
                    ||frame.getResources().getConfiguration().orientation!=2
                    ||stack.getResources().getConfiguration().orientation!=2)return;

            header(r.findViewById(QS_HEADER),frame);
            header(r.findViewById(NOTIF_HEADER),frame);

            // ---- v290 新增：把 shade_header_container 从整屏宽收窄到 QS 半屏 ----
            // 否则它内部的顺屏状态栏内容按整屏宽度居中 → 视觉上「状态栏还在中间」。
            View shadeHeader=r.findViewById(SHADE_HEADER);
            if(shadeHeader!=null && shadeHeader.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams hp=(FrameLayout.LayoutParams)shadeHeader.getLayoutParams();
                save(shadeHeader,hp);
                int targetW=frame.getWidth();
                if(targetW>0 && hp.width!=targetW) {
                    hp.width=targetW;
                    hp.gravity=Gravity.TOP|Gravity.LEFT;
                    hp.leftMargin=0;
                    hp.setMarginStart(0);
                    LayoutSnapshot snap=ORIGINAL.get(shadeHeader);
                    if(snap!=null)snap.last=copy(hp);
                    shadeHeader.setLayoutParams(hp);
                    if(LOGS.getAndIncrement()<6) android.util.Log.i("HCShadeLayout",
                            "shade_header narrowed to "+targetW+" (was full "+r.getWidth()+")");
                }
            }

            View button=r.findViewById(DISMISS);
            if(button!=null && button.getParent() instanceof View
                    && button.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                View parent=(View)button.getParent();
                FrameLayout.LayoutParams lp=(FrameLayout.LayoutParams)button.getLayoutParams();
                save(button,lp);

                // ★ v350 关键修复：清除按钮被 parent 裁剪
                //
                // 实测（v349，3200x1440 横屏展开态）：
                //   CircleAndTickAnimView{...#7f0b0866 notification_dismiss_view
                //                         VFED..C.. ......I. 2307,1208-2498,1399}
                //     它的 parent:
                //   android.widget.FrameLayout{... V.E...... ........ 0,0-1577,1440}
                //     ↑ 只有 1577 宽（= QS 左栏），而按钮 x=2307 远在右栏
                //
                // 症状：按钮 visibility=VISIBLE、尺寸正常 191x191、clickable=true，
                //       但视觉上完全看不见，也点不到 —— 因为被 parent 边界裁掉了。
                //
                // 根因：官方 XML 里 dismiss 按钮挂在 shade_header_container 内，
                //       官方 shade_header 是全屏 3200 宽，所以按钮本来就该在右栏。
                //       v290 为了让状态栏内容左对齐，把 shade_header 收窄成 1577，
                //       于是把它内部的 dismiss 按钮一起裁掉了。
                //       —— 这就是主人说的「被某个半成品 hook 弄得无法完全显示」。
                //
                // 修法：只让这一条链路突破裁剪，不动 shade_header 的宽度本身
                //       （宽度是状态栏左对齐的关键，不能回退）。
                //   ① dismiss 的 parent FrameLayout 撑到全屏宽（它只有 dismiss 一个子项，无副作用）
                //   ② parent 与 shade_header 都设 clipChildren=false，
                //      让它能绘制到 1577 之外
                //   ③ 触摸不受影响：ViewGroup 分发用 isTransformedTouchPointInView，
                //      判定依据是子 View 自己的 left/right，与父的裁剪无关。
                // ★ v358 关键修复：清除按钮在竖屏完全不显示
                //
                // 【v357 的致命疏漏】下面这段把 dismiss 的 parent 宽度从 1577 强行撑到
                //   整屏宽（横屏 3200），但**没有调用 save(parent, pp)**，
                //   于是 ORIGINAL 里从来没有 parent 的快照。
                //
                // 【后果链】
                //   横屏写入 parent.width = 3200
                //   → 转竖屏，restore() 只遍历 ORIGINAL，parent 不在里面 → 宽度保持 3200
                //   → 竖屏屏宽只有 1440，parent 仍按 3200 布局
                //   → 官方 dismiss 按钮在 parent 里居中 → x ≈ (3200-191)/2 = 1504
                //   → 屏幕外 → 主人看到「清除按钮在竖屏完全不显示」
                //
                // 这就是主人推测的「做横屏兼容把竖屏搞坏了」。
                //
                // 修法：① 写之前先 save(parent, pp) 记录原始 LP
                //       ② 写完后同步 snap.last，让 restoreOne 能识别这是我们的写入
                //       ③ clipChildren / clipToPadding 也要记录原值并复原
                if(parent.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                    FrameLayout.LayoutParams pp=
                            (FrameLayout.LayoutParams)parent.getLayoutParams();
                    int fullW=r.getWidth();
                    if(fullW>0 && pp.width!=fullW) {
                        save(parent,pp);          // ★ 记录原始值（v357 遗漏）
                        pp.width=fullW;
                        pp.gravity=Gravity.TOP|Gravity.LEFT;
                        pp.leftMargin=0;
                        pp.setMarginStart(0);
                        LayoutSnapshot psnap=ORIGINAL.get(parent);
                        if(psnap!=null)psnap.last=copy(pp);   // ★ 标记这是我们的写入
                        parent.setLayoutParams(pp);
                    }
                    if(parent instanceof android.view.ViewGroup) {
                        setClip((android.view.ViewGroup)parent,true);
                    }
                }
                View shadeHeaderForClip=r.findViewById(SHADE_HEADER);
                if(shadeHeaderForClip instanceof android.view.ViewGroup) {
                    setClip((android.view.ViewGroup)shadeHeaderForClip,true);
                }

                int gap=Math.round(12*r.getResources().getDisplayMetrics().density);
                int bottom=layoutY(stack)+stack.getHeight();
                WindowInsets wi=r.getRootWindowInsets();
                if(wi!=null) {
                    Insets inset=wi.getInsets(WindowInsets.Type.navigationBars()|WindowInsets.Type.displayCutout());
                    bottom=Math.min(bottom,layoutY(r)+r.getHeight()-inset.bottom);
                }
                int x=layoutX(stack)+(stack.getWidth()-lp.width)/2-layoutX(parent);
                int y=bottom-layoutY(parent)-lp.height-gap;
                place(button,lp,lp.width,x,Math.max(0,y));
                if(LOGS.get()<4 && LOGS.getAndIncrement()<4) android.util.Log.i("HCShadeLayout","footer columnCenter="
                        +(layoutX(stack)+stack.getWidth()/2)+" bottom="+bottom+" xy="+x+":"+y
                        +" parentW="+parent.getWidth()+" btnAbs="+(layoutX(parent)+x));
            }
        } finally {inFrame=false;}
    }
    private static void header(View header,View frame) {
        if(header==null||!(header.getParent() instanceof View)
                ||!(header.getLayoutParams() instanceof FrameLayout.LayoutParams))return;
        FrameLayout.LayoutParams lp=(FrameLayout.LayoutParams)header.getLayoutParams();save(header,lp);
        int left=layoutX(frame)-layoutX((View)header.getParent());
        // Keep the official top margin/height and inner padding; the whole header belongs to QS column.
        place(header,lp,frame.getWidth(),left,lp.topMargin);
    }
    private static final class LayoutSnapshot {
        final FrameLayout.LayoutParams original;
        FrameLayout.LayoutParams last;
        LayoutSnapshot(FrameLayout.LayoutParams lp) {original=copy(lp);}
    }
    private static void save(View view,FrameLayout.LayoutParams lp) {
        LayoutSnapshot snapshot=ORIGINAL.get(view);
        if(snapshot==null) { ORIGINAL.put(view,new LayoutSnapshot(lp));return; }
        // Keep any official configuration changes observed since our last write.
        FrameLayout.LayoutParams old=snapshot.original,last=snapshot.last;
        if(last==null)return;
        if(lp.width!=last.width)old.width=lp.width;
        if(lp.gravity!=last.gravity)old.gravity=lp.gravity;
        if(lp.leftMargin!=last.leftMargin)old.leftMargin=lp.leftMargin;
        if(lp.rightMargin!=last.rightMargin)old.rightMargin=lp.rightMargin;
        if(lp.topMargin!=last.topMargin)old.topMargin=lp.topMargin;
        if(lp.bottomMargin!=last.bottomMargin)old.bottomMargin=lp.bottomMargin;
        if(lp.getMarginStart()!=last.getMarginStart())old.setMarginStart(lp.getMarginStart());
        if(lp.getMarginEnd()!=last.getMarginEnd())old.setMarginEnd(lp.getMarginEnd());
    }
    private static FrameLayout.LayoutParams copy(FrameLayout.LayoutParams lp) {
        FrameLayout.LayoutParams c=new FrameLayout.LayoutParams(lp.width,lp.height,lp.gravity);
        c.setMargins(lp.leftMargin,lp.topMargin,lp.rightMargin,lp.bottomMargin);
        c.setMarginStart(lp.getMarginStart());c.setMarginEnd(lp.getMarginEnd());return c;
    }
    private static void place(View view,FrameLayout.LayoutParams lp,int width,int left,int top) {
        int g=Gravity.TOP|Gravity.LEFT;
        // 幂等：参数没变就直接返回，绝不 setLayoutParams → 不产生额外 requestLayout。
        if(lp.width==width&&lp.gravity==g&&lp.leftMargin==left&&lp.topMargin==top
                &&lp.rightMargin==0&&lp.bottomMargin==0&&lp.getMarginStart()==left&&lp.getMarginEnd()==0)return;
        save(view,lp);
        lp.width=width;lp.gravity=g;lp.setMargins(left,top,0,0);lp.setMarginStart(left);lp.setMarginEnd(0);
        LayoutSnapshot snap=ORIGINAL.get(view);
        if(snap!=null)snap.last=copy(lp);
        view.setLayoutParams(lp);
    }
    private static void restoreOne(View view) {
        LayoutSnapshot snapshot=ORIGINAL.remove(view);
        if(snapshot==null||snapshot.last==null||!(view.getLayoutParams() instanceof FrameLayout.LayoutParams))return;
        FrameLayout.LayoutParams current=(FrameLayout.LayoutParams)view.getLayoutParams();
        FrameLayout.LayoutParams lp=copy(current),old=snapshot.original,last=snapshot.last;
        // Restore only properties still equal to OUR last write. Never restore height: bridge did not own it.
        if(lp.width==last.width)lp.width=old.width;
        if(lp.gravity==last.gravity)lp.gravity=old.gravity;
        if(lp.leftMargin==last.leftMargin)lp.leftMargin=old.leftMargin;
        if(lp.rightMargin==last.rightMargin)lp.rightMargin=old.rightMargin;
        if(lp.topMargin==last.topMargin)lp.topMargin=old.topMargin;
        if(lp.bottomMargin==last.bottomMargin)lp.bottomMargin=old.bottomMargin;
        if(lp.getMarginStart()==last.getMarginStart())lp.setMarginStart(old.getMarginStart());
        if(lp.getMarginEnd()==last.getMarginEnd())lp.setMarginEnd(old.getMarginEnd());
        view.setLayoutParams(lp);
    }
    private static void restore() {
        for(View view:ORIGINAL.keySet().toArray(new View[0])) if(view!=null)restoreOne(view);
        ORIGINAL.clear();
        // ★ v358：裁剪状态也要复原，否则横屏留下的 clipChildren=false 会跟到竖屏。
        for(Map.Entry<View,boolean[]> e:CLIP_STATE.entrySet()) {
            View v=e.getKey();
            boolean[] st=e.getValue();
            if(v instanceof ViewGroup && st!=null && st.length>=2) {
                ((ViewGroup)v).setClipChildren(st[0]);
                ((ViewGroup)v).setClipToPadding(st[1]);
            }
        }
        CLIP_STATE.clear();
    }

    /**
     * ★ v358：横屏关裁剪（让 dismiss 能画到 parent 外面），并记录原始值。
     *
     * 为什么要记录：横屏下把 parent 与 shade_header 都设了 clipChildren=false，
     * 转竖屏后如果只恢复 LayoutParams 不恢复裁剪状态，会留下两个副作用：
     *   ① parent 变宽后内容溢出到屏幕外却仍被绘制（性能与视觉噪音）
     *   ② 官方竖屏布局依赖默认裁剪，可能造成其他元素错位
     */
    private static void setClip(ViewGroup vg, boolean clip) {
        boolean wantChildren=!clip, wantPadding=!clip;
        if(vg.getClipChildren()==wantChildren && vg.getClipToPadding()==wantPadding) {
            // 已经是目标状态：只在首次记录原始值
            if(!CLIP_STATE.containsKey(vg)) {
                CLIP_STATE.put(vg,new boolean[]{vg.getClipChildren(),vg.getClipToPadding()});
            }
            return;
        }
        if(!CLIP_STATE.containsKey(vg)) {
            CLIP_STATE.put(vg,new boolean[]{vg.getClipChildren(),vg.getClipToPadding()});
        }
        vg.setClipChildren(wantChildren);
        vg.setClipToPadding(wantPadding);
    }
    private static int layoutX(View v) {
        int result=v.getLeft();
        for(android.view.ViewParent p=v.getParent();p instanceof View;p=p.getParent())result+=((View)p).getLeft();
        return result;
    }
    private static int layoutY(View v) {
        int result=v.getTop();
        for(android.view.ViewParent p=v.getParent();p instanceof View;p=p.getParent())result+=((View)p).getTop();
        return result;
    }
    private static void error(Throwable t) {
        if(LOGS.getAndIncrement()<6)android.util.Log.w("HCShadeLayout","layout fallback",t);
    }
}
