package com.njust.helper

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.LocalBroadcastManager
import com.njust.helper.model.UpdateInfo
import com.njust.helper.tools.Constants
import com.njust.helper.tools.JsonData
import com.njust.helper.tools.Prefs
import com.njust.helper.tools.TimeReceiver
import com.njust.helper.update.UpdateActivity
import com.njust.helper.update.UpdateApi
import io.reactivex.android.schedulers.AndroidSchedulers

class BackgroundService : Service() {
    companion object {
        const val ACTION_UPDATE_INFO = "com.njust.helper.UPDATE_INFO"
        const val UPDATE_STATUS_NO_UPDATE = 0
        const val UPDATE_STATUS_UPDATE = 1
        const val UPDATE_STATUS_FAIL = 2
    }

    /**
     * false - 主动发起的检查更新请求，应当作出积极的响应<br></br>
     * true - 例行检查更新，只应当在检查到更新时发送通知栏消息
     */
    private var silentlyCheckUpdate: Boolean = false
    private var isCheckingUpdate: Boolean = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getStringExtra("action")
        if (action == null || "registerReceiver" == action) {
            val receiver = TimeReceiver()
            registerReceiver(receiver, IntentFilter(Intent.ACTION_TIME_TICK))
        } else if ("checkUpdate" == action) {
            silentlyCheckUpdate = intent.getBooleanExtra("silentlyCheckUpdate", true)
            if (!isCheckingUpdate) {
                isCheckingUpdate = true
                UpdateApi.INSTANCE.checkUpdate()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            isCheckingUpdate = false
                            when (it.status) {
                                JsonData.STATUS_SUCCESS -> onCheckUpdateResult(it.data)
                                JsonData.STATUS_LOG_FAIL -> onNoUpdate()
                            }
                        }, {
                            isCheckingUpdate = false
                            if (!silentlyCheckUpdate) {
                                LocalBroadcastManager.getInstance(this@BackgroundService)
                                        .sendBroadcast(Intent(ACTION_UPDATE_INFO)
                                                .putExtra("updateStatus", UPDATE_STATUS_FAIL))
                            }
                        })
            }
        }
        return START_STICKY
    }

    private fun onCheckUpdateResult(result: UpdateInfo) {
        if (silentlyCheckUpdate) {
            val builder = NotificationCompat.Builder(this@BackgroundService, "update")
                    .setContentTitle("南理工助手-发现新版本")
                    .setContentText("点击查看")
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(TaskStackBuilder.create(this@BackgroundService)
                            .addParentStack(UpdateActivity::class.java)
                            .addNextIntent(Intent(this@BackgroundService, UpdateActivity::class.java)
                                    .putExtra("updateInfo", result))
                            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT))
            NotificationManagerCompat.from(this@BackgroundService)
                    .notify(Constants.NOTIFICATION_CODE_UPDATE, builder.build())
        } else {
            val intent = Intent(ACTION_UPDATE_INFO)
                    .putExtra("updateInfo", result)
                    .putExtra("updateStatus", UPDATE_STATUS_UPDATE)
            LocalBroadcastManager.getInstance(this@BackgroundService).sendBroadcast(intent)
        }
        Prefs.putLastCheckUpdateTime(this@BackgroundService)
    }

    private fun onNoUpdate() {
        if (!silentlyCheckUpdate) {
            LocalBroadcastManager.getInstance(this@BackgroundService)
                    .sendBroadcast(Intent(ACTION_UPDATE_INFO)
                            .putExtra("updateStatus", UPDATE_STATUS_NO_UPDATE))
        }
        Prefs.putLastCheckUpdateTime(this@BackgroundService)
    }
}
