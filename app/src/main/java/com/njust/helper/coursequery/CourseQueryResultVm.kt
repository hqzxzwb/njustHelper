package com.njust.helper.coursequery

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.njust.helper.BR
import com.zwb.commonlibs.binding.BooleanObservableDelegate
import com.zwb.commonlibs.binding.ObservableDelegate

class CourseQueryResultVm : BaseObservable() {
    val brId: Int = BR.vm

    var loading by BooleanObservableDelegate(BR.loading)
        @Bindable get

    var items by ObservableDelegate(BR.items, emptyList<CourseQueryItemVm>())
        @Bindable get
}

class CourseQueryItemVm(val item: CourseQueryItem, val position: Int)
