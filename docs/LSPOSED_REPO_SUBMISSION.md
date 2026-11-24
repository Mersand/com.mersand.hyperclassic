# 上架 LSPosed-Modules-Repo 指南

> 日期：2026-11-25
> 目标仓库：https://github.com/Xposed-Modules-Repo/submission
> 展示站点：https://modules.lsposed.org

---

## 一、前置条件：Application ID 规则

官方要求（README 原文）：

```
### Application ID requirement
- For reverse domain name notation in your application id, please use something
  you own rather than random domain.
- You are require to set a TXT record for your root domain:
    lsposed-modules-repo-verification=your_github_username
- If you do not own any domain, you may use io.github.{username} prefix
```

有两条路：

| 方案 | 前提 | 是否需 DNS |
|---|---|---|
| A. 自有域名反写（如 `com.example.hyperclassic`） | 你**真的拥有**该根域名 | ✅ 必须加 TXT 记录 |
| B. `io.github.{username}` 前缀（如 `io.github.mersand.hyperclassic`） | 无 | ❌ 不需要 |

### 本项目选择：方案 B

设备上 `mersand.com` 虽能返回 HTTP 302，但**没有 A/AAAA 记录**，说明并非自有可控域名，
因此改用 `io.github.mersand.hyperclassic`。

---

## 二、包名迁移记录（v2.1.0 → v2.1.1）

### 变更内容

| 项目 | 旧值 | 新值 |
|---|---|---|
| `applicationId` | `com.mersand.hyperclassic` | `io.github.mersand.hyperclassic` |
| `namespace` | `com.mersand.hyperclassic` | `io.github.mersand.hyperclassic` |
| 源码目录 | `src/main/java/com/mersand/hyperclassic/` | `src/main/java/io/github/mersand/hyperclassic/` |
| 广播 action | `com.mersand.hyperclassic.TEST_HUN` | `io.github.mersand.hyperclassic.TEST_HUN` |
| `java_init.list` | `com.mersand.hyperclassic.ModuleMain` | `io.github.mersand.hyperclassic.ModuleMain` |

**注意**：GitHub 仓库名 `com.mersand.hyperclassic` **不需要改**——仓库名与 APK 包名无关。

### 迁移步骤（已执行）

```bash
# 1. 备份
tar czf /tmp/hc_pre_rename_$(date +%Y%m%d_%H%M%S).tar.gz src/ *.kts *.properties

# 2. 移动源码目录
mkdir -p src/main/java/io/github/mersand
git mv src/main/java/com/mersand/hyperclassic src/main/java/io/github/mersand/hyperclassic
rmdir src/main/java/com/mersand src/main/java/com

# 3. 全局替换（排除 .bak）
find src -type f \( -name '*.java' -o -name '*.kt' -o -name '*.xml' \
    -o -name '*.list' -o -name '*.prop' \) ! -name '*.bak' \
  | xargs sed -i 's/com\.mersand\.hyperclassic/io.github.mersand.hyperclassic/g'
sed -i 's/com\.mersand\.hyperclassic/io.github.mersand.hyperclassic/g' build.gradle.kts

# 4. 删除旧备份
find src -name '*.bak' -delete

# 5. 构建验证
./gradlew clean assembleDebug --offline
```

### 设备侧迁移（已执行）

1. 安装新包 → LSPosed 自动识别为新模块
2. 在 LSPosed 管理器里给新包启用 + 勾选 `com.android.systemui`
3. 确认 SystemUI 日志出现新包名 → 卸载旧包

> ⚠️ 不要直接改 LSPosed 的 `modules_config.db`：
> 设备上有 29+ 个模块共存，改库一旦出错影响面极大。用管理器 UI 更安全。

---

## 三、提交上架流程

### 步骤 1：开 issue 申请仓库

地址：https://github.com/Xposed-Modules-Repo/submission/issues/new

```
标题：[submission] io.github.mersand.hyperclassic

内容示例：
模块名称：HyperClassic
用途：为 HyperOS 4 解锁 AOSP 式 split shade（横屏双栏控制中心），
     并补齐柔光玻璃渲染、HUN 位置与动画修复。
适配：HyperOS 4 / Android 17（Redmi K80 Pro 实测）
```

Bot 会自动：
- 创建仓库 `Xposed-Modules-Repo/io.github.mersand.hyperclassic`
- **邀请提交者成为 maintainer**

### 步骤 2：配置仓库元信息

进入新仓库，设置：

| 位置 | 填写内容 |
|---|---|
| **Repository Title** | `io.github.mersand.hyperclassic`（官方要求填包名） |
| **Description** | `HyperClassic`（模块显示名） |
| **Home Page** | `https://github.com/Mersand/com.mersand.hyperclassic`（源码仓库） |

⚠️ 官方特别注明：**Repository Description 无法通过 API 更新**，必须在浏览器里改。

### 步骤 3：创建两个文件

#### `SUMMARY`（首页卡片显示的一句话简介）

```
为 HyperOS 4 解锁 AOSP 式双栏控制中心，并修复柔光玻璃渲染、HUN 位置与动画
```

#### `README.md`（详情页完整说明）

直接复制本仓库 `README.md` 内容即可。
**不要上传源代码**——官方只要 APK 与说明。

### 步骤 4：发布 Release（格式是硬性要求）

| 字段 | 格式 | 示例 |
|---|---|---|
| **Tag** | `[versionCode]-[versionName]` | `20101-2.1.1` |
| **Release Title** | versionName | `2.1.1` |
| **Release Content** | changelog | 见下方模板 |
| **Assets** | **必须附 APK** ⚠️ | `HyperClassic-release.apk` |

#### 关键细节

   1. **Tag 格式必须严格** `版本号-版本名`，用连字符 `-` 分隔。
      本项目历史 tag `v2.0` / `v2.1.0` 均为源码仓库自用标记，**不符合仓库要求**；
      若要在 LSPosed 仓库发版，需使用 `20100-2.1.0` / `20101-2.1.1` 这类格式。

2. **必须用 release（带 APK）触发**。
   如果之后只替换 APK 文件而不重新发 release，
   bot **无法感知**（GitHub 的 release asset 变更没有 webhook），tag 不会更新。

3. release APK 建议直接用 `./gradlew assembleRelease` 的产物。

### 步骤 5：等待展示

- 配置完整后自动触发 [build workflow](https://github.com/Xposed-Modules-Repo/modules/actions/workflows/build.yml)
- **3 个工作日内**出现在 https://modules.lsposed.org
- 如果仓库信息不完整，**不会显示**

---

## 四、后续发版流程

每次发新版只需：

```bash
# 1. 改 build.gradle.kts
#    versionCode = 20101
#    versionName = "2.1.1"

# 2. 构建
./gradlew assembleRelease --offline

# 3. 在 Xposed-Modules-Repo 仓库发 release
#    Tag:  20101-2.1.1
#    Title: 2.1.1
#    Asset: HyperClassic-release.apk（必须）

# 4. 源码仓库同步打 tag（可选，但推荐）
git tag -a '20101-2.1.1' -m 'v2.1.1'
hcgit push origin '20101-2.1.1'
```

---

## 五、tag 命名统一建议

为了同时满足 LSPosed 仓库要求和自己仓库的可读性：

| 场景 | 格式 | 说明 |
|---|---|---|
| **LSPosed 仓库**（必须） | `20101-2.1.1` | 硬性要求 |
| 源码仓库（推荐也统一） | `20101-2.1.1` | 避免两套命名 |
| 历史遗留 | `v2.0` / `v2.1.0` | 保留但不删除 |

---

## 六、检查清单

发版前逐项确认：

- [ ] `applicationId` 为 `io.github.mersand.hyperclassic`
- [ ] `versionCode` 已递增
- [ ] `versionName` 已更新
- [ ] `./gradlew assembleRelease` 成功
- [ ] release APK 内含 `resources.arsc` + `AndroidManifest.xml`（本项目 AGP 9 有坑，务必验证）
- [ ] release APK 内 `ic_launcher` 资源存在（22 个）
- [ ] APK 能正常安装
- [ ] 设备实测功能正常
- [ ] Xposed-Modules-Repo 仓库发 release，**Tag 用 `版本号-版本名`**
- [ ] release **附 APK 文件**
- [ ] SUMMARY / README 内容是最新的

---

## 七、常见问题

### Q: release APK 装不上 / 打开闪退
AGP 9.1.0 有资源优化 bug，会产出缺少 `resources.arsc` 的残包。
必须在 `gradle.properties` 里加：
```properties
android.enableResourceOptimizations=false
```
验证：
```bash
python3 -c "import zipfile; n=zipfile.ZipFile('HyperClassic-release.apk').namelist(); print('resources.arsc' in n, 'AndroidManifest.xml' in n)"
```

### Q: 提交后一直不显示
1. 检查 `SUMMARY` 和 `README.md` 是否都存在且非空
2. 检查 release tag 格式是否为 `版本号-版本名`
3. 检查 release 是否附了 APK
4. 看 [build workflow](https://github.com/Xposed-Modules-Repo/modules/actions/workflows/build.yml) 是否报错

### Q: 想改包名怎么办
需要重新走一遍 `[submission]` 流程。
旧包可以发 `[transfer]` 或直接弃用（用户会看到两个模块）。

### Q: 域名验证的 TXT 记录怎么加
在你域名服务商的 DNS 管理页添加：
```
类型：TXT
主机记录：@（或根域）
值：lsposed-modules-repo-verification=<你的github用户名>
```
生效后用 `dig TXT 你的域名` 验证，再提交 issue 会被**自动快速审核**。
