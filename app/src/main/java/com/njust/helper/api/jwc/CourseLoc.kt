package com.njust.helper.api.jwc

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loc1")
class CourseLoc : Cloneable {
    @PrimaryKey(autoGenerate = true)
    var rowid: Int = 0
    var id: String = ""
    var classroom: String? = null
    var week1: String? = null
    var week2: String? = null
    var sec1: Int = 0
    var sec2: Int = 0
    var day: Int = 0

    public override fun clone(): CourseLoc {
        return super.clone() as CourseLoc
    }
}
