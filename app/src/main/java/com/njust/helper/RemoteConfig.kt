package com.njust.helper

import java.text.SimpleDateFormat
import java.util.*

object RemoteConfig {
  private const val TAG = "RemoteConfig"

  fun getTermId(): String = "2021-2022-1"

  fun getTermStartTime(): Long {
    val dateString = "2021-08-23"
    val dd = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
    val date = dd.parse(dateString)!!
    return date.time
  }
}
