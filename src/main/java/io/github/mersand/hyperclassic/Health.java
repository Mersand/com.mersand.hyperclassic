package io.github.mersand.hyperclassic;

import android.content.ContentResolver;
import android.provider.Settings;

/**
 * ★ v166：hook 健康登记 + 简单异常报告（与详细日志独立）
 *
 * 【设计】
 *   · SystemUI 进程在 install() 结束时，把每个 hook 的安装结果
 *     写入 Settings.System.hyperclassic_health（一行 JSON，无详细堆栈）。
 *   · 设置页启动时读该键，有异常就渲染一张自检卡片，
 *     指出「哪一层 hook 没装上」，方便快速定位问题区域。
 *   · 不输出任何详细日志——详细日志仍走 L（LSPosed 日志），两套互不干扰。
 *
 * 【值格式】
 *   ok    → 一切正常（install 正常完成、无失败项）
 *   20    → time-to-live：写入时刻的 unix 分钟 + 60（1 小时内有效）
 *   失败项 → "L1,L4,L36" 这类层号列表（逗号分隔，仅层号不含原因）
 *   合并形如 "20|L1,L36"；缺失或过期 → 视为无数据（不渲染卡片）。
 */
public final class Health {

    public static final String KEY = "hyperclassic_health";
    /** 报告有效期（分钟）。过期视为「没有数据」而不是「异常」。 */
    public static final int TTL_MIN = 60;

    private static final java.util.List<String> FAILED = new java.util.ArrayList<>();
    private static volatile boolean COLLECTING = false;
    private static volatile boolean REPORTED = false;

    private Health() {
    }

    /** install() 开始时调用：清空上一次的结果并开始收集。 */
    public static void begin() {
        synchronized (Health.class) {
            FAILED.clear();
            COLLECTING = true;
            REPORTED = false;
        }
    }

    /**
     * 登记一个 hook 项的结果。
     *
     * @param label 层号，如 "L1"；region 关闭导致的「未安装」不传（传了也不算失败）
     * @param ok    安装是否成功
     */
    public static void item(String label, boolean ok) {
        if (!COLLECTING || ok) {
            return;
        }
        synchronized (Health.class) {
            if (!FAILED.contains(label)) {
                FAILED.add(label);
            }
        }
    }

    /** install() 结束时调用：把结果写入 Settings.System（静默，绝不抛出）。 */
    public static void report() {
        String payload;
        synchronized (Health.class) {
            COLLECTING = false;
            if (REPORTED) {
                return;
            }
            REPORTED = true;
            long ttl = System.currentTimeMillis() / 60000L + TTL_MIN;
            payload = FAILED.isEmpty()
                    ? String.valueOf(ttl)
                    : ttl + "|" + joinIds(FAILED);
        }
        try {
            Object cr = getCr();
            if (cr == null) {
                return;
            }
            Settings.System.putString(
                    (ContentResolver) cr, KEY, payload);
        } catch (Throwable ignored) {
        }
    }

    /** UI 进程读取：返回失败层号列表；null = 无数据/已过期/一切正常。 */
    public static java.util.List<String> readFailures(ContentResolver cr) {
        try {
            String raw = Settings.System.getString(cr, KEY);
            if (raw == null || raw.isEmpty()) {
                return null;
            }
            int bar = raw.indexOf('|');
            long ttl;
            String ids;
            if (bar < 0) {
                ttl = Long.parseLong(raw.trim());
                ids = "";
            } else {
                ttl = Long.parseLong(raw.substring(0, bar).trim());
                ids = raw.substring(bar + 1);
            }
            if (System.currentTimeMillis() / 60000L > ttl) {
                return null; // 过期
            }
            if (ids.isEmpty()) {
                return null; // 正常
            }
            java.util.List<String> out = new java.util.ArrayList<>();
            for (String s : ids.split(",")) {
                s = s.trim();
                if (!s.isEmpty()) {
                    out.add(s);
                }
            }
            return out.isEmpty() ? null : out;
        } catch (Throwable t) {
            return null;
        }
    }

    private static String joinIds(java.util.List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    private static ContentResolver getCr() {
        try {
            Class<?> at = Class.forName("android.app.ActivityThread");
            Object ctx = at.getMethod("currentApplication").invoke(null);
            if (ctx == null) {
                Object th = at.getMethod("currentActivityThread").invoke(null);
                if (th != null) {
                    ctx = at.getMethod("getSystemContext").invoke(th);
                }
            }
            if (ctx == null) {
                return null;
            }
            return (ContentResolver) ctx.getClass()
                    .getMethod("getContentResolver").invoke(ctx);
        } catch (Throwable t) {
            return null;
        }
    }
}