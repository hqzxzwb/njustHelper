package com.njust.helper

import android.app.Application
import com.njust.helper.compose.composeObservablePropertyDelegateModule
import com.njust.helper.course.data.courseDatabaseModule
import com.njust.helper.okhttp.okHttpModule
import com.njust.helper.settings.CourseAlarms
import com.njust.helper.shared.koin.initSharedModule
import com.umeng.commonsdk.UMConfigure
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
  override fun onCreate() {
    super.onCreate()

    startKoin {
      androidContext(this@App)
      initSharedModule()
      modules(
        composeObservablePropertyDelegateModule,
        courseDatabaseModule,
        okHttpModule,
      )
    }

    CourseAlarms.registerCourseAlarm(this)

    UMConfigure.init(this, getString(R.string.umengAppKey), null, 0, null)
  }
}
