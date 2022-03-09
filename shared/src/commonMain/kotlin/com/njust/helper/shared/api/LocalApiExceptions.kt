package com.njust.helper.shared.api

open class ApiRelatedException(message: String? = null, cause: Throwable? = null) :
  Exception(message, cause)

class ServerErrorException : ApiRelatedException()

class LoginErrorException : ApiRelatedException()

class ParseErrorException(message: String? = null, cause: Throwable? = null) :
  ApiRelatedException(message, cause)
