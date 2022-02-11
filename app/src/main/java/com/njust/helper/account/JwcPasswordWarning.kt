package com.njust.helper.account

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.njust.helper.tools.Prefs

object JwcPasswordWarning {
  fun warnIfDefaultPassword(context: Context, continueTo: () -> Unit) {
    if (Prefs.neverShowJwcPasswordWarning(context)) {
      continueTo()
      return
    }
    if (Prefs.getId(context).isNullOrBlank()) {
      continueTo()
      return
    }
    if (Prefs.getId(context) != Prefs.getJwcPwd(context)) {
      continueTo()
      return
    }
    AlertDialog.Builder(context)
      .setTitle("账号提醒")
      .setMessage("检测到你的教务处密码为默认密码，由于教务系统默认密码调整，可能需要用调整后的密码登录，是否自动修改？")
      .setPositiveButton("自动修改并继续") { _, _ ->
        Prefs.putJwcPwd(context, Prefs.getJwcPwd(context) + "njust")
        continueTo()
      }
      .setNeutralButton("取消并继续") { _, _ ->
        continueTo()
      }
      .setNegativeButton("不再提示") { _, _ ->
        Prefs.putNeverShowJwcPasswordWarning(context)
        continueTo()
      }
      .show()
  }
}
