package com.njust.helper.library.book

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.njust.helper.BuildConfig
import com.njust.helper.R
import com.njust.helper.library.collection.LibCollectManager
import com.njust.helper.shared.api.LibDetailItem
import com.njust.helper.shared.api.LibraryApi
import com.njust.helper.shared.api.ParseErrorException
import com.njust.helper.shared.api.ServerErrorException
import com.njust.helper.tools.Constants
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException

class LibDetailActivity : AppCompatActivity() {
  private lateinit var idString: String

  private val manager: LibCollectManager = LibCollectManager

  private val vm = LibDetailViewModel(
    onClickHome = this::finish,
    onClickCollection = this::onClickCollection,
    onRefresh = this::refresh,
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      LibDetailScreen(vm = this.vm)
    }

    idString = intent.getStringExtra(Constants.EXTRA_ID)!!

    lifecycleScope.launch {
      manager.collectedStateFlow(idString)
        .collectLatest {
          vm.collected = it
        }
    }
    refresh()
  }

  private fun onClickCollection() {
    lifecycleScope.launch {
      val detail = vm.detail
      when {
        detail == null -> showSnack("收藏失败，请刷新后重试")
        vm.collected -> {
          manager.removeCollect(idString)
          showSnack("已取消收藏")
        }
        else -> {
          val title = detail.head?.split("\n")?.getOrNull(1).orEmpty()
          val code = (detail.states.firstOrNull() as? LibDetailItem)?.code.orEmpty()
          if (manager.addCollect(idString, title, code)) {
            showSnack("收藏成功")
          }
        }
      }
    }
  }

  private fun refresh() {
    lifecycleScope.launch {
      vm.loading = true
      try {
        val result = LibraryApi.detail(idString)
        vm.detail = result
      } catch (e: Exception) {
        onError(e)
      }
      vm.loading = false
    }
  }

  private suspend fun onError(throwable: Throwable) {
    when (throwable) {
      is IOException -> showSnack(R.string.message_net_error)
      is ServerErrorException -> showSnack(R.string.message_server_error_lib)
      is ParseErrorException -> showSnack(R.string.message_parse_error)
      else -> if (BuildConfig.DEBUG) {
        throw throwable
      }
    }
  }

  private suspend fun showSnack(id: Int) {
    vm.snackbarMessageFlow.emit(getString(id))
  }

  private suspend fun showSnack(text: String) {
    vm.snackbarMessageFlow.emit(text)
  }

  companion object {
    fun buildIntent(context: Context, idString: String): Intent {
      val intent = Intent(context, LibDetailActivity::class.java)
      intent.putExtra(Constants.EXTRA_ID, idString)
      return intent
    }
  }
}
