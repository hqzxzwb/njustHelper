package com.njust.helper.shared.internal

import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import org.koin.core.component.get

internal actual fun createEngine(): HttpClientEngine = OkHttp.create {
  preconfigured = ModuleComponent.get()
}
