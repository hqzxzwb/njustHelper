package com.njust.helper;

import android.app.Application;
import android.content.Intent;

import com.njust.helper.tools.AppHttpHelper;
import com.tencent.bugly.crashreport.CrashReport;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent(this, BackgroundService.class)
                .putExtra("action", "registerReceiver"));

        CrashReport.initCrashReport(this, BuildConfig.TENCENT_BUGLY_ID, BuildConfig.DEBUG);

        AppHttpHelper.getInstance().init(this);
    }
}
