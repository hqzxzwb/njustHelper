package com.njust.helper.course;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.njust.helper.databinding.ItemCourseListBinding;
import com.njust.helper.model.Course;
import com.njust.helper.tools.DataBindingHolder;

import java.util.List;

public class CourseListAdapter extends RecyclerView.Adapter<DataBindingHolder<ItemCourseListBinding>> {
    private List<Course> mData;

    public CourseListAdapter(@NonNull List<Course> data) {
        mData = data;
    }

    public void setData(@NonNull List<Course> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public DataBindingHolder<ItemCourseListBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemCourseListBinding binding = ItemCourseListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DataBindingHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(DataBindingHolder<ItemCourseListBinding> holder, int position) {
        holder.getDataBinding().setCourse(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
