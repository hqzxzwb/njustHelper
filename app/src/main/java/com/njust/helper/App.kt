package com.njust.helper

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.njust.helper.settings.CourseAlarms

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        CourseAlarms.registerCourseAlarm(this)

        FirebaseRemoteConfig.getInstance().setDefaults(R.xml.remote_config_defaults)
    }
}
