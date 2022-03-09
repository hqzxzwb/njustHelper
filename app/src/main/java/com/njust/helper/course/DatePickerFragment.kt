package com.njust.helper.course

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
  private var listener: Listener? = null
  private var time = 0L

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    time = requireArguments().getLong(DATE_PICKER_ARG_TIME)
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    listener = context as Listener
  }

  override fun onDetach() {
    super.onDetach()
    listener = null
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time
    return DatePickerDialog(
        requireContext(),
        this,
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
  }

  override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
    listener?.onDateSet(year, month, dayOfMonth)
  }

  interface Listener {
    fun onDateSet(year: Int, month: Int, dayOfMonth: Int)
  }

  companion object {
    private const val DATE_PICKER_ARG_TIME = "DATE_PICKER_ARG_TIME"

    fun newInstance(time: Long = System.currentTimeMillis()): DatePickerFragment {
      val bundle = Bundle().apply {
        putLong(DATE_PICKER_ARG_TIME, time)
      }
      val fragment = DatePickerFragment()
      fragment.arguments = bundle
      return fragment
    }
  }
}
