package com.njust.helper.library

import android.text.Html
import com.njust.helper.library.book.LibDetailData
import com.njust.helper.library.book.LibDetailItem
import com.njust.helper.library.search.LibSearchBean
import com.njust.helper.tools.Apis
import com.njust.helper.tools.ServerErrorException
import com.zwb.commonlibs.rx.ioSubscribeUiObserve
import io.reactivex.Single
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
    }

    private val service = Apis.newRetrofitBuilder()
            .baseUrl("http://202.119.83.14:8080/uopac/opac/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(LibraryApiService::class.java)

    fun search(keyword: String): Single<List<LibSearchBean>> {
        return service.search(keyword)
                .map { parseSearch(it) }
                .ioSubscribeUiObserve()
    }

    fun detail(id: String): Single<LibDetailData> {
        return service.detail(id)
                .map { parseDetail(it) }
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
