package com.zwb.commonlibs.binding

import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

object ViewGroupBinding {
    @BindingAdapter("layoutId", "brId", "items", "onItemClick", requireAll = false)
    @JvmStatic
    fun <T> bindViewGroupWithViewModel(viewGroup: ViewGroup, layoutId: Int, brId: Int, items: List<T>?,
                                       onBindingItemClickListener: OnBindingItemClickListener<T>? = null) {
        recycleViewGroup(viewGroup, items, object : ViewGroupHandler<T> {
            override fun onViewCreate(viewGroup: ViewGroup): View {
                val inflater = LayoutInflater.from(viewGroup.context)
                val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, layoutId, viewGroup, false)
                return binding.root
            }

            override fun bindData(view: View, item: T, position: Int) {
                val binding = DataBindingUtil.bind<ViewDataBinding>(view)!!
                binding.setVariable(brId, item)
                binding.executePendingBindings()
                if (onBindingItemClickListener != null) {
                    binding.root.also {
                        it.setOnClickListener { onBindingItemClickListener(it, item, position) }
                    }
                }
            }
        })
    }

    /**
     * Method to recycle views in ViewGroup
     *
     * @param viewGroup the ViewGroup to be handle
     * @param items     the source
     * @param handler   the callback to define how to deal the views
     */
    @JvmStatic
    fun <T> recycleViewGroup(viewGroup: ViewGroup, items: List<T>?, handler: ViewGroupHandler<T>?) {
        if (items == null || handler == null) {
            return
        }
        var isLinearLayoutEnd = false
        if (viewGroup.childCount == 0) {
            isLinearLayoutEnd = true
        }
        for (i in items.indices) {
            when {
                isLinearLayoutEnd -> {
                    val view = handler.onViewCreate(viewGroup)
                    handler.bindData(view, items[i], i)
                    viewGroup.addView(view)
                }
                viewGroup.getChildAt(i) != null -> handler.bindData(viewGroup.getChildAt(i), items[i], i)
                else -> {
                    isLinearLayoutEnd = true
                    val view = handler.onViewCreate(viewGroup)
                    handler.bindData(view, items[i], i)
                    viewGroup.addView(view)
                }
            }
        }
        while (items.size < viewGroup.childCount) {
            viewGroup.removeViewAt(viewGroup.childCount - 1)
        }
    }
}

interface ViewGroupHandler<T> {
    fun onViewCreate(viewGroup: ViewGroup): View

    fun bindData(view: View, item: T, position: Int)
}

typealias OnBindingItemClickListener<T> = (View, T, Int) -> Unit
