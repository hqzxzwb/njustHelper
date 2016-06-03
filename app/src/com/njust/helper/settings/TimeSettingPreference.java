package com.njust.helper.settings;

import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.widget.TimePicker;

import com.zwb.commonlibs.ui.TimePickerDialogFix;

import java.util.Locale;

public class TimeSettingPreference extends DialogPreference implements OnTimeSetListener {

    private static final int DEFAULT_TIME = 1200;

    public TimeSettingPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void showDialog(Bundle state) {
        int time = getPersistedInt(DEFAULT_TIME);
        int hour = time / 60;
        int minute = time % 60;
        new TimePickerDialogFix(getContext(), this, hour, minute, true).show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        int time = hourOfDay * 60 + minute;
        persistInt(time);
        setSummary(getTimeString(time));
        OnPreferenceChangeListener listener = getOnPreferenceChangeListener();
        if (listener != null) {
            listener.onPreferenceChange(this, time);
        }
    }

    private String getTimeString(int time) {
        int hour = time / 60;
        int minute = time % 60;
        return String.format(Locale.getDefault(), "%1$02d:%2$02d", hour, minute);
    }

    @Override
    public CharSequence getSummary() {
        return getTimeString(getPersistedInt(DEFAULT_TIME));
    }
}
