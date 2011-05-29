package jp.yumyum;

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
	private ShowArea sArea;
	private int lineColor;
	private String tapAreaText;

	public TapArea(Context context, ShowArea sa) {
		super(context);
		this.lineColor = Color.parseColor("#dddddd");
		this.tapAreaText = "Tap Here";

		sArea = sa;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// Viewの高さと幅を取得
		int height = this.getHeight();
		int width = this.getWidth();

		// グラデーションを作成しペイントにセットし描画
		LinearGradient gr = new LinearGradient(0, 0, 0, (height) / 2,
				new int[] { 0xFFbbbbbb, 0xffffffff, }, null,
				Shader.TileMode.CLAMP);
		Paint p = new Paint();
		p.setShader(gr);
		canvas.drawPaint(p);

		// 角が丸まった四角を表示。線の色や幅も指定。
		p = new Paint();
		// アンチエイリアスを有効にする
		p.setAntiAlias(true);
		RectF rect = new RectF(20, 20, width - 20, height - 20);
		p.setStyle(Paint.Style.STROKE);
		p.setColor(lineColor);
		p.setStrokeWidth(7);
		canvas.drawRoundRect(rect, 30, 30, p);

		// "Tap Here"という文字を表示する
		p.setColor(Color.BLACK);
		p.setStyle(Paint.Style.FILL);
		p.setTextAlign(Paint.Align.CENTER);
		p.setTextSize(40);
		p.setStrokeWidth(2);
		p.setTypeface(Typeface.SANS_SERIF);
		canvas.drawText(tapAreaText, width / 2, height / 2 + 10, p);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// タップ中の枠の色
			lineColor = Color.parseColor("#bbbbbb");
			this.invalidate();
			// タップされた時刻の取得
			long eTime = event.getEventTime();
			sArea.tapEvent(eTime);
			return true;
		case MotionEvent.ACTION_UP:
			// タップを離したときの枠の色
			lineColor = Color.parseColor("#dddddd");
			this.invalidate();
			break;

		}
		return super.onTouchEvent(event);
	}

}