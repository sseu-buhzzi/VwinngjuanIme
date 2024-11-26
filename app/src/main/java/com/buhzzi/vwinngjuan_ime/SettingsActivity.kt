package com.buhzzi.vwinngjuan_ime

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.buhzzi.vwinngjuan_ime.kit.scInputMethodManager
import java.util.Date

class SettingsActivity : AppCompatActivity() {
	companion object {
		fun showImPicker(context: Context) {
			context.scInputMethodManager?.run { showInputMethodPicker() }
				?: Toast.makeText(context, R.string.im_picker_unavailable, Toast.LENGTH_SHORT).show()
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.settings_activity)
		if (savedInstanceState === null) {
			supportFragmentManager.beginTransaction()
				.replace(R.id.settingsFrameLayout, SettingsFragment())
				.commitNow()
		}
		findViewById<TextView>(R.id.testTextView).text = Date().toString()
	}
}

class SettingsFragment : PreferenceFragmentCompat() {
	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		setPreferencesFromResource(R.xml.preferences, rootKey)

		findPreference<Preference>(getString(R.string.im_settings_key))!!.apply {
			setOnPreferenceClickListener {
				startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
				true
			}
		}

		findPreference<Preference>(getString(R.string.im_picker_key))!!.apply {
			setOnPreferenceClickListener {
				SettingsActivity.showImPicker(context)
				true
			}
		}

		findPreference<ListPreference>(getString(R.string.night_mode_key))!!.apply {
			entryValues = sequenceOf(
				AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
				AppCompatDelegate.MODE_NIGHT_NO,
				AppCompatDelegate.MODE_NIGHT_YES,
				AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
			).map { it.toString(16) }.toList().toTypedArray()
			val nightModeMap = entryValues.zip(entries).toMap()
			val updateValue = { nightModeEntryValue: String ->
				summary = getString(R.string.night_mode_summary, nightModeMap[nightModeEntryValue])
				val nightMode = nightModeEntryValue.toInt(16)
				if (nightMode != AppCompatDelegate.getDefaultNightMode()) {
					AppCompatDelegate.setDefaultNightMode(nightMode)
					(context as SettingsActivity).recreate()
				}
			}
			setOnPreferenceChangeListener { _, newValue ->
				runCatching { updateValue(newValue as String) }.isSuccess
			}
			if (value === null) value = entryValues[0] as String
			updateValue(value)
		}

		findPreference<EditTextPreference>(getString(R.string.kb_height_key))!!.apply {
			val updateValue = { kbHeight: String ->
				summary = getString(R.string.kb_height_summary, kbHeight.toInt())
			}
			setOnPreferenceChangeListener { _, newValue ->
				runCatching { updateValue(newValue as String) }.isSuccess
			}
			if (text === null) text = "512"
			updateValue(text!!)
		}
	}
}
