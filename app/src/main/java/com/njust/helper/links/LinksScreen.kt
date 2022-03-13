package com.njust.helper.links

import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.njust.helper.R
import com.njust.helper.compose.DarkActionBarAppCompatTheme
import com.njust.helper.compose.emitOnAction
import com.njust.helper.shared.api.CommonLink
import com.njust.helper.shared.links.LinksViewModel
import com.njust.helper.shared.links.newLinksViewModel
import com.zwb.commonlibs.utils.NoOpFunction
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LinksScreen(
  vm: LinksViewModel,
  onClickLink: (link: CommonLink) -> Unit,
  onClickHome: () -> Unit,
) {
  val snackbarHostState = remember { SnackbarHostState() }
  val context = LocalContext.current
  LaunchedEffect(key1 = snackbarHostState, block = {
    vm.snackbarMessageFlow.collectLatest {
      snackbarHostState.showSnackbar(it.toString(context))
    }
  })
  DarkActionBarAppCompatTheme {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
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
        modifier = Modifier.fillMaxSize(),
        state = rememberSwipeRefreshState(isRefreshing = vm.loading),
        onRefresh = vm.onRefreshAction.emitOnAction(),
      ) {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
        ) {
          items(vm.items, null) { link ->
            LinkItem(link = link, onClickLink = onClickLink)
          }
        }
      }
    }
  }
}

@Composable
private fun LinkItem(link: CommonLink, onClickLink: (link: CommonLink) -> Unit) {
  Button(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 8.dp, vertical = 2.dp),
    onClick = { onClickLink(link) },
  ) {
    Text(text = link.name)
  }
}

@Composable
@Preview
private fun Preview() {
  LinksScreen(
    newLinksViewModel().apply {
      items = listOf(CommonLink("Link A", ""))
    },
    onClickLink = NoOpFunction,
    onClickHome = NoOpFunction,
  )
}
