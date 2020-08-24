package com.njust.helper.update

import android.content.Context
import androidx.appcompat.app.AlertDialog

import com.njust.helper.BuildConfig
import com.njust.helper.R

object UpdateLogDialog {
    private const val message =
            "${BuildConfig.VERSION_CODE} v${BuildConfig.VERSION_NAME}更新日志：\n" +
                    "1.修复 更新19-20学年第二学期数据\n"+
                    "上面依旧是原版得更新日志\n"+
                    "1.新增学期设置，学期起始日设置\n"+
                    "2.移除原本的远程配置文件\n"+
                    "3.不变的蓝色风格\n"+
                    "4.修复bug\n"+
                    "自用，请支持正版的“南理工助手”，正版下载地址可以在“常用链接”找到\n"+
                    "修改版下载地址也可以在“常用链接”找到"

    @JvmStatic
    fun showUpdateDialog(context: Context) {
        AlertDialog.Builder(context)
                .setTitle("更新日志：")
                .setNegativeButton(R.string.action_back, null)
                .setMessage(message)
                .show()
    }
}
