package com.zwb.commonlibs.ui

import android.app.DatePickerDialog
import android.content.Context

/**
 * 修复DatePickerDialog取消时也会执行改变的问题
 *
 * @author zwb
 */
class DatePickerDialogFix(context: Context, listener: OnDateSetListener?, year: Int, month: Int, dayOfMonth: Int)
    : DatePickerDialog(context, listener, year, month, dayOfMonth) {
    override fun onStop() {
    }
}
