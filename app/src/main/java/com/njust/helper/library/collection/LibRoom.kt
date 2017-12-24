package com.njust.helper.library.collection

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.*
import android.content.Context
import com.zwb.commonlibs.utils.SingletonHolder

private const val TABLE_NAME = "collection"
private const val DB_NAME = "library_"
private const val DB_VERSION = 1

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
    fun listCollects(): List<LibCollectItem>

    @Query("select * from $TABLE_NAME where id = :id")
    fun checkCollect(id: String): List<LibCollectItem>

    @Insert
    fun addCollect(item: LibCollectItem)
}

@Database(entities = [LibCollectItem::class], version = DB_VERSION)
abstract class LibCollectDatabase : RoomDatabase() {
    abstract fun getLibCollectDao(): LibCollectDao
}

class LibCollectManager private constructor(context: Context) {
    private val dao: LibCollectDao = Room
            .databaseBuilder(context, LibCollectDatabase::class.java, DB_NAME)
            .allowMainThreadQueries()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    LibraryHelper(context).bindArgs.forEach {
                        db.execSQL("INSERT OR IGNORE INTO `collection`(`id`,`name`,`code`,`time`) VALUES (?,?,?,?)", it)
                    }
                }
            })
            .build()
            .getLibCollectDao()

    fun addCollect(id: String, name: String, code: String): Boolean {
        val item = LibCollectItem()
        item.id = id
        item.name = name
        item.code = code
        item.time = System.currentTimeMillis()
        dao.addCollect(item)
        return true
    }

    fun checkCollect(id: String): Boolean {
        return dao.checkCollect(id).isNotEmpty()
    }

    fun removeCollect(id: String) {
        dao.deleteCollects(listOf(id))
    }

    fun removeCollects(ids: List<String>) {
        dao.deleteCollects(ids)
    }

    fun findCollect(): List<LibCollectItem> {
        return dao.listCollects()
    }

    companion object : SingletonHolder<LibCollectManager, Context>(::LibCollectManager)
}
