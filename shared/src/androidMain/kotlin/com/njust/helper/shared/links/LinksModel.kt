package com.njust.helper.shared.links

import com.njust.helper.shared.api.CommonLink
import com.njust.helper.shared.bizmodel.observableProperty
import org.koin.dsl.module

val linksViewModelModule = module {
  single<LinksViewModel> {
    object : LinksViewModel() {
      override var items: List<CommonLink> by observableProperty(listOf())
      override var loading: Boolean by observableProperty(false)
    }
  }
}
