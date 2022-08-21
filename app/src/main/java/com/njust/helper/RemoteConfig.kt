package com.njust.helper

import java.text.SimpleDateFormat
import java.util.*

object RemoteConfig {
  private const val TAG = "RemoteConfig"

  fun getTermId(): String = "2022-2023-1"

  fun getTermStartTime(): Long {
    val dateString = "2022-08-22"
    val dd = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
    val date = dd.parse(dateString)!!
    return date.time
  }
}
