package com.njust.helper.tools;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

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
