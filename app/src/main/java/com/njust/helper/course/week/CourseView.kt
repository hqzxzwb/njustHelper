package com.njust.helper.course.week

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.njust.helper.R
import com.njust.helper.model.Course
import java.util.*

typealias OnSelectCourseListener = (courses: List<Course>, day: Int, section: Int) -> Unit

/**
 * Created by zwb on 2015/4/7.
 * 课程表View
 */
class CourseView : View {
    private lateinit var mData: Array<Array<MutableList<Course>>>
    private val mLayoutContainer = HashMap<String, Layout>()
    private var mWeek: Int = 0
    private val mTextPaint = TextPaint()
    private val mDarkTextPaint = TextPaint()
    private val mLightPaint = Paint()
    private val mDarkPaint = Paint()
    private var path: Path = Path()
    private var mLeftColumnSize: Int = 0
    private var mSectionCount: Int = 0
    private var unitHeight: Int = 0
    private var unitWidth: Int = 0

    private var mListener: OnSelectCourseListener? = null

    private var downX: Int = 0
    private var downY: Int = 0

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CourseView, defStyleAttr, 0)
        mSectionCount = typedArray.getInt(R.styleable.CourseView_courseNum, 5)

        mData = Array(7) {
            Array(mSectionCount) {
                mutableListOf<Course>()
            }
        }
        mLeftColumnSize = typedArray.getDimensionPixelSize(R.styleable.CourseView_numColumnSize, resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin))
        typedArray.recycle()

        mLightPaint.color = -0x1f1f20
        mDarkPaint.color = -0x7f7f80
        val textSize = resources.getDimensionPixelSize(R.dimen.course_view_text_size)
        mTextPaint.textSize = textSize.toFloat()
        mTextPaint.color = Color.GRAY
        mTextPaint.isAntiAlias = true
        mDarkTextPaint.textSize = textSize.toFloat()
        mDarkTextPaint.color = Color.BLACK
        mDarkTextPaint.isFakeBoldText = true
        mDarkTextPaint.isAntiAlias = true
    }

    fun setCourses(inCourses: List<Course>?) {
        var courses = inCourses
        if (courses == null) {
            courses = emptyList()
        }
        mData.forEach { lists ->
            lists.forEach { list -> list.clear() }
        }
        for (course in courses) {
            var list: MutableList<Course>? = mData[course.day][course.sec1]
            if (list == null) {
                list = ArrayList()
                mData[course.day][course.sec1] = list
            }
            list.add(course)
        }
        mLayoutContainer.clear()
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = (event.x.toInt() - mLeftColumnSize) / unitWidth
                downY = event.y.toInt() / unitHeight
                return true
            }
            MotionEvent.ACTION_UP -> {
                var x = (event.x.toInt() - mLeftColumnSize) / unitWidth
                var y = event.y.toInt() / unitHeight
                if (x == downX && y == downY) {
                    x = Math.min(x, COLUMN_COUNT)
                    y = Math.min(y, mSectionCount)
                    mListener?.invoke(mData[x][y], x, y)
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.UNSPECIFIED) {
            setMeasuredDimension(View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
                    Math.max(suggestedMinimumHeight, View.MeasureSpec.getSize(heightMeasureSpec)))
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
        unitWidth = (measuredWidth - mLeftColumnSize) / 7
        unitHeight = measuredHeight / mSectionCount
        val delta2 = unitWidth / 15
        val side = unitWidth / 4
        path.moveTo((unitWidth - delta2).toFloat(), (unitHeight - delta2 - side).toFloat())
        path.lineTo((unitWidth - delta2).toFloat(), (unitHeight - delta2).toFloat())
        path.lineTo((unitWidth - delta2 - side).toFloat(), (unitHeight - delta2).toFloat())
        path.close()
    }

    fun setWeek(week: Int) {
        mWeek = week
        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var delta = (unitWidth / 20).toFloat()
        run {
            var i = unitHeight.toFloat()
            while (i < height + unitHeight) {
                var j = 0f
                while (j < width - mLeftColumnSize) {
                    canvas.drawLine(mLeftColumnSize + j - delta, i, mLeftColumnSize.toFloat() + j + delta, i, mTextPaint)
                    canvas.drawLine(mLeftColumnSize + j, i - delta, mLeftColumnSize + j, i + delta, mTextPaint)
                    j += unitWidth.toFloat()
                }
                canvas.drawText(Integer.toString((i / unitHeight).toInt()),
                        delta * 3, i - unitHeight / 2, mTextPaint)
                i += unitHeight.toFloat()
            }
        }

        delta = (unitWidth / 30).toFloat()
        val left = mLeftColumnSize + delta
        val top = delta
        canvas.translate(left, top)
        val width = unitWidth - 2 * delta
        val height = unitHeight - 2 * delta
        for (i in 0 until COLUMN_COUNT) {
            for (j in 0 until mSectionCount) {
                val list = mData[i][j]
                var course: Course? = null
                if (list.size == 0) {
                    canvas.translate(0f, unitHeight.toFloat())
                    continue
                }
                canvas.drawRect(0f, 0f, width, height, mLightPaint)
                if (list.size == 1) {
                    course = list[0]
                } else {
                    canvas.drawPath(path, mDarkPaint)
                    for (t in list) {
                        course = t
                        if (t.week2.contains(" $mWeek ")) {
                            break
                        }
                    }
                }

                var name = course!!.name
                if (name.length > 8) {
                    name = name.substring(0, 7) + "..."
                }
                val paint: TextPaint
                if (!course.week2.contains(" $mWeek ")) {
                    name = "[非本周]" + name
                    paint = mTextPaint
                } else {
                    paint = mDarkTextPaint
                }
                name = name + "@" + course.classroom
                var layout: Layout? = mLayoutContainer[name]
                if (layout == null) {
                    layout = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        StaticLayout(name, paint, (unitWidth - 2 * delta).toInt(),
                                Layout.Alignment.ALIGN_CENTER, 1f, 0f,
                                false)
                    } else {
                        StaticLayout.Builder.obtain(name, 0, name.length, paint,
                                (unitWidth - 2 * delta).toInt()).build()
                    }
                    mLayoutContainer.put(name, layout!!)
                }
                layout.draw(canvas)
                canvas.translate(0f, unitHeight.toFloat())
            }
            canvas.translate(unitWidth.toFloat(), (-unitHeight * mSectionCount).toFloat())
        }
    }

    fun setListener(listener: OnSelectCourseListener) {
        mListener = listener
    }

    companion object {
        private const val COLUMN_COUNT = 7
    }
}
