package com.njust.helper

import android.app.Application
import com.njust.helper.settings.CourseAlarms
import com.umeng.commonsdk.UMConfigure

class App : Application() {
  override fun onCreate() {
    super.onCreate()

    CourseAlarms.registerCourseAlarm(this)

    UMConfigure.init(this, getString(R.string.umengAppKey), null, 0, null)
  }
}
