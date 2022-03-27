package com.njust.helper.coursequery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.njust.helper.R
import com.njust.helper.compose.material.DarkActionBarAppCompatTheme
import com.zwb.commonlibs.utils.NoOpFunction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf

@Composable
fun CourseQueryResultScreen(
  isRefreshing: Boolean,
  items: List<CourseQueryItem>,
  snackbarMessageFlow: Flow<String>,
  onRefresh: () -> Unit,
  onClickHome: () -> Unit,
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
          title = { Text(stringResource(id = R.string.title_activity_courseQuery_result)) },
        )
      },
      scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    ) {
      SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
        onRefresh = onRefresh,
      ) {
        LazyColumn {
          itemsIndexed(items) { index, item ->
            Column(
              modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            ) {
              Text(text = "${index + 1}.${item.name}", style = MaterialTheme.typography.subtitle1)
              Text(
                text = stringResource(
                  R.string.text_courseQuery_day_and_section,
                  item.day + 1,
                  item.section + 1,
                ),
                style = MaterialTheme.typography.body2,
              )
              Text(
                text = item.teacher,
                style = MaterialTheme.typography.body2,
              )
              Text(
                text = item.classroom,
                style = MaterialTheme.typography.body2,
              )
              Text(
                text = item.week1,
                style = MaterialTheme.typography.body2,
              )
            }
          }
        }
      }
    }
  }
}

@Preview
@Composable
private fun Preview() {
  CourseQueryResultScreen(
    isRefreshing = false,
    items = listOf(
      CourseQueryItem(
        0,
        "II-101",
        1,
        2,
        1,
        2,
        "name",
        "teacher",
        "week1",
        "week2",
      )
    ),
    snackbarMessageFlow = flowOf(),
    onRefresh = NoOpFunction,
    onClickHome = NoOpFunction,
  )
}
