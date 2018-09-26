package com.njust.helper.api.common

import com.njust.helper.model.Link
import com.njust.helper.api.Apis
import com.njust.helper.tools.JsonData
import com.zwb.commonlibs.rx.ioSubscribeUiObserve
import io.reactivex.Single
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

object CommonApi {
    private interface CommonApiService {
        @GET("links.php")
        fun links(): Single<JsonData<List<Link>>>

        @FormUrlEncoded
        @POST("libBorrow.php")
        fun borrowedBooks(
                @Field("stuid") stuid: String,
                @Field("pwd") pwd: String
        ): Single<JsonData<String>>
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

    fun borrowedBooks(stuid: String, pwd: String): Single<JsonData<String>> {
        return SERVICE.borrowedBooks(stuid, pwd)
                .ioSubscribeUiObserve()
    }
}
