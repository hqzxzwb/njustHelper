package com.njust.helper.api.library

import android.text.Html
import com.njust.helper.api.Apis
import com.njust.helper.api.LoginErrorException
import com.njust.helper.api.ServerErrorException
import com.njust.helper.api.parseReportingError
import com.zwb.commonlibs.rx.ioSubscribeUiObserve
import io.reactivex.Single
import org.json.JSONObject
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object LibraryApi {
    private interface LibraryApiService {
        @GET("search_adv_result.php")
        fun search(
                @Query("q0") keyword: String,
                @Query("sType0") q1: String = "any",
                @Query("pageSize") q2: String = "100",
                @Query("sort") q3: String = "score",
                @Query("desc") q4: String = "true"
        ): Single<String>

        @GET("item.php")
        fun detail(
                @Query("marc_no") id: String
        ): Single<String>

        @GET("http://mc.m.5read.com/apis/user/userLogin.jspx")
        fun borrowed1(
                @Query("username") stuid: String,
                @Query("password") pwd: String,
                @Query("areaid") q1: String = "274",
                @Query("schoolid") q2: String = "528",
                @Query("userType") q3: String = "0",
                @Query("encPwd") q4: String = "0"
        ): Single<String>

        @GET("http://mc.m.5read.com/api/opac/showOpacLink.jspx?newSign")
        fun borrowed2(): Single<String>
    }

    private val service = Apis.newRetrofitBuilder()
            .baseUrl("http://202.119.83.14:8080/uopac/opac/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(LibraryApiService::class.java)

    fun search(keyword: String): Single<List<LibSearchBean>> {
        return service.search(keyword)
                .map { parseReportingError(it, ::parseSearch) }
                .ioSubscribeUiObserve()
    }

    fun detail(id: String): Single<LibDetailData> {
        return service.detail(id)
                .map { parseReportingError(it, ::parseDetail) }
                .ioSubscribeUiObserve()
    }

    fun borrowed(stuid: String, pwd: String): Single<String> {
        return service.borrowed1(stuid, pwd)
                .flatMap { s ->
                    val o = parseReportingError(s) { JSONObject(s) }
                    if (o.getInt("result") != 1) {
                        throw LoginErrorException()
                    }
                    service.borrowed2()
                }
                .map { s ->
                    parseReportingError(s) {
                        JSONObject(it)
                                .getJSONArray("opacUrl")
                                .getJSONObject(0)
                                .getString("opaclendurl")
                    }
                }
                .ioSubscribeUiObserve()
    }

    private fun parseSearch(string: String): List<LibSearchBean> {
        if (!string.contains("南京理工大学图书馆")) {
            throw ServerErrorException()
        }
        return Regex("""href="item.php\?marc_no=(\d*)">([^<]*)<.*\s*.*>(.*)<.*\s*.*>(.*)<""")
                .findAll(string)
                .mapTo(arrayListOf()) {
                    val groupValues = it.groupValues
                    LibSearchBean(
                            title = groupValues[2],
                            author = groupValues[3],
                            press = groupValues[4],
                            id = groupValues[1]
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
        return Html.fromHtml(input).toString().trim()
    }
}
