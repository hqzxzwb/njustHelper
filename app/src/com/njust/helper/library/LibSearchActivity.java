package com.njust.helper.library;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.njust.helper.R;
import com.njust.helper.activity.MyListActivity;
import com.njust.helper.activity.ProgressActivity;
import com.njust.helper.databinding.ItemLibSearchBinding;
import com.njust.helper.localapi.ApiConfiguration;
import com.njust.helper.model.LibSearch;
import com.njust.helper.tools.DataBindingHolder;
import com.zwb.commonlibs.injection.ViewInjection;

import java.util.Collections;
import java.util.List;

public class LibSearchActivity extends ProgressActivity {
    @ViewInjection(R.id.toolbar)
    private Toolbar toolbar;
    @ViewInjection(R.id.searchView)
    private SearchView searchView;
    @ViewInjection(R.id.recyclerView)
    private RecyclerView recyclerView;

    private String search;
    private SearchRecentSuggestions suggestions;
    private LibSearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryRefinementEnabled(true);
    }

    @Override
    protected void prepareViews() {
        adapter = new LibSearchAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
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
                .setPositiveButton("清除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getSuggestions().clearHistory();
                    }
                })
                .setNegativeButton(R.string.action_back, null).show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String query = intent.getStringExtra(SearchManager.QUERY);
        getSuggestions().saveRecentQuery(query, null);

        search = query;
        attachAsyncTask(new AsyncTask<String, String, List<LibSearch>>() {
            @Override
            protected List<LibSearch> doInBackground(String... params) {
                return ApiConfiguration.getConfiguration("libSearch")
                        .execute(Collections.singletonMap("word", params[0]), LibSearch.class,
                                new ApiConfiguration.ProgressCallback() {
                                    @Override
                                    public void onProgress(String progress) {
                                        publishProgress(progress);
                                    }
                                });
            }

            @Override
            protected void onPostExecute(List<LibSearch> libSearches) {
                if (libSearches != null) {
                    adapter.setData(libSearches);
                }
            }

            @Override
            protected void onProgressUpdate(String... values) {
                showSnack(values[0]);
            }
        }, search);
    }

    private SearchRecentSuggestions getSuggestions() {
        if (suggestions == null) {
            suggestions = new SearchRecentSuggestions(LibSearchActivity.this,
                    SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
        }
        return suggestions;
    }

    public static class LibSearchAdapter extends MyListActivity.ListRecycleAdapter<LibSearch, ItemLibSearchBinding> {
        private LibSearchActivity activity;

        private LibSearchAdapter(LibSearchActivity activity) {
            this.activity = activity;
        }

        public static void onClick(View view, String id) {
            LibDetailActivity.showLibDetail(view.getContext(), id);
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
