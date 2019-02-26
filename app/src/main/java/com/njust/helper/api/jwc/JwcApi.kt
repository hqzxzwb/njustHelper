package com.njust.helper.api.jwc

import com.njust.helper.api.Apis
import com.njust.helper.api.LoginErrorException
import com.njust.helper.api.ServerErrorException
import com.njust.helper.api.parseReportingError
import com.zwb.commonlibs.rx.ioSubscribeUiObserve
import com.zwb.commonlibs.utils.MD5
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.HttpException
import retrofit2.http.*
import java.util.*

private interface JwcApiService {
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
            @Field("xnxq01id") body4: String = "2018-2019-2"
    ): Single<String>

    @GET("kscj/djkscj_list")
    fun gradeLevel(): Single<String>

    @GET("xsks/xsksap_query")
    fun exams1(): Single<String>

    @FormUrlEncoded
    @POST("xsks/xsksap_list")
    fun exams2(
            @Field("xnxqid") xq: String,
            @Field("xqlbmc") body1: String = "",
            @Field("xqlb") body2: String = ""
    ): Single<String>

    @FormUrlEncoded
    @POST("kscj/cjcx_list")
    fun grade(
            @Field("kksj") body1: String = "",
            @Field("kcxz") body2: String = "",
            @Field("kcmc") body3: String = "",
            @Field("xsfs") body4: String = "max"
    ): Single<String>
}

object JwcApi {
    private val service = Apis.newRetrofit("http://202.119.81.113:9080/njlgdx/")
            .create(JwcApiService::class.java)

    fun courses(stuid: String, pwd: String): Single<CourseData> {
        return login(stuid, pwd)
                .andThen(service.courses())
                .map { parseReportingError(it, ::parseCourses) }
                .ioSubscribeUiObserve()
    }

    fun gradeLevel(stuid: String, pwd: String): Single<List<GradeLevelBean>> {
        return login(stuid, pwd)
                .andThen(service.gradeLevel())
                .map { parseReportingError(it, ::parseGradeLevel) }
                .ioSubscribeUiObserve()
    }

    fun exams(stuid: String, pwd: String): Single<List<Exam>> {
        return login(stuid, pwd)
                .andThen(service.exams1())
                .flatMap { s ->
                    parseReportingError(s) {
                        val xqMatch = Regex("""<option selected value="(.*?)">""")
                                .find(it)
                        val xq = if (xqMatch != null) {
                            xqMatch.groupValues[0]
                        } else {
                            val calendar = Calendar.getInstance()
                            val year = calendar.get(Calendar.YEAR)
                            val month = calendar.get(Calendar.MONTH)
                            when {
                                month <= Calendar.MARCH -> "${year - 1}-$year-1"
                                month >= Calendar.NOVEMBER -> "$year-${year + 1}-1"
                                else -> "${year - 1}-$year-2"
                            }
                        }
                        service.exams2(xq)
                    }
                }
                .map { parseReportingError(it, ::parseExams) }
                .ioSubscribeUiObserve()
    }

    fun grade(stuid: String, pwd: String): Single<Map<String, List<GradeItem>>> {
        return login(stuid, pwd)
                .andThen(service.grade())
                .map { parseReportingError(it, ::parseGrade) }
                .ioSubscribeUiObserve()
    }

    private fun login(stuid: String, pwd: String): Completable {
        return service
                .requestLogin(stuid, MD5.md5String(pwd, true))
                .onErrorResumeNext {
                    if (it is HttpException) {
                        if (it.code() / 100 == 3) {
                            return@onErrorResumeNext Single.just("success")
                        }
                    }
                    Single.error(ServerErrorException())
                }
                .flatMapCompletable {
                    when {
                        it == "success" -> Completable.complete()
                        it.contains("<html xmlns=\"http://www.w3.org/1999/xhtml\">") ->
                            Completable.error(LoginErrorException())
                        else -> Completable.error(ServerErrorException())
                    }
                }
    }

    internal fun parseCourses(string: String): CourseData {
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
