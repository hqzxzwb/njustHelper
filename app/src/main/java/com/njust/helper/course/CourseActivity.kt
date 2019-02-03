package com.njust.helper.course

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.njust.helper.BuildConfig
import com.njust.helper.R
import com.njust.helper.account.AccountActivity
import com.njust.helper.activity.BaseActivity
import com.njust.helper.api.LoginErrorException
import com.njust.helper.api.ParseErrorException
import com.njust.helper.api.ServerErrorException
import com.njust.helper.api.jwc.CourseData
import com.njust.helper.api.jwc.JwcApi
import com.njust.helper.course.data.CourseManager
import com.njust.helper.course.day.CourseDayFragment
import com.njust.helper.course.list.CourseListFragment
import com.njust.helper.course.week.CourseWeekFragment
import com.njust.helper.databinding.ActivityCourseBinding
import com.njust.helper.main.MainActivity
import com.njust.helper.model.Course
import com.njust.helper.tools.Constants
import com.njust.helper.tools.Prefs
import com.njust.helper.tools.TimeUtil
import com.tencent.bugly.crashreport.CrashReport
import com.zwb.commonlibs.rx.ioSubscribeUiObserve
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CourseActivity :
        BaseActivity(),
        DatePickerFragment.Listener,
        CourseDayFragment.Listener,
        PickWeekFragment.Listener,
        CourseWeekFragment.Listener,
        CourseActivityClickHandler {
    private var termStartTime: Long = 0
    private var currentDay: Int = 0
    private var currentWeek: Int = 0
    private lateinit var dateFormat: SimpleDateFormat
    private lateinit var dayFragment: CourseDayFragment
    private lateinit var weekFragment: CourseWeekFragment
    private val vm: CourseActivityVm = CourseActivityVm()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dateFormat = SimpleDateFormat(getString(R.string.date_course_today), Locale.CHINA)

        val group = supportActionBar!!.customView as RadioGroup
        (group.findViewById<View>(R.id.radio0) as RadioButton).isChecked = true
        group.setOnCheckedChangeListener { _, checkedId ->
            vm.dayView = checkedId == R.id.radio0
        }

        val manager = supportFragmentManager

        dayFragment = manager.findFragmentById(R.id.course_day_fragment) as CourseDayFragment
        weekFragment = manager.findFragmentById(R.id.course_week_fragment) as CourseWeekFragment

        Single
                .fromCallable { CourseManager.getInstance(this).courses }
                .ioSubscribeUiObserve()
                .subscribe { mainList ->
                    if (mainList.size == 0) {
                        //课表为空时，提示导入课表
                        promptImportMessage()
                    }
                    dayFragment.setList(mainList)
                    weekFragment.setList(mainList)
                }
                .addToLifecycleManagement()

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

    override fun layoutRes(): Int = 0

    override fun layout() {
        val binding = DataBindingUtil.setContentView<ActivityCourseBinding>(this, R.layout.activity_course)
        vm.clickHandler = this
        binding.vm = vm
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

    private var mProgressDialog: ProgressDialog? = null

    private fun progressState(b: Boolean) {
        if (b) {
            mProgressDialog = ProgressDialog.show(this, "请稍候...", "正在导入课表")
        } else {
            mProgressDialog?.dismiss()
            mProgressDialog = null
        }
    }

    private fun importCourses() {
        setResult(MainActivity.RESULT_COURSE_REFRESH)
        doImport()
    }

    private fun doImport() {
        progressState(true)
        JwcApi.courses(Prefs.getId(this), Prefs.getJwcPwd(this))
                .subscribeBy(
                        onSuccess = {
                            onImportSuccess(it)
                            progressState(false)
                        },
                        onError = {
                            onImportError(it)
                            progressState(false)
                        }
                )
                .addToLifecycleManagement()
    }

    private fun onImportSuccess(courseData: CourseData) {
        val dao = CourseManager.getInstance(this)
        dao.clear()
        if (courseData.infos.size > 0) {
            dao.add(courseData.infos, courseData.locs)
            showSnack(R.string.message_course_import_success)
        } else {
            showSnack("您的课表似乎是空的，过几天再来试试吧~")
        }
        refresh()
    }

    private fun onImportError(throwable: Throwable) {
        when (throwable) {
            is ServerErrorException -> showSnack(R.string.message_server_error)
            is LoginErrorException -> relogin()
            is IOException -> showSnack(R.string.message_net_error)
            is ParseErrorException -> showSnack(R.string.message_parse_error)
            else -> {
                if (BuildConfig.DEBUG) {
                    throwable.printStackTrace()
                    throw throwable
                }
                CrashReport.postCatchedException(throwable)
            }
        }
    }

    private fun refresh() {
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
        val timeDiff = now - termStartTime
        val week = if (timeDiff >= 0) {
            timeDiff / TimeUtil.ONE_WEEK + 1
        } else {
            timeDiff / TimeUtil.ONE_WEEK
        }
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        dayFragment.setCurrentDay(if (dayOfWeek > 1) dayOfWeek - 2 else 6)
        val string = dateFormat.format(Date())
        vm.bottomText = getString(R.string.text_course_today, string, week)
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

    override fun toToday() {
        showCurrentCourse()
    }

    override fun weekBefore() {
        currentDay -= 7
        updatePosition()
    }

    override fun weekAfter() {
        currentDay += 7
        updatePosition()
    }

    override fun pickDate() {
        DatePickerFragment.newInstance(termStartTime + currentDay * TimeUtil.ONE_DAY)
                .showNow(supportFragmentManager, null)
    }

    private fun relogin() {
        AccountActivity.alertPasswordError(this, AccountActivity.REQUEST_JWC)
    }

    override fun onDayChange(position: Int) {
        this.currentDay = position
        val week = (Math.floor(position / 7.0) + 1).toInt()
        if (week != currentWeek) {
            currentWeek = week
            weekFragment.setWeek(currentWeek)
            dayFragment.setWeek(currentWeek)

            vm.displayingWeek = currentWeek
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

    override fun onDateSet(year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        showCourse(calendar)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                doImport()
            }
        }
    }

    override fun showCourseList(courses: List<Course>, day: Int, section: Int) {
        val title = resources.getStringArray(R.array.days_of_week)[day] + resources.getStringArray(R.array.sections)[section]
        val subTitle = resources.getStringArray(R.array.section_start_end)[section]
        CourseListFragment.newInstance(courses, title, subTitle)
                .showNow(supportFragmentManager, "courseList")
    }

    override fun pickWeek() {
        PickWeekFragment.newInstance(currentWeek).showNow(supportFragmentManager, "pickWeek")
    }
}
