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

	private int REPEAT_INTERVAL = 60000 / targetBPM / 2;

	private final int FORWARD_PICS = 1;

	// �R���X�g���N�^�ŕ��ƍ������w��
	public GraphView(Context context, int width, int height, ValueView vview) {
		super(context);

		mWidth = winWidth = width;
		winHeight = height;
		handler = new Handler();
		lastValueX = lastCursor = cursor = 5;
		lastValueY = height / 2;
		mValueView = vview;

		// ���ŕ`�悷�邽�߂̃r�b�g�}�b�v���쐬
		// �Ƃ肠�������� ��ʕ�+800 �Ƃ��Ă݂�
		mBitmap = Bitmap.createBitmap(width + 800, height,
				Bitmap.Config.RGB_565);
		mCanvas.setBitmap(mBitmap);
		// �w�i�𔒂œh��
		initCanvas();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Paint paint = new Paint();
		// ����������
		if (nextValueY != 0) {
			paint.setColor(Color.BLUE);
			Log.d("GraphView", "lastValueX:" + lastValueX + "  lastValueY:"
					+ lastValueY + "  cursor:" + cursor + "  nextValueY:"
					+ nextValueY);
			mCanvas.drawLine(lastValueX, lastValueY, cursor, nextValueY, paint);
			lastValueX = cursor;
			lastValueY = nextValueY;
			nextValueY = 0;
		}

		// �K�C�h����������
		paint.setColor(Color.GRAY);

		if (cursor > lastCursor) {
			mCanvas.drawLine(lastCursor, winHeight / 2, cursor, winHeight / 2,
					paint);

			// �K�C�h�c��������
			if (cursor >= nextGuid) {
				mCanvas.drawLine(nextGuid, 10, nextGuid, winHeight - 10, paint);
				nextGuid += winWidth;
			}
		}
		// mBitmap�̓��e����ʂ̃L�����o�X�֕`��
		canvas.drawBitmap(mBitmap, 0, 0, null);

		// targetBPM�̂S�{�̎��ԃ^�b�v����Ȃ���Β�~
		if (cursor - lastValueX > 8) {
			this.stopScroll();
			cursor -= FORWARD_PICS * 8;
		}
		/*
		 * // �w�i�𔒂œh�� canvas.drawColor(Color.WHITE);
		 * 
		 * // �D�F�̐������� Paint paint = new Paint(); paint.setColor(Color.GRAY);
		 * canvas.drawLine(5, 100, mWidth - 5, 100, paint); //
		 * �X�N���[�����Ă���̂��킩��悤�ɉ�ʕ����Ƃɏc�������Ă݂� for (int x = winWidth / 2; x < mWidth
		 * - 5; x += winWidth) { canvas.drawLine(x, 10, x, winHeight - 10,
		 * paint); }
		 * 
		 * // �ŃO���t(�ɂȂ�\��)�̐������� paint.setColor(Color.BLUE); canvas.drawLine(5,
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
		if (cursor + 5 > mWidth) {
			addWidth(cursor + 5 - mWidth);
		}
	}

	public void startScroll() {
		if (runnable == null) {
			runnable = new Runnable() {
				@Override
				public void run() {

					// 2.���񏈗����Z�b�g
					handler.postDelayed(this, REPEAT_INTERVAL);

					// 3.�J��Ԃ�����
					// �P����forwordCursol(4)�ł������������������������ł���Ƃ������Ƃ�
					GraphView.this.forwordCursol(FORWARD_PICS);
					requestLayout();
					invalidate();

				}
			};

			// 1.������s
			handler.postDelayed(runnable, REPEAT_INTERVAL);
		}
	}

	public void stopScroll() {
		if (runnable != null) {
			handler.removeCallbacks(runnable);
			runnable = null;
			// ShowArea���擾���ăX�N���[���o�[�\��
			ShowArea parent = (ShowArea) getParent();
			parent.setHorizontalScrollBarEnabled(true);

			// �O��̃^�b�v���Ԃ��N���A
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
	}

	public boolean isScrolling() {
		if (runnable == null)
			return false;
		else
			return true;
	}

	private void initCanvas() {
		mCanvas.drawColor(Color.WHITE);
		// // �D�F�̃K�C�h����`��
		Paint p = new Paint();
		p.setColor(Color.GRAY);
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
}