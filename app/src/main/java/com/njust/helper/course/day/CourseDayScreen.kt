package com.njust.helper.course.day

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.style.TextAlign
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.njust.helper.R
import com.njust.helper.compose.material.DarkActionBarAppCompatTheme
import com.njust.helper.compose.material.VerticalDivider
import com.njust.helper.compose.material.textColors
import com.njust.helper.model.Course
import com.njust.helper.tools.Constants
import com.njust.helper.tools.TimeUtil
import com.zwb.commonlibs.utils.ThreadLocalDelegate
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private val dateMonthFormat by ThreadLocalDelegate {
  SimpleDateFormat("MMM", Locale.CHINA)
}
private val dateDayFormat by ThreadLocalDelegate {
  SimpleDateFormat("d", Locale.CHINA)
}

@Stable
class CourseDayScreenViewModel(
  val onClickCourse: (courses: List<Course>, day: Int, section: Int) -> Unit,
) {
  val switchingDayOfTermFlow = MutableSharedFlow<Pair<Int, Boolean>>(replay = 1)

  suspend fun scrollTo(position: Int, animate: Boolean) {
    switchingDayOfTermFlow.emit(position to animate)
  }

  var termStartTime: Long by mutableStateOf(0)
  var courses: Array<out Array<out List<Course>>> by mutableStateOf(
    Array(7) {
      Array(Constants.COURSE_SECTION_COUNT) { listOf() }
    }
  )
  var dayOfTermFlow = MutableStateFlow(0)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CourseDayScreen(vm: CourseDayScreenViewModel) = DarkActionBarAppCompatTheme {
  val pagerState = rememberPagerState()
  LaunchedEffect(key1 = pagerState) {
    launch {
      vm.switchingDayOfTermFlow.collectLatest { (position, animate) ->
        if (pagerState.targetPage != position) {
          if (animate) {
            pagerState.animateScrollToPage(position)
          } else {
            pagerState.scrollToPage(position)
          }
        }
      }
    }
    launch {
      snapshotFlow { pagerState.targetPage }.collectLatest {
        vm.dayOfTermFlow.emit(it)
      }
    }
  }
  val dayOfTerm by vm.dayOfTermFlow.collectAsState()
  Column(
    modifier = Modifier.fillMaxWidth(),
  ) {
    DayIndicators(vm)
    Divider()
    HorizontalPager(
      count = Constants.MAX_WEEK_COUNT * 7,
      state = pagerState,
    ) { page ->
      val dayOfWeek = page % 7
      val week = page / 7 + 1
      val coursesOfDay = vm.courses[dayOfWeek]
      val courseDayItemViewModels = coursesOfDay.mapIndexed { index, list ->
        val weekString = " $week "
        val course = list.firstOrNull { it.week2.contains(weekString) } ?: list.lastOrNull()
        CourseDayItemViewModel(
          course = course,
          multiple = list.size > 1,
          valid = course != null && course.week2.contains(weekString),
          position = index,
          onClick = {
            if (list.isNotEmpty()) {
              vm.onClickCourse(list, dayOfWeek, index)
            }
          }
        )
      }
      CourseDayPage(items = courseDayItemViewModels)
    }
  }
}

@Composable
private fun DayIndicators(
  vm: CourseDayScreenViewModel,
) {
  val dayOfTerm by vm.dayOfTermFlow.collectAsState()
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(IntrinsicSize.Max),
  ) {
    val week = dayOfTerm / 7 + 1
    val dayOfWeek = dayOfTerm % 7
    var time = vm.termStartTime + (week - 1) * TimeUtil.ONE_WEEK
    val date = Date(time)
    val dayNamesOfWeek = stringArrayResource(id = R.array.days_of_week_short)
    val labelTextStyle = MaterialTheme.typography.caption.copy(
      textAlign = TextAlign.Center,
    )
    Text(
      modifier = Modifier.weight(1F),
      text = dateMonthFormat.format(date),
      style = labelTextStyle,
      color = MaterialTheme.textColors.secondary,
    )
    val coroutineScope = rememberCoroutineScope()
    for (i in 0 until 7) {
      VerticalDivider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.06F))
      val string = dateDayFormat.format(date)
      val text = if (i > 0 && string == "1") {
        dateMonthFormat.format(date) + "\n" + dayNamesOfWeek[i]
      } else {
        string + "\n" + dayNamesOfWeek[i]
      }
      Text(
        modifier = Modifier
          .weight(1F)
          .clickable {
            coroutineScope.launch {
              vm.scrollTo((week - 1) * 7 + i, true)
            }
          },
        text = text,
        style = labelTextStyle,
        color = if (i == dayOfWeek) Color.Magenta else MaterialTheme.textColors.secondary,
      )
      time += TimeUtil.ONE_DAY
      date.time = time
    }
  }
}

@Composable
private fun CourseDayPage(items: List<CourseDayItemViewModel>) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()
      .verticalScroll(rememberScrollState()),
  ) {
    items.forEach { vm ->
      CourseDayItem(vm = vm)
      Divider()
    }
  }
}
