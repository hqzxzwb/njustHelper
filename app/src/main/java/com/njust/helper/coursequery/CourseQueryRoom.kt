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

private const val DB_VERSION = 3
private const val DB_NAME = "course_query"
private const val TABLE_NAME = "main"

@Entity(tableName = TABLE_NAME)
@Keep
class CourseQueryItem(
        @PrimaryKey val id: Int,
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
    fun queryCourses(
            name: String,
            teacher: String,
            maskedSection: Int,
            maskedDay: Int
    ): List<CourseQueryItem>

    @Query("select classroom from main where classroom like :building || '-%' group by classroom")
    fun queryClassroomSet(
            building: String
    ): List<String>

    @Query("select classroom from main where (classroom like :building || '-%') and (week2 like '% ' || :week || ' %') and (day == :day) and (maskedSection & :maskedSection) group by classroom")
    fun queryClassroom(
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
            .addMigrations(MyMigration(appContext, 1), MyMigration(appContext, 2))
            .build()
            .getDao()
}

private class MyMigration(val context: Context, startVersion: Int): Migration(startVersion, DB_VERSION) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.delete(TABLE_NAME, null, null)
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
                    val value = item.get(key)
                    if (value is String) {
                        cv.put(key, value)
                    } else if (value is Float) {
                        cv.put(key, value.roundToInt())
                    } else if (value is Double) {
                        cv.put(key, value.roundToInt())
                    } else if (value is Number) {
                        cv.put(key, value.toInt())
                    }
                    db.insert(TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, cv)
                }
            }
        }
    }
}
