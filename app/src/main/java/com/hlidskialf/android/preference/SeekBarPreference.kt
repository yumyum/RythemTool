/* The following code was written by Matthew Wiggins 
 * and is released under the APACHE 2.0 license 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Modifyed by yu_yum
 * http://twitter.com/yu_yum
 * http://blog.livedoor.jp/nobia1-android/
 */
package com.hlidskialf.android.preference

import jp.yumyum.R
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.preference.DialogPreference
import android.widget.SeekBar
import android.widget.TextView

class SeekBarPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), SeekBar.OnSeekBarChangeListener {

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

    var progress: Int
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
        var resouceId = attrs.getAttributeResourceValue(androidns,
                "dialogMessage", 0)
        if (resouceId != 0) {
            // リソースIDが見つかればリソースとして処理
            mDialogMessage = context.getString(resouceId)
        } else {
            // リソースIDが見つからなければ文字列として処理
            mDialogMessage = attrs
                    .getAttributeValue(androidns, "dialogMessage")
        }

        resouceId = attrs.getAttributeResourceValue(androidns, "text", 0)
        if (resouceId != 0) {
            mSuffix = context.getString(resouceId)
        } else {
            mSuffix = attrs.getAttributeValue(androidns, "text")
        }

        mMin = attrs.getAttributeIntValue(seekbarns, "min", 0)
        max = attrs.getAttributeIntValue(androidns, "max", 100)
        mTextColor = attrs.getAttributeIntValue(androidns, "text_color",
                Color.WHITE)
        dialogLayoutResource = R.layout.preference_dialog_seekber
    }


    override fun onDialogClosed(positiveResult: Boolean) {
        Log.d(TAG, "onDialogClosed")
        // キャンセルボタンが押されたら値を破棄
        if (positiveResult == false)
            return
        // 値が変更されていなければなにもしない
        if (mValue == tmpValue)
            return

        if (shouldPersist() && callChangeListener(tmpValue)) {
            progress = tmpValue
        }
        super.onDialogClosed(positiveResult)
    }

    override fun onBindDialogView(v: View) {
        Log.d(TAG, "onBindDialogView")
        super.onBindDialogView(v)
        mSplashText = v.findViewById(R.id.seekbar_splash_text) as TextView
        mSplashText!!.setTextColor(mTextColor)
        if (mDialogMessage != null)
            mSplashText!!.text = mDialogMessage

        mValueText = v.findViewById(R.id.seekbar_value_text) as TextView
        mValueText!!.setTextColor(mTextColor)
        mValueText!!.gravity = Gravity.CENTER_HORIZONTAL
        mValueText!!.textSize = 32f

        mSeekBar = v.findViewById(R.id.seekbar) as SeekBar

        mSeekBar!!.max = max - mMin
        mSeekBar!!.progress = mValue - mMin
        val t = mValue.toString()
        mValueText!!.text = if (mSuffix == null) t else t + mSuffix
        mSeekBar!!.setOnSeekBarChangeListener(this)
        return
    }

    /**
     * Adds the EditText widget of this preference to the dialog's view.
     *
     * @param dialogView
     * The dialog view.
     */
    protected fun onAddSeekBarToDialogView(dialogView: View, seekBar: SeekBar) {}

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        Log.d(TAG, "onGetDefaultValue : " + a.getInt(index, -1))
        return a.getInt(index, -1)
    }

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

    override fun onProgressChanged(seek: SeekBar, value: Int, fromTouch: Boolean) {
        Log.d(TAG, "onProgressChanged")
        tmpValue = value + mMin
        val t = tmpValue.toString()
        mValueText!!.text = if (mSuffix == null) t else t + mSuffix

    }

    override fun onStartTrackingTouch(arg0: SeekBar) {
        // TODO Auto-generated method stub

    }

    override fun onStopTrackingTouch(arg0: SeekBar) {
        // TODO Auto-generated method stub

    }

    companion object {
        private val androidns = "http://schemas.android.com/apk/res/android"
        private val seekbarns = "http://schemas.android.com/apk/res/jp.yumyum"
    }

}
