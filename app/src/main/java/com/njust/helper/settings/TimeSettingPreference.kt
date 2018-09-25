package com.njust.helper.settings

import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.util.AttributeSet
import android.widget.TimePicker
import androidx.preference.Preference
import com.zwb.commonlibs.ui.TimePickerDialogFix
import java.util.*

private const val DEFAULT_TIME = 20 * 60 + 0

class TimeSettingPreference(context: Context, attrs: AttributeSet?)
    : Preference(context, attrs), OnTimeSetListener {

    override fun onClick() {
        val time = getPersistedInt(DEFAULT_TIME)
        val hour = time / 60
        val minute = time % 60
        TimePickerDialogFix(context, this, hour, minute, true).show()
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val time = hourOfDay * 60 + minute
        persistInt(time)
        summary = getTimeString(time)
        onPreferenceChangeListener?.onPreferenceChange(this, time)
    }

    private fun getTimeString(time: Int): String {
        val hour = time / 60
        val minute = time % 60
        return String.format(Locale.getDefault(), "%1$02d:%2$02d", hour, minute)
    }

    override fun getSummary(): CharSequence {
        return getTimeString(getPersistedInt(DEFAULT_TIME))
    }
}
