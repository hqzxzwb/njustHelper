package com.njust.helper.update

import android.content.Context
import androidx.appcompat.app.AlertDialog

import com.njust.helper.BuildConfig
import com.njust.helper.R

object UpdateLogDialog {
    private const val message =
            "${BuildConfig.VERSION_CODE} v${BuildConfig.VERSION_NAME}更新日志：\n" +
                    "1.修复图书搜索\n"+
                    "2.更新学期\n"+
                    "自用，请支持正版的“南理工助手”，正版下载地址可以在“常用链接”找到\n"+
                    "修改版下载地址也可以在“常用链接”找到\n"+
                    "推荐使用微信小程序 `乐学学导` 安全 快速 更多功能！"

    @JvmStatic
    fun showUpdateDialog(context: Context) {
        AlertDialog.Builder(context)
                .setTitle("更新日志：")
                .setNegativeButton(R.string.action_back, null)
                .setMessage(message)
                .show()
    }
}
