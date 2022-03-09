package com.njust.helper.classroom

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.njust.helper.R
import com.njust.helper.compose.DarkActionBarAppCompatTheme
import com.zwb.commonlibs.utils.NoOpFunction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow

@Composable
fun ClassroomScreen(
  selectedDayState: MutableState<Int>,
  selectedBuildingState: MutableState<Int>,
  selectedSectionsState: MutableState<Int>,
  resultText: String,
  isRefreshing: Boolean,
  noSectionChosenPublisher: Flow<Unit>,
  onClickQuery: () -> Unit,
  onClickHome: () -> Unit,
) {
  val snackbarHostState = remember { SnackbarHostState() }
  val textNoSectionChosen = stringResource(id = R.string.toast_cr_choose_one_section)
  LaunchedEffect(key1 = snackbarHostState, block = {
    noSectionChosenPublisher.collectLatest {
      snackbarHostState.showSnackbar(textNoSectionChosen)
    }
  })
  DarkActionBarAppCompatTheme {
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(id = R.string.title_activity_cr)) },
          navigationIcon = {
            IconButton(onClick = onClickHome) {
              Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
            }
          }
        )
      },
      floatingActionButton = {
        QueryButton(onClickQuery)
      },
      snackbarHost = {
        SnackbarHost(
          hostState = snackbarHostState
        )
      },
      content = {
        SwipeRefresh(
          state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
          onRefresh = NoOpFunction,
          swipeEnabled = false,
        ) {
          Column(
            modifier = Modifier
              .verticalScroll(state = rememberScrollState())
          ) {
            Spacer(modifier = Modifier.height(16.dp))
            ControlCard(
              selectedDayState,
              selectedBuildingState,
              selectedSectionsState,
            )
            Spacer(modifier = Modifier.height(8.dp))
            ResultCard(resultText)
            Spacer(modifier = Modifier.height(88.dp))
          }
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
    elevation = 2.dp,
  ) {
    Column(
      modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()
    ) {
      Label(text = stringResource(R.string.label_classroom_date))
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
      Label(text = stringResource(R.string.label_classroom_buildings))
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
        BuildingRadioButton(
          selectedBuildingState = selectedBuildingState,
          index = 3,
          text = stringResource(R.string.radio_classroom_building_jiangyin)
        )
      }
      Label(text = stringResource(R.string.label_classroom_section))
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
private fun Label(text: String) {
  Text(
    modifier = Modifier.padding(start = 8.dp),
    text = text,
    style = MaterialTheme.typography.caption,
  )
}

@Composable
private fun DayRadioButton(selectedDayState: MutableState<Int>, index: Int, text: String) {
  var selectedDay by remember { selectedDayState }
  Row(modifier = Modifier.clickable { selectedDay = index }) {
    RadioButton(selected = selectedDay == index, onClick = null)
    Text(text = text)
  }
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
  Row(modifier = Modifier.clickable { selectedBuilding = index }) {
    RadioButton(selected = selectedBuilding == index, onClick = null)
    Text(text = text)
  }
}

@Composable
private fun SectionCheckBox(selectedSectionsState: MutableState<Int>, index: Int, text: String) {
  var selectedSections by remember {
    selectedSectionsState
  }
  val maskedIndex = 1 shl index
  val checked = (selectedSections and maskedIndex) != 0
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable {
        selectedSections = if (!checked) {
          selectedSections or maskedIndex
        } else {
          selectedSections and maskedIndex.inv()
        }
      }
  ) {
    Checkbox(checked = checked, onCheckedChange = null)
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
    Icon(
      imageVector = Icons.Filled.Check,
      contentDescription = null,
      tint = colorResource(id = android.R.color.white)
    )
  }
}

@Preview
@Composable
private fun Preview() {
  ClassroomScreen(
    selectedDayState = remember { mutableStateOf(0) },
    selectedBuildingState = remember { mutableStateOf(0) },
    selectedSectionsState = remember { mutableStateOf(5) },
    resultText = "Result",
    isRefreshing = true,
    noSectionChosenPublisher = flow { },
    onClickQuery = NoOpFunction,
    onClickHome = NoOpFunction,
  )
}
