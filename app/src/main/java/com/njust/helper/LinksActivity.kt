package com.njust.helper

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.njust.helper.api.sharedMoshi
import com.njust.helper.compose.DarkActionBarAppCompatTheme
import com.njust.helper.model.Link
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.buffer
import okio.source

class LinksActivity : AppCompatActivity() {
  private val viewModel by viewModels<LinksViewModel>()

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)

    setContent {
      val isRefreshing by viewModel.loading.collectAsState()
      val items by viewModel.items.collectAsState()
      Screen(
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

  @Composable
  fun Screen(
    isRefreshing: Boolean,
    items: List<Link>,
    snackbarHostState: SnackbarHostState,
    onRefresh: () -> Unit,
    onClickLink: (link: Link) -> Unit,
    onClickHome: () -> Unit,
  ) {
    DarkActionBarAppCompatTheme {
      Scaffold(
        topBar = {
          TopAppBar(
            title = { Text(text = stringResource(R.string.title_activity_links)) },
            navigationIcon = {
              IconButton(onClick = onClickHome) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
              }
            },
          )
        },
        snackbarHost = {
          SnackbarHost(hostState = snackbarHostState)
        },
      ) {
        SwipeRefresh(
          state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
          onRefresh = onRefresh,
        ) {
          LazyColumn {
            items(items, null) { link ->
              LinkItem(link = link, onClickLink = onClickLink)
            }
          }
        }
      }
    }
  }

  @Composable
  fun LinkItem(link: Link, onClickLink: (link: Link) -> Unit) {
    Button(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 2.dp),
      onClick = { onClickLink(link) },
    ) {
      Text(text = link.name)
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
      snackbarHostState.showSnackbar(context.getString(R.string.message_net_error))
    }
    loading.emit(false)
  }

  private suspend fun parseLinks(context: Context): List<Link> = withContext(Dispatchers.IO) {
    context.resources.openRawResource(R.raw.links).use {
      val type = Types.newParameterizedType(List::class.java, Link::class.java)
      val adapter = sharedMoshi.adapter<List<Link>>(type)
      adapter.fromJson(it.source().buffer())!!
    }
  }
}
