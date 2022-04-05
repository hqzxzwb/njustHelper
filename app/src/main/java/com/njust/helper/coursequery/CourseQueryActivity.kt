package com.njust.helper.coursequery

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity

class CourseQueryActivity : AppCompatActivity() {
  private val vm = CourseQueryViewModel(
    onClickHome = this::finish,
    onClickQuery = this::jumpToResult,
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      CourseQueryScreen(vm = this.vm)
    }
  }

  private fun jumpToResult() {
    val intent = Intent(this, CourseQueryResultActivity::class.java)
    intent.putExtra("section", vm.selectedSection)
    intent.putExtra("day", vm.selectedDayOfWeek)
    intent.putExtra("name", vm.courseName)
    intent.putExtra("teacher", vm.teacher)
    startActivity(intent)
  }
}
