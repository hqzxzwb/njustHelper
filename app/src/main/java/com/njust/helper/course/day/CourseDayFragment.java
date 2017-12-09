package com.njust.helper.course.day;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.njust.helper.R;
import com.njust.helper.model.Course;
import com.njust.helper.tools.Constants;
import com.zwb.commonlibs.adapter.EfficientPagerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CourseDayFragment extends Fragment implements OnPageChangeListener {
    @SuppressWarnings("unchecked")
    final List<Course>[][] mLists = new List[7][5];
    private SimpleDateFormat DATE_MONTH_FORMAT = new SimpleDateFormat("MMM", Locale.CHINA);
    private SimpleDateFormat DATE_DAY_FORMAT = new SimpleDateFormat("d", Locale.CHINA);
    private String[] dayOfWeek;

    private TextView[] mTextViews;
    @BindView(R.id.textMonth)
    TextView mMonthView;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private long beginTimeInMillis;
    private Listener listener;

    public Listener getListener() {
        return listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 5; j++)
                mLists[i][j] = new ArrayList<>();
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        mViewPager.addOnPageChangeListener(this);

        mTextViews = new TextView[7];
        mTextViews[0] = view.findViewById(R.id.dayOfWeek0);
        mTextViews[1] = view.findViewById(R.id.dayOfWeek1);
        mTextViews[2] = view.findViewById(R.id.dayOfWeek2);
        mTextViews[3] = view.findViewById(R.id.dayOfWeek3);
        mTextViews[4] = view.findViewById(R.id.dayOfWeek4);
        mTextViews[5] = view.findViewById(R.id.dayOfWeek5);
        mTextViews[6] = view.findViewById(R.id.dayOfWeek6);

        for (int i = 0; i < 7; i++) {
            final int j = i;
            mTextViews[i].setOnClickListener(v -> listener.onDayPressed(j));
        }
        mTextViews[0].setBackgroundColor(Color.GRAY);

        PagerAdapter adapter = new EfficientPagerAdapter() {
            @Override
            public int getCount() {
                return Constants.MAX_WEEK_COUNT * 7;
            }

            @Override
            protected void updateView(@NonNull View view, int position) {
                RecyclerView recyclerView = (RecyclerView) view;
                CourseDayAdapter adapter = new CourseDayAdapter(CourseDayFragment.this);
                int dayOfWeek = position % 7;
                adapter.setData(mLists[dayOfWeek], position / 7 + 1, dayOfWeek);
                recyclerView.setAdapter(adapter);
                view.setTag(position);
            }

            @NonNull
            @Override
            protected View onCreateNewView(@NonNull ViewGroup container) {
                RecyclerView view = (RecyclerView) getActivity().getLayoutInflater().inflate(
                        R.layout.pager_course_day, container, false);
                view.setLayoutManager(new LinearLayoutManager(getActivity()));
                view.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                return view;
            }
        };
        mViewPager.setAdapter(adapter);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fgmt_course_day, container, false);
    }

    @Override
    public void onAttach(Context context) {
        listener = (Listener) context;
        dayOfWeek = getResources().getStringArray(R.array.days_of_week_short);
        super.onAttach(context);
    }

    public void setList(List<Course> courses) {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 5; j++)
                mLists[i][j].clear();
        }
        for (Course course : courses) {
            mLists[course.getDay()][course.getSec1()].add(course);
        }
        PagerAdapter adapter = mViewPager.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void setPosition(int position) {
        mViewPager.setCurrentItem(position);
    }

    public void setCurrentDay(int currentDay) {
        mTextViews[currentDay].setTextColor(Color.MAGENTA);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < 7; i++) {
            mTextViews[i].setBackgroundColor(Color.TRANSPARENT);
        }
        mTextViews[position % 7].setBackgroundColor(Color.GRAY);
        listener.onDayChange(position);
    }

    @SuppressLint("SetTextI18n")
    public void setWeek(int week) {
        long time = beginTimeInMillis + (week - 1) * 604800000L;
        Date date = new Date(time);
        mMonthView.setText(DATE_MONTH_FORMAT.format(date));
        for (int i = 0; i < 7; i++) {
            String string = DATE_DAY_FORMAT.format(date);
            if (i > 0 && string.equals("1")) {
                mTextViews[i].setText(DATE_MONTH_FORMAT.format(date) + "\n" + dayOfWeek[i]);
            } else {
                mTextViews[i].setText(string + "\n" + dayOfWeek[i]);
            }
            time += 86400000L;
            date.setTime(time);
        }
    }

    public void setStartTime(long time) {
        beginTimeInMillis = time;
    }

    public interface Listener {
        void onDayChange(int position);

        void onDayPressed(int day);

        void showCourseList(List<? extends Course> courses, int day, int section);
    }
}
