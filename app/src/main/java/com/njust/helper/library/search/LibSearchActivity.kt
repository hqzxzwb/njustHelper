package com.njust.helper.library.search

import android.app.SearchManager
import android.content.Intent
import android.provider.SearchRecentSuggestions
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.crashlytics.android.Crashlytics
import com.njust.helper.BuildConfig
import com.njust.helper.R
import com.njust.helper.activity.ProgressActivity
import com.njust.helper.api.ParseErrorException
import com.njust.helper.api.ServerErrorException
import com.njust.helper.api.library.LibraryApi
import com.njust.helper.databinding.ActivityLibSearchBinding
import com.njust.helper.library.book.LibDetailActivity
import com.njust.helper.tools.SimpleListVm
import com.zwb.commonlibs.utils.getSearchManager
import kotlinx.coroutines.launch
import java.io.IOException

class LibSearchActivity : ProgressActivity() {
    private var suggestions: SearchRecentSuggestions? = null
    private lateinit var binding: ActivityLibSearchBinding
    private val vm = SimpleListVm<LibSearchItemVm>().apply {
        listener = { _, item, _ ->
            startActivity(LibDetailActivity.buildIntent(this@LibSearchActivity, item.id))
        }
    }

    override fun layout() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lib_search)
        binding.vm = vm
    }

    override fun prepareViews() {
        mSwipeRefreshLayout = binding.swipeRefreshLayout

        val searchManager = getSearchManager()
        binding.searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            isQueryRefinementEnabled = true
        }
        binding.buttonClearHistory.setOnClickListener { clearHistory() }
        binding.recyclerView.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(this, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))
    }

    override fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        ViewCompat.setElevation(binding.toolbar, 16f)
        super.setupActionBar()
    }

    override fun layoutRes(): Int {
        return 0
    }

    override fun addRefreshLayoutAutomatically(): Boolean {
        return false
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
        setRefreshing(true)

        lifecycleScope.launch {
            try {
                val result = LibraryApi.search(search)
                vm.items = result.mapIndexed { index, libSearchBean ->
                    LibSearchItemVm(libSearchBean, index)
                }
                setRefreshing(false)
            } catch (e: Exception) {
                onError(e)
                setRefreshing(false)
            }
        }
    }

    private fun onError(throwable: Throwable) {
        when (throwable) {
            is IOException -> showSnack(R.string.message_net_error)
            is ServerErrorException -> showSnack(R.string.message_server_error_lib)
            is ParseErrorException -> showSnack(R.string.message_parse_error)
            else -> if (BuildConfig.DEBUG) {
                throw throwable
            } else {
                Crashlytics.logException(throwable)
            }
        }
    }

    private fun getSuggestions(): SearchRecentSuggestions {
        if (suggestions == null) {
            suggestions = SearchRecentSuggestions(this@LibSearchActivity,
                    SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE)
        }
        return suggestions!!
    }
}
