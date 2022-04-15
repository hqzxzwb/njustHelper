package com.njust.helper.library.book

import android.view.Gravity
import android.widget.TextView
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.njust.helper.R
import com.njust.helper.compose.material.DarkActionBarAppCompatTheme
import com.njust.helper.compose.material.NavigationHome
import com.njust.helper.compose.material.VerticalDivider
import com.njust.helper.compose.material.textColors
import com.njust.helper.shared.api.LibDetailData
import com.njust.helper.shared.api.LibDetailItem
import com.njust.helper.shared.api.UnavailableItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Stable
class LibDetailViewModel(
  val onClickHome: () -> Unit,
  val onClickCollection: () -> Unit,
  val onRefresh: () -> Unit,
) {
  var inFavorites by mutableStateOf(false)
  var detail: LibDetailData? by mutableStateOf(null)
  var loading by mutableStateOf(false)
  val snackbarMessageFlow = MutableSharedFlow<String>()
}

@Composable
fun LibDetailScreen(
  vm: LibDetailViewModel,
) = DarkActionBarAppCompatTheme {
  val snackbarHostState = remember {
    SnackbarHostState()
  }
  LaunchedEffect(key1 = snackbarHostState) {
    launch {
      vm.snackbarMessageFlow.collectLatest {
        snackbarHostState.showSnackbar(it)
      }
    }
  }
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(stringResource(id = R.string.title_activity_lib_detail)) },
        navigationIcon = { NavigationHome(vm.onClickHome) },
        actions = {
          IconButton(onClick = vm.onClickCollection) {
            val icon = if (vm.inFavorites) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
            Icon(imageVector = icon, contentDescription = null)
          }
        },
      )
    },
    scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
  ) {
    SwipeRefresh(
      modifier = Modifier.fillMaxSize(),
      state = rememberSwipeRefreshState(isRefreshing = vm.loading),
      onRefresh = vm.onRefresh,
    ) {
      LibDetailContent(vm = vm)
    }
  }
}

@Composable
private fun LibDetailContent(vm: LibDetailViewModel) {
  val detail = vm.detail ?: return
  val contentStyle = MaterialTheme.typography.body2
    .copy(color = MaterialTheme.textColors.secondary)
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(bottom = 36.dp),
  ) {
    item {
      Text(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
        text = detail.head.orEmpty(),
        lineHeight = contentStyle.fontSize * 1.2F,
        style = contentStyle,
      )
      Divider()
    }
    items(detail.states) { state ->
      when (state) {
        is LibDetailItem -> {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(IntrinsicSize.Max),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              modifier = Modifier.weight(2F),
              text = state.code,
              style = contentStyle,
              textAlign = TextAlign.Center,
            )
            VerticalDivider(
              color = MaterialTheme.colors.onSurface.copy(alpha = 0.06F),
            )
            Text(
              modifier = Modifier.weight(3F),
              text = state.location,
              style = contentStyle,
              textAlign = TextAlign.Center,
            )
            VerticalDivider(
              color = MaterialTheme.colors.onSurface.copy(alpha = 0.06F),
            )
            AndroidView(
              modifier = Modifier.weight(2F),
              factory = { TextView(it).apply { gravity = Gravity.CENTER_HORIZONTAL } },
              update = {
                it.text = HtmlCompat.fromHtml(state.state, HtmlCompat.FROM_HTML_MODE_COMPACT)
              },
            )
          }
        }
        is UnavailableItem -> {
          Text(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
            text = state.message,
            style = contentStyle,
            color = Color.Red,
          )
        }
      }
      Divider()
    }
  }
}
