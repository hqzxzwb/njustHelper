package com.njust.helper.course.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.njust.helper.api.jwc.CourseInfo
import com.njust.helper.api.jwc.CourseLoc
import com.njust.helper.model.Course
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module

private const val DB_NAME = "course.db"
private const val DB_VERSION = 5

fun Module.injectCourseDatabase() {
  single {
    val context = androidApplication()
    Room.databaseBuilder(context, CourseDatabase::class.java, DB_NAME)
      .allowMainThreadQueries()
      .fallbackToDestructiveMigrationFrom(1, 2, 3, 4)
      .addMigrations()
      .build()
  }
}

@Database(entities = [CourseLoc::class, CourseInfo::class], version = DB_VERSION)
abstract class CourseDatabase : RoomDatabase() {
  abstract fun courseDao(): CourseDao

  fun clear() {
    courseDao().clearLoc()
    courseDao().clearInfo()
  }

  fun add(infos: List<CourseInfo>, locs: List<CourseLoc>) {
    courseDao().insertLocs(locs)
    courseDao().insertInfos(infos)
  }

  fun getCourses(): List<Course> = courseDao().getCourses()

  fun getCourses(dayOfSemester: Int): List<Course> {
    val week = dayOfSemester / 7 + 1
    val dayOfWeek = dayOfSemester % 7
    return courseDao().getCourses("% $week %", dayOfWeek)
  }

  fun countCourses(dayOfSemester: Int): Int = getCourses(dayOfSemester).size
}

@Dao
interface CourseDao {
  @Query("delete from loc1")
  fun clearLoc()

  @Insert
  fun insertLocs(locs: List<CourseLoc>)

  @Query("delete from info1")
  fun clearInfo()

  @Insert
  fun insertInfos(infos: List<CourseInfo>)

  @Query("select a.id, a.name, a.teacher, b.classroom, b.week1, b.week2, b.sec1, b.sec2, b.day from info1 as a,loc1 as b where a.id=b.id order by b.sec1 ")
  fun getCourses(): List<Course>

  @Query("select a.id, a.name, a.teacher, b.classroom, b.week1, b.week2, b.sec1, b.sec2, b.day from info1 as a,loc1 as b where a.id=b.id and b.day=:dayOfWeek and b.week2 like :weekPattern order by b.sec1 ")
  fun getCourses(weekPattern: String, dayOfWeek: Int): List<Course>
}
