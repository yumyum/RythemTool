package jp.yumyum

import com.hlidskialf.android.preference.SeekBarPreference

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.EditTextPreference
import android.preference.Preference
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.widget.Toast

class PrefActivity : PreferenceActivity(), OnPreferenceChangeListener {
    private var measureModePref: CheckBoxPreference? = null
    private var bpmPref: EditTextPreference? = null
    private var guidEnable: CheckBoxPreference? = null
    private var averageMode: CheckBoxPreference? = null
    private var averageCount: SeekBarPreference? = null
    private var graphSencitivity: SeekBarPreference? = null
    private var prefIntent: Intent? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefIntent = Intent()
        val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this)

        addPreferencesFromResource(R.xml.pref)

        // 計測モードの設定
        measureModePref = findPreference(getString(R.string.mesure_mode_key)) as CheckBoxPreference
        measureModePref!!.onPreferenceChangeListener = this

        // ターゲットBPM設定
        bpmPref = findPreference(getString(R.string.target_bpm_key)) as EditTextPreference
        // サマリーに現在の値を表示
        bpmPref!!.summary = getString(R.string.current_bpm) + sharedPreferences.getString(
                getString(R.string.target_bpm_key), "")!!
        bpmPref!!.onPreferenceChangeListener = this

        // テンポガイドの設定
        guidEnable = findPreference(getString(R.string.bpm_guid_key)) as CheckBoxPreference
        guidEnable!!.onPreferenceChangeListener = this

        // 平均値モードの設定
        averageMode = findPreference(getString(R.string.average_mode_key)) as CheckBoxPreference
        averageMode!!.onPreferenceChangeListener = this

        // 平均値モードで使用する値の数の設定
        averageCount = findPreference(getString(R.string.average_count_key)) as SeekBarPreference
        // サマリーに現在の値を表示
        averageCount!!.summary = getString(R.string.average_count_summary) + sharedPreferences.getInt(
                getString(R.string.average_count_key), 0)
        averageCount!!.onPreferenceChangeListener = this

        // グラフの描画感度
        graphSencitivity = findPreference(getString(R.string.graph_sencitivity_key)) as SeekBarPreference
        // サマリーに現在の値を表示
        graphSencitivity!!.summary = getString(R.string.graph_sencitivity_summary) + sharedPreferences.getInt(
                getString(R.string.graph_sencitivity_key), 0)
        graphSencitivity!!.onPreferenceChangeListener = this

    }

    // 設定値が変更されたときのリスナ
    override fun onPreferenceChange(arg0: Preference, arg1: Any): Boolean {
        if (arg0 === bpmPref) {
            try {
                val bpm = Integer.parseInt(arg1 as String)
                if (bpm <= 0 || bpm > 999)
                    throw NumberFormatException()
            } catch (e: NumberFormatException) {
                // 入力された値が不正だった場合の処理
                Toast.makeText(this, R.string.tempo_caution, Toast.LENGTH_LONG)
                        .show()
                return false
            }

            // サマリーに新しく設定された値を反映
            bpmPref!!.summary = getString(R.string.current_bpm) + arg1

            setResult(Activity.RESULT_OK, prefIntent)
            return true
        } else if (arg0 === guidEnable) {
            setResult(Activity.RESULT_OK, prefIntent)
            return true
        } else if (arg0 === measureModePref) {
            setResult(Activity.RESULT_OK, prefIntent)
            return true
        } else if (arg0 === averageMode) {
            setResult(Activity.RESULT_OK, prefIntent)
            return true
        } else if (arg0 === averageCount) {
            this.averageCount!!.summary = getString(R.string.average_count_summary) + arg1 as Int
            setResult(Activity.RESULT_OK, prefIntent)
            return true
        } else if (arg0 === graphSencitivity) {
            this.graphSencitivity!!.summary = getString(R.string.graph_sencitivity_summary) + arg1 as Int
            setResult(Activity.RESULT_OK, prefIntent)
            return true
        }
        return false
    }
}
