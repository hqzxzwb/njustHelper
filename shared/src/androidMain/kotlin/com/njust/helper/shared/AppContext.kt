package com.njust.helper.shared

import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal object AppContext: KoinComponent {
  val appContext: Context by inject()
}
