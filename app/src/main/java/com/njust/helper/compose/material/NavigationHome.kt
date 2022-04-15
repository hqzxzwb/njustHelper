package com.njust.helper.compose.material

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable

@Composable
fun NavigationHome(
  onClick: () -> Unit,
) {
  IconButton(onClick = onClick) {
    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
  }
}
