package com.njust.helper.course.day

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.njust.helper.R
import com.njust.helper.model.Course
import com.njust.helper.tools.Constants
import com.njust.helper.tools.TimeUtil
import kotlinx.android.synthetic.main.fgmt_course_day.*
import java.text.SimpleDateFormat
import java.util.*

class CourseDayFragment : Fragment() {
  private val mLists = Array(7) {
    Array(Constants.COURSE_SECTION_COUNT) { mutableListOf<Course>() }
  }
  private val dateMonthFormat = SimpleDateFormat("MMM", Locale.CHINA)
  private val dateDayFormat = SimpleDateFormat("d", Locale.CHINA)
  private lateinit var dayOfWeek: Array<String>

  private lateinit var mTextViews: Array<TextView>

  private var beginTimeInMillis: Long = 0
  private lateinit var listener: Listener

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
      override fun onPageSelected(position: Int) {
        for (i in 0 until 7) {
          mTextViews[i].setBackgroundColor(Color.TRANSPARENT)
        }
        mTextViews[position % 7].setBackgroundColor(Color.GRAY)
        listener.onDayChange(position)
      }
    })

    mTextViews = arrayOf(
        view.findViewById(R.id.dayOfWeek0),
        view.findViewById(R.id.dayOfWeek1),
        view.findViewById(R.id.dayOfWeek2),
        view.findViewById(R.id.dayOfWeek3),
        view.findViewById(R.id.dayOfWeek4),
        view.findViewById(R.id.dayOfWeek5),
        view.findViewById(R.id.dayOfWeek6)
    )

    for (i in 0 until 7) {
      mTextViews[i].setOnClickListener { listener.onDayPressed(i) }
    }
    mTextViews[0].setBackgroundColor(Color.GRAY)

    val adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
      override fun getItemCount(): Int {
        return Constants.MAX_WEEK_COUNT * 7
      }

      override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val recyclerView = holder.itemView as RecyclerView
        val adapter = CourseDayAdapter(this@CourseDayFragment)
        val dayOfWeek = position % 7
        adapter.setData(mLists[dayOfWeek], position / 7 + 1, dayOfWeek)
        recyclerView.adapter = adapter
        view.tag = position
      }

      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val recyclerView = activity!!.layoutInflater.inflate(
            R.layout.pager_course_day, parent, false) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        return object : RecyclerView.ViewHolder(recyclerView) {}
      }
    }
    viewPager.adapter = adapter
    super.onViewCreated(view, savedInstanceState)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fgmt_course_day, container, false)
  }

  override fun onAttach(context: Context) {
    listener = context as Listener
    dayOfWeek = resources.getStringArray(R.array.days_of_week_short)
    super.onAttach(context)
  }

  fun setList(courses: List<Course>) {
    for (i in 0 until 7) {
      for (j in 0 until Constants.COURSE_SECTION_COUNT) {
        mLists[i][j].clear()
      }
    }
    for (course in courses) {
      mLists[course.day][course.sec1].add(course)
    }
    val adapter = viewPager.adapter!!
    adapter.notifyDataSetChanged()
  }

  fun setPosition(position: Int, smoothScroll: Boolean) {
    viewPager.setCurrentItem(position, smoothScroll)
  }

  fun setCurrentDay(currentDay: Int) {
    mTextViews[currentDay].setTextColor(Color.MAGENTA)
  }

  @SuppressLint("SetTextI18n")
  fun setWeek(week: Int) {
    var time = beginTimeInMillis + (week - 1) * TimeUtil.ONE_WEEK
    val date = Date(time)
    monthTextView.text = dateMonthFormat.format(date)
    for (i in 0 until 7) {
      val string = dateDayFormat.format(date)
      if (i > 0 && string == "1") {
        mTextViews[i].text = dateMonthFormat.format(date) + "\n" + dayOfWeek[i]
      } else {
        mTextViews[i].text = string + "\n" + dayOfWeek[i]
      }
      time += TimeUtil.ONE_DAY
      date.time = time
    }
  }

  fun setStartTime(time: Long) {
    beginTimeInMillis = time
  }

  fun showCourseList(courses: List<Course>, day: Int, section: Int) {
    listener.showCourseList(courses, day, section)
  }

  interface Listener {
    fun onDayChange(position: Int)

    fun onDayPressed(day: Int)

    fun showCourseList(courses: List<Course>, day: Int, section: Int)
  }
}
