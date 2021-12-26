package com.zwb.commonlibs.utils

object NoOpFunction :
    () -> Unit,
    (Any?) -> Unit {
  override fun invoke() {
  }

  override fun invoke(p1: Any?) {
  }
}
