package com.njust.helper.shared.bizmodel

import com.njust.helper.shared.internal.ModuleComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named
import kotlin.properties.ReadWriteProperty

object ObservablePropertyDelegateFactory {
  const val KOIN_NAME = "ObservablePropertyDelegate"
}

fun <T> observableProperty(initialValue: T): ReadWriteProperty<Any, T> {
  val qualifier = named(ObservablePropertyDelegateFactory.KOIN_NAME)
  return ModuleComponent.get<ObservablePropertyDelegateProvider>(qualifier)
    .provide(initialValue)
}

interface ObservablePropertyDelegateProvider {
  fun <T> provide(initialValue: T): ReadWriteProperty<Any, T>
}
