package com.njust.helper.library.search

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.provider.SearchRecentSuggestions
import android.support.v4.view.ViewCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import com.njust.helper.BuildConfig
import com.njust.helper.R
import com.njust.helper.activity.ProgressActivity
import com.njust.helper.databinding.ActivityLibSearchBinding
import com.njust.helper.library.LibraryApi
import com.njust.helper.library.book.LibDetailActivity
import com.njust.helper.tools.ServerErrorException
import com.tencent.bugly.crashreport.CrashReport
import io.reactivex.rxkotlin.subscribeBy

class LibSearchActivity : ProgressActivity() {
    private var suggestions: SearchRecentSuggestions? = null
    private lateinit var binding: ActivityLibSearchBinding
    private val vm = LibSearchVm(
            onItemClick = { _, item, _ ->
                startActivity(LibDetailActivity.buildIntent(this@LibSearchActivity, item.id))
            }
    )

    override fun layout() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lib_search)
        binding.vm = vm
    }

    override fun prepareViews() {
        mSwipeRefreshLayout = binding.swipeRefreshLayout

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            isQueryRefinementEnabled = true
        }
        binding.buttonClearHistory.setOnClickListener { clearHistory() }
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
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

    private fun onRefresh(search: String) {
        setRefreshing(true)
        LibraryApi.search(search)
                .subscribeBy(
                        onSuccess = {
                            vm.data = it.mapIndexed { index, libSearchBean ->
                                LibSearchItemVm(libSearchBean, index)
                            }
                            setRefreshing(false)
                        },
                        onError = {
                            onError(it)
                            setRefreshing(false)
                        }
                )
                .addToLifecycleManagement()
    }

    private fun onError(throwable: Throwable) {
        if (throwable is ServerErrorException) {
            showSnack(R.string.message_server_error_lib)
        } else {
            if (BuildConfig.DEBUG) {
                throw throwable
            } else {
                CrashReport.postCatchedException(throwable)
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
