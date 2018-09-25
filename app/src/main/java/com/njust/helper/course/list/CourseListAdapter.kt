package com.njust.helper.course.list

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.njust.helper.databinding.ItemCourseListBinding
import com.njust.helper.model.Course
import com.njust.helper.tools.DataBindingHolder

class CourseListAdapter(private var mData: List<Course>) : androidx.recyclerview.widget.RecyclerView.Adapter<DataBindingHolder<ItemCourseListBinding>>() {
    fun setData(data: List<Course>) {
        mData = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingHolder<ItemCourseListBinding> {
        val binding = ItemCourseListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataBindingHolder(binding)
    }

    override fun onBindViewHolder(holder: DataBindingHolder<ItemCourseListBinding>, position: Int) {
        holder.dataBinding.course = mData[position]
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}
