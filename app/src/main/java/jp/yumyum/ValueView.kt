package jp.yumyum

import net.yu_yum.utils.DisplayUtil
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align
import android.os.Handler
import android.view.View

class ValueView(context: Context) : View(context) {

    private val TEXT_SIZE_LARGE: Int
    private val TEXT_SIZE_SMALL: Int
    private val MARGINE_RIGHTLEFT: Int
    private val MARGINE_UPDOWN: Int
    private val GUIDE_TOP_MARGINE: Int
    private val GUIDE_SIZE: Int

    private var mAverageBPM: Int = 0
    private var mTargetBPM: Int = 0
    private var mCurrentBPM: Int = 0
    private var mBufLen = 2
    private var mBuf = LongArray(mBufLen)
    private var mBufIndex: Int = 0
    private var mSum: Long = 0
    private var mIsShowGuid = false
    private var mRunnable: Runnable? = null
    private val mHandler: Handler
    private var mIsAverageMode = true
    private val mPaint: Paint

    private val CIRCLE_SHOW_TIME = 100

    init {
        mCurrentBPM = 0
        mTargetBPM = mCurrentBPM
        mAverageBPM = mTargetBPM
        mHandler = Handler()
        mPaint = Paint()

        TEXT_SIZE_LARGE = DisplayUtil.convertDPtoPX(context, 40)
        TEXT_SIZE_SMALL = DisplayUtil.convertDPtoPX(context, 20)
        MARGINE_RIGHTLEFT = DisplayUtil.convertDPtoPX(context, 5)
        MARGINE_UPDOWN = DisplayUtil.convertDPtoPX(context, 40)
        GUIDE_TOP_MARGINE = DisplayUtil.convertDPtoPX(context, 25)
        GUIDE_SIZE = DisplayUtil.convertDPtoPX(context, 13)
    }

    override fun onDraw(canvas: Canvas) {
        // 真ん中上部の今回のBPMの表示
        mPaint.color = Color.BLACK
        mPaint.textSize = TEXT_SIZE_LARGE.toFloat()
        mPaint.textAlign = Align.CENTER
        mPaint.isAntiAlias = true
        if (mIsAverageMode) {
            canvas.drawText(mAverageBPM.toString(), (width / 2).toFloat(), MARGINE_UPDOWN.toFloat(), mPaint)
        } else {
            canvas.drawText(mCurrentBPM.toString(), (width / 2).toFloat(), MARGINE_UPDOWN.toFloat(), mPaint)
        }

        // 左下の目標BPMの表示
        mPaint.textSize = TEXT_SIZE_SMALL.toFloat()
        mPaint.textAlign = Align.LEFT
        if (mTargetBPM == 0) {
            canvas.drawText(context.getString(R.string.measurering), MARGINE_RIGHTLEFT.toFloat(),
                    (height - TEXT_SIZE_SMALL).toFloat(), mPaint)
        } else {
            canvas.drawText(mTargetBPM.toString(), MARGINE_RIGHTLEFT.toFloat(),
                    (height - TEXT_SIZE_SMALL).toFloat(), mPaint)
        }
        // 右上の平均値BPMの表示
        mPaint.textAlign = Align.RIGHT
        if (mIsAverageMode) {
            canvas.drawText(mCurrentBPM.toString(), (width - MARGINE_RIGHTLEFT).toFloat(), MARGINE_UPDOWN.toFloat(), mPaint)
        } else {
            canvas.drawText(mAverageBPM.toString(), (width - MARGINE_RIGHTLEFT).toFloat(), MARGINE_UPDOWN.toFloat(), mPaint)
        }
        if (mIsShowGuid) {
            // 円を描画する
            mPaint.style = Paint.Style.FILL
            mPaint.color = resources.getColor(R.color.curcle_pink)
            mPaint.textAlign = Align.CENTER
            mPaint.isAntiAlias = true
            canvas.drawCircle(width / 2 - TEXT_SIZE_LARGE * 1.5f, GUIDE_TOP_MARGINE.toFloat(), GUIDE_SIZE.toFloat(), mPaint)
        }

        super.onDraw(canvas)
    }

    fun setTargetBPM(value: Int) {
        // 目標BPMを設定
        mTargetBPM = value
        var i: Int
        var temp1: Long
        var temp2: Long
        // 平均値を出すためのバッファを初期化
        if (mTargetBPM != 0) {
            temp1 = (60000 * mBufLen / mTargetBPM).toLong()
            mSum = 0
            i = 0
            while (i < mBufLen - 1) {
                temp2 = (60000 * (mBufLen - 1 - i) / mTargetBPM).toLong()
                mBuf[i] = temp1 - temp2
                temp1 = temp2
                mSum += mBuf[i]
                i++
            }
            mBuf[i] = 0
            mBufIndex = i
        }
        mCurrentBPM = 0
        mAverageBPM = 0

    }

    fun setAvarageMode(value: Boolean) {
        this.mIsAverageMode = value
    }

    fun getBPM(delta: Long): Int {
        // 今回のBPMを設定
        mCurrentBPM = (60000 / delta).toInt()
        // 平均値の計算をしながらバッファを更新
        mSum = mSum + delta - mBuf[mBufIndex]
        mBuf[mBufIndex] = delta

        // 平均値を設定
        mAverageBPM = (60000 * mBufLen / mSum).toInt()
        // バッファのインデックスを更新
        mBufIndex++
        mBufIndex %= mBufLen
        postInvalidate()
        return if (mIsAverageMode)
            mAverageBPM
        else
            mCurrentBPM
    }

    fun showGuid() {
        // ガイド円を表示
        this.mIsShowGuid = true
        invalidate()

        if (mRunnable == null) {
            mRunnable = Runnable {
                // ガイド円の消去
                this@ValueView.mIsShowGuid = false
                invalidate()
                this@ValueView.mRunnable = null
            }

            // ガイド円の消去処理を予約
            mHandler.postDelayed(mRunnable!!, CIRCLE_SHOW_TIME.toLong())
        }
    }

    fun setBufLen(len: Int) {
        if (this.mBufLen != len) {
            this.mBufLen = len
            this.mBuf = LongArray(len)
            mBufIndex = 0
        }
    }
}
