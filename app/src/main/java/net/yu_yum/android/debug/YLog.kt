package net.yu_yum.android.debug

import jp.yumyum.BuildConfig
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * ログユーティリティクラス
 *
 * @author yu
 */
class YLog : BroadcastReceiver() {

    /**
     * インテントでログの有効／無効を制御
     */
    override fun onReceive(arg0: Context, arg1: Intent) {
        val action = arg1.action
        if (ACTION_LOG_ENABLE == action) {
            sEnabled = true
            Log.d(TAG, "Log ENABLED")
        } else if (ACTION_LOG_DISABLE == action) {
            sEnabled = false
            Log.d(TAG, "Log DISABLED")
        }
    }

    companion object {

        private val TAG = "YLog"
        // インテントでログの有効／無効を制御可能（AndroidManifestに記述が必要）
        var ACTION_LOG_ENABLE = "net.yu_yum.LOG_ENABLE"
        var ACTION_LOG_DISABLE = "net.yu_yum.LOG_DISABLE"

        private var sEnabled: Boolean = false

        init {
            sEnabled = BuildConfig.DEBUG
        }

        fun d(tag: String, msg: String) {
            if (sEnabled) {
                Log.d(tag, msg)
            }
        }

        fun i(tag: String, msg: String) {
            if (sEnabled) {
                Log.i(tag, msg)
            }
        }

        fun w(tag: String, msg: String) {
            if (sEnabled) {
                Log.w(tag, msg)
            }
        }

        fun e(tag: String, msg: String) {
            if (sEnabled) {
                Log.e(tag, msg)
            }
        }
    }
}
