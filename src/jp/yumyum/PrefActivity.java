package jp.yumyum;

import com.hlidskialf.android.preference.SeekBarPreference;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class PrefActivity extends PreferenceActivity implements
        OnPreferenceChangeListener {
    private CheckBoxPreference measureModePref;
    private EditTextPreference bpmPref;
    private CheckBoxPreference guidEnable;
    private CheckBoxPreference averageMode;
    private SeekBarPreference averageCount;
    private SeekBarPreference graphSencitivity;
    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = new Intent();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        addPreferencesFromResource(R.xml.pref);

        // 計測モードの設定
        measureModePref = (CheckBoxPreference) findPreference(getString(R.string.mesure_mode_key));
        measureModePref.setOnPreferenceChangeListener(this);

        // ターゲットBPM設定
        bpmPref = (EditTextPreference) findPreference(getString(R.string.target_bpm_key));
        // サマリーに現在の値を表示
        bpmPref.setSummary(getString(R.string.current_bpm)
                + sharedPreferences.getString(
                        getString(R.string.target_bpm_key), ""));
        bpmPref.setOnPreferenceChangeListener(this);

        // テンポガイドの設定
        guidEnable = (CheckBoxPreference) findPreference(getString(R.string.bpm_guid_key));
        guidEnable.setOnPreferenceChangeListener(this);

        // 平均値モードの設定
        averageMode = (CheckBoxPreference) findPreference(getString(R.string.average_mode_key));
        averageMode.setOnPreferenceChangeListener(this);

        // 平均値モードで使用する値の数の設定
        averageCount = (SeekBarPreference) findPreference(getString(R.string.average_count_key));
        // サマリーに現在の値を表示
        averageCount.setSummary(getString(R.string.average_count_summary)
                + sharedPreferences.getInt(
                        getString(R.string.average_count_key), 0));
        averageCount.setOnPreferenceChangeListener(this);

        // グラフの描画感度
        graphSencitivity = (SeekBarPreference) findPreference(getString(R.string.graph_sencitivity_key));
        // サマリーに現在の値を表示
        graphSencitivity.setSummary(getString(R.string.graph_sencitivity_summary)
                + sharedPreferences.getInt(
                        getString(R.string.graph_sencitivity_key), 0));
        graphSencitivity.setOnPreferenceChangeListener(this);

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
            bpmPref.setSummary(getString(R.string.current_bpm) + arg1);

            setResult(Activity.RESULT_OK, intent);
            return true;
        } else if (arg0 == guidEnable) {
            setResult(Activity.RESULT_OK, intent);
            return true;
        } else if (arg0 == measureModePref) {
            setResult(Activity.RESULT_OK, intent);
            return true;
        } else if (arg0 == averageMode) {
            setResult(Activity.RESULT_OK, intent);
            return true;
        } else if (arg0 == averageCount) {
            this.averageCount.setSummary(getString(R.string.average_count_summary) + (Integer) arg1);
            setResult(Activity.RESULT_OK, intent);
            return true;
        } else if (arg0 == graphSencitivity) {
            this.graphSencitivity.setSummary(getString(R.string.graph_sencitivity_summary) + (Integer) arg1);
            setResult(Activity.RESULT_OK, intent);
            return true;
        }
        return false;
    }
}
