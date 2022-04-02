package com.njust.helper.course.week

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.njust.helper.R
import com.njust.helper.compose.material.textColors
import com.njust.helper.course.CourseScreenViewModel
import com.njust.helper.model.Course
import com.njust.helper.tools.Constants
import com.njust.helper.tools.TimeUtil
import com.zwb.commonlibs.utils.ThreadLocalDelegate
import java.text.SimpleDateFormat
import java.util.*

private val sectionIndicatorWidth = 16.dp
private val courseItemWidth = 100.dp
private val courseItemHeight = 120.dp
private val dividerSize = 1.dp
private val indicatorTextStyle
  @Composable get() = MaterialTheme.typography.caption
    .copy(
      color = MaterialTheme.textColors.secondary,
      textAlign = TextAlign.Center,
    )
private val itemBackgroundColor = Color(0xFFE0E0E0)
private val multipleIndicatorSize = 8.dp
private val multipleIndicatorColor = Color(0xFF808080)
private val crossIndicatorHalfSize = 2.dp
private val crossIndicatorColor
  @Composable get() = MaterialTheme.colors.onSurface
private const val MAX_COURSE_NAME_CHARS = 12

@Composable
fun CourseWeekScreen(
  modifier: Modifier,
  vm: CourseScreenViewModel,
) {
  val context = LocalContext.current
  val dateFormat by remember<ThreadLocalDelegate<SimpleDateFormat>> {
    ThreadLocalDelegate {
      SimpleDateFormat(context.getString(R.string.date_course_week), Locale.getDefault())
    }
  }
  val dayOfTerm = vm.dayOfTerm
  val week = dayOfTerm / 7 + 1
  val horizontalScrollState = rememberScrollState()
  val verticalScrollState = rememberScrollState()
  Column(
    modifier = modifier,
  ) {
    val time = vm.termStartTime + (week - 1) * TimeUtil.ONE_WEEK
    val dateText = remember(key1 = week) {
      dateFormat.format(Date(time)) + "~" + dateFormat.format(Date(time + 6 * TimeUtil.ONE_DAY))
    }
    Text(
      modifier = Modifier.fillMaxWidth(),
      text = dateText,
      style = indicatorTextStyle,
    )
    Row {
      Text(
        modifier = Modifier
          .width(sectionIndicatorWidth)
          .background(itemBackgroundColor),
        text = "",
        style = indicatorTextStyle,
      )
      Spacer(modifier = Modifier.width(dividerSize))
      DayOfWeekIndicators(horizontalScrollState)
    }
    Spacer(modifier = Modifier.height(dividerSize))
    Row {
      SectionIndicators(verticalScrollState)
      Spacer(modifier = Modifier.width(dividerSize))
      CourseArea(
        vm = vm,
        week = week,
        horizontalScrollState = horizontalScrollState,
        verticalScrollState = verticalScrollState,
      )
    }
  }
}

@Composable
private fun DayOfWeekIndicators(scrollState: ScrollState) {
  Row(
    modifier = Modifier.horizontalScroll(scrollState),
  ) {
    val texts = stringArrayResource(id = R.array.days_of_week_medium)
    texts.forEach { text ->
      Text(
        modifier = Modifier
          .width(courseItemWidth)
          .background(itemBackgroundColor),
        text = text,
        style = indicatorTextStyle,
      )
      Spacer(modifier = Modifier.width(dividerSize))
    }
  }
}

@Composable
private fun SectionIndicators(scrollState: ScrollState) {
  Column(
    modifier = Modifier.verticalScroll(scrollState),
  ) {
    repeat(Constants.COURSE_SECTION_COUNT) { section ->
      Box(
        modifier = Modifier
          .width(sectionIndicatorWidth)
          .height(courseItemHeight),
      ) {
        Text(
          modifier = Modifier.align(Alignment.CenterEnd),
          text = (section + 1).toString(),
          style = indicatorTextStyle,
        )
      }
      Spacer(modifier = Modifier.height(dividerSize))
    }
  }
}

@Composable
private fun CourseArea(
  vm: CourseScreenViewModel,
  week: Int,
  horizontalScrollState: ScrollState,
  verticalScrollState: ScrollState,
) {
  val crossIndicatorColor = crossIndicatorColor
  Column(
    modifier = Modifier
      .verticalScroll(verticalScrollState)
      .horizontalScroll(horizontalScrollState)
      .drawWithContent {
        drawContent()
        for (i in 1 until Constants.COURSE_SECTION_COUNT) {
          for (j in 1 until 7) {
            val x = (courseItemWidth + dividerSize) * j - dividerSize / 2
            val y = (courseItemHeight + dividerSize) * i - dividerSize / 2
            drawLine(
              color = crossIndicatorColor,
              start = Offset((x - crossIndicatorHalfSize).toPx(), y.toPx()),
              end = Offset((x + crossIndicatorHalfSize).toPx(), y.toPx()),
            )
            drawLine(
              color = crossIndicatorColor,
              start = Offset(x.toPx(), (y - crossIndicatorHalfSize).toPx()),
              end = Offset(x.toPx(), (y + crossIndicatorHalfSize).toPx()),
            )
          }
        }
      },
  ) {
    repeat(Constants.COURSE_SECTION_COUNT) { section ->
      Row {
        repeat(7) { dayOfWeek ->
          val courses = vm.courses[dayOfWeek][section]
          CourseItem(
            week = week,
            dayOfWeek = dayOfWeek,
            section = section,
            courses = courses,
            onClickCourse = vm.onClickCourse,
          )
          Spacer(modifier = Modifier.width(dividerSize))
        }
      }
      Spacer(modifier = Modifier.height(dividerSize))
    }
  }
}

@Composable
private fun CourseItem(
  week: Int,
  dayOfWeek: Int,
  section: Int,
  courses: List<Course>,
  onClickCourse: (courses: List<Course>, day: Int, section: Int) -> Unit,
) {
  val density = LocalDensity.current.density
  val multipleIndicatorPath = remember(density) {
    Path().apply {
      moveTo(courseItemWidth.px(density), (courseItemHeight - multipleIndicatorSize).px(density))
      lineTo(courseItemWidth.px(density), courseItemHeight.px(density))
      lineTo((courseItemWidth - multipleIndicatorSize).px(density), courseItemHeight.px(density))
      close()
    }
  }
  val weekString = " $week "
  val course = courses.firstOrNull { it.week2.contains(weekString) }
    ?: courses.firstOrNull()
  val color = if (course != null) {
    itemBackgroundColor
  } else {
    Color.Unspecified
  }
  val isCourseInThisWeek = course != null && course.week2.contains(weekString)
  val text = if (course != null) {
    var name = course.name
    if (name.length > MAX_COURSE_NAME_CHARS) {
      name = name.substring(0, MAX_COURSE_NAME_CHARS - 1) + "..."
    }
    if (!isCourseInThisWeek) {
      name = "[非本周]$name"
    }
    "${name}@${course.classroom}"
  } else {
    ""
  }
  val textColor = if (isCourseInThisWeek) {
    Color.Unspecified
  } else {
    MaterialTheme.textColors.tertiary
  }
  Text(
    modifier = Modifier
      .background(color)
      .width(courseItemWidth)
      .height(courseItemHeight)
      .let {
        if (course != null) {
          it.clickable {
            onClickCourse(courses, dayOfWeek, section)
          }
        } else {
          it
        }
      }
      .drawWithContent {
        drawContent()
        if (courses.size > 1) {
          drawPath(
            path = multipleIndicatorPath,
            color = multipleIndicatorColor,
          )
        }
      }
      .padding(2.dp),
    text = text,
    style = MaterialTheme.typography.body2,
    color = textColor,
  )
}

private fun Dp.px(density: Float): Float {
  return value * density
}
