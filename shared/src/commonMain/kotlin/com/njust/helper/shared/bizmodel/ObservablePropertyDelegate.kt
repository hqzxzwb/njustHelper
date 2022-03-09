package com.njust.helper.shared.bizmodel

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named
import kotlin.properties.ReadWriteProperty

object ObservablePropertyDelegateFactory : KoinComponent {
  const val KOIN_NAME = "ObservablePropertyDelegate"
}

fun <T> observableProperty(initialValue: T): ReadWriteProperty<Any, T> {
  val qualifier = named(ObservablePropertyDelegateFactory.KOIN_NAME)
  return ObservablePropertyDelegateFactory.get<ObservablePropertyDelegateProvider>(qualifier)
    .provide(initialValue)
}

interface ObservablePropertyDelegateProvider {
  fun <T> provide(initialValue: T): ReadWriteProperty<Any, T>
}
