# HyperClassic

为 HyperOS 4 的经典控制中心恢复玻璃材质，并解锁横屏双列布局。

一个基于 **libxposed API 102** 的 LSPosed 模块。通过 hook SystemUI 的材质绘制流程与布局逻辑，
让原本失去玻璃质感的界面元素重新获得系统原生反光玻璃，并为经典控制中心补上 AOSP 式的横屏分栏。

---

## 功能

### 一、玻璃材质恢复

给以下界面元素套用系统原生的 MiGlass 材质：

| 区域 | 说明 |
|---|---|
| 通知卡片 | 下拉通知面板中的通知背景 |
| 悬浮通知（HUN） | 顶部弹出的横幅通知 |
| 媒体卡片 | 媒体播放控制卡片 |
| 通知内按钮 | 展开 / 收起、清除等圆形按钮 |
| 控制中心磁贴 | 快捷开关图标背景 |
| 亮度条 | 亮度滑块及其容器 |
| Toast | 系统 Toast 提示框 |

每个区域可独立开关，关闭后恢复系统原始样式。

玻璃的外观参数（配方、混合色、模糊半径、圆角）直接读取系统资源，与官方实现保持一致。
系统升级导致参数变化时效果会自动跟随，无需改代码。

### 二、横屏双列布局（split shade）

**这是玻璃材质之外的附加能力。**

经典控制中心（二合一 shade）在横屏下原本是单栏纵向排列，平板上常见的
AOSP split shade 分栏布局在手机上无法启用。本模块把 AOSP 的 split shade 逻辑
移植到 HyperOS 的经典控制中心上，实现：

```
横屏展开后
┌──────────────┬──────────────────┐
│              │                  │
│   QS 磁贴     │    通知列表       │
│   亮度条      │                  │
│              │                  │
└──────────────┴──────────────────┘
     左栏            右栏
```

配套修复（每一项都是移植过程中发现的实际问题）：

| 问题 | 根因 | 修复 |
|---|---|---|
| **上滑退不出控制中心** | `split_shade_full_transition_distance = 400dp` × density 3.75 = **1500px**，超过横屏高度 1440px，导致 `isFullyExpanded` 永远为 false | 钳制过渡距离为 `min(原值, 面板高度)` |
| 中部和上部上滑无响应 | `handleCollapseState` 第 6 关 `qsController.getExpanded()` 在 split 下恒 true（QS 永久占左栏），堵死后续安全网 | 跳过该判定，保留 header / 列表底部 / 最后一条通知三层安全网 |
| 左栏全境无法上滑 | 官方判定依赖「通知列表还有内容可滚」，但左栏 x 范围根本没有通知列表 | 触摸点横向不在 NSSL 范围内时直接放行 |
| **清除按钮不可见** | 状态栏左对齐改动把 `shade_header_container` 从 3200 收窄到 1577，连带裁掉了 x=2307 的按钮 | parent 撑全屏宽 + `clipChildren=false` |
| 清除按钮点不动 | ① ViewGroup 命中测试因 parent 仍 1577 宽不收点 ② 横屏下 `isInDismissViewBounds` 阈值恒 0 | hook 窗口根 `dispatchTouchEvent`，自己做点按识别 |
| 时钟反色看不清 | `MiuiClock.updateLightDarkTint` 末行无条件覆盖文字色 | 对注入实例强制纯白 |
| **HUN 玻璃背景不跟随卡片** | `mDismissUsingRowTranslationX=false` 时只平移 `mTranslateableViews` 子 View，玻璃胶囊层不在列表里；同时 `shelfIcon` 被反向平移 | 对 HUN 行强制该标志为 `true`，走整行平移路径 |
| HUN 压住状态栏 | `miuiHeadsUpInset` 横屏分支只算 `cutout.safeInsetTop + 7.27dp`，漏了状态栏高度 | 横屏分支补上真实状态栏高度 |

### 三、控制中心样式切换

跟随系统 / 经典二合一 / 新版控制中心，三选一。

---

## 环境要求

- **实测环境**：Redmi K80 Pro · HyperOS 4.0 · Android 17（API 37）
- **生效条件**：HyperOS 4 + Android 17；低于 Android 17 的 HyperOS 4 属于未测试环境，
  模块会提示「不保证可用」但仍然运行
- **框架**：LSPosed（libxposed API 102+）
- **权限**：Root（用于写入系统属性，切换功能开关）

> 其他设备/版本未做测试。HyperOS 3 及更早、非 HyperOS 系统上模块会拒绝运行并在界面内提示。

---

## 安装

1. 从 Releases 下载 APK
2. 安装后在 LSPosed 管理器中启用模块
3. **作用域勾选「系统界面」**（`com.android.systemui`）
4. 重启 SystemUI 或重启设备

模块会申请 Root 权限用于写入开关属性，请在弹出的授权框中允许。

**包名**：`io.github.mersand.hyperclassic`

---

## 使用

打开应用，底部两页：

### 首页

- 模块状态大卡片（已激活 / 部分功能受限 / 无法运行）
- 已启用区域数 / 区域总数
- 环境信息表（系统版本、设备、Root 状态、控制中心样式）

### 调整

- **玻璃区域**：每个区域独立开关
- **控制中心样式**：三选一
- **弹出通知**：HUN 玻璃跟随、顶部留白
- **调试工具**：发送测试通知、几何探针、视图树 dump

所有改动都需要**重启 SystemUI** 才会生效（hook 在安装期决定）。
右上角「重启」按钮可直接完成。

---

## 构建

```bash
./gradlew assembleRelease --offline
./gradlew assembleDebug --offline
```

产物位于 `build/outputs/apk/`。

两种构建的区别：

- `release`：静默运行，仅输出 WARN / ERROR 日志
- `debug`：全量日志，versionName 带 `-debug` 后缀

### ⚠️ AGP 9 构建坑

AGP 9.1.0 的 `optimizeReleaseResources` 在本项目会「假成功」——任务报 SUCCESS，
但 `optimized_processed_res/release/optimizeReleaseResources/` 下只有
`output-metadata.json`，没有任何 `.ap_` 产物。结果是 release APK 里
**没有 `resources.arsc` / `AndroidManifest.xml` / 任何 res 文件**，装上必然失败。

本项目已在 `gradle.properties` 关闭该管线：

```properties
android.enableResourceOptimizations=false
android.enableAppCompileTimeRClass=false
```

发版前务必验证：

```bash
python3 -c "import zipfile; n=zipfile.ZipFile('HyperClassic-release.apk').namelist(); \
print('arsc:', 'resources.arsc' in n, '| manifest:', 'AndroidManifest.xml' in n, '| entries:', len(n))"
```

正常应为 `arsc: True | manifest: True | entries: 139`。

> 当前签名配置使用 debug keystore，仅供个人使用。如需分发请自行替换。

---

## 项目结构

```
hyperclassic/
├── src/main/
│   ├── java/io/github/mersand/hyperclassic/
│   │   ├── ModuleMain.java             模块入口，注册 hook
│   │   ├── CcHooks.java                核心 hook 逻辑
│   │   ├── MainActivity.kt             主界面（Compose + Miuix，双页导航）
│   │   ├── StatusCard.kt               首页状态卡片组件
│   │   ├── ConfigStore.kt              配置读写
│   │   ├── RootShell.kt                Root 命令封装
│   │   ├── HunProbe.java               HUN 探针 + 测试通知 + HUN 修复层
│   │   ├── TestReceiver.java           调试广播接收器
│   │   ├── ViewGlassProbe.java         玻璃属性探针（调试用）
│   │   └── L.java                      日志封装
│   ├── resources/META-INF/xposed/
│   │   ├── module.prop                 模块元信息
│   │   ├── java_init.list              入口类声明
│   │   └── scope.list                  作用域
│   ├── res/mipmap-*/                   模块图标（真实玻璃合成）
│   └── AndroidManifest.xml
├── docs/                               技术文档
│   ├── LSPOSED_REPO_SUBMISSION.md      上架 LSPosed 仓库指南
│   ├── ISLAND_RELOCATION_RESEARCH_*.md 超级岛位置研究
│   ├── RELEASE_v2.1.1_*.md              v2.1.1 发布说明（当前版本）
│   ├── RELEASE_v2.1.0_*.md              v2.1.0 发布说明
│   └── CREDENTIALS.md                  凭据管理
├── SUMMARY                             LSPosed 仓库首页简介
└── build.gradle.kts
```

---

## 调试

模块内置大量调试开关，通过系统属性现场调整，无需重新编译：

```bash
# 查看日志
logcat -s 'HC/*'

# 示例：调整 Toast 玻璃
setprop debug.hyperclassic.toast_bgr 40         # 背景模糊半径
setprop debug.hyperclassic.toast_refract 0.1    # 折射偏移
setprop debug.hyperclassic.toast_glass_blur 1   # 磨砂开关
```

属性改动会在下次对应元素绘制时生效。

### HUN 调试

```bash
# 发一条测试 heads-up 通知
am broadcast -a io.github.mersand.hyperclassic.TEST_HUN \
  -n io.github.mersand.hyperclassic/.TestReceiver \
  --es title "测试标题" --es text "测试正文"

# 清除测试通知
am broadcast -a io.github.mersand.hyperclassic.CANCEL_HUN \
  -n io.github.mersand.hyperclassic/.TestReceiver

# 连续发 3 条
am broadcast -a io.github.mersand.hyperclassic.TEST_HUN_BURST \
  -n io.github.mersand.hyperclassic/.TestReceiver --ei count 3
```

探针开关：

```bash
setprop debug.hyperclassic.probe_hun 1        # 几何探针
setprop debug.hyperclassic.probe_hun_dump 1   # 视图树 dump
```

---

## 已知限制

- **Toast** 位于独立 `TYPE_TOAST` 窗口，拿不到 SurfaceFlinger 快照，
  玻璃反射效果弱于通知卡片，需要额外模糊层补偿。
- **上滑退出**：左栏 QS 区若列表有内容可滚，会优先滚列表（官方设计）；
  右栏通知区需先把列表滚到底再上滑。
- **超级岛位置迁移**未实现（研究结论见 `docs/ISLAND_RELOCATION_RESEARCH_20261124.md`）。
- 部分系统版本的元素类名可能被混淆，若某个区域失效请附日志提交 issue。

---

## 上架信息

本模块正在准备提交 [Xposed-Modules-Repo](https://github.com/Xposed-Modules-Repo) 官方仓库，
完整流程与踩坑记录见 [docs/LSPOSED_REPO_SUBMISSION.md](docs/LSPOSED_REPO_SUBMISSION.md)。

- **Package**: `io.github.mersand.hyperclassic`
- **Release tag 格式**：`[versionCode]-[versionName]`，例如 `20101-2.1.1`

---

## Credits

本项目为 vibe coding，由 deepseek-v4.1-flash 与 glm-5.3-flash 辅助开发。

## 致谢

- [libxposed](https://github.com/libxposed/api) — 现代 Xposed API
- [Miuix](https://github.com/miuix-kotlin-multiplatform/miuix) — 设置界面的 Compose 组件库

## License

[GPL-3.0](LICENSE)