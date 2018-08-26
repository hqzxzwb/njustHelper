package com.zwb.commonlibs.binding

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.View

open class DataBindingHolder<T : ViewDataBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)

class BaseDataBindingHolder(binding: ViewDataBinding) : DataBindingHolder<ViewDataBinding>(binding)

class PlainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
