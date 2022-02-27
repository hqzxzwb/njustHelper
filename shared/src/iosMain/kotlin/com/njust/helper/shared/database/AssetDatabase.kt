package com.njust.helper.shared.database

import com.njust.helper.shared.async.ioDispatcher
import dev.icerock.moko.resources.AssetResource
import kotlinx.coroutines.withContext
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.Foundation.URLByAppendingPathComponent

actual suspend fun prepareAssetDatabase(asset: AssetResource): Unit = withContext(ioDispatcher) {
  val assetUrl = asset.url
  val name = asset.fileName
  val documentsDirectory = NSFileManager.defaultManager.URLForDirectory(
    directory = NSDocumentDirectory,
    inDomain = NSUserDomainMask,
    appropriateForURL = null,
    create = false,
    error = null,
  )!!
  val destination = documentsDirectory.URLByAppendingPathComponent(name)!!
  if (destination.isFileURL()) {
    return@withContext
  }
  NSFileManager.defaultManager.copyItemAtURL(assetUrl, destination, null)
}
