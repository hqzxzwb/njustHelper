package com.njust.helper.library.search

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.support.v4.view.ViewCompat
import android.support.v7.app.AlertDialog
import android.view.View
import com.njust.helper.R
import com.njust.helper.activity.MyListActivity
import com.njust.helper.databinding.ItemLibSearchBinding
import com.njust.helper.library.book.LibDetailActivity
import com.njust.helper.tools.AppHttpHelper
import com.njust.helper.tools.DataBindingHolder
import com.zwb.commonlibs.http.HttpMap
import kotlinx.android.synthetic.main.activity_lib_search.*

class LibSearchActivity : MyListActivity<LibSearchBean, ItemLibSearchBinding>() {
    private var search: String? = null
    private var suggestions: SearchRecentSuggestions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            isQueryRefinementEnabled = true
        }
    }

    override fun setupActionBar() {
        setSupportActionBar(toolbar)
        ViewCompat.setElevation(toolbar, 16f)
        super.setupActionBar()
    }

    override fun layoutRes(): Int {
        return R.layout.activity_lib_search
    }

    fun clear_history(view: View) {
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

        search = query
        onRefresh()
    }

    private fun getSuggestions(): SearchRecentSuggestions {
        if (suggestions == null) {
            suggestions = SearchRecentSuggestions(this@LibSearchActivity,
                    SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE)
        }
        return suggestions!!
    }

    override fun getServerErrorText(): Int {
        return R.string.message_server_error_lib
    }

    override fun onCreateAdapter(): MyListActivity.ListRecycleAdapter<LibSearchBean, ItemLibSearchBinding> {
        return LibSearchAdapter(this)
    }

    override fun buildCacheName(): String? {
        return null
    }

    @Throws(Exception::class)
    override fun getResponse(): String {
        val data = HttpMap()
        data.addParam("search", search)
        return AppHttpHelper().getPostResult("libSearch.php", data)
    }

    override fun getItemClass(): Class<LibSearchBean> {
        return LibSearchBean::class.java
    }

    class LibSearchAdapter(private val activity: LibSearchActivity)
        : MyListActivity.ListRecycleAdapter<LibSearchBean, ItemLibSearchBinding>() {
        override fun getLayoutRes(): Int {
            return R.layout.item_lib_search
        }

        override fun onBindViewHolder(holder: DataBindingHolder<ItemLibSearchBinding>, position: Int) {
            holder.dataBinding.libSearch = getItem(position)
            holder.dataBinding.position = position
        }

        override fun setData(data: List<LibSearchBean>) {
            super.setData(data)
            activity.recyclerView.scrollToPosition(0)
        }

        companion object {
            @JvmStatic
            fun onClick(view: View, id: String) {
                val context = view.context
                context.startActivity(LibDetailActivity.buildIntent(context, id))
            }
        }
    }
}
