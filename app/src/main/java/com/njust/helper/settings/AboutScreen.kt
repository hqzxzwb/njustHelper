package com.njust.helper.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.njust.helper.R
import com.njust.helper.compose.DarkActionBarAppCompatTheme
import com.zwb.commonlibs.utils.NoOpFunction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf

@Composable
fun AboutScreen(
  onClickHome: () -> Unit,
  onClickFeedback: () -> Unit,
  onClickComment: () -> Unit,
  onClickUpdateLog: () -> Unit,
  onClickShare: () -> Unit,
  snackbarMessageFlow: Flow<String>,
) {
  val snackbarHostState = remember { SnackbarHostState() }
  LaunchedEffect(key1 = snackbarHostState) {
    snackbarMessageFlow.collectLatest {
      snackbarHostState.showSnackbar(it)
    }
  }
  DarkActionBarAppCompatTheme {
    Scaffold(
      topBar = {
        TopAppBar(
          navigationIcon = {
            IconButton(onClick = onClickHome) {
              Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
            }
          },
          actions = {
            IconButton(onClick = onClickShare) {
              Icon(imageVector = Icons.Filled.Share, contentDescription = null)
            }
          },
          title = { Text(text = stringResource(id = R.string.title_activity_about)) },
        )
      },
      bottomBar = { Bottom(onClickFeedback, onClickComment, onClickUpdateLog) },
      scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    ) {
      Content()
    }
  }
}

@Composable
private fun Content() {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .scrollable(
        state = rememberScrollState(),
        orientation = Orientation.Vertical,
      )
      .fillMaxSize(),
  ) {
    Text(
      text = stringResource(id = R.string.label_about_upper),
      modifier = Modifier.padding(
        start = 16.dp,
        end = 16.dp,
        top = 8.dp,
      ),
      style = MaterialTheme.typography.body1,
    )
    Image(
      painter = painterResource(id = R.drawable.mm_qrcode),
      contentDescription = "二维码",
      modifier = Modifier.padding(top = 16.dp),
    )
    Text(
      text = stringResource(id = R.string.label_about_lower),
      modifier = Modifier.padding(
        start = 16.dp,
        end = 16.dp,
        top = 16.dp,
      ),
      style = MaterialTheme.typography.body1,
    )
  }
}

@Composable
private fun Bottom(
  onClickFeedback: () -> Unit,
  onClickComment: () -> Unit,
  onClickUpdateLog: () -> Unit,
) {
  Divider(
    color = MaterialTheme.colors.onSecondary.copy(alpha = 0.2F),
    thickness = 1.dp,
  )
  Row(
    modifier = Modifier.fillMaxWidth(),
  ) {
    BottomButton(
      text = stringResource(id = R.string.button_about_advice),
      onClick = onClickFeedback,
    )
    BottomButton(
      text = stringResource(id = R.string.button_comment),
      onClick = onClickComment,
    )
    BottomButton(
      text = stringResource(id = R.string.update_log),
      onClick = onClickUpdateLog,
    )
  }
}

@Composable
private fun RowScope.BottomButton(
  text: String,
  onClick: () -> Unit,
) {
  TextButton(
    onClick = onClick,
    modifier = Modifier
      .weight(1F)
      .padding(start = 12.dp, end = 12.dp),
    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.onSurface),
  ) {
    Text(text = text)
  }
}

@Preview
@Composable
private fun Preview() {
  AboutScreen(
    onClickHome = NoOpFunction,
    onClickFeedback = NoOpFunction,
    onClickComment = NoOpFunction,
    onClickUpdateLog = NoOpFunction,
    onClickShare = NoOpFunction,
    snackbarMessageFlow = flowOf(),
  )
}
