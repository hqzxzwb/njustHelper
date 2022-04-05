package com.njust.helper.shared.api

sealed class BookState

class LibDetailItem(
  val code: String,
  val location: String,
  val state: String,
): BookState()

class UnavailableItem(
  val message: String,
): BookState()

class LibDetailData(
  val states: List<BookState>,
  val head: String?,
)
