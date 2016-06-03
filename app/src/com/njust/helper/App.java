package com.njust.helper;

import android.app.Application;
import android.content.Intent;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.zwb.commonlibs.utils.LogUtils;

public class App extends Application {
    private static Tracker mTracker;

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

        LogUtils.init(BuildConfig.DEBUG);

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        mTracker = analytics.newTracker(R.xml.global_tracker);
    }
}
