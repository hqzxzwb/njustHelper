package com.zwb.commonlibs.ui;

import android.app.TimePickerDialog;
import android.content.Context;

/**
 * 修复TimePickerDialog取消时也会执行改变的问题
 *
 * @author zwb
 */
public class TimePickerDialogFix extends TimePickerDialog {

    public TimePickerDialogFix(Context context, OnTimeSetListener callBack,
                               int hourOfDay, int minute, boolean is24HourView) {
        super(context, callBack, hourOfDay, minute, is24HourView);
    }

    @Override
    protected void onStop() {
    }
}