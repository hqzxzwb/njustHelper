package com.njust.helper.main

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.njust.helper.BR

class MainViewModel : BaseObservable() {
    var courses: List<String>? = null
        set (value) {
            field = value
            notifyPropertyChanged(BR.courses)
        }
        @Bindable get
}
