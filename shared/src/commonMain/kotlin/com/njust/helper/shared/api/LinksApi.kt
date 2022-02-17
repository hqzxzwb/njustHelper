package com.njust.helper.shared.api

import com.njust.helper.shared.Platform
import com.njust.helper.shared.internal.JsonParserHolder.jsonParser
import org.example.library.MR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import okio.IOException
import okio.use
import kotlin.coroutines.cancellation.CancellationException

object LinksApi {
  @Throws(IOException::class, CancellationException::class)
  suspend fun links(): List<Link> {
    MR.assets.links
    return withContext(Dispatchers.Default) {
      val platform = Platform()
      platform.openAsset("links.json").use {
        jsonParser.decodeFromString(it.readUtf8())
      }
    }
  }
}
