package com.njust.helper.api.jwc

import com.njust.helper.BR
import java.text.DecimalFormat

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

data class GradeTerm(
        val termName: String,
        val items: List<GradeItem>
) {
    val brId: Int
        get() = BR.vm

    fun totalWeight() = items.sumByDouble { it.weight }.roundToString()

    fun mustTotalWeight() = items
            .sumByDouble { if (it.type == "必修") it.weight else 0.0 }
            .roundToString()

    fun meanPoint() = items.meanPoint().roundToString()

    fun meanGrade() = items.meanGrade().roundToString()

    fun mustMeanPoint() = items.filter { it.type == "必修" }.meanPoint().roundToString()

    fun mustMeanGrade() = items.filter { it.type == "必修" }.meanGrade().roundToString()

    private fun Double.roundToString(): String = DecimalFormat("#.##").format(this)
}

private fun List<GradeItem>.meanPoint(): Double {
    if (isEmpty()) return 0.0
    val totalPoint = sumByDouble { it.point * it.weight }
    val totalWeight = sumByDouble { it.weight }
    return totalPoint / totalWeight
}

private fun List<GradeItem>.meanGrade(): Double {
    if (isEmpty()) return 0.0
    val totalGrade = sumByDouble { it.grade * it.weight }
    val totalWeight = sumByDouble { it.weight }
    return totalGrade / totalWeight
}
