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

		// 自動スクロール中なら常にGraphViewの右端を表示
		if (graphView != null && graphView.isScrolling()) {
			scrollTo(graphView.getWidth() - this.getWidth(), 0);
		}
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// 自動スクロール中ならタッチされても何もしない
		if (graphView.isScrolling()) {
			return true;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// graphViewが空なら（初めて呼び出されるなら）作成して、子供Viewとして登録
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
			// // 自動スクロールが止まったらスクロールバー表示
			// this.setHorizontalScrollBarEnabled(true);
		} else {
			graphView.startScroll();
			// 自動スクロール中はスクロールバー非表示
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
	
	public int getTagetBpm(){
		return graphView.getTargetBpm();
	}
	
	public void stopScroll(){
		if(graphView != null)
			graphView.stopScroll();
	}
}
