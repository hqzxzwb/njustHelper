package com.njust.helper.shared.api

import kotlinx.serialization.Serializable

@Serializable
data class Link(
  val name: String,
  val url: String
)
