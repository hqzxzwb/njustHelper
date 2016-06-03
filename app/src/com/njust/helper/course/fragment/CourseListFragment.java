package com.njust.helper.course.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.njust.helper.R;
import com.njust.helper.course.CourseListAdapter;
import com.njust.helper.model.Course;
import com.zwb.commonlibs.ui.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class CourseListFragment extends BottomSheetDialogFragment {
    private CourseListAdapter adapter = new CourseListAdapter(new ArrayList<Course>());

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_course_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        assert recyclerView != null;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void setData(List<Course> data) {
        adapter.setData(data);
    }
}
