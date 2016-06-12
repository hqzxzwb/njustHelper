package com.njust.helper.settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.njust.helper.R;

public class SettingsActivityV9 extends PreferenceActivity {
    private final Preference.OnPreferenceChangeListener mModeOnPreferenceListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            setListModeSummary(preference, (String) newValue);
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection deprecation
        addPreferencesFromResource(R.xml.preference_v9);

        ListPreference preference = (ListPreference) findPreference("mode");
        setListModeSummary(preference, preference.getValue());
        preference.setOnPreferenceChangeListener(mModeOnPreferenceListener);
    }

    private void setListModeSummary(Preference preference, String index) {
        preference.setSummary(getResources().getStringArray(R.array.pref_mode_entry)[Integer.parseInt(index)]);
    }
}
