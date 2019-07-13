package com.njust.helper.api

import com.crashlytics.android.Crashlytics
import com.squareup.moshi.Moshi

inline fun <T, S> parseReportingError(input: T, parser: (T) -> S): S {
    try {
        return parser(input)
    } catch (t: Throwable) {
        val s = input.toString()
        Crashlytics.logException(Exception(s, t))
        throw ParseErrorException()
    }
}

val sharedMoshi: Moshi = Moshi.Builder().build()
