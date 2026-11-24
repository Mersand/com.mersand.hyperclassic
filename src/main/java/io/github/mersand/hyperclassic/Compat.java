package io.github.mersand.hyperclassic;

import android.os.Build;

/**
 * 版本门控：模块只在「实测环境」上保证可用。
 *
 * 实测环境：Redmi K80 Pro · HyperOS 4.0 · Android 17（SDK 37）
 *
 * 决策表（2026-11-05 与作者确认）：
 * <pre>
 *   HyperOS 4    + Android >= 17 → PASS               完整工作
 *   HyperOS 4    + Android <  17 → PASS_UNTESTED      「未测试，不保证可用」，允许注册 hook
 *   HyperOS 1~3  + 任意 Android  → REJECT_UNSUPPORTED 「无法运行」，跳过全部 hook
 *   非 HyperOS   + 任意 Android  → SKIP_NOT_HYPEROS   「非 HyperOS」，跳过全部 hook
 * </pre>
 *
 * 判定依据：
 *   ro.mi.os.version.code  HyperOS 大版本号（OS4.0 → "4"）；缺失 = 非 HyperOS
 *   ro.build.version.sdk   Android API level（Android 17 = 37）
 */
public final class Compat {

    public static final int PASS = 0;
    public static final int PASS_UNTESTED = 1;
    public static final int REJECT_UNSUPPORTED = 2;
    public static final int SKIP_NOT_HYPEROS = 3;

    /** 实测环境的 Android API level（Android 17）。 */
    public static final int TESTED_SDK = 37;
    /** 实测环境的 HyperOS 大版本。 */
    public static final int TESTED_OS = 4;
    /** 实测设备。 */
    public static final String TESTED_DEVICE = "Redmi K80 Pro";

    public static final class Result {
        public final int status;
        public final int androidSdk;
        public final int osCode;
        public final String osName;
        public final String device;
        public final String reason;

        Result(int status, int androidSdk, int osCode, String osName,
               String device, String reason) {
            this.status = status;
            this.androidSdk = androidSdk;
            this.osCode = osCode;
            this.osName = osName;
            this.device = device;
            this.reason = reason;
        }

        @Override
        public String toString() {
            return "gate{status=" + status + ", sdk=" + androidSdk
                    + ", os=" + osName + "(" + osCode + ")"
                    + ", device=" + device + ", reason=" + reason + "}";
        }
    }

    private Compat() {
    }

    public static Result check() {
        int sdk = Build.VERSION.SDK_INT;
        String device = safe(Build.MODEL);
        String osName = safe(prop("ro.mi.os.version.name"));
        String codeStr = prop("ro.mi.os.version.code");

        int osCode = -1;
        try {
            osCode = Integer.parseInt(codeStr.trim());
        } catch (Throwable ignored) {
        }

        // 非 HyperOS：没有 ro.mi.os.version.code（MIUI 老版本 / 其他 ROM）
        if (osCode <= 0) {
            return new Result(SKIP_NOT_HYPEROS, sdk, -1, "", device,
                    "no ro.mi.os.version.code");
        }

        if (osCode >= 4) {
            if (sdk >= TESTED_SDK) {
                if (osCode == TESTED_OS) {
                    return new Result(PASS, sdk, osCode, osName, device, "tested env");
                }
                // HyperOS 5+ / Android 17+：未来版本，允许但未测试
                return new Result(PASS_UNTESTED, sdk, osCode, osName, device,
                        "newer HyperOS, untested");
            }
            // HyperOS 4 但 Android < 17：允许注册 hook + 未测试警告
            return new Result(PASS_UNTESTED, sdk, osCode, osName, device,
                    "android < 17, untested");
        }

        // HyperOS 1~3：无法运行
        return new Result(REJECT_UNSUPPORTED, sdk, osCode, osName, device,
                "HyperOS <= 3 not supported");
    }

    private static String prop(String key) {
        try {
            Class<?> sp = Class.forName("android.os.SystemProperties");
            Object o = sp.getMethod("get", String.class, String.class)
                    .invoke(null, key, "");
            return o == null ? "" : o.toString();
        } catch (Throwable t) {
            return "";
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}