package com.njust.helper.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.njust.helper.BackgroundService
import com.njust.helper.BuildConfig
import com.njust.helper.LinksActivity
import com.njust.helper.R
import com.njust.helper.account.AccountActivity
import com.njust.helper.activity.BaseActivity
import com.njust.helper.classroom.ClassroomActivity
import com.njust.helper.course.CourseActivity
import com.njust.helper.course.data.CourseManager
import com.njust.helper.coursequery.CourseQueryActivity
import com.njust.helper.databinding.ActivityMainBinding
import com.njust.helper.grade.ExamsActivity
import com.njust.helper.grade.GradeActivity
import com.njust.helper.grade.GradeLevelActivity
import com.njust.helper.library.search.LibSearchActivity
import com.njust.helper.library.borrowed.BorrowedBooksActivity
import com.njust.helper.library.collection.LibCollectionActivity
import com.njust.helper.model.UpdateInfo
import com.njust.helper.settings.AboutActivity
import com.njust.helper.settings.SettingsActivity
import com.njust.helper.tools.Constants
import com.njust.helper.tools.Prefs
import com.njust.helper.tools.TimeUtil
import com.njust.helper.update.UpdateActivity
import com.njust.helper.update.UpdateLogDialog
import com.zwb.commonlibs.http.NetState
import java.util.*

class MainActivity : BaseActivity(), MainActivityClickHandler {
    private var receiver: BroadcastReceiver? = null

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

        val receiver = this.receiver
        if (receiver != null) {
            LocalBroadcastManager.getInstance(this)
                    .registerReceiver(receiver, IntentFilter(BackgroundService.ACTION_UPDATE_INFO))
        }

        checkUpdate()
    }

    override fun onDestroy() {
        super.onDestroy()

        val receiver = this.receiver
        if (receiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        }
    }

    private fun checkUpdate() {
        // 如果距离上次刷新超过一天,检查更新
        val now = System.currentTimeMillis()
        val lastTime = Prefs.getLastCheckUpdateTime(this)
        val time = now - lastTime
        if (time < 0 || time > 24 * 3600 * 1000L) {
            if (NetState.getNetworkState(this)) {
                startService(Intent(this, BackgroundService::class.java)
                        .putExtra("action", "checkUpdate"))
            }
        }
        // 删除更新文件并弹出更新日志
        val preVersion = Prefs.getVersion(this)
        if (BuildConfig.VERSION_CODE != preVersion) {
            //尝试删除之前下载到缓存目录的更新文件，并不关心删除是否成功
            externalCacheDir
                    ?.listFiles()
                    ?.filter { it.toString().endsWith(".apk") }
                    ?.forEach { it.delete() }

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

    override fun layoutRes(): Int {
        return 0
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
            R.id.item_update -> {
                showSnack("正在检查更新……")
                val intent = Intent(this, BackgroundService::class.java)
                intent.putExtra("silentlyCheckUpdate", false)
                        .putExtra("action", "checkUpdate")
                if (receiver == null) {
                    val receiver = object : BroadcastReceiver() {
                        override fun onReceive(context: Context, intent: Intent) {
                            onReceiveUpdateIntent(intent)
                        }
                    }
                    LocalBroadcastManager.getInstance(this)
                            .registerReceiver(receiver, IntentFilter(BackgroundService.ACTION_UPDATE_INFO))
                    this.receiver = receiver
                }
                startService(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    internal fun onReceiveUpdateIntent(intent: Intent) {
        val status = intent.getIntExtra("updateStatus", BackgroundService.UPDATE_STATUS_FAIL)
        when (status) {
            BackgroundService.UPDATE_STATUS_FAIL -> showSnack("检查更新失败，请检查网络后重试")
            BackgroundService.UPDATE_STATUS_NO_UPDATE -> showSnack("未检测到更新")
            else -> {
                val updateInfo = intent.getParcelableExtra<UpdateInfo>("updateInfo")
                val updateActivityIntent = UpdateActivity.createIntent(this, updateInfo)
                AlertDialog.Builder(this)
                        .setTitle("发现新版本")
                        .setMessage(updateInfo.toString())
                        .setPositiveButton("立即查看") { _, _ -> startActivity(updateActivityIntent) }
                        .setNegativeButton("以后再说", null)
                        .show()
            }
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
        val minus = System.currentTimeMillis() - Prefs.getTermStartTime(this)
        var day = (minus / TimeUtil.ONE_DAY).toInt()
        if (minus < 0L) {
            day--
        }
        val manager = CourseManager.getInstance(this)
        val list1 = manager.getCourses(day)
        val list2 = manager.getCourses(day + 1)
        val list3 = manager.getCourses(day + 2)
        if (list1.size + list2.size + list3.size == 0) {
            viewModel.courses = null
        } else {
            val timeList = resources.getStringArray(R.array.section_start)
            val millisOfDay = ((System.currentTimeMillis() - Prefs.getTermStartTime(this)) % TimeUtil.ONE_DAY).toInt()
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
