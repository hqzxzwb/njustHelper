package com.njust.helper.model

class CourseLoc : Cloneable {
    var id: String? = null
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
