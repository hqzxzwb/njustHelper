package com.njust.helper.shared.internal

import kotlinx.serialization.json.Json

internal object JsonParserHolder {
  internal val jsonParser = Json {
    ignoreUnknownKeys = true
  }
}
