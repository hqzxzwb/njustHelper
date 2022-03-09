package com.njust.helper.api

import com.njust.helper.BuildConfig
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object Apis : KoinComponent {
  fun newRetrofit(baseUrl: String = BuildConfig.BASE_URL): Retrofit {
    return Retrofit.Builder()
      .baseUrl(baseUrl)
      .client(get())
      .addConverterFactory(ScalarsConverterFactory.create())
      .addConverterFactory(MoshiConverterFactory.create())
      .build()
  }
}
