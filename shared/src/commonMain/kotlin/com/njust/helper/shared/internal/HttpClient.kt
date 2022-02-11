package com.njust.helper.shared.internal

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.cookies.*
import io.ktor.client.features.json.*

internal val httpClient = HttpClient(CIO) {
  followRedirects = false
  install(JsonFeature)
  install(HttpCookies) {
    storage = AcceptAllCookiesStorage()
  }
}
