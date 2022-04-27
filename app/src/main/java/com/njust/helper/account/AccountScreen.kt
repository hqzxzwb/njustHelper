package com.njust.helper.account

import android.text.InputType
import androidx.appcompat.widget.AppCompatEditText
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.njust.helper.R
import com.njust.helper.compose.material.AndroidTextInputLayout
import com.njust.helper.compose.material.DarkActionBarAppCompatTheme
import com.njust.helper.compose.material.NavigationHome
import com.njust.helper.compose.material.textColors
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest

@Stable
class AccountScreenViewModel(
  val onClickHome: () -> Unit,
  val onClickConfirm: () -> Unit,
) {
  var stuid: String by mutableStateOf("")
  var jwcPwd: String by mutableStateOf("")
  var libPwd: String by mutableStateOf("")
  val snackbarMessageFlow = MutableSharedFlow<String>()
}

@Composable
fun AccountScreen(
  vm: AccountScreenViewModel,
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
        title = { Text(text = stringResource(id = R.string.title_activity_account)) },
        navigationIcon = { NavigationHome(vm.onClickHome) },
        actions = {
          IconButton(onClick = vm.onClickConfirm) {
            Icon(imageVector = Icons.Filled.Check, contentDescription = null)
          }
        },
      )
    },
    scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
  ) {
    Column(
      modifier = Modifier.padding(
        horizontal = dimensionResource(id = R.dimen.activity_horizontal_margin),
        vertical = dimensionResource(id = R.dimen.activity_vertical_margin),
      ),
    ) {
      val configureEditTextPassword: AppCompatEditText.() -> Unit = {
        setSelectAllOnFocus(true)
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
      }
      AndroidTextInputLayout(
        text = vm.stuid,
        hint = stringResource(id = R.string.hint_account_input_id),
        onTextChanged = { vm.stuid = it },
        configureEditText = { setSelectAllOnFocus(true) },
      )
      AndroidTextInputLayout(
        text = vm.jwcPwd,
        hint = stringResource(id = R.string.hint_account_input_jwc_password),
        onTextChanged = { vm.jwcPwd = it },
        configureEditText = configureEditTextPassword,
      )
      AndroidTextInputLayout(
        text = vm.libPwd,
        hint = stringResource(id = R.string.hint_account_input_lib_password),
        onTextChanged = { vm.libPwd = it },
        configureEditText = configureEditTextPassword,
      )
      Text(
        text = stringResource(id = R.string.message_account_passwords),
        style = MaterialTheme.typography.caption,
        color = MaterialTheme.textColors.secondary,
      )
    }
  }
}
