package com.njust.helper.settings

import android.content.Context
import android.support.v7.app.AlertDialog

import com.njust.helper.BuildConfig
import com.njust.helper.R

object UpdateLogDialog {
    const val message =
            "${BuildConfig.VERSION_CODE} v${BuildConfig.VERSION_NAME}更新日志：\n" +
                    "1.修复 修复闪退问题\n"

    @JvmStatic
    fun showUpdateDialog(context: Context) {
        AlertDialog.Builder(context)
                .setTitle("更新日志：")
                .setNegativeButton(R.string.action_back, null)
                .setMessage(message)
                .show()
    }
}
