package com.njust.helper.library.search

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.njust.helper.BR
import com.zwb.commonlibs.binding.ObservableDelegate
import com.zwb.commonlibs.binding.OnBindingItemClickListener

class LibSearchVm(
        val onItemClick: OnBindingItemClickListener<LibSearchItemVm>
) : BaseObservable() {
    val brId: Int = BR.vm

    @get:Bindable
    var data: List<LibSearchItemVm>? by ObservableDelegate(BR.data)
}
