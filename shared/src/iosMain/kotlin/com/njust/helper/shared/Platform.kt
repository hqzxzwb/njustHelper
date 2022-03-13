package com.njust.helper.shared

import okio.FileSystem
import org.koin.core.context.startKoin
import platform.Foundation.NSThread
import platform.UIKit.UIDevice

actual class Platform actual constructor() {
    actual val platform: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

expect val FileSystem.Companion.IOS_SYSTEM: FileSystem

actual annotation class Keep

actual fun currentThreadName(): String {
    return NSThread.currentThread.let { it.name ?: it.toString() }
}

class IosConfiguration {
}

fun startIos(configuration: IosConfiguration) {
    startKoin {
    }
}
