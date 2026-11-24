# 超级岛位置迁移研究报告（方案① 侦查结论）

> 日期：2026-11-24
> 目标设备：Redmi K80 Pro（1440x3200），HyperOS 4 / Android 17
> 目标：把超级岛从屏幕顶部居中，移到横屏 split shade 下的 QS 左栏上方并居中
> 结论：**方案①（hook `getAreaLeftCollapseTransX`）不可行，正确拦截点是 `IslandCoordinateCalculator.getSmallIslandX()`**

---

## 一、研究对象与位置

| 项目 | 值 |
|---|---|
| 窗口名 | `DynamicIslandWindow` |
| 窗口所属进程 | `miui.systemui.plugin` |
| 插件 APK | `/product/app/MIUISystemUIPlugin/MIUISystemUIPlugin.apk` |
| 插件版本 | 18.3.2.67.0（`pm dump` 报 versionName=18.3.2.67.0，apktool 报 1.1.0） |
| 入口 service | `miui.systemui.notification.NotificationDynamicIslandPluginImpl` |
| 核心目录 | `plugin_decode/smali_classes2/miui/systemui/dynamicisland/` |
| 解包产物 | 工作区 `plugin_decode/`（14450 个 smali） |

实测窗口属性（`dumpsys window windows`）：

```
Window{4f0489a u0 DynamicIslandWindow}
  mAttrs={(0,0)(fillx176) gr=TOP CENTER_VERTICAL ... ty=KEYGUARD_DIALOG}
  mBaseLayer=191000 mSubLayer=0
  mTouchableInsets=3
```

> 注意：`gr=TOP CENTER_VERTICAL`，宽度 `fill`（MATCH_PARENT），高度 176px。
> 窗口本身横跨全屏，**岛的水平位置完全由窗口内部 View 的 translationX 决定**，不是窗口 LP。

---

## 二、视图树（完整）

```
DynamicIslandWindow（窗口，fill x 176）
└─ DynamicIslandWindowView            ← R.layout.dynamic_island_window_view
   ├─ DynamicIslandEmptyContentView  (id=empty_content_view)
   ├─ FrameLayout                    (id=glow_effect_bottom_container)
   └─ FrameLayout                    (id=glow_effect_top_container)
   └─ [+] addView(DynamicIslandBackgroundView)   ← 岛本体，动态添加

DynamicIslandBackgroundView           ← R.layout.dynamic_island_view, id=island_container
└─ DynamicIslandContentView           (id=island_content)
   └─ FrameLayout (container)         ← R.layout.dynamic_island_child_view, layout_gravity="bottom"
      ├─ ViewStub (expanded_view_stub)      layout_gravity="center_horizontal"
      ├─ DynamicIslandBigIslandView         layout_gravity="center_horizontal"
      ├─ FrameLayout (small_island_view)    layout_gravity="center_horizontal"
      └─ View (island_mask)
```

**关键 xml 证据：**

`res/layout/dynamic_island_view.xml`
```xml
<miui.systemui.dynamicisland.DynamicIslandBackgroundView
    android:id="@id/island_container"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content">
    <miui.systemui.dynamicisland.window.content.DynamicIslandContentView
        android:id="@id/island_content"
        android:visibility="invisible"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content">
        <include layout="@layout/dynamic_island_child_view" />
    </miui.systemui.dynamicisland.window.content.DynamicIslandContentView>
</miui.systemui.dynamicisland.DynamicIslandBackgroundView>
```

`res/layout/dynamic_island_child_view.xml`（节选）
```xml
<FrameLayout android:layout_gravity="bottom" android:id="@id/container" ...>
    <FrameLayout ...>
        <ViewStub android:layout_gravity="center_horizontal" android:id="@id/expanded_view_stub" .../>
        <DynamicIslandBigIslandView android:layout_gravity="center_horizontal" android:id="@id/big_island_view" .../>
        <FrameLayout android:layout_gravity="center_horizontal" android:id="@id/small_island_view" .../>
        <View android:id="@id/island_mask" .../>
    </FrameLayout>
</FrameLayout>
```

> **岛在窗口内的水平居中靠子 View 的 `layout_gravity="center_horizontal"`，窗口 LP 不参与。**

---

## 三、位置决定链（核心）

### 3.1 最终落地：`IslandPropertyUpdater.updateContainer()`

文件：`smali_classes2/miui/systemui/dynamicisland/anim/ui/animator/IslandPropertyUpdater.smali`（第 432 行）

```java
public final void updateContainer(IslandAnimProperties props) {
    view.setTranslationX(props.getContainerX());       // ★★★ 唯一水平位置来源
    view.setTranslationY(props.getContainerTransY());
    view.setAlpha(props.getContainerAlpha());

    // 平板分支：只影响背景色，不影响 X
    if (CommonUtils.INSTANCE.getIS_TABLET() && !MiBlurCompat.getBackgroundMaterialOpened(ctx)) {
        view.getContainer().setBackgroundColor(props.getContainerBgColor());
    }
    ...
}
```

### 3.2 containerX 的来源：`IslandCoordinateCalculator.getSmallIslandX()`

文件：`.../anim/ui/animator/IslandCoordinateCalculator.smali`（第 716 行）

```java
public final float getSmallIslandX() {
    int smallIslandX = dynamicIslandAnimController.getSmallIslandX();      // 屏幕绝对 X
    int screenW = DynamicIslandUtils.INSTANCE.getScreenWidthOld(ctx);
    int rel = smallIslandX - screenW / 2;                                  // 转成「相对屏幕中心」
    int halfIslandW = view.getSmallIslandViewWidth() / 2;
    return (float)(rel + halfIslandW);
}
```

### 3.3 基准位移

文件：`DynamicIslandWindowView.smali`（第 1481/1491 行，`buildDynamicIslandContentView`）

```java
int sw = DynamicIslandUtils.getScreenWidthOld(ctx);
contentView.setTranslationX(sw / 2.0f);          // ★ 基准：屏幕中心
int sh = DynamicIslandUtils.getScreenHeightOld(ctx);
contentView.setTranslationY(-sh);                // 初始藏在屏幕外（动画起点）
addView(contentView.getBackgroundView());
```

**合成公式：**

```
绝对 X = screenWidth/2 + containerX
       = screenWidth/2 + (smallIslandX - screenWidth/2) + islandWidth/2
       = smallIslandX + islandWidth/2
```

即：**`getSmallIslandX()` 返回的就是岛的屏幕绝对左边缘**，与屏幕宽度无关。

---

## 四、`IslandAnimProperties` 构造参数顺序（实测确认）

文件：`.../anim/model/IslandAnimProperties.smali`

字段按字典序（Kotlin data class 编译产物），共 28 个参数：

| # | 参数 | 类型 |
|---|---|---|
| 1 | containerBlur | F |
| 2 | **containerX** | F |
| 3 | containerTransY | F |
| 4 | containerAlpha | F |
| 5 | containerClipStart | F |
| 6 | **bigIslandAreaLeft** | F |
| 7 | **bigIslandAreaRight** | F |
| ... | ... | ... |

验证方式：`copy$default` 的 `p1..p28` 掩码顺序与 `getContainerX`/`getBigIslandAreaLeft` 的字段读取一一对应。

---

## 五、官方「左/中/右三区域」机制（重要）

`DynamicIslandBaseContentView` 持有三个区域容器：

```java
private FrameLayout bigIslandAreaLeft;    // 左区域
private FrameLayout bigIslandAreaMargin;  // 中区域（岛当前所在）
private FrameLayout bigIslandAreaRight;   // 右区域
```

绑定点：`DynamicIslandContentView.smali:3360` 附近（`findAndInitViews` 之后）

```java
holder = helper.findAndInitViews(...);
setBigContainer(holder.getBigContainer());
setSmallContainer(holder.getSmallContainer());
setBigIslandAreaLeft(holder.getBigIslandAreaLeft());     // ★
setBigIslandAreaRight(holder.getBigIslandAreaRight());
setBigIslandAreaMargin(holder.getBigIslandAreaMargin());
```

各自继承宽度：`DynamicIslandContentView.smali:1470-1500`（`inheritViewWidth` + `DynamicIslandContentView$inheritWidth$3/$4`）。

### 5.1 `getAreaLeftCollapseTransX()` / `getAreaRightCollapseTransX()`

`IslandCoordinateCalculator.smali:43`

```java
public final float getAreaLeftCollapseTransX() {
    boolean rtl = CommonUtils.isLayoutRtl(ctx);
    float w = 0;
    FrameLayout areaLeft = view.getBigIslandAreaLeft();
    if (areaLeft != null) w = areaLeft.getWidth();
    return rtl ? -w : w;
}
```

### 5.2 **致命发现：它不移动岛本体**

调用点仅 3 个，全在 `IslandAnimStateFactory`：
- `:986` → `createCutoutProperties()`
- `:1600` → `createHiddenProperties()`
- `:2073` → `createSmallIslandProperties()`

结果存入 `v18` → `IslandAnimProperties` 第 6 参数 = `bigIslandAreaLeft`。

**唯一消费者**（`IslandPropertyUpdater.smali:283-295`）：

```java
FrameLayout areaLeft = view.getBigIslandAreaLeft();
if (areaLeft != null) {
    areaLeft.setTranslationX(props.getBigIslandAreaLeft());   // 只平移这个空容器
}
```

→ **改这个值只会让一个空白 FrameLayout 左右平移，岛本体纹丝不动。**

---

## 六、结论：方案①不可行

| 候选拦截点 | 调用点 | 是否影响岛位置 | 判定 |
|---|---|---|---|
| `getAreaLeftCollapseTransX()` | 3（Cutout/Hidden/SmallIsland） | ❌ 只平移空容器 | **方案① 否决** |
| `IslandPropertyUpdater.updateContainer()` | 1（动画每帧） | ✅ 但需区分状态、多关键帧 | 可行但复杂 |
| **`IslandCoordinateCalculator.getSmallIslandX()`** | **9** | ✅ **直接决定 containerX** | **★ 推荐** |

### 6.1 `getSmallIslandX()` 全部 9 个调用点

```
IslandAnimStateFactory          :1492  createHiddenProperties
IslandAnimStateFactory          :2015  createSmallIslandProperties
IslandSwipeAnimator             :341
IslandSwipeAnimator             :473
IslandSwipeAnimator             :1077
IslandSwipeAnimator             :1120
IslandGestureAnimator           :588
FakeViewAnimator                :631
DynamicIslandAnimationDelegate  :4603
```

改这一个返回点，全链路生效。

---

## 七、平板分支调查（排除项）

`IslandAnimStateFactory` 中存在两个平板判定：

```java
private final boolean isTableMiddleIsland() {
    return CommonUtils.getIS_TABLET()
        && view.getCurrentIslandData() != null
        && data.getProperties() != null
        && data.getProperties().intValue() == 0;
}

private final boolean isTableSummaryIsland() { ... }
```

**仅影响尺寸/模板，不影响岛的水平 X 位置**（实测 `updateContainer` 中平板分支只设置背景色）。
`CommonUtils.getIS_TABLET()` 在 `updateContainer` 中的另一个用途也是背景色。
**未发现任何 pad/fold/largeScreen 分支影响窗口 x/y/gravity。**

---

## 八、后续实施建议（留给后续工作或子代理）

### 8.1 实现方案

hook `IslandCoordinateCalculator.getSmallIslandX()`：

```java
float result = original();
if (isLandscape() && isSplitShadeActive()) {
    float screenW = getScreenWidthOld(ctx);
    float leftPaneCenter = (qsLeft + qsRight) / 2f;   // 实测 QSContent 范围
    float islandW = smallIslandView.getWidth();
    result = leftPaneCenter - islandW / 2f;           // 绝对坐标，与上面公式一致
}
return result;
```

> 注意：`getSmallIslandX()` 返回的是**绝对左边缘**（见 §3.3 公式），
> 不要减去 `screenW/2`，那是 `getSmallIslandX()` 内部自己做的事。

### 8.2 待实测参数

- QS 左栏实际范围（已知 `QSContent{fe2353d VFE 53,0-1524,1730}`，横屏 3200x1440）
- 岛当前宽度 `getSmallIslandViewWidth()`（`@dimen/small_island_width`）
- 岛期望 Y 位置（QS 托盘顶边 / 状态栏图标带，参考时钟的 topMargin 标定）

### 8.3 必须先解决的前置条件：LSPosed 作用域

```
src/main/resources/META-INF/xposed/scope.list
当前：com.android.systemui
需要：com.android.systemui
      miui.systemui.plugin      ← 必须新增
```

注意 `module.prop` 里 `staticScope=true`，改完 scope 需重新安装模块并重启 `miui.systemui.plugin` 进程。

### 8.4 风险提示

1. **动画关键帧**：`containerX` 参与插值动画，若只改原始返回值，动画中间帧会平滑过渡到新位置（可能是好事，也可能出现飞入效果）。
2. **多状态**：Hidden / SmallIsland / BigIsland / Expanded / Cutout 各有独立 properties，`getSmallIslandX()` 只覆盖其中一部分。
3. **触摸热区**：`DynamicIslandWindow` 的 `mTouchableInsets=3`，热区随 View bounds 走。改 translationX 后热区会跟随（View 变换），但**无法在未实机验证前断言**。
4. **横竖屏切换**：hook 需按 `orientation` 分支，竖屏保持原值。

---

## 九、参考资料

- 插件解包：工作区 `plugin_decode/`（14450 smali）
- SystemUI 解包：工作区 `hyperos4_sysui_decode/`
- 相关子代理：`agent_mwujtjv9_phe1jb`（插件 APK 研究，65 次工具调用，已 completed）
- 前一阶段 SystemUI 侧结论：`agent_mwu7toq5_4q1q6t`（已 completed，结论限于 SystemUI 侧，对插件不适用）

---

## 十、一句话总结

> 超级岛的 X 坐标唯一来源是 `IslandCoordinateCalculator.getSmallIslandX()`；
> 官方虽有三区域容器（`bigIslandAreaLeft/Margin/Right`）和 `getAreaLeftCollapseTransX()`，
> 但后者只平移空白容器、不移动岛本体，**方案① 否决**。
> 正确做法是 hook `getSmallIslandX()`，前置条件是给 LSPosed 作用域加上 `miui.systemui.plugin`。
