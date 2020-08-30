package com.njust.helper.api.jwc

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "info1")
class CourseInfo {
  @PrimaryKey
  var id: String = ""
  var name: String? = null
  var teacher: String? = null
}
