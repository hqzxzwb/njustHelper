package com.njust.helper.course

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.njust.helper.BuildConfig
import com.njust.helper.R
import com.njust.helper.RemoteConfig
import com.njust.helper.account.AccountActivity
import com.njust.helper.api.jwc.CourseData
import com.njust.helper.api.jwc.JwcApi
import com.njust.helper.course.data.CourseDatabase
import com.njust.helper.course.list.CourseListFragment
import com.njust.helper.main.MainActivity
import com.njust.helper.model.Course
import com.njust.helper.shared.api.LoginErrorException
import com.njust.helper.shared.api.ParseErrorException
import com.njust.helper.shared.api.ServerErrorException
import com.njust.helper.tools.Constants
import com.njust.helper.tools.Prefs
import com.njust.helper.tools.TimeUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.IOException
import java.util.*

class CourseActivity :
  AppCompatActivity(),
  DatePickerFragment.Listener,
  PickWeekFragment.Listener,
  KoinComponent {
  private val termStartTime: Long = RemoteConfig.getTermStartTime()
  private val vm = CourseScreenViewModel(
    onClickCourse = this::showCourseList,
    onClickHome = this::finish,
    onClickSelectingWeek = this::pickWeek,
    onClickSelectingDate = this::pickDate,
    onClickImporting = this::importCourses,
    onClickClearing = this::clearCourses,
  )
  private val courseDatabase: CourseDatabase by inject()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      CourseScreen(vm = vm)
    }

    refresh(promptIfEmpty = true)
    vm.termStartTime = termStartTime

    showIntentCourse()
  }

  private fun promptImportMessage() {
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

  private fun clearCourses() {
    AlertDialog.Builder(this)
      .setTitle("清空课表")
      .setMessage("确认删除所有课程？")
      .setPositiveButton("确认删除") { _, _ ->
        courseDatabase.clear()
        refresh()
      }
      .setNegativeButton(android.R.string.cancel, null)
      .show()
    setResult(MainActivity.RESULT_COURSE_REFRESH)
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
    lifecycleScope.launch {
      try {
        val data = JwcApi.courses(Prefs.getId(this@CourseActivity), Prefs.getJwcPwd(this@CourseActivity))
        onImportSuccess(data)
        progressState(false)
      } catch (e: Exception) {
        onImportError(e)
        progressState(false)
      }
    }
  }

  private suspend fun onImportSuccess(courseData: CourseData) {
    val database = courseDatabase
    database.clear()
    if (courseData.infos.size > 0) {
      database.add(courseData.infos, courseData.locs)
      vm.showSnackbar(getString(R.string.message_course_import_success))
    } else {
      vm.showSnackbar("您的课表似乎是空的，过几天再来试试吧~")
    }
    refresh()
  }

  private suspend fun onImportError(throwable: Throwable) {
    when (throwable) {
      is ServerErrorException -> vm.showSnackbar(getString(R.string.message_server_error))
      is LoginErrorException -> relogin()
      is IOException -> vm.showSnackbar(getString(R.string.message_net_error))
      is ParseErrorException -> vm.showSnackbar(getString(R.string.message_parse_error))
      else -> {
        if (BuildConfig.DEBUG) {
          throwable.printStackTrace()
          throw throwable
        }
      }
    }
  }

  private fun refresh(promptIfEmpty: Boolean = false) {
    lifecycleScope.launch {
      val mainList = withContext(Dispatchers.IO) {
        courseDatabase.getCourses()
      }
      if (promptIfEmpty && mainList.isEmpty()) {
        promptImportMessage()
      }
      val lists = Array(7) {
        Array(Constants.COURSE_SECTION_COUNT) { mutableListOf<Course>() }
      }
      for (i in 0 until 7) {
        for (j in 0 until Constants.COURSE_SECTION_COUNT) {
          lists[i][j].clear()
        }
      }
      for (course in mainList) {
        lists[course.day][course.sec1].add(course)
      }
      vm.courses = lists
      vm.scrollToToday()
    }
  }

  private fun showIntentCourse() {
    val intentTime = intent.getLongExtra("time", System.currentTimeMillis())
    showCourse(intentTime)
  }

  private fun showCourse(timeInMillis: Long) {
    val currentDay = ((timeInMillis - termStartTime) / TimeUtil.ONE_DAY).toInt()
      .coerceIn(0, Constants.MAX_WEEK_COUNT * 7 - 1)
    lifecycleScope.launch {
      vm.scrollTo(currentDay, false)
    }
  }

  private fun pickDate() {
    val currentDay = vm.dayOfTermFlow.value
    DatePickerFragment.newInstance(termStartTime + currentDay * TimeUtil.ONE_DAY)
      .showNow(supportFragmentManager, null)
  }

  private fun relogin() {
    AccountActivity.alertPasswordError(this, AccountActivity.REQUEST_JWC)
  }

  override fun setWeek(week: Int) {
    val currentDay = (week - 1) * 7 + vm.dayOfTermFlow.value % 7
    lifecycleScope.launch {
      vm.scrollTo(currentDay, false)
    }
  }

  override fun onDateSet(year: Int, month: Int, dayOfMonth: Int) {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, dayOfMonth)
    showCourse(calendar.timeInMillis)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 0) {
      if (resultCode == RESULT_OK) {
        doImport()
      }
    }
  }

  private fun showCourseList(courses: List<Course>, day: Int, section: Int) {
    val title = resources.getStringArray(R.array.days_of_week)[day] + resources.getStringArray(R.array.sections)[section]
    val subtitle = resources.getStringArray(R.array.section_start_end)[section]
    CourseListFragment.newInstance(courses, title, subtitle)
      .showNow(supportFragmentManager, "courseList")
  }

  private fun pickWeek() {
    val currentWeek = vm.dayOfTermFlow.value / 7 + 1
    PickWeekFragment.newInstance(currentWeek).showNow(supportFragmentManager, "pickWeek")
  }
}
