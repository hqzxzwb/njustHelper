package com.njust.helper.shared

import okio.BufferedSource
import okio.IOException

expect class Platform() {
  val platform: String

  @Throws(IOException::class)
  fun openAsset(name: String): BufferedSource
}

expect annotation class Keep()
