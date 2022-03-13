package com.njust.helper.shared.api

import kotlinx.serialization.Serializable

@Serializable
data class CommonLink(
  val name: String,
  val url: String
)
