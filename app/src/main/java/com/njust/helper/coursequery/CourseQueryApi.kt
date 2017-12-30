package com.njust.helper.coursequery

import com.njust.helper.model.CourseQuery
import com.njust.helper.tools.Apis
import com.njust.helper.tools.JsonData
import com.zwb.commonlibs.utils.JsonUtils
import io.reactivex.Observable
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.lang.reflect.Type

interface CourseQueryApi {
    @FormUrlEncoded
    @POST("course_query.php")
    fun queryCourse(
            @Field("section") section: Int,
            @Field("day") day: Int,
            @Field("name") name: String,
            @Field("teacher") teacher: String
    ): Observable<JsonData<List<CourseQuery>>>

    companion object {
        val INSTANCE: CourseQueryApi = Apis.newRetrofitBuilder()
                .addConverterFactory(object : Converter.Factory() {
                    override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
                        return Converter<ResponseBody, JsonData<List<CourseQuery>>> { it ->
                            object : JsonData<List<CourseQuery>>(it.string()) {
                                @Throws(Exception::class)
                                override fun parseData(jsonObject: JSONObject): List<CourseQuery> {
                                    return JsonUtils.parseArray(jsonObject.getJSONArray("content"), CourseQuery::class.java)
                                }
                            }
                        }
                    }
                })
                .build()
                .create(CourseQueryApi::class.java)
    }
}
