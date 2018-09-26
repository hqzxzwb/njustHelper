package com.njust.helper.api

import com.tencent.bugly.crashreport.BuglyLog
import com.tencent.bugly.crashreport.CrashReport

inline fun <T> parseReportingError(s: String, parser: (String) -> T): T {
    try {
        return parser(s)
    } catch (t: Throwable) {
        BuglyLog.e("ParseFailed", s)
        CrashReport.postCatchedException(Exception(s, t))
        throw ParseErrorException()
    }
}
