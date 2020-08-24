package com.njust.helper.api.jwc

import com.njust.helper.RemoteConfig
import com.njust.helper.api.Apis
import com.njust.helper.api.LoginErrorException
import com.njust.helper.api.ServerErrorException
import com.njust.helper.api.parseReportingError
import com.zwb.commonlibs.utils.MD5
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.http.*

private interface JwcApiService {
    @GET("xk/LoginToXk")
    fun requestLoginAsync(
            @Query("USERNAME") stuid: String,
            @Query("PASSWORD") pwd: String,
            @Query("method") method: String = "verify"
    ): Deferred<String>

    @FormUrlEncoded
    @POST("xskb/xskb_list.do")
    fun coursesAsync(
            @Field("xnxq01id") body4: String,
            @Query("Ves632DSdyV") query1: String = "NEW_XSD_PYGL",
            @Field("cj0701id") body1: String = "",
            @Field("zc") body2: String = "",
            @Field("demo") body3: String = ""
    ): Deferred<String>

    @GET("kscj/djkscj_list")
    fun gradeLevelAsync(): Deferred<String>

    @FormUrlEncoded
    @POST("xsks/xsksap_list")
    fun examsAsync(
            @Field("xnxqid") xq: String,
            @Field("xqlbmc") body1: String = "",
            @Field("xqlb") body2: String = ""
    ): Deferred<String>

    @FormUrlEncoded
    @POST("kscj/cjcx_list")
    fun gradeAsync(
            @Field("kksj") body1: String = "",
            @Field("kcxz") body2: String = "",
            @Field("kcmc") body3: String = "",
            @Field("xsfs") body4: String = "max"
    ): Deferred<String>
}

object JwcApi {
    private val service = Apis.newRetrofit("http://202.119.81.113:9080/njlgdx/")
            .create(JwcApiService::class.java)

    suspend fun courses(stuid: String, pwd: String): CourseData = withContext(Dispatchers.IO) {
        login(stuid, pwd)
        try{
            parseReportingError(service.coursesAsync(RemoteConfig.getTermId()).await(), ::parseCourses)
        }catch (e:Exception){
            throw LoginErrorException()
        }
    }

    suspend fun gradeLevel(stuid: String, pwd: String): List<GradeLevelBean> = withContext(Dispatchers.IO) {
        login(stuid, pwd)
        parseReportingError(service.gradeLevelAsync().await(), ::parseGradeLevel)
    }

    suspend fun exams(stuid: String, pwd: String): List<Exam> = withContext(Dispatchers.IO) {
        login(stuid, pwd)
        try{
            parseReportingError(service.examsAsync(RemoteConfig.getTermId()).await(), ::parseExams)
        }catch (e:Exception){
            throw LoginErrorException()
        }
    }

    suspend fun grade(stuid: String, pwd: String): Map<String, List<GradeItem>> = withContext(Dispatchers.IO) {
        login(stuid, pwd)
        parseReportingError(service.gradeAsync().await(), ::parseGrade)
    }

    private suspend fun login(stuid: String, pwd: String) {
        val string = try {
            service.requestLoginAsync(stuid, MD5.md5String(pwd, true)).await()
        } catch (e: Exception) {
            if (e is HttpException && e.code() / 100 == 3) {
                "success"
            } else {
                throw ServerErrorException()
            }
        }
        if (string.contains("<html xmlns=\"http://www.w3.org/1999/xhtml\">")) {
            throw LoginErrorException()
        } else if (string != "success") {
            throw ServerErrorException()
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
                    courseInfo.id = MD5.md5String(courseInfo.name + courseInfo.teacher)
                    courseMap[courseInfo.id] = courseInfo
                    loc.id = courseInfo.id
                    weekRegex.find(item)
                            .let { if (it == null) "1(周)" else it.groupValues[1] }
                            .let {
                                loc.week1 = it
                                loc.week2 = analyseWeek(it)
                            }
                    loc.classroom = classroomRegex.find(item)
                            .let { if (it == null) "" else it.groupValues[1] }
                    locList.add(loc.clone())
                }
            }
        }
        val courseData = CourseData()
        courseData.infos = courseMap.map { it.value }
        courseData.locs = locList
        courseData.startdate = "2019-02-25"
        return courseData
    }

    private fun analyseWeek(string: String): String {
        val weeks = arrayListOf<Int>()
        val groupValues = Regex("""^([\d- ,]*)\(([单双]?)周\)$""")
                .find(string)!!
                .groupValues
        for (s in groupValues[1].split(',')) {
            val trimmed = s.trim()
            val j = trimmed.indexOf('-')
            if (j == -1) {
                weeks += trimmed.toInt()
            } else {
                var k = trimmed.substring(0, j).toInt()
                while (k <= trimmed.substring(j + 1).toInt()) {
                    weeks += k
                    k++
                }
            }
        }
        val indexFilter = groupValues[2]
        when (indexFilter) {
            "单" -> weeks.retainAll { it % 2 == 1 }
            "双" -> weeks.retainAll { it % 2 == 0 }
        }
        return weeks.joinToString(separator = " ", prefix = " ", postfix = " ")
    }

    private fun convertScore(s: String) = if (s == "0") "--" else s

    private fun parseGradeLevel(string: String): List<GradeLevelBean> {
        return Regex("""<td align="left">(.*)</td>\s*<td>(.*)</td>\s*<td>(.*)</td>\s*<td>(.*)</td>\s*<td>(.*)</td>\s*<td>(.*)</td>\s*<td>(.*)</td>\s*<td>(.*)</td>""")
                .findAll(string)
                .mapTo(arrayListOf()) {
                    val groupValues = it.groupValues
                    GradeLevelBean(
                            courseName = groupValues[1],
                            writtenPartScore = convertScore(groupValues[2]),
                            computerPartScore = convertScore(groupValues[3]),
                            totalScore = groupValues[4],
                            time = groupValues[8]
                    )
                }
    }

    private fun parseExams(string: String): List<Exam> {
        return Regex("""<table id="dataList"[\s\S]*?</table>""")
                .find(string)!!
                .groupValues[0]
                .let {
                    Regex("""<td.*?>(.*)</td>\s*<td>(.*?)</td>\s*<td>(.*?)</td>\s*<td>(.*?)</td>\s*</tr>""")
                            .findAll(it)
                }
                .mapTo(arrayListOf()) {
                    val groupValues = it.groupValues
                    Exam(
                            course = groupValues[1],
                            time = groupValues[2],
                            room = groupValues[3],
                            seat = groupValues[4]
                    )
                }
    }

    private fun parseGrade(string: String): Map<String, List<GradeItem>> {
        val table = Regex("""<table id="dataList"[\s\S]*?</table>""")
                .find(string)
                ?: return emptyMap()
        val tdRegex = Regex("""<td.*?>(.*?)</td>""")
        return Regex("""<tr>(\s*<td.*)+""")
                .findAll(table.groupValues[0])
                .map {
                    val groupValues = tdRegex.findAll(it.groupValues[0]).toList()
                    val gradeText = groupValues[4].groupValues[1]
                    GradeItem(
                            termName = groupValues[1].groupValues[1],
                            courseName = groupValues[3].groupValues[1],
                            weight = groupValues[6].groupValues[1].toDouble(),
                            gradeText = gradeText,
                            grade = gradeTextToDouble(gradeText),
                            type = groupValues[9].groupValues[1]
                    )
                }
                .groupBy { it.termName }
    }

    private fun gradeTextToDouble(s: String): Double {
        return when (s) {
            "优秀" -> 90.0
            "良好" -> 80.0
            "中等" -> 70.0
            "合格", "及格", "通过" -> 60.0
            "不通过", "不及格", "不合格" -> 50.0
            "免修" -> 89.0
            "请评教" -> -1.0
            else -> s.toDouble()
        }
    }
}
