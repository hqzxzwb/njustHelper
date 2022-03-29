package com.njust.helper.course.day

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.njust.helper.model.Course
import com.njust.helper.tools.Constants
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

class CourseDayFragment : Fragment() {
  private val mLists = Array(7) {
    Array(Constants.COURSE_SECTION_COUNT) { mutableListOf<Course>() }
  }

  private lateinit var listener: Listener

  private val vm = CourseDayScreenViewModel(
    onClickCourse = this::showCourseList,
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    lifecycleScope.launch {
      vm.dayOfTermFlow.drop(1).collect {
        listener.onDayChange(it)
      }
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    val view = ComposeView(requireContext())
    view.setContent { CourseDayScreen(vm = vm) }
    return view
  }

  override fun onAttach(context: Context) {
    listener = context as Listener
    super.onAttach(context)
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

  fun setPosition(position: Int, smoothScroll: Boolean) {
    lifecycleScope.launch {
      vm.scrollTo(position, smoothScroll)
    }
  }

  fun setStartTime(time: Long) {
    vm.termStartTime = time
  }

  fun showCourseList(courses: List<Course>, day: Int, section: Int) {
    listener.showCourseList(courses, day, section)
  }

  interface Listener {
    fun onDayChange(position: Int)

    fun onDayPressed(day: Int)

    fun showCourseList(courses: List<Course>, day: Int, section: Int)
  }
}
