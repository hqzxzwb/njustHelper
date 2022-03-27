package com.njust.helper.compose.material

import android.content.Context
import android.content.res.TypedArray
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.use
import com.njust.helper.R

@Immutable
class TextColors(
  val primary: Color,
  val secondary: Color,
  val tertiary: Color,
)

val LocalTextColors = compositionLocalOf<TextColors> {
  noLocalProvidedFor("LocalTextColors")
}

private fun noLocalProvidedFor(name: String): Nothing {
  error("CompositionLocal $name not present")
}

fun Context.readTextColors(): TextColors = obtainStyledAttributes(R.styleable.AppCompatThemeAdapterThemeTextColors).use { ta ->
  TextColors(
    ta.getComposeColor(R.styleable.AppCompatThemeAdapterThemeTextColors_android_textColorPrimary),
    ta.getComposeColor(R.styleable.AppCompatThemeAdapterThemeTextColors_android_textColorSecondary),
    ta.getComposeColor(R.styleable.AppCompatThemeAdapterThemeTextColors_android_textColorTertiary),
  )
}

internal fun TypedArray.getComposeColor(
  index: Int,
  fallbackColor: Color = Color.Unspecified
): Color = if (hasValue(index)) Color(getColorOrThrow(index)) else fallbackColor

val MaterialTheme.textColors: TextColors
  @Composable @ReadOnlyComposable get() = LocalTextColors.current
