package com.njust.helper.course

import android.app.DatePickerDialog.OnDateSetListener
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import butterknife.BindView
import com.njust.helper.R
import com.njust.helper.account.AccountActivity
import com.njust.helper.activity.BaseActivity
import com.njust.helper.course.data.CourseManager
import com.njust.helper.course.day.CourseDayFragment
import com.njust.helper.course.list.CourseListFragment
import com.njust.helper.course.week.CourseWeekFragment
import com.njust.helper.main.MainActivity
import com.njust.helper.model.Course
import com.njust.helper.tools.Constants
import com.njust.helper.tools.Prefs
import com.njust.helper.tools.TimeUtil
import com.zwb.commonlibs.ui.DatePickerDialogFix
import java.text.SimpleDateFormat
import java.util.*

class CourseActivity : BaseActivity(), OnDateSetListener, CourseDayFragment.Listener, PickWeekFragment.Listener, CourseWeekFragment.Listener {
    private var termStartTime: Long = 0
    private var currentDay: Int = 0
    private var currentWeek: Int = 0
    private var dateFormat: SimpleDateFormat? = null
    private lateinit var dayFragment: CourseDayFragment
    private lateinit var weekFragment: CourseWeekFragment
    @BindView(R.id.txtToday)
    lateinit var todayTextView: TextView
    @BindView(R.id.tvPickWeek)
    lateinit var pickWeekButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dateFormat = SimpleDateFormat(getString(R.string.date_course_today), Locale.CHINA)

        val weekView = findViewById<View>(R.id.course_week_fragment)

        weekView.visibility = View.GONE
        val dayView = findViewById<View>(R.id.course_day_fragment)

        val group = supportActionBar!!.customView as RadioGroup
        (group.findViewById<View>(R.id.radio0) as RadioButton).isChecked = true
        group.setOnCheckedChangeListener { group1, checkedId ->
            if (checkedId == R.id.radio0) {
                dayView.visibility = View.VISIBLE
                weekView.visibility = View.GONE
            } else {
                dayView.visibility = View.GONE
                weekView.visibility = View.VISIBLE
            }
        }

        val manager = supportFragmentManager

        dayFragment = manager.findFragmentById(R.id.course_day_fragment) as CourseDayFragment
        weekFragment = manager.findFragmentById(R.id.course_week_fragment) as CourseWeekFragment

        object : Thread() {
            override fun run() {
                val mainList = CourseManager.getInstance(this@CourseActivity).courses
                runOnUiThread {
                    if (mainList.size == 0) {
                        //课表为空时，提示导入课表
                        promptImportMessage()
                    }
                    dayFragment.setList(mainList)
                    weekFragment.setList(mainList)
                }
            }
        }.start()

        termStartTime = Prefs.getTermStartTime(this)

        dayFragment.setStartTime(termStartTime)
        weekFragment.setBeginTimeInMillis(termStartTime)

        showIntentCourse()
    }

    internal fun promptImportMessage() {
        val importListener = { _: DialogInterface, which: Int ->
            when (which) {
                AlertDialog.BUTTON_POSITIVE -> importCourses()
                AlertDialog.BUTTON_NEGATIVE -> finish()
            }
        }
        AlertDialog.Builder(this)
                .setTitle(R.string.title_activity_course)
                .setMessage("您的课表为空，是否立即从教务系统导入？")
                .setPositiveButton("立即导入", importListener)
                .setNegativeButton("以后再说", importListener)
                .show()
    }

    override fun layoutRes(): Int {
        return R.layout.activity_course
    }

    override fun setupActionBar() {
        super.setupActionBar()

        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setCustomView(R.layout.navi_course_view)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.course, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_import -> {
                importCourses()
                return true
            }
            R.id.item_clear -> {
                AlertDialog.Builder(this)
                        .setTitle("清空课表")
                        .setMessage("确认删除所有课程？")
                        .setPositiveButton("确认删除") { _, _ ->
                            CourseManager.getInstance(this@CourseActivity).clear()
                            refresh()
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
                setResult(MainActivity.RESULT_COURSE_REFRESH)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun importCourses() {
        setResult(MainActivity.RESULT_COURSE_REFRESH)
        CourseTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    fun refresh() {
        termStartTime = Prefs.getTermStartTime(this)
        val mainList = CourseManager.getInstance(this).courses
        dayFragment.setList(mainList)
        weekFragment.setList(mainList)
        dayFragment.setStartTime(termStartTime)
        weekFragment.setBeginTimeInMillis(termStartTime)
        showCurrentCourse()
    }

    private fun setTodayText() {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis
        val week = Math.floor(((now - termStartTime) / TimeUtil.ONE_WEEK.toFloat()).toDouble()).toInt() + 1
        val day_of_week = calendar.get(Calendar.DAY_OF_WEEK)
        dayFragment.setCurrentDay(if (day_of_week > 1) day_of_week - 2 else 6)
        val string = dateFormat!!.format(Date())
        todayTextView.text = getString(R.string.text_course_today, string, week)
    }

    private fun showIntentCourse() {
        setTodayText()
        val intentTime = intent.getLongExtra("time", System.currentTimeMillis())
        currentDay = ((intentTime - termStartTime) / TimeUtil.ONE_DAY).toInt()
        if (currentDay <= 0) {
            currentDay = 1
            updatePosition()
            currentDay = 0
        }
        updatePosition()
    }

    private fun showCurrentCourse() {
        setTodayText()
        showCourse(System.currentTimeMillis())
    }

    private fun showCourse(calendar: Calendar) {
        showCourse(calendar.timeInMillis)
    }

    private fun showCourse(timeInMillis: Long) {
        currentDay = ((timeInMillis - termStartTime) / TimeUtil.ONE_DAY).toInt()
        updatePosition()
    }

    private fun updatePosition() {
        if (currentDay < 0) {
            currentDay = 0
        } else if (currentDay >= Constants.MAX_WEEK_COUNT * 7) {
            currentDay = Constants.MAX_WEEK_COUNT * 7 - 1
        }
        dayFragment.setPosition(currentDay)
    }

    fun jump_to_today(view: View) {
        showCurrentCourse()
    }

    fun week_before(view: View) {
        currentDay -= 7
        updatePosition()
    }

    fun week_after(view: View) {
        currentDay += 7
        updatePosition()
    }

    fun pickDate(view: View) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = termStartTime + currentDay * TimeUtil.ONE_DAY
        DatePickerDialogFix(this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    fun relogin() {
        changeAccount(AccountActivity.REQUEST_JWC)
    }

    override fun onDayChange(position: Int) {
        this.currentDay = position
        val week = (Math.floor(position / 7.0) + 1).toInt()
        if (week != currentWeek) {
            currentWeek = week
            weekFragment.setWeek(currentWeek)
            dayFragment.setWeek(currentWeek)

            pickWeekButton.text = getString(R.string.button_course_pick_week, currentWeek)
        }
    }

    override fun setWeek(week: Int) {
        currentDay = (week - 1) * 7 + currentDay % 7
        updatePosition()
    }

    override fun onDayPressed(day: Int) {
        currentDay += day - currentDay % 7
        dayFragment.setPosition(currentDay)
    }

    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, monthOfYear, dayOfMonth)
        showCourse(calendar)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                CourseTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            }
        }
    }

    override fun showCourseList(courses: List<Course>, day: Int, section: Int) {
        val title = resources.getStringArray(R.array.days_of_week)[day] + resources.getStringArray(R.array.sections)[section]
        val subTitle = resources.getStringArray(R.array.section_start_end)[section]
        CourseListFragment.newInstance(courses, title, subTitle)
                .show(supportFragmentManager, "courseList")
    }

    fun pickWeek(view: View) {
        PickWeekFragment.newInstance(currentWeek).show(supportFragmentManager, "pickWeek")
    }
}
