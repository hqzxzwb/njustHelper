package com.njust.helper.compose

import kotlinx.coroutines.flow.MutableSharedFlow

fun MutableSharedFlow<Unit>.emitOnAction(): () -> Unit {
  return {
    this@emitOnAction.tryEmit(Unit)
  }
}
