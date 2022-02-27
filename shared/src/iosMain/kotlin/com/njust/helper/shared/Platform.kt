package com.njust.helper.shared

import okio.FileSystem
import platform.UIKit.UIDevice

actual class Platform actual constructor() {
    actual val platform: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

expect val FileSystem.Companion.IOS_SYSTEM: FileSystem

actual annotation class Keep
