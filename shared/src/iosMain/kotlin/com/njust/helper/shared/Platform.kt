package com.njust.helper.shared

import okio.BufferedSource
import okio.FileNotFoundException
import okio.FileSystem
import okio.IOException
import okio.Path.Companion.toPath
import okio.buffer
import platform.Foundation.NSBundle
import platform.UIKit.UIDevice

actual class Platform actual constructor() {
    actual val platform: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion

    @Throws(IOException::class)
    actual fun openAsset(name: String): BufferedSource {
        val path = NSBundle.mainBundle.pathForResource(name, null)?.toPath()
            ?: throw FileNotFoundException()
        return FileSystem.IOS_SYSTEM.source(path).buffer()
    }
}

expect val FileSystem.Companion.IOS_SYSTEM: FileSystem

actual annotation class Keep
