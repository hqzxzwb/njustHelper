package com.njust.helper.library;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.njust.helper.R;
import com.njust.helper.activity.MyListActivity;
import com.njust.helper.databinding.ItemLibSearchBinding;
import com.njust.helper.model.LibSearch;
import com.njust.helper.tools.AppHttpHelper;
import com.njust.helper.tools.DataBindingHolder;
import com.zwb.commonlibs.http.HttpMap;

import java.util.List;

import butterknife.BindView;

public class LibSearchActivity extends MyListActivity<LibSearch, ItemLibSearchBinding> {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.searchView)
    SearchView searchView;

    private String search;
    private SearchRecentSuggestions suggestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryRefinementEnabled(true);
    }

    @Override
    protected void setupActionBar() {
        setSupportActionBar(toolbar);
        ViewCompat.setElevation(toolbar, 16f);
        super.setupActionBar();
    }

    @Override
    protected int layoutRes() {
        return R.layout.activity_lib_search;
    }

    public void clear_history(View view) {
        new AlertDialog.Builder(this)
                .setTitle("图书馆")
                .setMessage("您确定清除搜索历史吗？")
                .setPositiveButton("清除", (dialog, which) -> getSuggestions().clearHistory())
                .setNegativeButton(R.string.action_back, null).show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String query = intent.getStringExtra(SearchManager.QUERY);
        getSuggestions().saveRecentQuery(query, null);

        search = query;
        onRefresh();
    }

    private SearchRecentSuggestions getSuggestions() {
        if (suggestions == null) {
            suggestions = new SearchRecentSuggestions(LibSearchActivity.this,
                    SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
        }
        return suggestions;
    }


    @Override
    protected int getServerErrorText() {
        return R.string.message_server_error_lib;
    }

    @NonNull
    @Override
    protected ListRecycleAdapter<LibSearch, ItemLibSearchBinding> onCreateAdapter() {
        return new LibSearchAdapter(this);
    }

    @Override
    protected String buildCacheName() {
        return null;
    }

    @Override
    protected String getResponse() throws Exception {
        HttpMap data = new HttpMap();
        data.addParam("search", search);
        return new AppHttpHelper().getPostResult("libSearch.php", data);
    }

    @Override
    protected Class<LibSearch> getItemClass() {
        return LibSearch.class;
    }

    public static class LibSearchAdapter extends ListRecycleAdapter<LibSearch, ItemLibSearchBinding> {
        private LibSearchActivity activity;

        LibSearchAdapter(LibSearchActivity activity) {
            this.activity = activity;
        }

        public static void onClick(View view, String id) {
            Context context = view.getContext();
            context.startActivity(LibDetailActivity.buildIntent(context, id));
        }

        @Override
        protected int getLayoutRes() {
            return R.layout.item_lib_search;
        }

        @Override
        public void onBindViewHolder(DataBindingHolder<ItemLibSearchBinding> holder, int position) {
            holder.getDataBinding().setLibSearch(getItem(position));
            holder.getDataBinding().setPosition(position);
        }

        @Override
        public void setData(List<LibSearch> data) {
            super.setData(data);
            activity.recyclerView.scrollToPosition(0);
        }
    }

}
