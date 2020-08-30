package com.zwb.commonlibs.ui

import android.app.TimePickerDialog
import android.content.Context

/**
 * Created by zhuwenbo on 2017/10/27.
 *
 * 修复TimePickerDialog取消时也会执行改变的问题
 */
class TimePickerDialogFix(context: Context, callBack: TimePickerDialog.OnTimeSetListener,
                          hourOfDay: Int, minute: Int, is24HourView: Boolean) : TimePickerDialog(context, callBack, hourOfDay, minute, is24HourView) {

  override fun onStop() {}
}
