package com.njust.helper.course

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.njust.helper.BR

class CourseActivityVm : BaseObservable() {
    var dayView = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.dayView)
        }
        @Bindable get
    var bottomText = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.bottomText)
        }
        @Bindable get
    var displayingWeek = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.displayingWeek)
        }
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
