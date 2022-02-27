package com.njust.helper.shared.database

import dev.icerock.moko.resources.AssetResource

expect suspend fun prepareAssetDatabase(asset: AssetResource)
