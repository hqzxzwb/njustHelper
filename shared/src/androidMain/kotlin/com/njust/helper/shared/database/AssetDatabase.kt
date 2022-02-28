package com.njust.helper.shared.database

import com.njust.helper.shared.AppContext
import dev.icerock.moko.resources.AssetResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream

actual suspend fun prepareAssetDatabase(
  asset: AssetResource,
  forceRewrite: Boolean
): Unit = withContext(Dispatchers.IO) {
  val path = asset.path
  val context = AppContext.appContext
  val destination = context.getDatabasePath(path)
  if (destination.isFile && !forceRewrite) {
    return@withContext
  }
  context.assets.open(path).use { inputStream ->
    FileOutputStream(context.getDatabasePath(path))
      .use { outputStream ->
        inputStream.copyTo(outputStream)
      }
  }
}
