package com.njust.helper.course.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.njust.helper.model.Course

class CourseListFragment : BottomSheetDialogFragment() {
  private lateinit var vm: CourseListViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val args = requireArguments()
    vm = CourseListViewModel(
      title = args.getString("title").orEmpty(),
      subtitle = args.getString("subtitle").orEmpty(),
      courses = args.getParcelableArrayList<Course>("courses")!!,
    )
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val view = ComposeView(requireContext())
    view.setContent {
      CourseListScreen(vm = this.vm)
    }
    return view
  }

  companion object {
    @JvmStatic
    fun newInstance(list: List<Course>, title: String, subtitle: String): CourseListFragment {
      val arrayList = if (list is ArrayList<*>) {
        list as ArrayList<Course>
      } else {
        ArrayList(list)
      }
      val bundle = Bundle()
      bundle.putParcelableArrayList("courses", arrayList)
      bundle.putString("title", title)
      bundle.putString("subtitle", subtitle)
      val clf = CourseListFragment()
      clf.arguments = bundle
      return clf
    }
  }
}
