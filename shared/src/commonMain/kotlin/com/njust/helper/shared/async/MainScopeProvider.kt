package com.njust.helper.shared.async

import com.futuremind.koruksp.ExportedScopeProvider
import com.futuremind.koruksp.ScopeProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

private val mainScope = CoroutineScope(Dispatchers.Main)

@ExportedScopeProvider
class MainScopeProvider : ScopeProvider {
  override val scope: CoroutineScope
    get() = mainScope
}
