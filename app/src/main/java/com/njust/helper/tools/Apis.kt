package com.njust.helper.tools

import android.os.Build
import com.njust.helper.BuildConfig
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

object Apis {
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

    fun newRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(globalOkHttpClient)
    }
}
