package com.njust.helper.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import com.njust.helper.BuildConfig
import com.njust.helper.links.LinksActivity
import com.njust.helper.R
import com.njust.helper.RemoteConfig
import com.njust.helper.account.AccountActivity
import com.njust.helper.activity.BaseActivity
import com.njust.helper.course.CourseActivity
import com.njust.helper.course.data.CourseDatabase
import com.njust.helper.classroom.ClassroomActivity
import com.njust.helper.coursequery.CourseQueryActivity
import com.njust.helper.databinding.ActivityMainBinding
import com.njust.helper.grade.ExamsActivity
import com.njust.helper.grade.GradeActivity
import com.njust.helper.grade.GradeLevelActivity
import com.njust.helper.library.borrowed.BorrowedBooksActivity
import com.njust.helper.library.collection.LibCollectionActivity
import com.njust.helper.library.search.LibSearchActivity
import com.njust.helper.settings.AboutActivity
import com.njust.helper.settings.SettingsActivity
import com.njust.helper.tools.Constants
import com.njust.helper.tools.Prefs
import com.njust.helper.tools.TimeUtil
import com.njust.helper.update.UpdateLogDialog
import java.util.*

class MainActivity : BaseActivity(), MainActivityClickHandler {
  private val viewModel = MainViewModel(this)

  override fun layout() {
    val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    binding.vm = viewModel
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    val id = Prefs.getId(this)
    if (id == "") {
      startActivity(AccountActivity::class.java)
    } else {
      updateCourse()
    }

    checkUpdate()
  }

  private fun checkUpdate() {
    val preVersion = Prefs.getVersion(this)
    if (BuildConfig.VERSION_CODE != preVersion) {
      UpdateLogDialog.showUpdateDialog(this)
      Prefs.putVersion(this, BuildConfig.VERSION_CODE)
      if (preVersion == 0) {
        if (BuildConfig.DEBUG) {
          Prefs.putIdValues(this,
              getString(R.string.testStuid),
              getString(R.string.testJwcPwd),
              getString(R.string.testLibPwd))
        }
      }
    }
  }

  override fun openLibBorrowActivity(view: View) {
    startActivity(BorrowedBooksActivity::class.java)
  }

  override fun openLibCollectionActivity(view: View) {
    startActivity(LibCollectionActivity::class.java)
  }

  override fun openLibSearchActivity(view: View) {
    startActivity(LibSearchActivity::class.java)
  }

  override fun openCourseQueryActivity(view: View) {
    startActivity(CourseQueryActivity::class.java)
  }

  override fun openGradeLevelActivity(v: View) {
    startActivity(GradeLevelActivity::class.java)
  }

  override fun openLinksActivity(view: View) {
    startActivity(LinksActivity::class.java)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == REQUEST_COURSE_REFRESH && resultCode == RESULT_COURSE_REFRESH) {
      updateCourse()
    }
    super.onActivityResult(requestCode, resultCode, data)
  }

  override fun setupActionBar() {}

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.home, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.item_settings -> {
        startActivity(SettingsActivity::class.java)
        return true
      }
      R.id.item_about -> {
        startActivity(AboutActivity::class.java)
        return true
      }
      R.id.item_account -> {
        startActivity(AccountActivity::class.java)
        return true
      }
      else -> return super.onOptionsItemSelected(item)
    }
  }

  override fun openCourseActivity(view: View) {
    startActivityForResult(CourseActivity::class.java, REQUEST_COURSE_REFRESH)
  }

  override fun openClassroomActivity(view: View) {
    startActivity(ClassroomActivity::class.java)
  }

  override fun openExamsActivity(view: View) {
    startActivity(ExamsActivity::class.java)
  }

  override fun openGradeActivity(v: View) {
    startActivity(GradeActivity::class.java)
  }

  private fun updateCourse() {
    val minus = System.currentTimeMillis() - RemoteConfig.getTermStartTime()
    var day = (minus / TimeUtil.ONE_DAY).toInt()
    if (minus < 0L) {
      day--
    }
    val manager = CourseDatabase.getInstance(this)
    val list1 = manager.getCourses(day)
    val list2 = manager.getCourses(day + 1)
    val list3 = manager.getCourses(day + 2)
    if (list1.size + list2.size + list3.size == 0) {
      viewModel.courses = null
    } else {
      val timeList = resources.getStringArray(R.array.section_start)
      val millisOfDay = ((System.currentTimeMillis() - RemoteConfig.getTermStartTime()) % TimeUtil.ONE_DAY).toInt()
      val strings = list1
          .filter { millisOfDay <= Constants.SECTION_END[it.sec1] }
          .mapTo(ArrayList()) { "今天" + timeList[it.sec1] + "/" + it.classroom + "/" + it.name }
      list2.mapTo(strings) { "明天" + timeList[it.sec1] + "/" + it.classroom + "/" + it.name }
      list3.mapTo(strings) { "后天" + timeList[it.sec1] + "/" + it.classroom + "/" + it.name }
      viewModel.courses = strings
    }
  }

  companion object {
    const val RESULT_COURSE_REFRESH = 2
    const val REQUEST_COURSE_REFRESH = 0
  }
}
