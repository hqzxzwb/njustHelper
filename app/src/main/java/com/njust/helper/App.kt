package com.njust.helper

import android.app.Application
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.njust.helper.settings.CourseAlarms

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        CourseAlarms.registerCourseAlarm(this)

//        FirebaseRemoteConfig.getInstance().setDefaults(R.xml.remote_config_defaults)
    }
}
