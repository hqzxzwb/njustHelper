package com.njust.helper.shared.database

import co.touchlab.sqliter.DatabaseConfiguration
import com.squareup.sqldelight.db.AfterVersionWithDriver
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.db.migrateWithCallbacks
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import com.squareup.sqldelight.drivers.native.wrapConnection

actual class SqliteDriverFactory {
  actual fun createDriver(
    schema: SqlDriver.Schema,
    name: String,
    vararg callbacks: AfterVersionWithDriver,
  ): SqlDriver {
    return NativeSqliteDriver(
      configuration = DatabaseConfiguration(
        name = name,
        version = schema.version,
        create = { connection ->
          wrapConnection(connection) { schema.create(it) }
        },
        upgrade = { connection, oldVersion, newVersion ->
          wrapConnection(connection) {
            schema.migrateWithCallbacks(
              it,
              oldVersion,
              newVersion,
              *callbacks,
            )
          }
        }
      )
    )
  }
}
