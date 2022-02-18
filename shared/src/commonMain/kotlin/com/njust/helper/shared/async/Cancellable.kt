package com.njust.helper.shared.async

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val globalScope = CoroutineScope(Dispatchers.Main)

interface Cancellable {
  fun cancel()
}

internal inline fun <T> suspendToCancellable(
  noinline completionHandler: (result: T?, error: Throwable?) -> Unit,
  crossinline suspendBlock: suspend () -> T,
): Cancellable {
  val job = globalScope.launch {
    try {
      val result = suspendBlock()
      completionHandler(result, null)
    } catch (e: Exception) {
      completionHandler(null, e)
    }
  }
  return object : Cancellable {
    override fun cancel() {
      job.cancel()
    }
  }
}
