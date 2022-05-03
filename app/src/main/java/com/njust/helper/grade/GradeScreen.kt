package com.njust.helper.grade

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.njust.helper.R
import com.njust.helper.compose.material.DarkActionBarAppCompatTheme
import com.njust.helper.compose.material.NavigationHome
import com.njust.helper.compose.material.VerticalDivider
import com.njust.helper.compose.material.textColors
import com.njust.helper.shared.api.GradeItem
import kotlinx.coroutines.flow.collectLatest

private val contentTextStyle: TextStyle
  @Composable
  @ReadOnlyComposable
  get() = MaterialTheme.typography.caption
    .copy(
      color = MaterialTheme.textColors.secondary,
      textAlign = TextAlign.Center,
    )
private val tableHeadTextStyle: TextStyle
  @Composable
  @ReadOnlyComposable
  get() = MaterialTheme.typography.caption
    .copy(
      fontWeight = FontWeight.SemiBold,
      textAlign = TextAlign.Center,
    )
private val titleStyle: TextStyle
  @Composable
  @ReadOnlyComposable
  get() = MaterialTheme.typography.subtitle1
    .copy(
      color = MaterialTheme.textColors.primary,
      textAlign = TextAlign.Center,
    )

@Composable
fun GradeScreen(
  vm: GradeVm,
) = DarkActionBarAppCompatTheme {
  val snackbarHostState = remember { SnackbarHostState() }
  LaunchedEffect(key1 = snackbarHostState, block = {
    vm.snackbarMessageFlow.collectLatest {
      snackbarHostState.showSnackbar(it)
    }
  })
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(text = stringResource(id = R.string.title_activity_grade)) },
        navigationIcon = { NavigationHome(vm.onClickHome) },
      )
    },
    scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
  ) {
    SwipeRefresh(
      state = rememberSwipeRefreshState(isRefreshing = vm.loading),
      onRefresh = vm.onClickRefresh,
    ) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
      ) {
        vm.terms.forEach { gradeTermVm ->
          GradeTerm(gradeTermVm = gradeTermVm)
        }
        if (vm.terms.isNotEmpty()) {
          item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
              modifier = Modifier.fillMaxWidth(),
              text = "全部已修课程",
              style = titleStyle,
            )
            vm.totalMean?.let {
              GradeMeanTable(
                meanGradeVm = it,
              )
            }
          }
        }
      }
    }
  }
}

private fun LazyListScope.GradeTerm(
  gradeTermVm: GradeTermVm,
) {
  item {
    Spacer(modifier = Modifier.height(20.dp))
    Text(
      modifier = Modifier.fillMaxWidth(),
      text = "学期：${gradeTermVm.termName}",
      style = titleStyle,
    )
    Spacer(modifier = Modifier.height(12.dp))
    LightDivider()
    GradeTableHead()
    LightDivider()
  }
  items(gradeTermVm.items) { item ->
    GradeTableItem(item = item)
    LightDivider()
  }
  item {
    GradeMeanTable(
      meanGradeVm = gradeTermVm.mean,
    )
    Divider()
  }
}

@Composable
private fun GradeMeanTable(
  meanGradeVm: MeanGradeVm,
) {
  Column(
    modifier = Modifier
      .padding(top = 12.dp, bottom = 20.dp)
      .fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Row {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = "课程类型",
          modifier = Modifier.padding(4.dp),
          style = tableHeadTextStyle,
        )
        Text(
          text = "所有课程",
          modifier = Modifier.padding(4.dp),
          style = tableHeadTextStyle,
        )
        Text(
          text = "必修课程",
          modifier = Modifier.padding(4.dp),
          style = tableHeadTextStyle,
        )
      }
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = "总学分",
          modifier = Modifier.padding(4.dp),
          style = tableHeadTextStyle,
        )
        Text(
          text = meanGradeVm.totalWeight,
          modifier = Modifier.padding(4.dp),
          style = contentTextStyle,
        )
        Text(
          text = meanGradeVm.requiredWeight,
          modifier = Modifier.padding(4.dp),
          style = contentTextStyle,
        )
      }
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = "加权平均分",
          modifier = Modifier.padding(4.dp),
          style = tableHeadTextStyle,
        )
        Text(
          text = meanGradeVm.totalGrade,
          modifier = Modifier.padding(4.dp),
          style = contentTextStyle,
        )
        Text(
          text = meanGradeVm.requiredGrade,
          modifier = Modifier.padding(4.dp),
          style = contentTextStyle,
        )
      }
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = "GPA",
          modifier = Modifier.padding(4.dp),
          style = tableHeadTextStyle,
        )
        Text(
          text = meanGradeVm.totalPoint,
          modifier = Modifier.padding(4.dp),
          style = contentTextStyle,
        )
        Text(
          text = meanGradeVm.requiredPoint,
          modifier = Modifier.padding(4.dp),
          style = contentTextStyle,
        )
      }
    }
    if (meanGradeVm.hasUnrecognizedGrade) {
      Text(
        text = "未评教的科目没有参与计算",
        style = contentTextStyle,
      )
    }
  }
}

@Composable
private fun GradeTableHead() {
  GradeItemRow(
    text1 = "课程",
    text2 = "成绩",
    text3 = "学分",
    text4 = "类型",
    text5 = "绩点",
    isTableHead = true,
  )
}

@Composable
private fun GradeTableItem(item: GradeItem) {
  GradeItemRow(
    text1 = item.courseName,
    text2 = item.gradeText,
    text3 = "%.2f".format(item.weight),
    text4 = item.type,
    text5 = "%.2f".format(item.point),
    isTableHead = false,
  )
}

@Composable
private fun GradeItemRow(
  text1: String,
  text2: String,
  text3: String,
  text4: String,
  text5: String,
  isTableHead: Boolean,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(IntrinsicSize.Max)
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    val style = if (isTableHead) tableHeadTextStyle else contentTextStyle
    Text(
      text = text1,
      modifier = Modifier.weight(1F),
      style = style,
    )
    VerticalLightDivider()
    Text(
      text = text2,
      modifier = Modifier.width(48.dp),
      style = style,
    )
    VerticalLightDivider()
    Text(
      text = text3,
      modifier = Modifier.width(48.dp),
      style = style,
    )
    VerticalLightDivider()
    Text(
      text = text4,
      modifier = Modifier.width(48.dp),
      style = style,
    )
    VerticalLightDivider()
    Text(
      text = text5,
      modifier = Modifier.width(48.dp),
      style = style,
    )
  }
}

@Composable
private fun LightDivider() {
  Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.06F))
}

@Composable
private fun VerticalLightDivider() {
  VerticalDivider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.06F))
}
