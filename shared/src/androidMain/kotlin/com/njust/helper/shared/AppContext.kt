package com.njust.helper.shared

import android.content.Context
import com.njust.helper.shared.internal.ModuleComponent
import org.koin.core.component.inject

internal object AppContext {
  val appContext: Context by ModuleComponent.inject()
}
