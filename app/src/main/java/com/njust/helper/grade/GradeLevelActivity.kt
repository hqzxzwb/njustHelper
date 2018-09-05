package com.njust.helper.grade

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.njust.helper.R
import com.njust.helper.activity.BaseActivity
import com.njust.helper.api.JwcApi
import com.njust.helper.databinding.ActivityGradeLevelBinding
import com.njust.helper.tools.Prefs

class GradeLevelActivity : BaseActivity() {
    private val vm = GradeLevelVm()

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        refresh()
    }

    private fun refresh() {
        JwcApi.gradeLevel(Prefs.getId(this), Prefs.getJwcPwd(this))
                .subscribe({ onDataReceived(it) }, { onError() })
                .addToLifecycleManagement()
    }

    private fun onDataReceived(list: List<GradeLevelBean>) {
        vm.loading = false
        if (list.isEmpty()) {
            showSnack(R.string.message_no_result)
        } else {
            vm.items = list
        }
    }

    private fun onError() {
        vm.loading = false
        showSnack(R.string.message_net_error)
    }

    override fun layoutRes(): Int = 0

    override fun layout() {
        val binding = DataBindingUtil.setContentView<ActivityGradeLevelBinding>(this, R.layout.activity_grade_level)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.swipeRefreshLayout.setOnRefreshListener(this::refresh)
        binding.vm = vm
    }
}
