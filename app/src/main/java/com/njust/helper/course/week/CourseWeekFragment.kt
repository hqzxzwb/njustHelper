package com.njust.helper.course.week

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import com.njust.helper.course.day.CourseDayScreenViewModel
import com.njust.helper.model.Course
import com.njust.helper.tools.Constants
import kotlinx.coroutines.launch

class CourseWeekFragment : androidx.fragment.app.Fragment() {
  private val mLists = Array(7) {
    Array(Constants.COURSE_SECTION_COUNT) { mutableListOf<Course>() }
  }

  private lateinit var listener: Listener

  private val vm = CourseDayScreenViewModel(
    onClickCourse = { courses, day, section -> listener.showCourseList(courses, day, section) },
  )

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    val view = ComposeView(requireContext())
    view.setContent { CourseWeekScreen(vm = vm) }
    return view
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    listener = context as Listener
  }

  fun setPosition(position: Int) {
    lifecycleScope.launch {
      vm.dayOfTermFlow.emit(position)
    }
  }

  fun setList(courses: List<Course>) {
    for (i in 0 until 7) {
      for (j in 0 until Constants.COURSE_SECTION_COUNT) {
        mLists[i][j].clear()
      }
    }
    for (course in courses) {
      mLists[course.day][course.sec1].add(course)
    }
    vm.courses = mLists
  }

  fun setBeginTimeInMillis(time: Long) {
    vm.termStartTime = time
  }

  interface Listener {
    fun showCourseList(courses: List<Course>, day: Int, section: Int)
  }
}
