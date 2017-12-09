package com.njust.helper.course.week;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.njust.helper.R;
import com.njust.helper.databinding.FgmtCourseWeekBinding;
import com.njust.helper.model.Course;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CourseWeekFragment extends Fragment {
    private long beginTimeInMillis;
    private SimpleDateFormat dateFormat;
    private Listener listener;

    private FgmtCourseWeekBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FgmtCourseWeekBinding.inflate(inflater, container, false);
        binding.courseView.setListener(listener::showCourseList);
        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        dateFormat = new SimpleDateFormat(getString(R.string.date_course_week), Locale.CHINA);
        super.onAttach(context);
        listener = (Listener) context;
    }

    private String getTime(int week) {
        long time = beginTimeInMillis + (week - 1) * 604800000L;
        return convert(time) + "~" + convert(time + 518400000L);
    }

    private String convert(long millis) {
        return dateFormat.format(new Date(millis));
    }

    public void setWeek(int week) {
        binding.setDateRange(getTime(week));
        binding.setWeek(week);
    }

    public void setList(List<Course> courses) {
        binding.setCourses(courses);
    }

    public void setBeginTimeInMillis(long time) {
        beginTimeInMillis = time;
    }

    public interface Listener {
        void showCourseList(List<Course> courses, int day, int section);
    }
}
