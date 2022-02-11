package com.njust.helper.api.jwc

import com.njust.helper.RemoteConfig
import com.njust.helper.api.Apis
import com.njust.helper.shared.api.LoginErrorException
import com.njust.helper.shared.api.ServerErrorException
import com.njust.helper.shared.api.parseReportingError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.ByteString.Companion.encodeUtf8
import retrofit2.HttpException
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.IOException
import java.util.*

private interface JwcApiService {
  @GET("xk/LoginToXk")
  suspend fun requestLogin(
      @Query("USERNAME") stuid: String,
      @Query("PASSWORD") pwd: String,
      @Query("method") method: String = "verify"
  ): String

  @FormUrlEncoded
  @POST("xskb/xskb_list.do")
  suspend fun courses(
      @Query("Ves632DSdyV") query1: String = "NEW_XSD_PYGL",
      @Field("cj0701id") body1: String = "",
      @Field("zc") body2: String = "",
      @Field("demo") body3: String = "",
      @Field("xnxq01id") body4: String = RemoteConfig.getTermId()
  ): String

  @FormUrlEncoded
  @POST("xsks/xsksap_list")
  suspend fun exams(
      @Field("xnxqid") xq: String,
      @Field("xqlbmc") body1: String = "",
      @Field("xqlb") body2: String = ""
  ): String
}

object JwcApi {
  private val service = Apis.newRetrofit("http://202.119.81.113:9080/njlgdx/")
      .create(JwcApiService::class.java)

  suspend fun courses(stuid: String, pwd: String): CourseData = withContext(Dispatchers.IO) {
    login(stuid, pwd)
    parseReportingError(service.courses(), ::parseCourses)
  }

  suspend fun exams(stuid: String, pwd: String): List<Exam> = withContext(Dispatchers.IO) {
    login(stuid, pwd)
    parseReportingError(service.exams(RemoteConfig.getTermId()), ::parseExams)
  }

  private suspend fun login(stuid: String, pwd: String) {
    val string = try {
      service.requestLogin(stuid, pwd.encodeUtf8().md5().hex().uppercase(Locale.US))
    } catch (e: Exception) {
      if (e is HttpException && e.code() / 100 == 3) {
        "success"
      } else if (e is IOException) {
        throw e
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
          courseInfo.id = (courseInfo.name + courseInfo.teacher).encodeUtf8().md5().hex()
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
}
