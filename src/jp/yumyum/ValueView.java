package jp.yumyum;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Handler;
import android.view.View;

public class ValueView extends View {
    private int mAverageBPM;
    private int mTargetBPM;
    private int mCurrentBPM;
    private int mBufLen = 2;
    private long[] mBuf = new long[mBufLen];
    private int mIndex;
    private long mSum;
    private boolean mIsShowGuid = false;
    private Runnable mRunnable;
    private Handler mHandler;
    private boolean mIsAverageMode = true;
    private Paint mPaint;

    private static final int CIRCLE_SHOW_TIME = 100;

    public ValueView(Context context) {
        super(context);
        mAverageBPM = mTargetBPM = mCurrentBPM = 0;
        mHandler = new Handler();
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 真ん中上部の今回のBPMの表示
        mPaint.setTextSize(40);
        mPaint.setTextAlign(Align.CENTER);
        mPaint.setAntiAlias(true);
        if (mIsAverageMode) {
            canvas.drawText(String.valueOf(mAverageBPM), getWidth() / 2, 40, mPaint);
        } else {
            canvas.drawText(String.valueOf(mCurrentBPM), getWidth() / 2, 40, mPaint);
        }

        // 左下の目標BPMの表示
        mPaint.setTextSize(20);
        mPaint.setTextAlign(Align.LEFT);
        if (mTargetBPM == 0) {
            canvas.drawText(getContext().getString(R.string.measurering), 5,
                    getHeight() * 0.75f, mPaint);
        } else {
            canvas.drawText(String.valueOf(mTargetBPM), 5, getHeight() * 0.75f,
                    mPaint);
        }
        // 右上の平均値BPMの表示
        mPaint.setTextAlign(Align.RIGHT);
        if (mIsAverageMode) {
            canvas.drawText(String.valueOf(mCurrentBPM), getWidth() - 5, 40, mPaint);
        } else {
            canvas.drawText(String.valueOf(mAverageBPM), getWidth() - 5, 40, mPaint);
        }
        if (mIsShowGuid) {
            // 円を描画する
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(getResources().getColor(R.color.curcle_pink));
            mPaint.setTextAlign(Align.CENTER);
            mPaint.setAntiAlias(true);
            canvas.drawCircle(getWidth() / 2 - 55, 25, 13, mPaint);
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
            mIndex = i;
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
        mSum = mSum + delta - mBuf[mIndex];
        mBuf[mIndex] = delta;

        // 平均値を設定
        mAverageBPM = (int) (60000 * mBufLen / mSum);
        // バッファのインデックスを更新
        mIndex++;
        mIndex %= mBufLen;
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
            mIndex = 0;
        }
    }
}
