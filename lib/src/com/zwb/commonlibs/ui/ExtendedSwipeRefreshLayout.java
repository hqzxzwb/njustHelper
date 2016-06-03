package com.zwb.commonlibs.ui;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;

/**
 * Created by zwb on 2014/11/17.<br>
 * 重写SwipeRefreshLayout中的canChildScrollUp，并提供更灵活的滚动控件选择
 */
public class ExtendedSwipeRefreshLayout extends SwipeRefreshLayout {
    private View swipeView;

    public ExtendedSwipeRefreshLayout(Context context) {
        super(context);
    }

    public void setSwipeView(View view) {
        swipeView = view;
    }

    public void setSwipeView(int resId) {
        swipeView = findViewById(resId);
    }

    @Override
    public boolean canChildScrollUp() {
        if (swipeView == null) {
            swipeView = getChildAt(0);
        }
        return swipeView.canScrollVertically(-1);
    }
}
