package jp.yumyum

import android.content.Context
import android.content.DialogInterface
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference


class SeekBarDialogPreference(private val context: Context, attrs: AttributeSet?) :
    Preference(context, attrs), SeekBar.OnSeekBarChangeListener {
    private var mSeekBar: SeekBar? = null
    private var mSplashText: TextView? = null
    private var mValueText: TextView? = null

    private var mDialogMessage: String? = null
    private var mSuffix: String? = null
    private val mDefault: Int = 0
    private val mMin: Int
    var max: Int = 0
    private var mValue: Int = 0
    private var tmpValue: Int = 0
    private val mTextColor: Int

    private val TAG = "SeekBarPref"

    private var progress: Int
        get() {
            Log.d(TAG, "getProgress : $mValue")
            return mValue
        }
        set(progress) {
            mValue = progress
            if (mSeekBar != null)
                mSeekBar!!.progress = mValue - mMin
            persistInt(mValue)
        }

    init {
        Log.d(TAG, "Constructer")

        // リソースIDで書かれているかどうかを判定
        var resouceId = attrs?.getAttributeResourceValue(
            androidns,
            "dialogMessage", 0) ?: 0
        if (resouceId != 0) {
            // リソースIDが見つかればリソースとして処理
            mDialogMessage = context.getString(resouceId)
        } else {
            // リソースIDが見つからなければ文字列として処理
            mDialogMessage = attrs
                ?.getAttributeValue(androidns, "dialogMessage")
        }

        resouceId = attrs?.getAttributeResourceValue(androidns, "text", 0) ?: 0
        if (resouceId != 0) {
            mSuffix = context.getString(resouceId)
        } else {
            mSuffix = attrs?.getAttributeValue(androidns, "text")
        }

        mMin = attrs?.getAttributeIntValue(seekbarns, "min", 0) ?:0
        max = attrs?.getAttributeIntValue(androidns, "max", 100) ?: 100
        mTextColor = attrs?.getAttributeIntValue(
            androidns, "text_color",
            Color.WHITE) ?: Color.WHITE
    }


    override fun onClick() { //what happens when clicked on the preference
        val dialog = LayoutInflater.from(context).inflate(R.layout.preference_dialog_seekber, null)
        mSplashText = dialog.findViewById(R.id.seekbar_splash_text) as TextView
        mSplashText!!.setTextColor(mTextColor)
        if (mDialogMessage != null)
            mSplashText!!.text = mDialogMessage

        mValueText = dialog.findViewById(R.id.seekbar_value_text) as TextView
        mValueText!!.setTextColor(mTextColor)
        mValueText!!.gravity = Gravity.CENTER_HORIZONTAL
        mValueText!!.textSize = 32f

        mSeekBar = dialog.findViewById(R.id.seekbar) as SeekBar

        mSeekBar!!.max = max - mMin
        mSeekBar!!.progress = mValue - mMin
        val t = mValue.toString()
        mValueText!!.text = if (mSuffix == null) t else t + mSuffix
        mSeekBar!!.setOnSeekBarChangeListener(this)

        val builder = AlertDialog.Builder(context)
        builder.setView(dialog)
            .setPositiveButton(
                "OK"
            ) { dialog: DialogInterface?, which: Int ->
                val preferenceKey =
                    key //You can update the SharedPreference with the key
                if (progress != tmpValue) {
                    progress = tmpValue
                }
                if (shouldPersist()) {
                    callChangeListener(progress)
                }
            }
            .create().show()
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        Log.d(TAG, "onGetDefaultValue : " + a.getInt(index, -1))
        return a.getInt(index, -1)
    }

    @Deprecated("Deprecated in Java")
    override fun onSetInitialValue(restore: Boolean, defaultValue: Any?) {
        Log.d(TAG, "onSetInitialValue : $defaultValue")
        super.onSetInitialValue(restore, defaultValue)
        val value: Int
        if (restore)
            value = if (shouldPersist()) getPersistedInt(mDefault) else 0
        else
            value = defaultValue as Int
        progress = value
    }

    companion object {
        private val androidns = "http://schemas.android.com/apk/res/android"
        private val seekbarns = "http://schemas.android.com/apk/res/jp.yumyum"
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        Log.d(TAG, "onProgressChanged")
        tmpValue = p1 + mMin
        val t = tmpValue.toString()
        mValueText!!.text = if (mSuffix == null) t else t + mSuffix
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }
}