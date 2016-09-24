package com.njust.helper.course.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.njust.helper.R;
import com.njust.helper.course.CourseListAdapter;
import com.njust.helper.model.Course;
import com.zwb.commonlibs.ui.DividerItemDecoration;

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
        View view = inflater.inflate(R.layout.bottom_sheet_course_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(adapter);
        ((TextView) view.findViewById(R.id.tvTitle)).setText(title);
        ((TextView) view.findViewById(R.id.tvSubTitle)).setText(subTitle);
        return view;
    }
}
