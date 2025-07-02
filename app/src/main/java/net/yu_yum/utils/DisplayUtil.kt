package net.yu_yum.utils

import android.content.Context
import android.util.TypedValue

/**
 * ディスプレイ関連のユーティリティ
 *
 * @author yu
 */
object DisplayUtil {

    /**
     * DPをPXに変換する
     *
     * @param context
     * @param dp 変換したいDP値
     * @return PXに変換した値
     */
    fun convertDPtoPX(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(), context.resources.displayMetrics).toInt()
    }
}
