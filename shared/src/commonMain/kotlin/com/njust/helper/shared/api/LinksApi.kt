package com.njust.helper.shared.api

import com.futuremind.koruksp.ToNativeClass
import com.njust.helper.shared.MR
import com.njust.helper.shared.async.MainScopeProvider
import com.njust.helper.shared.async.ioDispatcher
import com.njust.helper.shared.internal.jsonParser
import com.njust.helper.shared.readText
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import okio.IOException
import kotlin.coroutines.cancellation.CancellationException

@ToNativeClass(launchOnScope = MainScopeProvider::class)
object LinksApi {
  @Throws(IOException::class, CancellationException::class)
  suspend fun links(): List<CommonLink> {
    return withContext(ioDispatcher) {
      jsonParser.decodeFromString(MR.assets.links.readText())
    }
  }
}
