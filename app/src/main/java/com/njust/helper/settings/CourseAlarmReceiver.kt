package com.njust.helper.settings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.njust.helper.R
import com.njust.helper.RemoteConfig
import com.njust.helper.course.CourseActivity
import com.njust.helper.course.data.CourseDatabase
import com.njust.helper.tools.Constants
import com.njust.helper.tools.Prefs
import com.njust.helper.tools.TimeUtil
import com.zwb.commonlibs.utils.getNotificationManager

class CourseAlarmReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent?) {
    if (intent?.action != CourseAlarms.ACTION_COURSE_ALARM) {
      return
    }
    CourseAlarms.registerCourseAlarm(context)
    notifyCourse(context)
  }

  private fun notifyCourse(context: Context) {
    val mode = Prefs.getCourseNotificationMode(context)
    val now = System.currentTimeMillis()
    val minus = now - RemoteConfig.getTermStartTime()
    if (minus < 0) {
      return
    }
    var day = ((System.currentTimeMillis() - RemoteConfig.getTermStartTime()) / TimeUtil.ONE_DAY).toInt()
    val count: Int
    var notiString: String? = null
    when (mode) {
      CourseAlarms.ALARM_MODE_PREVIOUS_DAY -> {
        count = CourseDatabase.getInstance(context).countCourses(++day)
        if (count > 0) {
          notiString = "明天有" + count + "节课，点击查看"
        }
      }
      CourseAlarms.ALARM_MODE_CURRENT_DAY -> {
        count = CourseDatabase.getInstance(context).countCourses(day)
        if (count > 0) {
          notiString = "今天有" + count + "节课，点击查看"
        }
      }
    }
    if (notiString == null) return
    val intent = Intent(context, CourseActivity::class.java)
        .putExtra("time", now + (if (mode == CourseAlarms.ALARM_MODE_CURRENT_DAY) 0 else 1) * TimeUtil.ONE_DAY)
    val pendingIntent = TaskStackBuilder.create(context)
        .addParentStack(CourseActivity::class.java)
        .addNextIntent(intent)
        .getPendingIntent(R.id.pending_request_code_course_notification, 0)
    val builder = NotificationCompat.Builder(context, "CourseReminder")
        .setTicker(context.getText(R.string.app_name))
        .setContentTitle(context.getText(R.string.app_name))
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentText(notiString)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
    if (context.getSharedPreferences("time", 0).getBoolean("course_vib", true)) {
      builder.setVibrate(Constants.NOTIFICATION_VIBRATION_TIME)
    }
    context.getNotificationManager().notify(Constants.NOTIFICATION_CODE_COURSE, builder.build())
  }
}
