package com.zwb.commonlibs.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * 带有View回收机制的PagerAdapter
 *
 * @author zwb
 */
public abstract class EfficientPagerAdapter extends PagerAdapter {
    private LinkedList<View> convertViews = new LinkedList<>();

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        convertViews.push(view);
        container.removeView(view);
    }

    @Override
    public final Object instantiateItem(ViewGroup container, int position) {
        ViewPager viewPager = (ViewPager) container;
        View view;
        try {
            view = convertViews.pop();
        } catch (NoSuchElementException e) {
            view = onCreateNewView(container);
        }
        updateView(view, position);
        viewPager.addView(view);
        return view;
    }

    /**
     * 不能使用回收View时，新生成一个View的方法
     *
     * @param container 生成View的容器
     * @return 新生成的View
     */
    protected abstract View onCreateNewView(ViewGroup container);

    /**
     * 对View上面的元素进行更新，可以用ViewHolder模式实现
     *
     * @param view     需要更新的View
     * @param position ViewPager中的位置
     */
    protected abstract void updateView(View view, int position);

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
