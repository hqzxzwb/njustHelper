package com.njust.helper

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.njust.helper.tools.TimeReceiver

class BackgroundService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getStringExtra("action")
        if (action == null || "registerReceiver" == action) {
            val receiver = TimeReceiver()
            registerReceiver(receiver, IntentFilter(Intent.ACTION_TIME_TICK))
        }
        return START_STICKY
    }
}
