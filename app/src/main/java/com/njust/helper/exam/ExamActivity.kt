package com.njust.helper.exam

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.njust.helper.BuildConfig
import com.njust.helper.R
import com.njust.helper.RemoteConfig
import com.njust.helper.account.AccountActivity
import com.njust.helper.shared.api.JwcApi
import com.njust.helper.shared.api.LoginErrorException
import com.njust.helper.shared.api.ParseErrorException
import com.njust.helper.shared.api.ServerErrorException
import com.njust.helper.tools.Prefs
import kotlinx.coroutines.launch
import java.io.IOException

class ExamActivity : AppCompatActivity() {
  private val vm = ExamViewModel(
    onClickHome = this::finish,
    onRefresh = this::refresh,
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent { ExamScreen(vm = vm) }
    refresh()
  }

  private fun refresh() {
    lifecycleScope.launch {
      vm.loading = true
      try {
        val result = JwcApi.exams(
          Prefs.getId(this@ExamActivity),
          Prefs.getJwcPwd(this@ExamActivity),
          RemoteConfig.getTermId(),
        )
        vm.exams = result
      } catch (e: Exception) {
        onError(e)
      }
      vm.loading = false
    }
  }

  private fun onError(throwable: Throwable) {
    vm.loading = false
    when (throwable) {
      is ServerErrorException -> showSnack(R.string.message_server_error)
      is LoginErrorException -> AccountActivity.alertPasswordError(this)
      is IOException -> showSnack(R.string.message_net_error)
      is ParseErrorException -> showSnack(R.string.message_parse_error)
      else -> {
        if (BuildConfig.DEBUG) {
          throwable.printStackTrace()
          throw throwable
        }
      }
    }
  }

  private fun showSnack(messageRes: Int) {
    lifecycleScope.launch {
      vm.snackbarMessageFlow.emit(getString(messageRes))
    }
  }
}
