package net.yu_yum.android.debug;

import jp.yumyum.BuildConfig;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * ログユーティリティクラス
 * 
 * @author yu
 *
 */
public class YLog extends BroadcastReceiver {

    private static final String TAG = "YLog";
    // インテントでログの有効／無効を制御可能（AndroidManifestに記述が必要）
    public static String ACTION_LOG_ENABLE = "net.yu_yum.LOG_ENABLE";
    public static String ACTION_LOG_DISABLE = "net.yu_yum.LOG_DISABLE";

    private static boolean sEnabled;
    static {
        sEnabled = BuildConfig.DEBUG;
    }

    public static void d(String tag, String msg) {
        if (sEnabled) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (sEnabled) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (sEnabled) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (sEnabled) {
            Log.e(tag, msg);
        }
    }

    /**
     * インテントでログの有効／無効を制御
     */
    @Override
    public void onReceive(Context arg0, Intent arg1) {
        String action = arg1.getAction();
        if (ACTION_LOG_ENABLE.equals(action)) {
            sEnabled = true;
            Log.d(TAG, "Log ENABLED");
        } else if (ACTION_LOG_DISABLE.equals(action)) {
            sEnabled = false;
            Log.d(TAG, "Log DISABLED");
        }
    }
}
