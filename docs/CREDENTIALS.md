# 凭据管理约定

> 目标：GitHub access token **只存在本机**，不进仓库、不进命令行、不进日志。

## 一、凭据存放位置

```
$HOME/.local/share/hyperclassic/github.credentials   # 0660 → 600，目录 700
$HOME/.local/bin/hcgit                               # 0700
```

文件格式（标准 git-credential-store 格式）：
```
https://<用户名>:<token>@github.com
```

> ⚠️ 这两个路径都在 **proot 沙箱内**，不是 `sdcard`，不进任何同步目录。

## 二、仓库侧配置（已就位）

```ini
# .git/config
[remote "origin"]
    url = https://github.com/Mersand/com.mersand.hyperclassic.git   # 不含 token
[credential]
    helper = store --file=/root/.local/share/hyperclassic/github.credentials
```

关键点：`url` **必须不含** `user:token@`，否则 token 会随 `.git/config` 被看到。

## 三、日常操作

**一律用 `hcgit`，不要直接 `git push`：**

```bash
hcgit status
hcgit add -A && hcgit commit -m "..."
hcgit push origin main
hcgit push origin v2.0
```

`hcgit` 内部：
```bash
cd "$REPO"
export GIT_TERMINAL_PROMPT=0
exec git -c credential.helper="store --file=$CRED" "$@"
```

## 四、安全检查清单

- [x] `.git/config` 的 remote url 无 token
- [x] `.git/config` 的 credential.helper 指向仓库外文件
- [x] 仓库内 `grep -r gho_` 无命中
- [x] git 全局配置无 token
- [ ] `.gitignore` 不含凭据文件（它在仓库外，无需 ignore，但加上更稳）：见下方注意事项

`.gitignore` 兜底（防止将来误放进仓库）：
```
# 凭据（防御性，正常在仓库外）
*.credentials
github.credentials
```

## 五、token 轮换

1. GitHub → Settings → Developer settings → Personal access tokens → 撤销旧 token
2. 生成新 token（建议 fine-grained，仅 `contents: read/write` 权限，限定本仓库）
3. 覆盖写入凭据文件：
```bash
printf 'https://Mersand:%s@github.com\n' '<新 token>' \
  > "$HOME/.local/share/hyperclassic/github.credentials"
chmod 600 "$HOME/.local/share/hyperclassic/github.credentials"
```
4. 验证：`hcgit ls-remote --heads origin`

## 六、如果凭据文件丢失

重新生成 token 后按「五-3」写入即可，无需改仓库配置（remote url 和 credential.helper 都已固定）。

## 七、禁止事项

- ❌ 不要把 token 写进 `git remote add/set-url` 的 URL
- ❌ 不要在 commit message / 文档 / 日志里粘贴 token
- ❌ 不要把凭据文件放到 `sdcard` 或任何外置存储
- ❌ 不要 `git push` 直接用裸 `git`（会走全局配置，未来可能被污染）
