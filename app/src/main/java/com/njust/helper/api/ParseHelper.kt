package com.njust.helper.api

inline fun <T> parseReportingError(s: String, parser: (String) -> T): T {
    try {
        return parser(s)
    } catch (t: Throwable) {
        throw ParseErrorException()
    }
}
