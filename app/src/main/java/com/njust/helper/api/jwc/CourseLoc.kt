package com.njust.helper.api.jwc

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loc1")
data class CourseLoc(
  @PrimaryKey(autoGenerate = true)
  val rowid: Int = 0,
  val id: String = "",
  val classroom: String? = null,
  val week1: String? = null,
  val week2: String? = null,
  val sec1: Int = 0,
  val sec2: Int = 0,
  val day: Int = 0,
)
