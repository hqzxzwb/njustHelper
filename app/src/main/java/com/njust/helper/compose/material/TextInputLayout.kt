package com.njust.helper.compose.material

import androidx.appcompat.widget.AppCompatEditText
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.zwb.commonlibs.utils.NoOpFunction

@Composable
fun AndroidTextInputLayout(
  hint: String,
  text: String,
  onTextChanged: (text: String) -> Unit,
  configureEditText: AppCompatEditText.() -> Unit = NoOpFunction,
) {
  var rememberedText by remember { mutableStateOf("") }
  AndroidView(
    modifier = Modifier.fillMaxWidth(),
    factory = { context ->
      val textInputLayout = TextInputLayout(context)
      val editText = AppCompatEditText(context)
      editText.hint = hint
      editText.doAfterTextChanged {
        val string = it?.toString().orEmpty()
        onTextChanged(string)
        rememberedText = string
      }
      editText.configureEditText()
      textInputLayout.addView(editText)
      textInputLayout
    },
    update = {
      val editText = it.editText!!
      if (rememberedText != text) {
        editText.setText(text)
        rememberedText = text
      }
    },
  )
}
