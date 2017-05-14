package com.njust.helper.course;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.njust.helper.AccountActivity;
import com.njust.helper.MainActivity;
import com.njust.helper.R;
import com.njust.helper.activity.BaseActivity;
import com.njust.helper.course.fragment.CourseDayFragment;
import com.njust.helper.course.fragment.CourseListFragment;
import com.njust.helper.course.fragment.CourseWeekFragment;
import com.njust.helper.course.fragment.PickWeekFragment;
import com.njust.helper.model.Course;
import com.njust.helper.tools.Constants;
import com.njust.helper.tools.Prefs;
import com.zwb.commonlibs.injection.ViewInjection;
import com.zwb.commonlibs.ui.DatePickerDialogFix;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CourseActivity extends BaseActivity implements OnDateSetListener,
        CourseDayFragment.Listener, PickWeekFragment.Listener, CourseWeekFragment.Listener {
    public PickWeekFragment pickWeekFragment;
    private long termStartTime;
    private int currentDay, currentWeek;
    private SimpleDateFormat dateFormat;
    private CourseDayFragment dayFragment;
    private CourseWeekFragment weekFragment;
    @ViewInjection(R.id.txtToday)
    private TextView todayTextView;
    @ViewInjection(R.id.tvPickWeek)
    private Button pickWeekButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dateFormat = new SimpleDateFormat(getString(R.string.date_course_today), Locale.CHINA);

        final View weekView = findViewById(R.id.course_week_fragment);
        //noinspection ConstantConditions
        weekView.setVisibility(View.GONE);
        final View dayView = findViewById(R.id.course_day_fragment);
        //noinspection ConstantConditions
        RadioGroup group = (RadioGroup) getSupportActionBar().getCustomView();
        ((RadioButton) group.findViewById(R.id.radio0)).setChecked(true);
        group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio0) {
                    //noinspection ConstantConditions
                    dayView.setVisibility(View.VISIBLE);
                    weekView.setVisibility(View.GONE);
                } else {
                    //noinspection ConstantConditions
                    dayView.setVisibility(View.GONE);
                    weekView.setVisibility(View.VISIBLE);
                }
            }
        });

        FragmentManager manager = getFragmentManager();

        dayFragment = (CourseDayFragment) manager.findFragmentById(R.id.course_day_fragment);
        weekFragment = (CourseWeekFragment) manager.findFragmentById(R.id.course_week_fragment);

        new Thread() {
            @Override
            public void run() {
                final List<Course> mainList = CourseManager.getInstance(CourseActivity.this).getCourses();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mainList.size() == 0) {
                            //课表为空时，提示导入课表
                            promptImportMessage();
                        }
                        dayFragment.setList(mainList);
                        weekFragment.setList(mainList);
                    }
                });
            }
        }.start();

        termStartTime = Prefs.getTermStartTime(this);

        dayFragment.setStartTime(termStartTime);
        weekFragment.setBeginTimeInMillis(termStartTime);

        showIntentCourse();
    }

    private void promptImportMessage() {
        DialogInterface.OnClickListener importListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case AlertDialog.BUTTON_POSITIVE:
                        importCourses();
                        break;
                    case AlertDialog.BUTTON_NEGATIVE:
                        finish();
                        break;
                }
            }
        };
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_activity_course)
                .setMessage("您的课表为空，是否立即从教务系统导入？")
                .setPositiveButton("立即导入", importListener)
                .setNegativeButton("以后再说", importListener)
                .show();
    }

    @Override
    protected int layoutRes() {
        return R.layout.activity_course;
    }

    @Override
    protected void setupActionBar() {
        super.setupActionBar();
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.navi_course_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_import:
                importCourses();
                return true;
            case R.id.item_clear:
                new AlertDialog.Builder(this)
                        .setTitle("清空课表")
                        .setMessage("确认删除所有课程？")
                        .setPositiveButton("确认删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CourseManager.getInstance(CourseActivity.this).clear();
                                refresh();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                setResult(MainActivity.RESULT_COURSE_REFRESH);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void importCourses() {
        setResult(MainActivity.RESULT_COURSE_REFRESH);
        AsyncTaskCompat.executeParallel(new CourseTask(this));
    }

    public void refresh() {
        termStartTime = Prefs.getTermStartTime(this);
        List<Course> mainList = CourseManager.getInstance(this).getCourses();
        dayFragment.setList(mainList);
        weekFragment.setList(mainList);
        dayFragment.setStartTime(termStartTime);
        weekFragment.setBeginTimeInMillis(termStartTime);
        showCurrentCourse();
    }

    private void setTodayText() {
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        int week = (int) Math.floor((now - termStartTime) / 604800000d) + 1;
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
        dayFragment.setCurrentDay(day_of_week > 1 ? day_of_week - 2 : 6);
        String string = dateFormat.format(new Date());
        todayTextView.setText(getString(R.string.text_course_today, string, week));
    }

    private void showIntentCourse() {
        setTodayText();
        long intentTime = getIntent().getLongExtra("time", System.currentTimeMillis());
        currentDay = (int) ((intentTime - termStartTime) / 86400000L);
        if (currentDay <= 0) {
            currentDay = 1;
            updatePosition();
            currentDay = 0;
        }
        updatePosition();
    }

    private void showCurrentCourse() {
        setTodayText();
        showCourse(System.currentTimeMillis());
    }

    private void showCourse(Calendar calendar) {
        showCourse(calendar.getTimeInMillis());
    }

    private void showCourse(long timeInMillis) {
        currentDay = (int) ((timeInMillis - termStartTime) / 86400000);
        updatePosition();
    }

    private void updatePosition() {
        if (currentDay < 0) {
            currentDay = 0;
        } else if (currentDay >= Constants.MAX_WEEK_COUNT * 7) {
            currentDay = Constants.MAX_WEEK_COUNT * 7 - 1;
        }
        dayFragment.setPosition(currentDay);
    }

    public void jump_to_today(View view) {
        showCurrentCourse();
    }

    public void week_before(View view) {
        currentDay -= 7;
        updatePosition();
    }

    public void week_after(View view) {
        currentDay += 7;
        updatePosition();
    }

    public void pickDate(View view) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(termStartTime + currentDay * 86400000L);
        new DatePickerDialogFix(this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void relogin() {
        changeAccount(AccountActivity.REQUEST_JWC);
    }

    @Override
    public void onDayChange(int position) {
        this.currentDay = position;
        int week = (int) (Math.floor(position / 7d) + 1);
        if (week != currentWeek) {
            currentWeek = week;
            weekFragment.setWeek(currentWeek);
            dayFragment.setWeek(currentWeek);

            pickWeekButton.setText(getString(R.string.button_course_pick_week, currentWeek));
        }
    }

    @Override
    public void setWeek(int week) {
        currentDay = --week * 7 + currentDay % 7;
        updatePosition();
    }

    @Override
    public void onDayPressed(int day) {
        currentDay += day - currentDay % 7;
        dayFragment.setPosition(currentDay);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        showCourse(calendar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                AsyncTaskCompat.executeParallel(new CourseTask(this));
            }
        }
    }

    @Override
    public void showCourseList(List<Course> courses, int day, int section) {
        String title = getResources().getStringArray(R.array.days_of_week)[day] +
                getResources().getStringArray(R.array.sections)[section];
        String subTitle = getResources().getStringArray(R.array.section_start_end)[section];
        CourseListFragment.getInstance(courses, title, subTitle)
                .show(getSupportFragmentManager(), "courseList");
    }

    public void pickWeek(View view) {
        if (pickWeekFragment == null) {
            pickWeekFragment = new PickWeekFragment();
        }
        pickWeekFragment.setChosenWeek(currentWeek);
        pickWeekFragment.show(getSupportFragmentManager(), "pickWeek");
    }
}
