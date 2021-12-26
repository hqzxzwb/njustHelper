package com.njust.helper.coursequery

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.njust.helper.R
import com.njust.helper.RemoteConfig
import com.njust.helper.tools.Constants
import com.njust.helper.tools.TimeUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * 自习室查询
 *
 * @author zwb
 */
class ClassroomActivity : AppCompatActivity() {
  private val viewModel by viewModels<ClassroomViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      ClassroomScreen(
        selectedDayState = viewModel.selectedDay,
        selectedBuildingState = viewModel.selectedBuilding,
        selectedSectionsState = viewModel.selectedSections,
        resultText = viewModel.resultText,
        isRefreshing = viewModel.loading,
        noSectionChosenPublisher = viewModel.noSectionChosenPublisher,
        onClickQuery = {
          lifecycleScope.launchWhenCreated {
            viewModel.query(this@ClassroomActivity)
          }
        },
        onClickHome = { finish() },
      )
    }

    val time = (System.currentTimeMillis() - RemoteConfig.getTermStartTime()) % TimeUtil.ONE_DAY

    var i = 0
    while (i < Constants.COURSE_SECTION_COUNT) {
      if (time < Constants.SECTION_END[i]) {
        break
      }
      i++
    }
    var selectedSections = 0
    if (i < Constants.COURSE_SECTION_COUNT) {
      selectedSections = selectedSections or (1 shl i)
      if (i < 4 && 2 * time > Constants.SECTION_START[i] + Constants.SECTION_END[i]) {
        selectedSections = selectedSections or (1 shl (i + 1))
      }
    }
    viewModel.selectedSections.setValue(viewModel, viewModel::selectedSections, selectedSections)
  }
}

class ClassroomViewModel : ViewModel() {
  var selectedDay = mutableStateOf(0)
  var selectedBuilding = mutableStateOf(0)
  var selectedSections = mutableStateOf(0)
  var resultText by mutableStateOf("")
    private set
  private val noSectionChosenFlow = MutableSharedFlow<Unit>()
  val noSectionChosenPublisher: Flow<Unit>
    get() = noSectionChosenFlow
  var loading by mutableStateOf(false)

  private val BUILDING_VALUE = arrayOf("Ⅳ", "II", "I")

  suspend fun query(context: Context) {
    val sections = selectedSections.value
    if (sections == 0) {
      noSectionChosenFlow.emit(Unit)
      return
    }
    val dayId = selectedDay.value
    val dateLong = System.currentTimeMillis() + TimeUtil.ONE_DAY * dayId
    val termStart = RemoteConfig.getTermStartTime()
    val dayIndex = ((dateLong - termStart) / TimeUtil.ONE_DAY).toInt()
    val week = dayIndex / 7 + 1
    val day = dayIndex % 7
    loading = true
    resultText = try {
      val dao = CourseQueryDao.getInstance(context)
      val building = BUILDING_VALUE[selectedBuilding.value]
      val allRooms = dao.queryClassroomSet(building)
      val ruledOutRooms = dao.queryClassroom(building, week, day, sections).toSet()
      val result = (allRooms - ruledOutRooms)
        .joinToString(separator = "  ") { it.replace('-', '_') }
      if (result.isBlank()) {
        context.getString(R.string.text_classroom_no_info)
      } else {
        result
      }
    } catch (e: Exception) {
      context.getString(R.string.text_classroom_fail)
    }
    loading = false
  }
}
