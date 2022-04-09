package com.njust.helper.library.search

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.njust.helper.BuildConfig
import com.njust.helper.R
import com.njust.helper.library.book.LibDetailActivity
import com.njust.helper.shared.api.LibraryApi
import com.njust.helper.shared.api.ParseErrorException
import com.njust.helper.shared.api.ServerErrorException
import com.zwb.commonlibs.utils.requireSystemService
import kotlinx.coroutines.launch
import java.io.IOException

class LibSearchActivity : AppCompatActivity() {
  private var suggestions: SearchRecentSuggestions? = null
  private val vm = LibSearchViewModel(
    onClickHome = this::finish,
    onClickResultItem = {
      startActivity(LibDetailActivity.buildIntent(this@LibSearchActivity, it.id))
    },
    onClickClearHistory = { clearHistory() },
  ) { searchView ->
    val searchManager = requireSystemService<SearchManager>()
    searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      LibSearchScreen(vm = this.vm)
    }
  }

  private fun clearHistory() {
    AlertDialog.Builder(this)
      .setTitle("图书馆")
      .setMessage("您确定清除搜索历史吗？")
      .setPositiveButton("清除") { _, _ -> getSuggestions().clearHistory() }
      .setNegativeButton(R.string.action_back, null).show()
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)

    val query = intent.getStringExtra(SearchManager.QUERY)
    getSuggestions().saveRecentQuery(query, null)

    onRefresh(query)
  }

  private fun onRefresh(search: String?) {
    if (search == null) {
      return
    }
    vm.isRefreshing = true

    lifecycleScope.launch {
      try {
        val result = LibraryApi.search(search)
        vm.result = result
      } catch (e: Exception) {
        onError(e)
      }
      vm.isRefreshing = false
    }
  }

  private suspend fun onError(throwable: Throwable) {
    when (throwable) {
      is IOException -> showSnack(R.string.message_net_error)
      is ServerErrorException -> showSnack(R.string.message_server_error_lib)
      is ParseErrorException -> showSnack(R.string.message_parse_error)
      else -> if (BuildConfig.DEBUG) {
        throw throwable
      }
    }
  }

  private suspend fun showSnack(resId: Int) {
    vm.snackbarMessageFlow.emit(getString(resId))
  }

  private fun getSuggestions(): SearchRecentSuggestions {
    if (suggestions == null) {
      suggestions = SearchRecentSuggestions(this@LibSearchActivity,
        SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE)
    }
    return suggestions!!
  }
}
