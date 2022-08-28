package com.njust.helper.shared.api

open class ApiRelatedException(message: String? = null, cause: Throwable? = null) :
  Exception(message, cause)

class ServerErrorException(message: String? = null, cause: Throwable? = null) :
  ApiRelatedException(message, cause)

class LoginErrorException(message: String? = null, cause: Throwable? = null) :
  ApiRelatedException(message, cause)

class ParseErrorException(message: String? = null, cause: Throwable? = null) :
  ApiRelatedException(message, cause)
