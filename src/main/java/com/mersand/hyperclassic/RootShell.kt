package com.mersand.hyperclassic

import android.content.Context
import android.widget.Toast
import java.io.BufferedReader
import java.io.DataOutputStream

/**
 * Root 命令执行（用于写系统属性 + 重启 SystemUI）
 *
 * ★★ 2026-10-31 15:43 重写（主人反馈「GUI 拖滑块很卡」）★★
 *
 *  旧实现每次都 fork 一个 su 进程：
 *      Runtime.getRuntime().exec("su")  →  50~200ms
 *  拖一次滑块写一次属性 = 掉一帧甚至几百毫秒。
 *
 *  新实现：常驻一个 su shell，命令通过 stdin 送入，
 *        每条约 0.3~2ms（只是写管道），快了两个数量级。
 *
 *  协议：每条命令后追加 `echo <令牌>:$?`，
 *        读到该令牌行即说明命令结束，`$?` 是退出码。
 *        这样无需 sleep，也不会把上一条的输出混进来。
 */
object RootShell {

    private var proc: Process? = null
    private var stdin: DataOutputStream? = null
    private var stdout: BufferedReader? = null
    private var seq = 0
    /** 常驻 shell 是否可用（su 被拒 / 进程死掉时置 false，退回一次性执行）。 */
    private var persistentOk = false

    /** 属性写入是否成功。注意 debug.* 属性无需重启即可被 getprop 读到。 */
    fun setProp(key: String, value: String): Boolean =
        exec("setprop $key $value").first

    /** 读属性 */
    fun getProp(key: String): String = exec("getprop $key").second.trim()

    /** 读 Settings.System 的 int（读不到返回 def） */
    fun getSettingInt(key: String, def: Int): Int {
        val raw = exec("settings get system $key").second.trim()
        return raw.toIntOrNull() ?: def
    }

    /** 写 Settings.System 的 int */
    fun putSettingInt(key: String, value: Int): Pair<Boolean, String> =
        exec("settings put system $key $value")

    /** 删除 Settings.System 的键（恢复「跟随系统」） */
    fun deleteSetting(key: String): Pair<Boolean, String> =
        exec("settings delete system $key")

    /** 重启 SystemUI（改 glass 开关等需要重建视图的项时用） */
    fun restartSystemUi(): Boolean =
        exec("pkill -f com.android.systemui").first

    /** 检查 root 可用 */
    fun hasRoot(): Boolean = exec("id").second.contains("uid=0")

    /**
     * 提前建立常驻 shell。GUI 启动时在后台线程调一次，
     * 之后拖滑块的写属性就是纯管道写入（毫秒级）。
     */
    fun warmUp() {
        try {
            ensureShell()
        } catch (_: Throwable) {
        }
    }

    /** 关闭常驻 shell（进程退出前调用）。 */
    fun close() {
        try {
            stdin?.writeBytes("exit\n")
            stdin?.flush()
        } catch (_: Throwable) {
        }
        try {
            stdin?.close()
        } catch (_: Throwable) {
        }
        try {
            stdout?.close()
        } catch (_: Throwable) {
        }
        try {
            proc?.destroy()
        } catch (_: Throwable) {
        }
        proc = null
        stdin = null
        stdout = null
        persistentOk = false
    }

    @Synchronized
    private fun ensureShell(): Boolean {
        if (persistentOk && stdin != null && proc != null) {
            return true
        }
        return try {
            // ★★ 2026-10-31 17:40 修正：
            //   KSU 的 `su -c <cmd>` 是「把 cmd 当参数直接执行」，不做 shell 展开，
            //   所以 `su -c sh` 起的是一个从 stdin 读命令的 sh —— 这部分是对的；
            //   但探测时写 `echo CCWARM:$?` 期望 shell 展开 $?，
            //   实测输出成了字面量 "CCWARM:$?"（见下方验证），
            //   导致永远读不到令牌行 → persistentOk 一直 false → 每条命令都 fork su。
            //
            //   改用不依赖 $? 的探测方式：
            //     写 "echo CCMMARK" （纯字面量），读到这一行即说明 shell 通了；
            //     再写 "id" 收集输出判断 uid=0。
            val p = Runtime.getRuntime().exec(arrayOf("su", "-c", "sh"))
            proc = p
            stdin = DataOutputStream(p.outputStream)
            stdout = p.inputStream.bufferedReader()
            val mark = "CCMMARK" + System.nanoTime()
            stdin!!.writeBytes("id\n")
            stdin!!.writeBytes("echo $mark\n")
            stdin!!.flush()
            var isRoot = false
            var gotMark = false
            var guard = 0
            while (guard++ < 40) {
                val line = stdout!!.readLine() ?: break
                if (line.trim() == mark) {
                    gotMark = true
                    break
                }
                if (line.contains("uid=0")) {
                    isRoot = true
                }
            }
            persistentOk = isRoot && gotMark
            if (!persistentOk) {
                close()
            }
            persistentOk
        } catch (t: Throwable) {
            close()
            false
        }
    }

    @Synchronized
    private fun exec(cmd: String): Pair<Boolean, String> {
        if (ensureShell()) {
            try {
                // ★ 2026-10-31 17:40：令牌不依赖 $?（KSU 的 sh 不做展开时也能工作）。
                //   用两个标记夹住输出，读到结束标记即认为执行完毕。
                val token = "CCEND" + (seq++)
                val out = stdin!!
                out.writeBytes("echo $token\n")
                out.writeBytes(cmd + "\n")
                out.writeBytes("echo $token\n")
                out.flush()

                val sb = StringBuilder()
                var phase = 0
                var ok = true
                var guard = 0
                while (guard++ < 400) {
                    val line = stdout!!.readLine() ?: break
                    if (line.trim() == token) {
                        if (phase == 0) {
                            phase = 1
                            continue
                        }
                        phase = 2
                        break
                    }
                    if (phase == 1) {
                        sb.append(line).append('\n')
                    }
                }
                if (phase == 2) {
                    return ok to sb.toString()
                }
                // 读不到结束标记 → 常驻 shell 已死，降级
                close()
            } catch (t: Throwable) {
                close()
            }
        }
        return execOnce(cmd)
    }

    /** 兜底：一次性 su 执行（只在常驻 shell 不可用时用）。 */
    private fun execOnce(cmd: String): Pair<Boolean, String> {
        var os: DataOutputStream? = null
        return try {
            val p = Runtime.getRuntime().exec("su")
            os = DataOutputStream(p.outputStream)
            os.writeBytes(cmd + "\n")
            os.writeBytes("exit\n")
            os.flush()
            val text = p.inputStream.bufferedReader().readText()
            val ok = p.waitFor() == 0
            ok to text
        } catch (t: Throwable) {
            false to ""
        } finally {
            try {
                os?.close()
            } catch (_: Throwable) {
            }
        }
    }

    fun toast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}