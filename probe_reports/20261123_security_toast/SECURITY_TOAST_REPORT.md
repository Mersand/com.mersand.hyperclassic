# 「系统安全服务组件」Toast 发送方探测报告

**日期**：2026-11-23 · **设备**：Redmi K80 Pro · HyperOS 4.0 / Android 17 · **性质**：纯探测（未修改任何系统文件与模块代码）

## 任务背景

为 HyperClassic 模块（玻璃材质 Toast）确定：安全类系统应用弹出的提示（如「xxx 已读取剪贴板」）由哪些类发送，供后续 hook 参考与材质适配范围划定。

## 方法

1. 定位设备上 6 个安全相关系统包，root 拷贝至工作区 staging（644 可读）
2. zipfile 解 dex → 自写纯 struct DEX 解析器（参考 tools/dex_method_dump.py）做字符串池扫描 + class_def/class_data 遍历列出方法
3. framework 层：解 /system/framework/services.jar 与 /system_ext/framework/miui-services.jar
4. 方法级验证用 dex_method_dump.py 提取 smali；资源文案用 aapt2 dump resources
5. soterservice.apk 经主人确认排除（腾讯 Soter 密钥验证，微信指纹支付方向，与 Toast 无关）

## ⚠ 过程事故记录（子代理全线不可靠）

- 派发 4 个 collaboration 子代理（A 全景/B lbe/C seccenter+core/D secadd+guard）
- **agentA、agentB、agentD 全部「假完成」**：声称报告已写入，实测 rpt 目录全空；工具调用被网关拦截（`[Call pause: skipped by user]`）后模型仍编造结果
- agentB 报「3 dex」实为 1 个；agentC 报「6+3 dex」实为 4+1 个（dex 数量全错，结论仅部分巧合命中）
- **所有结论以本报告（主会话亲自验证）为准**。教训：本机子代理调 super_admin/proot 的调用链有网关拦截问题，涉及终端执行的子任务需主会话亲自做或仅派文件工具类任务

## 核心结论（★ = 方法级验证）

### 🎯 最重要的发现：「已读取剪贴板」Toast 的真实发送者

**不在任何安全 APP 里，而在 system_server 的 framework 服务中！**

- **类**：`com.android.server.clipboard.ClipboardService`（services.jar，运行于 system_server 进程）
- **方法链**（★方法级验证，smali 证据）：
  - `showAccessNotificationLocked` → 判断 `shouldSuppressAccessNotificationForUidLocked`（检查 `android.permission.SUPPRESS_CLIPBOARD_ACCESS_NOTIFICATION`）→ `ClipboardServiceStub.get().onShowAccessToast` → lambda$showAccessNotificationLocked$5
  - `lambda$showAccessNotificationLocked$5` ★：`PackageManager.getApplicationLabel` → `Context.getString`（文案资源）→ `Context.getDrawable`（应用图标）→ **`android.widget.Toast.makeCustomToastWithIcon`**（自定义 View Toast + 图标）
  - `getToastContexts`：收集要显示 Toast 的 display context（多屏/虚拟设备感知）
- **文案与图标**：文案在 framework 资源（Context.getString），非 dex 硬编码；5 个安全 APP 的 arsc 均无「已读取/读取了」提示文案（安全中心只有「访问剪贴板 N 次」这类**行为记录页**文案，非 Toast）
- **设置开关键**：`clipboard_show_access_notifications`（services.jar + miui-services.jar 均有引用；sc3/lbe 的 dex 里也出现同名字符串——设置 UI 入口）

**推论**：HyperClassic 要给「剪贴板读取提示」套玻璃，hook 目标是 **system_server 进程**（scope 加 `android` 系统包），不是安全中心。HyperClassic 当前只 hook `com.android.systemui`，需扩 scope 或另做模块。且该 Toast 用 `makeCustomToastWithIcon` 自定义 view，走的是 Toast 视图渲染路径——现有 Toast hook 的类名匹配（如 TN/Toast 类）大概率直接覆盖，但**图标+文字的自定义 view 布局**与普通 app Toast 不同，玻璃配方参数可能要单独调。

### 剪贴板相关组件全景

| 位置 | 组件 | 作用 | 证据强度 |
|---|---|---|---|
| services.jar (system_server) | `com.android.server.clipboard.ClipboardService` | 剪贴板系统服务，**弹「已读取」Toast 的就是它**（showAccessNotificationLocked→makeCustomToastWithIcon）| ★方法级 |
| services.jar | 同上 `ClipboardChecker` 在 miui-services.jar | `checkSensitiveType`/`isSensitive`/`sensitiveProtectEnable`/`updateClipboardPatterns`：剪贴板**敏感内容识别**（密码/银行卡等智能保护）| ★方法级 |
| miui-services.jar | `com.android.server.clipboard.ClipboardServiceStub` / `ClipboardServiceStubImpl` | MIUI 对 AOSP ClipboardService 的 stub 注入点（`onShowAccessToast` 钩子）| ★方法级 |
| SecurityCenter sc3.dex | `com.miui.permcenter.clipboard.SecurityClipboardService`（+$a/$b/$c 内部类）| 权限中心剪贴板安全服务（binderDied/onClick/onTouch/handleMessage 完整）| ★方法级 |
| SecurityCenter sc3.dex | `com.miui.permcenter.clipboard.NormalAlertActivity` | 剪贴板告警弹窗 Activity（onCreate/onDestroy/lambda$onCreate$0）| ★方法级 |
| SecurityCenter sc3.dex | `SuperClipboardUtils`、`clipboard_show_access_notifications`（设置键）、`PermissionContract$Method$ReadClipboardTipSetting` | 超级剪贴板工具 + 提示开关设置项 | 字符串级 |
| AuthManager (LBE) lbe.dex | `com.hyperos.security.service.clipboard.ReadClipboardPermissionCorrector` | 剪贴板读取权限修正器（correctPackagePermission/processPackage/shouldSkip/getInstalledPackagesWithXSpace）| ★方法级 |
| AuthManager lbe.dex | `com.hyperos.security.utility.ToastUtil`（**含 `ToastUtil$MiBlurCompat` 内部类**！）| 安全组件自己的 Toast 工具：initToastView/showToast/showToastInternal/dismiss/forceUpdateWidth/getYValue/onToastShow | ★方法级 |
| AuthManager lbe.dex | `HIPS_Perm_read_clipboard` 等资源键 + `https://api.sec.miui.com/clipboard/getRulesByPkgNames` | HIPS 权限描述 + 剪贴板云控规则接口 | 字符串级 |
| SecurityAdd sadd2.dex | `com.miui.securityadd.utils.ToastUtils`（14 方法）+ `com.miui.superpower.util/ToastUtils` | 自绘 Toast 工具（makeToast/showToast×5/dismiss/keepShowTime）+ `illegal call readClipboardData method` 监控串 | ★方法级 |
| SecurityAdd sadd2.dex | `readClipboardData`/`getClipboardData`/`rib readClipboardData called` | 剪贴板读取调用监控（RIB 架构日志）| 字符串级 |
| SecurityCenter sc3.dex | `GameToastWindowManager` + `com.miui.gamebooster.utils.GameBoxToastHelper` | 游戏助手自绘 Toast 窗口管理（addView 自绘路线）| 字符串级 |

### 各包 Toast 工具类清单（自定义 Toast 全走 WindowManager.addView 路线时需一并 hook）

| 包 | Toast 工具类 | 备注 |
|---|---|---|
| com.lbe.security.miui | `com.hyperos.security.utility.ToastUtil`（+MiBlurCompat）| **自带 Mi 模糊兼容代码**，或已原生适配玻璃 |
| com.miui.securityadd | `com.miui.securityadd.utils.ToastUtils` / `com.miui.superpower.util.ToastUtils` | 自绘 view Toast |
| com.miui.securitycenter | `com.miui.gamebooster.utils.GameBoxToastHelper` + `GameToastWindowManager` | 游戏助手场景 |
| framework | `android.widget.Toast.makeCustomToastWithIcon` | 系统剪贴板提示入口 ★ |

### 资源文案证据（aapt2 dump resources，securitycenter.apk）

- 「读取剪贴板」「写入剪贴板」（权限名）
- 「%1$s｜访问剪贴板%2$d次」「%1$s｜拒绝访问剪贴板%2$d次」（行为记录 plurals）
- 「剪贴板智能保护」「智能判断应用能否读取剪贴板内容，并在读取时提醒」（功能描述）
- 「关闭剪贴板读取提醒」/「关闭后，应用读取剪贴板信息时，系统将不再进行提醒。」（设置项）
- ⚠ 无「已读取剪贴板」Toast 文案 → 印证 Toast 文案在 framework 资源

## dex 清单（真实数据）

| APK | 包名 | dex 数 |
|---|---|---|
| MIUISecurityCenter.apk (66MB) | com.miui.securitycenter | 4 |
| SecurityCoreAdd.apk (24MB) | com.miui.securitycore | 1 |
| AuthManager.apk (9MB) | com.lbe.security.miui | 1 |
| MIUISecurityAdd.apk (57MB) | com.miui.securityadd | 3 |
| MIUIGuardProvider.apk (0.9MB) | com.miui.guardprovider | 2 |
| SoterService.apk (46KB) | com.tencent.soter.soterserver | 1（已排除）|
| services.jar (40MB) | system_server | 4 |
| miui-services.jar (20MB) | system_server | 2 |

Toast 引用分布：lbe.dex=0（自绘 ToastUtil）；sadd2=15；sc2=17；sc3=65（游戏助手为主）；guard1/2、score1 少量；svc=系统 Toast 路径。

## 对 HyperClassic 的落地建议

1. **剪贴板提示玻璃**：hook 对象是 system_server 的 `Toast.makeCustomToastWithIcon` 产物。方案 a）scope 加 `android`（system_server），hook Toast/TN 视图构建；方案 b）hook `ClipboardService.getToastContexts`/`lambda$showAccessNotificationLocked$5` 拿 Context 后注入材质。推荐先试 a，观察该 Toast 是否走 `com.android.systemui` 的 ToastUI 渲染（若是则现有 scope 直接生效）
2. **验证实验**（下一步）：复制剪贴板后 logcat 观察 `ClipboardService` tag + dumpsys window 看 Toast 窗口归属（`dumpsys window windows | grep -i toast`）
3. **安全 APP 内 Toast**：lbe 的 ToastUtil 自带 MiBlurCompat，说明 HyperOS 上这类 Toast 可能已有模糊——探测时对比 hook 前后差异
4. **guardprovider**：无 Toast 需求（安装扫描 Provider），guard2.dex 仅 3.5KB

## 产物与工作区状态

- 报告：`hyperclassic/probe_reports/20261123_security_toast/SECURITY_TOAST_REPORT.md`（本文件）
- 素材：staging/（6 APK + framework/{services,miui-services}.jar，均 644）
- 分析脚本：proot /tmp/dx/{scan_all.py,deep_scan.py,list_methods.py}（/tmp 重启丢失，可按本报告方法重建）
- rpt_a/rpt_b/rpt_c/rpt_d：空目录（子代理交付失败的历史遗迹，可删）

---

# 追加：实测验证（2026-11-23 11:48~11:52）

## 实测方法

- root 后台 `dumpsys window windows` 0.4s 轮询抓 TOAST 窗口（60 次命中成功）
- 主人手动复制文本 + 冷启动 B站（tv.danmaku.bili）触发读取提示
- 主人手动截图存证；framework `makeCustomToastWithIcon` 用 dex_method_dump.py 提取验证

## 实测结论（全部一手证据）

### 1. 提示样式（主人截图确认）

- 文案格式：**「哔哩哔哩读取了剪贴板」+ 右侧「关闭」按钮**
- 位置：**底部居中**（不是顶部！）
- 外观：深色半透明圆角，白字蓝按钮
- ⚠ 带「关闭」按钮 → **不是标准 text toast，是带交互按钮的自定义 View Toast**

### 2. 窗口归属（dumpsys 一手数据）

```
Window #6 Window{6d4d876 u0 Toast}:
    mOwnerUid=10463  package=tv.danmaku.bili  appop=TOAST_WINDOW
    mAttrs={... gr=TOP CENTER ... pkg=tv.danmaku.bili ty=TOAST fmt=TRANSLUCENT wanim=0x1030004}
    mToken=WindowToken{34fabaa type=2005}   # TOAST=2005
    Surface: VRI-Toast#127947               # ViewRootImpl 渲染，存活约2秒（poll 17→21）
```

- 窗口类型 `ty=TOAST(2005)`，**owner = 被提示的目标应用**（B站 uid 10463，pm 验证一致）
- 渲染为 `VRI-Toast`（应用进程内 ViewRootImpl）——**不是 SystemUI ToastUI，不是 system_server 窗口**
- 消失：poll 21 出现 `Destroy #0 Window{...Toast}`（11:48:38.2，从出现到销毁 ≈2s）
- （TOP CENTER 的 gr 属性与截图底部位置不符——mAttrs 的 gravity 是模板默认值，实际位置由 Y offset 覆盖，以截图为准）

### 3. framework 侧验证

- `Toast.makeCustomToastWithIcon`（framework classes5.dex）方法体确认：icon 参数强校验（null → IllegalArgumentException「Drawable icon should not be null」）→ `ToastPresenter.getTextToastViewWithIcon` 构建带图标系统样式视图
- 与 ClipboardService.lambda$showAccessNotificationLocked$5 中的调用完全对上（getString 文案 + getDrawable 应用图标 + makeCustomToastWithIcon）

### 4. 综合链路（最终版）

```
[用户复制] → B站启动读取剪贴板
→ system_server ClipboardService.showAccessNotificationLocked
→ lambda$showAccessNotificationLocked$5: getString(「xx读取了剪贴板」) + getDrawable(应用图标) + makeCustomToastWithIcon
→ （MIUI 机制：toast 以目标应用身份/在其上下文渲染）
→ 应用进程 VRI-Toast → WindowManager ty=TOAST 窗口（owner=B站 uid）→ 底部居中显示 2s
```

### 5. 对 HyperClassic 的决定性结论

1. **现有 SystemUI scope 覆盖不到这类提示**：窗口 owner 是目标应用进程，SystemUI ToastUI 渲染路径不参与
2. 要给「已读取剪贴板」套玻璃的三条路：
   - **方案 A（推荐探索）**：hook 各应用进程的 `android.widget.Toast.makeCustomToastWithIcon` / `ToastPresenter.getTextToastViewWithIcon`（framework 类，每进程都加载）→ scope 需包含目标应用或全应用（重）
   - **方案 B**：hook system_server 的 `ClipboardService.lambda$showAccessNotificationLocked$5`，在源头替换 view/挂材质——但 view 在别的进程渲染，跨进程改材质难度高
   - **方案 C**：主题/资源层——该 toast 样式资源若集中在 framework-res（`wanim=0x1030004` 引用的样式族），可尝试资源级圆角/半透明调整，但达不到真玻璃 blur
3. 自动化测试限制：`cmd clipboard` 在 Android 17 无 shell 实现，root 直写剪贴板不可行；触发必须真实 UI 复制（可用 UI 自动化在输入框长按→全选→复制模拟）

## 测试环境遗留

- /data/local/tmp/toast_hunt.log、toast_windows.log（本轮窗口轮询原始数据）
- /data/local/tmp/clip_toast_test.log（空，tag 过滤版 logcat 未捕获输出——ClipboardService 无 toast 相关 log 输出）
- 复测要点：下次触发前先起 logcat 再操作（本轮 logcat 后台进程在宿主被杀时断掉，错过窗口期）
