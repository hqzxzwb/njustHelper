package com.njust.helper.coursequery

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.njust.helper.R
import com.njust.helper.activity.BaseActivity
import com.njust.helper.databinding.ActivityCourseQueryResultBinding
import com.njust.helper.tools.SimpleListVm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CourseQueryResultActivity : BaseActivity() {
    private var section: Int = 0
    private var day: Int = 0
    private val vm = SimpleListVm<CourseQueryItemVm>()
    private lateinit var name: String
    private lateinit var teacher: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val timeOfDay = intent.getIntExtra("section", 0)
        section = if (timeOfDay < 0) -1 else 1 shl timeOfDay
        val dayOfWeek = intent.getIntExtra("day", 0)
        day = if (dayOfWeek < 0) -1 else 1 shl dayOfWeek
        name = intent.getStringExtra("name")
        teacher = intent.getStringExtra("teacher")
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        refresh()
    }

    private fun refresh() {
        lifecycleScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    CourseQueryDao.getInstance(this@CourseQueryResultActivity)
                            .queryCourses(name, teacher, section, day)
                }
                onDataReceived(data)
            } catch (e: Exception) {
                onError()
            }
        }
    }

    private fun onDataReceived(list: List<CourseQueryItem>) {
        vm.loading = false
        if (list.isEmpty()) {
            showSnack(R.string.message_no_result)
        } else {
            vm.items = list.mapIndexed { index, item -> CourseQueryItemVm(item, index) }
        }
    }

    private fun onError() {
        vm.loading = false
        showSnack(R.string.message_net_error)
    }

    override fun layoutRes(): Int = 0

    override fun layout() {
        val binding = DataBindingUtil.setContentView<ActivityCourseQueryResultBinding>(this, R.layout.activity_course_query_result)
        binding.recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.recyclerView.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(this, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))
        binding.swipeRefreshLayout.setOnRefreshListener(this::refresh)
        binding.vm = vm
    }
}

class CourseQueryItemVm(val item: CourseQueryItem, val position: Int)
