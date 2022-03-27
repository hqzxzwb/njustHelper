package com.njust.helper.grade

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.njust.helper.R
import com.njust.helper.compose.material.DarkActionBarAppCompatTheme
import com.njust.helper.shared.api.GradeLevelBean
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun GradeLevelScreen(
  items: List<GradeLevelBean>,
  loading: Boolean,
  snackbarMessageFlow: Flow<String>,
  onClickHome: () -> Unit,
  onRefresh: () -> Unit,
) {
  val snackbarHostState = remember { SnackbarHostState() }
  LaunchedEffect(key1 = snackbarHostState) {
    snackbarMessageFlow.collectLatest {
      snackbarHostState.showSnackbar(it)
    }
  }
  DarkActionBarAppCompatTheme {
    Scaffold(
      topBar = {
        TopAppBar(
          navigationIcon = {
            IconButton(onClick = onClickHome) {
              Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
            }
          },
          title = { Text(text = stringResource(id = R.string.title_activity_level)) },
        )
      },
      scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
    ) {
      SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = loading),
        onRefresh = onRefresh,
      ) {
        LazyColumn {
          items(items) { item ->
            Item(item)
            Divider(
              color = MaterialTheme.colors.onSecondary.copy(alpha = 0.2F),
              thickness = 1.dp,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun Item(
  data: GradeLevelBean,
) {
  Column(
    modifier = Modifier.padding(16.dp, 8.dp),
  ) {
    Text(
      text = data.courseName,
      style = MaterialTheme.typography.subtitle1,
    )
    Text(
      text = "笔试成绩：${data.writtenPartScore}",
      style = MaterialTheme.typography.body2,
    )
    Text(
      text = "机试成绩：${data.computerPartScore}",
      style = MaterialTheme.typography.body2,
    )
    Text(
      text = "总成绩：${data.totalScore}",
      style = MaterialTheme.typography.body2,
    )
    Text(
      text = "考试时间：${data.time}",
      style = MaterialTheme.typography.body2,
    )
  }
}
