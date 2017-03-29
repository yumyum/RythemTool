/* The following code was written by Matthew Wiggins 
 * and is released under the APACHE 2.0 license 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Modifyed by yu_yum
 * http://twitter.com/yu_yum
 * http://blog.livedoor.jp/nobia1-android/
 */
package com.hlidskialf.android.preference;

import jp.yumyum.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.preference.DialogPreference;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekBarPreference extends DialogPreference implements
		SeekBar.OnSeekBarChangeListener {
	private static final String androidns = "http://schemas.android.com/apk/res/android";
	private static final String seekbarns = "http://schemas.android.com/apk/res/jp.yumyum";

	private SeekBar mSeekBar;
	private TextView mSplashText, mValueText;

	private String mDialogMessage, mSuffix;
	private int mDefault, mMin, mMax, mValue, tmpValue;
	private int mTextColor;

	private final String TAG = "SeekBarPref";

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "Constructer");

		// リソースIDで書かれているかどうかを判定
		int resouceId = attrs.getAttributeResourceValue(androidns,
				"dialogMessage", 0);
		if (resouceId != 0) {
			// リソースIDが見つかればリソースとして処理
			mDialogMessage = context.getString(resouceId);
		} else {
			// リソースIDが見つからなければ文字列として処理
			mDialogMessage = attrs
					.getAttributeValue(androidns, "dialogMessage");
		}

		resouceId = attrs.getAttributeResourceValue(androidns, "text", 0);
		if (resouceId != 0) {
			mSuffix = context.getString(resouceId);
		} else {
			mSuffix = attrs.getAttributeValue(androidns, "text");
		}

		mMin = attrs.getAttributeIntValue(seekbarns, "min", 0);
		mMax = attrs.getAttributeIntValue(androidns, "max", 100);
		mTextColor = attrs.getAttributeIntValue(androidns, "text_color",
				Color.WHITE);
		setDialogLayoutResource(R.layout.preference_dialog_seekber);
	}


	@Override
	protected void onDialogClosed(boolean positiveResult) {
		Log.d(TAG, "onDialogClosed");
		// キャンセルボタンが押されたら値を破棄
		if (positiveResult == false)
			return;
		// 値が変更されていなければなにもしない
		if (mValue == tmpValue)
			return;

		if (shouldPersist() && callChangeListener(new Integer(tmpValue))) {
			setProgress(tmpValue);
		}
		super.onDialogClosed(positiveResult);
	}

	@Override
	protected void onBindDialogView(View v) {
		Log.d(TAG, "onBindDialogView");
		super.onBindDialogView(v);
		mSplashText = (TextView) v.findViewById(R.id.seekbar_splash_text);
		mSplashText.setTextColor(mTextColor);
		if (mDialogMessage != null)
			mSplashText.setText(mDialogMessage);

		mValueText = (TextView) v.findViewById(R.id.seekbar_value_text);
		mValueText.setTextColor(mTextColor);
		mValueText.setGravity(Gravity.CENTER_HORIZONTAL);
		mValueText.setTextSize(32);

		mSeekBar = (SeekBar) v.findViewById(R.id.seekbar);

		mSeekBar.setMax(mMax - mMin);
		mSeekBar.setProgress(mValue - mMin);
		String t = String.valueOf(mValue);
		mValueText.setText(mSuffix == null ? t : t.concat(mSuffix));
		mSeekBar.setOnSeekBarChangeListener(this);
		return;
	}

	/**
	 * Adds the EditText widget of this preference to the dialog's view.
	 * 
	 * @param dialogView
	 *            The dialog view.
	 */
	protected void onAddSeekBarToDialogView(View dialogView, SeekBar seekBar) {
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		Log.d(TAG, "onGetDefaultValue : " + a.getInt(index, -1));
		return a.getInt(index, -1);
	}

	@Override
	protected void onSetInitialValue(boolean restore, Object defaultValue) {
		Log.d(TAG, "onSetInitialValue : " + defaultValue);
		super.onSetInitialValue(restore, defaultValue);
		int value;
		if (restore)
			value = shouldPersist() ? getPersistedInt(mDefault) : 0;
		else
			value = (Integer) defaultValue;
		setProgress(value);
	}

	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
		Log.d(TAG, "onProgressChanged");
		tmpValue = value + mMin;
		String t = String.valueOf(tmpValue);
		mValueText.setText(mSuffix == null ? t : t.concat(mSuffix));

	}

	public void setMax(int max) {
		mMax = max;
	}

	public int getMax() {
		return mMax;
	}

	public void setProgress(int progress) {
		mValue = progress;
		if (mSeekBar != null)
			mSeekBar.setProgress(mValue - mMin);
		persistInt(mValue);
	}

	public int getProgress() {
		Log.d(TAG,"getProgress : " + mValue);
		return mValue;
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

}
