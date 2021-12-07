package com.njust.helper.coursequery

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.njust.helper.R
import com.njust.helper.RemoteConfig
import com.njust.helper.activity.BaseActivity
import com.njust.helper.tools.Constants
import com.njust.helper.tools.TimeUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * 自习室查询
 *
 * @author zwb
 */
class ClassroomActivity : BaseActivity() {
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
    lifecycleScope.launchWhenCreated {
      viewModel.noSectionChosenPublisher.collect {
        viewModel.snackbarHostState.showSnackbar(
          getString(R.string.toast_cr_choose_one_section)
        )
      }
    }
  }

  override fun layoutRes(): Int = 0

  override fun layout() {
    setContent { Screen(viewModel) }
  }

  @Composable
  private fun Screen(viewModel: ClassroomViewModel) {
    val result = viewModel.resultText
    val snackbarHostState = viewModel.snackbarHostState
    MaterialTheme {
      Scaffold(
        floatingActionButton = {
          QueryButton {
            lifecycleScope.launch {
              viewModel.query(this@ClassroomActivity)
            }
          }
        },
        snackbarHost = {
          SnackbarHost(
            hostState = snackbarHostState
          )
        },
        content = {
          Column(
            modifier = Modifier
              .scrollable(state = rememberScrollState(), orientation = Orientation.Vertical)
          ) {
            Spacer(modifier = Modifier.height(16.dp))
            ControlCard(
              viewModel.selectedDay,
              viewModel.selectedBuilding,
              viewModel.selectedSections,
            )
            Spacer(modifier = Modifier.height(8.dp))
            ResultCard(
              result
            )
            Spacer(modifier = Modifier.height(88.dp))
          }
        }
      )
    }
  }

  @Composable
  private fun ControlCard(
    selectedDayState: MutableState<Int>,
    selectedBuildingState: MutableState<Int>,
    selectedSectionsState: MutableState<Int>,
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      elevation = 2.dp,
    ) {
      Column {
        Text(text = stringResource(R.string.label_classroom_date))
        Row(verticalAlignment = Alignment.CenterVertically) {
          DayRadioButton(
            selectedDayState = selectedDayState,
            index = 0,
            text = stringResource(R.string.radio_classroom_today)
          )
          DayRadioButton(
            selectedDayState = selectedDayState,
            index = 1,
            text = stringResource(R.string.radio_classroom_tomorrow)
          )
          DayRadioButton(
            selectedDayState = selectedDayState,
            index = 2,
            text = stringResource(R.string.radio_classroom_day_after)
          )
        }
        Text(text = stringResource(R.string.label_classroom_buildings))
        Row(verticalAlignment = Alignment.CenterVertically) {
          BuildingRadioButton(
            selectedBuildingState = selectedBuildingState,
            index = 0,
            text = stringResource(R.string.radio_classroom_building_four)
          )
          BuildingRadioButton(
            selectedBuildingState = selectedBuildingState,
            index = 1,
            text = stringResource(R.string.radio_classroom_building_two)
          )
          BuildingRadioButton(
            selectedBuildingState = selectedBuildingState,
            index = 2,
            text = stringResource(R.string.radio_classroom_building_one)
          )
        }
        Text(text = stringResource(R.string.label_classroom_section))
        stringArrayResource(id = R.array.sections)
          .forEachIndexed { index, s ->
            SectionCheckBox(
              selectedSectionsState = selectedSectionsState,
              index = index,
              text = s,
            )
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
  private fun ResultCard(
    resultText: String,
  ) {
    Card(
      modifier = Modifier.fillMaxWidth(),
      elevation = 2.dp,
    ) {
      Text(
        modifier = Modifier.padding(8.dp),
        text = resultText,
      )
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
  private val noSectionChosenFlow = MutableSharedFlow<Unit>()
  val noSectionChosenPublisher: Flow<Unit>
    get() = noSectionChosenFlow
  val snackbarHostState by mutableStateOf(SnackbarHostState())

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
  }
}
