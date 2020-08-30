package com.njust.helper.main

import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.njust.helper.BR
import com.zwb.commonlibs.binding.ObservableDelegate

class MainViewModel(val clickHandler: MainActivityClickHandler) : BaseObservable() {
  var courses: List<String>? by ObservableDelegate(BR.courses)
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
