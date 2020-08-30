package com.njust.helper.parser

import com.google.gson.Gson

fun main() {
  val source = Thread.currentThread().contextClassLoader.getResourceAsStream("source.txt")!!
      .use { it.bufferedReader().readText() }
  val items = Parser().parse(source)
  val gson = Gson()
  items.forEach { item ->
    println(gson.toJson(item))
  }
}

class Parser {
  private val trRegex = """<tr>[\s\S]+?</tr>""".toRegex()
  private val tdRegex = """<td.*>[\s\S]+?</td>""".toRegex()
  private val classroomRegex = """<nobr>([^<>]*)</nobr>""".toRegex()
  private val contentRegex = """<div[^<>]*>([^<>]*)<br>([^<>()]*)\(([\d,-]+周)\).*</div>""".toRegex()
  private val sectionCount = 6

  fun parse(s: String): List<Item> {
    val result = arrayListOf<Item>()
    trRegex.findAll(s).forEach { trResult ->
      val tr = trResult.value
      val tds = tdRegex.findAll(tr).toList()
      if (tds.isEmpty()) return@forEach
      val classroomResult = classroomRegex.find(tds[0].value) ?: return@forEach
      val classroom = classroomResult.groupValues[1]
      tds.subList(1, tds.size).forEachIndexed { index, td ->
        val dayInWeek = index / sectionCount
        val section = index % sectionCount
        contentRegex.findAll(td.value.filter { !it.isWhitespace() }).forEach { contentResult ->
          val weeksString = contentResult.groupValues[3]
          val weeks = weeksString.removeSuffix("周")
              .split(",")
              .flatMap { seg ->
                if (seg.contains("-")) {
                  seg.split("-").let { it[0].toInt()..it[1].toInt() }
                } else {
                  listOf(seg.toInt())
                }
              }
              .joinToString(separator = " ", prefix = " ", postfix = " ")
          result += Item(
              contentResult.groupValues[1],
              classroom,
              contentResult.groupValues[2],
              dayInWeek,
              section,
              weeksString,
              weeks
          )
        }
      }
    }
    return result
  }
}

data class Item(
    val name: String,
    val classroom: String,
    val teacher: String,
    val day: Int,
    val section: Int,
    val week1: String,
    val week2: String
) {
  val maskedDay = 1 shl day
  val maskedSection = 1 shl section
}
