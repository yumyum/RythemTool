package net.yu_yum.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * ディスプレイ関連のユーティリティ
 * 
 * @author yu
 *
 */
public class DisplayUtil {
    private DisplayUtil() {
    };

    /**
     * DPをPXに変換する
     * 
     * @param context
     * @param dp 変換したいDP値
     * @return PXに変換した値
     */
    public static int convertDPtoPX(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics());
    }
}
