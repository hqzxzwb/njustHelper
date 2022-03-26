package com.njust.helper.compose

import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import com.njust.helper.compose.drawablepainter.rememberDrawablePainter

@Composable
fun rememberDrawableResourcePainter(@DrawableRes resId: Int): Painter {
  val drawable = AppCompatResources.getDrawable(LocalContext.current, resId)
  return rememberDrawablePainter(drawable = drawable)
}
