package com.njust.helper.course.week;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.njust.helper.R;
import com.njust.helper.model.Course;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zwb on 2015/4/7.
 * 课程表View
 */
public class CourseView extends View {
    private List<Course>[][] mData;
    private Map<String, Layout> mLayoutContainer = new HashMap<>();
    private int mWeek;
    private TextPaint mTextPaint = new TextPaint();
    private TextPaint mDarkTextPaint = new TextPaint();
    private Paint mLightPaint = new Paint();
    private Paint mDarkPaint = new Paint();
    private Path path;
    private int mLeftColumnSize;
    private int mHeight;
    private int unitHeight, unitWidth;

    private OnSelectCourseListener mListener;

    private int downX, downY;

    public CourseView(Context context) {
        super(context);
        init(null, 0);
    }

    public CourseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CourseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CourseView, defStyleAttr, 0);
        mHeight = typedArray.getInt(R.styleable.CourseView_courseNum, 5);
        //noinspection unchecked
        mData = new List[7][mHeight];
        mLeftColumnSize = typedArray.getDimensionPixelSize(R.styleable.CourseView_numColumnSize, getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin));
        typedArray.recycle();

        //noinspection deprecation
        mLightPaint.setColor(0xFFe0e0e0);
        mDarkPaint.setColor(0xFF808080);
        int textSize = getResources().getDimensionPixelSize(R.dimen.course_view_text_size);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(Color.GRAY);
        mTextPaint.setAntiAlias(true);
        mDarkTextPaint.setTextSize(textSize);
        mDarkTextPaint.setColor(Color.BLACK);
        mDarkTextPaint.setFakeBoldText(true);
        mDarkTextPaint.setAntiAlias(true);

        path = new Path();
    }

    public void setCourses(List<Course> courses) {
        if (courses == null) {
            courses = Collections.emptyList();
        }
        for (List[] lists : mData) {
            if (lists != null) {
                for (List list : lists) {
                    if (list != null) {
                        list.clear();
                    }
                }
            }
        }
        for (Course course : courses) {
            List<Course> list = mData[course.getDay()][course.getSec1()];
            if (list == null) {
                list = new ArrayList<>();
                mData[course.getDay()][course.getSec1()] = list;
            }
            list.add(course);
        }
        mLayoutContainer.clear();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ((int) event.getX() - mLeftColumnSize) / unitWidth;
                downY = (int) event.getY() / unitHeight;
                return true;
            case MotionEvent.ACTION_UP:
                int x = ((int) event.getX() - mLeftColumnSize) / unitWidth;
                int y = (int) event.getY() / unitHeight;
                if (x == downX && y == downY && mListener != null) {
                    List<Course> list = mData[x][y];
                    if (list != null) {
                        mListener.onSelectCourse(mData[x][y], x, y);
                    }
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                    Math.max(getSuggestedMinimumHeight(), MeasureSpec.getSize(heightMeasureSpec)));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        unitWidth = (getMeasuredWidth() - mLeftColumnSize) / 7;
        unitHeight = getMeasuredHeight() / mHeight;
        int delta2 = unitWidth / 15;
        int side = unitWidth / 4;
        path.moveTo(unitWidth - delta2, unitHeight - delta2 - side);
        path.lineTo(unitWidth - delta2, unitHeight - delta2);
        path.lineTo(unitWidth - delta2 - side, unitHeight - delta2);
        path.close();
    }

    public void setWeek(int week) {
        mWeek = week;
        invalidate();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        int unitWidth = (getWidth() - mLeftColumnSize) / 7;
//        int unitHeight = getHeight() / mHeight;
        float delta = unitWidth / 20;
        for (float i = unitHeight; i < getHeight() + unitHeight; i += unitHeight) {
            for (float j = 0; j < getWidth() - mLeftColumnSize; j += unitWidth) {
                canvas.drawLine(mLeftColumnSize + j - delta, i, mLeftColumnSize + j + delta, i, mTextPaint);
                canvas.drawLine(mLeftColumnSize + j, i - delta, mLeftColumnSize + j, i + delta, mTextPaint);
            }
            canvas.drawText(Integer.toString((int) (i / unitHeight)),
                    delta * 3, i - unitHeight / 2, mTextPaint);
        }

        delta = unitWidth / 30;
        float left = mLeftColumnSize + delta;
        float top = delta;
        canvas.translate(left, top);
        float width = unitWidth - 2 * delta;
        float height = unitHeight - 2 * delta;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < mHeight; j++) {
                List<Course> list = mData[i][j];
                Course course = null;
                if (list == null || list.size() == 0) {
                    canvas.translate(0, unitHeight);
                    continue;
                }
                canvas.drawRect(0, 0, width, height, mLightPaint);
                if (list.size() == 1) {
                    course = list.get(0);
                } else {
                    canvas.drawPath(path, mDarkPaint);
                    for (Course t : list) {
                        course = t;
                        if (t.getWeek2().contains(" " + mWeek + " ")) {
                            break;
                        }
                    }
                }
                //noinspection ConstantConditions
                String name = course.getName();
                if (name.length() > 8) {
                    name = name.substring(0, 7) + "...";
                }
                TextPaint paint;
                if (!course.getWeek2().contains(" " + mWeek + " ")) {
                    name = "[非本周]" + name;
                    paint = mTextPaint;
                } else {
                    paint = mDarkTextPaint;
                }
                name = name + "@" + course.getClassroom();
                Layout layout = mLayoutContainer.get(name);
                if (layout == null) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        layout = new StaticLayout(name, paint, (int) (unitWidth - 2 * delta), Layout.Alignment.ALIGN_CENTER, 1f, 0f, false);
                    } else {
                        layout = StaticLayout.Builder.obtain(name, 0, name.length(), paint, (int) (unitWidth - 2 * delta)).build();
                    }
                    mLayoutContainer.put(name, layout);
                }
                layout.draw(canvas);
                canvas.translate(0, unitHeight);
            }
            canvas.translate(unitWidth, -unitHeight * mHeight);
        }
    }

    public void setListener(OnSelectCourseListener listener) {
        mListener = listener;
    }

    public interface OnSelectCourseListener {
        void onSelectCourse(List<Course> courses, int day, int section);
    }
}