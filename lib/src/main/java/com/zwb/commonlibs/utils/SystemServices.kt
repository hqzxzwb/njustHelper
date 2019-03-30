package com.zwb.commonlibs.utils

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.SearchManager
import android.content.ClipboardManager
import android.content.Context

fun Context.getAlarmManager() = getSystemService(Context.ALARM_SERVICE) as AlarmManager

fun Context.getNotificationManager() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

fun Context.getSearchManager() = getSystemService(Context.SEARCH_SERVICE) as SearchManager

fun Context.getClipboardManager() = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
