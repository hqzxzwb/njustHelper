package com.njust.helper.shared.api

import com.njust.helper.shared.internal.JsonParserHolder.jsonParser
import com.njust.helper.shared.readText
import com.njust.helper.shared.MR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import okio.IOException
import kotlin.coroutines.cancellation.CancellationException

object LinksApi {
  @Throws(IOException::class, CancellationException::class)
  suspend fun links(): List<Link> {
    return withContext(Dispatchers.Default) {
      jsonParser.decodeFromString(MR.assets.links.readText())
    }
  }
}
