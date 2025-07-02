package jp.yumyum

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.widget.Toast
import androidx.preference.CheckBoxPreference
import androidx.preference.EditTextPreference
import androidx.preference.Preference

import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class PrefFragment: PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    private var measureModePref: CheckBoxPreference? = null
    private var bpmPref: EditTextPreference? = null
    private var guidEnable: CheckBoxPreference? = null
    private var averageMode: CheckBoxPreference? = null
    private var averageCount: SeekBarDialogPreference? = null
    private var graphSencitivity: SeekBarDialogPreference? = null
    private var prefIntent: Intent? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref)
        prefIntent = Intent()
        context?.let {
            val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(it)

            // 計測モードの設定
            measureModePref = findPreference(getString(R.string.mesure_mode_key)) as CheckBoxPreference?
            measureModePref!!.onPreferenceChangeListener = this

            // ターゲットBPM設定
            bpmPref = findPreference(getString(R.string.target_bpm_key)) as EditTextPreference?
            // サマリーに現在の値を表示
            bpmPref!!.summary = getString(R.string.current_bpm) + sharedPreferences.getString(
                getString(R.string.target_bpm_key), "")!!
            bpmPref!!.onPreferenceChangeListener = this

            // テンポガイドの設定
            guidEnable = findPreference(getString(R.string.bpm_guid_key)) as CheckBoxPreference?
            guidEnable!!.onPreferenceChangeListener = this

            // 平均値モードの設定
            averageMode = findPreference(getString(R.string.average_mode_key)) as CheckBoxPreference?
            averageMode!!.onPreferenceChangeListener = this

            // 平均値モードで使用する値の数の設定
            averageCount = findPreference(getString(R.string.average_count_key)) as SeekBarDialogPreference?
            // サマリーに現在の値を表示
            averageCount!!.summary = getString(R.string.average_count_summary) + sharedPreferences.getInt(
                getString(R.string.average_count_key), 0)
            averageCount!!.onPreferenceChangeListener = this

            // グラフの描画感度
            graphSencitivity = findPreference(getString(R.string.graph_sencitivity_key)) as SeekBarDialogPreference?
            // サマリーに現在の値を表示
            graphSencitivity!!.summary = getString(R.string.graph_sencitivity_summary) + sharedPreferences.getInt(
                getString(R.string.graph_sencitivity_key), 0)
            graphSencitivity!!.onPreferenceChangeListener = this

        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
            ?.unregisterOnSharedPreferenceChangeListener(listener)
    }

    private val listener =
        OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == "KEY") {
                // setSummary等行う
            }
        }

    companion object {
        private const val PREF_KEY = "KEY"
    }

    // 設定値が変更されたときのリスナ
    override fun onPreferenceChange(arg0: Preference, arg1: Any?): Boolean {
        if (arg0 === bpmPref) {
            try {
                val bpm = Integer.parseInt(arg1 as String)
                if (bpm <= 0 || bpm > 999)
                    throw NumberFormatException()
            } catch (e: NumberFormatException) {
                // 入力された値が不正だった場合の処理
                Toast.makeText(context, R.string.tempo_caution, Toast.LENGTH_LONG)
                    .show()
                return false
            }

            // サマリーに新しく設定された値を反映
            bpmPref!!.summary = getString(R.string.current_bpm) + arg1

            activity?.setResult(Activity.RESULT_OK, prefIntent)
            return true
        } else if (arg0 === guidEnable) {
            activity?.setResult(Activity.RESULT_OK, prefIntent)
            return true
        } else if (arg0 === measureModePref) {
            activity?.setResult(Activity.RESULT_OK, prefIntent)
            return true
        } else if (arg0 === averageMode) {
            activity?.setResult(Activity.RESULT_OK, prefIntent)
            return true
        } else if (arg0 === averageCount) {
            this.averageCount!!.summary = getString(R.string.average_count_summary) + arg1 as Int
            activity?.setResult(Activity.RESULT_OK, prefIntent)
            return true
        } else if (arg0 === graphSencitivity) {
            this.graphSencitivity!!.summary = getString(R.string.graph_sencitivity_summary) + arg1 as Int
            activity?.setResult(Activity.RESULT_OK, prefIntent)
            return true
        }
        return false
    }
}