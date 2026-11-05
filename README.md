# HyperClassic

为 HyperOS / MIUI 的经典控制中心恢复玻璃材质。

一个基于 **libxposed API 102** 的 LSPosed 模块。通过 hook SystemUI 的材质绘制流程，让原本失去玻璃质感的界面元素重新获得与系统原生一致的反光玻璃外观。

## 功能

给以下界面元素套用系统原生的 MiGlass 材质：

| 区域 | 说明 |
|---|---|
| 通知卡片 | 下拉通知面板中的通知背景 |
| 悬浮通知 | 顶部弹出的横幅通知 |
| 媒体卡片 | 媒体播放控制卡片 |
| 通知内按钮 | 展开 / 收起、清除等圆形按钮 |
| 控制中心磁贴 | 快捷开关图标背景 |
| 亮度条 | 亮度滑块及其容器 |
| Toast | 系统 Toast 提示框 |

每个区域都可以独立开关，关闭后该处恢复系统原始样式。

玻璃的外观参数（配方、混合色、模糊半径、圆角）全部直接读取系统资源，与官方实现保持一致。系统升级导致参数变化时效果会自动跟随，无需改代码。

此外还提供**控制中心样式切换**：跟随系统 / 经典二合一 / 新版控制中心。

## 环境要求

- **实测环境**：Redmi K80 Pro · HyperOS 4.0 · Android 17（API 37）
- **生效条件**：HyperOS 4 + Android 17；低于 Android 17 的 HyperOS 4 属于未测试环境，模块会提示「不保证可用」但仍然运行
- **框架**：LSPosed（libxposed API 102+）
- **权限**：Root（用于写入系统属性，切换功能开关）

> 其他设备/版本未做测试。HyperOS 3 及更早、非 HyperOS 系统上模块会拒绝运行并在界面内提示。

## 安装

1. 从 Releases 下载 APK
2. 安装后在 LSPosed 管理器中启用模块
3. **作用域勾选「系统界面」**（`com.android.systemui`）
4. 重启 SystemUI 或重启设备

模块会申请 Root 权限用于写入开关属性，请在弹出的授权框中允许。

## 使用

打开应用可以看到两组设置：

- **玻璃区域**：每个区域独立开关
- **控制中心样式**：三选一

所有改动都需要**重启 SystemUI** 才会生效，因为 hook 是在安装期决定的。应用右上角的「重启」按钮可以直接完成。

## 构建

```bash
./gradlew :hyperclassic:assembleRelease
./gradlew :hyperclassic:assembleDebug
```

产物位于 `hyperclassic/build/outputs/apk/`。

两种构建的区别：

- `release`：静默运行，仅输出 WARN / ERROR 日志
- `debug`：全量日志，versionName 带 `-debug` 后缀

> 当前签名配置使用 debug keystore，仅供个人使用。如需分发请自行替换。

## 项目结构

```
hyperclassic/
├── src/main/
│   ├── java/com/mersand/hyperclassic/
│   │   ├── ModuleMain.java      模块入口，注册 hook
│   │   ├── CcHooks.java         核心 hook 逻辑
│   │   ├── MainActivity.kt      设置界面（Compose + Miuix）
│   │   ├── ConfigStore.kt       配置读写
│   │   ├── RootShell.kt         Root 命令封装
│   │   ├── ViewGlassProbe.java  玻璃属性探针（调试用）
│   │   └── L.java               日志封装
│   ├── resources/META-INF/xposed/
│   │   ├── module.prop          模块元信息
│   │   ├── java_init.list       入口类声明
│   │   └── scope.list           作用域
│   └── AndroidManifest.xml
└── build.gradle.kts
```

## 调试

模块内置大量调试开关，通过系统属性现场调整，无需重新编译：

```bash
# 查看日志
logcat -s HyperClassic

# 示例：调整 Toast 玻璃
setprop debug.hyperclassic.toast_bgr 40         # 背景模糊半径
setprop debug.hyperclassic.toast_refract 0.1    # 折射偏移
setprop debug.hyperclassic.toast_glass_blur 1   # 磨砂开关
```

属性改动会在下次对应元素绘制时生效。

## 已知限制

- Toast 位于独立的 `TYPE_TOAST` 窗口，拿不到 SurfaceFlinger 快照，玻璃反射效果弱于通知卡片，需要额外的模糊层补偿。
- 部分系统版本的元素类名可能被混淆，若某个区域失效请附日志提交 issue。

## Credits

本项目为 vibe coding，由 deepseek-v4.1-flash 与 glm-5.3-flash 辅助开发。

## 致谢

- [libxposed](https://github.com/libxposed/api) — 现代 Xposed API
- [Miuix](https://github.com/miuix-kotlin-multiplatform/miuix) — 设置界面的 Compose 组件库

## License

[GPL-3.0](LICENSE)
