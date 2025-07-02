package jp.yumyum

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class AboutActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about)
        val supportLink = "Produced by <a href=http://twitter.com/yu_yum>yu_yum</a>"
        val cs = Html.fromHtml(supportLink)
        val mTextView = findViewById<TextView>(R.id.supportLink)
        val mm = LinkMovementMethod.getInstance()
        mTextView.movementMethod = mm
        mTextView.text = cs

        val helpBtn = findViewById(R.id.helpBtn) as Button
        helpBtn.setOnClickListener(this)

        val iRootLayout = findViewById<View>(android.R.id.content).getRootView()
        ViewCompat.setOnApplyWindowInsetsListener(iRootLayout) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                top = insets.top, bottom = insets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }
    }

    override fun onClick(arg0: View) {
        // インテントの生成
        val intent = Intent(this, HelpActivity::class.java)

        // アクティビティの呼び出し
        startActivity(intent)
        finish()
    }

}
