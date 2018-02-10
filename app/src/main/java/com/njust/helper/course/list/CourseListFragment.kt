package com.njust.helper.course.list

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.njust.helper.databinding.BottomSheetCourseListBinding
import com.njust.helper.model.Course
import java.util.*

class CourseListFragment : BottomSheetDialogFragment() {
    private val adapter = CourseListAdapter(ArrayList())
    private var title: String? = null
    private var subTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments!!
        val list = args.getParcelableArrayList<Course>("courses")

        adapter.setData(list!!)
        title = args.getString("title")
        subTitle = args.getString("subTitle")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = BottomSheetCourseListBinding.inflate(inflater, container, false)
        binding.title = title
        binding.subTitle = subTitle
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = adapter
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(list: List<Course>, title: String, subTitle: String): CourseListFragment {
            val arrayList = if (list is ArrayList<*>) {
                list as ArrayList<Course>
            } else {
                ArrayList(list)
            }
            val bundle = Bundle()
            bundle.putParcelableArrayList("courses", arrayList)
            bundle.putString("title", title)
            bundle.putString("subTitle", subTitle)
            val clf = CourseListFragment()
            clf.arguments = bundle
            return clf
        }
    }
}
