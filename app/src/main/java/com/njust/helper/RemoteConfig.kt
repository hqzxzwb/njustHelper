package com.njust.helper

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.njust.helper.tools.TimeUtil
import java.text.SimpleDateFormat
import java.util.*

object RemoteConfig {
    private const val TAG = "RemoteConfig"

    init {
        val settings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(TimeUtil.ONE_HOUR / TimeUtil.ONE_SECOND)
                .build()
        FirebaseRemoteConfig.getInstance().setConfigSettingsAsync(settings)
        FirebaseRemoteConfig.getInstance().fetchAndActivate()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Fetch and activate succeeded.")
                    }
                }
    }

    fun getTermId(): String = FirebaseRemoteConfig.getInstance().getString("termId")

    fun getTermStartTime(): Long {
        val dateString = FirebaseRemoteConfig.getInstance().getString("termStartDate")
        val dd = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = dd.parse(dateString)!!
        return date.time
    }
}
