package io.github.mersand.hyperclassic

import android.content.Context
import android.content.SharedPreferences

/**
 * 配置存储（v10.2 重写）
 *
 * ★★ 为什么要重写 ★★
 *
 *   旧版的问题（主人原话：「前端的选项大部分是死代码」）：
 *
 *     ① 「解锁玻璃材质」开关只写 SharedPreferences，
 *        SystemUI 侧 CcHooks 从来没读过它 → 纯死代码。
 *
 *     ② 对比度 / 偏光色 / 模糊半径 / 底衬 四个滑块
 *        写的是 debug.hyperclassic.glass_* 属性，但玻璃真正的
 *        反射比来自 42 项参数数组（index 0 = 反射比），
 *        这几个属性只在少数旧路径里被读到 → 大部分场景无效。
 *
 *     ③ 「控制中心样式」三选一：这项是真生效的（写 Settings.System），
 *        但它属于另一个功能，不属于「玻璃」。
 *
 * ★★ 新设计：只控制「哪个区域的玻璃是否开关」★★
 *
 *   每个区域一个布尔开关，值落到系统属性 debug.hyperclassic.region_<name>，
 *   取值 "1"（开）/ "0"（关），默认全开。
 *
 *   CcHooks.install 在装各层 hook 前调 regionOn(name)，
 *   关掉就整层不装 —— 这是真正的开关，不是装饰。
 *
 *   区域与 CcHooks.REGION_* 常量必须一一对应。
 */
object ConfigStore {

    private const val SP_NAME = "hyperclassic_config"

    /** 系统属性前缀，必须与 CcHooks.regionOn 保持一致。 */
    private const val PROP_REGION_PREFIX = "debug.hyperclassic.region_"

    /**
     * 区域定义。
     *
     * @param key    属性后缀，必须与 CcHooks.REGION_* 常量一致
     * @param title  显示名
     * @param desc   说明
     * @param defOn  默认是否开启
     */
    data class Region(
        val key: String,
        val title: String,
        val desc: String,
        val defOn: Boolean = true
    )

    /**
     * 全部区域（顺序即界面顺序）。
     *
     * ★ 这里的 key 必须和 CcHooks 里 REGION_* 常量的字符串值完全相同：
     *     REGION_NOTIF   = "notif"
     *     REGION_HEADSUP = "headsup"
     *     REGION_MEDIA   = "media"
     *     REGION_BUTTON  = "button"
     *     REGION_QS      = "qs"
     *     REGION_SLIDER  = "slider"
     */
    val REGIONS: List<Region> = listOf(
        Region("notif", "通知卡片", "下拉面板里的通知背景玻璃"),
        Region("headsup", "悬浮通知", "顶部弹出的横幅通知"),
        Region("media", "媒体卡片", "媒体播放控制卡片的背景"),
        Region("button", "通知内按钮", "展开 / 收起、清除等圆形按钮"),
        Region("qs", "控制中心磁贴", "快捷开关图标的背景"),
        Region("slider", "亮度条", "亮度滑块及其容器"),
        Region("toast", "Toast", "系统 Toast 提示框的玻璃")
    )

    // ---------------- 区域开关 ----------------

    /** 读某区域是否开启。优先系统属性（SystemUI 真正读的那个），SP 只是本地缓存。 */
    fun isRegionOn(context: Context, key: String, defOn: Boolean = true): Boolean {
        val prop = RootShell.getProp(PROP_REGION_PREFIX + key).trim()
        if (prop == "0") return false
        if (prop == "1") return true
        // 属性还没写过 → 用本地缓存，再退回默认值
        return prefs(context).getBoolean("region_" + key, defOn)
    }

    /** 写某区域开关：同时落 SP（给界面恢复）和系统属性（给 SystemUI 读）。 */
    fun setRegionOn(context: Context, key: String, on: Boolean): Boolean {
        prefs(context).edit().putBoolean("region_" + key, on).apply()
        return RootShell.setProp(PROP_REGION_PREFIX + key, if (on) "1" else "0")
    }

    /** 把所有区域的当前值一次性写回系统属性（首次启动 / 重启后同步用）。 */
    fun syncAllRegions(context: Context) {
        REGIONS.forEach { r ->
            val on = prefs(context).getBoolean("region_" + r.key, r.defOn)
            RootShell.setProp(PROP_REGION_PREFIX + r.key, if (on) "1" else "0")
        }
    }

    fun prefs(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

    // ==================================================================
    // 控制中心样式（保留：这项是真生效的，写 Settings.System）
    // ==================================================================

    const val KEY_CC_MODE = "cc_mode"

    /** 控制中心样式：跟随系统设置（不干预） */
    const val CC_MODE_FOLLOW = 0

    /** 强制经典控制中心 */
    const val CC_MODE_CLASSIC = -1

    /** 强制新版控制中心 */
    const val CC_MODE_NEW = 1

    /** 控制中心模式的设置键，必须与 CcHooks.KEY_CC_MODE 保持一致 */
    const val KEY_SYSTEM_CC_MODE = "hyperclassic_cc_mode"

    /**
     * 读当前控制中心模式。
     *
     * ★ 为什么不直接用 SharedPreferences：
     *   SystemUI 和本 App 是两个进程，SP 跨进程不可靠。
     *   真正生效的是 Settings.System 的 hyperclassic_cc_mode（CcHooks.readCcMode 读它）。
     *   SP 只是用来在控件上快速恢复显示。
     */
    fun getCcMode(context: Context): Int {
        val fromSystem = RootShell.getSettingInt(KEY_SYSTEM_CC_MODE, CC_MODE_FOLLOW)
        if (fromSystem == CC_MODE_CLASSIC || fromSystem == CC_MODE_NEW) {
            return fromSystem
        }
        return prefs(context).getInt(KEY_CC_MODE, CC_MODE_FOLLOW)
    }

    /** 写控制中心模式：SP 存一份 + 落 Settings.System（跨进程真正生效）。 */
    fun setCcMode(context: Context, mode: Int): Boolean {
        prefs(context).edit().putInt(KEY_CC_MODE, mode).apply()
        return if (mode == CC_MODE_FOLLOW) {
            RootShell.deleteSetting(KEY_SYSTEM_CC_MODE).first
        } else {
            RootShell.putSettingInt(KEY_SYSTEM_CC_MODE, mode).first
        }
    }
}