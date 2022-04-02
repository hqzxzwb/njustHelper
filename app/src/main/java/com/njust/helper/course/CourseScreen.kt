package com.njust.helper.course

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.njust.helper.R
import com.njust.helper.compose.material.DarkActionBarAppCompatTheme
import com.njust.helper.compose.material.textColors
import com.njust.helper.course.day.CourseDayScreen
import com.njust.helper.course.week.CourseWeekScreen
import com.njust.helper.model.Course
import com.njust.helper.tools.Constants
import com.njust.helper.tools.TimeUtil
import com.zwb.commonlibs.utils.ThreadLocalDelegate
import com.zwb.commonlibs.utils.plus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Stable
class CourseScreenViewModel(
  val onClickCourse: (courses: List<Course>, day: Int, section: Int) -> Unit,
  val onClickHome: () -> Unit,
  val onClickSelectingWeek: () -> Unit,
  val onClickSelectingDate: () -> Unit,
  val onClickImporting: () -> Unit,
  val onClickClearing: () -> Unit,
) {
  val switchingDayOfTermFlow = MutableSharedFlow<Pair<Int, Boolean>>()

  suspend fun scrollTo(position: Int, animate: Boolean) {
    switchingDayOfTermFlow.emit(position to animate)
    dayOfTerm = position
  }

  suspend fun scrollToToday() {
    val currentDay = ((System.currentTimeMillis() - termStartTime) / TimeUtil.ONE_DAY).toInt()
    val maxDayOfTerm = 7 * Constants.MAX_WEEK_COUNT - 1
    scrollTo(currentDay.coerceIn(0, maxDayOfTerm), false)
  }

  var termStartTime: Long by mutableStateOf(0)
  var courses: Array<out Array<out List<Course>>> by mutableStateOf(
    Array(7) {
      Array(Constants.COURSE_SECTION_COUNT) { listOf() }
    }
  )
  var dayOfTerm by mutableStateOf(0)
  var dayModeOrWeekMode by mutableStateOf(true)
  var menuExpanded by mutableStateOf(false)

  val snackbarMessageFlow = MutableSharedFlow<String>()

  suspend fun showSnackbar(message: String) {
    snackbarMessageFlow.emit(message)
  }
}

@Composable
fun CourseScreen(vm: CourseScreenViewModel) = DarkActionBarAppCompatTheme {
  val snackbarHostState = remember { SnackbarHostState() }
  LaunchedEffect(key1 = snackbarHostState) {
    launch {
      vm.snackbarMessageFlow.collectLatest {
        snackbarHostState.showSnackbar(it)
      }
    }
  }
  Scaffold(
    topBar = { TopBar(vm) },
    scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
    ) {
      val courseAreaModifier = Modifier.weight(1F)
      if (vm.dayModeOrWeekMode) {
        CourseDayScreen(modifier = courseAreaModifier, vm = vm)
      } else {
        CourseWeekScreen(modifier = courseAreaModifier, vm = vm)
      }
      ControllerRow(modifier = Modifier.align(Alignment.CenterHorizontally), vm = vm)
      TodayText(modifier = Modifier.align(Alignment.CenterHorizontally), vm = vm)
    }
  }
}

@Composable
private fun TopBar(
  vm: CourseScreenViewModel,
) {
  TopAppBar(
    title = {
      Text(text = stringResource(id = R.string.title_activity_course))
    },
    navigationIcon = {
      IconButton(onClick = vm.onClickHome) {
        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
      }
    },
    actions = {
      Row(
        modifier = Modifier.clickable { vm.dayModeOrWeekMode = true },
      ) {
        RadioButton(selected = vm.dayModeOrWeekMode, onClick = null)
        Text(text = "按天")
      }
      Row(
        modifier = Modifier.clickable { vm.dayModeOrWeekMode = false },
      ) {
        RadioButton(selected = !vm.dayModeOrWeekMode, onClick = null)
        Text(text = "按周")
      }
      Box {
        IconButton(onClick = { vm.menuExpanded = true }) {
          Icon(imageVector = Icons.Filled.MoreVert, contentDescription = null)
        }
        CourseMenu(vm = vm)
      }
    },
  )
}

@Composable
private fun ControllerRow(
  modifier: Modifier,
  vm: CourseScreenViewModel,
) {
  val dayOfTerm = vm.dayOfTerm
  val week = dayOfTerm / 7 + 1
  val coroutineScope = rememberCoroutineScope()
  Row(
    modifier = modifier,
  ) {
    TextButton(
      modifier = Modifier.widthIn(min = 1.dp),
      onClick = {
        coroutineScope.launch {
          vm.scrollTo((dayOfTerm - 7).coerceAtLeast(0), false)
        }
      },
    ) {
      Text(
        text = " ◁ ",
        color = MaterialTheme.textColors.primary,
      )
    }
    TextButton(
      modifier = Modifier.widthIn(min = 1.dp),
      onClick = vm.onClickSelectingWeek,
    ) {
      Text(
        text = stringResource(id = R.string.button_course_pick_week, week),
        color = MaterialTheme.textColors.primary,
      )
    }
    TextButton(
      modifier = Modifier.widthIn(min = 1.dp),
      onClick = {
        coroutineScope.launch {
          val maxDayOfTerm = 7 * Constants.MAX_WEEK_COUNT - 1
          vm.scrollTo((dayOfTerm + 7).coerceAtMost(maxDayOfTerm), false)
        }
      },
    ) {
      Text(
        text = " ▷ ",
        color = MaterialTheme.textColors.primary,
      )
    }
    TextButton(
      modifier = Modifier.widthIn(min = 1.dp),
      onClick = {
        coroutineScope.launch {
          vm.scrollToToday()
        }
      },
    ) {
      Text(text = "今天")
    }
    TextButton(onClick = vm.onClickSelectingDate) {
      Text(text = "指定日期")
    }
  }
}

@Composable
private fun TodayText(modifier: Modifier, vm: CourseScreenViewModel) {
  val now = System.currentTimeMillis()
  val timeDiff = now - vm.termStartTime
  val week = if (timeDiff >= 0) {
    timeDiff / TimeUtil.ONE_WEEK + 1
  } else {
    timeDiff / TimeUtil.ONE_WEEK
  }
  val context = LocalContext.current
  val dateFormat by remember<ThreadLocalDelegate<SimpleDateFormat>> {
    ThreadLocalDelegate {
      SimpleDateFormat(context.getString(R.string.date_course_today), Locale.CHINA)
    }
  }
  val dateString = dateFormat.format(Date())
  val text = stringResource(R.string.text_course_today, dateString, week)
  Text(
    modifier = modifier,
    text = text,
    style = MaterialTheme.typography.caption,
    color = MaterialTheme.textColors.secondary,
  )
}

@Composable
private fun CourseMenu(vm: CourseScreenViewModel) {
  val collapsingMenu = { vm.menuExpanded = false }
  DropdownMenu(
    expanded = vm.menuExpanded,
    onDismissRequest = collapsingMenu,
  ) {
    DropdownMenuItem(onClick = vm.onClickImporting + collapsingMenu) {
      Text("教务系统导入")
    }
    DropdownMenuItem(onClick = vm.onClickClearing + collapsingMenu) {
      Text("清空课表")
    }
  }
}
