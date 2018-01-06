package com.njust.helper.main

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.view.View
import com.njust.helper.BR

class MainViewModel(val clickHandler: MainActivityClickHandler) : BaseObservable() {
    var courses: List<String>? = null
        set (value) {
            field = value
            notifyPropertyChanged(BR.courses)
        }
        @Bindable get
}

interface MainActivityClickHandler {
    fun openLibBorrowActivity(view: View)

    fun openLibCollectionActivity(view: View)

    fun openLibSearchActivity(view: View)

    fun openCourseQueryActivity(view: View)

    fun openGradeLevelActivity(v: View)

    fun openLinksActivity(view: View)

    fun openCourseActivity(view: View)

    fun openClassroomActivity(view: View)

    fun openExamsActivity(view: View)

    fun openGradeActivity(v: View)
}
