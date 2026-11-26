# 文字玻璃化 —— 新一轮研究（Part 2：libhwui 层实证）

> 日期：2026-11-26 凌晨
> 状态：**纯静态调研，未改动任何代码，未重启 SystemUI**
> 前序：`PENDING_clock_text_glass.md`（v364~v377 View/Paint 双路全失败）

---

## 〇、一句话结论

> **Paint 级玻璃有完全独立的 native 渲染器 `MiDrawGlassPath`，它走 Vulkan/GLES 的
> SDF 路径渲染，跟 View 材质层（`MiGlassFilter`）是两套东西。**
> 我们之前一直在用 View 材质的日志（`setMiGlass params not updated`）判断结论，
> 所以「黄块 = 缺采样源」的诊断**可能从一开始就错了方向**。

---

## 一、本轮新发现（全部来自 libhwui.so 实证）

### 1.1 两条完全独立的玻璃渲染通道

`/system/lib64/libhwui.so`（17.4MB）里存在两套毫不相干的实现：

| 通道 | 触发方式 | 着色器 | 载体 | 输出 |
|---|---|---|---|---|
| **A. View 材质玻璃** | `View.setMiViewMaterialType(1)` | `MiGlassFilter` | 矩形（View 背景） | 背景模糊采样 + 折射 |
| **B. Path 描边玻璃** | `Paint.setGlassEffect(true)` | `MiDrawGlassPath` | **任意 Path** | SDF 纹理着色 |

通道 B 的完整符号链：

```
Paint.setGlassEffect(true)
  → Paint.nSetGlassEffect(long, long)        ← native
  → RenderNode.nSetUsageHint(..., SDF_GLASS) ← hwui 内部自动设
  → UsageHint::SDF_GLASS MiDrawGlassPath::Draw
  → MiDrawGlassPath::Draw created SDF texture shader
  → MiDrawGlassPath::Draw runtime effect ready
  → FontSdfVulkanSvg::draw / FontSdfGLSvg::render
```

**关键**：路径 B 需要 `RuntimeShader`（Android 13+ 的 runtime effect），
失败时会有明确日志 `MiDrawGlassPath: Failed to create runtime effect: %s`。

### 1.2 通道 B 的源码位置（已在 .so 里找到）

```
vendor/xiaomi/frameworks/native/libs/libmisvg/misdf/fontsdfvulkan.cpp
vendor/xiaomi/frameworks/native/libs/libmisvg/mananger/memControlManager.cpp
```

这是小米自研的 `libmisvg`（Mi SDF Vector Graphics）库，
**用 Vulkan 做 SDF 路径渲染**，与标准 Skia 的路径光栅化完全并行。

相关着色器文件名：`path_distance.vert` / `path_distance.frag` / `path_interior.frag`

### 1.3 `MiGlassFilter` 的完整 shader 已提取

从 .so 中提取到三段 GLSL（含小米标注 `// MIUI MOD: SF_AllInOneGlass`）：

```glsl
// ① 矩形玻璃（View 材质，单参数）
half4 textureBlur(vec2 uv)

// ② 矩形玻璃（第二版）
half4 textureBlur(vec2 uv)

// ③ 时钟玻璃（★ 双参数，带 isBg 标志）
half4 textureBlur(vec2 uv, bool isBg)
```

**这是本轮最重要的发现**：第三段 shader 的注释和日志明确写着
`clock glass`，说明**系统内存在专门给时钟用的玻璃着色器**。

### 1.4 时钟玻璃 shader 的核心结构

```glsl
half4 renderGlassShape(half2 n2, half boxSdf, vec2 vUv) {
    half edge = half(uShapeEdgePx);           // ← glassData[19] = 30.0
    half oneMinusW = clamp(1.0 - boxSdf, 0.0, 1.0);
    half nmlZ = 1.0 - pow(oneMinusW, 1.0 / uShapeEdgePow);  // ← glassData[20] = 2.0
    half finalAlpha = smoothstep(0.0, uNmlZAAThreshold, nmlZ);
    ...
    if (uIsIcon > 0.5) {
        // ==== 图标模式 ====
        half4 baseColor = uBgColorTex.eval(uv);   // 直接采样，不模糊
        // 走 luminance_curve_lab 提亮
    } else {
        // ==== 标准玻璃模式 ====
        half4 glassMaterial = textureBlur(vUv, true);   // 采样 + 模糊 + 变暗 + hsvv
        glassMaterial = processLighting(glassMaterial, lightNormal);  // 折射光照
        glassMaterial = pow(glassMaterial, vec4(uColorPow));
    }
    result.a *= finalAlpha * half(uAlpha);
}

half4 main(vec2 xy) {
    vec2 localPx = xy - uGeoPxPos;
    vec2 cornerPos = min(localPx, uGeoPxSize.xy - localPx);   // ★ 镜像折叠
    half4 nmlData = uSdfTex.eval(cornerPos);
    half sdf = nmlData.w;
    if (sdf <= 0.) return half4(0.0);      // ★ SDF<=0 才透明
    ...
}
```

**★ 关键洞察**：`uSdfTex` 是**只覆盖四分之一区域**的 SDF 纹理，
靠 `cornerPos = min(localPx, size - localPx)` 做**镜像折叠**复用四个角。
这意味着 SDF 纹理尺寸只有 View 的一半（`uGeoPxSize` 的一半）。

### 1.5 为什么出黄块（新解释）

shader 里 `if (sdf <= 0.) return half4(0.0);` —— **SDF 值 > 0 才画**。

我们输出的黄块**尺寸等于整个 View**，对应 shader 的
`uGeoPxSize` 被设成了 View 尺寸、`uSdfTex` 全区域 SDF > 0。
这更像是 **SDF 纹理内容错误**（不是「缺采样源」），
即 `setMiGlassClip` 传入的几何与 `setMiGlass` 的 SDF 预分配尺寸不匹配，
导致 SDF 纹理被填成「全部在内侧」。

### 1.6 View 材质 vs Path 玻璃的调用矩阵

| API | 作用对象 | 我们要用的 |
|---|---|---|
| `View.setMiViewMaterialType(int)` | 通道 A（矩形） | ❌ 文字不是矩形 |
| `View.setMiGlass(float[])` | 通道 A（42 项配方） | ❌ 同上 |
| `View.setMiGlassClip(l,t,r,b)` | 通道 A（矩形裁剪） | ❌ 同上 |
| `Paint.setGlassEffect(boolean)` | **通道 B（Path）** | ✅ **文字该用这个** |
| `Paint.setAtlasMaxPathSize(w,h)` | **通道 B（SDF atlas）** | ✅ 需配对 |
| `Paint.simplifyPath(Path)` | 通道 B（路径简化） | ✅ 减顶点数 |

**结论**：`View.setMiGlass` 那 42 项参数是给**矩形 View** 的，
`Paint.setGlassEffect` 走的是**另一套 uniform 表**（`uSdfTex`/`uBgColorTex`/`uGeoPx*`）。
我们之前把两套混用（既设 View 材质又设 Paint 玻璃），
导致 View 材质层先画了一个矩形，把 Path 玻璃盖住了 —— **这很可能就是黄块的真相**。

---

## 二、`uIsIcon` 分支的启发（可能是最优解）

shader 里有个 `uIsIcon > 0.5` 的分支，行为完全不同：

```glsl
// uIsIcon = 1（图标模式）
half4 baseColor = uBgColorTex.eval(uv);   // 直接采样背景，不模糊
→ luminance_curve_lab(baseColor, vec4(0.701, 0.700, 1.502, 1.500), boostedMix)
→ 只做「亮度曲线提亮」

// uIsIcon = 0（标准玻璃）
half4 glassMaterial = textureBlur(vUv, true);
→ 完整折射 + 光照 + 色彩幂
```

**`uIsIcon` 对应的 42 项索引待查**（可能是 `glassData` 里的某个开关位）。
如果文字走 `uIsIcon = 1`：
- 不做模糊（文字本来就不需要大范围模糊）
- 只做背景色采样 + 亮度提亮
- **不需要 `blurInput`，不需要窗口模糊环境**

这可能是**文字玻璃化的最短路径** —— 而且完美契合「文字笔画本身的玻璃化」，
因为图标模式天然就是给「小面积、有形状、不需要大模糊」的元素设计的。

---

## 三、修正上一轮的错误结论

| 上一轮的判断 | 本轮修正 |
|---|---|
| 「黄块 = 缺可采样背景层」 | **不准确**。更可能是 View 材质层的矩形 SDF 覆盖了 Path 玻璃 |
| 「Paint 玻璃和 View 材质是同一套」 | **错**。是两套完全独立的渲染器（`MiGlassFilter` vs `MiDrawGlassPath`） |
| 「必须解决 blurInput NULL」 | **不一定**。若走 `uIsIcon=1` 分支则不需要 blurInput |
| 「竖屏 QS 顶部没玻璃层所以不可能」 | **待验证**。Path 玻璃可能根本不需要背景层（`uBgColorTex` 可能来自壁纸快照） |

---

## 四、下一步实验设计（等主人起床后确认）

### 实验 1：纯 Paint 玻璃，完全不碰 View 材质 ★推荐先做

**改动点**：`GlassClockView` 里删掉所有 `View.setMiGlass*` 调用，只保留：
```java
paint.setGlassEffect(true);
paint.setAtlasMaxPathSize(2048, 2048);
paint.simplifyPath(textPath);      // 可选
canvas.drawPath(textPath, paint);
```

**预期**：如果黄块消失 → 证实「View 材质层是元凶」。
如果仍黄块 → 说明 Path 玻璃本身需要额外环境。

**这个实验极其干净，只改 3 行。**

### 实验 2：查 `uIsIcon` 的索引

在 smali 里搜 `uIsIcon` 不对，要搜 `setMiGlass` 的 42 项数组哪个位置
对应 `uIsIcon`。可以从 `MiuiBlurUtils.smali` 里找 setter，
或从别的调用处（通知卡片 vs 图标）比对 42 项差异。

### 实验 3：验证 Path 玻璃是否真的被触发

在 `MiDrawGlassPath_Draw` 附近下 logcat 过滤（native log tag 待确认），
或直接看 `[skia] ** ERROR ** ... fSamplerMask NOT isInitialized` 这类报错。

---

## 五、本轮产出的可复用资产

1. **完整 shader 源码**：`/tmp/glass/clock_shader_0.txt`（16KB，含 `textureBlur(uv, isBg)` 全实现）
2. **libhwui 副本**：`/data/user/0/com.ai.assistance.operit/files/tmp/libhwui.so`（便于后续 strings 分析）
3. **通道对照表**（本文 1.6 节）
4. **`uIsIcon` 分支发现**（可能的最短路径）
5. **源码路径线索**：`vendor/xiaomi/frameworks/native/libs/libmisvg/misdf/fontsdfvulkan.cpp`

---

## 六、傍晚遗留待办

- [ ] 确认 `uIsIcon` 在 42 项数组中的索引
- [ ] 确认 `MiDrawGlassPath` 是否依赖 `uBgColorTex`（若不依赖背景则文字玻璃完全可行）
- [ ] 查 `RenderNode.setUsageHint` 的公开调用方式（是否可由 App 层设置）
- [ ] 查 `Paint.setGlassEffect` 是否需要配合 `setMiGlass`（还是完全独立）

---

## 七、【核心问题回答】改官方字体 vs 自建视图

> 主人 2026-11-26 的提问：
> 「问题还是我们应该改官方的字体成玻璃，还是自建视图来做。
>   同时两者都要面对最核心的怎么处理文字玻璃化的问题。」

### 7.1 结论：**必须自建视图（Paint 级），改官方字体这条路不存在**

#### 证据：官方 `big_time` 根本没有玻璃能力

对照竖屏 QS 使用的类，逐层查玻璃相关代码：

| 类 | 继承 | 玻璃代码引用数 | 有 onDraw？ |
|---|---|---|---|
| `com.android.systemui.statusbar.views.MiuiClock`（`big_time` 的实际类型） | `TextView` | **0** | **无** |
| `com.android.systemui.statusbar.views.MiuiNotificationHeaderClock` | `MiuiClock` | **0** | 无 |
| `com.miui.clock.MiuiBaseClock` | `LinearLayout` | **0** | — |

**`MiuiClock` 的全部方法只有 16 个**，全是 setText / setClockMode /
updateTime / 明暗变化这类**文字设置**，**一个绘制方法都没有**。

`setBlurRadius(int)` / `getBlurRadius()` 只是存取一个 int 字段，
全项目**没有任何调用方**（死代码）。

**⇒ 官方的竖屏 QS 大时钟本身就是「纯文字、零玻璃」。
没有任何官方代码路径能把它变成玻璃 —— 改字体只能改字形，改不出材质。**

#### 那 `TimeView` 的玻璃是给谁用的？

`TimeView` 只被 `AllInOne*Clock` 使用，而 `AllInOne*Clock` 的继承链是：

```
AllInOneMinuteClock / AllInOneSingleClock
  extends AllInOneBase
    extends MiuiGalleryBaseClock
      extends MiuiBaseClock2   ← 抽象基类，hasOverlappingRendering()=false
```

`MiuiGalleryBaseClock` 的 "Gallery" 指向**画廊/壁纸时钟**，
是**锁屏或桌面壁纸联动的那套时钟**（`MiuiBaseClock2` 家族），
**不是** QS 下拉面板里的「通知大时钟」。

⇒ 之前把 `TimeView` 的玻璃序列照搬到 QS 时钟上，
相当于**把一个别的场景的实现套到不支持的载体上**。
API 能调通（因为是同进程反射），但渲染环境不匹配。

### 7.2 两者的对比结论

| 方案 | 可行性 | 原因 |
|---|---|---|
| **改官方字体成玻璃** | ❌ **不可行** | `MiuiClock` 无 onDraw、无玻璃代码、无材质能力。<br>它是 TextView，玻璃要么靠 View 材质（被不透明文字遮住），<br>要么靠 Paint 玻璃（需要自己接管 onDraw，那就已经等于自建了） |
| **自建视图做玻璃** | ✅ **唯一可行** | 我们自己的 View 可以完全掌控 onDraw，<br>才能用 `Paint.setGlassEffect(true)` 走 `MiDrawGlassPath` |

**注意**：这两条路在「文字玻璃化」上有本质差别——

- **改官方字体** = 只能动 `Typeface` / size / weight，**画法还是系统的 `TextView.onDraw`**，
  我们插不进 Path 玻璃。而且 `TextView` 内部会 drawText，
  Paint 级玻璃需要 `canvas.drawPath`，TextView 不给这个口子。
- **自建视图** = 自己 `getTextPath` + `canvas.drawPath`，
  这时 `Paint.setGlassEffect(true)` 才有意义。

**⇒ 「改官方字体」和「文字玻璃化」在技术上是互斥的。
想做玻璃，必须自建视图。主人提的两个选项里，只有一个是真的。**

### 7.3 核心难题：文字玻璃化（已找到明确方向）

现在只剩一个问题：**Path 玻璃怎么才能真正出图**。

#### 关键洞察：我们之前的做法可能自相矛盾

v370~v377 的实现里，**同时**做了两件事：

```java
// ① View 级材质（矩形通道）
View.setMiViewMaterialType(1)        // 打开矩形材质
View.setMiGlass(glassData)           // 42 项矩形配方
View.setMiGlassClip(l, t, r, b)      // 矩形裁剪区

// ② Paint 级玻璃（Path 通道）
paint.setGlassEffect(true)           // 打开 Path 玻璃
canvas.drawPath(textPath, paint)     // 画字形
```

**这两套是独立的渲染器**（本报告 1.1 节实证）。

问题在于：`setMiViewMaterialType(1)` 会在**View 背景层**生成一个矩形玻璃，
而 shader 里 `if (sdf <= 0.) return half4(0.0);` 决定它只在矩形内侧可见。
这个矩形**盖在我们的字形下面**，且它自己有 `uBgColorTex` 采样逻辑。

**黄块尺寸 = 整个 View** 这个现象，正好对应「View 材质层输出的矩形」，
而不是「字形玻璃画错了」。

⇒ **最该做的实验：把 View 级材质全部关掉，只留 Paint 级玻璃。**

```java
// 只留这三行
paint.setGlassEffect(true);
paint.setAtlasMaxPathSize(2048, 2048);
canvas.drawPath(textPath, paint);

// 删掉所有：
//   View.setMiViewMaterialType / setMiGlass / setMiGlassClip
//   MiuiBlurUtils.setGlassEffectMethod
//   setPassWindowBlurEnabled / setMiBlurWinExc / setMiBackgroundBlur*
```

**这个实验成本极低（改 3 行），且能一举判定方向对错。**

#### 第二个方向：`uIsIcon = 1` 分支

shader 里有 `uIsIcon > 0.5` 的分支（见 1.4 节），
走这个分支**完全不做模糊**，只采样 `uBgColorTex` 再提亮。

对文字来说这个模式非常合适：
- 文字笔画细，大模糊反而糊成一团
- 图标模式天然给「小面积有形状」的元素设计
- **不依赖 `blurInput`**，绕开最难的环境问题

只需找到 `uIsIcon` 在 42 项数组里对应哪个索引（索引 >= 42 时
可能是通过别的 setter 设置，比如 `Paint.setGlassBloom`）。

### 7.4 如果 Paint 玻璃仍不行，还有什么路

按成本排序：

1. **`Paint` + 自己的 RuntimeShader**（绕过 MiGlass，最干净）
   `Paint.setRenderEffect(RenderEffect.createRuntimeShaderEffect(shader, "input"))`，
   自己写一个「采样背景 + 折射 + 高光」的 GLSL。
   - 优点：完全自主，不受 MiGlass 环境限制
   - 缺点：拿不到背景快照（`RenderEffect` 的 `input` 是自身内容，不是背景）
   - **但这可以配合 `setPassWindowBlurEnabled` 拿窗口模糊图层**

2. **`Shader` 手动实现玻璃**（最朴素但一定能出图）
   用 `LinearGradient` / `BitmapShader` + 多层叠加，
   模拟玻璃的高光/边缘/折射。做不到真折射，但视觉上像。
   - 优点：100% 可控，一定能看到效果
   - 缺点：不是「真玻璃」，主人可能不满意

3. **`RenderNode` + `setUsageHint(SDF_GLASS)`**（最深，风险最大）
   直接操作 `RenderNode.setUsageHint` 触发 `MiDrawGlassPath`。
   需要 `RenderNode` 实例（`View.getRenderNode()`）。
   - 这是 shader 日志里 `UsageHint::SDF_GLASS MiDrawGlassPath::Draw` 的直接入口
   - 但 `setUsageHint` 是 hidden API，且需要 `RenderNode` 已完成 record

4. **等官方把玻璃下放到 QS 时钟**（最省事）
   既然是「官方没做」，说明可能是产品设计取舍。可以观察后续 OTA。

---

## 八、补充：本轮挖到的未被使用的框架 API（潜在突破口）

在 `framework.jar classes4.dex` 的 `android.view.View` 里，
还有一批**SystemUI 官方从未调用过**的玻璃/模糊 API：

### 8.1 `View.setMiBackgroundBlurPath(Path)` ★值得关注

```
method: setMiBackgroundBlurPath   code_off 0x3e346c
守卫  : ViewRootImpl.getSupportedMiBlur("backgroundBlur")
```

**这是唯一一个「用 Path 指定模糊区域」的 View API！**

含义：官方允许你把**任意 Path** 作为背景模糊的采样区域。
理论上可以让「字形 Path」区域去采样背景并模糊 ——
这正好是「文字笔画玻璃化」想要的效果。

官方 SystemUI 里**零调用**（死代码/预留接口），无人验证过，
但它的存在说明「按 Path 做模糊」是被框架设计支持的。

### 8.2 `View.setGlassBloom(...)`

```
method: setGlassBloom   code_off 0x3e21e0
守卫  : ViewRootImpl.getSupportedMiBlur("backgroundBlur")
         + ViewRootImpl.getAdvancedVisualRelease()
```

对应 hwui 里的 `MiGlassBloomFilter` / `SelfBlurBlendProperties::setGlassBloomParam`。
**玻璃泛光/辉光效果**，官方 SystemUI 也是零调用。

对文字玻璃可能有用：笔画边缘的高光/辉光正是玻璃质感的关键，
而这个 API 在 shader 里是**独立于 MiGlassFilter 的另一条路**
（`BloomStrokeParam`）。

### 8.3 完整的 View 玻璃/模糊 API 清单（classes4.dex 实证）

**getter（可用于探测现状）**：
```
getBackGroundBlurContainer      0x3c7178
getMiBackgroundBlurEnhanceFlag  0x3d2180
getMiBackgroundBlurMode         0x3d21a8
getMiBackgroundBlurRadius       0x3d21d0
getMiBackgroundBlurScaleRatio   0x3d1038
getMiBackgroundBlurType         0x3d21f8
getMiPassBlurEnhanceFlag        0x3d2220
getMiSelfBlurEnhanceFlag        0x3d2248
getMiSelfBlurExtend             0x3d2260
getMiSelfBlurRadius             0x3d2278
getMiSelfBlurType               0x3d2290
getMiViewBlurMode               0x3d22a8
getPassWindowBlurEnabled        0x3ca2a0
isPassWindowBlurWhitelisted     0x3cbf10
isBackgroundBlurDrawable        0x3cb0d0
```

**setter（我们可用）**：
```
setBackgroundBlur (两个重载)     0x3ce4fc / 0x3ce520
setBackgroundBlurAlpha          0x3ce548
setBackgroundBlurCrop           0x3ce57c
setBackgroundGradientBlurParams 0x3e0c6c
setForceEnablePassBlur          0x3ce5b8
setGlassBloom                   0x3e21e0   ★
setMiBackgroundBlurPath         0x3e346c   ★
setMiBackgroundBlurType         0x3e3748
setMiBackgroundBlurEnhanceFlag  0x3e3178
setMiBlurWinExc / List          0x3cea94 / 0x3ceb54
setMiBlurWinInc / List          0x3cec14 / 0x3cecd4
setMiBlurWinType                0x3ced94
setMiGlass                      0x3e399c
setMiGlassBlurRadius            0x3e39f4
setMiGlassClip                  0x3e3a80
setMiGlassSdfMaxSize            0x3e3b2c
setMiSelfBlur (两个重载)         0x3e3bb8 / 0x3e3bd4
setMiSelfBlurEnhanceFlag        0x3e3c10
setMiSelfBlurType               0x3e3d38
setMiViewBlurMode               0x3e4004
setMiViewMaterialType           0x3e40f8
setPassWindowBlurEnabled        0x3cf200
setSelfGradientBlurParams       0x3e5528
```

### 8.4 下一轮实验的优先级（更新版）

| 优先级 | 实验 | 成本 | 判定价值 |
|---|---|---|---|
| **P0** | 纯 Paint 玻璃（删掉全部 View 材质调用） | 极低（改 3 行） | 一举判定黄块元凶 |
| **P1** | 若 P0 出图但太淡 → 加 `setGlassBloom` | 低 | 增强玻璃质感 |
| **P2** | 若 P0 完全无图 → 试 `setMiBackgroundBlurPath(字形Path)` | 中 | 用 Path 定模糊区 |
| **P3** | 找 `uIsIcon` 索引，走图标模式 | 中 | 绕开 blurInput |
| **P4** | 自写 RuntimeShader | 高 | 完全自主 |

---

## 九、给主人的最终总结（三句话）

1. **「改官方字体做玻璃」这条路不存在** —— 官方 `big_time` 是 `MiuiClock`
   （纯 TextView，**0 行玻璃代码，连 onDraw 都没有**）。
   它天生不支持材质，改字体只能改字形。

2. **必须自建视图，而且有明确的技术依据** ——
   libhwui 里有**专为 Path 设计的独立玻璃渲染器** `MiDrawGlassPath`
   （`UsageHint::SDF_GLASS`），它和 View 材质层（`MiGlassFilter`）是两套东西。
   自建视图才能真正调用 `Paint.setGlassEffect(true)` 走这条路。

3. **黄块最可能的真正原因：我们同时开了两套通道，View 材质的矩形盖住了 Path 玻璃。**
   下一轮只需删掉所有 `setMiGlass*` / `setMiViewMaterialType`，
   只留 `paint.setGlassEffect(true)` + `canvas.drawPath()`，
   **3 行改动即可验证。**
