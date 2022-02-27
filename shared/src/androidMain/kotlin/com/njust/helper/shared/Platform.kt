package com.njust.helper.shared

actual class Platform actual constructor() {
  actual val platform: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

actual typealias Keep = androidx.annotation.Keep

actual fun currentThreadName(): String {
  return Thread.currentThread().name
}
