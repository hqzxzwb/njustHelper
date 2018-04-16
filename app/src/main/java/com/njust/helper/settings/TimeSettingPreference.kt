package com.njust.helper.settings

import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.os.Bundle
import android.preference.DialogPreference
import android.util.AttributeSet
import android.widget.TimePicker
import com.zwb.commonlibs.ui.TimePickerDialogFix
import java.util.*

class TimeSettingPreference(context: Context, attrs: AttributeSet)
    : DialogPreference(context, attrs), OnTimeSetListener {

    override fun showDialog(state: Bundle) {
        val time = getPersistedInt(DEFAULT_TIME)
        val hour = time / 60
        val minute = time % 60
        TimePickerDialogFix(context, this, hour, minute, true).show()
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val time = hourOfDay * 60 + minute
        persistInt(time)
        summary = getTimeString(time)
        val listener = onPreferenceChangeListener
        listener?.onPreferenceChange(this, time)
    }

    private fun getTimeString(time: Int): String {
        val hour = time / 60
        val minute = time % 60
        return String.format(Locale.getDefault(), "%1$02d:%2$02d", hour, minute)
    }

    override fun getSummary(): CharSequence {
        return getTimeString(getPersistedInt(DEFAULT_TIME))
    }

    companion object {
        private const val DEFAULT_TIME = 20 * 60 + 0
    }
}
