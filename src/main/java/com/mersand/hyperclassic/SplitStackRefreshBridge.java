package com.mersand.hyperclassic;

import android.content.res.Configuration;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;

final class SplitStackRefreshBridge {
    private static final String TAG="HCShadeStack";
    private static final String PROP="debug.hyperclassic.split_stack_refresh";
    private static final String DIAG="debug.hyperclassic.split_stack_diag";
    private static final Map<View,State> STATES=new WeakHashMap<>();
    private static Class<?> rowClass, notificationRow;
    private static Field nsslInj, ownerField, qsLazy, ccLazy, split, bar, fraction, ambientField,
            algorithmField, algorithmInjector, interactorLazy, stackingBottom, calculator, rowCalculator,
            rowProgress, rowZ, applyFlag, scaleFlag;
    /** ★ v287 新增：split 下的展开判定依据（fraction 恒为 0，不可用）。 */
    private static Field height, maxHeight, transitionProgress;
    private static Method lazyGet, useCC, updateResources, requestUpdate, rowInjector, schedule,
            dispatch, children, actualHeight, superScaleX, superScaleY, superY, superZ, propGet;
    private static long lastProps;
    private static boolean enabled, diagnostic;
    private static Handler writer;
    private static int lines, bytes;

    private static final class State {
        int orientation, generation, width, height, stableFrames, waited;
        boolean armed, watching, pendingAlgorithm;
        long due;
        View.OnLayoutChangeListener unused; // No layout mutation/listener is installed on notification rows.
    }

    static void install(XposedModule module,ClassLoader cl) {
        try {
            Class<?> n=Class.forName("com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout",false,cl);
            Class<?> ni=Class.forName("com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutInjector",false,cl);
            Class<?> ctl=Class.forName("com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutControllerInjectorImpl",false,cl);
            Class<?> q=Class.forName("com.android.systemui.shade.QuickSettingsControllerImpl",false,cl);
            Class<?> cc=Class.forName("com.android.systemui.controlcenter.policy.ControlCenterControllerImpl",false,cl);
            Class<?> alg=Class.forName("com.android.systemui.statusbar.notification.stack.StackScrollAlgorithm",false,cl);
            Class<?> ai=Class.forName("com.android.systemui.statusbar.notification.stack.StackScrollAlgorithmInjector",false,cl);
            Class<?> am=Class.forName("com.android.systemui.statusbar.notification.stack.AmbientState",false,cl);
            Class<?> inter=Class.forName("com.miui.systemui.notification.domain.interactor.NotificationStackingInteractor",false,cl);
            Class<?> ri=Class.forName("com.android.systemui.statusbar.notification.row.ExpandableViewInjector",false,cl);
            Class<?> calc=Class.forName("com.miui.systemui.notification.domain.interactor.NotificationStackingCalculator",false,cl);
            Class<?> properties=Class.forName("com.miui.systemui.widget.PropertiesFrameLayout",false,cl);
            rowClass=Class.forName("com.android.systemui.statusbar.notification.row.ExpandableView",false,cl);
            notificationRow=Class.forName("com.android.systemui.statusbar.notification.row.ExpandableNotificationRow",false,cl);
            nsslInj=field(n,"mNsslInjector");ownerField=field(ni,"nsslControllerInjector");
            qsLazy=field(ctl,"qsController");ccLazy=field(ctl,"controlCenterControllerImpl");
            split=field(q,"mSplitShadeEnabled");bar=field(q,"mBarState");fraction=field(q,"mShadeExpandedFraction");
            // ★ v287 新增：split 下 fraction 恒为 0，改用高度/progress 判定展开状态
            height=field(q,"mExpansionHeight");
            maxHeight=field(q,"mMaxExpansionHeight");
            Field tp=null; try{tp=field(q,"mTransitioningToFullShadeProgress");}catch(Throwable ignored){}
            transitionProgress=tp;
            ambientField=field(n,"mAmbientState");algorithmField=field(n,"mStackScrollAlgorithm");
            algorithmInjector=field(alg,"mInjector");interactorLazy=field(ai,"notifStackingInteractor");
            stackingBottom=field(ctl,"stackingBottom");calculator=field(inter,"calculator");
            rowCalculator=field(ri,"stackingInfoCalculator");rowProgress=field(ri,"stackingProgress");rowZ=field(ri,"stackingTransZ");
            applyFlag=field(ri,"DEBUG_APPLY_STACK_INFO");scaleFlag=field(calc,"USE_SCALE");
            lazyGet=method(Class.forName("dagger.Lazy",false,cl),"get");useCC=method(cc,"isUseControlCenter");
            updateResources=method(inter,"updateResourceValues$packages__apps__MiuiSystemUI__packages__SystemUI__android_common__MiuiSystemUI_core");
            requestUpdate=method(n,"requestChildrenUpdate");rowInjector=method(rowClass,"getInjector");
            schedule=method(properties,"scheduleUpdateProperties");dispatch=method(properties,"dispatchPropertiesUpdate");
            children=method(notificationRow,"getAttachedChildren");actualHeight=method(rowClass,"getActualHeight");
            superScaleX=method(properties,"getSuperScaleX");superScaleY=method(properties,"getSuperScaleY");
            superY=method(properties,"getSuperTranslationY");superZ=method(properties,"getSuperTranslationZ");
            propGet=Class.forName("android.os.SystemProperties").getMethod("get",String.class,String.class);
            Method config=method(n,"onConfigurationChanged",Configuration.class);
            module.hook(config).setId("hc_split_stack_config").setExceptionMode(XposedInterface.ExceptionMode.DEFAULT).intercept(chain -> {
                Object result=chain.proceed();
                try {
                    if(main()&&active()) {
                        View v=(View)chain.getThisObject();State s=state(v);
                        arm(v,s,((Configuration)chain.getArg(0)).orientation);
                    }
                }catch(Throwable t){error(t);}
                return result;
            });
            Method layout=method(n,"onLayout",boolean.class,int.class,int.class,int.class,int.class);
            module.hook(layout).setId("hc_split_stack_layout").setExceptionMode(XposedInterface.ExceptionMode.DEFAULT).intercept(chain -> {
                Object result=chain.proceed();
                try {
                    if(main()&&active()) {
                        View v=(View)chain.getThisObject();State s=state(v);
                        int o=v.getResources().getConfiguration().orientation;
                        if(s.orientation!=o)arm(v,s,o);
                        watch(v,s);
                    }
                }catch(Throwable t){error(t);}
                return result;
            });
            Method reset=method(alg,"resetViewStates",am,int.class);
            module.hook(reset).setId("hc_split_stack_after_algorithm").setExceptionMode(XposedInterface.ExceptionMode.DEFAULT).intercept(chain -> {
                Object result=chain.proceed();
                try {
                    if(main()&&active())for(Map.Entry<View,State> entry:STATES.entrySet()) {
                        View v=entry.getKey();State s=entry.getValue();
                        if(v==null||!s.pendingAlgorithm||algorithmField.get(v)!=chain.getThisObject())continue;
                        s.pendingAlgorithm=false;
                        int generation=s.generation;
                        WeakReference<View> weak=new WeakReference<>(v);
                        v.postOnAnimation(() -> applyRows(weak,s,generation));
                        break;
                    }
                }catch(Throwable t){error(t);}
                return result;
            });
            android.util.Log.i(TAG,"installed v215 one-shot official resource/algorithm/row-properties refresh; default=0; no geometry writes");
        }catch(Throwable t){android.util.Log.w(TAG,"install fail-closed",t);}
    }

    private static State state(View v) {
        State s=STATES.get(v);
        if(s==null){s=new State();STATES.put(v,s);}
        return s;
    }
    private static void arm(View v,State s,int orientation) {
        s.generation++;s.orientation=orientation;s.pendingAlgorithm=false;s.stableFrames=0;s.waited=0;
        s.armed=orientation==Configuration.ORIENTATION_LANDSCAPE;
        s.due=SystemClock.uptimeMillis()+160;
        emit(v,"config generation="+s.generation+" orientation="+orientation+" refresh="+enabled);
        watch(v,s);
    }
    private static void watch(View v,State s) {
        if(!s.armed||s.watching)return;
        s.watching=true;
        WeakReference<View> weak=new WeakReference<>(v);
        v.getViewTreeObserver().addOnPreDrawListener(new android.view.ViewTreeObserver.OnPreDrawListener() {
            private void remove(View view) {
                s.watching=false;
                if(view!=null&&view.getViewTreeObserver().isAlive())view.getViewTreeObserver().removeOnPreDrawListener(this);
            }
            @Override public boolean onPreDraw() {
                View view=weak.get();
                try {
                    if(view==null||!active()||!s.armed||!view.isAttachedToWindow()){remove(view);return true;}
                    View root=view.getRootView();
                    boolean geometry=view.getWidth()>0&&view.getHeight()>0&&root.getWidth()>root.getHeight()
                            &&view.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE;
                    if(!geometry){s.stableFrames=0;return true;}
                    if(view.getWidth()==s.width&&view.getHeight()==s.height)s.stableFrames++;
                    else {s.width=view.getWidth();s.height=view.getHeight();s.stableFrames=0;}
                    // A closed shade is deliberately not kept drawing. The next official draw rechecks eligibility.
                    if(SystemClock.uptimeMillis()<s.due||s.stableFrames<2||!eligible(view))return true;
                    if(Boolean.TRUE.equals(read(view,"mIsBeingDragged")))return true;
                    s.armed=false;remove(view);
                    Object inter=interactor(view);
                    snapshot(view,s,"before",inter);
                    if(!enabled){emit(view,"observation-only generation="+s.generation);return true;}
                    updateResources.invoke(inter);
                    s.pendingAlgorithm=true;
                    requestUpdate.invoke(view);
                    emit(view,"requestChildrenUpdate generation="+s.generation+" waiting_official_algorithm=true");
                    int generation=s.generation;
                    view.postDelayed(() -> {
                        View x=weak.get();
                        if(x!=null&&s.generation==generation&&s.pendingAlgorithm){s.pendingAlgorithm=false;emit(x,"skip generation="+generation+" reason=no_official_algorithm_within_1000ms");}
                    },1000);
                }catch(Throwable t){s.armed=false;s.pendingAlgorithm=false;remove(view);error(t);}
                return true;
            }
        });
    }
    private static void applyRows(WeakReference<View> weak,State s,int generation) {
        View v=weak.get();
        try {
            if(v==null||s.generation!=generation||!active()||!enabled||!eligible(v))return;
            snapshot(v,s,"before-properties",interactor(v));
            List<View> rows=rows((ViewGroup)v);
            for(View row:rows)if(row.isAttachedToWindow()) {
                // Both calls are official and dirty-bit aware. Never set row scale/Y/Z/alpha ourselves.
                schedule.invoke(row);dispatch.invoke(row);
            }
            emit(v,"applied generation="+generation+" attachedRows="+rows.size()+" source=official_schedule_and_dispatch");
            snapshot(v,s,"after-properties",interactor(v));
            v.postDelayed(() -> {
                View x=weak.get();
                if(x!=null&&s.generation==generation&&active())try{snapshot(x,s,"settled160ms",interactor(x));}catch(Throwable t){error(t);}
            },160);
        }catch(Throwable t){error(t);}
    }
    private static boolean eligible(View v)throws Exception {
        if(!main()||!CcHooks.diagnosticLandscapeNow()||!v.isAttachedToWindow()||!v.isShown()
                ||v.getResources().getConfiguration().orientation!=Configuration.ORIENTATION_LANDSCAPE
                ||v.getRootView().getWidth()<=v.getRootView().getHeight())return false;
        Object owner=ownerField.get(nsslInj.get(v));
        Object qs=lazyGet.invoke(qsLazy.get(owner));
        if(!split.getBoolean(qs)||bar.getInt(qs)!=0)return false;
        if(!Boolean.FALSE.equals(useCC.invoke(lazyGet.invoke(ccLazy.get(owner)))))return false;
        // ★ v287 关键修正：split 下 mShadeExpandedFraction 恒为 0.0（V274 文档事实 B），
        //   原判断 fraction>=0.90f 在 split 横屏下永远不成立，导致整个 refresh 从不执行。
        return splitExpanded(qs);
    }

    /**
     * split 感知的「已展开」判定。
     *
     * 背景（smali 实证）：
     *   QuickSettingsControllerImpl.updateExpansion() 里有
     *     iget-boolean v0, mSplitShadeEnabled
     *     if-nez v0, :cond_3        ← split 开启时直接跳过常规 fraction 累加
     *   所以 mShadeExpandedFraction 在 split 横屏恒为 0.0，
     *   真正反映展开程度的是 mExpansionHeight / mMaxExpansionHeight 或 transitionProgress。
     */
    private static boolean splitExpanded(Object qs){
        // 优先：高度撑开比例
        try {
            float cur = height.getFloat(qs);
            int maxH = maxHeight.getInt(qs);
            if (maxH > 0 && cur > 0 && cur / (float) maxH >= 0.85f) return true;
        } catch (Throwable ignored) { }
        // 次选：mTransitioningToFullShadeProgress > 0
        try {
            if (transitionProgress != null && transitionProgress.getFloat(qs) > 0.0f) return true;
        } catch (Throwable ignored) { }
        // 兜底：fraction
        try {
            return fraction.getFloat(qs) >= 0.90f;
        } catch (Throwable ignored) { }
        return false;
    }
    private static Object interactor(View v)throws Exception {
        return lazyGet.invoke(interactorLazy.get(algorithmInjector.get(algorithmField.get(v))));
    }
    private static List<View> rows(ViewGroup v)throws Exception {
        List<View> list=new ArrayList<>();
        for(int i=0;i<v.getChildCount()&&list.size()<64;i++)addRow(v.getChildAt(i),list);
        return list;
    }
    private static void addRow(View v,List<View> rows)throws Exception {
        if(!rowClass.isInstance(v)||rows.size()>=64)return;
        rows.add(v);
        if(notificationRow.isInstance(v)) {
            Object group=children.invoke(v);
            if(group instanceof List<?>)for(Object child:(List<?>)group)
                if(child instanceof View&&child!=v&&rows.size()<64&&!rows.contains(child))rows.add((View)child);
        }
    }
    private static void snapshot(View v,State s,String phase,Object inter)throws Exception {
        Object ambient=ambientField.get(v),owner=ownerField.get(nsslInj.get(v)),calc=calculator.get(inter);
        View tray=v.getRootView().findViewById(0x7f0b09a6);
        // tray is looked up by runtime name below; no hard dependency on a guessed resource id.
        int id=v.getResources().getIdentifier("qsBackground","id","com.android.systemui");
        tray=id==0?null:v.getRootView().findViewById(id);
        emit(v,"snapshot phase="+phase+" generation="+s.generation+" wh="+v.getWidth()+":"+v.getHeight()
                +" scroll="+read(v,"mOwnScrollY")+" topPadding="+read(ambient,"mTopPadding")
                +" stackY="+read(ambient,"mStackY")+" bottom="+stackingBottom.getFloat(owner)
                +" boundary="+read(inter,"stackingBoundary")+" heights="+read(inter,"stackingHeight1")+":"+read(inter,"stackingHeight2")
                +" padding="+read(inter,"padding")+" calcHeights="+read(calc,"stackingHeight1")+":"+read(calc,"stackingHeight2")
                +" apply="+applyFlag.get(null)+" useScale="+scaleFlag.get(null)+" nsslLayoutY="+layoutY(v)
                +" trayLayoutY="+(tray==null?"?":layoutY(tray)));
        List<View> list=rows((ViewGroup)v);
        for(int i=0;i<list.size()&&i<12;i++) {
            View row=list.get(i);Object inj=rowInjector.invoke(row),c=rowCalculator.get(inj);
            emit(v,"row phase="+phase+" index="+i+" attached="+row.isAttachedToWindow()+" visible="+row.getVisibility()
                    +" calculator="+(c==null?"null":c.getClass().getSimpleName())
                    +" baseY="+row.getTranslationY()+" actualY="+superY.invoke(row)
                    +" baseScale="+row.getScaleX()+":"+row.getScaleY()+" actualScale="+superScaleX.invoke(row)+":"+superScaleY.invoke(row)
                    +" progress="+rowProgress.getFloat(inj)+" stackingZ="+rowZ.getFloat(inj)+" actualZ="+superZ.invoke(row)
                    +" actualHeight="+actualHeight.invoke(row)+" foregroundAlpha="+(row.getForeground()==null?"none":row.getForeground().getAlpha()));
        }
    }
    private static int layoutY(View v) {
        int y=v.getTop();
        for(android.view.ViewParent p=v.getParent();p instanceof View;p=p.getParent())y+=((View)p).getTop();
        return y;
    }
    private static boolean main(){return Looper.myLooper()==Looper.getMainLooper();}
    private static boolean active() {
        long now=SystemClock.uptimeMillis();
        if(now-lastProps>300) {
            try {enabled="1".equals(propGet.invoke(null,PROP,"0"));diagnostic="1".equals(propGet.invoke(null,DIAG,"0"));}
            catch(Throwable ignored){enabled=false;diagnostic=false;}
            lastProps=now;
        }
        return enabled||diagnostic;
    }
    private static Field field(Class<?> c,String n)throws Exception{Field f=c.getDeclaredField(n);f.setAccessible(true);return f;}
    private static Method method(Class<?> c,String n,Class<?>...args)throws Exception{Method m=c.getDeclaredMethod(n,args);m.setAccessible(true);return m;}
    private static Object read(Object o,String n) {
        if(o==null)return "null";
        try {return field(o.getClass(),n).get(o);}catch(Throwable t){return "?";}
    }
    private static void emit(View v,String message) {
        if(lines++>=500)return;
        String line="pid="+Process.myPid()+" t="+SystemClock.uptimeMillis()+" "+message;
        android.util.Log.i(TAG,line);
        if(writer==null){HandlerThread t=new HandlerThread("HC-StackDiag",Process.THREAD_PRIORITY_BACKGROUND);t.start();writer=new Handler(t.getLooper());}
        File file=new File(v.getContext().getCacheDir(),"hyperclassic/stackrefresh-"+Process.myPid()+".log");
        writer.post(() -> {
            try {
                byte[] data=(line+"\n").getBytes(StandardCharsets.UTF_8);
                if(bytes+data.length>512*1024)return;
                File parent=file.getParentFile();if(!parent.isDirectory()&&!parent.mkdirs())return;
                try(FileOutputStream stream=new FileOutputStream(file,true)){stream.write(data);bytes+=data.length;}
            }catch(Throwable ignored){ /* bounded logcat remains available */ }
        });
    }
    private static int errors;
    private static void error(Throwable t){if(errors++<6)android.util.Log.w(TAG,"refresh fail-closed; original path preserved",t);}
}