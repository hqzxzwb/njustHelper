package com.njust.helper.shared.links

import com.futuremind.koruksp.ToNativeClass
import com.njust.helper.shared.MR
import com.njust.helper.shared.api.CommonLink
import com.njust.helper.shared.api.LinksApi
import com.njust.helper.shared.async.MainScopeProvider
import com.njust.helper.shared.bizmodel.BizModel
import com.njust.helper.shared.internal.ModuleComponent
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.get

@ToNativeClass(launchOnScope = MainScopeProvider::class)
class LinksModel(val vm: LinksViewModel = ModuleComponent.get()) : BizModel() {
  suspend fun collectEvents() = coroutineScope {
    launch {
      vm.onRefreshAction.collectLatest {
        load()
      }
    }
  }

  suspend fun load() {
    try {
      vm.loading = true
      val data = LinksApi.links()
      vm.items = data
    } catch (e: Exception) {
      e.printStackTrace()
      vm.snackbarMessageFlow.emit(MR.strings.message_net_error.desc())
    }
    vm.loading = false
  }
}

abstract class LinksViewModel {
  abstract var items: List<CommonLink>
  abstract var loading: Boolean
  val snackbarMessageFlow = MutableSharedFlow<StringDesc>()
  val onRefreshAction = MutableSharedFlow<Unit>()
}
