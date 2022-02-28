package com.njust.helper.shared.async

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.newFixedThreadPoolContext

actual val ioDispatcher: CoroutineDispatcher =
  newFixedThreadPoolContext(10, "IODispatcherPool")
