package com.njust.helper.shared.database

import androidx.sqlite.db.SupportSQLiteDatabase
import com.njust.helper.shared.AppContext
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class SqliteDriverFactory {
  actual fun createDriver(
    schema: SqlDriver.Schema,
    name: String
  ): SqlDriver {
    return AndroidSqliteDriver(
      schema,
      AppContext.appContext,
      name,
      callback = object : AndroidSqliteDriver.Callback(schema) {
        override fun onConfigure(db: SupportSQLiteDatabase) {
          super.onConfigure(db)
          setPragma(db, "JOURNAL_MODE = WAL")
          setPragma(db, "SYNCHRONOUS = 2")
        }

        private fun setPragma(db: SupportSQLiteDatabase, pragma: String) {
          val cursor = db.query("PRAGMA $pragma")
          cursor.moveToFirst()
          cursor.close()
        }
      }
    )
  }
}
