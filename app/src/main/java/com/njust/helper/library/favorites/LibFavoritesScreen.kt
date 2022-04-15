package com.njust.helper.library.favorites

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.njust.helper.R
import com.njust.helper.compose.material.DarkActionBarAppCompatTheme
import com.njust.helper.compose.material.NavigationHome
import com.njust.helper.compose.material.textColors
import com.zwb.commonlibs.utils.ThreadLocalDelegate
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private val DATE_FORMAT by ThreadLocalDelegate {
  SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
}

@Stable
class LibFavoritesViewModel(
  val onClickHome: () -> Unit,
  val onClickItem: (LibFavoritesItem) -> Unit,
  val onClickDelete: () -> Unit,
) {
  var itemsLoaded by mutableStateOf(false)
  var items: List<LibFavoritesItemViewModel> by mutableStateOf(listOf())
  var inModification by mutableStateOf(false)
    private set
  val snackbarMessageFlow = MutableSharedFlow<String>()

  fun exitModificationMode() {
    inModification = false
    items.forEach { it.checked = false }
  }

  fun enterModificationMode() {
    inModification = true
  }
}

@Stable
class LibFavoritesItemViewModel(
  val item: LibFavoritesItem,
) {
  var checked by mutableStateOf(false)
}

@Composable
fun LibFavoritesScreen(
  vm: LibFavoritesViewModel,
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
        title = { Text(stringResource(id = R.string.title_activity_lib_favorites)) },
        navigationIcon = {
          NavigationHome {
            if (vm.inModification) {
              vm.exitModificationMode()
            } else {
              vm.onClickHome()
            }
          }
        },
        actions = {
          if (vm.inModification) {
            IconButton(onClick = vm.onClickDelete) {
              Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
            }
          }
        },
      )
    },
    scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
  ) {
    if (vm.items.isNotEmpty()) {
      Items(vm)
    } else if (vm.itemsLoaded) {
      Empty()
    }
  }
}

@Composable
private fun Items(
  vm: LibFavoritesViewModel,
) {
  LazyColumn {
    items(vm.items) { itemVm ->
      LibFavoritesItem(
        vm = itemVm,
        inModification = vm.inModification,
        onClick = vm.onClickItem,
        onLongClick = {
          it.checked = true
          vm.enterModificationMode()
        },
      )
      Divider()
    }
  }
}

@Composable
private fun Empty() {
  Box(
    modifier = Modifier.fillMaxSize(),
  ) {
    Text(
      modifier = Modifier.align(Alignment.Center),
      text = stringResource(id = R.string.content_favorites_no_result),
      style = MaterialTheme.typography.body1,
      color = MaterialTheme.textColors.primary,
      textAlign = TextAlign.Center,
    )
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LibFavoritesItem(
  vm: LibFavoritesItemViewModel,
  inModification: Boolean,
  onClick: (LibFavoritesItem) -> Unit,
  onLongClick: (LibFavoritesItemViewModel) -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .combinedClickable(
        onClick = {
          if (inModification) {
            vm.checked = !vm.checked
          } else {
            onClick(vm.item)
          }
        },
        onLongClick = { onLongClick(vm) },
      )
      .padding(horizontal = 16.dp, vertical = 8.dp),
  ) {
    Column {
      Text(
        text = vm.item.name,
        style = MaterialTheme.typography.subtitle1,
        color = MaterialTheme.textColors.primary,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = stringResource(R.string.text_libFavorites_code, vm.item.code),
        style = MaterialTheme.typography.body2,
        color = MaterialTheme.textColors.secondary,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "收藏时间：${DATE_FORMAT.format(Date(vm.item.time))}",
        style = MaterialTheme.typography.body2,
        color = MaterialTheme.textColors.secondary,
      )
    }
    if (inModification) {
      Checkbox(
        modifier = Modifier.align(Alignment.BottomEnd),
        checked = vm.checked,
        onCheckedChange = { vm.checked = it },
      )
    }
  }
}
