package com.njust.helper.course.week

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.njust.helper.R
import com.njust.helper.databinding.FgmtCourseWeekBinding
import com.njust.helper.model.Course
import java.text.SimpleDateFormat
import java.util.*

class CourseWeekFragment : Fragment() {
    private var beginTimeInMillis: Long = 0
    private var dateFormat: SimpleDateFormat? = null
    private lateinit var listener: Listener

    private lateinit var binding: FgmtCourseWeekBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FgmtCourseWeekBinding.inflate(inflater, container, false)
        binding.courseView.setListener(listener::showCourseList)
        return binding.root
    }

    override fun onAttach(context: Context?) {
        dateFormat = SimpleDateFormat(getString(R.string.date_course_week), Locale.CHINA)
        super.onAttach(context)
        listener = context as Listener
    }

    private fun getTime(week: Int): String {
        val time = beginTimeInMillis + (week - 1) * 604800000L
        return convert(time) + "~" + convert(time + 518400000L)
    }

    private fun convert(millis: Long): String {
        return dateFormat!!.format(Date(millis))
    }

    fun setWeek(week: Int) {
        binding.dateRange = getTime(week)
        binding.week = week
    }

    fun setList(courses: List<Course>) {
        binding.courses = courses
    }

    fun setBeginTimeInMillis(time: Long) {
        beginTimeInMillis = time
    }

    interface Listener {
        fun showCourseList(courses: List<@JvmSuppressWildcards Course>, day: Int, section: Int)
    }
}
