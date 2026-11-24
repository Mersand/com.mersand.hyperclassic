# HyperClassic v2.0 发布说明

> 版本：versionCode 20001 / versionName 2.0
> 日期：2026-11-24
> 设备：Redmi K80 Pro（1440x3200，density 600），HyperOS 4 / Android 17
> 定位：在非平板设备上强行启用 AOSP split shade（横屏双栏：左栏 QS、右栏通知），并逐项修补移植缺陷

---

## 一、本版核心成果（v344 → v351）

### 1. 全局触摸：上滑退出控制中心（v344 + v345 + v349）

**问题**：横屏 split shade 下，上滑无法退出控制中心，手势被吞。仅底部一条窄热区有效。

**根因链（三层叠加）**：

| 层 | 文件 | 问题 |
|---|---|---|
| ① | `res/values/dimens.xml` | `split_shade_full_transition_distance = 400dp`；density 600 → **1500px**，而横屏高度仅 **1440px** |
| ② | `NotificationPanelViewController.getMaxPanelTransitionDistance()` | 恒返回 1500 → `isFullyExpanded$1()`（`mExpandedHeight >= 1500`）**永远 false** |
| ③ | `NotificationPanelViewControllerInjector.handleCollapseState(FFF)` | 首行 `if (!isFullyExpandedOnDown) return false;` → 收起判定直接返回 |

补充根因（v345 发现）：
```
handleCollapseState 判定顺序（dy, dx, initialTouchY）：
  ① !isFullyExpandedOnDown           → false
  ② |dy| <= |dx|                     → false
  ③ dy >= 0                          → false
  ④ |dy| <= touchSlop                → false
  ⑤ touchY > panelH - hotZone - pad  → true   ← 底部热区（唯一能过）
  ⑥ qsController.getExpanded()       → false  ← ★ split 下恒 true，堵死后面所有安全网
  ⑦ touchHeaderArea                  → true
  ⑧ nss.isScrolledToBottom()         → true
  ⑨ nss.isBelowLastNotification(x,y) → true
  ⑩ false
```

**修复（三层）**：

```
[v344] hook getMaxPanelTransitionDistance()
       return min(orig, getMaxPanelHeight$1())
       → 1500 钳到 1440，mExpandedFraction 从 0.96 变 1.0，isFullyExpandedOnDown 正确为 true
       → 副作用：6 个消费点全部是比例换算，返回值变小只让 fraction 更准

[v345] hook handleCollapseState()
       跳过 ⑥ qsController.getExpanded()（split 下永真的单栏遗留条件）
       保留 ⑦⑧⑨ 三张安全网（header / 列表滚到底 / 最后一条通知下方）

[v349] 在 ⑧⑨ 之后追加 ⑨b：
       触摸点横向不在 NSSL [getLeft, getRight] 内 → 直接放行
       （左栏 QS 区 x<1612 没有可滚通知，官方安全网会误挡）
```

**实测验证（3200x1440 横屏）**：
```
左栏上部  (400, 400)  → 1.0 → 0.0  ✅
左栏中部  (400, 900)  → 1.0 → 0.0  ✅（v349 新增）
左栏底部  (400,1400)  → 1.0 → 0.0  ✅
右栏列表滚到底后上滑   → 1.0 → 0.0  ✅
右栏有通知可滚时上滑   → 优先滚动列表（官方设计，正确）
```

**开关**：`debug.hyperclassic.local.collapse_fix`（默认 1）

---

### 2. 通知清除按钮（v350 + v351）

**问题**：清除按钮完全不显示；修好显示后又点不动。

**根因（两阶段）**：

**阶段一 —— 不显示（v350）**：
```
实测 dumpsys（v349，横屏展开态）：
  CircleAndTickAnimView{#7f0b0866 notification_dismiss_view
                        VFED..C.. ......I. 2307,1208-2498,1399}
  它的 parent:
  android.widget.FrameLayout{... V.E...... 0,0-1577,1440}   ← 只有 1577 宽
```
- 按钮 visibility=VISIBLE、尺寸正常 191x191、clickable=true
- 但 **x=2307 远超 parent 的 1577**，被边界裁掉
- 根因：v290 为了「状态栏内容左对齐」，把 `shade_header_container` 从整屏 3200 收窄到 QS 半屏 1577，**连带裁掉了它内部的 dismiss 按钮**

修复：
```java
// dismiss 的 parent FrameLayout 撑到全屏宽（它只有 dismiss 一个子项，无副作用）
pp.width = r.getWidth();   // 3200
pp.gravity = Gravity.TOP | Gravity.LEFT;
// parent 与 shade_header 都允许溢出绘制
setClipChildren(false); setClipToPadding(false);
```
> `shade_header_container` 宽度**不变**（状态栏左对齐依赖它），只让 dismiss 这一条链路突破裁剪。

**阶段二 —— 点不动（v351）**：
```
修好后实测：
  CircleAndTickAnimView{... VFED..C.. ........ 2307,1208-2498,1399}   ← 已 VISIBLE
  android.widget.FrameLayout{... 0,0-3200,1440}                        ← 已全屏宽
但点击 (2402,1303) 无任何反应。
```

三层独立拦截：

| 层 | 位置 | 行为 |
|---|---|---|
| ① | `ViewGroup` 命中测试 | `ShadeHeaderContainer` 仍只有 1577 宽（不能回退），触摸点不在其 bounds → `isTransformedTouchPointInView` 返回 false → 事件根本不会分发到 dismiss |
| ② | `NotificationPanelViewControllerInjector.isInDismissViewBounds(FF)` | 内部 `if (orientation != PORTRAIT) threshold = 0`，横屏判 `y < 0` **恒 false** → 官方横屏根本没给 dismiss 触摸支持 |
| ③ | `NotificationShadeWindowView:1148` | `if (!isInDismissViewBounds(x,y)) allowParentInterceptSwitchEvent = true` → 面板抢走事件 |

修复思路：**完全绕开命中测试，自己做点按识别**。

hook `NotificationShadeWindowView.dispatchTouchEvent`（窗口根第一入口，比任何拦截层都早）：

```java
ACTION_DOWN：用 getLocationInWindow 算按钮的根坐标矩形，命中则记 dismissTapDown=true 并消费事件
ACTION_UP  ：若 dismissTapDown 且仍在矩形内 → btn.performClick()，消费事件
CANCEL     ：清标志，若之前认领过则消费
其它（MOVE）：若 dismissTapDown 则继续消费（避免漏到底部空白区）
```

**实测验证**：
```
HCShadeLayout: dismiss DOWN hit rect=2307,1208-2498,1399 touch=2402,1303
HCShadeLayout: dismiss UP -> performClick
→ 通知全部清除，面板自动收起（mExpandedFraction 1.0 → 0.0）
```

---

### 3. 时钟反色锁定白色（v350）

**问题**：左栏顶部时钟「晚上 9:18」显示为暗灰色，在深色托盘上几乎看不清。

**根因**：`MiuiClock.updateLightDarkTint$2()` 最后一行**无条件** `setTextColor(mNonAdaptedColor)`：

```java
// MiuiClock.smali:229  onDarkChanged(ArrayList, float, int)
//   → 存 mAreas / mDarkIntensity / mTint
//   → updateLightDarkTint$2(...)

// MiuiClock.smali:422  updateLightDarkTint$2(...)
if (mUseTint) mNonAdaptedColor = DarkIconDispatcher.getTint(...)
else {
    float d = DarkIconDispatcherExt.getDarkIntensity(...)
    if (d > 0) mNonAdaptedColor = mDarkColor;   // ← 走这里
    else       mNonAdaptedColor = mLightColor;
}
setTextColor(mNonAdaptedColor);                 // ★ 无条件覆盖
```

只要状态栏反色状态一变（下拉/收起/切应用），`DarkIconDispatcher` 就重新推 `onDarkChanged` → 覆盖我们的白色。这正是「反色跟随延迟，重启控制中心后正常」的本质。

**修复**：hook `MiuiClock.updateLightDarkTint$2`，`chain.proceed()` 后对**注入实例**强制 `setTextColor(0xFFFFFFFF)`：

```java
if (self != null && self == injectedClock && self instanceof TextView) {
    ((TextView) self).setTextColor(0xFFFFFFFF);
}
```
> 只处理我们注入的实例（引用比对），不碰官方时钟。

**实测**：日志 `clock white lock applied #0/#1/#2`，截图确认时钟为纯白。

---

## 二、前序版本成果回顾（v321 → v343）

| 版本 | 成果 |
|---|---|
| v321 | 修复 77 个 `illegal character`（根因是注释里写 `/*LANDSCAPE*/` 导致 Java 块注释提前闭合） |
| v330–v331 | **删除 `SplitRowAlphaGuard`（约 305 行）**——它 hook `StackScrollAlgorithm.updateAlphaState` 强行写通知行 alpha，是「通知淡入失效」的唯一原因。层数 12/12 → 11/12，通知淡入恢复，竖屏残留、HUN 带出堆叠一并修好 |
| v332 | 证伪 `calculateQsNotificationsTopPadding`（6 个调用点，2 个在 TouchHandler 是手势阈值输入，污染后横屏无法下拉） |
| v334 | `NSSL.updateTopPadding(FZ)V` 成功——全模块仅 1 个调用点，`AmbientState.mTopPadding` 的 15 个读取点全在布局方法，无一在 TouchHandler |
| v335–v337 | 像素互相关标定：`extra=0` → shift=-6.0（MSE 6.08）；`extra=4` → shift=0.0；`extra=6` → +2.0（过冲） |
| v338 | 新建 `SplitClockInject.java`（约 560 行）：hook `FrameLayout.onLayout` + `QSContent` 实例过滤，`new MiuiClock(ctx, null)` + `setClockMode(2)` + `setTextAppearance(QuickSettingDate)` |
| v339–v343 | `topMargin` 71 → 78（数值中心对齐 ≠ 视觉重心对齐，中文+数字字形 ascent 偏高） |

---

## 三、已证伪的技术路线（不要再试）

| 路线 | 证伪结论 |
|---|---|
| `sncTransitionAlpha` | 5 路离散状态量，4 秒手势中只求值 2 次 |
| `mCurrentPanelAlpha` | 恒 255 |
| `blurRatioAlpha` | MiSelf 分支硬写 1.0 |
| `StackScrollAlgorithm.updateAlphaState` | 根本不给普通通知行设 alpha；强行写会破坏淡入 |
| `NPVCInjector.calculateQsNotificationsTopPadding` | 污染 TouchHandler 手势阈值，导致横屏无法下拉 |
| `large_screen_shade_header.xml` 的 clock | height=0.0dp，且 `config_use_large_screen_shade_header` 全 APK 只有一处 `false` 定义、无 values-land 覆盖 |
| `MiuiQSHeaderView` 的 `horizontal_date_time` | 官方横屏路径因整个 header 是 INVISIBLE 而失效 |
| `getAreaLeftCollapseTransX()` | 只平移 `bigIslandAreaLeft` 这个空 FrameLayout，不移动超级岛本体 |

---

## 四、技术教训

### 像素测量两次踩坑
1. **托盘「左边缘」用亮像素扫描会被圆角干扰**——得到的 142 是圆角内切位置而非布局边界，导致误判 58px 偏差。
2. **数值中心对齐 ≠ 视觉重心对齐**——时钟文字含中文（「晚上」）+ 数字，字形 ascent 偏高，数值上 106.5 vs 108 只差 1.5px 但观感偏上。

### padding / margin 不 1:1 生效
**topPadding 加 N 不等于视觉下移 N**，堆叠算法会吃掉一部分增量，必须靠像素实测标定。

### 日志限流陷阱
`wideHits` 只在 `noteWide()` 里递增，若 hook 未命中则永远不会增加，导致「入口探针」被静默吞掉。必须用**独立计数器**（`wideEnter`）做入口探针。

### ViewGroup 裁剪 vs 触摸分发
- `clipChildren=false` 只影响**绘制**，不影响**命中测试**。
- 命中测试依据是 `isTransformedTouchPointInView`，判定基准是子 View 自身 bounds，但**前提是事件已经分发到该父容器**。
- 父容器 bounds 外的点永远不会被子 View 收到——这就是「画出来了但点不动」的原因。

---

## 五、当前开关一览

| 属性 | 默认 | 作用 |
|---|---|---|
| `debug.hyperclassic.cc_landscape` | 1 | 横屏 split 总开关 |
| `debug.hyperclassic.split_chrome` | 1 | split 布局桥（header/footer 重排）总开关 |
| `debug.hyperclassic.local.collapse_fix` | 1 | **v344** 上滑退出修复 |
| `debug.hyperclassic.local.inject_clock` | 1 | **v338** 时钟注入 |
| `debug.hyperclassic.local.clock_top_margin` | 78 | **v343** 时钟上边距（px） |
| `debug.hyperclassic.local.clock_pad_start` | 15 | **v342** 时钟左边距（px） |
| `debug.hyperclassic.local.top_padding_align` | 1 | **v334** 右栏顶部 padding 对齐 |
| `debug.hyperclassic.local.top_padding_extra` | 4 | **v337** padding 补偿值（像素标定） |
| `debug.hyperclassic.local.touch_block` | 1 | 横屏通知列表拖拽修复（**不可关闭**） |
| `debug.hyperclassic.local.classic_alpha` | 1 | 通知淡入修正 |
| `debug.hyperclassic.local.clip_ratio` | 1 | header 裁剪旁路 |

---

## 六、待处理

1. **超级岛左移**：研究结论已存档 `docs/ISLAND_RELOCATION_RESEARCH_20261124.md`，正确切入点是 hook `IslandCoordinateCalculator.getSmallIslandX()`，前置条件是给 LSPosed 作用域加 `miui.systemui.plugin`。二期研究由子代理 `agent_mwumep24_hmkyjj` 继续。
2. **竖屏偶发完整 QS 托盘残留**：v336 已加 `clearLandscapeResidue`，需实测确认是否还会复现。
3. **左栏个别位置（如 x=200,y=900）触摸未走到 `handleCollapseState`**：可能被磁贴/亮度条等子 View 消费，属正常优先级，如需全覆盖需进一步处理。

---

## 七、相关文件

| 文件 | 说明 |
|---|---|
| `SplitSncTransitionAlpha.java` | L2–L6 层：淡入/触摸阻断/裁剪旁路/padding 对齐/**上滑退出修复** |
| `SplitClockInject.java` | L7 层：横屏时钟注入 + **白锁** |
| `SplitLayoutBridge.java` | split 布局桥：header/footer 重排 + shade_header 收窄 + **dismiss 显示/触摸** |
| `SplitRotationReset.java` | 旋屏状态机重置 |
| `docs/ISLAND_RELOCATION_RESEARCH_20261124.md` | 超级岛研究报告（335 行） |
| `docs/SPLIT_ROW_ALPHA_REMOVAL_20261124.md` | SplitRowAlphaGuard 删除报告（154 行） |
