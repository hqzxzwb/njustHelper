package com.njust.helper.shared

import com.njust.helper.shared.KMMBootProvider.Companion.app
import okio.BufferedSource
import okio.buffer
import okio.source

actual class Platform actual constructor() {
  actual val platform: String = "Android ${android.os.Build.VERSION.SDK_INT}"

  actual fun openAsset(name: String): BufferedSource {
    return app.resources.assets.open(name).source().buffer()
  }
}

actual typealias Keep = androidx.annotation.Keep
