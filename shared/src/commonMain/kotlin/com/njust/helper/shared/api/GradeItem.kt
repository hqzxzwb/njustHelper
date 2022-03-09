package com.njust.helper.shared.api

data class GradeItem(
    val termName: String,
    val courseName: String,
    val gradeText: String,
    val grade: Double,
    val weight: Double,
    val type: String
) {
  val point: Double
    get() {
      val grade = grade
      return when {
        grade < 60 -> .0
        grade < 64 -> 1.0
        grade < 68 -> 1.5
        grade < 72 -> 2.0
        grade < 75 -> 2.3
        grade < 78 -> 2.7
        grade < 82 -> 3.0
        grade < 85 -> 3.3
        grade < 90 -> 3.7
        else -> 4.0
      }
    }
}
