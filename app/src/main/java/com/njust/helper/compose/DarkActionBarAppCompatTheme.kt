package com.njust.helper.compose

import android.content.Context
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.appcompattheme.createAppCompatTheme

@Composable
fun DarkActionBarAppCompatTheme(
  context: Context = LocalContext.current,
  readColors: Boolean = true,
  readTypography: Boolean = true,
  shapes: Shapes = MaterialTheme.shapes,
  content: @Composable () -> Unit
) {
  val themeParams = remember(context.theme) {
    context.createAppCompatTheme(
      readColors = readColors,
      readTypography = readTypography
    )
  }

  val colors = (themeParams.colors ?: MaterialTheme.colors)
    .copy(onPrimary = Color.White)

  MaterialTheme(
    colors = colors,
    typography = themeParams.typography ?: MaterialTheme.typography,
    shapes = shapes,
  ) {
    // We update the LocalContentColor to match our onBackground. This allows the default
    // content color to be more appropriate to the theme background
    CompositionLocalProvider(
      LocalContentColor provides MaterialTheme.colors.onBackground,
      content = content
    )
  }
}
