package com.mersand.hyperclassic

import android.content.ContentResolver
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * HyperClassic 设置页
 *
 * 只暴露真正生效的开关：
 *   · 区域开关 → debug.hyperclassic.region_<key>，CcHooks 安装期读取
 *   · 控制中心样式 → Settings.System.hyperclassic_cc_mode
 *
 * 顶部根据版本门控（Compat.check）显示环境状态：
 *   · 非 HyperOS / HyperOS 1~3 → 模块不运行
 *   · HyperOS 4 + Android < 17 → 未测试警告，模块仍会运行
 *
 * 改动均需重启 SystemUI 生效。
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 提前建好常驻 su shell，避免第一次切换时卡顿
        Thread { RootShell.warmUp() }.start()
        setContent {
            MiuixTheme {
                // UI 进程内重算一遍门控结果（与 hook 进程同一套逻辑）
                SettingsScreen(this, Compat.check())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RootShell.close()
    }
}

/**
 * hook 健康自检卡片（v166）。
 *
 * SystemUI 进程 install() 结束时把失败层号写入 Settings.System.hyperclassic_health，
 * 这里读取后渲染一张简单卡片。仅列出失败层号，不含详细日志——
 * 详细日志仍走 LSPosed 日志（logcat -s LSPosedFramework）。
 * 无数据 / 已过期（1 小时）/ 全部正常时都不显示。
 */
@Composable
private fun HealthCard(contentResolver: ContentResolver?) {
    var failures by remember { mutableStateOf<List<String>?>(null) }
    LaunchedEffect(contentResolver) {
        failures = withContext(Dispatchers.IO) {
            contentResolver?.let { Health.readFailures(it) }
        }
    }
    val failed = failures ?: return
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .background(Color(0xFFB3261E).copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Column {
            Text("Hook 自检：部分层未安装", color = Color(0xFFB3261E))
            Text(
                failed.joinToString("、") + "。细节见 LSPosed 日志。",
                color = Color(0xFFB3261E).copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

/** 版本门控提示条（PASS 时不显示）。 */
@Composable
private fun GateBanner(gate: Compat.Result) {
    if (gate.status == Compat.PASS) {
        return
    }
    val pair = when (gate.status) {
        Compat.SKIP_NOT_HYPEROS -> Pair(
            "当前系统不是 HyperOS（${gate.device} · Android ${Build.VERSION.RELEASE}），模块不会生效",
            Color(0xFFB3261E)
        )
        Compat.REJECT_UNSUPPORTED -> Pair(
            "暂不支持 ${gate.osName}（需要 HyperOS 4 / Android 17），模块不会生效",
            Color(0xFFB3261E)
        )
        else -> Pair(
            "${gate.osName} / Android ${Build.VERSION.RELEASE} 未测试，不保证可用",
            Color(0xFF9A6700)
        )
    }
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .background(pair.second.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(pair.first, color = pair.second)
    }
}

@Composable
private fun SettingsScreen(activity: ComponentActivity, gate: Compat.Result) {
    val ctx = activity

    var ccMode by remember { mutableStateOf(ConfigStore.getCcMode(ctx)) }
    val rootOk = remember { RootShell.hasRoot() }

    // 区域开关状态：一次异步读全部（每个都要跑一次 getprop，不能放重组路径）
    var regionStates by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
    var reloadKey by remember { mutableStateOf(0) }

    LaunchedEffect(reloadKey) {
        regionStates = withContext(Dispatchers.IO) {
            ConfigStore.REGIONS.associate { r ->
                r.key to ConfigStore.isRegionOn(ctx, r.key, r.defOn)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "HyperClassic",
                actions = {
                    IconButton(onClick = {
                        Thread {
                            val ok = RootShell.restartSystemUi()
                            activity.runOnUiThread {
                                RootShell.toast(
                                    activity,
                                    if (ok) "已重启 SystemUI" else "重启失败，请确认 Root"
                                )
                            }
                        }.start()
                    }) {
                        Text("重启")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
        ) {
            GateBanner(gate)

            HealthCard(ctx.contentResolver)

            if (!rootOk) {
                SmallTitle("未授予 Root")
                Text(
                    "需要 Root 权限写入系统属性。",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // ---------------- 玻璃区域 ----------------
            SmallTitle("玻璃区域")

            ConfigStore.REGIONS.forEach { region ->
                // 未读到状态前先显示默认值，避免开关闪烁
                val checked = regionStates[region.key] ?: region.defOn
                SwitchPreference(
                    title = region.title,
                    summary = region.desc,
                    checked = checked,
                    onCheckedChange = { v ->
                        // 先更新界面（立即响应），再后台落属性
                        regionStates = regionStates + (region.key to v)
                        Thread {
                            val ok = ConfigStore.setRegionOn(ctx, region.key, v)
                            activity.runOnUiThread {
                                RootShell.toast(
                                    activity,
                                    if (ok) "重启 SystemUI 生效" else "写入失败，请确认 Root"
                                )
                            }
                        }.start()
                    }
                )
            }

            // ---------------- 控制中心样式 ----------------
            SmallTitle("控制中心样式")

            listOf(
                ConfigStore.CC_MODE_FOLLOW to "跟随系统",
                ConfigStore.CC_MODE_CLASSIC to "经典二合一",
                ConfigStore.CC_MODE_NEW to "新版控制中心"
            ).forEach { (mode, label) ->
                SwitchPreference(
                    title = label,
                    summary = "",
                    checked = ccMode == mode,
                    onCheckedChange = { checked ->
                        if (checked) {
                            ccMode = mode
                            Thread {
                                val ok = ConfigStore.setCcMode(ctx, mode)
                                activity.runOnUiThread {
                                    RootShell.toast(
                                        activity,
                                        if (ok) "重启 SystemUI 生效" else "写入失败，请确认 Root"
                                    )
                                }
                            }.start()
                        }
                    }
                )
            }

            IconButton(onClick = { reloadKey++ }) {
                Text("刷新")
            }
        }
    }
}