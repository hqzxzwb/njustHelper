package com.njust.helper.coursequery

import android.content.Intent
import androidx.databinding.DataBindingUtil
import com.njust.helper.R
import com.njust.helper.activity.BaseActivity
import com.njust.helper.databinding.ActivityCourseQueryBinding

class CourseQueryActivity : BaseActivity() {
  private lateinit var binding: ActivityCourseQueryBinding

  override fun layoutRes(): Int = 0

  override fun layout() {
    binding = DataBindingUtil.setContentView(this, R.layout.activity_course_query)
    binding.button1.setOnClickListener { jumpToResult() }
  }

  private fun jumpToResult() {
    val intent = Intent(this, CourseQueryResultActivity::class.java)
    intent.putExtra("section", getCheckedTimeOfDay(binding.rgpSection.checkedRadioButtonId))
    intent.putExtra("day", getCheckedDayOfWeek(binding.rgpDay.checkedRadioButtonId))
    intent.putExtra("name", binding.txtCourseName.text.toString())
    intent.putExtra("teacher", binding.txtTeacher.text.toString())
    startActivity(intent)
  }

  private fun getCheckedTimeOfDay(checkedId: Int): Int {
    return when (checkedId) {
      R.id.radio1 -> -1
      R.id.radio2 -> 0
      R.id.radio3 -> 1
      R.id.radio4 -> 2
      R.id.radio5 -> 3
      R.id.radio6 -> 4
      else -> throw IllegalArgumentException()
    }
  }

  private fun getCheckedDayOfWeek(checkedId: Int): Int {
    return when (checkedId) {
      R.id.radio7 -> -1
      R.id.radio8 -> 0
      R.id.radio9 -> 1
      R.id.radio10 -> 2
      R.id.radio11 -> 3
      R.id.radio12 -> 4
      R.id.radio13 -> 5
      R.id.radio14 -> 6
      else -> throw IllegalArgumentException()
    }
  }
}
