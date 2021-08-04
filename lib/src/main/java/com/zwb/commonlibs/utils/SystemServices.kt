package com.zwb.commonlibs.utils

import android.content.Context
import androidx.core.content.ContextCompat

inline fun <reified T> Context.getSystemService(): T? {
  return ContextCompat.getSystemService(this, T::class.java)
}

inline fun <reified T> Context.requireSystemService(): T {
  return requireNotNull(getSystemService()) { "Required ${T::class.java} is not found." }
}
