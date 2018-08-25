package com.njust.helper.course

import com.njust.helper.model.CourseData
import com.njust.helper.model.CourseInfo
import com.njust.helper.model.CourseLoc
import com.njust.helper.tools.LoginErrorException
import com.njust.helper.tools.ServerErrorException
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.net.CookieManager
import java.net.CookiePolicy
import java.security.MessageDigest

interface CourseApi {
    @GET("njlgdx/xk/LoginToXk")
    fun requestLogin(
            @Query("USERNAME") stuid: String,
            @Query("PASSWORD") pwd: String,
            @Query("method") method: String = "verify"
    ): Single<String>

    @FormUrlEncoded
    @POST("njlgdx/xskb/xskb_list.do")
    fun courses(
            @Query("Ves632DSdyV") query1: String = "NEW_XSD_PYGL",
            @Field("cj0701id") body1: String = "",
            @Field("zc") body2: String = "",
            @Field("demo") body3: String = "",
            @Field("xnxq01id") body4: String = "2018-2019-1"
    ): Single<String>

    companion object {
        private val API = run {
            val cookieManager = CookieManager()
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
            val cookieJar = JavaNetCookieJar(cookieManager)
            val client = OkHttpClient.Builder()
                    .cookieJar(cookieJar)
                    .followRedirects(false)
                    .build()
            Retrofit.Builder()
                    .baseUrl("http://202.119.81.113:9080/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build()
                    .create(CourseApi::class.java)!!
        }

        fun get(stuid: String, pwd: String): Single<CourseData> {
            return API
                    .requestLogin(stuid, md5(pwd).toUpperCase())
                    .subscribeOn(Schedulers.io())
                    .mapLogin()
                    .flatMap { API.courses() }
                    .map { mapParse(it) }
                    .observeOn(AndroidSchedulers.mainThread())
        }

        private fun Single<String>.mapLogin(): Single<Unit> {
            return this
                    .onErrorResumeNext {
                        if (it is HttpException) {
                            if (it.code() / 100 == 3) {
                                return@onErrorResumeNext Single.just("success")
                            }
                        }
                        Single.error(ServerErrorException())
                    }
                    .map<Unit> {
                        when {
                            it == "success" -> Unit
                            it.contains("<html xmlns=\"http://www.w3.org/1999/xhtml\">") -> throw LoginErrorException()
                            else -> throw ServerErrorException()
                        }
                    }
        }

        private fun mapParse(string: String): CourseData {
            val tableRegex = Regex("""<table id="kbtable"[\s\S]*?</table>""")
            val trRegex = Regex("""<tr>[\s\S]*?</tr>""")
            val tdRegex = Regex("""<td[\s\S]*?</td>""")
            val divRegex = Regex("""<div id=".*-2".*</div>""")
            val brRegex = Regex(""">(.*?)<br/>""")
            val teacherRegex = Regex("""'老师'>(.*?)</font>""")
            val weekRegex = Regex("""'周次\(节次\)'>(.*?)</font>""")
            val classroomRegex = Regex("""'教室'>(.*?)</font>""")

            val table = tableRegex.find(string)!!.groupValues[0]
            val sectionRegex = """第[一二三四五]大节""".toRegex()
            val result1 = trRegex.findAll(table)
                    .mapNotNullTo(arrayListOf()) { match ->
                        match.groupValues[0].takeIf { it.contains(sectionRegex) }
                    }
            val locList = arrayListOf<CourseLoc>()
            val courseMap = hashMapOf<String, CourseInfo>()
            for (timeOfDay in 0 until 5) {
                val loc = CourseLoc()
                loc.sec1 = timeOfDay
                loc.sec2 = timeOfDay
                val match1 = tdRegex.findAll(result1[timeOfDay]).toList()
                for (dayOfWeek in 0 until 7) {
                    loc.day = dayOfWeek
                    val match2 = divRegex
                            .find(match1[dayOfWeek].groupValues[0])!!
                            .groupValues[0]
                            .split("-----")
                    for (item in match2) {
                        val courseInfo: CourseInfo
                        val find = brRegex.find(item)
                        if (find == null) {
                            continue
                        } else {
                            courseInfo = CourseInfo()
                            courseInfo.name = find.groupValues[1]
                        }
                        courseInfo.teacher = run {
                            val result = teacherRegex.find(item) ?: return@run ""
                            result.groupValues[1]
                        }
                        courseInfo.id = md5(courseInfo.name + courseInfo.teacher)
                        courseMap[courseInfo.id] = courseInfo
                        loc.id = courseInfo.id
                        run {
                            val result = weekRegex.find(item) ?: return@run "1(周)"
                            result.groupValues[1]
                        }
                                .also { loc.week1 = it }
                                .let { loc.week2 = analyseWeek(it) }
                        loc.classroom = run {
                            val result = classroomRegex.find(item) ?: return@run ""
                            result.groupValues[1]
                        }
                        locList.add(loc.clone())
                    }
                }
            }
            val courseData = CourseData()
            courseData.infos = courseMap.map { it.value }
            courseData.locs = locList
            courseData.startdate = "2018-08-27"
            return courseData
        }

        private fun md5(s: String): String {
            val instance = MessageDigest.getInstance("MD5")
            val digest: ByteArray = instance.digest(s.toByteArray())
            val sb = StringBuilder()
            for (b in digest) {
                val i: Int = b.toInt() and 0xff
                var hexString = Integer.toHexString(i)
                if (hexString.length < 2) {
                    hexString = "0$hexString"
                }
                sb.append(hexString)
            }
            return sb.toString()
        }

        private fun analyseWeek(string: String): String {
            val builder = StringBuilder(" ")
            val strings = Regex("""(.*)\(周\)""")
                    .find(string)!!
                    .groupValues[1]
                    .split(",")
            for (tstring in strings) {
                val j = tstring.indexOf('-')
                if (j == -1) {
                    builder.append(tstring).append(' ')
                } else {
                    var k = tstring.substring(0, j).toInt()
                    while (k <= tstring.substring(j + 1).toInt()) {
                        builder.append(k).append(' ')
                        k++
                    }
                }
            }
            return builder.toString()
        }
    }
}
