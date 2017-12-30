package com.njust.helper.tools

import android.os.Build
import com.njust.helper.BuildConfig
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object HttpClients {
    val globalOkHttpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor {
                val request = it.request().newBuilder()
                        .addHeader("njusthelper", (BuildConfig.VERSION_CODE).toString())
                        .addHeader("sdk", (Build.VERSION.SDK_INT).toString())
                        .addHeader("phone", "${Build.MANUFACTURER} ${Build.MODEL}")
                        .build()
                it.proceed(request)
            }
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
}
