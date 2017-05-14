package com.njust.helper.tools;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

public class DataBindingHolder<V extends ViewDataBinding> extends RecyclerView.ViewHolder {
    private V dataBinding;

    public DataBindingHolder(V dataBinding) {
        super(dataBinding.getRoot());
        this.dataBinding = dataBinding;
    }

    public V getDataBinding() {
        return dataBinding;
    }
}
