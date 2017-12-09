package com.njust.helper.course.day;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.njust.helper.course.CourseListAdapter;
import com.njust.helper.databinding.BottomSheetCourseListBinding;
import com.njust.helper.model.Course;

import java.util.ArrayList;
import java.util.List;

public class CourseListFragment extends BottomSheetDialogFragment {
    private CourseListAdapter adapter = new CourseListAdapter(new ArrayList<Course>());
    private String title, subTitle;

    public static CourseListFragment getInstance(List<Course> list, String title, String subTitle) {
        ArrayList<Course> arrayList;
        if (list instanceof ArrayList) {
            arrayList = (ArrayList<Course>) list;
        } else {
            arrayList = new ArrayList<>(list);
        }
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("courses", arrayList);
        bundle.putString("title", title);
        bundle.putString("subTitle", subTitle);
        CourseListFragment clf = new CourseListFragment();
        clf.setArguments(bundle);
        return clf;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<Course> list = getArguments().getParcelableArrayList("courses");
        //noinspection ConstantConditions
        adapter.setData(list);
        title = getArguments().getString("title");
        subTitle = getArguments().getString("subTitle");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BottomSheetCourseListBinding binding = BottomSheetCourseListBinding.inflate(inflater, container, false);
        binding.setTitle(title);
        binding.setSubTitle(subTitle);
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        return binding.getRoot();
    }
}
