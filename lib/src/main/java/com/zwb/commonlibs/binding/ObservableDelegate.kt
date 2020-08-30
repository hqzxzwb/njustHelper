@file:Suppress("NOTHING_TO_INLINE")

package com.zwb.commonlibs.binding

import androidx.databinding.BaseObservable
import kotlin.reflect.KProperty

class ObservableDelegate<T>(
    @JvmField val id: Int,
    @JvmField var value: T
) {
  inline operator fun getValue(thisRef: BaseObservable, property: KProperty<*>): T {
    return value
  }

  inline operator fun setValue(thisRef: BaseObservable, property: KProperty<*>, value: T) {
    this.value = value
    thisRef.notifyPropertyChanged(id)
  }
}

@Suppress("FunctionName") //Constructor function
inline fun <T> ObservableDelegate(id: Int): ObservableDelegate<T?> = ObservableDelegate(id, null)

class IntObservableDelegate(
    @JvmField val id: Int,
    @JvmField var value: Int = 0
) {
  inline operator fun getValue(thisRef: BaseObservable, property: KProperty<*>): Int {
    return value
  }

  inline operator fun setValue(thisRef: BaseObservable, property: KProperty<*>, value: Int) {
    this.value = value
    thisRef.notifyPropertyChanged(id)
  }
}

class LongObservableDelegate(
    @JvmField val id: Int,
    @JvmField var value: Long = 0
) {
  inline operator fun getValue(thisRef: BaseObservable, property: KProperty<*>): Long {
    return value
  }

  inline operator fun setValue(thisRef: BaseObservable, property: KProperty<*>, value: Long) {
    this.value = value
    thisRef.notifyPropertyChanged(id)
  }
}

class FloatObservableDelegate(
    @JvmField val id: Int,
    @JvmField var value: Float = 0F
) {
  inline operator fun getValue(thisRef: BaseObservable, property: KProperty<*>): Float {
    return value
  }

  inline operator fun setValue(thisRef: BaseObservable, property: KProperty<*>, value: Float) {
    this.value = value
    thisRef.notifyPropertyChanged(id)
  }
}

class DoubleObservableDelegate(
    @JvmField val id: Int,
    @JvmField var value: Double = .0
) {
  inline operator fun getValue(thisRef: BaseObservable, property: KProperty<*>): Double {
    return value
  }

  inline operator fun setValue(thisRef: BaseObservable, property: KProperty<*>, value: Double) {
    this.value = value
    thisRef.notifyPropertyChanged(id)
  }
}

class BooleanObservableDelegate(
    @JvmField val id: Int,
    @JvmField var value: Boolean = false
) {
  inline operator fun getValue(thisRef: BaseObservable, property: KProperty<*>): Boolean {
    return value
  }

  inline operator fun setValue(thisRef: BaseObservable, property: KProperty<*>, value: Boolean) {
    this.value = value
    thisRef.notifyPropertyChanged(id)
  }
}
