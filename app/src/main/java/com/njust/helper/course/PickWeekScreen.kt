package com.njust.helper.course

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.njust.helper.R
import com.njust.helper.compose.DarkActionBarAppCompatTheme
import com.njust.helper.tools.Constants
import com.zwb.commonlibs.utils.NoOpFunction

@Composable
fun PickWeekScreen(selectedWeekIndex: Int, onSelectWeek: (Int) -> Unit) {
  val weekIndexes = 1..Constants.MAX_WEEK_COUNT
  DarkActionBarAppCompatTheme {
    Column(
      modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
      Text(
        text = "选择周次",
        style = MaterialTheme.typography.h6,
      )
      Spacer(modifier = Modifier.height(8.dp))
      weekIndexes.chunked(5)
        .forEach { chunkedWeekIndexes ->
          Row(
            modifier = Modifier.fillMaxWidth(),
          ) {
            chunkedWeekIndexes.forEach { weekIndex ->
              PickWeekItem(
                weekIndex = weekIndex,
                enabled = selectedWeekIndex != weekIndex,
              ) {
                onSelectWeek(weekIndex)
              }
            }
          }
        }
    }
  }
}

@Composable
private fun RowScope.PickWeekItem(
  weekIndex: Int,
  enabled: Boolean,
  onClick: () -> Unit
) {
  TextButton(
    modifier = Modifier
      .height(40.dp)
      .weight(1F),
    enabled = enabled,
    onClick = onClick,
  ) {
    Text(
      text = stringResource(R.string.button_course_pick_week, weekIndex),
    )
  }
}

@Preview
@Composable
private fun PreviewPickWeekScreen() {
  PickWeekScreen(
    selectedWeekIndex = 6,
    onSelectWeek = NoOpFunction,
  )
}
