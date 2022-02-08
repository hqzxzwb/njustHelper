package com.njust.helper.shared.api

class LibDetailItem(
    val code: String,
    val place: String,
    val state: String
)

class LibDetailData(
  val states: List<LibDetailItem>?,
  val head: String?,
)
