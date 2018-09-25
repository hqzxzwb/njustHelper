package com.njust.helper.course.day

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.njust.helper.databinding.ItemCourseDayBinding
import com.njust.helper.model.Course
import com.njust.helper.tools.DataBindingHolder

class CourseDayAdapter internal constructor(private val fragment: CourseDayFragment)
    : androidx.recyclerview.widget.RecyclerView.Adapter<DataBindingHolder<ItemCourseDayBinding>>() {
    private var mData: Array<MutableList<Course>> = arrayOf()
    private var mWeek = 0
    private var mDay = 0

    fun setData(data: Array<MutableList<Course>>, week: Int, day: Int) {
        mData = data
        mWeek = week
        mDay = day
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingHolder<ItemCourseDayBinding> {
        val binding = ItemCourseDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataBindingHolder(binding)
    }

    override fun onBindViewHolder(holder: DataBindingHolder<ItemCourseDayBinding>, position: Int) {
        val list = mData[position]
        val binding = holder.dataBinding
        binding.empty = list.isEmpty()
        binding.multiple = list.size > 1
        binding.position = position
        val weekString = " $mWeek "
        if (list.isNotEmpty()) {
            var course: Course? = null
            for (t in list) {
                course = t
                if (course.week2.contains(weekString)) {
                    break
                }
            }
            binding.valid = course!!.week2.contains(weekString)
            binding.course = course
            binding.root.setOnClickListener { fragment.showCourseList(list, mDay, binding.position) }
        } else {
            binding.root.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}
