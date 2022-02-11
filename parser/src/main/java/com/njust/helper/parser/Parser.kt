package com.njust.helper.parser

import java.nio.file.Files
import java.sql.DriverManager

fun main() {
  val source = Thread.currentThread().contextClassLoader.getResourceAsStream("source.txt")!!
      .use { it.bufferedReader().readText() }
  val items = Parser().parse(source)
  val output = Files.createTempFile(null, ".db").toAbsolutePath()
  println("Output file: $output")
  val conn = DriverManager.getConnection("jdbc:sqlite:$output")
  val stmt = conn.createStatement()
  stmt.execute("CREATE TABLE IF NOT EXISTS `main` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `classroom` TEXT NOT NULL, `day` INTEGER NOT NULL, `maskedDay` INTEGER NOT NULL, `section` INTEGER NOT NULL, `maskedSection` INTEGER NOT NULL, `name` TEXT NOT NULL, `teacher` TEXT NOT NULL, `week1` TEXT NOT NULL, `week2` TEXT NOT NULL)")
  val fields = Item::class.java.declaredFields.onEach { it.isAccessible = true }
  val fieldNames = fields.map { it.name }.toTypedArray()
  val pstmt = conn.prepareStatement("INSERT INTO main (${fieldNames.joinToString()}) VALUES (${fieldNames.joinToString { "?" }})", fieldNames)
  items.forEachIndexed { itemIndex, item ->
    fields.forEachIndexed { fieldIndex, field ->
      pstmt.setObject(fieldIndex + 1, field.get(item))
    }
    pstmt.executeUpdate()
  }
}

class Parser {
  private val trRegex = """<tr>[\s\S]+?</tr>""".toRegex()
  private val tdRegex = """<td.*>[\s\S]+?</td>""".toRegex()
  private val classroomRegex = """<nobr>([^<>]*)</nobr>""".toRegex()
  private val contentRegex = """<div[^<>]*>([^<>]*)<br>([^<>()]*)\(([\d,-]+周)\).*?</div>""".toRegex()
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
