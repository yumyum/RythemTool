package jp.yumyum;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

class ShowArea extends HorizontalScrollView {
	private GraphView graphView;
	private ValueView vv;

	public ShowArea(Context context, ValueView vview) {
		super(context);
		this.setHorizontalFadingEdgeEnabled(false);
		vv = vview;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Log.d("ShowArea", "onLayout");

		// �����X�N���[�����Ȃ���GraphView�̉E�[��\��
		if (graphView != null && graphView.isScrolling()) {
			scrollTo(graphView.getWidth() - this.getWidth(), 0);
		}
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// �����X�N���[�����Ȃ�^�b�`����Ă��������Ȃ�
		if (graphView.isScrolling()) {
			return true;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.d("ShowArea", "onDraw");
		// graphView����Ȃ�i���߂ČĂяo�����Ȃ�j�쐬���āA�q��View�Ƃ��ēo�^
		if (graphView == null) {
			graphView = new GraphView(this.getContext(), this.getWidth(),
					this.getHeight(), vv);
			this.addView(graphView);
			invalidate();
		}

		super.onDraw(canvas);
	}

	public void tapEvent(long time) {
		if (graphView.isScrolling()) {
			// scrolling = false;
			// graphView.stopScroll();
			// // �����X�N���[�����~�܂�����X�N���[���o�[�\��
			// this.setHorizontalScrollBarEnabled(true);
		} else {
			graphView.startScroll();
			// �����X�N���[�����̓X�N���[���o�[��\��
			this.setHorizontalScrollBarEnabled(false);
			requestLayout();
		}
		graphView.tap(time);
	}

	public void reset() {
		graphView.reset();
	}

	public void setTargetBpm(int bpm) {
		if (bpm > 0) {
			graphView.setTargetBpm(bpm);
		}
	}
	public void stopScroll(){
		graphView.stopScroll();
	}
}
