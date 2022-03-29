package com.zwb.commonlibs.utils

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ThreadLocalDelegate<T : Any>(
  val supplier: () -> T,
) : ReadOnlyProperty<Any?, T> {
  private val threadLocal = object : ThreadLocal<T>() {
    override fun initialValue(): T {
      return supplier()
    }
  }

  override fun getValue(thisRef: Any?, property: KProperty<*>): T {
    return threadLocal.get()!!
  }
}
