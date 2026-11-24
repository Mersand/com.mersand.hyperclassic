package io.github.mersand.hyperclassic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 首页的状态卡片组。
 *
 * 参考 HyperIsland 的首页布局：
 *   ┌──────────────┬────────────┐
 *   │  大卡片（主状态）│ 小卡片      │
 *   │                ├────────────┤
 *   │                │ 小卡片      │
 *   └──────────────┴────────────┘
 *
 * 这里左侧放「模块运行状态」，右侧两格放「区域开关数 / Hook 层数」。
 */

/** 状态语义 → 配色。 */
private data class StatusTone(val accent: Color, val light: Color)

/** 状态语义 → 配色。
 *
 * ★ 注意：toneOf 里要读 MiuixTheme.colorScheme（@Composable），
 *   所以它本身也必须标 @Composable，否则编译报
 *   "Functions which invoke @Composable functions must be marked with @Composable"。
 */
@Composable
private fun toneOf(kind: Tone): StatusTone = when (kind) {
    Tone.OK -> StatusTone(Color(0xFF2E7D32), Color(0xFFD8F3DC))
    Tone.WARN -> StatusTone(Color(0xFF9A6700), Color(0xFFFDF3D3))
    Tone.ERROR -> StatusTone(Color(0xFFB3261E), Color(0xFFFADAD7))
    Tone.IDLE -> StatusTone(MiuixTheme.colorScheme.onSurfaceVariantSummary, MiuixTheme.colorScheme.surface)
}

/** 状态语义。
 *
 * ★ 必须是 public：MetricCard 是 public 且以它作参数类型，
 *   用 private 会报 "'public' function exposes its 'private-in-file' parameter type"。
 */
enum class Tone { OK, WARN, ERROR, IDLE }

/** 圆角容器（miuix 没有 Card，这里用 Box + 圆角背景自己搭）。 */
@Composable
private fun CardBox(
    modifier: Modifier = Modifier,
    color: Color = MiuixTheme.colorScheme.surfaceContainer,
    content: @Composable androidx.compose.foundation.layout.BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(color, RoundedCornerShape(20.dp))
            .padding(16.dp),
        content = content
    )
}

/** 首页顶部：模块总体状态大卡片。 */
@Composable
fun ModuleStatusCard(
    gate: Compat.Result,
    rootOk: Boolean,
    activeRegions: Int,
    totalRegions: Int,
    ccMode: Int,
    hookFailures: List<String>?,
    modifier: Modifier = Modifier
) {
    // 综合判定
    val broken = gate.status == Compat.SKIP_NOT_HYPEROS || gate.status == Compat.REJECT_UNSUPPORTED
    val failing = !hookFailures.isNullOrEmpty()
    val tone = when {
        broken -> Tone.ERROR
        !rootOk -> Tone.ERROR
        failing -> Tone.WARN
        gate.status != Compat.PASS -> Tone.WARN
        else -> Tone.OK
    }
    val t = toneOf(tone)

    val headline = when (tone) {
        Tone.OK -> "已激活"
        Tone.WARN -> "部分功能受限"
        Tone.ERROR -> if (broken) "无法运行" else "缺少 Root"
        Tone.IDLE -> "未知"
    }
    val sub = when {
        broken -> gate.reason.ifEmpty { "当前系统不受支持" }
        !rootOk -> "需要 Root 才能写入系统属性"
        failing -> "有 ${hookFailures!!.size} 层 hook 未安装"
        gate.status != Compat.PASS -> "${gate.osName} / Android ${gate.androidSdk} 未测试"
        else -> "${gate.osName} · ${gate.device}"
    }

    CardBox(modifier.fillMaxWidth(), color = if (tone == Tone.OK) t.light else MiuixTheme.colorScheme.surfaceContainer) {
        Column {
            Text(
                headline,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (tone == Tone.OK) t.accent else MiuixTheme.colorScheme.onSurface
            )
            Text(
                sub,
                fontSize = 14.sp,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                modifier = Modifier.padding(top = 6.dp)
            )

            // 底部：小圆点 + 环境信息
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 18.dp)
            ) {
                Box(
                    Modifier
                        .size(8.dp)
                        .background(t.accent, CircleShape)
                )
                Text(
                    ccLabel(ccMode),
                    fontSize = 13.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
    }
}

/** 首页右侧：两格小卡片。 */
@Composable
fun MetricCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    tone: Tone = Tone.IDLE
) {
    val t = toneOf(tone)
    CardBox(modifier) {
        Column {
            Text(
                label,
                fontSize = 13.sp,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
            )
            Text(
                value,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (tone == Tone.IDLE) MiuixTheme.colorScheme.onSurface else t.accent,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/** 两列布局：左大卡 + 右两小卡。 */
@Composable
fun StatusOverview(
    gate: Compat.Result,
    rootOk: Boolean,
    activeRegions: Int,
    totalRegions: Int,
    ccMode: Int,
    hookFailures: List<String>?
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        ModuleStatusCard(
            gate = gate,
            rootOk = rootOk,
            activeRegions = activeRegions,
            totalRegions = totalRegions,
            ccMode = ccMode,
            hookFailures = hookFailures
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            MetricCard(
                value = "$activeRegions",
                label = "已启用区域",
                modifier = Modifier.weight(1f),
                tone = if (activeRegions > 0) Tone.OK else Tone.IDLE
            )
            MetricCard(
                value = "$totalRegions",
                label = "区域总数",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/** 控制中心样式 → 文案。 */
fun ccLabel(mode: Int): String = when (mode) {
    ConfigStore.CC_MODE_CLASSIC -> "经典二合一"
    ConfigStore.CC_MODE_NEW -> "新版控制中心"
    else -> "跟随系统"
}

/** 一条细分隔线，用于卡片内部。 */
@Composable
fun ThinDivider(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(MiuixTheme.colorScheme.dividerLine)
    )
}
