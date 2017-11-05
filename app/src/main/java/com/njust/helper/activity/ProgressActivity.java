package com.njust.helper.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ViewGroup;

import com.njust.helper.R;

import java.util.WeakHashMap;

public abstract class ProgressActivity extends BaseActivity {
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private WeakHashMap<String, AsyncTask<?, ?, ?>> taskMap = new WeakHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prepareViews();

        setupPullLayout(mSwipeRefreshLayout);

        firstRefresh();
    }

    protected abstract void prepareViews();

    protected void setupPullLayout(SwipeRefreshLayout refreshLayout) {
        refreshLayout.setEnabled(false);
    }

    protected void firstRefresh() {

    }

    protected boolean addRefreshLayoutAutomatically() {
        return true;
    }

    @Override
    public void setContentView(int layoutResID) {
        if (addRefreshLayoutAutomatically()) {
            mSwipeRefreshLayout = new SwipeRefreshLayout(this);
            //noinspection deprecation
            mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright),
                    getResources().getColor(android.R.color.holo_green_light),
                    getResources().getColor(android.R.color.holo_orange_light));
            mSwipeRefreshLayout.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            getLayoutInflater().inflate(layoutResID, mSwipeRefreshLayout);
            setContentView(mSwipeRefreshLayout);
        } else {
            super.setContentView(layoutResID);
            mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        }
    }

    public void setRefreshing(final boolean b) {
//        mSwipeRefreshLayout.setRefreshing(b);
        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(b));
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
