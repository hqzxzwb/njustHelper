package com.zwb.commonlibs.utils

abstract class SingletonHolder<out Singleton, in Param>() {
  @Volatile
  private var instance: Singleton? = null

  fun getInstance(arg: Param): Singleton {
    val i = instance
    if (i != null) {
      return i
    }

    return synchronized(this) {
      val i2 = instance
      if (i2 != null) {
        i2
      } else {
        val created = createInstance(arg)
        instance = created
        created
      }
    }
  }

  protected abstract fun createInstance(param: Param): Singleton
}
