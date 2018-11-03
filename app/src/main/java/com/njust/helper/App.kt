package com.njust.helper

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import com.tencent.bugly.crashreport.CrashReport

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        startService(Intent(this, BackgroundService::class.java)
                .putExtra("action", "registerReceiver"))

        CrashReport.initCrashReport(this, BuildConfig.TENCENT_BUGLY_ID, BuildConfig.DEBUG)
        CrashReport.setIsDevelopmentDevice(this, BuildConfig.DEBUG)
    }
}
