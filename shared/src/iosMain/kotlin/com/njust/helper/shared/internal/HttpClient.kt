package com.njust.helper.shared.internal

import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*

internal actual val engine: HttpClientEngine = Darwin.create()
