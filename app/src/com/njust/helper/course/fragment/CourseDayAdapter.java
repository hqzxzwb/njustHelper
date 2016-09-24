package com.njust.helper.course.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.njust.helper.databinding.ItemCourseDayBinding;
import com.njust.helper.model.Course;
import com.njust.helper.tools.DataBindingHolder;

import java.util.List;

public class CourseDayAdapter extends RecyclerView.Adapter<DataBindingHolder<ItemCourseDayBinding>> {
    @SuppressWarnings("unchecked")
    private List<Course>[] mData = new List[0];
    private int mWeek = 0;
    private int mDay = 0;
    private CourseDayFragment fragment;

    CourseDayAdapter(CourseDayFragment fragment) {
        this.fragment = fragment;
    }

    public void setData(@NonNull List<Course>[] data, int week, int day) {
        mData = data;
        mWeek = week;
        mDay = day;
        notifyDataSetChanged();
    }

    @Override
    public DataBindingHolder<ItemCourseDayBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemCourseDayBinding binding = ItemCourseDayBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DataBindingHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(DataBindingHolder<ItemCourseDayBinding> holder, int position) {
        final List<Course> list = mData[position];
        final ItemCourseDayBinding binding = holder.getDataBinding();
        binding.setEmpty(list.size() == 0);
        binding.setMultiple(list.size() > 1);
        binding.setPosition(position);
        String weekString = " " + mWeek + " ";
        if (list.size() > 0) {
            Course course = null;
            for (Course t : list) {
                course = t;
                if (course.getWeek2().contains(weekString))
                    break;
            }
            assert course != null;
            binding.setValid(course.getWeek2().contains(weekString));
            binding.setCourse(course);
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment.getListener().showCourseList(list, mDay, binding.getPosition());
                }
            });
        } else {
            binding.getRoot().setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return mData.length;
    }
}