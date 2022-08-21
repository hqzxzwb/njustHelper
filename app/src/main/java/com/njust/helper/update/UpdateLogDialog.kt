package com.njust.helper.update

import android.content.Context
import androidx.appcompat.app.AlertDialog

import com.njust.helper.BuildConfig
import com.njust.helper.R

object UpdateLogDialog {
  private const val message =
      "${BuildConfig.VERSION_CODE} v${BuildConfig.VERSION_NAME}更新日志：\n" +
          "1.更新 2022-2023-1学期数据\n"

  @JvmStatic
  fun showUpdateDialog(context: Context) {
    AlertDialog.Builder(context)
        .setTitle("更新日志：")
        .setNegativeButton(R.string.action_back, null)
        .setMessage(message)
        .show()
  }
}
