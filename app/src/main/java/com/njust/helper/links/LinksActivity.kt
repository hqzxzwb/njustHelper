package com.njust.helper.links

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.njust.helper.shared.links.LinksModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class LinksActivity : AppCompatActivity() {
  private val model by viewModels<LinksModel>()

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)

    val snackbarMessageFlow = model.vm.snackbarMessageFlow
      .map { it.toString(this@LinksActivity) }
    setContent {
      LinksScreen(
        isRefreshing = model.vm.loading,
        items = model.vm.items,
        snackbarMessageFlow = snackbarMessageFlow,
        onRefresh = { load() },
        onClickLink = { link ->
          val intent = Intent(Intent.ACTION_VIEW)
          intent.data = link.url.toUri()
          startActivity(intent)
        },
        onClickHome = { finish() },
      )
    }
    load()
  }

  private fun load() {
    lifecycleScope.launch {
      model.load()
    }
  }
}
