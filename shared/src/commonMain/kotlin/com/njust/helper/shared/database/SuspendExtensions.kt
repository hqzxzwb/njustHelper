package com.njust.helper.shared.database

import com.njust.helper.shared.async.ioDispatcher
import com.squareup.sqldelight.Query
import kotlinx.coroutines.withContext

suspend fun <T : Any> Query<T>.suspendAsList(): List<T> = withContext(ioDispatcher) {
  executeAsList()
}
