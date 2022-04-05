package com.njust.helper.library.collection

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.android.ext.koin.androidApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.module.Module

private const val TABLE_NAME = "collection"
private const val DB_NAME = "library.db"
private const val DB_VERSION = 9

@Entity(tableName = TABLE_NAME)
class LibCollectItem {
  @PrimaryKey
  var id: String = ""
  var name: String = ""
  var code: String = ""
  var time: Long = 0
}

@Dao
interface LibCollectDao {
  @Query("delete from $TABLE_NAME where id in (:ids)")
  fun deleteCollects(ids: List<String>)

  @Query("select * from $TABLE_NAME order by time desc")
  fun listCollects(): MutableList<LibCollectItem>

  @Query("select * from $TABLE_NAME where id = :id")
  fun checkCollect(id: String): Flow<List<LibCollectItem>>

  @Insert
  suspend fun addCollect(item: LibCollectItem)
}

@Database(entities = [LibCollectItem::class], version = DB_VERSION)
abstract class LibCollectDatabase : RoomDatabase() {
  abstract fun getLibCollectDao(): LibCollectDao
}

private class CollectMigration(startVersion: Int) : Migration(startVersion, DB_VERSION) {
  override fun migrate(database: SupportSQLiteDatabase) {
    if (startVersion < 4) {
      database.execSQL("drop table if exists mylib")
    }
    if (startVersion < 5) {
      database.execSQL("create table collection (id varchar primary key,name varchar,time long)")
    }
    if (startVersion < 6) {
      database.execSQL("drop table if exists borrow")
    }
    if (startVersion < 7) {
      database.execSQL("drop table if exists history")
    }
    if (startVersion < 8) {
      database.execSQL("alter table collection add column code varchar")
    }
    if (startVersion < 9) {
      val query = SupportSQLiteQueryBuilder.builder(TABLE_NAME)
          .columns(arrayOf("*"))
          .create()
      val bindArgs = mutableListOf<Array<Any>>()
      database.query(query).use { cursor ->
        val idColumn = cursor.getColumnIndex("id")
        val nameColumn = cursor.getColumnIndex("name")
        val codeColumn = cursor.getColumnIndex("code")
        val timeColumn = cursor.getColumnIndex("time")
        while (cursor.moveToNext()) {
          bindArgs.add(arrayOf(
              cursor.getString(idColumn) ?: "",
              cursor.getString(nameColumn) ?: "",
              cursor.getString(codeColumn) ?: "",
              cursor.getLong(timeColumn)
          ))
        }
      }
      database.execSQL("DROP TABLE IF EXISTS `collection`")
      database.execSQL("CREATE TABLE IF NOT EXISTS `collection` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `code` TEXT NOT NULL, `time` INTEGER NOT NULL, PRIMARY KEY(`id`))")
      bindArgs.forEach {
        database.execSQL("INSERT OR IGNORE INTO `collection`(`id`,`name`,`code`,`time`) VALUES (?,?,?,?)", it)
      }
    }
  }
}

object LibCollectManager: KoinComponent {
  private val dao: LibCollectDao = get<LibCollectDatabase>()
      .getLibCollectDao()

  suspend fun addCollect(id: String, name: String, code: String): Boolean {
    val item = LibCollectItem()
    item.id = id
    item.name = name
    item.code = code
    item.time = System.currentTimeMillis()
    dao.addCollect(item)
    return true
  }

  fun collectedStateFlow(id: String): Flow<Boolean> {
    return dao.checkCollect(id).map { it.isNotEmpty() }
  }

  fun removeCollect(id: String) {
    dao.deleteCollects(listOf(id))
  }

  fun removeCollects(ids: Collection<String>) {
    dao.deleteCollects(ArrayList(ids))
  }

  fun listCollects(): MutableList<LibCollectItem> {
    return dao.listCollects()
  }
}

fun Module.injectLibCollectDatabase() {
  single {
    Room
      .databaseBuilder(androidApplication(), LibCollectDatabase::class.java, DB_NAME)
      .allowMainThreadQueries()
      .addMigrations(*((1 until DB_VERSION).map { CollectMigration(it) }.toTypedArray()))
      .build()
  }
}
