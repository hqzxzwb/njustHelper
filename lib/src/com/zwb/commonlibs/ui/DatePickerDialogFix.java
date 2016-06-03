package com.zwb.commonlibs.ui;

import android.app.DatePickerDialog;
import android.content.Context;

import java.util.Calendar;

/**
 * 修复DatePickerDialog取消时也会执行改变的问题
 *
 * @author zwb
 */
public class DatePickerDialogFix extends DatePickerDialog {
    public DatePickerDialogFix(Context context, OnDateSetListener callBack,
                               int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }

    @Override
    protected void onStop() {
    }
}