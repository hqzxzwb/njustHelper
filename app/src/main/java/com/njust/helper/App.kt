package com.njust.helper

import android.app.Application
import android.content.Intent
import com.njust.helper.tools.AppHttpHelper
import com.tencent.bugly.crashreport.CrashReport

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startService(Intent(this, BackgroundService::class.java)
                .putExtra("action", "registerReceiver"))

        CrashReport.initCrashReport(this, BuildConfig.TENCENT_BUGLY_ID, BuildConfig.DEBUG)

        AppHttpHelper.getInstance().init(this)
    }
}
