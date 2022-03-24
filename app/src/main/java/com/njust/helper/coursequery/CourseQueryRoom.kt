package com.njust.helper.coursequery

import androidx.annotation.Keep
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module

private const val DB_VERSION = 7
private const val DB_NAME = "course_query"
private const val TABLE_NAME = "main"

@Entity(tableName = TABLE_NAME)
@Keep
class CourseQueryItem(
  @PrimaryKey(autoGenerate = true) val id: Int,
  val classroom: String,
  val day: Int,
  val maskedDay: Int,
  val section: Int,
  val maskedSection: Int,
  val name: String,
  val teacher: String,
  val week1: String,
  val week2: String
)

@Dao
interface CourseQueryDao {
  @Query("select * from main where name like '%' || :name || '%' and teacher like '%' || :teacher || '%' and maskedSection & :maskedSection and maskedDay & :maskedDay limit 200")
  suspend fun queryCourses(
    name: String,
    teacher: String,
    maskedSection: Int,
    maskedDay: Int
  ): List<CourseQueryItem>

  @Query("select classroom from main where classroom like :building || '%' group by classroom")
  suspend fun queryClassroomSet(
    building: String
  ): List<String>

  @Query("select classroom from main where (classroom like :building || '%') and (week2 like '% ' || :week || ' %') and (day == :day) and (maskedSection & :maskedSection) group by classroom")
  suspend fun queryClassroom(
    building: String,
    week: Int,
    day: Int,
    maskedSection: Int
  ): List<String>
}

@Database(entities = [CourseQueryItem::class], version = DB_VERSION)
abstract class CourseQueryDatabase : RoomDatabase() {
  abstract fun getDao(): CourseQueryDao
}

fun Module.injectCourseQueryDatabase() {
  single {
    Room.databaseBuilder(androidApplication(), CourseQueryDatabase::class.java, DB_NAME)
      .createFromAsset("classes.db")
      .fallbackToDestructiveMigration()
      .build()
  }
}
