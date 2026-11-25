# HyperClassic v2.1.2

> 发布日期：2026-11-25
> 适配：HyperOS 4 / Android 17（Redmi K80 Pro 实测）
> 上一版：v2.1.1
> **竖屏修复版**

---

## ✨ 本版亮点

### 一、修复：清除按钮在竖屏完全不显示

#### 现象

横屏 split 下清除按钮正常，但切到**竖屏**后按钮彻底消失 —— 既看不见也点不到。

#### 根因：横屏兼容代码污染了竖屏

`SplitLayoutBridge.apply()` 为了让横屏的清除按钮能画到 QS 左栏之外，
把 dismiss 按钮的 **parent FrameLayout 宽度从 1577 强行撑到整屏宽（3200）**：

```java
// v357 的问题代码
FrameLayout.LayoutParams pp = (FrameLayout.LayoutParams)parent.getLayoutParams();
int fullW = r.getWidth();
if (fullW > 0 && pp.width != fullW) {
    pp.width = fullW;                    // ← 撑到 3200
    pp.gravity = Gravity.TOP|Gravity.LEFT;
    pp.leftMargin = 0;
    parent.setLayoutParams(pp);          // ← 但没调 save(parent, pp)！
}
```

**关键疏漏：没有调用 `save(parent, pp)`**，所以 `ORIGINAL` 快照表里从来没有 parent 的记录。

于是 `restore()` 在转竖屏时遍历 `ORIGINAL`，**parent 不在里面，宽度就永远是 3200**：

```
横屏：parent.width = 3200
  ↓ 转竖屏
竖屏：parent.width 仍是 3200（实际屏幕只有 1440）
  ↓
官方 dismiss 按钮在 parent 里居中定位
  → x ≈ (3200 - 191) / 2 = 1504
  → 1440 宽屏幕上，1504 在屏幕外
  ↓
主人看到的：清除按钮完全消失
```

这正是「为了兼容横屏，反而把竖屏搞坏了」的典型案例。

#### 修法（四项）

| # | 修改 | 作用 |
|---|---|---|
| ① | 写入前先 `save(parent, pp)` | 记录 parent 的原始 LayoutParams |
| ② | 写入后同步 `snap.last = copy(pp)` | 让 `restoreOne()` 能认出这是我们的写入 |
| ③ | 新增 `CLIP_STATE` 记录 `clipChildren` / `clipToPadding` 原值 | 横屏关裁剪的状态也要能复原 |
| ④ | `restore()` 遍历 `CLIP_STATE` 复原裁剪，进入条件也加上它 | 避免「只剩裁剪状态残留」时漏恢复 |

修复后的行为：

```
横屏：parent.width = 3200，clipChildren=false，dismiss 在右栏底部
  ↓ 转竖屏
restore() 触发：
  ORIGINAL 里有 parent → 宽度恢复原始值
  CLIP_STATE 里有 parent → clipChildren 恢复默认
  ↓
竖屏：按钮正常显示在底部居中 ✅
```

### 二、新增：竖屏通知几何探针（只读，默认关闭）

#### 为什么需要

主人反馈：竖屏下拉时通知的顶部距离被**横屏的 ~160px**（正好是横屏状态栏高度 161px）覆盖，
通知整个贴到了屏幕顶部。但这个问题**无法稳定复现** —— 不能靠猜，必须抓现场。

#### 观测点

| 侧 | Hook | 采集内容 |
|---|---|---|
| **写入侧** | `NSSL.updateTopPadding(FZ)V` | 谁传了什么值 + 10 帧调用栈 |
| **结果侧** | `NSSL.onLayout` | `AmbientState.mTopPadding` / `mStackY` / `mScrollY` / `mShadeExpanded`、NSSL 几何与 translationY、QS 展开高度、QS 托盘底部、第一行通知视觉落点 |

#### 关键设计：区分「正常」与「异常」

主人特别指出 —— **小 padding 本身不等于 bug**，有两种完全不同的情形：

| 情形 | QS 展开高度 | 判定 |
|---|---|---|
| 用户上滑把 QS 托盘收起 / 隐藏 | 小（→ 0） | ✅ **正常**，官方设计，不干预 |
| QS 托盘**仍可见**，通知却被压上去 | 大（> 400） | ❌ **异常**，抓栈 |

所以判定是「padding < 200px **且** mExpansionHeight > 400px」的**组合条件**，
再加 **300ms 持续性门控**，排除展开/折叠动画的瞬时中间态。

#### 实测验证

三轮展开/折叠循环后，`SUSPICIOUS` 仅触发 **1 次**，动画态未误报 ✅

#### 首次复现抓到的调用栈

```
SharedNotificationContainerBinder$bind$2$2$2$1#emit:106
  → NSSL.updateTopPadding(0.0f)
     但此时 qsH=1211（QS 仍可见！）
```

#### 日志格式

```bash
# 竖屏几何采样（正常态）
portraitGeom pad=1211 stackY=1211 scrollY=0 shadeExp=true
             nssl=0/h3200/ty0 vpad=0 qsH=1211 trayBot=3200
             slider=-1 row1=(0+1211)=1211/h582 rows=7 #5

# 异常写入（带调用栈）
SUSPICIOUS pad=0 but qsH=1211 (QS still visible!) portrait #0 stack:
    @ ...SharedNotificationContainerBinder$bind$2$2$2$1#emit:106
    @ kotlinx.coroutines.flow.StateFlowImpl#collect:171
    ...
```

#### 开关

```bash
setprop debug.hyperclassic.probe_portrait 1    # 打开（默认 0）
setprop debug.hyperclassic.probe_portrait 0    # 关闭
```

> 只在**竖屏 + 面板展开**时打印，横屏不产生任何日志。
> release 版默认关闭，不输出任何探针日志。

### 三、修复：ShadeQsHeightFix 缺横屏门控

`mSplitShadeEnabled` 被 `SplitForceFlag` hook `Resources.getBoolean` 强制为 `true`，
**在竖屏下也是 true**。导致这个「split 横屏专用」的 QS 高度修复在竖屏照样执行：

```
竖屏 h==0 → setExpansionHeight(max) → QS 被强行拉到最大高度
         → 竖屏 shade 高度布局错乱
```

它本来要解决的问题（「竖屏展开完整 CC → 转横屏 → 下拉时磁贴不绘制」）只在横屏出现，
竖屏下官方逻辑完全正常，绝不能插手。

**修法**：`afterUpdate()` 开头补上 `if (!CcHooks.diagnosticLandscapeNow()) return;`

---

## 🔧 技术信息

| 项 | 值 |
|---|---|
| applicationId | `io.github.mersand.hyperclassic` |
| versionCode | `20102` |
| versionName | `2.1.2` |
| minSdk / targetSdk / compileSdk | 33 / 36 / 37 |
| Xposed API | libxposed 102 |
| 作用域 | `com.android.systemui` |
| Hook 层数 | 13 → **14**（新增 L9 PortraitShadeProbe） |
| APK 大小 | 23.63 MB |
| APK 条目 | 139（arsc + manifest + 22 图标齐全） |

### 安装

```bash
adb push HyperClassic-release.apk /data/local/tmp/
adb shell pm install -r -d /data/local/tmp/HyperClassic-release.apk
adb shell "kill -TERM $(pidof com.android.systemui)"
```

### 调试

```bash
# 模块日志
adb logcat -s 'HC/*'

# 竖屏探针（本次新增）
adb shell setprop debug.hyperclassic.probe_portrait 1
adb logcat -d --pid=$(adb shell pidof com.android.systemui) | grep -E 'SUSPICIOUS|portraitGeom'

# HUN 测试通知
adb shell am broadcast -a io.github.mersand.hyperclassic.TEST_HUN \
  -n io.github.mersand.hyperclassic/.TestReceiver \
  --es title "测试标题" --es text "测试正文"
```

---

## 📋 发版检查清单

- [x] `applicationId` = `io.github.mersand.hyperclassic`
- [x] `versionCode` = 20102，`versionName` = 2.1.2
- [x] `./gradlew assembleRelease --offline` 成功（31s）
- [x] APK 139 条目 / `resources.arsc` + `AndroidManifest.xml` 齐全
- [x] APK 22 个 `ic_launcher` 图标资源
- [x] APK v2 签名块存在
- [x] `module.prop` / `java_init.list` 包名正确
- [x] git commit `44c3eaf` 已推送
- [x] tag `20102-2.1.2` 已推送
- [ ] **设备实测验收**（清除按钮竖屏显示）
- [ ] **Xposed-Modules-Repo 发 release**（附 APK）

---

## 🔜 待办

1. 主人在设备上验收竖屏清除按钮
2. 通知 padding 问题复现后，用探针抓 `SUSPICIOUS` 调用栈定位
3. 到 [Xposed-Modules-Repo/submission](https://github.com/Xposed-Modules-Repo/submission/issues/new) 提交上架 issue
4. Bot 建仓库后配置 Description / Home Page，上传 `SUMMARY` + `README.md`
5. 发 release：Tag `20102-2.1.2`，附 APK

详见 [LSPOSED_REPO_SUBMISSION.md](LSPOSED_REPO_SUBMISSION.md)。

---

## 已知限制

- **Toast** 位于独立 `TYPE_TOAST` 窗口，拿不到 SurfaceFlinger 快照，玻璃反射效果弱于通知卡片
- **上滑退出**：左栏 QS 区若列表有内容可滚，会优先滚列表（官方设计）
- **通知 padding 异常**：尚未定位到根因，探针已就绪等待复现
- **超级岛位置迁移**未实现（见 `ISLAND_RELOCATION_RESEARCH_20261124.md`）
- 其他设备/版本未测试，HyperOS 3 及更早会拒绝运行

---

## License

[GPL-3.0](../LICENSE)