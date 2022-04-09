package com.njust.helper.library.search

import android.view.ContextThemeWrapper
import androidx.appcompat.widget.SearchView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.njust.helper.R
import com.njust.helper.compose.material.DarkActionBarAppCompatTheme
import com.njust.helper.compose.material.textColors
import com.njust.helper.shared.api.LibSearchBean
import com.zwb.commonlibs.utils.NoOpFunction
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest

@Stable
class LibSearchViewModel(
  val onClickHome: () -> Unit,
  val onClickResultItem: (LibSearchBean) -> Unit,
  val onClickClearHistory: () -> Unit,
  val prepareSearchView: (SearchView) -> Unit,
) {
  var isRefreshing by mutableStateOf(false)
  var result: List<LibSearchBean> by mutableStateOf(listOf())
  val snackbarMessageFlow = MutableSharedFlow<String>()
}

@Composable
fun LibSearchScreen(
  vm: LibSearchViewModel,
) = DarkActionBarAppCompatTheme {
  val snackbarHostState = remember {
    SnackbarHostState()
  }
  LaunchedEffect(key1 = snackbarHostState) {
    vm.snackbarMessageFlow.collectLatest {
      snackbarHostState.showSnackbar(it)
    }
  }
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        navigationIcon = {
          IconButton(onClick = vm.onClickHome) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
          }
        },
        title = { SearchView(vm = vm) },
      )
    },
    scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
  ) {
    Column {
      SearchResult(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1F),
        vm = vm,
      )
      Divider()
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable(onClick = vm.onClickClearHistory),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Spacer(modifier = Modifier.width(16.dp))
        Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
          modifier = Modifier.padding(vertical = 12.dp),
          text = "清除搜索历史",
          style = MaterialTheme.typography.h6,
          color = MaterialTheme.textColors.primary,
        )
      }
    }
  }
}

@Composable
private fun SearchResult(
  modifier: Modifier,
  vm: LibSearchViewModel,
) = SwipeRefresh(
  modifier = modifier,
  state = rememberSwipeRefreshState(isRefreshing = vm.isRefreshing),
  swipeEnabled = false,
  onRefresh = NoOpFunction,
) {
  LazyColumn {
    itemsIndexed(vm.result) { index, item ->
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { vm.onClickResultItem(item) },
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
          Text(
            text = "${index + 1}. ${item.title}",
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.textColors.primary,
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = item.author,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.textColors.tertiary,
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = item.press,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.textColors.tertiary,
          )
        }
      }
      Divider()
    }
  }
}

@Composable
private fun SearchView(vm: LibSearchViewModel) {
  AndroidView(
    factory = { context ->
      val v =
        SearchView(ContextThemeWrapper(context, R.style.ThemeOverlay_AppCompat_Dark_ActionBar))
      v.setIconifiedByDefault(false)
      v.isQueryRefinementEnabled = true
      vm.prepareSearchView(v)
      v
    },
  )
}
