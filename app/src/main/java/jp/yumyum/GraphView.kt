package jp.yumyum

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.view.View
import android.preference.PreferenceManager
import net.yu_yum.android.debug.YLog
import net.yu_yum.utils.DisplayUtil

internal class GraphView// コンストラクタで幅と高さを指定
(context: Context, width: Int, private val winHeight: Int, private val mValueView: ValueView) : View(context) {
    private var mWidth: Int = 0
    private var winWidth: Int = 0
    private val processHandler: Handler
    private var lastCursor: Int = 0
    private var cursor: Int = 0
    private val mBitmap: Bitmap
    private val mCanvas = Canvas()
    private var runnable: Runnable? = null
    private var nextGuid: Int = 0
    private var lastValueX: Int = 0
    private var lastValueY: Int = 0
    private var nextValueY = 0
    // 目標BPMの設定
    // 画面上に表示するためにvalueViewが持っている目標値も変更
    var targetBpm = 0
        private set(bpm) {
            if (bpm == 0) {
                field = 0
                repeatInterval = 0
            } else {
                field = bpm
                repeatInterval = 60000 / targetBpm / 2
            }
            mValueView.setTargetBPM(bpm)
        }
    private var lastTime: Long = 0
    private var backCount = 0
    private var isGuidEnable: Boolean = false
    private var isEndOfGraph = false
    private var isMeasuring: Boolean = false
    private var measureCount: Int = 0
    private var measureLast: Long = 0
    private var measureSum: Long = 0
    private var sencitivity: Int = 0
    private val mPaint: Paint

    private var repeatInterval = 0

    // 計測モード時、計測に使用するタップ回数
    private val sensitivityParam: Int = DisplayUtil.convertDPtoPX(context, 1)
    private val measureMax = 5
    private val forwardPics: Int = DisplayUtil.convertDPtoPX(context, 1)
    private val lineWidth: Int = DisplayUtil.convertDPtoPX(context, 1)

    // グラフの最大幅
    private val graphMaxWidth: Int = DisplayUtil.convertDPtoPX(context, 1000)
    private val sharedPreferences: SharedPreferences

    val isScrolling: Boolean
        get() = runnable != null

    init {

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context)

        winWidth = width
        mWidth = winWidth
        processHandler = Handler()
        cursor = 5
        lastCursor = cursor
        lastValueX = lastCursor
        lastValueY = winHeight / 2

        // 画面リセットのたびに行う必要のあるフィールドの初期化はリセットメソッドにまとめた
        reset()

        // 裏で描画するためのビットマップを作成
        mBitmap = Bitmap.createBitmap(graphMaxWidth, winHeight,
                Bitmap.Config.RGB_565)
        mCanvas.setBitmap(mBitmap)

        mPaint = Paint()

        // 背景を白で塗る
        initCanvas()

    }

    override fun onDraw(canvas: Canvas) {
        YLog.d("GraphView", "YUM onDraw")
        super.onDraw(canvas)

        // ガイド横線を引く
        mPaint.color = resources.getColor(R.color.line_gray)

        if (cursor > lastCursor) {
            mCanvas.drawLine(lastCursor.toFloat(), (winHeight / 2).toFloat(), cursor.toFloat(), (winHeight / 2).toFloat(),
                    mPaint)

            // ガイド縦線を引く
            if (cursor >= nextGuid) {
                mCanvas.drawLine(nextGuid.toFloat(), 10f, nextGuid.toFloat(), (winHeight - 10).toFloat(), mPaint)
                nextGuid += winWidth
            }
        }

        // 青い線を引く
        if (nextValueY != 0) {
            mPaint.color = resources.getColor(R.color.line_blue)
            mPaint.strokeWidth = lineWidth.toFloat()

            mCanvas.drawLine(lastValueX.toFloat(), lastValueY.toFloat(), cursor.toFloat(), nextValueY.toFloat(), mPaint)
            lastValueX = cursor
            lastValueY = nextValueY
            nextValueY = 0
        }

        // mBitmapの内容を画面のキャンバスへ描く
        canvas.drawBitmap(mBitmap, 0f, 0f, null)

        // targetBPMの４倍の時間タップされなければ停止
        if (cursor - lastValueX > forwardPics * 8) {
            this.stopScroll()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(mWidth, winHeight)
        layout(0, 0, mWidth, winHeight)
    }

    private fun addWidth(value: Int) {
        mWidth += value
    }

    private fun forwordCursol(value: Int) {
        if (cursor + 5 >= graphMaxWidth) {
            endOfGraphArea()
        }
        if (cursor > lastCursor)
            lastCursor = cursor
        cursor += value
        backCount += value
        if (cursor + 5 > mWidth) {
            addWidth(cursor + 5 - mWidth)
        }
    }

    private fun endOfGraphArea() {
        backCount = 0
        stopScroll()
        val dlg: AlertDialog.Builder = AlertDialog.Builder(this.context)
        dlg.setTitle(R.string.EndOfGraphArea)
        dlg.setMessage(R.string.pleaseReset)
        dlg.show()
        isEndOfGraph = true

        return
    }

    private fun startScroll(): Boolean {
        if (runnable == null) {
            runnable = object : Runnable {
                private var guid = true

                override fun run() {

                    // 2.次回処理をセット
                    processHandler.postDelayed(this, repeatInterval.toLong())

                    // 3.繰り返し処理
                    // 単純にforwordCursol(4)でもいいがこういう書き方もできるということで
                    this@GraphView.forwordCursol(forwardPics)
                    requestLayout()
                    // テンポガイド表示
                    if (isGuidEnable) {
                        guid = if (guid) {
                            mValueView.showGuid()
                            false
                        } else {
                            true
                        }
                    }

                    invalidate()

                }
            }

            // 1.初回実行
            processHandler.postDelayed(runnable!!, repeatInterval.toLong())
        }
        return true
    }

    fun stopScroll() {
        cursor -= backCount
        backCount = 0
        if (runnable != null) {
            processHandler.removeCallbacks(runnable!!)
            runnable = null
            // ShowAreaを取得してスクロールバー表示
            val parent = parent as ShowArea
            parent.isHorizontalScrollBarEnabled = true

            // 前回のタップ時間をクリア
            lastTime = 0
        }
        if (isMeasuring) {
            measureCount = 0
            measureLast = 0
            measureSum = 0
        }
    }

    fun tap(time: Long) {
        // グラフ終端状態ならなにもしない
        if (isEndOfGraph)
            return
        // 計測中処理
        if (isMeasuring) {
            measureCount++
            if (measureCount > 1)
                measureSum += time - measureLast
            measureLast = time
            if (measureCount == measureMax) {
                isMeasuring = false
                val estimateBpm = (60000 / (measureSum / (measureMax - 1))).toInt()
                targetBpm = estimateBpm
                lastTime = time
            }
            return

        }
        if (!isScrolling) {
            startScroll()
        }
        // 2回目以降のタップでBPMを計算
        if (lastTime != 0L) {
            val delta = ((mValueView.getBPM(time - lastTime) - targetBpm)
                    * sencitivity * sensitivityParam)
            nextValueY = winHeight / 2 - delta * lineWidth
        }
        lastTime = time
        backCount = 0
    }

    private fun initCanvas() {
        mCanvas.drawColor(Color.WHITE)
        // // 灰色のガイド線を描画
        val p = Paint()
        p.color = resources.getColor(R.color.line_gray)
        // mCanvas.drawLine(5, height / 2, width - 5, height / 2, p);
        mCanvas.drawLine((winWidth / 2).toFloat(), 10f, (winWidth / 2).toFloat(), (winHeight - 10).toFloat(), p)
        nextGuid = (winWidth * 1.5).toInt()

        requestLayout()
    }

    // リセット処理。数値の初期化と設定値の読み込みはここでやる。
    fun reset() {
        stopScroll()
        mWidth = winWidth
        cursor = 5
        lastCursor = cursor
        lastValueX = lastCursor
        lastValueY = winHeight / 2
        nextValueY = 0
        lastTime = 0

        isEndOfGraph = false
        measureCount = 0
        measureSum = 0
        measureLast = 0

        val context = context

        initCanvas()
        invalidate()

        mValueView.setBufLen(sharedPreferences.getInt(
                context.getString(R.string.average_count_key), 4))
        isMeasuring = sharedPreferences.getBoolean(
                context.getString(R.string.mesure_mode_key), false)
        targetBpm = if (isMeasuring) {
            0
        } else {
            val bpm = Integer.parseInt(sharedPreferences.getString(
                    context.getString(R.string.target_bpm_key), "0")!!)
            bpm
        }
        isGuidEnable = sharedPreferences.getBoolean(
                context.getString(R.string.bpm_guid_key), false)

        sencitivity = sharedPreferences.getInt(context.getString(R.string.graph_sencitivity_key), 1)

        mValueView.setAvarageMode(sharedPreferences.getBoolean(
                context.getString(R.string.average_mode_key), true))

    }
}