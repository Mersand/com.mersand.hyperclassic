# HyperClassic v2.1.1

> 发布日期：2026-11-25
> 适配：HyperOS 4 / Android 17（Redmi K80 Pro 实测）
> 上一版：v2.1.0
> **本版为 LSPosed 官方仓库上架准备版**

---

## ⚠️ 重要：包名变更

**`com.mersand.hyperclassic` → `io.github.mersand.hyperclassic`**

### 为什么改

LSPosed 官方模块仓库（[modules.lsposed.org](https://modules.lsposed.org)）对 `applicationId` 有硬性要求：

```
- For reverse domain name notation in your application id, please use
  something you own rather than random domain.
- You are require to set a TXT record for your root domain:
    lsposed-modules-repo-verification=your_github_username
- If you do not own any domain, you may use io.github.{username} prefix
```

`mersand.com` 虽能返回 HTTP 302，但**没有 A/AAAA 记录**，不属于可控域名。
因此改用官方提供的免域名方案：`io.github.{username}` 前缀。

### 升级须知（重要）

这是一个**新的 applicationId**，Android 视作另一个应用：

| 事项 | 说明 |
|---|---|
| **不会覆盖安装** | 旧版 `com.mersand.hyperclassic` 需手动卸载 |
| **需要重新授权** | LSPosed 管理器里重新启用模块 + 勾选「系统界面」作用域 |
| **需要重启 SystemUI** | 作用域变更后必须重启才生效 |
| **配置会重置** | 旧版开关设置不继承，需重新调整 |

推荐顺序（避免功能断档）：

1. 安装新版 APK
2. LSPosed 管理器 → 启用 `io.github.mersand.hyperclassic` → 作用域勾选 `com.android.systemui`
3. 重启 SystemUI，确认 SystemUI 日志出现新包名
4. 卸载旧版 `com.mersand.hyperclassic`

---

## ✨ 本版变更

### 一、上架 LSPosed 官方仓库的准备

- `namespace` / `applicationId` 迁移至 `io.github.mersand.hyperclassic`
- 源码目录树、`java_init.list`、Manifest 广播 action、`TestReceiver` 常量同步更新
- 新增 `SUMMARY` 文件（LSPosed 仓库首页卡片简介）
- 新增 [docs/LSPOSED_REPO_SUBMISSION.md](LSPOSED_REPO_SUBMISSION.md)（完整上架流程与踩坑记录）

> GitHub 仓库名仍是 `Mersand/com.mersand.hyperclassic`，**不需要改**——仓库名与 APK 包名解耦。

### 二、模块描述文案补全

模块「横屏双列布局」是**玻璃材质之外的独立能力**，此前描述里没有体现。本次统一为：

```
为HyperOS 4经典控制中心解锁横屏双列布局，并补齐柔光玻璃渲染
```

涉及三处：

| 位置 | 作用 |
|---|---|
| `module.prop` 的 `description` | LSPosed 管理器模块列表里显示 |
| `AndroidManifest.xml` 的 `android:description` | 应用信息页显示 |
| `SUMMARY` | LSPosed 官方仓库首页卡片显示 |

### 三、README 全面重写

旧 README 停留在 v1 时代（11 月 5 日），本次重写，明确把**玻璃材质**与**横屏双列布局**拆成两个并列章节，
并补上双列布局配套修复的完整问题表（8 项，含上滑退出、清除按钮、时钟反色、HUN 玻璃跟随、HUN 顶部留白）。

---

## 📦 完整功能清单

### 玻璃材质恢复

| 区域 | 状态 |
|---|---|
| 通知卡片 | ✅ |
| 悬浮通知（HUN） | ✅ |
| 媒体卡片 | ✅ |
| 通知内按钮 | ✅ |
| 控制中心磁贴 | ✅ |
| 亮度条 | ✅ |
| Toast | ✅（反射较弱，见已知限制） |

### 横屏双列布局（split shade）

经典控制中心在横屏下原本单栏纵向排列。本模块移植 AOSP split shade 逻辑：

```
┌──────────────┬──────────────────┐
│   QS 磁贴     │                  │
│   亮度条      │    通知列表       │
└──────────────┴──────────────────┘
     左栏              右栏
```

配套修复 8 项：

| 问题 | 根因 |
|---|---|
| 上滑退不出控制中心 | `split_shade_full_transition_distance` 400dp × 3.75 = 1500px > 横屏高度 1440px |
| 中部和上部上滑无响应 | `handleCollapseState` 第 6 关在 split 下恒 true |
| 左栏全境无法上滑 | 判定依赖通知列表可滚，左栏无列表 |
| 清除按钮不可见 | `shade_header_container` 收窄裁掉按钮 |
| 清除按钮点不动 | ViewGroup 命中测试 + `isInDismissViewBounds` 阈值恒 0 |
| 时钟反色看不清 | `MiuiClock.updateLightDarkTint` 无条件覆盖文字色 |
| HUN 玻璃背景不跟随 | `mDismissUsingRowTranslationX=false` 走子 View 平移路径 |
| HUN 压住状态栏 | `miuiHeadsUpInset` 横屏分支漏算状态栏高度 |

### 控制中心样式切换

跟随系统 / 经典二合一 / 新版控制中心，三选一。

---

## 🔧 技术信息

| 项 | 值 |
|---|---|
| applicationId | `io.github.mersand.hyperclassic` |
| versionCode | `20101` |
| versionName | `2.1.1` |
| minSdk | 33 |
| targetSdk | 36 |
| compileSdk | 37 |
| Xposed API | libxposed 102 |
| 作用域 | `com.android.systemui` |

### 安装与调试

```bash
# 安装
adb push HyperClassic-release.apk /data/local/tmp/
adb shell pm install -r -d /data/local/tmp/HyperClassic-release.apk

# 重启 SystemUI 使 hook 生效
adb shell "kill -TERM $(pidof com.android.systemui)"

# 看模块日志
adb logcat -s 'HC/*'
```

调试广播（包名已更新）：

```bash
am broadcast -a io.github.mersand.hyperclassic.TEST_HUN \
  -n io.github.mersand.hyperclassic/.TestReceiver \
  --es title "测试标题" --es text "测试正文"

am broadcast -a io.github.mersand.hyperclassic.CANCEL_HUN \
  -n io.github.mersand.hyperclassic/.TestReceiver
```

---

## 📋 发版检查清单

- [x] `applicationId` 为 `io.github.mersand.hyperclassic`
- [x] `versionCode` = 20101，`versionName` = 2.1.1
- [x] `./gradlew assembleRelease --offline` 成功
- [x] release APK 含 `resources.arsc` + `AndroidManifest.xml`（139 条目）
- [x] release APK 含 22 个 `ic_launcher` 图标资源
- [x] `SUMMARY` / `README.md` 已就绪
- [x] 模块描述三处文案一致
- [ ] **APK 设备实测**（待主人验收）
- [ ] **Xposed-Modules-Repo 发 release**（Tag 用 `20101-2.1.1`，必须附 APK）

---

## 🔜 下一步

1. 主人在设备上安装 v2.1.1 验收
2. 在源码仓库提交并推送
3. 到 [Xposed-Modules-Repo/submission](https://github.com/Xposed-Modules-Repo/submission/issues/new) 开 issue：
   ```
   标题：[submission] io.github.mersand.hyperclassic
   ```
4. Bot 建仓库后配置 Description / Home Page，上传 `SUMMARY` + `README.md`
5. 在新仓库发 release：Tag `20101-2.1.1`，附 `HyperClassic-release.apk`
6. 等待 3 个工作日内出现在 modules.lsposed.org

详细流程见 [LSPOSED_REPO_SUBMISSION.md](LSPOSED_REPO_SUBMISSION.md)。

---

## 已知限制

- **Toast** 位于独立 `TYPE_TOAST` 窗口，拿不到 SurfaceFlinger 快照，玻璃反射效果弱于通知卡片
- **上滑退出**：左栏 QS 区若列表有内容可滚，会优先滚列表（官方设计）；右栏通知区需先滚到底再上滑
- **超级岛位置迁移**未实现（研究结论见 `ISLAND_RELOCATION_RESEARCH_20261124.md`）
- 其他设备/版本未做测试，HyperOS 3 及更早会拒绝运行

---

## License

[GPL-3.0](../LICENSE)
