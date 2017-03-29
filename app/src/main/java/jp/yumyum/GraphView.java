package jp.yumyum;

import net.yu_yum.android.debug.YLog;
import net.yu_yum.utils.DisplayUtil;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;

class GraphView extends View {
    private int winHeight;
    private int mWidth;
    private int winWidth;
    private Handler handler;
    private int lastCursor, cursor;
    private Bitmap mBitmap;
    private Canvas mCanvas = new Canvas();
    private Runnable runnable;
    private int nextGuid;
    private int lastValueX;
    private int lastValueY;
    private int nextValueY = 0;
    private int targetBPM = 0;
    private long lastTime = 0;
    private ValueView mValueView;
    private int backCount = 0;
    private boolean isGuidEnable;
    private boolean isEndOfGraph = false;
    private boolean isMeasuring;
    private int measureCount;
    private long measureLast;
    private long measureSum;
    private int sencitivity;
    private Paint mPaint;

    private int REPEAT_INTERVAL = 0;

    // 計測モード時、計測に使用するタップ回数
    private final int SENCIVITY_PARM;
    private final int MEASURE_MAX = 5;
    private final int FORWARD_PICS;
    private final int LINE_WIDTH;

    // グラフの最大幅
    private final int GRAPH_MAX_WIDTH;
    private SharedPreferences sharedPreferences;

    // コンストラクタで幅と高さを指定
    public GraphView(Context context, int width, int height, ValueView vview) {
        super(context);

        SENCIVITY_PARM = DisplayUtil.convertDPtoPX(context, 1);
        FORWARD_PICS = DisplayUtil.convertDPtoPX(context, 1);
        LINE_WIDTH = DisplayUtil.convertDPtoPX(context, 1);
        GRAPH_MAX_WIDTH = DisplayUtil.convertDPtoPX(context, 1000);

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        mWidth = winWidth = width;
        winHeight = height;
        handler = new Handler();
        lastValueX = lastCursor = cursor = 5;
        lastValueY = height / 2;
        mValueView = vview;

        // 画面リセットのたびに行う必要のあるフィールドの初期化はリセットメソッドにまとめた
        reset();

        // 裏で描画するためのビットマップを作成
        mBitmap = Bitmap.createBitmap(GRAPH_MAX_WIDTH, height,
                Bitmap.Config.RGB_565);
        mCanvas.setBitmap(mBitmap);

        mPaint = new Paint();

        // 背景を白で塗る
        initCanvas();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        YLog.d("GraphView", "YUM onDraw");
        super.onDraw(canvas);

        // ガイド横線を引く
        mPaint.setColor(getResources().getColor(R.color.line_gray));

        if (cursor > lastCursor) {
            mCanvas.drawLine(lastCursor, winHeight / 2, cursor, winHeight / 2,
                    mPaint);

            // ガイド縦線を引く
            if (cursor >= nextGuid) {
                mCanvas.drawLine(nextGuid, 10, nextGuid, winHeight - 10, mPaint);
                nextGuid += winWidth;
            }
        }

        // 青い線を引く
        if (nextValueY != 0) {
            mPaint.setColor(getResources().getColor(R.color.line_blue));
            mPaint.setStrokeWidth(LINE_WIDTH);

            mCanvas.drawLine(lastValueX, lastValueY, cursor, nextValueY, mPaint);
            lastValueX = cursor;
            lastValueY = nextValueY;
            nextValueY = 0;
        }

        // mBitmapの内容を画面のキャンバスへ描く
        canvas.drawBitmap(mBitmap, 0, 0, null);

        // targetBPMの４倍の時間タップされなければ停止
        if (cursor - lastValueX > FORWARD_PICS * 8) {
            this.stopScroll();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(mWidth, winHeight);
        layout(0, 0, mWidth, winHeight);
    }

    private void addWidth(int value) {
        mWidth += value;
    }

    private void forwordCursol(int value) {
        if (cursor + 5 >= GRAPH_MAX_WIDTH) {
            endOfGraphArea();
        }
        if (cursor > lastCursor)
            lastCursor = cursor;
        cursor += value;
        backCount += value;
        if (cursor + 5 > mWidth) {
            addWidth(cursor + 5 - mWidth);
        }
    }

    private void endOfGraphArea() {
        backCount = 0;
        stopScroll();
        AlertDialog.Builder dlg;
        dlg = new AlertDialog.Builder(this.getContext());
        dlg.setTitle(R.string.EndOfGraphArea);
        dlg.setMessage(R.string.pleaseReset);
        dlg.show();
        isEndOfGraph = true;

        return;
    }

    private boolean startScroll() {
        if (runnable == null) {
            runnable = new Runnable() {
                private boolean guid = true;

                @Override
                public void run() {

                    // 2.次回処理をセット
                    handler.postDelayed(this, REPEAT_INTERVAL);

                    // 3.繰り返し処理
                    // 単純にforwordCursol(4)でもいいがこういう書き方もできるということで
                    GraphView.this.forwordCursol(FORWARD_PICS);
                    requestLayout();
                    // テンポガイド表示
                    if (isGuidEnable) {
                        if (guid) {
                            mValueView.showGuid();
                            guid = false;
                        } else {
                            guid = true;
                        }
                    }

                    invalidate();

                }
            };

            // 1.初回実行
            handler.postDelayed(runnable, REPEAT_INTERVAL);
        }
        return true;
    }

    public void stopScroll() {
        cursor -= backCount;
        backCount = 0;
        if (runnable != null) {
            handler.removeCallbacks(runnable);
            runnable = null;
            // ShowAreaを取得してスクロールバー表示
            ShowArea parent = (ShowArea) getParent();
            parent.setHorizontalScrollBarEnabled(true);

            // 前回のタップ時間をクリア
            lastTime = 0;
        }
        if (isMeasuring) {
            measureCount = 0;
            measureLast = 0;
            measureSum = 0;
        }
    }

    public void tap(long time) {
        // グラフ終端状態ならなにもしない
        if (isEndOfGraph)
            return;
        // 計測中処理
        if (isMeasuring) {
            measureCount++;
            if (measureCount > 1)
                measureSum += time - measureLast;
            measureLast = time;
            if (measureCount == MEASURE_MAX) {
                isMeasuring = false;
                int estimateBpm = (int) (60000 / (measureSum / (MEASURE_MAX - 1)));
                setTargetBpm(estimateBpm);
                lastTime = time;
            }
            return;

        }
        if (isScrolling() == false) {
            startScroll();
        }
        // 2回目以降のタップでBPMを計算
        if (lastTime != 0) {
            int delta = (mValueView.getBPM(time - lastTime) - targetBPM)
                    * sencitivity * SENCIVITY_PARM;
            nextValueY = winHeight / 2 - delta * LINE_WIDTH;
        }
        lastTime = time;
        backCount = 0;
    }

    public boolean isScrolling() {
        if (runnable == null)
            return false;
        else
            return true;
    }

    private void initCanvas() {
        mCanvas.drawColor(Color.WHITE);
        // // 灰色のガイド線を描画
        Paint p = new Paint();
        p.setColor(getResources().getColor(R.color.line_gray));
        // mCanvas.drawLine(5, height / 2, width - 5, height / 2, p);
        mCanvas.drawLine(winWidth / 2, 10, winWidth / 2, winHeight - 10, p);
        nextGuid = (int) (winWidth * 1.5);

        requestLayout();
    }

    // リセット処理。数値の初期化と設定値の読み込みはここでやる。
    public void reset() {
        stopScroll();
        mWidth = winWidth;
        lastValueX = lastCursor = cursor = 5;
        lastValueY = winHeight / 2;
        nextValueY = 0;
        lastTime = 0;

        isEndOfGraph = false;
        measureCount = 0;
        measureSum = 0;
        measureLast = 0;

        Context context = getContext();

        initCanvas();
        invalidate();

        mValueView.setBufLen(sharedPreferences.getInt(
                context.getString(R.string.average_count_key), 4));
        isMeasuring = sharedPreferences.getBoolean(
                context.getString(R.string.mesure_mode_key), false);
        if (isMeasuring) {
            setTargetBpm(0);
        } else {
            int bpm = Integer.parseInt(sharedPreferences.getString(
                    context.getString(R.string.target_bpm_key), "0"));
            setTargetBpm(bpm);
        }
        isGuidEnable = sharedPreferences.getBoolean(
                context.getString(R.string.bpm_guid_key), false);

        sencitivity = sharedPreferences.getInt(context.getString(R.string.graph_sencitivity_key), 1);

        mValueView.setAvarageMode(sharedPreferences.getBoolean(
                context.getString(R.string.average_mode_key), true));

    }

    // 目標BPMの設定
    private void setTargetBpm(int bpm) {
        if (bpm == 0) {
            targetBPM = 0;
            REPEAT_INTERVAL = 0;
        } else {
            targetBPM = bpm;
            REPEAT_INTERVAL = 60000 / targetBPM / 2;
        }
        // 画面上に表示するためにvalueViewが持っている目標値も変更
        mValueView.setTargetBPM(bpm);
    }

    public int getTargetBpm() {
        return targetBPM;
    }
}