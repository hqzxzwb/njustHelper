package com.njust.helper.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Link(
    val name: String,
    val url: String
)
