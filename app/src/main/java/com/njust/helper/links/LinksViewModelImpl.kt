package com.njust.helper.links

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.njust.helper.shared.api.CommonLink
import com.njust.helper.shared.links.LinksViewModel

class LinksViewModelImpl : LinksViewModel() {
  override var items: List<CommonLink> by mutableStateOf(listOf())

  override var loading: Boolean by mutableStateOf(false)
}
