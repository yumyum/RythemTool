package jp.yumyum

import net.yu_yum.android.debug.YLog
import net.yu_yum.utils.DisplayUtil
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.View

internal class TapArea(context: Context, private val mShowArea: ShowArea) : View(context) {

    private val TAP_AREA_TEXT: String
    private val TAP_TEXT_SIZE: Int
    private val FRAME_MARGINE: Int
    private val FRAME_STROKE_WIDTH: Int
    private val ROUND: Int

    private var mWidth: Int = 0
    private var mHeight: Int = 0

    private var mGradient: LinearGradient? = null
    private var mPaint: Paint? = null
    private var mRect: RectF? = null
    private var mLineColor: Int = 0

    init {
        mLineColor = resources.getColor(R.color.tap_area_line, null)
        TAP_AREA_TEXT = "Tap Here"
        TAP_TEXT_SIZE = DisplayUtil.convertDPtoPX(context, 40)
        FRAME_MARGINE = DisplayUtil.convertDPtoPX(context, 20)
        FRAME_STROKE_WIDTH = DisplayUtil.convertDPtoPX(context, 7)
        YLog.d("TapArea", "YUM STROKE_WIDTH:$FRAME_STROKE_WIDTH")
        ROUND = DisplayUtil.convertDPtoPX(context, 30)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (mPaint == null) {
            return
        }

        // グラデーションを作成しペイントにセットし描画
        mPaint!!.shader = mGradient
        canvas.drawPaint(mPaint!!)

        // 角が丸まった四角を表示。線の色や幅も指定。
        // アンチエイリアスを有効にする
        mPaint!!.shader = null
        mPaint!!.isAntiAlias = true
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.color = mLineColor
        mPaint!!.strokeWidth = FRAME_STROKE_WIDTH.toFloat()
        canvas.drawRoundRect(mRect!!, ROUND.toFloat(), ROUND.toFloat(), mPaint!!)

        // "Tap Here"という文字を表示する
        mPaint!!.color = Color.BLACK
        mPaint!!.style = Paint.Style.FILL
        mPaint!!.textAlign = Paint.Align.CENTER
        mPaint!!.textSize = TAP_TEXT_SIZE.toFloat()
        mPaint!!.strokeWidth = 2f
        mPaint!!.typeface = Typeface.SANS_SERIF
        canvas.drawText(TAP_AREA_TEXT, (mWidth / 2).toFloat(), ((mHeight + TAP_TEXT_SIZE) / 2).toFloat(), mPaint!!)
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (mPaint == null) {
            initDrawParams()
            invalidate()
        }
    }

    private fun initDrawParams() {
        mPaint = Paint()

        mWidth = width
        mHeight = height
        YLog.d("TapArea", "YUM width:$mWidth height:$mHeight")

        mGradient = LinearGradient(
            0f, 0f, 0f, (mHeight / 2).toFloat(),
            resources.getColor(R.color.grad_top, null),
            resources.getColor(R.color.grad_bottom, null),
            Shader.TileMode.CLAMP
        )
        mRect = RectF(
            FRAME_MARGINE.toFloat(),
            FRAME_MARGINE.toFloat(),
            (mWidth - FRAME_MARGINE).toFloat(),
            (mHeight - FRAME_MARGINE).toFloat()
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // タップ中の枠の色
                mLineColor = resources.getColor(R.color.tap_area_line_taped)
                this.invalidate()
                // タップされた時刻の取得
                val eTime = event.eventTime
                mShowArea.tapEvent(eTime)
                return true
            }
            MotionEvent.ACTION_UP -> {
                // タップを離したときの枠の色
                mLineColor = resources.getColor(R.color.tap_area_line, null)
                this.invalidate()
            }
        }
        return super.onTouchEvent(event)
    }

}