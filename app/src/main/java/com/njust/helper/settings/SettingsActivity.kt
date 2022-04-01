package com.njust.helper.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.njust.helper.R

class SettingsActivity : AppCompatActivity(R.layout.activity_settings) {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (savedInstanceState == null) {
      supportFragmentManager.beginTransaction().replace(R.id.holder, HolderFragment()).commit()
    }
  }

  class HolderFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
      addPreferencesFromResource(R.xml.preference)

      val modePreference = findPreference<ListPreference>("mode")!!
      setListModeSummary(modePreference, modePreference.value)
      modePreference.setOnPreferenceChangeListener { preference, newValue ->
        setListModeSummary(preference, newValue as String)
        CourseAlarms.registerCourseAlarm(requireContext())
        true
      }

      val timePreference = findPreference<TimeSettingPreference>("course_time")!!
      timePreference.setOnPreferenceChangeListener { _, _ ->
        CourseAlarms.registerCourseAlarm(requireContext())
        true
      }
    }

    private fun setListModeSummary(preference: Preference, index: String) {
      preference.summary = resources.getStringArray(R.array.pref_mode_entry)[index.toInt()]
    }
  }
}
