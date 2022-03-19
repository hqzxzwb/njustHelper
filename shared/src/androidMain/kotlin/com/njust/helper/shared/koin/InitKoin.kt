package com.njust.helper.shared.koin

import com.njust.helper.shared.links.linksViewModelModule
import org.koin.core.KoinApplication

fun KoinApplication.initSharedModule() {
  modules(
    linksViewModelModule,
  )
}
