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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.njust.helper.R
import com.njust.helper.compose.DarkActionBarAppCompatTheme
import com.njust.helper.model.Link

@Composable
fun LinksScreen(
  isRefreshing: Boolean,
  items: List<Link>,
  snackbarHostState: SnackbarHostState,
  onRefresh: () -> Unit,
  onClickLink: (link: Link) -> Unit,
  onClickHome: () -> Unit,
) {
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
        state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
        onRefresh = onRefresh,
      ) {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
        ) {
          items(items, null) { link ->
            LinkItem(link = link, onClickLink = onClickLink)
          }
        }
      }
    }
  }
}

@Composable
private fun LinkItem(link: Link, onClickLink: (link: Link) -> Unit) {
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
    isRefreshing = false,
    items = listOf(Link("Link A", "")),
    snackbarHostState = SnackbarHostState(),
    onRefresh = {},
    onClickLink = {},
    onClickHome = {},
  )
}
