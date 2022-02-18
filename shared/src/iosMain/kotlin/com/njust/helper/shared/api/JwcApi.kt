package com.njust.helper.shared.api

import com.njust.helper.shared.async.Cancellable
import com.njust.helper.shared.async.suspendToCancellable

fun JwcApi.gradeLevelIos(
  stuid: String,
  pwd: String,
  completionHandler: (result: List<GradeLevelBean>?, error: Throwable?) -> Unit,
): Cancellable {
  return suspendToCancellable(
    completionHandler
  ) { gradeLevel(stuid, pwd) }
}
