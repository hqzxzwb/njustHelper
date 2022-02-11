package com.njust.helper.shared.api

import com.njust.helper.shared.internal.httpClient
import com.njust.helper.shared.internal.jsonParser
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

object LibraryApi {
  private const val BASE_URL_1 = "http://202.119.83.14:8080/uopac/opac/"
  private const val BASE_URL_2 = "http://mc.m.5read.com/"

  @Throws(ApiRelatedException::class, CancellationException::class)
  suspend fun search(keyword: String): List<LibSearchBean> {
    val body = buildJsonObject {
      put("sortField", "relevance")
      put("sortType", "desc")
      put("pageSize", 100)
      put("pageCount", 1)
      put("locale", "")
      put("first", true)
      put("filters", buildJsonArray { })
      put("limiters", buildJsonArray { })
      put("searchWords", buildJsonArray {
        add(
          buildJsonObject {
            put("fieldList", buildJsonArray {
              add(
                buildJsonObject {
                  put("fieldCode", "")
                  put("fieldValue", keyword)
                }
              )
            })
          }
        )
      })
    }

    val json: String = httpClient.post("${BASE_URL_1}ajax_search_adv.php") {
      contentType(ContentType.Application.Json)
      this.body = body
    }
    parseReportingError(json) {
      val jsonTree = jsonParser.decodeFromString<JsonObject>(json)
      return jsonTree["content"]!!.jsonArray.map {
        val obj = it.jsonObject
        LibSearchBean(
          title = obj["title"]?.jsonPrimitive?.content.orEmpty(),
          author = obj["author"]?.jsonPrimitive?.content.orEmpty(),
          press = obj["publisher"]?.jsonPrimitive?.content.orEmpty(),
          id = obj["marcRecNo"]?.jsonPrimitive?.content.orEmpty(),
        )
      }
    }
  }

  @Throws(ApiRelatedException::class, CancellationException::class)
  suspend fun detail(id: String): LibDetailData {
    val text = httpClient.get<String>("${BASE_URL_1}item.php") {
      parameter("marc_no", id)
    }
    return parseReportingError(text, ::parseDetail)
  }

  private fun parseDetail(string: String): LibDetailData {
    val headerBuilder = StringBuilder()
    Regex("""<dt>题名/责任者:</dt>\s*<dd>(.*?)</dd>""")
      .find(string)
      ?.let {
        headerBuilder.append("题名/责任者:\n")
          .append(trimHtmlString(it.groupValues[1]))
          .append("\n\n")
      }
    Regex("""<dt>出版发行项:</dt>\s*<dd>(.*?)</dd>""")
      .find(string)
      ?.let {
        headerBuilder.append("出版发行项:\n")
          .append(trimHtmlString(it.groupValues[1]))
          .append("\n\n")
      }
    Regex("""<dt>ISBN及定价:</dt>\s*<dd>(.*?)</dd>""")
      .find(string)
      ?.let {
        headerBuilder.append("ISBN及定价:\n")
          .append(trimHtmlString(it.groupValues[1]))
          .append("\n\n")
      }
    Regex("""<dt>提要文摘附注:</dt>\s*<dd>(.*?)</dd>""")
      .find(string)
      ?.let {
        headerBuilder.append("提要文摘附注:\n")
          .append(trimHtmlString(it.groupValues[1]))
          .append("\n\n")
      }

    val stateList = arrayListOf<LibDetailItem>()
    val matches1 = Regex("""<tr align="left" class="whitetext"[\s\S]*?</tr>""")
      .findAll(string)
    val tdRegex = Regex("""<td.*?>[\s ]*([\s\S]*?)[\s ]*</td>""")
    matches1.forEach {
      val matches2 = tdRegex.findAll(it.groupValues[0]).toList()
      stateList += if (matches2.size >= 5) {
        LibDetailItem(
          code = trimHtmlString(matches2[0].groupValues[1]),
          place = trimHtmlString(matches2[3].groupValues[1]),
          state = matches2[4].groupValues[1].replace("应还日期", "应还")
        )
      } else {
        LibDetailItem(
          code = matches2[0].groupValues[1],
          place = "",
          state = ""
        )
      }
    }

    return LibDetailData(stateList, headerBuilder.toString())
  }

  private fun trimHtmlString(input: String): String {
    return input
      .replace(Regex("&#x([0-9a-fA-F]*);")) {
        it.groupValues[1].toInt(16).toChar().toString()
      }
      .replace("&nbsp;", " ")
      .replace(Regex("</?a[^<>]*>"), "")
      .trim()
  }

  private suspend fun borrowed1(
    stuid: String,
    pwd: String,
  ): String {
    return httpClient.get("${BASE_URL_2}apis/user/userLogin.jspx") {
      parameter("username", stuid)
      parameter("password", pwd)
      parameter("areaid", "274")
      parameter("schoolid", "528")
      parameter("userType", "0")
      parameter("encPwd", "0")
    }
  }

  private suspend fun borrowed2(): String {
    return httpClient.get("${BASE_URL_2}api/opac/showOpacLink.jspx?newSign")
  }

  @Throws(ApiRelatedException::class, CancellationException::class)
  suspend fun borrowed(stuid: String, pwd: String): String {
    return borrowed1(stuid, pwd)
      .let { s ->
        val o = parseReportingError(s) { jsonParser.decodeFromString<JsonObject>(s) }
        if (o["result"] != JsonPrimitive(1)) {
          throw LoginErrorException()
        } else {
          borrowed2()
        }
      }
      .let { s ->
        parseReportingError(s) {
          jsonParser.decodeFromString<JsonObject>(it)["opacUrl"]!!.jsonArray[0].jsonObject["opaclendurl"]!!.jsonPrimitive.content
        }
      }
  }
}
