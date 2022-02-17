package com.njust.helper.shared

import dev.icerock.moko.resources.AssetResource

actual fun AssetResource.readText(): String {
  return readText(KMMBootProvider.app)
}
