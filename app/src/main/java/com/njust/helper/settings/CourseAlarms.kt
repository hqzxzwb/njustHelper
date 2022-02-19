package com.njust.helper.settings

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import com.njust.helper.BuildConfig
import com.njust.helper.R
import com.njust.helper.tools.Prefs
import com.njust.helper.tools.TimeUtil
import com.zwb.commonlibs.utils.requireSystemService
import java.util.*

object CourseAlarms {
  const val ACTION_COURSE_ALARM = BuildConfig.APPLICATION_ID + ".ACTION_COURSE_ALARM"

  const val ALARM_MODE_PREVIOUS_DAY = 0
  const val ALARM_MODE_CURRENT_DAY = 1
  const val ALARM_MODE_NONE = 2

  fun registerCourseAlarm(context: Context) {
    val alarmManager = context.requireSystemService<AlarmManager>()
    val pendingIntent = buildPendingIntent(context)
    alarmManager.cancel(pendingIntent)
    val nextAlarmTime = getNextAlarmTime(context)
    if (nextAlarmTime < 0) {
      return
    }
    AlarmManagerCompat.setExact(alarmManager, AlarmManager.RTC, nextAlarmTime, pendingIntent)
  }

  private fun getNextAlarmTime(context: Context): Long {
    val mode = Prefs.getCourseNotificationMode(context)
    if (mode == ALARM_MODE_NONE) {
      return -1
    }
    val notificationTime = Prefs.getCourseNotificationTime(context)
    val calendar = Calendar.getInstance()
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
    val currentMinute = calendar.get(Calendar.MINUTE)
    var timeDiff = (notificationTime - (currentHour * 60 + currentMinute)) * TimeUtil.ONE_MINUTE
    if (timeDiff <= 50) {    // 留出后续代码执行时间
      timeDiff += TimeUtil.ONE_DAY
    }
    return System.currentTimeMillis() + timeDiff
  }

  private fun buildPendingIntent(context: Context): PendingIntent {
    val intent = Intent(ACTION_COURSE_ALARM)
    return PendingIntent.getBroadcast(context, R.id.pending_request_code_course_alarm, intent, PendingIntent.FLAG_IMMUTABLE)
  }
}
