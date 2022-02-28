package com.njust.helper.moko.resources

import android.content.Context
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun Flow<StringDesc>.mapToString(context: Context): Flow<String> {
  return map { it.toString(context) }
}
