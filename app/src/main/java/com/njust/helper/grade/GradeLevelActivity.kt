package com.njust.helper.grade

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.njust.helper.BuildConfig
import com.njust.helper.R
import com.njust.helper.account.AccountActivity
import com.njust.helper.shared.api.GradeLevelBean
import com.njust.helper.shared.api.JwcApi
import com.njust.helper.shared.api.LoginErrorException
import com.njust.helper.shared.api.ParseErrorException
import com.njust.helper.shared.api.ServerErrorException
import com.njust.helper.tools.Prefs
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.io.IOException

class GradeLevelActivity : AppCompatActivity() {
  private val viewModel: GradeLevelViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      GradeLevelScreen(
        items = viewModel.items,
        loading = viewModel.loading,
        snackbarMessageFlow = viewModel.messageFlow,
        onClickHome = { finish() },
        onRefresh = { doRefresh() }
      )
    }
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)

    doRefresh()
  }

  private fun doRefresh() {
    lifecycleScope.launch {
      viewModel.refresh(this@GradeLevelActivity)
    }
  }
}

class GradeLevelViewModel : ViewModel() {
  var items: List<GradeLevelBean> by mutableStateOf(listOf())
  var loading by mutableStateOf(false)
  val messageFlow = MutableSharedFlow<String>()

  suspend fun refresh(context: Context) {
    loading = true
    try {
      val result = JwcApi.gradeLevel(Prefs.getId(context), Prefs.getJwcPwd(context))
      onDataReceived(context, result)
    } catch (e: Exception) {
      onError(context, e)
    }
  }

  private suspend fun onDataReceived(context: Context, list: List<GradeLevelBean>) {
    loading = false
    if (list.isEmpty()) {
      messageFlow.emit(context.getString(R.string.message_no_result))
    } else {
      items = list
    }
  }

  private suspend fun onError(context: Context, throwable: Throwable) {
    loading = false
    when (throwable) {
      is ServerErrorException -> messageFlow.emit(context.getString(R.string.message_server_error))
      is LoginErrorException -> AccountActivity.alertPasswordError(
        context,
        AccountActivity.REQUEST_JWC
      )
      is IOException -> messageFlow.emit(context.getString(R.string.message_net_error))
      is ParseErrorException -> messageFlow.emit(context.getString(R.string.message_parse_error))
      else -> {
        if (BuildConfig.DEBUG) {
          throwable.printStackTrace()
          throw throwable
        }
      }
    }
  }
}
