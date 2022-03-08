package com.njust.helper.shared.internal

import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

private object Component : KoinComponent

internal actual val engine: HttpClientEngine = OkHttp.create {
  preconfigured = Component.get()
}
