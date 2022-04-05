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
import androidx.compose.runtime.Stable
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
import com.njust.helper.compose.material.DarkActionBarAppCompatTheme
import com.zwb.commonlibs.utils.NoOpFunction
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlin.reflect.KMutableProperty0

@Stable
class ClassroomViewModel(
  val onClickQuery: () -> Unit,
  val onClickHome: () -> Unit,
) {
  var selectedDay by mutableStateOf(0)
  var selectedBuilding by mutableStateOf(0)
  var selectedSections by mutableStateOf(0)
  var resultText by mutableStateOf("")
  var isRefreshing by mutableStateOf(false)
  val noSectionChosenFlow = MutableSharedFlow<Unit?>()
}

@Composable
fun ClassroomScreen(
  vm: ClassroomViewModel,
) {
  val snackbarHostState = remember { SnackbarHostState() }
  val textNoSectionChosen = stringResource(id = R.string.toast_cr_choose_one_section)
  LaunchedEffect(key1 = snackbarHostState, block = {
    vm.noSectionChosenFlow.collectLatest {
      if (it != null) {
        snackbarHostState.showSnackbar(textNoSectionChosen)
      }
    }
  })
  DarkActionBarAppCompatTheme {
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(id = R.string.title_activity_cr)) },
          navigationIcon = {
            IconButton(onClick = vm.onClickHome) {
              Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
            }
          }
        )
      },
      floatingActionButton = {
        QueryButton(vm.onClickQuery)
      },
      snackbarHost = {
        SnackbarHost(
          hostState = snackbarHostState
        )
      },
      content = {
        SwipeRefresh(
          state = rememberSwipeRefreshState(isRefreshing = vm.isRefreshing),
          onRefresh = NoOpFunction,
          swipeEnabled = false,
        ) {
          Column(
            modifier = Modifier
              .verticalScroll(state = rememberScrollState())
          ) {
            Spacer(modifier = Modifier.height(16.dp))
            ControlCard(vm)
            Spacer(modifier = Modifier.height(8.dp))
            ResultCard(vm.resultText)
            Spacer(modifier = Modifier.height(88.dp))
          }
        }
      }
    )
  }
}

@Composable
private fun ControlCard(
  vm: ClassroomViewModel,
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
        val dayNames = listOf(
          stringResource(R.string.radio_classroom_today),
          stringResource(R.string.radio_classroom_tomorrow),
          stringResource(R.string.radio_classroom_day_after),
        )
        dayNames.forEachIndexed { index, dayName ->
          DayRadioButton(
            vm = vm,
            index = index,
            text = dayName,
          )
        }
      }
      Label(text = stringResource(R.string.label_classroom_buildings))
      Row(verticalAlignment = Alignment.CenterVertically) {
        val buildingNames = listOf(
          stringResource(R.string.radio_classroom_building_four),
          stringResource(R.string.radio_classroom_building_two),
          stringResource(R.string.radio_classroom_building_one),
          stringResource(R.string.radio_classroom_building_jiangyin),
        )
        buildingNames.forEachIndexed { index, buildingName ->
          BuildingRadioButton(
            vm = vm,
            index = index,
            text = buildingName,
          )
        }
      }
      Label(text = stringResource(R.string.label_classroom_section))
      stringArrayResource(id = R.array.sections)
        .forEachIndexed { index, s ->
          SectionCheckBox(
            vm = vm,
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
private fun DayRadioButton(vm: ClassroomViewModel, index: Int, text: String) {
  Row(modifier = Modifier.clickable { vm.selectedDay = index }) {
    RadioButton(selected = vm.selectedDay == index, onClick = null)
    Text(text = text)
  }
}

@Composable
private fun BuildingRadioButton(
  vm: ClassroomViewModel,
  index: Int,
  text: String,
) {
  Row(modifier = Modifier.clickable { vm.selectedBuilding = index }) {
    RadioButton(selected = vm.selectedBuilding == index, onClick = null)
    Text(text = text)
  }
}

@Composable
private fun SectionCheckBox(
  vm: ClassroomViewModel,
  index: Int,
  text: String,
) {
  val maskedIndex = 1 shl index
  val checked = (vm.selectedSections and maskedIndex) != 0
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable {
        vm.selectedSections = if (!checked) {
          vm.selectedSections or maskedIndex
        } else {
          vm.selectedSections and maskedIndex.inv()
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
    ClassroomViewModel(
      onClickHome = NoOpFunction,
      onClickQuery = NoOpFunction,
    ).apply {
      selectedSections = 5
      resultText = "Result"
      isRefreshing = true
    }
  )
}
