package com.njust.helper.coursequery

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.annotation.Keep
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.njust.helper.R
import com.zwb.commonlibs.utils.SingletonHolder
import org.json.JSONObject
import java.util.*
import kotlin.math.roundToInt

private const val DB_VERSION = 5
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
      .addCallback(object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
          initializeData(appContext, db)
        }
      })
      .addMigrations(
          MyMigration(appContext, 1),
          MyMigration(appContext, 2),
          MyMigration(appContext, 3),
          MyMigration(appContext, 4)
      )
      .build()
      .getDao()
}

private class MyMigration(val context: Context, startVersion: Int) : Migration(startVersion, DB_VERSION) {
  override fun migrate(database: SupportSQLiteDatabase) {
    database.execSQL("DROP TABLE IF EXISTS `main`")
    database.execSQL("CREATE TABLE IF NOT EXISTS `main` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `classroom` TEXT NOT NULL, `day` INTEGER NOT NULL, `maskedDay` INTEGER NOT NULL, `section` INTEGER NOT NULL, `maskedSection` INTEGER NOT NULL, `name` TEXT NOT NULL, `teacher` TEXT NOT NULL, `week1` TEXT NOT NULL, `week2` TEXT NOT NULL)")
    database.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
    database.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'd77c9e76ed522d3fff23947fb79f4223')")
    initializeData(context, database)
  }
}

private fun initializeData(context: Context, db: SupportSQLiteDatabase) {
  val cv = ContentValues()
  context.resources.openRawResource(R.raw.courses).use { inputStream ->
    Scanner(inputStream).use { scanner ->
      while (scanner.hasNext()) {
        val s = scanner.nextLine()
        if (s.isEmpty()) {
          continue
        }
        val item = JSONObject(s)
        cv.clear()
        item.keys().forEach { key ->
          when (val value = item.get(key)) {
            is String -> cv.put(key, value)
            is Float -> cv.put(key, value.roundToInt())
            is Double -> cv.put(key, value.roundToInt())
            is Number -> cv.put(key, value.toInt())
          }
          db.insert(TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, cv)
        }
      }
    }
  }
}
