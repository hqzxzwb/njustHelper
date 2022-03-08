package com.njust.helper.shared.internal

import com.njust.helper.shared.internal.JsonParserHolder.jsonParser
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cookies.*
import io.ktor.serialization.kotlinx.json.*

internal object HttpClientHolder {
  val httpClient = HttpClient(engine) {
    followRedirects = false
    install(ContentNegotiation) {
      json(json = jsonParser)
    }
    install(HttpCookies) {
      storage = AcceptAllCookiesStorage()
    }
  }
}

internal expect val engine: HttpClientEngine
