package com.njust.helper.shared.api

import com.njust.helper.shared.Platform
import com.njust.helper.shared.internal.JsonParserHolder.jsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import okio.use

object LinksApi {
  suspend fun links(): List<Link> {
    return withContext(Dispatchers.Default) {
      val platform = Platform()
      platform.openAsset("links.json").use {
        jsonParser.decodeFromString(it.readUtf8())
      }
    }
  }
}
