package com.njust.helper.grade

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.njust.helper.BR
import com.zwb.commonlibs.binding.BooleanObservableDelegate
import com.zwb.commonlibs.binding.ObservableDelegate

class GradeLevelVm : BaseObservable() {
    val brId: Int = BR.vm

    var loading by BooleanObservableDelegate(BR.loading)
        @Bindable get

    var items by ObservableDelegate(BR.items, emptyList<GradeLevelBean>())
        @Bindable get
}
