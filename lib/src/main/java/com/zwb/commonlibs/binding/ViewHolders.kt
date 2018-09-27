package com.zwb.commonlibs.binding

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

open class DataBindingHolder<T : ViewDataBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)

class BaseDataBindingHolder(binding: ViewDataBinding) : DataBindingHolder<ViewDataBinding>(binding)

class PlainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
