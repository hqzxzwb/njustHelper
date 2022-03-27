package com.njust.helper.course.day

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.njust.helper.R
import com.njust.helper.compose.material.DarkActionBarAppCompatTheme
import com.njust.helper.compose.material.textColors
import com.njust.helper.compose.rememberDrawableResourcePainter
import com.njust.helper.model.Course
import com.zwb.commonlibs.utils.NoOpFunction

@Immutable
class CourseDayItemViewModel(
  val course: Course?,
  val multiple: Boolean,
  val valid: Boolean,
  val position: Int,
  val onClick: () -> Unit,
)

@Composable
fun CourseDayItem(
  vm: CourseDayItemViewModel,
) = DarkActionBarAppCompatTheme {
  ConstraintLayout(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = vm.onClick)
      .padding(horizontal = 16.dp, vertical = 12.dp),
  ) {
    val (sectionIndex, courseTime, name, classroom, more, teacher, week) = createRefs()

    Text(
      modifier = Modifier
        .width(40.dp)
        .constrainAs(sectionIndex) {
          start.linkTo(parent.start)
          top.linkTo(parent.top)
          bottom.linkTo(parent.bottom)
        },
      text = "${vm.position + 1}",
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.body2,
      color = MaterialTheme.textColors.primary,
    )
    Text(
      modifier = Modifier.constrainAs(courseTime) {
        top.linkTo(parent.top)
        end.linkTo(parent.end)
      },
      text = stringArrayResource(id = R.array.section_start)[vm.position],
      style = MaterialTheme.typography.body2,
      color = MaterialTheme.textColors.tertiary,
    )
    Text(
      modifier = Modifier
        .constrainAs(name) {
          linkTo(
            start = sectionIndex.end,
            end = courseTime.start,
            bias = 0F,
          )
          top.linkTo(parent.top)
          width = Dimension.fillToConstraints
        },
      text = when {
        vm.course == null -> ""
        vm.valid -> vm.course.name
        else -> stringResource(R.string.text_course_not_this_week, vm.course.name)
      },
      style = MaterialTheme.typography.subtitle1,
      color = if (vm.valid) {
        MaterialTheme.colors.primary
      } else {
        MaterialTheme.textColors.secondary
      },
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
    InfoWithIcon(
      modifier = Modifier.constrainAs(classroom) {
        top.linkTo(name.bottom, margin = 4.dp)
        start.linkTo(sectionIndex.end)
      },
      iconResId = R.drawable.ic_course_classroom_icon,
      text = vm.course?.classroom.orEmpty(),
      enabled = vm.valid,
    )
    Text(
      modifier = Modifier
        .alpha(if (vm.multiple) 1F else 0F)
        .constrainAs(more) {
          end.linkTo(parent.end)
          top.linkTo(parent.top)
          bottom.linkTo(parent.bottom)
        },
      text = "更多>",
      style = MaterialTheme.typography.caption,
      color = MaterialTheme.textColors.secondary,
    )
    InfoWithIcon(
      modifier = Modifier.constrainAs(teacher) {
        linkTo(
          start = classroom.end,
          startMargin = 16.dp,
          end = more.start,
          endMargin = 16.dp,
          bias = 0F,
        )
        top.linkTo(classroom.top)
        width = Dimension.fillToConstraints
      },
      iconResId = R.drawable.ic_course_teacher_icon,
      text = vm.course?.teacher.orEmpty(),
      enabled = vm.valid,
    )
    InfoWithIcon(
      modifier = Modifier.constrainAs(week) {
        linkTo(
          start = sectionIndex.end,
          end = more.start,
          endMargin = 16.dp,
          bias = 0F,
        )
        top.linkTo(classroom.bottom, margin = 4.dp)
        width = Dimension.fillToConstraints
      },
      iconResId = R.drawable.ic_course_week_icon,
      text = vm.course?.week1.orEmpty(),
      enabled = vm.valid,
    )
  }
}

@Composable
private fun InfoWithIcon(
  modifier: Modifier,
  iconResId: Int,
  text: String,
  enabled: Boolean,
) {
  val tertiaryColor = MaterialTheme.textColors.tertiary
  val tertiaryColorWithState = if (enabled) {
    tertiaryColor
  } else {
    tertiaryColor.copy(alpha = ContentAlpha.disabled)
  }
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    if (text.isNotEmpty()) {
      Icon(
        modifier = Modifier.alpha(if (enabled) 1F else ContentAlpha.disabled),
        painter = rememberDrawableResourcePainter(resId = iconResId),
        contentDescription = "",
      )
    }
    Text(
      text = text,
      style = MaterialTheme.typography.body2,
      color = tertiaryColorWithState,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

@Preview
@Composable
private fun CourseDayItemPreviewMultiple() {
  CourseDayItem(
    vm = CourseDayItemViewModel(
      course = Course().apply {
        name = "NAME"
        teacher = "TEACHER"
        classroom = "CLASSROOM"
        week1 = "WEEKS"
      },
      multiple = true,
      valid = true,
      position = 1,
      onClick = NoOpFunction,
    )
  )
}

@Preview
@Composable
private fun CourseDayItemPreviewSingleDisabled() {
  CourseDayItem(
    vm = CourseDayItemViewModel(
      course = Course().apply {
        name = "A VERY LONG NAME MMMMMMMMMMMMMMMMMMMMMM"
        teacher = "TEACHER"
        classroom = "CLASSROOM"
        week1 = "WEEKS"
      },
      multiple = false,
      valid = false,
      position = 1,
      onClick = NoOpFunction,
    )
  )
}

@Preview
@Composable
private fun CourseDayItemPreviewEmpty() {
  CourseDayItem(
    vm = CourseDayItemViewModel(
      course = null,
      multiple = false,
      valid = false,
      position = 1,
      onClick = NoOpFunction,
    )
  )
}
