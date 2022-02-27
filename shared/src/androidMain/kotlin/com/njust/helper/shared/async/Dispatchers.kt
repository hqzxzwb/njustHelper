package com.njust.helper.shared.async

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val ioDispatcher: CoroutineDispatcher
  inline get() = Dispatchers.IO
