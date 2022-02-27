package com.njust.helper.shared.database

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class SqliteDriverFactory {
  actual fun createDriver(
    schema: SqlDriver.Schema,
    name: String
  ): SqlDriver {
    return NativeSqliteDriver(schema, name)
  }
}
