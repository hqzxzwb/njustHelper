package com.njust.helper.update

import com.njust.helper.model.UpdateInfo
import com.njust.helper.tools.Apis
import com.njust.helper.tools.JsonData
import com.zwb.commonlibs.utils.JsonUtils
import io.reactivex.Observable
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.http.GET
import java.lang.reflect.Type

interface UpdateApi {
    @GET("update_info.php")
    fun checkUpdate(): Observable<JsonData<UpdateInfo>>

    companion object {
        val INSTANCE = Apis.newRetrofitBuilder()
                .addConverterFactory(object : Converter.Factory() {
                    override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
                        return Converter<ResponseBody, JsonData<UpdateInfo>> { it ->
                            object : JsonData<UpdateInfo>(it.string()) {
                                @Throws(Exception::class)
                                override fun parseData(jsonObject: JSONObject): UpdateInfo {
                                    return JsonUtils.parseBean(jsonObject, UpdateInfo::class.java)
                                }
                            }
                        }
                    }
                })
                .build()
                .create(UpdateApi::class.java)!!
    }
}
