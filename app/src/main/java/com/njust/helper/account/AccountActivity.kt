package com.njust.helper.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.njust.helper.R
import com.njust.helper.activity.BaseActivity
import com.njust.helper.main.MainActivity
import com.njust.helper.tools.Prefs

/**
 * 账户切换
 *
 * @author zwb
 */
class AccountActivity : BaseActivity(R.layout.activity_account) {
  companion object {
    const val REQUEST_JWC = 2
    const val REQUEST_LIB = 4
    const val EXTRA_PASSWORD_TYPE = "password_type"

    fun alertPasswordError(context: Context, accountRequest: Int) {
      AlertDialog.Builder(context)
        .setMessage(R.string.message_wrong_password)
        .setPositiveButton(R.string.dialog_base_modify_immediately) { _, _ ->
          val intent = Intent(context, AccountActivity::class.java)
          intent.putExtra(EXTRA_PASSWORD_TYPE, accountRequest)
          context.startActivity(intent)
        }
        .setNegativeButton(R.string.action_back, null)
        .show()
    }
  }

  private var type: Int = 0

  private lateinit var stuidText: TextView
  private lateinit var jwcPwdText: TextView
  private lateinit var libPwdText: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val intent = intent
    type = intent.getIntExtra(EXTRA_PASSWORD_TYPE, 0)

    stuidText = findViewById(R.id.stuidText)
    jwcPwdText = findViewById(R.id.jwcPwdText)
    libPwdText = findViewById(R.id.libPwdText)
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)

    // 读入原账号数据
    stuidText.setText(Prefs.getId(this))
    jwcPwdText.setText(Prefs.getJwcPwd(this))
    libPwdText.setText(Prefs.getLibPwd(this))

    // 设置焦点
    when (type) {
      REQUEST_JWC -> jwcPwdText.requestFocus()
      REQUEST_LIB -> libPwdText.requestFocus()
      else -> stuidText.requestFocus()
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.account, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.item_save -> {
        val stuid = stuidText.text.toString().trim()
        val jwcPwd = jwcPwdText.text.toString()
        val libPwd = libPwdText.text.toString()
        if (stuid == "") {
          showSnack(getString(R.string.toast_input_id))
          return true
        }
        if (jwcPwd == "") {
          showSnack(getString(R.string.toast_input_jwc_pwd))
          return true
        }
        if (libPwd == "") {
          showSnack(getString(R.string.toast_input_lib_pwd))
          return true
        }
        Prefs.putIdValues(this, stuid, jwcPwd, libPwd)
        Prefs.putCookie(this, "", null, 1)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        return true
      }
      else -> {
        return super.onOptionsItemSelected(item)
      }
    }
  }
}
