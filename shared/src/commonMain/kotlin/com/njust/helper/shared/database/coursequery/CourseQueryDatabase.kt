package com.njust.helper.shared.database.coursequery

import com.njust.helper.shared.CourseQueryItem
import com.njust.helper.shared.MR
import com.njust.helper.shared.database.SqliteDriverFactory
import com.njust.helper.shared.database.prepareAssetDatabase
import com.njust.helper.shared.database.suspendAsList

class CourseQueryDatabase private constructor() {
  private val queries = run {
    val driver =
      SqliteDriverFactory().createDriver(CourseQueryDatabaseInternal.Schema, "course_query.db")
    val database = CourseQueryDatabaseInternal(driver)
    database.courseQueryQueries
  }

  suspend fun queryCourses(
    name: String,
    teacher: String,
    maskedSection: Int,
    maskedDay: Int,
  ): List<CourseQueryItem> {
    return queries
      .queryCourses(
        name,
        teacher,
        maskedSection,
        maskedDay,
      )
      .suspendAsList()
  }

  suspend fun queryClassroomSet(
    building: String,
  ): List<String> {
    return queries
      .queryClassroomSet(building)
      .suspendAsList()
  }

  suspend fun queryClassroom(
    building: String,
    week: Int,
    day: Int,
    maskedSection: Int,
  ): List<String> {
    return queries
      .queryClassroom(
        building,
        week.toString(),
        day,
        maskedSection,
      )
      .suspendAsList()
  }

  companion object {
    private val INSTANCE = CourseQueryDatabase()

    suspend operator fun invoke(): CourseQueryDatabase {
      prepareAssetDatabase(MR.assets.course_query)
      return INSTANCE
    }
  }
}
