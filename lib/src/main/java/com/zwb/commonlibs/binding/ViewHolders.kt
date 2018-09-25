package com.zwb.commonlibs.binding

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import android.view.View

open class DataBindingHolder<T : ViewDataBinding>(val binding: T) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

class BaseDataBindingHolder(binding: ViewDataBinding) : DataBindingHolder<ViewDataBinding>(binding)

class PlainViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView)
