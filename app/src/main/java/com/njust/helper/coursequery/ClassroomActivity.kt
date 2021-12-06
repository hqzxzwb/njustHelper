package com.njust.helper.coursequery

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.njust.helper.R
import com.njust.helper.RemoteConfig
import com.njust.helper.activity.BaseActivity
import com.njust.helper.databinding.ActivityClassroomBinding
import com.njust.helper.tools.Constants
import com.njust.helper.tools.TimeUtil
import kotlinx.coroutines.launch

/**
 * 自习室查询
 *
 * @author zwb
 */
class ClassroomActivity : BaseActivity() {
  lateinit var binding: ActivityClassroomBinding
  private val viewModel by viewModels<ClassroomViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

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

  override fun layoutRes(): Int = 0

  override fun layout() {
    setContent { Screen(viewModel) }
  }

  @Composable
  private fun Screen(viewModel: ClassroomViewModel) {
    val result = viewModel.resultText
    MaterialTheme {
      Column {
        ControlCard(viewModel)
        ResultCard(result)
      }
      QueryButton {
        lifecycleScope.launch {
          viewModel.query(this@ClassroomActivity)
        }
      }
    }
  }

  @Composable
  private fun ControlCard(viewModel: ClassroomViewModel) {
    val selectedDay = viewModel.selectedDay
    val selectedBuilding = viewModel.selectedBuilding
    val selectedSections = viewModel.selectedSections
    Card {
      Column {
        Text(text = stringResource(R.string.label_classroom_date))
        Row(verticalAlignment = Alignment.CenterVertically) {
          DayRadioButton(
            selectedDayState = selectedDay,
            index = 0,
            text = stringResource(R.string.radio_classroom_today)
          )
          DayRadioButton(
            selectedDayState = selectedDay,
            index = 1,
            text = stringResource(R.string.radio_classroom_tomorrow)
          )
          DayRadioButton(
            selectedDayState = selectedDay,
            index = 2,
            text = stringResource(R.string.radio_classroom_day_after)
          )
        }
        Text(text = stringResource(R.string.label_classroom_buildings))
        Row(verticalAlignment = Alignment.CenterVertically) {
          BuildingRadioButton(
            selectedBuildingState = selectedBuilding,
            index = 0,
            text = stringResource(R.string.radio_classroom_building_four)
          )
          BuildingRadioButton(
            selectedBuildingState = selectedBuilding,
            index = 1,
            text = stringResource(R.string.radio_classroom_building_two)
          )
          BuildingRadioButton(
            selectedBuildingState = selectedBuilding,
            index = 2,
            text = stringResource(R.string.radio_classroom_building_one)
          )
        }
        Text(text = stringResource(R.string.label_classroom_section))
        stringArrayResource(id = R.array.sections)
          .forEachIndexed { index, s ->
            SectionCheckBox(selectedSectionsState = selectedSections, index = index, text = s)
          }
      }
    }
  }

  @Composable
  private fun DayRadioButton(selectedDayState: MutableState<Int>, index: Int, text: String) {
    var selectedDay by remember { selectedDayState }
    RadioButton(selected = selectedDay == index, onClick = { selectedDay = index })
    Text(text = text)
  }

  @Composable
  private fun BuildingRadioButton(
    selectedBuildingState: MutableState<Int>,
    index: Int,
    text: String
  ) {
    var selectedBuilding by remember {
      selectedBuildingState
    }
    RadioButton(selected = selectedBuilding == index, onClick = { selectedBuilding = index })
    Text(text = text)
  }

  @Composable
  private fun SectionCheckBox(selectedSectionsState: MutableState<Int>, index: Int, text: String) {
    var selectedSections by remember {
      selectedSectionsState
    }
    val maskedIndex = 1 shl index
    Row {
      Checkbox(checked = (selectedSections and maskedIndex) != 0, onCheckedChange = {
        selectedSections = if (it) {
          selectedSections or maskedIndex
        } else {
          selectedSections and maskedIndex.inv()
        }
      })
      Text(text = text)
    }
  }

  @Composable
  private fun ResultCard(resultText: String) {
    Card {
      Text(text = resultText)
    }
  }

  @Composable
  private fun QueryButton(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {

    }
  }
}

class ClassroomViewModel : ViewModel() {
  var selectedDay = mutableStateOf(0)
  var selectedBuilding = mutableStateOf(0)
  var selectedSections = mutableStateOf(0)
  var resultText by mutableStateOf("")
    private set

  private val BUILDING_VALUE = arrayOf("Ⅳ", "II", "I")

  suspend fun query(context: Context) {
    val sections = selectedSections.value
//    if (sections == 0) {
//      showSnack(R.string.toast_cr_choose_one_section)
//      return
//    }
    val dayId = selectedDay.value
    val dateLong = System.currentTimeMillis() + TimeUtil.ONE_DAY * dayId
    val termStart = RemoteConfig.getTermStartTime()
    val dayIndex = ((dateLong - termStart) / TimeUtil.ONE_DAY).toInt()
    val week = dayIndex / 7 + 1
    val day = dayIndex % 7
    try {
      val dao = CourseQueryDao.getInstance(context)
      val building = BUILDING_VALUE[selectedBuilding.value]
      val allRooms = dao.queryClassroomSet(building)
      val ruledOutRooms = dao.queryClassroom(building, week, day, sections).toSet()
      val result = (allRooms - ruledOutRooms)
        .joinToString(separator = "  ") { it.replace('-', '_') }
      resultText = if (result.isBlank()) {
        context.getString(R.string.text_classroom_no_info)
      } else {
        result
      }
    } catch (e: Exception) {
      resultText = context.getString(R.string.text_classroom_fail)
    }
  }
}
