package com.njust.helper.exam

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.njust.helper.R
import com.njust.helper.compose.material.DarkActionBarAppCompatTheme
import com.njust.helper.compose.material.NavigationHome
import com.njust.helper.compose.material.textColors
import com.njust.helper.shared.api.Exam
import com.zwb.commonlibs.utils.NoOpFunction
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest

@Stable
class ExamViewModel(
  val onClickHome: () -> Unit,
  val onRefresh: () -> Unit,
) {
  var loading by mutableStateOf(false)
  var exams: List<Exam> by mutableStateOf(listOf())
  val snackbarMessageFlow = MutableSharedFlow<String>()
}

@Composable
fun ExamScreen(
  vm: ExamViewModel,
) = DarkActionBarAppCompatTheme {
  val snackbarHostState = remember { SnackbarHostState() }
  LaunchedEffect(key1 = snackbarHostState, block = {
    vm.snackbarMessageFlow.collectLatest {
      snackbarHostState.showSnackbar(it)
    }
  })
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(text = stringResource(id = R.string.title_activity_exam)) },
        navigationIcon = { NavigationHome(vm.onClickHome) },
      )
    },
    scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
  ) {
    SwipeRefresh(
      modifier = Modifier.fillMaxSize(),
      state = rememberSwipeRefreshState(isRefreshing = vm.loading),
      onRefresh = vm.onRefresh,
    ) {
      val exams = vm.exams
      ExamList(exams = exams)
    }
  }
}

@Composable
private fun ExamList(exams: List<Exam>) {
  if (exams.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize()) {
      Text(
        modifier = Modifier.align(Alignment.Center),
        text = stringResource(id = R.string.message_no_result_exam),
        style = MaterialTheme.typography.body1,
        color = MaterialTheme.textColors.primary,
        textAlign = TextAlign.Center,
      )
    }
  } else {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
      items(exams) { exam ->
        Column(
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
          Text(
            text = exam.course,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.textColors.primary,
          )
          val subContentStyle = MaterialTheme.typography.body2
            .copy(color = MaterialTheme.textColors.tertiary)
          Text(
            text = "考试时间：${exam.time}",
            style = subContentStyle,
          )
          Text(
            text = "考场：${exam.room}",
            style = subContentStyle,
          )
          Text(
            text = "座位号：${exam.seat}",
            style = subContentStyle,
          )
        }
        Divider()
      }
    }
  }
}

@Preview
@Composable
private fun PreviewExamScreen() {
  ExamScreen(
    ExamViewModel(
      onClickHome = NoOpFunction,
      onRefresh = NoOpFunction,
    ).apply {
      loading = true
      exams = listOf(
        Exam(
          course = "CourseName",
          time = "2022-04-28",
          room = "X教学楼II-501",
          seat = "36",
        )
      )
    }
  )
}
