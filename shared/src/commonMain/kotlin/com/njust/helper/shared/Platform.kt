package com.njust.helper.shared

expect class Platform() {
  val platform: String
}

expect annotation class Keep()

expect fun currentThreadName(): String
