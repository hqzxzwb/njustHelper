package com.zwb.commonlibs.utils

import android.content.Context
import androidx.core.content.ContextCompat

inline fun <reified T> Context.getSystemService(): T? {
  return ContextCompat.getSystemService(this, T::class.java)
}

inline fun <reified T> Context.requireSystemService(): T {
  return requireSystemService(T::class.java)
}

@PublishedApi
internal fun <T> Context.requireSystemService(clazz: Class<T>): T {
  return requireNotNull(ContextCompat.getSystemService(this, clazz)) {
    "Required $clazz is not found."
  }
}
