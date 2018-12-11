package com.njust.helper.api

import com.squareup.moshi.Moshi
import com.tencent.bugly.crashreport.BuglyLog
import com.tencent.bugly.crashreport.CrashReport

inline fun <T, S> parseReportingError(input: T, parser: (T) -> S): S {
    try {
        return parser(input)
    } catch (t: Throwable) {
        val s = input.toString()
        BuglyLog.e("ParseFailed", s)
        CrashReport.postCatchedException(Exception(s, t))
        throw ParseErrorException()
    }
}

val sharedMoshi: Moshi = Moshi.Builder().build()
