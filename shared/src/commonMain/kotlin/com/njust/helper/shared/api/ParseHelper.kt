package com.njust.helper.shared.api

inline fun <T, S> parseReportingError(input: T, parser: (T) -> S): S {
  try {
    return parser(input)
  } catch (t: Throwable) {
    throw ParseErrorException(cause = t)
  }
}
