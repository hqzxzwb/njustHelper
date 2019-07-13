package com.njust.helper

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.njust.helper.settings.CourseAlarms

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        CourseAlarms.registerCourseAlarm(this)
    }
}
