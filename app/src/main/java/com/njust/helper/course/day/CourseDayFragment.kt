package com.njust.helper.course.day

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.njust.helper.R
import com.njust.helper.model.Course
import com.njust.helper.tools.Constants
import com.njust.helper.tools.TimeUtil
import com.zwb.commonlibs.adapter.EfficientPagerAdapter
import java.text.SimpleDateFormat
import java.util.*

class CourseDayFragment : Fragment(), OnPageChangeListener {
    internal val mLists: Array<Array<MutableList<Course>>> = Array(7) {
        Array<MutableList<Course>>(5) {
            arrayListOf()
        }
    }
    private val DATE_MONTH_FORMAT = SimpleDateFormat("MMM", Locale.CHINA)
    private val DATE_DAY_FORMAT = SimpleDateFormat("d", Locale.CHINA)
    private var dayOfWeek: Array<String>? = null

    private lateinit var mTextViews: Array<TextView>
    @BindView(R.id.textMonth)
    lateinit var mMonthView: TextView
    @BindView(R.id.viewPager)
    lateinit var mViewPager: ViewPager

    private var beginTimeInMillis: Long = 0
    var listener: Listener? = null
        private set

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ButterKnife.bind(this, view)

        mViewPager.addOnPageChangeListener(this)

        mTextViews = arrayOf(
                view.findViewById(R.id.dayOfWeek0),
                view.findViewById(R.id.dayOfWeek1),
                view.findViewById(R.id.dayOfWeek2),
                view.findViewById(R.id.dayOfWeek3),
                view.findViewById(R.id.dayOfWeek4),
                view.findViewById(R.id.dayOfWeek5),
                view.findViewById(R.id.dayOfWeek6)
        )

        for (i in 0..6) {
            mTextViews[i].setOnClickListener { listener!!.onDayPressed(i) }
        }
        mTextViews[0].setBackgroundColor(Color.GRAY)

        val adapter = object : EfficientPagerAdapter() {
            override fun getCount(): Int {
                return Constants.MAX_WEEK_COUNT * 7
            }

            override fun updateView(view: View, position: Int) {
                val recyclerView = view as RecyclerView
                val adapter = CourseDayAdapter(this@CourseDayFragment)
                val dayOfWeek = position % 7
                adapter.setData(mLists[dayOfWeek], position / 7 + 1, dayOfWeek)
                recyclerView.adapter = adapter
                view.setTag(position)
            }

            override fun onCreateNewView(container: ViewGroup): View {
                val recyclerView = activity.layoutInflater.inflate(
                        R.layout.pager_course_day, container, false) as RecyclerView
                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
                return recyclerView
            }
        }
        mViewPager.adapter = adapter
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fgmt_course_day, container, false)
    }

    override fun onAttach(context: Context?) {
        listener = context as Listener?
        dayOfWeek = resources.getStringArray(R.array.days_of_week_short)
        super.onAttach(context)
    }

    fun setList(courses: List<Course>) {
        for (i in 0..6) {
            for (j in 0..4)
                mLists[i][j].clear()
        }
        for (course in courses) {
            mLists[course.day][course.sec1].add(course)
        }
        val adapter = mViewPager.adapter
        adapter?.notifyDataSetChanged()
    }

    fun setPosition(position: Int) {
        mViewPager.currentItem = position
    }

    fun setCurrentDay(currentDay: Int) {
        mTextViews[currentDay].setTextColor(Color.MAGENTA)
    }

    override fun onPageScrollStateChanged(arg0: Int) {}

    override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}

    override fun onPageSelected(position: Int) {
        for (i in 0..6) {
            mTextViews[i].setBackgroundColor(Color.TRANSPARENT)
        }
        mTextViews[position % 7].setBackgroundColor(Color.GRAY)
        listener!!.onDayChange(position)
    }

    @SuppressLint("SetTextI18n")
    fun setWeek(week: Int) {
        var time = beginTimeInMillis + (week - 1) * TimeUtil.ONE_WEEK
        val date = Date(time)
        mMonthView.text = DATE_MONTH_FORMAT.format(date)
        for (i in 0..6) {
            val string = DATE_DAY_FORMAT.format(date)
            if (i > 0 && string == "1") {
                mTextViews[i].text = DATE_MONTH_FORMAT.format(date) + "\n" + dayOfWeek!![i]
            } else {
                mTextViews[i].text = string + "\n" + dayOfWeek!![i]
            }
            time += TimeUtil.ONE_DAY
            date.time = time
        }
    }

    fun setStartTime(time: Long) {
        beginTimeInMillis = time
    }

    interface Listener {
        fun onDayChange(position: Int)

        fun onDayPressed(day: Int)

        fun showCourseList(courses: @JvmSuppressWildcards List<Course>, day: Int, section: Int)
    }
}
