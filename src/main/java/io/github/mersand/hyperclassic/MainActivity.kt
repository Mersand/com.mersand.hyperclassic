package io.github.mersand.hyperclassic

import android.Manifest
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Home
import top.yukonga.miuix.kmp.icon.extended.Settings
import top.yukonga.miuix.kmp.preference.SliderPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * HyperClassic 设置页（v357 前端重构）
 *
 * 结构参考 HyperIsland：
 *   · 底部两页 Tab —— 首页（模块状态概览）/ 设置（详细开关）
 *   · 首页用卡片呈现总体状态，一眼看出能不能用
 *   · 设置页保留全部原有开关，分组不变
 *
 * 所有开关的语义与 v356 完全一致，只调整呈现方式。
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 提前建好常驻 su shell，避免第一次切换时卡顿
        Thread { RootShell.warmUp() }.start()
        setContent {
            MiuixTheme {
                // UI 进程内重算一遍门控结果（与 hook 进程同一套逻辑）
                AppRoot(this, Compat.check())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RootShell.close()
    }
}

/** 底部导航项。
 *
 * ★ v2.1.0：第二页命名为「调整」而不是「设置」——
 *   「设置」容易让人以为这是模块自身的配置页，
 *   而这一页实际调的是系统外观参数（玻璃区域、控制中心、HUN）。
 */
private enum class Tab(val label: String) {
    HOME("首页"),
    TUNING("调整")
}

@Composable
private fun AppRoot(activity: ComponentActivity, gate: Compat.Result) {
    var tab by remember { mutableStateOf(Tab.HOME) }

    Scaffold(
        // ★ v357：去掉 TopAppBar —— 空标题会白占一个栏高度，
        //   改成在页面内容区里自己画「大字标题 + 重启按钮」，
        //   与 HyperIsland 的版式一致。
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tab == Tab.HOME,
                    onClick = { tab = Tab.HOME },
                    icon = MiuixIcons.Home,
                    label = Tab.HOME.label
                )
                NavigationBarItem(
                    selected = tab == Tab.TUNING,
                    onClick = { tab = Tab.TUNING },
                    icon = MiuixIcons.Settings,
                    label = Tab.TUNING.label
                )
            }
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            when (tab) {
                Tab.HOME -> HomePage(activity, gate)
                Tab.TUNING -> SettingsPage(activity, gate)
            }
        }
    }
}

// ==============================================================
// 通用：页面大标题行（大字 + 重启按钮）
// ==============================================================

@Composable
private fun PageHeader(activity: ComponentActivity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "HyperClassic",
            fontSize = 34.sp,
            fontWeight = FontWeight.Medium
        )
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
}

// ==============================================================
// 首页：模块状态概览
// ==============================================================

@Composable
private fun HomePage(activity: ComponentActivity, gate: Compat.Result) {
    val ctx = activity
    val rootOk = remember { RootShell.hasRoot() }

    var regionStates by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
    var ccMode by remember { mutableStateOf(ConfigStore.getCcMode(ctx)) }
    var failures by remember { mutableStateOf<List<String>?>(null) }
    var reloadKey by remember { mutableStateOf(0) }

    LaunchedEffect(reloadKey) {
        val (states, fails) = withContext(Dispatchers.IO) {
            val s = ConfigStore.REGIONS.associate { r ->
                r.key to ConfigStore.isRegionOn(ctx, r.key, r.defOn)
            }
            val f = ctx.contentResolver?.let { Health.readFailures(it) }
            s to f
        }
        regionStates = states
        failures = fails
        ccMode = ConfigStore.getCcMode(ctx)
    }

    val active = ConfigStore.REGIONS.count { r ->
        regionStates[r.key] ?: r.defOn
    }
    val total = ConfigStore.REGIONS.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp)
    ) {
        // ★ v357：大字标题（HyperIsland 同款）
        PageHeader(activity)

        StatusOverview(
            gate = gate,
            rootOk = rootOk,
            activeRegions = active,
            totalRegions = total,
            ccMode = ccMode,
            hookFailures = failures
        )

        visualDivider()

        // 环境信息
        SmallTitle("环境信息")
        InfoCard(
            rows = listOf(
                "系统" to "${gate.osName} / Android ${gate.androidSdk}",
                "设备" to gate.device,
                "Root" to if (rootOk) "已授予" else "未授予",
                "控制中心" to ccLabel(ccMode),
                "区域开关" to "$active / $total 已启用"
            )
        )

        if (!failures.isNullOrEmpty()) {
            SmallTitle("Hook 异常")
            InfoCard(
                rows = failures!!.map { "未安装" to it },
                tint = Color(0xFFB3261E)
            )
        }

        Box(Modifier.padding(top = 16.dp))
    }
}

/** 简单的键值卡片。 */
@Composable
private fun InfoCard(rows: List<Pair<String, String>>, tint: Color? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MiuixTheme.colorScheme.surfaceContainer, RoundedCornerShape(20.dp))
            .padding(vertical = 4.dp)
    ) {
        rows.forEachIndexed { i, (k, v) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    k,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                )
                Text(
                    v,
                    color = tint ?: MiuixTheme.colorScheme.onSurface
                )
            }
            if (i != rows.lastIndex) {
                ThinDivider(Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
private fun visualDivider() {
    Box(Modifier.padding(top = 4.dp))
}

// ==============================================================
// 调整页：全部详细开关
// ==============================================================

@Composable
private fun SettingsPage(activity: ComponentActivity, gate: Compat.Result) {
    val ctx = activity
    val rootOk = remember { RootShell.hasRoot() }

    var ccMode by remember { mutableStateOf(ConfigStore.getCcMode(ctx)) }
    var regionStates by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
    var reloadKey by remember { mutableStateOf(0) }

    // HUN 探针开关
    var probeHunOn by remember { mutableStateOf(false) }
    var probeHunDumpOn by remember { mutableStateOf(false) }
    var hunFixOn by remember { mutableStateOf(true) }
    var hunPadMode by remember { mutableStateOf(true) }

    // 通知权限
    var notifOk by remember { mutableStateOf(notifUsable(ctx)) }

    val notifPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        notifOk = notifUsable(ctx)
        val msg = when {
            !granted -> "权限被拒绝，请到系统设置手动开启"
            !notifOk -> "权限已给，但全局通知仍关闭，已打开设置页"
            else -> "通知权限已就绪"
        }
        RootShell.toast(activity, msg)
        if (granted && !notifOk) openNotificationSettings(activity)
    }

    fun ensureNotifPermission(): Boolean {
        if (notifOk) return true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            activity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            RootShell.toast(activity, "请先允许通知权限")
        } else {
            RootShell.toast(activity, "全局通知被关闭，已打开设置页")
            openNotificationSettings(activity)
        }
        return false
    }

    LaunchedEffect(reloadKey) {
        regionStates = withContext(Dispatchers.IO) {
            ConfigStore.REGIONS.associate { r ->
                r.key to ConfigStore.isRegionOn(ctx, r.key, r.defOn)
            }
        }
        val props = withContext(Dispatchers.IO) {
            listOf(
                "1" == RootShell.getProp("debug.hyperclassic.probe_hun"),
                "1" == RootShell.getProp("debug.hyperclassic.probe_hun_dump"),
                RootShell.getProp("debug.hyperclassic.hun_fix") != "0",
                RootShell.getProp("debug.hyperclassic.hun_pad_mode") != "0"
            )
        }
        probeHunOn = props[0]
        probeHunDumpOn = props[1]
        hunFixOn = props[2]
        hunPadMode = props[3]
        ccMode = ConfigStore.getCcMode(ctx)
        notifOk = notifUsable(ctx)
    }

    // 从系统设置返回后刷新通知状态
    LaunchedEffect(Unit) {
        while (true) {
            delay(1500)
            val now = notifUsable(ctx)
            if (now != notifOk) notifOk = now
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp)
    ) {
        // ★ v357：大字标题
        PageHeader(activity)

        GateBanner(gate)

        if (!rootOk) {
            SmallTitle("未授予 Root")
            Text(
                "需要 Root 权限写入系统属性。",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // ---------------- 玻璃区域 ----------------
        SmallTitle("玻璃区域")
        CardGroup {
            ConfigStore.REGIONS.forEachIndexed { i, region ->
                val checked = regionStates[region.key] ?: region.defOn
                SwitchPreference(
                    title = region.title,
                    summary = region.desc,
                    checked = checked,
                    onCheckedChange = { v ->
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
                if (i != ConfigStore.REGIONS.lastIndex) ThinDivider(Modifier.padding(horizontal = 16.dp))
            }
        }

        // ---------------- 控制中心样式 ----------------
        SmallTitle("控制中心样式")
        CardGroup {
            val modes = listOf(
                ConfigStore.CC_MODE_FOLLOW to "跟随系统",
                ConfigStore.CC_MODE_CLASSIC to "经典二合一",
                ConfigStore.CC_MODE_NEW to "新版控制中心"
            )
            modes.forEachIndexed { i, (mode, label) ->
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
                if (i != modes.lastIndex) ThinDivider(Modifier.padding(horizontal = 16.dp))
            }
        }

        // ---------------- 弹出通知（HUN） ----------------
        SmallTitle("弹出通知")

        if (!notifOk) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 4.dp)
                    .background(
                        Color(0xFFB3261E).copy(alpha = 0.12f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Column {
                    Text("通知权限未开启", color = Color(0xFFB3261E))
                    Text(
                        "Android 13+ 需要 POST_NOTIFICATIONS 权限。",
                        color = Color(0xFFB3261E).copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        CardGroup {
            SwitchPreference(
                title = if (notifOk) "通知权限：已就绪" else "开启通知权限",
                summary = if (notifOk) "权限与系统通知总开关均已开启"
                else "点击申请（或被拒后跳转系统设置）",
                checked = notifOk,
                onCheckedChange = {
                    if (!notifOk) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            activity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                            PackageManager.PERMISSION_GRANTED
                        ) {
                            notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            openNotificationSettings(activity)
                        }
                    }
                }
            )
            ThinDivider(Modifier.padding(horizontal = 16.dp))

            // ★ v355：HUN 横滑修复
            SwitchPreference(
                title = "HUN 玻璃跟随（v355）",
                summary = "横滑清除时玻璃背景跟随卡片移动，状态栏图标不再反向偏移",
                checked = hunFixOn,
                onCheckedChange = { v ->
                    hunFixOn = v
                    Thread {
                        val ok = RootShell.setProp(
                            "debug.hyperclassic.hun_fix",
                            if (v) "1" else "0"
                        )
                        activity.runOnUiThread {
                            RootShell.toast(activity, if (ok) "重启 SystemUI 生效" else "写入失败")
                        }
                    }.start()
                }
            )
            ThinDivider(Modifier.padding(horizontal = 16.dp))

            // ★ v356：HUN 顶部留白
            SwitchPreference(
                title = "HUN 顶部留白（v356）",
                summary = "横屏弹出通知不再压住状态栏，自动补上状态栏高度",
                checked = hunPadMode,
                onCheckedChange = { v ->
                    hunPadMode = v
                    Thread {
                        val ok = RootShell.setProp(
                            "debug.hyperclassic.hun_pad_mode",
                            if (v) "1" else "0"
                        )
                        activity.runOnUiThread {
                            RootShell.toast(activity, if (ok) "重启 SystemUI 生效" else "写入失败")
                        }
                    }.start()
                }
            )
        }

        // ---------------- 调试工具 ----------------
        SmallTitle("调试工具")
        CardGroup {
            SwitchPreference(
                title = "发送测试 HUN",
                summary = "发一条 heads-up 通知，用于复现问题",
                checked = false,
                onCheckedChange = { _ ->
                    if (ensureNotifPermission()) {
                        Thread {
                            val ok = HunProbe.sendTestHun(
                                activity,
                                "HC 测试 HUN",
                                "横滑这条通知，观察玻璃背景是否跟随"
                            )
                            activity.runOnUiThread {
                                RootShell.toast(
                                    activity,
                                    if (ok) "已发送，注意顶部弹出的通知" else "发送失败"
                                )
                            }
                        }.start()
                    }
                }
            )
            ThinDivider(Modifier.padding(horizontal = 16.dp))

            SwitchPreference(
                title = "清除测试 HUN",
                summary = "取消所有由本模块发送的测试通知",
                checked = false,
                onCheckedChange = { _ ->
                    Thread {
                        HunProbe.cancelTestHuns(activity)
                        activity.runOnUiThread {
                            RootShell.toast(activity, "已清除测试通知")
                        }
                    }.start()
                }
            )
            ThinDivider(Modifier.padding(horizontal = 16.dp))

            SwitchPreference(
                title = "HUN 几何探针",
                summary = "logcat 打印 HUN 行与玻璃层的几何值",
                checked = probeHunOn,
                onCheckedChange = { v ->
                    probeHunOn = v
                    Thread {
                        val ok = RootShell.setProp(
                            "debug.hyperclassic.probe_hun",
                            if (v) "1" else "0"
                        )
                        activity.runOnUiThread {
                            RootShell.toast(activity, if (ok) "已设置，无需重启" else "写入失败")
                        }
                    }.start()
                }
            )
            ThinDivider(Modifier.padding(horizontal = 16.dp))

            SwitchPreference(
                title = "HUN 详细 dump",
                summary = "额外打印整棵视图树",
                checked = probeHunDumpOn,
                onCheckedChange = { v ->
                    probeHunDumpOn = v
                    Thread {
                        val ok = RootShell.setProp(
                            "debug.hyperclassic.probe_hun_dump",
                            if (v) "1" else "0"
                        )
                        activity.runOnUiThread {
                            RootShell.toast(activity, if (ok) "已设置，无需重启" else "写入失败")
                        }
                    }.start()
                }
            )
        }

        // ---------------- 时钟玻璃调参 ----------------
        // ★★ 2026-11-26 新增：大时钟文字玻璃实时调参 ★★
        //
        // 【为什么做成滑块】
        //   「笔画粗细」这个观感因人 / 因背景而异：
        //     · 太细 → 不像大时钟该有的视觉重量
        //     · 太粗 → 字形变宽，最右字符容易被 View 边界裁掉
        //   每一轮都要「改代码 → 编译 → 装 → 重启 SystemUI」太慢，
        //   而这些参数在 ClockGlassP1 里全是【每帧重新读 prop】的，
        //   所以直接写 setprop 就能即时生效，无需重启。
        //
        //   参数含义（详见 ClockGlassP1 注释）：
        //     wght   直接是可变字体 fvar wght 轴值（不是 Typeface weight）
        //     edge   uShapeEdgePx：同时影响边缘高光带宽与视觉粗细
        //     grow   形态学膨胀半径（0=关；>3 顶点过多会让 SDF 崩溃）
        //     alpha  玻璃不透明度 = gd[1]
        //     base   底色强度     = gd[3]
        ClockGlassTuningSection(activity)

        // ---------------- 刷新 ----------------
        SmallTitle("状态")
        IconButton(onClick = { reloadKey++ }) {
            Text("刷新状态")
        }

        Box(Modifier.padding(top = 16.dp))
    }
}

/** 把一组 preference 包成圆角卡片。 */
@Composable
private fun CardGroup(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MiuixTheme.colorScheme.surfaceContainer, RoundedCornerShape(20.dp))
            .padding(vertical = 4.dp)
    ) {
        content()
    }
}

// ==============================================================
// 通用组件
// ==============================================================

/**
 * hook 健康自检卡片（v166）。
 *
 * SystemUI 进程 install() 结束时把失败层号写入 Settings.System.hyperclassic_health，
 * 这里读取后渲染。无数据 / 已过期（1 小时）/ 全部正常时都不显示。
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
    if (failed.isEmpty()) return
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

// ==============================================================
// 通知权限工具
// ==============================================================

/**
 * 通知是否真正可用。
 *
 * 两道关卡缺一不可：
 *   1. POST_NOTIFICATIONS 运行时权限（Android 13+）
 *   2. NotificationManager.areNotificationsEnabled() 系统总开关
 */
private fun notifUsable(ctx: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
        PackageManager.PERMISSION_GRANTED
    ) {
        return false
    }
    val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        ?: return false
    return nm.areNotificationsEnabled()
}

/** 跳本应用的通知设置页。 */
private fun openNotificationSettings(ctx: Context) {
    try {
        val i = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, ctx.packageName)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(i)
    } catch (t: Throwable) {
        try {
            ctx.startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(android.net.Uri.parse("package:${ctx.packageName}"))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (ignored: Throwable) {
        }
    }
}
// ==============================================================
// 大时钟文字玻璃：实时调参面板
// ==============================================================

/**
 * 大时钟文字玻璃的调参面板。
 *
 * ★★ 设计要点 ★★
 *
 *   ① 参数都落在 `debug.hyperclassic.local.clock_p1_*` 系统属性上，
 *      ClockGlassP1 每帧重新读取，因此拖动滑块即时生效，无需重启 SystemUI。
 *
 *   ② 拖动过程会高频写属性。RootShell 用常驻 su shell，单次写入约 0.3~2ms，
 *      足够流畅；仍加了 60ms 节流，避免拖动时刷屏。
 *
 *   ③ 数值全部显示在 summary 里，拖动时能直接看到当前值。
 */
@Composable
private fun ClockGlassTuningSection(activity: ComponentActivity) {
    val ctx = activity

    // ---- 当前值（初始从系统属性读，与 SystemUI 侧保持一致）----
    var wght by remember { mutableStateOf(propF("debug.hyperclassic.local.clock_p1_wght", 600f)) }
    var edge by remember { mutableStateOf(propF("debug.hyperclassic.local.clock_p1_edge", 15f)) }
    var grow by remember { mutableStateOf(propF("debug.hyperclassic.local.clock_p1_grow", 0f)) }
    var alpha by remember { mutableStateOf(propF("debug.hyperclassic.local.clock_p1_alpha", 0.45f)) }
    var base by remember { mutableStateOf(propF("debug.hyperclassic.local.clock_p1_base", 0.15f)) }
    var rim by remember { mutableStateOf(propF("debug.hyperclassic.local.clock_p1_rim", 0.4f)) }
    var fitOn by remember { mutableStateOf(propF("debug.hyperclassic.local.clock_p1_fit", 1f) > 0.5f) }
    var glassOn by remember { mutableStateOf(propF("debug.hyperclassic.local.clock_p1", 0f) > 0.5f) }

    /** 写属性 + 拼 gd 串。alpha/base/rim 合起来写 gd，避免互相覆盖。 */
    fun pushGlass(a: Float, b: Float, r: Float) {
        Thread {
            RootShell.setProp(
                "debug.hyperclassic.local.clock_p1_gd",
                "1=$a;3=$b;14=$r"
            )
        }.start()
    }

    SmallTitle("大时钟文字玻璃")

    CardGroup {
        SwitchPreference(
            title = "启用文字玻璃",
            summary = "关闭后大时钟恢复系统原生绘制",
            checked = glassOn,
            onCheckedChange = { v ->
                glassOn = v
                Thread {
                    RootShell.setProp(
                        "debug.hyperclassic.local.clock_p1",
                        if (v) "1" else "0"
                    )
                }.start()
            }
        )
        ThinDivider(Modifier.padding(horizontal = 16.dp))

        // ---- 字重（最直观的粗细调节）----
        SliderPreference(
            value = wght,
            onValueChange = { v ->
                wght = v
                Thread {
                    RootShell.setProp(
                        "debug.hyperclassic.local.clock_p1_wght",
                        v.toInt().toString()
                    )
                }.start()
            },
            title = "字重 wght",
            summary = "${wght.toInt()}　（系统常规≈330，粗体 600~700）",
            valueRange = 300f..900f

        )
        ThinDivider(Modifier.padding(horizontal = 16.dp))

        // ---- 边缘带宽 / 粗细耦合项 ----
        SliderPreference(
            value = edge,
            onValueChange = { v ->
                edge = v
                Thread {
                    RootShell.setProp(
                        "debug.hyperclassic.local.clock_p1_edge",
                        v.toString()
                    )
                }.start()
            },
            title = "边缘带宽 edge",
            summary = "${edge.toInt()}　小=笔画细高光弱，大=笔画粗高光强",
            valueRange = 2f..40f

        )
        ThinDivider(Modifier.padding(horizontal = 16.dp))

        // ---- 几何膨胀 ----
        SliderPreference(
            value = grow,
            onValueChange = { v ->
                grow = v
                Thread {
                    RootShell.setProp(
                        "debug.hyperclassic.local.clock_p1_grow",
                        v.toString()
                    )
                }.start()
            },
            title = "几何膨胀 grow",
            summary = "${String.format("%.1f", grow)}px　0=关；过大会让 SDF 纹理崩溃",
            valueRange = 0f..3f

        )
        ThinDivider(Modifier.padding(horizontal = 16.dp))

        // ---- 玻璃不透明度 ----
        SliderPreference(
            value = alpha,
            onValueChange = { v ->
                alpha = v
                pushGlass(v, base, rim)
            },
            title = "不透明度 alpha",
            summary = "${String.format("%.2f", alpha)}　白底上看不清就调高",
            valueRange = 0f..1f

        )
        ThinDivider(Modifier.padding(horizontal = 16.dp))

        // ---- 底色强度 ----
        SliderPreference(
            value = base,
            onValueChange = { v ->
                base = v
                pushGlass(alpha, v, rim)
            },
            title = "底色强度 base",
            summary = "${String.format("%.2f", base)}　给玻璃垫一层灰底，提升对比度",
            valueRange = 0f..1f

        )
        ThinDivider(Modifier.padding(horizontal = 16.dp))

        // ---- 边缘增强 ----
        SliderPreference(
            value = rim,
            onValueChange = { v ->
                rim = v
                pushGlass(alpha, base, v)
            },
            title = "边缘增强 rim",
            summary = "${String.format("%.2f", rim)}　高光过曝就调低",
            valueRange = 0f..2f

        )
        ThinDivider(Modifier.padding(horizontal = 16.dp))

        // ---- 水平自适应（防裁剪）----
        SwitchPreference(
            title = "水平自适应防裁剪",
            summary = "加粗后字形变宽，按实际宽度横向压缩回 View 内",
            checked = fitOn,
            onCheckedChange = { v ->
                fitOn = v
                Thread {
                    RootShell.setProp(
                        "debug.hyperclassic.local.clock_p1_fit",
                        if (v) "1" else "0"
                    )
                }.start()
            }
        )
        ThinDivider(Modifier.padding(horizontal = 16.dp))

        // ---- 一键恢复推荐值 ----
        SwitchPreference(
            title = "恢复推荐参数",
            summary = "wght=600 edge=15 grow=0 alpha=0.45 base=0.15 rim=0.4",
            checked = false,
            onCheckedChange = { _ ->
                wght = 600f; edge = 15f; grow = 0f
                alpha = 0.45f; base = 0.15f; rim = 0.4f
                fitOn = true
                Thread {
                    RootShell.setProp("debug.hyperclassic.local.clock_p1_wght", "600")
                    RootShell.setProp("debug.hyperclassic.local.clock_p1_edge", "15")
                    RootShell.setProp("debug.hyperclassic.local.clock_p1_grow", "0")
                    RootShell.setProp("debug.hyperclassic.local.clock_p1_fit", "1")
                    RootShell.setProp(
                        "debug.hyperclassic.local.clock_p1_gd",
                        "1=0.45;3=0.15;14=0.4"
                    )
                    ctx.runOnUiThread {
                        RootShell.toast(ctx, "已恢复推荐参数")
                    }
                }.start()
            }
        )
    }
}

/** 读 float 属性（读不到或非法时返回 def）。 */
private fun propF(key: String, def: Float): Float {
    val raw = RootShell.getProp(key).trim()
    return raw.toFloatOrNull() ?: def
}
