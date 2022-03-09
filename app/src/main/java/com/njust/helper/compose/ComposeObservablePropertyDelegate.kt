package com.njust.helper.compose

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.njust.helper.shared.bizmodel.ObservablePropertyDelegateFactory
import com.njust.helper.shared.bizmodel.ObservablePropertyDelegateProvider
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private class ComposeObservablePropertyDelegate<T>(private val mutableState: MutableState<T>) :
  ReadWriteProperty<Any, T> {
  override fun getValue(thisRef: Any, property: KProperty<*>): T {
    return mutableState.value
  }

  override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
    mutableState.value = value
  }
}

val composeObservablePropertyDelegateModule = module {
  single<ObservablePropertyDelegateProvider>(named(ObservablePropertyDelegateFactory.KOIN_NAME)) {
    object : ObservablePropertyDelegateProvider {
      override fun <T> provide(initialValue: T): ReadWriteProperty<Any, T> {
        return ComposeObservablePropertyDelegate(mutableStateOf(initialValue))
      }
    }
  }
}
