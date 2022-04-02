package com.zwb.commonlibs.binding

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

open class DataBindingHolder<T : ViewDataBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)

class BaseDataBindingHolder(binding: ViewDataBinding) : DataBindingHolder<ViewDataBinding>(binding)
