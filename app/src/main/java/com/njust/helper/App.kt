package com.njust.helper

import android.app.Application
import com.njust.helper.course.data.courseDatabaseModule
import com.njust.helper.links.LinksViewModelImpl
import com.njust.helper.okhttp.okHttpModule
import com.njust.helper.settings.CourseAlarms
import com.njust.helper.shared.koin.initSharedModule
import com.njust.helper.shared.links.LinksViewModel
import com.umeng.commonsdk.UMConfigure
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

class App : Application() {
  override fun onCreate() {
    super.onCreate()

    startKoin {
      androidContext(this@App)
      initSharedModule()
      modules(
        courseDatabaseModule,
        okHttpModule,
        module {
          factoryOf<LinksViewModel>(::LinksViewModelImpl)
        }
      )
    }

    CourseAlarms.registerCourseAlarm(this)

    UMConfigure.init(this, getString(R.string.umengAppKey), null, 0, null)
  }
}
