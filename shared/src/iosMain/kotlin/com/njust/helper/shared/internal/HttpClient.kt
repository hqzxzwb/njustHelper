package com.njust.helper.shared.internal

import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*

internal actual fun createEngine(): HttpClientEngine = Darwin.create()
