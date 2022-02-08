package com.njust.helper.grade

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.njust.helper.BuildConfig
import com.njust.helper.R
import com.njust.helper.account.AccountActivity
import com.njust.helper.activity.BaseActivity
import com.njust.helper.shared.api.LoginErrorException
import com.njust.helper.shared.api.ParseErrorException
import com.njust.helper.shared.api.ServerErrorException
import com.njust.helper.api.jwc.GradeLevelBean
import com.njust.helper.api.jwc.JwcApi
import com.njust.helper.databinding.ActivityGradeLevelBinding
import com.njust.helper.tools.Prefs
import com.njust.helper.tools.SimpleListVm
import kotlinx.coroutines.launch
import java.io.IOException

class GradeLevelActivity : BaseActivity() {
  private val vm = SimpleListVm<GradeLevelBean>()

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)

    refresh()
  }

  private fun refresh() {
    lifecycleScope.launch {
      try {
        val result = JwcApi.gradeLevel(Prefs.getId(this@GradeLevelActivity), Prefs.getJwcPwd(this@GradeLevelActivity))
        onDataReceived(result)
      } catch (e: Exception) {
        onError(e)
      }
    }
  }

  private fun onDataReceived(list: List<GradeLevelBean>) {
    vm.loading = false
    if (list.isEmpty()) {
      showSnack(R.string.message_no_result)
    } else {
      vm.items = list
    }
  }

  private fun onError(throwable: Throwable) {
    vm.loading = false
    when (throwable) {
        is ServerErrorException -> showSnack(R.string.message_server_error)
        is LoginErrorException -> AccountActivity.alertPasswordError(this, AccountActivity.REQUEST_JWC)
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
    val binding = DataBindingUtil.setContentView<ActivityGradeLevelBinding>(this, R.layout.activity_grade_level)
    binding.recyclerView.layoutManager = LinearLayoutManager(this)
    binding.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    binding.swipeRefreshLayout.setOnRefreshListener(this::refresh)
    binding.vm = vm
  }
}
