package com.njust.helper.shared.links

import com.njust.helper.shared.MR
import com.njust.helper.shared.api.Link
import com.njust.helper.shared.api.LinksApi
import com.njust.helper.shared.bizmodel.BizModel
import com.njust.helper.shared.bizmodel.observableProperty
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.MutableSharedFlow

class LinksModel : BizModel() {
  val vm: LinksViewModel = LinksViewModel()

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

class LinksViewModel {
  var items by observableProperty(listOf<Link>())
  var loading by observableProperty(false)
  val snackbarMessageFlow = MutableSharedFlow<StringDesc>()
}
