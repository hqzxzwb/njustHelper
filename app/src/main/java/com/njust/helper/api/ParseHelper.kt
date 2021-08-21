package com.njust.helper.api

import com.squareup.moshi.Moshi

inline fun <T, S> parseReportingError(input: T, parser: (T) -> S): S {
  try {
    return parser(input)
  } catch (t: Throwable) {
    throw ParseErrorException()
  }
}

val sharedMoshi: Moshi = Moshi.Builder().build()
