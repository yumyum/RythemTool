package jp.yumyum;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class PrefActivity extends PreferenceActivity implements
		OnPreferenceChangeListener {
	private EditTextPreference bpmPref;
	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intent = new Intent();
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		addPreferencesFromResource(R.xml.pref);
		bpmPref = (EditTextPreference) findPreference(getString(R.string.target_bpm_key));
		// サマリーに現在の値を表示
		bpmPref.setSummary(getString(R.string.current_bpm)
				+ " "
				+ sharedPreferences.getString(
						getString(R.string.target_bpm_key), ""));
		bpmPref.setOnPreferenceChangeListener(this);

	}

	// 設定値が変更されたときのリスナ
	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		if (arg0 == bpmPref) {
			try {
				int bpm = Integer.parseInt((String) arg1);
				if (bpm <= 0 || bpm > 999)
					throw (new NumberFormatException());
			} catch (NumberFormatException e) {
				// 入力された値が不正だった場合の処理
				Toast.makeText(this, R.string.tempo_caution, Toast.LENGTH_LONG)
						.show();
				return false;
			}
			// サマリーに新しく設定された値を反映
			bpmPref.setSummary(getString(R.string.current_bpm) + " " + arg1);

			setResult(Activity.RESULT_OK, intent);
			return true;
		}
		return false;
	}
}
