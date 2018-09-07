package com.njust.helper.api.library

import android.support.annotation.Keep

@Keep
class LibDetailItem(
        val code: String,
        val place: String,
        val state: String
)

class LibDetailData {
    var states: List<LibDetailItem>? = null
    var head: String? = null
}
