package com.njust.helper.compose.material

import android.content.Context
import android.content.res.TypedArray
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.use
import com.njust.helper.R

@Immutable
class TextColors(
  val primaryTextColor: Color,
  val secondaryTextColor: Color,
)

val LocalTextColors = compositionLocalOf<TextColors> {
  noLocalProvidedFor("LocalTextColors")
}

private fun noLocalProvidedFor(name: String): Nothing {
  error("CompositionLocal $name not present")
}

fun Context.readTextColors(): TextColors = obtainStyledAttributes(R.styleable.AppCompatThemeAdapterThemeTextColors).use { ta ->
  val primaryTextColor = ta.getComposeColor(R.styleable.AppCompatThemeAdapterThemeTextColors_android_textColorPrimary)
  val secondaryTextColor = ta.getComposeColor(R.styleable.AppCompatThemeAdapterThemeTextColors_android_textColorSecondary)
  TextColors(primaryTextColor, secondaryTextColor)
}

internal fun TypedArray.getComposeColor(
  index: Int,
  fallbackColor: Color = Color.Unspecified
): Color = if (hasValue(index)) Color(getColorOrThrow(index)) else fallbackColor

val MaterialTheme.textColors: TextColors
  @Composable get() = LocalTextColors.current
