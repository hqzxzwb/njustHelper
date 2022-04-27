package com.njust.helper.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.njust.helper.R
import com.njust.helper.main.MainActivity
import com.njust.helper.tools.Prefs
import kotlinx.coroutines.launch

/**
 * 账户切换
 *
 * @author zwb
 */
class AccountActivity : AppCompatActivity() {
  companion object {
    fun alertPasswordError(context: Context) {
      AlertDialog.Builder(context)
        .setMessage(R.string.message_wrong_password)
        .setPositiveButton(R.string.dialog_base_modify_immediately) { _, _ ->
          val intent = Intent(context, AccountActivity::class.java)
          context.startActivity(intent)
        }
        .setNegativeButton(R.string.action_back, null)
        .show()
    }
  }

  private val vm = AccountScreenViewModel(
    onClickHome = this::finish,
    onClickConfirm = this::onClickConfirm,
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      AccountScreen(vm = vm)
    }
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)

    // 读入原账号数据
    vm.stuid = Prefs.getId(this)
    vm.jwcPwd = Prefs.getJwcPwd(this)
    vm.libPwd = Prefs.getLibPwd(this)
  }

  private fun onClickConfirm() {
    val stuid = vm.stuid
    val jwcPwd = vm.jwcPwd
    val libPwd = vm.libPwd
    if (stuid == "") {
      showSnack(getString(R.string.toast_input_id))
      return
    }
    if (jwcPwd == "") {
      showSnack(getString(R.string.toast_input_jwc_pwd))
      return
    }
    if (libPwd == "") {
      showSnack(getString(R.string.toast_input_lib_pwd))
      return
    }
    Prefs.putIdValues(this, stuid, jwcPwd, libPwd)
    val intent = Intent(this, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
  }

  private fun showSnack(message: String) {
    lifecycleScope.launch {
      vm.snackbarMessageFlow.emit(message)
    }
  }
}
