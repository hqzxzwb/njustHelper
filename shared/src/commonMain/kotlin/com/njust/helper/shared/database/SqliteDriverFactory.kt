package com.njust.helper.shared.database

import com.squareup.sqldelight.db.AfterVersionWithDriver
import com.squareup.sqldelight.db.SqlDriver

expect class SqliteDriverFactory() {
  fun createDriver(
    schema: SqlDriver.Schema,
    name: String,
    vararg callbacks: AfterVersionWithDriver,
  ): SqlDriver
}
