package com.njust.helper.update

import android.content.Context
import androidx.appcompat.app.AlertDialog

import com.njust.helper.BuildConfig
import com.njust.helper.R

object UpdateLogDialog {
    private const val message =
            "${BuildConfig.VERSION_CODE} v${BuildConfig.VERSION_NAME}更新日志：\n" +
                    "1.修复 修复未评教时无法查询成绩的问题\n" +
                    "2.修复 部分成绩值无法解析的问题\n" +
                    "3.修复 导入新学期课表\n"

    @JvmStatic
    fun showUpdateDialog(context: Context) {
        AlertDialog.Builder(context)
                .setTitle("更新日志：")
                .setNegativeButton(R.string.action_back, null)
                .setMessage(message)
                .show()
    }
}
