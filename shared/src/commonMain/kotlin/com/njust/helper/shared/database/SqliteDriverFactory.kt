package com.njust.helper.shared.database

import com.squareup.sqldelight.db.SqlDriver

expect class SqliteDriverFactory() {
  fun createDriver(schema: SqlDriver.Schema, name: String): SqlDriver
}
