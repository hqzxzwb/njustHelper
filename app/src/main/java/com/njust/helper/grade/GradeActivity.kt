package com.njust.helper.grade

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.njust.helper.BuildConfig
import com.njust.helper.R
import com.njust.helper.account.AccountActivity
import com.njust.helper.shared.api.GradeItem
import com.njust.helper.shared.api.JwcApi
import com.njust.helper.shared.api.LoginErrorException
import com.njust.helper.shared.api.ParseErrorException
import com.njust.helper.shared.api.ServerErrorException
import com.njust.helper.tools.Prefs
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.DecimalFormat

class GradeActivity : AppCompatActivity() {
  private val vm = GradeVm(
    onClickHome = this::finish,
    onClickRefresh = this::refresh,
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      GradeScreen(vm = vm)
    }
    refresh()
  }

  private fun refresh() {
    lifecycleScope.launch {
      vm.loading = true
      try {
        val result = JwcApi.grade(
          Prefs.getId(this@GradeActivity),
          Prefs.getJwcPwd(this@GradeActivity),
        )
        vm.applyGradeData(result)
      } catch (e: Exception) {
        onError(e)
      }
      vm.loading = false
    }
  }

  private fun onError(throwable: Throwable) {
    when (throwable) {
      is ServerErrorException -> showSnack(R.string.message_server_error)
      is LoginErrorException -> AccountActivity.alertPasswordError(this)
      is IOException -> showSnack(R.string.message_net_error)
      is ParseErrorException -> showSnack(R.string.message_parse_error)
      else -> {
        if (BuildConfig.DEBUG) {
          throwable.printStackTrace()
          throw throwable
        }
      }
    }
  }

  private fun showSnack(messageRes: Int) {
    lifecycleScope.launch {
      vm.snackbarMessageFlow.emit(getString(messageRes))
    }
  }
}

class MeanGradeValues {
  var hasUnrecognizedGrade = false
  var totalWeight = 0.0
  var totalPoint = 0.0
  var totalGrade = 0.0
  var requiredWeight = 0.0
  var requiredPoint = 0.0
  var requiredGrade = 0.0
}

@Immutable
class MeanGradeVm(values: MeanGradeValues) {
  val hasUnrecognizedGrade = values.hasUnrecognizedGrade
  val totalWeight: String = values.totalWeight.roundToString()
  val totalPoint: String = (values.totalPoint / values.totalWeight).roundToString()
  val totalGrade: String = (values.totalGrade / values.totalWeight).roundToString()
  val requiredWeight: String = values.requiredWeight.roundToString()
  val requiredPoint: String = (values.requiredPoint / values.requiredWeight).roundToString()
  val requiredGrade: String = (values.requiredGrade / values.requiredWeight).roundToString()

  private fun Double.roundToString(): String {
    return if (this.isNaN()) {
      toString()
    } else {
      DecimalFormat("#.##").format(this)
    }
  }
}

@Stable
class GradeVm(
  val onClickHome: () -> Unit,
  val onClickRefresh: () -> Unit,
) {
  val snackbarMessageFlow = MutableSharedFlow<String>(replay = 1)

  var terms: List<GradeTermVm> by mutableStateOf(listOf())

  var totalMean: MeanGradeVm? by mutableStateOf(null)

  var loading by mutableStateOf(false)

  fun applyGradeData(data: Map<String, List<GradeItem>>) {
    val total = MeanGradeValues()
    val terms = arrayListOf<GradeTermVm>()
    data.forEach { entry ->
      val termName = entry.key
      val items = entry.value
      val loop = MeanGradeValues()
      items.forEach {
        foldToTripleDouble(total, it)
        foldToTripleDouble(loop, it)
      }
      terms += GradeTermVm(items, termName, MeanGradeVm(loop))
    }
    terms.sortByDescending { it.termName }
    this.terms = terms
    totalMean = MeanGradeVm(total)
  }

  private fun foldToTripleDouble(acc: MeanGradeValues, gradeItem: GradeItem) {
    if (gradeItem.grade < 0) {
      acc.hasUnrecognizedGrade = true
      return
    }
    val weight = gradeItem.weight
    val grade = weight * gradeItem.grade
    val point = weight * gradeItem.point
    acc.totalWeight += weight
    acc.totalGrade += grade
    acc.totalPoint += point
    if (gradeItem.type == "必修") {
      acc.requiredWeight += weight
      acc.requiredGrade += grade
      acc.requiredPoint += point
    }
  }
}

@Immutable
class GradeTermVm(
  val items: List<GradeItem>,
  val termName: String,
  val mean: MeanGradeVm
)
