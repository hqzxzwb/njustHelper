package com.njust.helper.course.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.njust.helper.R
import com.njust.helper.compose.material.DarkActionBarAppCompatTheme
import com.njust.helper.compose.material.textColors
import com.njust.helper.compose.rememberDrawableResourcePainter
import com.njust.helper.model.Course

@Stable
class CourseListViewModel(
  val title: String,
  val subtitle: String,
  val courses: List<Course>,
)

@Composable
fun CourseListScreen(vm: CourseListViewModel) = DarkActionBarAppCompatTheme {
  Column(
    modifier = Modifier.padding(vertical = 16.dp),
  ) {
    Text(
      text = vm.title,
      style = MaterialTheme.typography.h6,
      modifier = Modifier.padding(horizontal = 24.dp),
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = vm.subtitle,
      style = MaterialTheme.typography.body1,
      modifier = Modifier.padding(horizontal = 24.dp),
    )
    Spacer(modifier = Modifier.height(8.dp))
    vm.courses.forEach {
      CourseListItem(it)
    }
  }
}

@Composable
private fun CourseListItem(course: Course) {
  Column(
    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
  ) {
    Text(
      text = course.name,
      color = colorResource(id = R.color.course_name),
      style = MaterialTheme.typography.subtitle1,
    )
    Spacer(modifier = Modifier.height(4.dp))
    IconTextRow(iconResId = R.drawable.ic_course_classroom_icon, text = course.classroom)
    Spacer(modifier = Modifier.height(4.dp))
    IconTextRow(iconResId = R.drawable.ic_course_teacher_icon, text = course.teacher)
    Spacer(modifier = Modifier.height(4.dp))
    IconTextRow(iconResId = R.drawable.ic_course_week_icon, text = course.week1)
  }
}

@Composable
private fun IconTextRow(iconResId: Int, text: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Image(
      painter = rememberDrawableResourcePainter(resId = iconResId),
      contentDescription = "",
    )
    Text(
      text = text,
      style = MaterialTheme.typography.caption,
      color = MaterialTheme.textColors.secondaryTextColor,
    )
  }
}
