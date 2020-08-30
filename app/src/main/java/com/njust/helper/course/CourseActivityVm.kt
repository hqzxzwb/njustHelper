package com.njust.helper.course

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.njust.helper.BR
import com.zwb.commonlibs.binding.BooleanObservableDelegate
import com.zwb.commonlibs.binding.IntObservableDelegate
import com.zwb.commonlibs.binding.ObservableDelegate

class CourseActivityVm : BaseObservable() {
  var dayView by BooleanObservableDelegate(BR.dayView, true)
    @Bindable get
  var bottomText by ObservableDelegate(BR.bottomText, "")
    @Bindable get
  var displayingWeek by IntObservableDelegate(BR.displayingWeek)
    @Bindable get
  var clickHandler: CourseActivityClickHandler? = null
}

interface CourseActivityClickHandler {
  fun weekBefore()

  fun weekAfter()

  fun pickWeek()

  fun pickDate()

  fun toToday()
}
