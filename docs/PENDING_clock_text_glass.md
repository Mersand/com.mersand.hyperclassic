# 竖屏时钟「文字笔画玻璃化」— 待研究（PENDING）

> 日期：2026-11-25
> 状态：**待研究，代码已撤销**
> 前序：`EXPERIMENT_clock_glass_v364_v369_FAILED.md`（六轮 View/Paint 级实验失败记录）

---

## 一、结论速览

**目标**：竖屏 QS 顶部大时钟，让**文字笔画本身**呈现 MiGlass 玻璃质感。

**当前结果**：字形位置/描边/裁剪区全部正确，但输出是一块
**尺寸等于整个 View（391×198）的黄色矩形** —— 即 View 材质层的 SDF 距离场纹理。

**根因判断**：玻璃着色器缺少**可采样的背景层**（采样源）。
没有采样源时，玻璃管线把内部的 SDF atlas 原样输出 → 黄色。

**已撤销**：`ClockReplacement.java`、`PaintGlassProbe.java`、`CcHooks` 的 L11 接入、
`applyGlassForClock` 桥方法。
**保留**：`PortraitClockStyle.java` 的字体管线（已实机验收通过）。

---

## 二、重大进展（本轮 v370~v377 实证）

### 2.1 视图替换方案完全成功 ★

这是本轮最有价值的成果，**与玻璃无关、可独立复用**。

**官方布局结构**（`res/layout/miui_ns_qs_header_view.xml`）：

```
MiuiQSHeaderView  @id/header_content
  └ LinearLayout  @id/qs_header_vertical_info   高 336px（受上层约束）
      └ LinearLayout（竖向，只装两个 View）
          ├ MiuiClock @id/big_time   height=wrap_content  ← 撑起父高 ≈198px
          └ MiuiClock @id/date_time  height=match_parent  ← 吃掉剩余 ≈138px
```

**四次尝试的教训（前三次全部失败）**：

| # | 方案 | 结果 | 原因 |
|---|---|---|---|
| ① | `big_time` 设 GONE + 自建 View 插父容器 | 日期消失 | 父容器失去高度锚点，`date_time` 的 `match_parent` 解析成 0 |
| ② | GONE + 自算高度（276px） | 日期消失 | 自建 View 带 padding 比官方高 78px，把日期挤出可见区 |
| ③ | INVISIBLE + 兄弟 View（198px） | 日期消失 | 官方占 198 + 我们占 198 = 396px > 父容器 336px |
| ④ | **FrameLayout 原地包装** | **✅ 成功** | 父容器子 View 数量、位置、LayoutParams 全不变 |

**正解代码结构**：

```java
// 1. 建 wrapper，沿用官方时钟的 LayoutParams 对象本身
FrameLayout wrapper = new FrameLayout(ctx);
ViewGroup.LayoutParams clockLp = clock.getLayoutParams();
int idx = parent.indexOfChild(clock);

// 2. 官方时钟从父容器摘下，放进 wrapper（保留测量作用）
parent.removeViewAt(idx);
wrapper.addView(clock, new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

// 3. 我们的 View 以 MATCH_PARENT 叠在 wrapper 里
wrapper.addView(ours, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));

// 4. wrapper 放回原位置（LayoutParams 一模一样）
parent.addView(wrapper, idx, clockLp);

// 5. 官方时钟 INVISIBLE（不是 GONE —— GONE 会让 wrapper 的 wrap_content 失效）
clock.setVisibility(View.INVISIBLE);
```

**实测日志**：
```
replaced big_time #2 parent=LinearLayout idx=0 clockLp=-2x-2
```
**实测结果**：时钟与日期均正常显示，布局零影响。

### 2.2 完整摸清官方玻璃文字流程 ★

`com.miui.clock.allInOne.TimeView.onDraw`（smali 行 480~890）的玻璃分支：

```
① textPaint.setGlassEffect(true)                                行 499-521
② textPaint.setTextSize(textSizePx)                             行 535-539（在 ① 之后）
③ textPaint.getTextPath(mText, 0, len, 0, 0, mTextPath)         行 541-557
④ MiuiBlurUtils.simplifyPath(mTextPath, textPaint)              行 578-582
⑤ mTextPath.computeBounds(mTextBounds, true)                    行 585-591
⑥ rect.offset(mDrawTextX, mDrawTextY)                           行 703-707
⑦ 四边各外扩 50px（硬编码 const/high16 0x42480000 = 50.0f）      行 709-725
⑧ View.setMiGlassClip(l-50, t-50, r+50, b+50)                   行 727-755
⑨ MiuiBlurUtils.setMiGlass(view, glassData)                     行 769-771
⑩ textPaint.setStrokeWidth(glassData[19])                       行 774-782
⑪ textPaint.setStrokeMiter(glassData[20])                       行 784-792
⑫ textPaint.setAlpha(fontAlpha * 255)                           行 866-874
⑬ canvas.translate(mDrawTextX, mDrawTextY)                      行 876-880
⑭ canvas.drawPath(mTextPath, textPaint)                         行 882-886
```

另外，`AllInOneBase` 还给每个 `TimeView` 调了：
```smali
MiuiBlurUtils.setGlassEffectMethod(TimeView, glassData)
MiuiBlurUtils.chooseBackgroundBlurContainer(TimeView, 背景模糊容器View)  ← ★ 未实现
```

### 2.3 `MiuiBlurUtils.setGlassEffectMethod(View, float[])` 的四步 ★★

位置：`smali_classes3/com/miui/clock/utils/MiuiBlurUtils.smali` 行 1094~1270

```smali
① View.setMiViewMaterialType(1)           ← 打开 View 材质
② setMiGlass(view, glassData)             ← 42 项配方
③ setMiViewBlurMode(3, view)              ← 模糊模式 3（1=磨砂，3=玻璃）
④ View.setMiCustomSurfaceColorType(16)    ← surface 颜色类型 16（玻璃专用）
```

**本轮重大发现**：③ 和 ④ 是玻璃系统的核心开关！
补上这两个值后，**通知卡片立刻变成漂亮的彩虹玻璃**（实机截图确认）。
这说明 API 找对了，方向没错。

### 2.4 官方 42 项玻璃配方（`TimeView:array_0` 原值）

```java
0.05f, 0.35f, 0.5f,  0.55f, 1.0f, 2.0f, 0.3f, 0.0f,   // [0..7]
0.0f,  1.0f,  0.05f, 1.0f,  1.0f, 1.0f, 0.4f, 0.8f,   // [8..15]
0.0f,  1.1f,  1.0f,  30.0f, 2.0f, 200.0f, 400.0f, 0.3f, // [16..23]
2.0f, -2.0f,  2.0f, -1.0f,  6.0f, 3.0f, 0.3f, 1.176f,  // [24..31]
1.33f, 1.0f,  1.0f,  1.0f,  0.0f, 0.8f, 0.82f, 0.0f,   // [32..39]
0.0f,  0.0f                                             // [40..41]
```

**索引常量**（`TimeView` 构造 `<init>` 实证）：

| 字段 | 值 | 用途 |
|---|---|---|
| `GLASS_SATURATION_INDEX` | 5 | 饱和度 |
| `GLASS_BRIGHTNESS_INDEX` | 6 | 亮度 |
| `GLASS_COLOR_R_INDEX` | 11 | 颜色 R |
| `GLASS_COLOR_G_INDEX` | 12 | 颜色 G |
| `GLASS_COLOR_B_INDEX` | 13 | 颜色 B |
| `GLASS_COLOR_A_INDEX` | 14 | 颜色 A |
| `GLASS_COLOR_MIX_INDEX` | 16 | 混色 |
| `GLASS_COLOR_POW_INDEX` | 17 | 颜色强度 |
| **`GLASS_EDGE_PX_INDEX`** | **19** | **→ Paint.setStrokeWidth（30.0）** |
| **`GLASS_EDGE_POW_INDEX`** | **20** | **→ Paint.setStrokeMiter（2.0）** |

### 2.5 可用的 `MiuiBlurUtils` API 全清单

路径：`com.miui.clock.utils.MiuiBlurUtils`（**必须用 SystemUI 的 ClassLoader**）

**public static 字段（Method，官方已 resolve）**：
```
M_SET_GLASS_EFFECT               Paint.setGlassEffect(boolean)
M_SIMPLIFY_PATH                  Paint.simplifyPath(Path)
M_SET_ATLAS_MAX_PATH_SIZE        Paint.setAtlasMaxPathSize(int, int)
M_SET_MI_GLASS_CLIP              View.setMiGlassClip(float, float, float, float)
M_SET_MI_GLASS                   View.setMiGlass(float[])
M_SET_MI_VIEW_MATERIAL_TYPE      View.setMiViewMaterialType(int)
M_SET_MI_CUSTOM_SURFACE_COLOR_TYPE  View.setMiCustomSurfaceColorType(int)
M_SET_MI_GLASS_BLUR_RADIUS       View.setMiGlassBlurRadius(int, int)
M_SET_MI_VIEW_BLUR_MODE          View.setMiViewBlurMode(int)
M_SET_MI_BACKGROUND_BLUR_MODE    View.setMiBackgroundBlurMode(int)
M_SET_MI_BACKGROUND_BLUR_RADIUS  View.setMiBackgroundBlurRadius(int)
M_SET_PASS_WINDOW_BLUR_ENABLED   View.setPassWindowBlurEnabled(boolean)
M_CHOOSE_BACKGROUND_BLUR_CONTAINER   View.setBackgroundBlurContainer(View)  ← 未用
M_SET_PASS_TEXTURE_SCALE         View.setPassTextureScale(float)
M_SET_MI_SELF_BLUR_TYPE          View.setMiSelfBlurType(int)
M_SET_SELF_GRADIENT_BLUR_PARAMS  View.setSelfGradientBlurParams(float[], int)
M_SET_GLOW_EFFECT_8              View.setGlowEffect(int,int,int,float,float,float,int,int)
M_ADD_MI_BACKGROUND_BLEND_COLOR  View.addMiBackgroundBlendColor(int, int)
M_CLEAR_MI_BACKGROUND_BLEND_COLOR
M_SET_MI_BACKGROUND_BLEND_COLORS
M_DISABLE_MI_BACKGROUND_CONTAIN_BELOW
```

**public static 方法（可直接调）**：
```
setGlassEffectMethod(View, float[])            ← 官方四步封装
chooseBackgroundBlurContainer(View, View)      ← ★ 采样源（未实现）
clearContainerPassBlur(View)
setContainerPassBlur(View, int, boolean)
setMiGlass(View, float[])
setMiGlassBlurRadius(View, int, int)
setMiViewBlurMode(int, View)
setPassWindowBlurEnabled(View, boolean) → boolean
setPassTextureScale(float, View) → boolean
setMiBackgroundBlurMode(int, View)
setMiBackgroundBlurRadius(int, View)
simplifyPath(Path, Paint)
setMemberBlendColor / setMemberBlendColors
setMemberDiffEffectColor
setGlowEffectMethod / setGradientBlurMethod
setDiffEffectContainer / clearDiffEffectContainer
```

---

## 三、为什么仍然是黄块（本轮最终判断）

### 3.1 已排除的原因

| 假设 | 是否成立 | 依据 |
|---|---|---|
| Path 生成错误 | ❌ 排除 | 分离测试（不开玻璃）时字形清晰、位置准确 |
| `setAtlasMaxPathSize` 参数错 | ❌ 排除 | 两参数已统一为 2048 |
| 用了 TextView 自己的 Paint | ❌ 排除 | 已用 `new Paint(basePaint)` 副本 |
| 缺 `setMiGlassClip` | ❌ 排除 | 已按官方 ±50px 外扩传入 |
| 缺 `setMiGlass(glassData)` | ❌ 排除 | 已传官方 42 项原值 |
| 缺 `setStrokeWidth/Miter` | ❌ 排除 | 已传 glassData[19]=30 / [20]=2 |
| View 材质=1 导致整块输出 | ❌ 排除 | 设为 0 后黄块**仍然存在** |
| 缺 passWindowBlur / winExc | ❌ 排除 | 均已补上，黄块不变 |

### 3.2 当前判断

**黄块尺寸 = 整个 View（391×198）而不是字形范围**，这一点很关键：
说明输出的是 **View 材质层的 SDF atlas**，而不是「字形玻璃没渲染出来」。

玻璃管线在「有材质、无采样源」时的行为就是输出内部 SDF 纹理。
竖屏 QS 顶部大时钟背后是**纯壁纸**，没有可采样的玻璃层 ——
这与 `EXPERIMENT_clock_glass_v364_v369_FAILED.md` 里的结论一致。

HyperLight 等模块能出效果，是因为它们作用于**新版控制中心**，
那里到处都有玻璃背景，采样源天然存在。

---

## 四、若继续研究，下一步可尝试的方向

按成本从低到高：

1. **`chooseBackgroundBlurContainer`**（成本低，未试）
   ```java
   invoke(M_CHOOSE_BACKGROUND_BLUR_CONTAINER, ourView, blurContainerView);
   ```
   需要找到合适的采样容器。候选（实测 UI 树里存在）：
   - `com.android.systemui:id/blur_provider`（NotificationShadeWindowView 下）
   - `NotificationShadeWindowView` 本身
   - 父级 `MiuiQSHeaderView` 或 `qs_header_vertical_info`
   官方 `AllInOneBase.setBackgroundBlurContainer(View p1)` 是由系统上层传入的，
   需要往上追 `MiuiBaseClock2` 的调用方才能知道确切对象。

2. **反向验证：先让 View 材质层正常显示玻璃**（成本中）
   把我们的 View 做成圆角矩形底板（v370 曾成功出货，截图确认），
   然后再开 Paint 玻璃 —— 此时采样源就是我们自己的底板。
   但这已不是「纯文字玻璃」，属于折中方案。

3. **`setPassTextureScale` + `setMiSelfBlurType`**（成本中，未试）
   官方 `M_SET_PASS_TEXTURE_SCALE` 与 `M_SET_MI_SELF_BLUR_TYPE` 可能影响
   采样纹理缩放与自模糊类型。

4. **hook `TimeView` 真实使用场景做对照**（成本高，最可靠）
   找一个界面上真实用 `TimeView` 玻璃的页面（如新版控制中心时钟），
   在 `TimeView.onDraw` 里 dump：
   - `getGlassData()` 实际值（可能被上层改过，不是构造默认值）
   - `getParent()` 链上所有 View 的 blur 相关属性
   - 对比 `setBackgroundBlurContainer` 传的是哪个 View
   拿到真实环境参数后照抄。

5. **下沉到 RenderEffect / SurfaceControl 层**（成本最高）
   直接给 Paint 挂原生 `RenderEffect`，绕过 MiGlass 体系。
   需要 Android 17 的 `Paint.setRenderEffect` 或自定义 Shader。

---

## 五、撤销清单（本轮已还原）

| 文件 | 处理 |
|---|---|
| `ClockReplacement.java` | 删除（整条路径替换 + 玻璃） |
| `PaintGlassProbe.java` | 删除（API 探针） |
| `CcHooks.java` L11 接入 | 回退 |
| `CcHooks.applyGlassForClock` 桥方法 | 删除 |
| `PortraitClockStyle.java` 玻璃代码 | 已在 v370 移除 |
| `PortraitClockStyle.java` 字体管线 | **保留**（已实机验收通过） |

---

## 六、可复用的资产（后续研究直接拿）

1. `docs/EXPERIMENT_clock_glass_v364_v369_FAILED.md` —— 六轮失败详细记录
2. 本文档第二节的 API 清单 + 42 项配方 + 索引表
3. 本文档 2.1 节的 **FrameLayout 原地包装** 代码结构（已验证，与玻璃无关）
4. `PortraitClockStyle.java` 的字体管线（内置 5 字体 / 自定义文件 / wght 可变轴 /
   Paint 假粗 / 250ms 节流热调），可作为将来任何时钟改造的字体基座

---

## 七、给后来者的三条建议

1. **先验证采样源，再写代码**。
   如果目标 View 背后没有玻璃层，任何 Paint 级玻璃都只会输出 SDF。
   判断方法：先给一个已知能出玻璃的 View（如通知卡片）加同样代码，
   能出货才说明环境没问题。

2. **`setMiViewBlurMode(3)` + `setMiCustomSurfaceColorType(16)` 是玻璃的开关**。
   这两个值不到，玻璃永远出不来（本轮实证：补上后通知卡片立刻变彩虹玻璃）。

3. **改布局时，父容器的「高度锚点」比 View 本身更重要**。
   `match_parent` 兄弟 View 依赖父容器高度，而父容器高度往往由某个
   `wrap_content` 子 View 撑起。替换那个子 View 时必须保持父容器视角不变 ——
   FrameLayout 原地包装是唯一稳妥的做法。
