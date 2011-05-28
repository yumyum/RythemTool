package jp.yumyum;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Handler;
import android.view.View;

public class ValueView extends View {
	private int averageBPM;
	private int targetBPM;
	private int currentBPM;
	private int bufLen = 3;
	private int[] buf = new int[bufLen];
	private int index;
	private int sum;
	private boolean isShowGuid = false;
	private Runnable runnable;
	private Handler handler;
	private final int CIRCLE_SHOW_TIME = 100;

	public ValueView(Context context) {
		super(context);
		averageBPM = targetBPM = currentBPM = 0;
		handler = new Handler();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 真ん中上部のBPM平均値の表示
		Paint p = new Paint();
		p.setTextSize(40);
		p.setTextAlign(Align.CENTER);
		p.setAntiAlias(true);
		canvas.drawText(String.valueOf(averageBPM), getWidth() / 2, 40, p);

		// 左下の目標BPMの表示
		p.setTextSize(20);
		p.setTextAlign(Align.LEFT);
		canvas.drawText(String.valueOf(targetBPM), 5, getHeight() * 0.75f, p);

		// 右上の今回のBPMの表示
		p.setTextAlign(Align.RIGHT);
		canvas.drawText(String.valueOf(currentBPM), getWidth() - 5, 40, p);

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
		// 平均値を出すためのバッファを目標値で初期化
		for (int i = 0; i < bufLen - 1; i++) {
			buf[i] = targetBPM;
		}
		// 最初の値を入れるスペースの処理
		index = bufLen - 1;
		buf[index] = 0;
		// 平均値を出すために合計を計算
		sum = targetBPM * (bufLen - 1);
		currentBPM = 0;
		averageBPM = 0;
	}

	public void setCurrentBPM(int value) {
		// 今回のBPMを設定
		currentBPM = value;
		// 平均値の計算をしながらバッファを更新
		sum -= buf[index];
		buf[index] = currentBPM;
		sum += buf[index];
		// 平均値を設定
		averageBPM = sum / bufLen;
		// バッファのインデックスを更新
		index++;
		index %= bufLen;
	}

	public void showGuid() {
		// 　ガイド円を表示
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
