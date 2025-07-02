package jp.yumyum

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.preference.PreferenceManager

class RhythmToolActivity : AppCompatActivity() {
    private var mShowArea: ShowArea? = null
    private var mTapArea: TapArea? = null
    private var rootLayout: LinearLayout? = null

    /** Called when the activity is first created.  */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceManager.setDefaultValues(this, R.xml.pref, true)
        setContentView(R.layout.main)
        // メインのLinearLayoutを取得
        if (rootLayout == null) {
            rootLayout = findViewById(R.id.Linear01) as LinearLayout
            val iRootLayout = findViewById<View>(android.R.id.content).getRootView()
            ViewCompat.setOnApplyWindowInsetsListener(iRootLayout) { v, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.updatePadding(
                    top = insets.top, bottom = insets.bottom
                )
                WindowInsetsCompat.CONSUMED
            }


            // レイアウトパラメータ作成
            val lParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )
//            lParam.topMargin = 500

            // 表示エリア作成、LinearLayoutへ登録
            // FrameLayoutを使用する
            val fl = FrameLayout(this)
            fl.layoutParams = lParam
            val vv = ValueView(this)
            mShowArea = ShowArea(this, vv)
            mShowArea!!.layoutParams = lParam
            // FrameLayoutにShowAreaとValueViewを追加
            fl.addView(mShowArea)
            fl.addView(vv)
            // LinearLayoutにFrameLayoutを追加
            rootLayout!!.addView(fl)

            // タップエリア作成、LinearLayoutへ登録
            mTapArea = TapArea(this, mShowArea!!)
            mTapArea!!.layoutParams = lParam
            rootLayout!!.addView(mTapArea)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        val intent: Intent
        when (item.itemId) {
            R.id.mReset -> {
                mShowArea!!.reset()
                return true
            }

            R.id.mOption -> {
                // インテントの生成
                intent = Intent(this, PrefActivity2::class.java)

                // アクティビティの呼び出し
                startActivityForResult(intent, REQUEST_OPTION)

                return true
            }

            R.id.mAbout -> {
                // インテントの生成
                intent = Intent(this, AboutActivity::class.java)

                // アクティビティの呼び出し
                startActivity(intent)
                return true
            }
        }
        return false
    }

    // アクティビティ呼び出し結果の取得
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller
    ) {
        if (requestCode == REQUEST_OPTION && resultCode == RESULT_OK) {
            // 設定値が変更された場合はグラフを初期化。初期化処理で設定値を読み込んでいる。
            mShowArea!!.reset()
        }
    }

    override fun onPause() {
        if (mShowArea != null)
            mShowArea!!.stopScroll()
        super.onPause()
    }

    companion object {
        internal const val REQUEST_OPTION = 1
    }

}
