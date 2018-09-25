package com.zwb.commonlibs.binding

import androidx.databinding.BindingAdapter
import android.view.View

object CommonBindingUtil {
    @BindingAdapter("visibleVsGone")
    @JvmStatic
    fun visibleVsGone(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @BindingAdapter("visibleVsInvisible")
    @JvmStatic
    fun visibleVsInvisible(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }
}
