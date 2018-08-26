package com.njust.helper.coursequery

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.njust.helper.R
import com.njust.helper.activity.BaseActivity
import com.njust.helper.databinding.ActivityCourseQueryResultBinding
import com.njust.helper.databinding.ItemCourseQueryBinding
import com.njust.helper.model.CourseQuery
import com.njust.helper.tools.DataBindingHolder
import com.zwb.commonlibs.rx.ioSubscribeUiObserve
import io.reactivex.android.schedulers.AndroidSchedulers

class CourseQueryResultActivity : BaseActivity() {
    private var section: Int = 0
    private var day: Int = 0
    private lateinit var name: String
    private lateinit var teacher: String
    private lateinit var binding: ActivityCourseQueryResultBinding
    private lateinit var adapter: CourseQueryAdapter

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
        CourseQueryApi.INSTANCE.queryCourse(section, day, name, teacher)
                .doOnSubscribe { binding.loading = true }
                .ioSubscribeUiObserve()
                .subscribe({ onDataReceived(it.data) }, { onError() })
    }

    private fun onDataReceived(list: List<CourseQuery>) {
        binding.loading = false
        if (list.isEmpty()) {
            showSnack(R.string.message_no_result)
        }
        adapter.data = list
    }

    private fun onError() {
        binding.loading = false
        showSnack(R.string.message_net_error)
    }

    override fun layoutRes(): Int = 0

    override fun layout() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_course_query_result)
        adapter = CourseQueryAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.swipeRefreshLayout.setOnRefreshListener(this::refresh)
    }

    private inner class CourseQueryAdapter : RecyclerView.Adapter<DataBindingHolder<ItemCourseQueryBinding>>() {
        val inflater = LayoutInflater.from(this@CourseQueryResultActivity)!!
        var data: List<CourseQuery> = listOf()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingHolder<ItemCourseQueryBinding> {
            val binding = ItemCourseQueryBinding.inflate(inflater, parent, false)
            return DataBindingHolder(binding)
        }

        override fun onBindViewHolder(holder: DataBindingHolder<ItemCourseQueryBinding>, position: Int) {
            holder.dataBinding.course = data[position]
            holder.dataBinding.position = position
        }
    }
}
