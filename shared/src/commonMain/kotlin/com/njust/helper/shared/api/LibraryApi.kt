package com.njust.helper.shared.api

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

object LibraryApi {
  private val parser = Json {
    ignoreUnknownKeys = true
  }

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

    val client = HttpClient(CIO) {
      install(JsonFeature)
    }
    val json: String = client.post("http://202.119.83.14:8080/uopac/opac/ajax_search_adv.php") {
      contentType(ContentType.Application.Json)
      this.body = body
    }
    val jsonTree = parser.decodeFromString<JsonObject>(json)
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
