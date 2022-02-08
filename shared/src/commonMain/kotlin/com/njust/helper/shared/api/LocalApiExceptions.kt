package com.njust.helper.shared.api

class ServerErrorException : Exception()

class LoginErrorException : Exception()

class ParseErrorException(message: String? = null, cause: Throwable? = null) :
  Exception(message, cause)
