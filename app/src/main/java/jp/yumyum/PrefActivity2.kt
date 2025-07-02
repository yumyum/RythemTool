package jp.yumyum

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class PrefActivity2: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // fragmentManager → supportFragmentManager に変更
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, PrefFragment()).commit()
        val iRootLayout = findViewById<View>(android.R.id.content).getRootView()
        ViewCompat.setOnApplyWindowInsetsListener(iRootLayout) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                top = insets.top, bottom = insets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }
    }
}