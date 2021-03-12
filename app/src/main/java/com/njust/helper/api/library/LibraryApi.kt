package com.njust.helper.api.library

import androidx.collection.ArrayMap
import androidx.core.text.HtmlCompat
import com.njust.helper.api.Apis
import com.njust.helper.api.LoginErrorException
import com.njust.helper.api.parseReportingError
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.*

private interface LibraryApiService {
    @POST("ajax_search_adv.php")
    fun searchAsync(
        @Body body: Any
    ): Deferred<Map<String, Any>>

    @GET("item.php")
    fun detailAsync(
        @Query("marc_no") id: String
    ): Deferred<String>

    @GET("http://mc.m.5read.com/apis/user/userLogin.jspx")
    fun borrowed1Async(
        @Query("username") stuid: String,
        @Query("password") pwd: String,
        @Query("areaid") q1: String = "274",
        @Query("schoolid") q2: String = "528",
        @Query("userType") q3: String = "0",
        @Query("encPwd") q4: String = "0"
    ): Deferred<String>

    @GET("http://mc.m.5read.com/api/opac/showOpacLink.jspx?newSign")
    fun borrowed2Async(): Deferred<String>
}

object LibraryApi {
    private val service = Apis.newRetrofit("http://202.119.83.14:8080/uopac/opac/")
        .create(LibraryApiService::class.java)

    suspend fun search(keyword: String): List<LibSearchBean> = withContext(Dispatchers.IO) {
        val fieldData = ArrayMap<String, String>().apply {
            put("fieldCode", "")
            put("fieldValue", keyword)
        }
        val keywordArray = listOf(Collections.singletonMap("fieldList", listOf(fieldData)))
        val body = ArrayMap<String, Any>()
            .apply {
                put("sortField", "relevance")
                put("sortType", "desc")
                put("pageSize", 100)
                put("pageCount", 1)
                put("locale", "")
                put("first", true)
                put("filters", listOf<Any>())
                put("limiters", listOf<Any>())
                put("searchWords", keywordArray)
            }
        parseReportingError(service.searchAsync(body).await(), ::parseSearch)
    }

    suspend fun detail(id: String): LibDetailData = withContext(Dispatchers.IO) {
        parseReportingError(service.detailAsync(id).await(), ::parseDetail)
    }

    suspend fun borrowed(stuid: String, pwd: String): String = withContext(Dispatchers.IO) {
        service.borrowed1Async(stuid, pwd).await()
            .let { s ->
                val o = parseReportingError(s) { JSONObject(s) }
                if (o.getInt("result") != 1) {
                    throw LoginErrorException()
                } else {
                    service.borrowed2Async()
                }
            }
            .await()
            .let { s ->
                parseReportingError(s) {
                    JSONObject(it)
                        .getJSONArray("opacUrl")
                        .getJSONObject(0)
                        .getString("opaclendurl")
                }
            }
    }

    private fun parseSearch(json: Map<String, Any>): List<LibSearchBean> {
        val array = json["content"] as List<*>
        return array.map {
            val obj = it as Map<*, *>
            LibSearchBean(
                title = obj["title"] as String,
                author = obj["author"] as String,
                press = obj["publisher"] as String,
                id = obj["marcRecNo"] as String
            )
        }
    }

    private fun parseDetail(string: String): LibDetailData {
        val result = LibDetailData()

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
        result.head = headerBuilder.toString()

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
        result.states = stateList

        return result
    }

    private fun trimHtmlString(input: String): String {
        return HtmlCompat.fromHtml(input, 0).toString().trim()
    }
}
