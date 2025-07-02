package jp.yumyum

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.help)
        val iRootLayout = findViewById<View>(android.R.id.content).getRootView()
        ViewCompat.setOnApplyWindowInsetsListener(iRootLayout) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                top = insets.top, bottom = insets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }


        super.onCreate(savedInstanceState)
    }

}
