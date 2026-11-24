package io.github.mersand.hyperclassic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * TestReceiver —— 通过广播触发测试动作，方便命令行自动化。
 *
 * 用法（adb / root shell）：
 * <pre>
 *   # 发一条测试 HUN
 *   am broadcast -a io.github.mersand.hyperclassic.TEST_HUN \
 *       -n io.github.mersand.hyperclassic/.TestReceiver \
 *       --es title "标题" --es text "内容"
 *
 *   # 清掉所有测试 HUN
 *   am broadcast -a io.github.mersand.hyperclassic.CANCEL_HUN \
 *       -n io.github.mersand.hyperclassic/.TestReceiver
 *
 *   # 连续发 N 条（压力测试）
 *   am broadcast -a io.github.mersand.hyperclassic.TEST_HUN_BURST \
 *       -n io.github.mersand.hyperclassic/.TestReceiver --ei count 3
 * </pre>
 *
 * 注意：广播是 exported 的（AndroidManifest 里声明），
 * 所以任何应用都能触发 —— 这里只用于本机调试，不带任何敏感操作。
 */
public class TestReceiver extends BroadcastReceiver {

    public static final String ACTION_TEST_HUN = "io.github.mersand.hyperclassic.TEST_HUN";
    public static final String ACTION_CANCEL = "io.github.mersand.hyperclassic.CANCEL_HUN";
    public static final String ACTION_BURST = "io.github.mersand.hyperclassic.TEST_HUN_BURST";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        switch (action) {
            case ACTION_TEST_HUN: {
                String title = intent.getStringExtra("title");
                String text = intent.getStringExtra("text");
                boolean ok = HunProbe.sendTestHun(context, title, text);
                L.always("HCHunProbe", "broadcast TEST_HUN ok=" + ok);
                break;
            }
            case ACTION_CANCEL: {
                HunProbe.cancelTestHuns(context);
                break;
            }
            case ACTION_BURST: {
                int count = intent.getIntExtra("count", 3);
                if (count < 1) {
                    count = 1;
                }
                if (count > 20) {
                    count = 20;
                }
                for (int i = 1; i <= count; ++i) {
                    HunProbe.sendTestHun(context,
                            "HC 测试 HUN " + i + "/" + count,
                            "第 " + i + " 条，横滑观察玻璃背景");
                    // 轻微错开，避免同时 notify 被合并
                    try {
                        Thread.sleep(350L);
                    } catch (Throwable ignored) {
                    }
                }
                L.always("HCHunProbe", "broadcast BURST count=" + count);
                break;
            }
            default:
                break;
        }
    }
}