package com.njust.helper.course

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
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
                modifier = Modifier.weight(1F),
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
private fun PickWeekItem(
  modifier: Modifier,
  weekIndex: Int,
  enabled: Boolean,
  onClick: () -> Unit
) {
  TextButton(
    modifier = modifier.height(40.dp),
    enabled = enabled,
    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.onSurface),
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
