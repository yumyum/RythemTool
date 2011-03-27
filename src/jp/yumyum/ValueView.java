package jp.yumyum;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.View;

public class ValueView extends View {
	private int averageBPM;
	private int targetBPM;
	private int currentBPM;
	private int bufLen = 3;
	private int[] buf = new int[bufLen];
	private int index;
	private int sum;

	public ValueView(Context context) {
		super(context);
		averageBPM = targetBPM = currentBPM = 0;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// �^�񒆏㕔��BPM���ϒl�̕\��
		Paint p = new Paint();
		p.setTextSize(40);
		p.setTextAlign(Align.CENTER);
		p.setAntiAlias(true);
		canvas.drawText(String.valueOf(averageBPM), getWidth() / 2, 40, p);

		// �����̖ڕWBPM�̕\��
		p.setTextSize(20);
		p.setTextAlign(Align.LEFT);
		canvas.drawText(String.valueOf(targetBPM), 5, getHeight() * 0.75f, p);

		// �E��̍����BPM�̕\��
		p.setTextAlign(Align.RIGHT);
		canvas.drawText(String.valueOf(currentBPM), getWidth() - 5, 40, p);

		super.onDraw(canvas);
	}

	public void setTargetBPM(int value) {
		// �ڕWBPM��ݒ�
		targetBPM = value;
		// ���ϒl���o�����߂̃o�b�t�@��ڕW�l�ŏ�����
		for (int i = 0; i < bufLen - 1; i++) {
			buf[i] = targetBPM;
		}
		// �ŏ��̒l������X�y�[�X�̏���
		index = bufLen - 1;
		buf[index] = 0;
		// ���ϒl���o�����߂ɍ��v���v�Z
		sum = targetBPM*(bufLen-1);
		currentBPM = 0;
		averageBPM = 0;
	}

	public void setCurrentBPM(int value) {
		// �����BPM��ݒ�
		currentBPM = value;
		// ���ϒl�̌v�Z�����Ȃ���o�b�t�@���X�V
		sum-=buf[index];
		buf[index] = currentBPM;
		sum+=buf[index];
		// ���ϒl��ݒ�
		averageBPM = sum/bufLen;
		// �o�b�t�@�̃C���f�b�N�X���X�V
		index++;
		index %= bufLen;
	}

}
