package com.njust.helper.api.library

import com.njust.helper.api.Apis
import com.njust.helper.shared.api.LoginErrorException
import com.njust.helper.shared.api.parseReportingError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.http.GET
import retrofit2.http.Query

private interface LibraryApiService {
  @GET("http://mc.m.5read.com/apis/user/userLogin.jspx")
  suspend fun borrowed1(
      @Query("username") stuid: String,
      @Query("password") pwd: String,
      @Query("areaid") q1: String = "274",
      @Query("schoolid") q2: String = "528",
      @Query("userType") q3: String = "0",
      @Query("encPwd") q4: String = "0"
  ): String

  @GET("http://mc.m.5read.com/api/opac/showOpacLink.jspx?newSign")
  suspend fun borrowed2(): String
}

object LibraryApi {
  private val service = Apis.newRetrofit("http://202.119.83.14:8080/uopac/opac/")
      .create(LibraryApiService::class.java)

  suspend fun borrowed(stuid: String, pwd: String): String = withContext(Dispatchers.IO) {
    service.borrowed1(stuid, pwd)
        .let { s ->
          val o = parseReportingError(s) { JSONObject(s) }
          if (o.getInt("result") != 1) {
            throw LoginErrorException()
          } else {
            service.borrowed2()
          }
        }
        .let { s ->
          parseReportingError(s) {
            JSONObject(it)
                .getJSONArray("opacUrl")
                .getJSONObject(0)
                .getString("opaclendurl")
          }
        }
  }
}
