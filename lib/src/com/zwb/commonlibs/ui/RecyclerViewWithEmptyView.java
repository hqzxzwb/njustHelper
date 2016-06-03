package com.zwb.commonlibs.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class RecyclerViewWithEmptyView extends ViewGroup {
    private View mEmptyView;
    private RecyclerView mRecyclerView;
    private LayoutParams mEmptyViewParams;

    private boolean isShowingEmptyView;

    public RecyclerViewWithEmptyView(Context context) {
        super(context);
    }

    public RecyclerViewWithEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewWithEmptyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mRecyclerView.getAdapter() == null || mRecyclerView.getAdapter().getItemCount() == 0) {
            mEmptyView.layout(0, 0, getWidth(), getHeight());
            isShowingEmptyView = true;
        } else {
            mRecyclerView.layout(0, 0, getWidth(), getHeight());
            isShowingEmptyView = false;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mEmptyView = getChildAt(0);
        mRecyclerView = (RecyclerView) getChildAt(1);
        if (mRecyclerView == null)
            mRecyclerView = new RecyclerView(getContext());
    }

    //为了下拉刷新行为正常
    @Override
    public boolean canScrollVertically(int direction) {
        return !isShowingEmptyView && mRecyclerView.canScrollVertically(direction);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return !isShowingEmptyView && mRecyclerView.canScrollHorizontally(direction);
    }

    static class LayoutParams extends FrameLayout.LayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }
    }
}
