package com.njust.helper;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.os.AsyncTaskCompat;

import com.njust.helper.model.UpdateInfo;
import com.njust.helper.settings.UpdateActivity;
import com.njust.helper.tools.AppHttpHelper;
import com.njust.helper.tools.Constants;
import com.njust.helper.tools.JsonData;
import com.njust.helper.tools.JsonTask;
import com.njust.helper.tools.Prefs;
import com.njust.helper.tools.TimeReceiver;
import com.zwb.commonlibs.utils.JsonUtils;

import org.json.JSONObject;

public class BackgroundService extends Service {
    public static final String ACTION_UPDATE_INFO = "com.njust.helper.UPDATE_INFO";
    public static final int UPDATE_STATUS_NO_UPDATE = 0;
    public static final int UPDATE_STATUS_UPDATE = 1;
    public static final int UPDATE_STATUS_FAIL = 2;

    /**
     * false - 主动发起的检查更新请求，应当作出积极的响应<br>
     * true - 例行检查更新，只应当在检查到更新时发送通知栏消息
     */
    private boolean silentlyCheckUpdate;
    private boolean isCheckingUpdate;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent == null ? null : intent.getStringExtra("action");
        if (action == null || "registerReceiver".equals(action)) {
            //TimeTick监听
            BroadcastReceiver receiver = new TimeReceiver();
            registerReceiver(receiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        } else if ("checkUpdate".equals(action)) {
            silentlyCheckUpdate = intent.getBooleanExtra("silentlyCheckUpdate", true);
            if (!isCheckingUpdate) AsyncTaskCompat.executeParallel(new UpdateTask());
        }
        return START_STICKY;
    }

    private class UpdateTask extends JsonTask<Void, UpdateInfo> {
        @Override
        protected void onPreExecute() {
            isCheckingUpdate = true;
        }

        @Override
        protected JsonData<UpdateInfo> doInBackground(Void... params) {
            AppHttpHelper httpHelper = new AppHttpHelper();
            try {
                String s = httpHelper.getGetResult(BuildConfig.BASE_URL + "update_info.php");
                return new JsonData<UpdateInfo>(s) {
                    @Override
                    protected UpdateInfo parseData(JSONObject jsonObject) throws Exception {
                        return JsonUtils.parseBean(jsonObject, UpdateInfo.class);
                    }
                };
            } catch (Exception e) {
                e.printStackTrace();
            }
            return JsonData.newNetErrorInstance();
        }

        @Override
        protected void onPostExecute(JsonData<UpdateInfo> updateInfoJsonData) {
            isCheckingUpdate = false;
            super.onPostExecute(updateInfoJsonData);
        }

        @Override
        protected void onSuccess(UpdateInfo updateInfo) {
            if (silentlyCheckUpdate) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(BackgroundService.this)
                        .setContentTitle("南理工助手-发现新版本")
                        .setContentText("点击查看")
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentIntent(TaskStackBuilder.create(BackgroundService.this)
                                .addParentStack(UpdateActivity.class)
                                .addNextIntent(new Intent(BackgroundService.this, UpdateActivity.class)
                                        .putExtra("updateInfo", updateInfo))
                                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT))
                        .setVibrate(Constants.NOTIFICATION_VIBRATION_TIME);
                NotificationManagerCompat.from(BackgroundService.this)
                        .notify(Constants.NOTIFICATION_CODE_UPDATE, builder.build());
            } else {
                Intent intent = new Intent(ACTION_UPDATE_INFO);
                intent.putExtra("updateInfo", updateInfo);
                intent.putExtra("updateStatus", UPDATE_STATUS_UPDATE);
                LocalBroadcastManager.getInstance(BackgroundService.this).sendBroadcast(intent);
            }
            Prefs.putLastCheckUpdateTime(BackgroundService.this);
        }

        @Override
        protected void onNetError() {
            if (!silentlyCheckUpdate) {
                LocalBroadcastManager.getInstance(BackgroundService.this)
                        .sendBroadcast(new Intent(ACTION_UPDATE_INFO).putExtra("updateStatus", UPDATE_STATUS_FAIL));
            }
        }

        @Override
        protected void onLogFailed() {
            if (!silentlyCheckUpdate) {
                LocalBroadcastManager.getInstance(BackgroundService.this)
                        .sendBroadcast(new Intent(ACTION_UPDATE_INFO).putExtra("updateStatus", UPDATE_STATUS_NO_UPDATE));
            }
            Prefs.putLastCheckUpdateTime(BackgroundService.this);
        }
    }
}
