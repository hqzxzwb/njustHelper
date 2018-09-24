package com.njust.helper.api.common

import com.njust.helper.model.Link
import com.njust.helper.tools.Apis
import com.njust.helper.tools.JsonData
import com.zwb.commonlibs.rx.ioSubscribeUiObserve
import io.reactivex.Single
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

object CommonApi {
    private interface CommonApiService {
        @GET("links.php")
        fun links(): Single<JsonData<List<Link>>>
    }

    private val SERVICE = Apis.newRetrofitBuilder()
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CommonApiService::class.java)

    fun links(): Single<List<Link>> {
        return SERVICE.links()
                .map { it.content }
                .ioSubscribeUiObserve()
    }
}
