package com.njust.helper.links

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.njust.helper.R
import com.njust.helper.shared.api.Link
import com.njust.helper.shared.api.LinksApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LinksActivity : AppCompatActivity() {
  private val viewModel by viewModels<LinksViewModel>()

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)

    setContent {
      val isRefreshing by viewModel.loading.collectAsState()
      val items by viewModel.items.collectAsState()
      LinksScreen(
        isRefreshing = isRefreshing,
        items = items,
        snackbarHostState = viewModel.snackbarHostState,
        onRefresh = { load() },
        onClickLink = { link ->
          val intent = Intent(Intent.ACTION_VIEW)
          intent.data = link.url.toUri()
          startActivity(intent)
        },
        onClickHome = { finish() },
      )
    }
    load()
  }

  private fun load() {
    lifecycleScope.launch {
      viewModel.load(this@LinksActivity)
    }
  }
}

class LinksViewModel : ViewModel() {
  val items = MutableStateFlow(listOf<Link>())
  val loading = MutableStateFlow(false)
  val snackbarHostState = SnackbarHostState()

  suspend fun load(context: Context) {
    try {
      loading.emit(true)
      val data = parseLinks(context)
      items.emit(data)
    } catch (e: Exception) {
      e.printStackTrace()
      snackbarHostState.showSnackbar(context.getString(R.string.message_net_error))
    }
    loading.emit(false)
  }

  private suspend fun parseLinks(context: Context): List<Link> =
    LinksApi.links()
}
