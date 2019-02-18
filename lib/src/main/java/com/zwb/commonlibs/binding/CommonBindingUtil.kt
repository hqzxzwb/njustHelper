package com.zwb.commonlibs.binding

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("visibleVsGone")
fun View.visibleVsGone(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("visibleVsInvisible")
fun View.visibleVsInvisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.INVISIBLE
}
