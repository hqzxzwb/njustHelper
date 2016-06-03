package com.njust.helper.settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

import com.njust.helper.R;
import com.njust.helper.activity.BaseActivity;

public class SettingsActivity extends BaseActivity {
    private HolderFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (fragment == null) {
                fragment = new HolderFragment();
            }
            getFragmentManager().beginTransaction().replace(R.id.holder, fragment).commit();
        }
    }

    @Override
    protected int layoutRes() {
        return R.layout.activity_settings;
    }

    public static class HolderFragment extends PreferenceFragment {
        private final OnPreferenceChangeListener mModeOnPreferenceListener = new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                setListModeSummary(preference, (String) newValue);
                return true;
            }
        };

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preference);

            ListPreference preference = (ListPreference) findPreference("mode");
            setListModeSummary(preference, preference.getValue());
            preference.setOnPreferenceChangeListener(mModeOnPreferenceListener);
        }

        private void setListModeSummary(Preference preference, String index) {
            preference.setSummary(getResources().getStringArray(R.array.pref_mode_entry)[Integer.parseInt(index)]);
        }
    }
}
