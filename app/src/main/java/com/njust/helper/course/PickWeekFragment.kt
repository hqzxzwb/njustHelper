package com.njust.helper.course

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.njust.helper.R
import com.njust.helper.databinding.ItemPickWeekBinding
import com.njust.helper.tools.Constants
import com.njust.helper.tools.DataBindingHolder

/**
 * Created by zwb on 2017/12/31.
 * 选择周次
 */
class PickWeekFragment : BottomSheetDialogFragment() {
  private lateinit var listener: Listener
  private var selectedWeek: Int = 0

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.bottom_sheet_course_week, container, false)
    val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
    recyclerView.layoutManager = GridLayoutManager(context, 5)
    recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
    recyclerView.adapter = PickWeekAdapter(selectedWeek)
    return view
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)

    listener = context as Listener
    selectedWeek = requireArguments().getInt(ARG_SELECTED_WEEK)
  }

  interface Listener {
    fun setWeek(week: Int)
  }

  private inner class PickWeekAdapter(var selectedWeek: Int)
    : RecyclerView.Adapter<DataBindingHolder<ItemPickWeekBinding>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingHolder<ItemPickWeekBinding> {
      val binding = ItemPickWeekBinding.inflate(LayoutInflater.from(parent.context),
          parent, false)
      return DataBindingHolder(binding)
    }

    override fun onBindViewHolder(holder: DataBindingHolder<ItemPickWeekBinding>, position: Int) {
      holder.dataBinding.week = position
      holder.dataBinding.chosen = selectedWeek == position + 1
      holder.itemView.setOnClickListener {
        listener.setWeek(position + 1)
        dismiss()
      }
    }

    override fun getItemCount(): Int {
      return Constants.MAX_WEEK_COUNT
    }
  }

  companion object {
    private const val ARG_SELECTED_WEEK = "selectedWeek"

    @JvmStatic
    fun newInstance(selectedWeek: Int): PickWeekFragment {
      val bundle = Bundle()
      bundle.putInt(ARG_SELECTED_WEEK, selectedWeek)
      val ret = PickWeekFragment()
      ret.arguments = bundle
      return ret
    }
  }
}
