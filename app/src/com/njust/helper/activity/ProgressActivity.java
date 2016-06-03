package com.njust.helper.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.view.ViewGroup;

import com.zwb.commonlibs.ui.ExtendedSwipeRefreshLayout;

import java.util.WeakHashMap;

public abstract class ProgressActivity extends BaseActivity {
    private ExtendedSwipeRefreshLayout mSwipeRefreshLayout;

    private WeakHashMap<String, AsyncTask<?, ?, ?>> taskMap = new WeakHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prepareViews();

        setupPullLayout(mSwipeRefreshLayout);

        firstRefresh();
    }

    protected abstract void prepareViews();

    protected void setupPullLayout(ExtendedSwipeRefreshLayout refreshLayout) {
        refreshLayout.setEnabled(false);
    }

    protected void firstRefresh() {

    }

    @Override
    public void setContentView(int layoutResID) {
        mSwipeRefreshLayout = new ExtendedSwipeRefreshLayout(this);
        //noinspection deprecation
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light));
        mSwipeRefreshLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        getLayoutInflater().inflate(layoutResID, mSwipeRefreshLayout);
        setContentView(mSwipeRefreshLayout);
    }

    public void setRefreshing(final boolean b) {
//        mSwipeRefreshLayout.setRefreshing(b);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(b);
            }
        });
    }

    @SafeVarargs
    public final <Params> void attachAsyncTask(AsyncTask<Params, ?, ?> task, Params... params) {
        taskMap.put(task.getClass().getName(), AsyncTaskCompat.executeParallel(task, params));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        for (AsyncTask<?, ?, ?> task : taskMap.values()) {
            if (task != null) {
                task.cancel(true);
            }
        }
    }
}
