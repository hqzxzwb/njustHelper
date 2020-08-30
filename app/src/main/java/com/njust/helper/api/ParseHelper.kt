package com.njust.helper.api

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.squareup.moshi.Moshi

inline fun <T, S> parseReportingError(input: T, parser: (T) -> S): S {
  try {
    return parser(input)
  } catch (t: Throwable) {
    val s = input.toString()
    FirebaseCrashlytics.getInstance().recordException(Exception(s, t))
    throw ParseErrorException()
  }
}

val sharedMoshi: Moshi = Moshi.Builder().build()
