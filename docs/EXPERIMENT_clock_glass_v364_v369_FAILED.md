# 竖屏时钟玻璃化：失败路径记录（v364–v369）

> 日期：2026-11-25
> 状态：**实验失败，未提交代码**（仅保留结论供后来者参考）
> 相关实现：`PortraitClockStyle.java`（字体部分已成功，玻璃部分未启用）

---

## 一、目标

给竖屏控制中心的通知大时钟（`@id/big_time`）做**玻璃化**，
同时允许替换字体。

字体部分**已成功**（见文末「四、已成功部分」），
本文只记录**玻璃化**的失败过程。

---

## 二、目标对象溯源

### 2.1 两个 `big_time`

SystemUI 里有**三个**布局都含 `@id/big_time`：

| 布局 | 宿主类 | 说明 |
|---|---|---|
| `miui_ns_qs_header_view.xml` | `MiuiQSHeaderView` | ★ **竖屏可见的 44dp 大时钟** |
| `miui_ns_notification_header_view.xml` | `MiuiNotificationHeaderView` | `MiuiNotificationHeaderClock`，竖屏不显示 |
| `control_center_date_view.xml` | `ControlCenterDateView` | 横屏用 |

**踩坑**：v360 第一版按「宿主类」找时钟（hook `MiuiNotificationHeaderView`
再反射 `mBigTime`），日志显示 `font applied` 成功但截图零变化——
因为改的是不显示的那一个。**教训：按 View 的 id 名匹配，不认宿主类。**

### 2.2 字体为什么锁死

`MiuiClock.<init>` 里：

```smali
sget-object p2, Lcom/miui/utils/configs/MiuiConfigs;->sMiproTypefaceWght500
invoke-static {p2, p0}, Lcom/miui/utils/configs/MiuiConfigs;->applyStatusBarTypeface(...)
```

而 `applyStatusBarTypeface` 内部还会：
```
Typeface.create("mipro-medium", 0)   ← 把传进来的顶掉
```
⇒ 换主题字体/系统字体对它统统无效，这就是「有自己独立资源」的真相。

`MiuiNotificationHeaderView.onFinishInflate` 里给 `date_time` 和
`horizontal_time` 都重新设了字体，**唯独 `big_time` 什么也没做**。

---

## 三、玻璃化：三条路全部失败

### 3.1 核心矛盾（主人一语中的）

> 「有没有可能文字本身的纯白也会覆盖掉玻璃？」

完全正确。`MiGlassCompat.setMiGlassCompat(view, params)` 挂的是
**View 后置材质层**，而 `TextView.drawText` 把不透明颜色**直接填**在字形上，
这层填充位于材质层**之上** → 材质被整个盖住。

### 3.2 路径 ①：只挂 View 级材质 —— 看不见

```
setMiGlassCompat(clock, params)
setMiViewMaterialTypeCompat(1, clock)
```
日志证明材质确实挂上了（`glass applied #1 357x199 blur=48 len=42`），
但视觉零变化 —— 就是被纯白字形盖住了。

### 3.3 路径 ②：只开 Paint 级玻璃 —— 黄色块

参考小米官方玻璃时钟 `com.miui.clock.allInOne.TimeView` 的实现：

```java
paint.getTextPath(text, 0, len, 0, 0, mTextPath);   // 字形转 Path
MiuiBlurUtils.simplifyPath(mTextPath, paint);        // 简化轮廓
M_SET_GLASS_EFFECT.invoke(paint, isGlass);           // Paint 开玻璃
canvas.drawPath(mTextPath, paint);                   // 画 Path
```

`MiuiBlurUtils` 里三个 public static 字段（已实证可用）：

| 字段 | 实际方法 |
|---|---|
| `M_SET_GLASS_EFFECT` | `Paint.setGlassEffect(boolean)` |
| `M_SIMPLIFY_PATH` | `Paint.simplifyPath(Path)` |
| `M_SET_ATLAS_MAX_PATH_SIZE` | `Paint.setAtlasMaxPathSize(int,int)` |

实测：`paint glass apis: setGlassEffect=true simplifyPath=true atlas=true`

**但结果是把字形渲染成一块黄色。**
分离测试证明 Path 完全正确（`clock_glass_paint_only=1` 时「18:15」清晰准确），
所以问题在 `setGlassEffect(true)` 本身。

途中修掉的两个真实 bug：
- `setAtlasMaxPathSize` 两个参数必须**相同**（官方 `TimeView.setGpuOptimize`
  实证：`z ? 0x800 : 0x1`，两参数永远同一个值）。传宽高会让 atlas 格子
  非方形 → SDF 采样错位。
- 必须用 `new Paint(tv.getPaint())` **副本**，不能改 TextView 自己的 Paint。
  HyperLight `eb1.f` 第 1499 行就是 `new Paint(tv.getPaint())`。

### 3.4 路径 ③：两层叠加 —— 还是黄色块

同时开 View 材质 + Paint 玻璃，并补上 HyperLight 用到的两个 View 级调用：
- `setMiViewBlurModeCompat(1, view)`
- `View.setMiCustomSurfaceColorType(int)`

还补了本项目通知玻璃用的采样源：
- `setPassWindowBlurEnabledCompat(view, true)`
- `setMiBlurWinExc(view, "NotificationShade")`

结果：字形轮廓出来了，但底下仍拖着黄色块。

### 3.5 结论：那个黄色是 **SDF atlas 距离场纹理本身**

`setGlassEffect(true)` 让字形进入玻璃着色器，玻璃需要**采样源**
（身后背景的模糊纹理）。拿不到采样源时，着色器把内部距离场原样输出
—— 就是那块黄色。

**为什么 HyperLight 能跑而我们不能**：它针对**新版控制中心**，
那里到处都有玻璃背景，采样源天然存在；
竖屏 QS 顶部大时钟的位置背后是**纯壁纸**，没有可采样的玻璃层。

⇒ 纯粹的「字形玻璃」在当前位置**不具备渲染条件**。
继续硬攻需要深入 `RenderEffect` / `SurfaceControl` 层，成本高、成功率不确定。

---

## 四、已成功部分（保留可用）

### 4.1 字体替换 ✅

- 挂点：`MiuiConfigs.applyStatusBarTypeface`（唯一字体下发出口）
- 方法：proceed 之后把我们的 Typeface 再盖回去
- 支持：内置 5 种字体 / 自定义字体文件 / `wght` 可变轴加粗 / Paint 假粗
- 字体缓存 `TF_CACHE` 必须存在（不缓存会因每次 `build()` 返回新引用而反复 set）

### 4.2 热调即时生效 ✅

- 挂点：`TextView.onDraw`（250ms 节流），下拉面板必然触发
- 每帧 Paint 断言（只用 volatile 缓存值，极廉价）修复
  「时间更新后变回默认字体」

### 4.3 踩过的坑（已修）

| 现象 | 根因 | 修法 |
|---|---|---|
| 日志刷 227 次/0.5s | `Typeface.Builder.build()` 每次返回新引用，幂等判断失效 | `TF_CACHE` 缓存 |
| 时钟上屏一次触发 4 次 apply | `onAttachedToWindow` 内四条独立路径各命中一次，**不是死循环** | 按「配置签名」去重（不用时间节流） |
| 加粗影响下面的日期 | 每帧 Paint 断言放在 `isOurClock()` 之前，而 `date_time` 也是 `MiuiClock` | 断言只对 `TAG_MARK` 已确认的时钟执行 |
| 字形边缘有毛刺 | 描边默认 `Join.MITER`，转折处伸出尖刺 | `setStrokeJoin(ROUND)` + `setStrokeCap(ROUND)` + `setAntiAlias(true)` |

---

## 五、下一步方向（主人提出）

**整条路径替换**，而不是改造官方时钟：

1. 官方 `big_time` 设为 `INVISIBLE` 占位（不动布局）
2. 在同一父容器同一位置插入自己的 View
3. 时间自取自绘
4. 玻璃底板用 **View 级材质**——本项目通知玻璃已实证这条路径能出货
   （`CcHooks.applyUnifiedGlass` / `GLASS_PARAMS_FALLBACK`）

这个方案的可行性显著更高：View 级玻璃对「有形状的普通 View」
是有效的，不像文字那样被填充盖住。
