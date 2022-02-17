package com.njust.helper.shared

import okio.FileSystem

actual val FileSystem.Companion.IOS_SYSTEM: FileSystem
    get() = SYSTEM
