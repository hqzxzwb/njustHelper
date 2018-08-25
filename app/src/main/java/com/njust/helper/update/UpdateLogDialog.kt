package com.njust.helper.update

import android.content.Context
import android.support.v7.app.AlertDialog

import com.njust.helper.BuildConfig
import com.njust.helper.R

object UpdateLogDialog {
    private const val message =
            "${BuildConfig.VERSION_CODE} v${BuildConfig.VERSION_NAME}更新日志：\n" +
                    "由于一些疏忽，大部分功能暂时处于瘫痪状态。此版本对课表导入进行了紧急修复，其他功能将在后续版本尽快修复。十分抱歉。\n"

    @JvmStatic
    fun showUpdateDialog(context: Context) {
        AlertDialog.Builder(context)
                .setTitle("更新日志：")
                .setNegativeButton(R.string.action_back, null)
                .setMessage(message)
                .show()
    }
}
