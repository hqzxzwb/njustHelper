package com.njust.helper.coursequery

import androidx.appcompat.widget.AppCompatEditText
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.widget.doAfterTextChanged
import com.google.accompanist.flowlayout.FlowRow
import com.google.android.material.textfield.TextInputLayout
import com.njust.helper.R
import com.njust.helper.compose.material.DarkActionBarAppCompatTheme
import com.njust.helper.compose.material.textColors

class CourseQueryViewModel(
  val onClickHome: () -> Unit,
  val onClickQuery: () -> Unit,
) {
  var courseName = ""
  var teacher = ""
  var selectedSection by mutableStateOf(-1)
  var selectedDayOfWeek by mutableStateOf(-1)
}

@Composable
fun CourseQueryScreen(
  vm: CourseQueryViewModel,
) = DarkActionBarAppCompatTheme {
  Scaffold(
    modifier = Modifier
      .fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(text = stringResource(id = R.string.title_courseQuery)) },
        navigationIcon = {
          IconButton(onClick = vm.onClickHome) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
          }
        },
      )
    },
    floatingActionButton = {
      FloatingActionButton(onClick = vm.onClickQuery) {
        Icon(
          imageVector = Icons.Filled.Check,
          contentDescription = null,
          tint = colorResource(id = android.R.color.white)
        )
      }
    }
  ) {
    Column(
      modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 20.dp),
    ) {
      AndroidTextInputLayout(
        hint = "课程名",
        onTextChanged = { vm.courseName = it },
      )
      AndroidTextInputLayout(
        hint = "教师",
        onTextChanged = { vm.teacher = it },
      )
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "节次：",
        style = MaterialTheme.typography.caption,
        color = MaterialTheme.textColors.secondary,
      )
      FlowRow(
        mainAxisSpacing = 16.dp,
      ) {
        SectionSelectionRadio(vm = vm, target = -1, text = "不限　　")
        val sections = stringArrayResource(id = R.array.sections)
        sections.forEachIndexed { index, section ->
          SectionSelectionRadio(vm = vm, target = index, text = section)
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "星期几：",
        style = MaterialTheme.typography.caption,
        color = MaterialTheme.textColors.secondary,
      )
      FlowRow(
        mainAxisSpacing = 16.dp,
      ) {
        DayOfWeekSelectionRadio(vm = vm, target = -1, text = "不限　")
        val daysOfWeek = stringArrayResource(id = R.array.days_of_week)
        daysOfWeek.forEachIndexed { index, dayOfWeek ->
          DayOfWeekSelectionRadio(vm = vm, target = index, text = dayOfWeek)
        }
      }
    }
  }
}

@Composable
private fun SectionSelectionRadio(
  vm: CourseQueryViewModel,
  target: Int,
  text: String,
) {
  Row(
    modifier = Modifier.clickable { vm.selectedSection = target },
  ) {
    RadioButton(
      selected = vm.selectedSection == target,
      onClick = null,
    )
    Text(
      text = text,
    )
  }
}

@Composable
private fun DayOfWeekSelectionRadio(
  vm: CourseQueryViewModel,
  target: Int,
  text: String,
) {
  Row(
    modifier = Modifier.clickable { vm.selectedDayOfWeek = target },
  ) {
    RadioButton(
      selected = vm.selectedDayOfWeek == target,
      onClick = null,
    )
    Text(
      text = text,
    )
  }
}

@Composable
private fun AndroidTextInputLayout(
  hint: String,
  onTextChanged: (text: String) -> Unit,
) {
  AndroidView(
    modifier = Modifier.fillMaxWidth(),
    factory = { context ->
      val textInputLayout = TextInputLayout(context)
      val editText = AppCompatEditText(context)
      editText.hint = hint
      editText.doAfterTextChanged { onTextChanged(it?.toString().orEmpty()) }
      textInputLayout.addView(editText)
      textInputLayout
    }
  )
}
