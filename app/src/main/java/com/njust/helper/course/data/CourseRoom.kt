package com.njust.helper.course.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.njust.helper.api.jwc.CourseInfo
import com.njust.helper.api.jwc.CourseLoc
import com.njust.helper.model.Course
import com.zwb.commonlibs.utils.SingletonHolder

private const val DB_NAME = "course.db"
private const val DB_VERSION = 5

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

    companion object : SingletonHolder<CourseDatabase, Context>() {
        override fun createInstance(param: Context): CourseDatabase {
            return Room.databaseBuilder(param.applicationContext, CourseDatabase::class.java, DB_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigrationFrom(1, 2, 3)
                    .addMigrations(Migration_4_5())
                    .addMigrations()
                    .build()
        }
    }
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

    @Query("select a.id, a.name, a.teacher, b.classroom, b.week1, b.week2, b.sec1, b.sec2, b.day from info1 as a,loc1 as b where a.id=b.id order by b.id, b.sec1 ")
    fun getCourses(): List<Course>

    @Query("select a.id, a.name, a.teacher, b.classroom, b.week1, b.week2, b.sec1, b.sec2, b.day from info1 as a,loc1 as b where a.id=b.id and b.day=:dayOfWeek and b.week2 like :weekPattern order by b.id, b.sec1 ")
    fun getCourses(weekPattern: String, dayOfWeek: Int): List<Course>
}

private class Migration_4_5 : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `loc1` (`rowid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `id` TEXT NOT NULL, `classroom` TEXT, `week1` TEXT, `week2` TEXT, `sec1` INTEGER NOT NULL, `sec2` INTEGER NOT NULL, `day` INTEGER NOT NULL)")
        database.execSQL("CREATE TABLE IF NOT EXISTS `info1` (`id` TEXT NOT NULL, `name` TEXT, `teacher` TEXT, PRIMARY KEY(`id`))")
        database.execSQL("INSERT INTO `loc1` (id,classroom,week1,week2,sec1,sec2,day) SELECT id,classroom,week1,week2,sec1,sec2,day FROM `loc`")
        database.execSQL("INSERT INTO `info1` SELECT * FROM `info`")
        database.execSQL("DROP TABLE `loc`")
        database.execSQL("DROP TABLE `info`")
    }
}
