package com.zwb.commonlibs.http

import java.net.URLEncoder

/**
 * Created by zhuwenbo on 2017/10/27.
 */
class HttpMap {
    private val map = HashMap<String, String>()

    fun addParam(name: String, value: String?): HttpMap {
        map.put(name, value ?: "")
        return this
    }

    fun clear(): HttpMap {
        map.clear()
        return this
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (entry in map.entries) {
            sb.append(URLEncoder.encode(entry.key, "utf-8"))
                    .append("=")
                    .append(URLEncoder.encode(entry.value, "utf-8"))
                    .append("&")
        }
        if (sb.isNotEmpty()) {
            sb.setLength(sb.length - 1)
        }
        return sb.toString()
    }
}
