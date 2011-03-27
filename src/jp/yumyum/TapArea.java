package jp.yumyum;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

class TapArea extends View {
	private ShowArea sArea;
	private long lastETime;

	public TapArea(Context context, ShowArea sa) {
		super(context);
		lastETime = -1;

		sArea = sa;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// View�̍����ƕ����擾
		int height = this.getHeight();
		int width = this.getWidth();

		// �O���f�[�V�������쐬���y�C���g�ɃZ�b�g���`��
		LinearGradient gr = new LinearGradient(0, 0, 0, (height) / 2,
				new int[] { 0xFFbbbbbb, 0xffffffff, }, null,
				Shader.TileMode.CLAMP);
		Paint p = new Paint();
		p.setShader(gr);
		canvas.drawPaint(p);

		// �p���ۂ܂����l�p��\���B���̐F�╝���w��B
		p = new Paint();
		// �A���`�G�C���A�X��L���ɂ���
		p.setAntiAlias(true);
		RectF rect = new RectF(20, 20, width - 20, height - 20);
		p.setStyle(Paint.Style.STROKE);
		p.setColor(Color.argb(0xff, 0xdd, 0xdd, 0xdd));
		p.setStrokeWidth(7);
		canvas.drawRoundRect(rect, 30, 30, p);

		// "Tap Here"�Ƃ���������\������
		p.setColor(Color.BLACK);
		p.setStyle(Paint.Style.FILL);
		p.setTextAlign(Paint.Align.CENTER);
		p.setTextSize(40);
		p.setStrokeWidth(2);
		p.setTypeface(Typeface.SANS_SERIF);
		canvas.drawText("Tap Here", width / 2, height / 2 + 10, p);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// �^�b�v���ꂽ�����̎擾
		long eTime = event.getEventTime();
		// �ŏ��̈��͌v�Z���Ȃ�(�ł��Ȃ�)
		if (lastETime > 0) {
			long interval = eTime - lastETime;
			// BPM���v�Z
			long bpm = 60000 / interval;
			// �Ƃ肠����Log�ɏo��
			Log.d("TapArea", "onTaouchEvent  interval:" + interval + " bpm:"
					+ bpm);
		}

		lastETime = eTime;

		sArea.tapEvent(eTime);
		return super.onTouchEvent(event);
	}

}