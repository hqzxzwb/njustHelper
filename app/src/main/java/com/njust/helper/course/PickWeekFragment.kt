package com.njust.helper.course

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Created by zwb on 2017/12/31.
 * 选择周次
 */
class PickWeekFragment : BottomSheetDialogFragment() {
  private lateinit var listener: Listener
  private var selectedWeek: Int = 0

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val view = ComposeView(requireContext())
    view.setContent {
      PickWeekScreen(
        selectedWeekIndex = selectedWeek,
        onSelectWeek = {
          dismiss()
          listener.setWeek(it)
        },
      )
    }
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
