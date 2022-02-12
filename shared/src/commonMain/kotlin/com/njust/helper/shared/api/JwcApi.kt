package com.njust.helper.shared.api

import com.njust.helper.shared.internal.HttpClientHolder.httpClient
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.CancellationException
import okio.ByteString.Companion.encodeUtf8

object JwcApi {
  private const val BASE_URL = "http://202.119.81.113:9080/njlgdx/"

  private suspend fun login(stuid: String, pwd: String) {
    val string = try {
      httpClient.get("${BASE_URL}xk/LoginToXk") {
        parameter("USERNAME", stuid)
        parameter("PASSWORD", pwd.encodeUtf8().md5().hex().uppercase())
        parameter("method", "verify")
      }
    } catch (e: Exception) {
      if (e is RedirectResponseException) {
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

  @Throws(ApiRelatedException::class, CancellationException::class)
  suspend fun gradeLevel(stuid: String, pwd: String): List<GradeLevelBean> {
    login(stuid, pwd)
    val html: String = httpClient.get("${BASE_URL}kscj/djkscj_list")
    return parseReportingError(html, ::parseGradeLevel)
  }

  private fun parseGradeLevel(string: String): List<GradeLevelBean> {
    fun convertScore(s: String) = if (s == "0") "--" else s

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

  @Throws(ApiRelatedException::class, CancellationException::class)
  suspend fun grade(stuid: String, pwd: String): Map<String, List<GradeItem>> {
    login(stuid, pwd)
    val html: String = httpClient.submitForm(
      url = "${BASE_URL}kscj/cjcx_list",
      formParameters = Parameters.build {
        append("kksj", "")
        append("kcxz", "")
        append("kcmc", "")
        append("xsfs", "max")
      },
    )
    return parseReportingError(html, ::parseGrade)
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

  suspend fun exams(stuid: String, pwd: String, termId: String): List<Exam> {
    login(stuid, pwd)
    val html: String = httpClient.submitForm(
      url = "${BASE_URL}xsks/xsksap_list",
      formParameters = Parameters.build {
        append("xnxqid", termId)
        append("xqlbmc", "")
        append("xqlb", "")
      }
    )
    return parseReportingError(html, ::parseExams)
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
          seat = groupValues[4],
        )
      }
  }
}
