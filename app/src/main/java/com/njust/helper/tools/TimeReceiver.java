package com.njust.helper.tools;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.njust.helper.R;
import com.njust.helper.course.CourseActivity;
import com.njust.helper.course.data.CourseManager;

import java.util.Calendar;

// TODO: 2016/6/4 better use AlarmManager instead
public final class TimeReceiver extends BroadcastReceiver {
    private NotificationManager mNm;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
            show_noties(context);
        }
    }

    private void show_noties(Context context) {
        int time = Prefs.getCourseNotificationTime(context);
        Calendar calendar = Calendar.getInstance();
        mNm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int current_hour = calendar.get(Calendar.HOUR_OF_DAY);
        int current_minute = calendar.get(Calendar.MINUTE);
        if (current_hour * 60 + current_minute == time) {
            int mode = Prefs.getCourseNotificationMode(context);
            if (mode < 2) {
                noti_course(context, calendar, mode);
            }
        }
    }

    private void noti_course(Context context, Calendar calendar, int mode) {
        long now = calendar.getTimeInMillis();
        long minus = now - Prefs.getTermStartTime(context);
        if (minus < 0) {
            return;
        }
        int day = (int) ((System.currentTimeMillis() - Prefs.getTermStartTime(context)) / TimeUtil.ONE_DAY);
        int count;
        String notiString = null;
        switch (mode) {
            case 0:
                count = CourseManager.getInstance(context).countCourses(++day);
                if (count > 0)
                    notiString = "明天有" + count + "节课，点击查看";
                break;
            case 1:
                count = CourseManager.getInstance(context).countCourses(day);
                if (count > 0)
                    notiString = "今天有" + count + "节课，点击查看";
                break;
        }
        if (notiString == null) return;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CourseReminder")
                .setTicker(context.getText(R.string.app_name))
                .setContentTitle(context.getText(R.string.app_name))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText(notiString)
                .setAutoCancel(true)
                .setContentIntent(TaskStackBuilder.create(context)
                        .addParentStack(CourseActivity.class)
                        .addNextIntent(new Intent(context, CourseActivity.class)
                                .putExtra("time", now + (mode == 1 ? 0 : 1) * TimeUtil.ONE_DAY))
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT));
        if (context.getSharedPreferences("time", 0).getBoolean("course_vib", true)) {
            builder.setVibrate(Constants.NOTIFICATION_VIBRATION_TIME);
        }
        mNm.notify(Constants.NOTIFICATION_CODE_COURSE, builder.build());
    }
}
