package com.njust.helper.coursequery

import android.content.Context
import androidx.annotation.Keep
import androidx.room.*
import com.zwb.commonlibs.utils.SingletonHolder

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

  @Query("select classroom from main where classroom like :building || '-%' group by classroom")
  suspend fun queryClassroomSet(
    building: String
  ): List<String>

  @Query("select classroom from main where (classroom like :building || '-%') and (week2 like '% ' || :week || ' %') and (day == :day) and (maskedSection & :maskedSection) group by classroom")
  suspend fun queryClassroom(
    building: String,
    week: Int,
    day: Int,
    maskedSection: Int
  ): List<String>

  companion object : SingletonHolder<CourseQueryDao, Context>() {
    override fun createInstance(param: Context): CourseQueryDao = newDao(param)
  }
}

@Database(entities = [CourseQueryItem::class], version = DB_VERSION)
abstract class CourseQueryDatabase : RoomDatabase() {
  abstract fun getDao(): CourseQueryDao
}

private fun newDao(context: Context): CourseQueryDao {
  val appContext = context.applicationContext
  return Room.databaseBuilder(appContext, CourseQueryDatabase::class.java, DB_NAME)
    .createFromAsset("classes.db")
    .fallbackToDestructiveMigration()
    .build()
    .getDao()
}
