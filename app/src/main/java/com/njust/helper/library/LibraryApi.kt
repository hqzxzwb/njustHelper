package com.njust.helper.library

import com.njust.helper.library.search.LibSearchBean
import com.njust.helper.tools.Apis
import com.njust.helper.tools.ServerErrorException
import com.zwb.commonlibs.rx.ioSubscribeUiObserve
import io.reactivex.Single
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface LibraryApi {
    @GET("search_adv_result.php")
    fun search(
            @Query("q0") keyword: String,
            @Query("sType0") q1: String = "any",
            @Query("pageSize") q2: String = "100",
            @Query("sort") q3: String = "score",
            @Query("desc") q4: String = "true"
    ): Single<String>

    companion object {
        private val API = Apis.newRetrofitBuilder()
                .baseUrl("http://202.119.83.14:8080/uopac/opac/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(LibraryApi::class.java)

        fun search(keyword: String): Single<List<LibSearchBean>> {
            return API.search(keyword)
                    .map { parseSearch(it) }
                    .ioSubscribeUiObserve()
        }
    }
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
