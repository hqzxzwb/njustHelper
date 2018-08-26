package com.njust.helper.library.borrowed

import com.njust.helper.tools.Apis
import com.njust.helper.tools.JsonData
import io.reactivex.Single
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.lang.reflect.Type

interface BorrowedBooksApi {
    @FormUrlEncoded
    @POST("libBorrow.php")
    fun borrowedBooks(
            @Field("stuid") stuid: String,
            @Field("pwd") pwd: String
    ): Single<JsonData<String>>

    companion object {
        val INSTANCE: BorrowedBooksApi = Apis.newRetrofitBuilder()
                .addConverterFactory(object : Converter.Factory() {
                    override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
                        return Converter<ResponseBody, JsonData<String>> { it ->
                            object : JsonData<String>(it.string()) {
                                @Throws(Exception::class)
                                override fun parseData(jsonObject: JSONObject): String {
                                    return jsonObject.getString("content")
                                }
                            }
                        }
                    }
                })
                .build()
                .create(BorrowedBooksApi::class.java)
    }
}
