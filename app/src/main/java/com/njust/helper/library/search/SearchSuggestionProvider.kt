package com.njust.helper.library.search

import android.content.SearchRecentSuggestionsProvider

class SearchSuggestionProvider : SearchRecentSuggestionsProvider() {
  init {
    setupSuggestions(AUTHORITY, MODE)
  }

  companion object {
    // For consistency, use old class name for Authority.
    const val AUTHORITY = "com.njust.helper.library.SearchSuggestionProvider"
    const val MODE = SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES
  }
}
