package com.njust.helper.okhttp

import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import org.koin.core.module.Module
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

fun Module.injectOkHttp() {
  single {
    val userAgent = System.getProperty("http.agent")
      ?.filterNot { c -> (c <= '\u001f' && c != '\t') || c >= '\u007f' }
    val cookieManager = CookieManager()
    cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
    val cookieJar = JavaNetCookieJar(cookieManager)
    OkHttpClient.Builder()
      .cookieJar(cookieJar)
      .followRedirects(false)
      .connectTimeout(10, TimeUnit.SECONDS)
      .readTimeout(10, TimeUnit.SECONDS)
      .writeTimeout(10, TimeUnit.SECONDS)
      .addInterceptor { chain ->
        chain.request()
          .newBuilder()
          .also { if (userAgent != null) it.addHeader("User-Agent", userAgent) }
          .build()
          .let { chain.proceed(it) }
      }
      .build()
  }
}
