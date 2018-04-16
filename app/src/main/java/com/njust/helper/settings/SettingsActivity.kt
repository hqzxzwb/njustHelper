package com.njust.helper.settings

import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import com.njust.helper.R
import com.njust.helper.activity.BaseActivity

class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().replace(R.id.holder, HolderFragment()).commit()
        }
    }

    override fun layoutRes(): Int {
        return R.layout.activity_settings
    }

    class HolderFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.preference)

            val modePreference = findPreference("mode") as ListPreference
            setListModeSummary(modePreference, modePreference.value)
            modePreference.setOnPreferenceChangeListener { preference, newValue ->
                setListModeSummary(preference, newValue as String)
                true
            }
        }

        private fun setListModeSummary(preference: Preference, index: String) {
            preference.summary = resources.getStringArray(R.array.pref_mode_entry)[Integer.parseInt(index)]
        }
    }
}
