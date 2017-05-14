package com.njust.helper;

import android.app.Application;
import android.content.Intent;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.njust.helper.tools.AppHttpHelper;
import com.tencent.bugly.crashreport.CrashReport;

public class App extends Application {
    private static Tracker mTracker;

    static {

    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public static Tracker getDefaultTracker() {
        return mTracker;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent(this, BackgroundService.class)
                .putExtra("action", "registerReceiver"));

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        mTracker = analytics.newTracker(R.xml.global_tracker);
        mTracker.enableAutoActivityTracking(true);
        CrashReport.initCrashReport(this, BuildConfig.TENCENT_BUGLY_ID, BuildConfig.DEBUG);

        AppHttpHelper.getInstance().init(this);
    }
}
