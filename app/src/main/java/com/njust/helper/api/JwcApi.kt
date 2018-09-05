package com.njust.helper.api

import com.njust.helper.grade.GradeLevelBean
import com.njust.helper.model.CourseData
import com.njust.helper.model.CourseInfo
import com.njust.helper.model.CourseLoc
import com.njust.helper.tools.Apis
import com.njust.helper.tools.LoginErrorException
import com.njust.helper.tools.ServerErrorException
import com.zwb.commonlibs.rx.ioSubscribeUiObserve
import io.reactivex.Single
import retrofit2.HttpException
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.security.MessageDigest

object JwcApi {
    private interface CourseApiService {
        @GET("xk/LoginToXk")
        fun requestLogin(
                @Query("USERNAME") stuid: String,
                @Query("PASSWORD") pwd: String,
                @Query("method") method: String = "verify"
        ): Single<String>

        @FormUrlEncoded
        @POST("xskb/xskb_list.do")
        fun courses(
                @Query("Ves632DSdyV") query1: String = "NEW_XSD_PYGL",
                @Field("cj0701id") body1: String = "",
                @Field("zc") body2: String = "",
                @Field("demo") body3: String = "",
                @Field("xnxq01id") body4: String = "2018-2019-1"
        ): Single<String>

        @GET("kscj/djkscj_list")
        fun gradeLevel(): Single<String>
    }

    private val service = Apis.newRetrofitBuilder()
            .baseUrl("http://202.119.81.113:9080/njlgdx/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(CourseApiService::class.java)!!

    fun courses(stuid: String, pwd: String): Single<CourseData> {
        return login(stuid, pwd)
                .flatMap { service.courses() }
                .map { parseCourses(it) }
                .ioSubscribeUiObserve()
    }

    fun gradeLevel(stuid: String, pwd: String): Single<List<GradeLevelBean>> {
        return login(stuid, pwd)
                .flatMap { service.gradeLevel() }
                .map { parseGradeLevel(it) }
                .ioSubscribeUiObserve()
    }

    private fun login(stuid: String, pwd: String): Single<Unit> {
        return service
                .requestLogin(stuid, md5(pwd).toUpperCase())
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
                        it.contains("<html xmlns=\"http://www.w3.org/1999/xhtml\">") ->
                            throw LoginErrorException()
                        else -> throw ServerErrorException()
                    }
                }
    }

    private fun parseCourses(string: String): CourseData {
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
                    weekRegex.find(item)
                            .let { if (it == null) "1(周)" else it.groupValues[1] }
                            .also { loc.week1 = it }
                            .let { loc.week2 = analyseWeek(it) }
                    loc.classroom = classroomRegex.find(item)
                            .let { if (it == null) "" else it.groupValues[1] }
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

    private fun parseGradeLevel(string: String): List<GradeLevelBean> {
        return Regex("""<td align="left">(.*)</td>\s*<td>(.*)</td>\s*<td>(.*)</td>\s*<td>(.*)</td>\s*<td>(.*)</td>\s*<td>(.*)</td>\s*<td>(.*)</td>\s*<td>(.*)</td>""")
                .findAll(string)
                .mapTo(arrayListOf()) {
                    val groupValues = it.groupValues
                    GradeLevelBean(
                            courseName = groupValues[1],
                            writtenPartScore = groupValues[2],
                            computerPartScore = groupValues[3],
                            totalScore = groupValues[4],
                            time = groupValues[8]
                    )
                }
    }
}
