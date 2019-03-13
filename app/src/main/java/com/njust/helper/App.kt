package com.njust.helper

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.njust.helper.settings.CourseAlarms
import com.tencent.bugly.crashreport.CrashReport

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        CourseAlarms.registerCourseAlarm(this)

        CrashReport.initCrashReport(this, BuildConfig.TENCENT_BUGLY_ID, BuildConfig.DEBUG)
        CrashReport.setIsDevelopmentDevice(this, BuildConfig.DEBUG)
    }
}
