package jp.yumyum;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
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
	private int targetBPM = 120;
	private long lastTime = 0;
	private ValueView mValueView;
	private int backCount = 0;

	private int REPEAT_INTERVAL = 60000 / targetBPM / 2;

	private final int FORWARD_PICS = 1;

	// コンストラクタで幅と高さを指定
	public GraphView(Context context, int width, int height, ValueView vview) {
		super(context);

		mWidth = winWidth = width;
		winHeight = height;
		handler = new Handler();
		lastValueX = lastCursor = cursor = 5;
		lastValueY = height / 2;
		mValueView = vview;

		// 裏で描画するためのビットマップを作成
		// とりあえず幅は 画面幅+800 としてみる
		mBitmap = Bitmap.createBitmap(width + 800, height,
				Bitmap.Config.RGB_565);
		mCanvas.setBitmap(mBitmap);
		// 背景を白で塗る
		initCanvas();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Paint paint = new Paint();
		// 青い線を引く
		if (nextValueY != 0) {
			paint.setColor(getResources().getColor(R.color.line_blue));
			Log.d("GraphView", "lastValueX:" + lastValueX + "  lastValueY:"
					+ lastValueY + "  cursor:" + cursor + "  nextValueY:"
					+ nextValueY);
			mCanvas.drawLine(lastValueX, lastValueY, cursor, nextValueY, paint);
			lastValueX = cursor;
			lastValueY = nextValueY;
			nextValueY = 0;
		}

		// ガイド横線を引く
		paint.setColor(getResources().getColor(R.color.line_gray));

		if (cursor > lastCursor) {
			mCanvas.drawLine(lastCursor, winHeight / 2, cursor, winHeight / 2,
					paint);

			// ガイド縦線を引く
			if (cursor >= nextGuid) {
				mCanvas.drawLine(nextGuid, 10, nextGuid, winHeight - 10, paint);
				nextGuid += winWidth;
			}
		}
		// mBitmapの内容を画面のキャンバスへ描く
		canvas.drawBitmap(mBitmap, 0, 0, null);

		// targetBPMの４倍の時間タップされなければ停止
		if (cursor - lastValueX > 8) {
			this.stopScroll();
		}
		/*
		 * // 背景を白で塗る canvas.drawColor(Color.WHITE);
		 * 
		 * // 灰色の線を引く Paint paint = new Paint(); paint.setColor(Color.GRAY);
		 * canvas.drawLine(5, 100, mWidth - 5, 100, paint); //
		 * スクロールしているのがわかるように画面幅ごとに縦線を入れてみる for (int x = winWidth / 2; x < mWidth
		 * - 5; x += winWidth) { canvas.drawLine(x, 10, x, winHeight - 10,
		 * paint); }
		 * 
		 * // 青でグラフ(になる予定)の線を引く paint.setColor(Color.BLUE); canvas.drawLine(5,
		 * 80, cursor, 80, paint);
		 */
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
		if (cursor > lastCursor)
			lastCursor = cursor;
		cursor += value;
		backCount += value;
		if (cursor + 5 > mWidth) {
			addWidth(cursor + 5 - mWidth);
		}
	}

	public void startScroll() {
		if (runnable == null) {
			runnable = new Runnable() {
				@Override
				public void run() {

					// 2.次回処理をセット
					handler.postDelayed(this, REPEAT_INTERVAL);

					// 3.繰り返し処理
					// 単純にforwordCursol(4)でもいいがこういう書き方もできるということで
					GraphView.this.forwordCursol(FORWARD_PICS);
					requestLayout();
					invalidate();

				}
			};

			// 1.初回実行
			handler.postDelayed(runnable, REPEAT_INTERVAL);
		}
	}

	public void stopScroll() {
		cursor -= backCount;
		if (runnable != null) {
			handler.removeCallbacks(runnable);
			runnable = null;
			// ShowAreaを取得してスクロールバー表示
			ShowArea parent = (ShowArea) getParent();
			parent.setHorizontalScrollBarEnabled(true);

			// 前回のタップ時間をクリア
			lastTime = 0;
		}
	}

	public void tap(long time) {
		if (lastTime != 0) {
			int bpm = (int) (60000 / (lastTime - time));
			nextValueY = bpm + targetBPM + winHeight / 2;
			mValueView.setCurrentBPM(Math.abs(bpm));
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
		mValueView.setTargetBPM(targetBPM);
		requestLayout();
	}

	public void reset() {
		stopScroll();
		mWidth = winWidth;
		lastValueX = lastCursor = cursor = 5;
		lastValueY = winHeight / 2;
		nextValueY = 0;
		lastTime = 0;
		initCanvas();
		invalidate();
	}

	public void setTargetBpm(int bpm) {
		targetBPM = bpm;
		REPEAT_INTERVAL = 60000 / targetBPM / 2;
		reset();
	}
	
	public int getTargetBpm(){
		return targetBPM;
	}
}