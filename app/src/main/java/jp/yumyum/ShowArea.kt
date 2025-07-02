package jp.yumyum

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import android.widget.HorizontalScrollView

internal class ShowArea(context: Context, private val vv: ValueView) : HorizontalScrollView(context) {
    private var graphView: GraphView? = null

//    val tagetBpm: Int
//        get() = graphView!!.targetBpm
//
//    init {
//        this.isHorizontalFadingEdgeEnabled = false
//    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        // 自動スクロール中なら常にGraphViewの右端を表示
        if (graphView != null && graphView!!.isScrolling) {
            scrollTo(graphView!!.width - this.width, 0)
        }
        super.onLayout(changed, l, t, r, b)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        // 自動スクロール中ならタッチされても何もしない
        return if (graphView!!.isScrolling) {
            true
        } else super.onTouchEvent(ev)
    }

    override fun onDraw(canvas: Canvas) {
        // graphViewが空なら（初めて呼び出されるなら）作成して、子供Viewとして登録
        if (graphView == null) {
            graphView = GraphView(this.context, this.width,
                    this.height, vv)
            this.addView(graphView)
            invalidate()
        }
        super.onDraw(canvas)
    }

    fun tapEvent(time: Long) {
        if (!graphView!!.isScrolling) {
            // 自動スクロール中はスクロールバー非表示
            this.isHorizontalScrollBarEnabled = false
        }
        graphView!!.tap(time)
        return
    }

    fun reset() {
        graphView!!.reset()
    }

    fun stopScroll() {
        if (graphView != null)
            graphView!!.stopScroll()
    }

}
