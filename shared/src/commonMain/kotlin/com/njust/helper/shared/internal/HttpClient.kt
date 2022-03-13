package com.njust.helper.shared.internal

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cookies.*
import io.ktor.serialization.kotlinx.json.*

internal expect fun createEngine(): HttpClientEngine

val httpClient = HttpClient(createEngine()) {
  followRedirects = false
  install(ContentNegotiation) {
    json(json = jsonParser)
  }
  install(HttpCookies) {
    storage = AcceptAllCookiesStorage()
  }
}
