package jp.yumyum;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Handler;
import android.view.View;

public class ValueView extends View {
	private int averageBPM;
	private int targetBPM;
	private int currentBPM;
	private static int BUF_LEN = 4;
	private long[] buf = new long[BUF_LEN];
	private int index;
	private long sum;
	private boolean isShowGuid = false;
	private Runnable runnable;
	private Handler handler;
	private boolean isAverageMode = true;

	private final int CIRCLE_SHOW_TIME = 100;

	public ValueView(Context context) {
		super(context);
		averageBPM = targetBPM = currentBPM = 0;
		handler = new Handler();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 真ん中上部の今回のBPMの表示
		Paint p = new Paint();
		p.setTextSize(40);
		p.setTextAlign(Align.CENTER);
		p.setAntiAlias(true);
		if (isAverageMode) {
			canvas.drawText(String.valueOf(averageBPM), getWidth() / 2, 40, p);
		} else {
			canvas.drawText(String.valueOf(currentBPM), getWidth() / 2, 40, p);
		}

		// 左下の目標BPMの表示
		p.setTextSize(20);
		p.setTextAlign(Align.LEFT);
		if (targetBPM == 0) {
			canvas.drawText(getContext().getString(R.string.measurering), 5,
					getHeight() * 0.75f, p);
		} else {
			canvas.drawText(String.valueOf(targetBPM), 5, getHeight() * 0.75f,
					p);
		}
		// 右上の平均値BPMの表示
		p.setTextAlign(Align.RIGHT);
		if (isAverageMode) {
			canvas.drawText(String.valueOf(currentBPM), getWidth() - 5, 40, p);
		} else {
			canvas.drawText(String.valueOf(averageBPM), getWidth() - 5, 40, p);
		}
		if (isShowGuid) {
			// 円を描画する
			p.setStyle(Paint.Style.FILL);
			p.setColor(getResources().getColor(R.color.curcle_pink));
			p.setTextAlign(Align.CENTER);
			p.setAntiAlias(true);
			canvas.drawCircle(getWidth() / 2 - 55, 25, 13, p);
		}

		super.onDraw(canvas);
	}

	public void setTargetBPM(int value) {
		// 目標BPMを設定
		targetBPM = value;
		/*
		 * // 平均値を出すためのバッファを目標値で初期化 for (int i = 0; i < bufLen - 1; i++) {
		 * buf[i] = targetBPM; } // 最初の値を入れるスペースの処理 index = bufLen - 1;
		 * buf[index] = 0; // 平均値を出すために合計を計算 sum = targetBPM * (bufLen - 1);
		 */
		currentBPM = 0;
		averageBPM = 0;
	}
	
	public void setAvarageMode(boolean value){
		this.isAverageMode = value;
	}

	public void initAverageBuf(long time) {
		int i = 0;
		long temp1, temp2;
		temp1 = time - 60000 * BUF_LEN / targetBPM;
		sum = 0;
		for (i = 0; i < BUF_LEN - 1; i++) {
			temp2 = time - 60000 * (BUF_LEN - 1 - i) / targetBPM;
			buf[i] = temp2 - temp1;
			temp1 = temp2;
			sum += buf[i];
		}
		buf[i] = 0;
		index = i;
		currentBPM = 0;
		averageBPM = 0;
	}

	public int getBPM(long delta) {
		// 今回のBPMを設定
		currentBPM = (int) (60000 / delta);
		// 平均値の計算をしながらバッファを更新
		sum -= buf[index];
		buf[index] = delta;
		sum += buf[index];
		// 平均値を設定
		averageBPM = (int) (60000*BUF_LEN/sum);
		// バッファのインデックスを更新
		index++;
		index %= BUF_LEN;
		if(isAverageMode)
			return averageBPM;
		else
			return currentBPM;
	}

	public void showGuid() {
		// ガイド円を表示
		this.isShowGuid = true;
		invalidate();

		if (runnable == null) {
			runnable = new Runnable() {
				@Override
				public void run() {
					// ガイド円の消去
					ValueView.this.isShowGuid = false;
					invalidate();
					ValueView.this.runnable = null;
				}
			};

			// ガイド円の消去処理を予約
			handler.postDelayed(runnable, CIRCLE_SHOW_TIME);
		}
	}
}
