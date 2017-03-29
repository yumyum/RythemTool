package jp.yumyum;

import net.yu_yum.utils.DisplayUtil;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Handler;
import android.view.View;

public class ValueView extends View {

    private final int TEXT_SIZE_LARGE;
    private final int TEXT_SIZE_SMALL;
    private final int MARGINE_RIGHTLEFT;
    private final int MARGINE_UPDOWN;
    private final int GUIDE_TOP_MARGINE;
    private final int GUIDE_SIZE;

    private int mAverageBPM;
    private int mTargetBPM;
    private int mCurrentBPM;
    private int mBufLen = 2;
    private long[] mBuf = new long[mBufLen];
    private int mBufIndex;
    private long mSum;
    private boolean mIsShowGuid = false;
    private Runnable mRunnable;
    private Handler mHandler;
    private boolean mIsAverageMode = true;
    private Paint mPaint;

    private final int CIRCLE_SHOW_TIME = 100;

    public ValueView(Context context) {
        super(context);
        mAverageBPM = mTargetBPM = mCurrentBPM = 0;
        mHandler = new Handler();
        mPaint = new Paint();

        TEXT_SIZE_LARGE = DisplayUtil.convertDPtoPX(context, 40);
        TEXT_SIZE_SMALL = DisplayUtil.convertDPtoPX(context, 20);
        MARGINE_RIGHTLEFT = DisplayUtil.convertDPtoPX(context, 5);
        MARGINE_UPDOWN = DisplayUtil.convertDPtoPX(context, 40);
        GUIDE_TOP_MARGINE = DisplayUtil.convertDPtoPX(context, 25);
        GUIDE_SIZE = DisplayUtil.convertDPtoPX(context, 13);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 真ん中上部の今回のBPMの表示
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(TEXT_SIZE_LARGE);
        mPaint.setTextAlign(Align.CENTER);
        mPaint.setAntiAlias(true);
        if (mIsAverageMode) {
            canvas.drawText(String.valueOf(mAverageBPM), getWidth() / 2, MARGINE_UPDOWN, mPaint);
        } else {
            canvas.drawText(String.valueOf(mCurrentBPM), getWidth() / 2, MARGINE_UPDOWN, mPaint);
        }

        // 左下の目標BPMの表示
        mPaint.setTextSize(TEXT_SIZE_SMALL);
        mPaint.setTextAlign(Align.LEFT);
        if (mTargetBPM == 0) {
            canvas.drawText(getContext().getString(R.string.measurering), MARGINE_RIGHTLEFT,
                    getHeight() - TEXT_SIZE_SMALL, mPaint);
        } else {
            canvas.drawText(String.valueOf(mTargetBPM), MARGINE_RIGHTLEFT,
                    getHeight() - TEXT_SIZE_SMALL, mPaint);
        }
        // 右上の平均値BPMの表示
        mPaint.setTextAlign(Align.RIGHT);
        if (mIsAverageMode) {
            canvas.drawText(String.valueOf(mCurrentBPM), getWidth() - MARGINE_RIGHTLEFT, MARGINE_UPDOWN, mPaint);
        } else {
            canvas.drawText(String.valueOf(mAverageBPM), getWidth() - MARGINE_RIGHTLEFT, MARGINE_UPDOWN, mPaint);
        }
        if (mIsShowGuid) {
            // 円を描画する
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(getResources().getColor(R.color.curcle_pink));
            mPaint.setTextAlign(Align.CENTER);
            mPaint.setAntiAlias(true);
            canvas.drawCircle(getWidth() / 2 - (TEXT_SIZE_LARGE * 1.5f), GUIDE_TOP_MARGINE, GUIDE_SIZE, mPaint);
        }

        super.onDraw(canvas);
    }

    public void setTargetBPM(int value) {
        // 目標BPMを設定
        mTargetBPM = value;
        int i;
        long temp1, temp2;
        // 平均値を出すためのバッファを初期化
        if (mTargetBPM != 0) {
            temp1 = 60000 * mBufLen / mTargetBPM;
            mSum = 0;
            for (i = 0; i < mBufLen - 1; i++) {
                temp2 = 60000 * (mBufLen - 1 - i) / mTargetBPM;
                mBuf[i] = temp1 - temp2;
                temp1 = temp2;
                mSum += mBuf[i];
            }
            mBuf[i] = 0;
            mBufIndex = i;
        }
        mCurrentBPM = 0;
        mAverageBPM = 0;

    }

    public void setAvarageMode(boolean value) {
        this.mIsAverageMode = value;
    }

    public int getBPM(long delta) {
        // 今回のBPMを設定
        mCurrentBPM = (int) (60000 / delta);
        // 平均値の計算をしながらバッファを更新
        mSum = mSum + delta - mBuf[mBufIndex];
        mBuf[mBufIndex] = delta;

        // 平均値を設定
        mAverageBPM = (int) (60000 * mBufLen / mSum);
        // バッファのインデックスを更新
        mBufIndex++;
        mBufIndex %= mBufLen;
        postInvalidate();
        if (mIsAverageMode)
            return mAverageBPM;
        else
            return mCurrentBPM;
    }

    public void showGuid() {
        // ガイド円を表示
        this.mIsShowGuid = true;
        invalidate();

        if (mRunnable == null) {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    // ガイド円の消去
                    ValueView.this.mIsShowGuid = false;
                    invalidate();
                    ValueView.this.mRunnable = null;
                }
            };

            // ガイド円の消去処理を予約
            mHandler.postDelayed(mRunnable, CIRCLE_SHOW_TIME);
        }
    }

    public void setBufLen(int len) {
        if (this.mBufLen != len) {
            this.mBufLen = len;
            this.mBuf = new long[len];
            mBufIndex = 0;
        }
    }
}
