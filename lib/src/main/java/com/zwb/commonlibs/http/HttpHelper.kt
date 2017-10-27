package com.zwb.commonlibs.http

import com.zwb.commonlibs.utils.LogUtils
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

@Throws(IOException::class)
fun inputStreamToString(inputStream: InputStream, encoding: String): String {
    return inputStream.bufferedReader(Charset.forName(encoding)).use { it.readText() }
}

fun buildUrl(url: String, map: HttpMap?): String {
    return if (map !== null) url + "?" + map else url
}

/**
 * Created by zhuwenbo on 2017/10/27.
 * 基于URLConnection的Http实现
 */
open class HttpHelper {
    private var encoding: String = "utf-8"

    @Throws(IOException::class)
    fun getGetResult(url: String): String {
        val string = inputStreamToString(getGetInputStream(url), encoding)
        LogUtils.i(this, string)
        return string
    }

    @Throws(IOException::class)
    open fun getGetResult(url: String, map: HttpMap): String {
        return getGetResult(buildUrl(url, map))
    }

    @Throws(IOException::class)
    fun getGetInputStream(url: String): InputStream {
        val urlConnection = URL(url).openConnection() as HttpURLConnection
        setupURLConnection(urlConnection)
        urlConnection.connect()
        return urlConnection.inputStream
    }

    @Throws(IOException::class)
    open fun getPostResult(url: String, data: HttpMap): String {
        val string = inputStreamToString(getPostInputStream(url, data), encoding)
        LogUtils.i(this, string)
        return string
    }

    @Throws(IOException::class)
    fun getPostInputStream(url: String, data: HttpMap?): InputStream {
        val urlConnection = URL(url).openConnection() as HttpURLConnection
        setupURLConnection(urlConnection)
        if (data != null) {
            urlConnection.doOutput = true
            urlConnection.doInput = true
            urlConnection.outputStream.write(data.toString().toByteArray())
            urlConnection.outputStream.flush()
            urlConnection.outputStream.close()
        }
        return urlConnection.inputStream
    }

    open protected fun setupURLConnection(urlConnection: HttpURLConnection) {
        urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded")
        urlConnection.connectTimeout = 10000
        urlConnection.readTimeout = 10000
    }
}
