package com.njust.helper.course.day

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.njust.helper.model.Course

class CourseDayAdapter internal constructor(private val fragment: CourseDayFragment)
  : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  private var mData: Array<MutableList<Course>> = arrayOf()
  private var mWeek = 0
  private var mDay = 0

  fun setData(data: Array<MutableList<Course>>, week: Int, day: Int) {
    mData = data
    mWeek = week
    mDay = day
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val view = ComposeView(parent.context)
    return object : RecyclerView.ViewHolder(view) {
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val list = mData[position]
    val weekString = " $mWeek "
    val course = list.firstOrNull { it.week2.contains(weekString) } ?: list.lastOrNull()
    val vm = CourseDayItemViewModel(
      course = course,
      multiple = list.size > 1,
      valid = course != null && course.week2.contains(weekString),
      position = position,
      onClick = {
        if (list.isNotEmpty()) {
          fragment.showCourseList(list, mDay, position)
        }
      }
    )
    (holder.itemView as ComposeView).setContent {
      CourseDayItem(vm = vm)
    }
  }

  override fun getItemCount(): Int {
    return mData.size
  }
}
