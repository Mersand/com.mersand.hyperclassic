# HyperClassic v2.1.0

> 发布日期：2026-11-25
> 适配：HyperOS 4 / Android 17（Redmi K80 Pro 实测）
> 上一版：v2.0

---

## ✨ 本版亮点

### 一、界面全面重构（参考 HyperIsland）

原本是一个长滚动列表，所有开关堆在一起。现在拆成**两页底部导航**：

| 页 | 内容 |
|---|---|
| **首页** | 模块状态大卡片（已激活 / 部分受限 / 无法运行）+ 已启用区域数 / 区域总数 + 环境信息表 |
| **调整** | 玻璃区域、控制中心样式、弹出通知、调试工具四组开关 |

> 第二页命名「调整」而非「设置」——这一页调的是**系统外观参数**，
> 不是模块自身配置，用「设置」容易误导。

首页状态卡片会综合判断：系统版本门控、Root 权限、Hook 安装情况，
一眼就能看出模块当前能不能用。

### 二、模块图标

图标不是画出来的，而是**从系统真实玻璃截图里提取合成**的：

1. 下拉通知面板，让真实玻璃模糊渲染出来
2. 截图 → 裁出玻璃区域 → 高斯模糊抹掉 UI 残留
3. 前景叠加 `2×2 QS 磁贴 + 圆角托盘`（对角两格亮蓝、两格半透）
4. `squircle` 圆角蒙版

提供 **legacy mipmap 全套**（48/72/96/144/192，含 round）
和 **自适应图标**（108dp 画布 / 72dp 安全区，bg + fg 分层）。
圆形、方圆、方形三种 launcher 遮罩下都已验证不会被裁切。

### 三、弹出通知（HUN）两项修复

#### HUN 玻璃背景不跟随卡片（v355）

**现象**：横屏横滑清除 HUN 时，一层薄玻璃背景留在原地，
同时左侧状态栏的小图标跟着反向跑。

**根因**：`ExpandableNotificationRow.setTranslation(F)` 有两条互斥路径

```java
if (mDismissUsingRowTranslationX) {
    setTranslationX(dx);                                  // ① 整行一起动
} else {
    for (v : mTranslateableViews) v.setTranslationX(dx);   // ② 子 View 各自动
    getShelfIcon().setTranslationX(-dx);                   //    图标反向补偿
}
```

这个开关来自 `NotificationStackScrollLayout.updateDismissBehavior()`：

```java
mDismissUsingRowTranslationX = mShouldUseSplitNotificationShade
                             && mStatusBarState != KEYGUARD
                             && mIsExpanded;    // ★ HUN 弹出时面板未展开 → false
```

HUN 弹出时面板没展开，于是走路径 ② —— 玻璃胶囊层不在
`mTranslateableViews` 里，就留在原地；`shelfIcon` 被反向平移，
就是那个跟着跑的小图标。

**修复**：对 HUN 行强制把 `mDismissUsingRowTranslationX` 置 `true`，
让官方代码自己走路径 ①（不自己造平移，避免二次错位）。
开关 `debug.hyperclassic.hun_fix`（默认开）。

实测数据：修复前 `row.tx=0.0 / bg.tx=191.98`（差 192px），
修复后两者同步。

#### HUN 顶部不留状态栏空间（v356）

**现象**：横屏弹出通知紧贴屏幕上沿，压住状态栏。

**根因**：`HeadsUpManagerInjectorImplKt.miuiHeadsUpInset(ctx)`

```java
int base = res.getDimensionPixelSize(heads_up_status_bar_padding); // 7.27dp ≈ 27px
if (orientation == LANDSCAPE)
    return (cutout != null ? cutout.getSafeInsetTop() : 0) + base;  // ★ 横屏漏了状态栏
else
    return SystemBarUtils.getStatusBarHeight(ctx) + base;           // 竖屏正常
```

横屏时挖孔在侧边，`safeInsetTop == 0`，结果只剩 27px。

**修复**：横屏分支补上真实状态栏高度。
开关 `debug.hyperclassic.hun_pad_mode`（默认开）。
实测 `miuiHeadsUpInset 27 → 188 (sbh=161)`。

### 四、HUN 测试与探针能力

为定位上面两个问题新增的调试工具，也留给以后排查用：

- **发送测试 HUN / 清除测试 HUN** —— 不用等别人发消息就能复现
- **HUN 几何探针**（`debug.hyperclassic.probe_hun`）——
  logcat 打印 `ExpandableNotificationRow` 与 `NotificationBackgroundView`
  的 left/top/width/height/translationX/Y，以及两者差值 `dTX/dTY`
- **HUN 详细 dump**（`debug.hyperclassic.probe_hun_dump`）—— 打印整棵视图树

另配广播接收器，便于命令行自动化：

```bash
am broadcast -a io.github.mersand.hyperclassic.TEST_HUN \
  -n io.github.mersand.hyperclassic/.TestReceiver \
  --es title "标题" --es text "正文"
```

支持 `TEST_HUN` / `CANCEL_HUN` / `TEST_HUN_BURST`。

### 五、通知权限修复（v353）

模块此前 **manifest 里没有任何 `uses-permission`**，
Android 13+ 没有 `POST_NOTIFICATIONS` 就永远发不出通知 ——
渠道能建，`notify()` 被静默丢弃。

本版：
- manifest 声明 `POST_NOTIFICATIONS`
- 调整页提供权限状态条 + 一键申请
- 权限已给但系统总开关关着时，直接跳通知设置页

---

## 🔧 构建修复

**AGP 9.1.0 的 release 资源优化在本项目会「假成功」**：
`optimizeReleaseResources` 报 SUCCESS，但
`optimized_processed_res/release/optimizeReleaseResources/` 下
只有 `output-metadata.json`，没有任何 `.ap_` 产物。
`packageRelease` 因此拿不到资源，打出的 APK 连
`AndroidManifest.xml` 和 `resources.arsc` 都没有，装上必失败。

修法（`gradle.properties`）：

```properties
android.enableResourceOptimizations=false
android.enableAppCompileTimeRClass=false
```

关闭后 release 与 debug 走同一套打包路径。
对比验证：

| 项目 | 修复前 | 修复后 |
|---|---|---|
| APK 条目数 | 77 | **139** |
| `resources.arsc` | 无 | 有 |
| `AndroidManifest.xml` | 无 | 有 |
| 图标资源 | 0 | **22** |

---

## 📋 完整开关一览

| 属性 | 默认 | 说明 |
|---|---|---|
| `debug.hyperclassic.hun_fix` | 1 | HUN 玻璃跟随卡片 |
| `debug.hyperclassic.hun_pad_mode` | 1 | HUN 顶部留白（1=状态栏高度，0=关） |
| `debug.hyperclassic.hun_pad_extra` | 0 | `hun_pad_mode=2` 时的额外像素 |
| `debug.hyperclassic.probe_hun` | 0 | HUN 几何探针 |
| `debug.hyperclassic.probe_hun_dump` | 0 | HUN 视图树 dump |
| `debug.hyperclassic.local.collapse_fix` | 1 | 上滑退出修复（v344/v345/v349） |
| `debug.hyperclassic.local.inject_clock` | 1 | 时钟注入 |
| `debug.hyperclassic.local.touch_block` | 1 | 横屏通知列表拖拽（**不要关**） |

---

## ⚠️ 已知限制

- 超级岛位置迁移尚未实现（研究结论见 `docs/ISLAND_RELOCATION_RESEARCH_20261124.md`，
  正确切入点是 `IslandCoordinateCalculator.getSmallIslandX()`，
  需要 LSPosed 作用域加上 `miui.systemui.plugin`）
- 上滑退出：左栏 QS 区若列表有内容可滚，会优先滚列表（官方设计）；
  右栏通知区需先把列表滚到底再上滑

---

## 📦 安装

```bash
adb install -r HyperClassic-release.apk
```

重启 SystemUI 生效（模块内首页右上角「重启」按钮）。
