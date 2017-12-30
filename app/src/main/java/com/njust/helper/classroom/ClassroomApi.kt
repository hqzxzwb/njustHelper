package com.njust.helper.classroom

import com.njust.helper.tools.Apis
import com.njust.helper.tools.JsonData
import io.reactivex.Observable
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.lang.reflect.Type

interface ClassroomApi {
    @FormUrlEncoded
    @POST("classroom.php")
    fun getClassrooms(
            @Field("date") data: String,
            @Field("building") building: String,
            @Field("timeofday") timeOfDay: Int
    ): Observable<JsonData<String>>

    companion object {
        val INSTANCE: ClassroomApi = Apis.newRetrofitBuilder()
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
                .create(ClassroomApi::class.java)
    }
}
