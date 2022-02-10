package com.njust.helper.update

import android.content.Context
import androidx.appcompat.app.AlertDialog

import com.njust.helper.BuildConfig
import com.njust.helper.R

object UpdateLogDialog {
  private const val message =
      "${BuildConfig.VERSION_CODE} v${BuildConfig.VERSION_NAME}更新日志：\n" +
          "1.修复 更新21-22学年第二学期数据\n" +
          "2.修复 图书可借状态查看\n" +
          "3.新增 默认密码自动调整提示\n"

  @JvmStatic
  fun showUpdateDialog(context: Context) {
    AlertDialog.Builder(context)
        .setTitle("更新日志：")
        .setNegativeButton(R.string.action_back, null)
        .setMessage(message)
        .show()
  }
}
