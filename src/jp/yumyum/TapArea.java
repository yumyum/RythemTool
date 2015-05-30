package jp.yumyum;

import net.yu_yum.android.debug.YLog;
import net.yu_yum.utils.DisplayUtil;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;

class TapArea extends View {

    private final String TAP_AREA_TEXT;
    private final int TAP_TEXT_SIZE;
    private final int FRAME_MARGINE;
    private final int FRAME_STROKE_WIDTH;
    private final int ROUND;

    private int mWidth;
    private int mHeight;

    private LinearGradient mGradient;
    private Paint mPaint;
    private RectF mRect;

    private ShowArea mShowArea;
    private int mLineColor;

    public TapArea(Context context, ShowArea showArea) {
        super(context);
        mLineColor = Color.parseColor("#dddddd");
        TAP_AREA_TEXT = "Tap Here";
        TAP_TEXT_SIZE = DisplayUtil.convertDPtoPX(context, 40);
        FRAME_MARGINE = DisplayUtil.convertDPtoPX(context, 20);
        FRAME_STROKE_WIDTH = DisplayUtil.convertDPtoPX(context, 7);
        YLog.d("TapArea", "YUM STROKE_WIDTH:" + FRAME_STROKE_WIDTH);
        ROUND = DisplayUtil.convertDPtoPX(context, 30);

        mShowArea = showArea;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mPaint == null) {
            return;
        }

        // グラデーションを作成しペイントにセットし描画
        mPaint.setShader(mGradient);
        canvas.drawPaint(mPaint);

        // 角が丸まった四角を表示。線の色や幅も指定。
        // アンチエイリアスを有効にする
        mPaint.setShader(null);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mLineColor);
        mPaint.setStrokeWidth(FRAME_STROKE_WIDTH);
        canvas.drawRoundRect(mRect, ROUND, ROUND, mPaint);

        // "Tap Here"という文字を表示する
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(TAP_TEXT_SIZE);
        mPaint.setStrokeWidth(2);
        mPaint.setTypeface(Typeface.SANS_SERIF);
        canvas.drawText(TAP_AREA_TEXT, mWidth / 2, (mHeight + TAP_TEXT_SIZE) / 2, mPaint);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (mPaint == null) {
            initDrawParams();
            invalidate();
        }
    }

    private void initDrawParams() {
        mPaint = new Paint();

        mWidth = getWidth();
        mHeight = getHeight();
        YLog.d("TapArea", "YUM width:" + mWidth + " height:" + mHeight);

        mGradient = new LinearGradient(0, 0, 0, (mHeight) / 2,
                new int[] { 0xFFbbbbbb, 0xffffffff, }, null,
                Shader.TileMode.CLAMP);
        mRect = new RectF(FRAME_MARGINE, FRAME_MARGINE, mWidth - FRAME_MARGINE, mHeight - FRAME_MARGINE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            // タップ中の枠の色
            mLineColor = Color.parseColor("#bbbbbb");
            this.invalidate();
            // タップされた時刻の取得
            long eTime = event.getEventTime();
            mShowArea.tapEvent(eTime);
            return true;
        case MotionEvent.ACTION_UP:
            // タップを離したときの枠の色
            mLineColor = Color.parseColor("#dddddd");
            this.invalidate();
            break;

        }
        return super.onTouchEvent(event);
    }

}