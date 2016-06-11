package com.zwb.commonlibs.ui;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (swipeView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) swipeView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(swipeView, -1) || swipeView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(swipeView, -1);
        }
    }
}
