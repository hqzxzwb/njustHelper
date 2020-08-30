package com.zwb.commonlibs.binding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.zwb.commonlibs.BR

class MultiBindingAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  private var inflater: LayoutInflater? = null
  var list: List<ItemSpec> = listOf()
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    var inflater = this.inflater
    if (inflater == null) {
      inflater = LayoutInflater.from(parent.context)!!
      this.inflater = inflater
    }
    return getViewHolder(inflater, parent, viewType)
  }

  override fun getItemViewType(position: Int): Int {
    return list[position].let { it.layoutResId * Integer.signum(it.brId) }
  }

  override fun getItemCount(): Int {
    return list.size
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = list[position]
    if (item.brId < 0) {
      return
    }
    DataBindingUtil.findBinding<ViewDataBinding>(holder.itemView)
        ?.setVariable(item.brId, item)
  }

  private fun getViewHolder(inflater: LayoutInflater, parent: ViewGroup, layoutResId: Int): RecyclerView.ViewHolder {
    return if (layoutResId < 0) {
      inflater.inflate(-layoutResId, parent, false)
          .let(::newViewHolder)
    } else {
      DataBindingUtil.inflate<ViewDataBinding>(inflater, layoutResId, parent, false)
          .root
          .let(::newViewHolder)
    }
  }

  private fun newViewHolder(view: View) = object : RecyclerView.ViewHolder(view) {}
}

open class ItemSpec(val brId: Int, val layoutResId: Int) : BaseObservable()

class SimpleItemSpec<T>(brId: Int, layoutResId: Int, data: T) : ItemSpec(brId, layoutResId) {
  var data by ObservableDelegate(BR.data, data)
    @Bindable get
}
