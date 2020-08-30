package com.njust.helper.tools

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.njust.helper.BR
import com.zwb.commonlibs.binding.BooleanObservableDelegate
import com.zwb.commonlibs.binding.ObservableDelegate
import com.zwb.commonlibs.binding.OnBindingItemClickListener

class SimpleListVm<T> : BaseObservable() {
  val brId: Int = BR.vm

  var loading by BooleanObservableDelegate(BR.loading)
    @Bindable get

  var items by ObservableDelegate(BR.items, emptyList<T>())
    @Bindable get

  var listener: OnBindingItemClickListener<T>? by ObservableDelegate(BR.listener)
    @Bindable get
}
