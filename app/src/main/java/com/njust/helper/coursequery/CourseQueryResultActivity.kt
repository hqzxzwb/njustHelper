package com.njust.helper.coursequery

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.njust.helper.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class CourseQueryResultActivity : AppCompatActivity() {
  private var section: Int = 0
  private var day: Int = 0
  private val viewModel by viewModels<CourseQueryResultViewModel>()
  private lateinit var name: String
  private lateinit var teacher: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val intent = intent
    val timeOfDay = intent.getIntExtra("section", 0)
    section = if (timeOfDay < 0) -1 else 1 shl timeOfDay
    val dayOfWeek = intent.getIntExtra("day", 0)
    day = if (dayOfWeek < 0) -1 else 1 shl dayOfWeek
    name = intent.getStringExtra("name")!!
    teacher = intent.getStringExtra("teacher")!!

    query()

    setContent {
      CourseQueryResultScreen(
        isRefreshing = viewModel.isRefreshing,
        items = viewModel.items,
        snackbarMessageFlow = viewModel.snackbarMessageFlow,
        onRefresh = {
          if (!viewModel.isRefreshing) {
            query()
          }
        },
        onClickHome = { finish() },
      )
    }
  }

  private fun query() {
    lifecycleScope.launch {
      viewModel.query(this@CourseQueryResultActivity, name, teacher, section, day)
    }
  }
}

class CourseQueryResultViewModel : ViewModel() {
  var isRefreshing by mutableStateOf(false)
  var items: List<CourseQueryItem> by mutableStateOf(listOf())
  var snackbarMessageFlow = MutableSharedFlow<String>()

  suspend fun query(context: Context, name: String, teacher: String, section: Int, day: Int) {
    isRefreshing = true
    try {
      val data = CourseQueryDao.getInstance(context)
        .queryCourses(name, teacher, section, day)
      if (data.isEmpty()) {
        snackbarMessageFlow.emit(context.getString(R.string.message_no_result))
      } else {
        items = data
      }
    } catch (e: Exception) {
      isRefreshing = false
      snackbarMessageFlow.emit(context.getString(R.string.message_net_error))
    }
    isRefreshing = false
  }
}
