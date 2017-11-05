package com.njust.helper.retrofit

import com.njust.helper.BuildConfig
import retrofit2.Retrofit

object RetrofitFactory {
    @JvmField
    val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .build()
}
