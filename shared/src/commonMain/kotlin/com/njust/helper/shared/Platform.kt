package com.njust.helper.shared

import okio.BufferedSource

expect class Platform() {
  val platform: String

  fun openAsset(name: String): BufferedSource
}

expect annotation class Keep()
