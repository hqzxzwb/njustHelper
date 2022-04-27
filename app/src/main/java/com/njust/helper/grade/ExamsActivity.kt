package com.njust.helper.grade

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.njust.helper.BuildConfig
import com.njust.helper.R
import com.njust.helper.RemoteConfig
import com.njust.helper.account.AccountActivity
import com.njust.helper.activity.BaseActivity
import com.njust.helper.shared.api.LoginErrorException
import com.njust.helper.shared.api.ParseErrorException
import com.njust.helper.shared.api.ServerErrorException
import com.njust.helper.shared.api.Exam
import com.njust.helper.databinding.ActivityExamBinding
import com.njust.helper.shared.api.JwcApi
import com.njust.helper.tools.Prefs
import com.njust.helper.tools.SimpleListVm
import kotlinx.coroutines.launch
import java.io.IOException

class ExamsActivity : BaseActivity() {
  private val vm = SimpleListVm<Exam>()

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)

    refresh()
  }

  private fun refresh() {
    lifecycleScope.launch {
      try {
        val result = JwcApi.exams(
          Prefs.getId(this@ExamsActivity),
          Prefs.getJwcPwd(this@ExamsActivity),
          RemoteConfig.getTermId(),
        )
        onDataReceived(result)
      } catch (e: Exception) {
        onError(e)
      }
    }
  }

  private fun onDataReceived(list: List<Exam>) {
    vm.loading = false
    if (list.isEmpty()) {
      showSnack(R.string.message_no_result_exam)
    } else {
      vm.items = list
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

  override fun layout() {
    val binding = DataBindingUtil.setContentView<ActivityExamBinding>(this, R.layout.activity_exam)
    binding.recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
    binding.recyclerView.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(this, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))
    binding.swipeRefreshLayout.setOnRefreshListener(this::refresh)
    binding.vm = vm
  }
}
