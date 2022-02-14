package com.njust.helper.shared

import okio.BufferedSource
import platform.UIKit.UIDevice

actual class Platform actual constructor() {
    actual val platform: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion

    actual fun openAsset(name: String): BufferedSource {
        TODO("Not yet implemented")
    }
}

actual annotation class Keep
