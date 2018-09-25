package com.njust.helper

import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.njust.helper.activity.BaseActivity
import com.njust.helper.api.common.CommonApi
import com.njust.helper.databinding.ActivityLinksBinding
import com.njust.helper.model.Link
import com.njust.helper.tools.SimpleListVm
import com.tencent.bugly.crashreport.CrashReport
import java.io.IOException


class LinksActivity : BaseActivity() {
    private val vm = SimpleListVm<Link>().apply {
        listener = { _, link, _ ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(link.url)
            startActivity(intent)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        refresh()
    }

    private fun refresh() {
        CommonApi.links()
                .subscribe({ onDataReceived(it) }, { onError(it) })
                .addToLifecycleManagement()
    }

    private fun onDataReceived(list: List<Link>) {
        vm.loading = false
        vm.items = list
    }

    private fun onError(throwable: Throwable) {
        vm.loading = false
        when (throwable) {
            is IOException -> showSnack(R.string.message_net_error)
            else -> {
                if (BuildConfig.DEBUG) {
                    throwable.printStackTrace()
                    throw throwable
                }
                CrashReport.postCatchedException(throwable)
            }
        }
    }

    override fun layoutRes(): Int = 0

    override fun layout() {
        val binding = DataBindingUtil.setContentView<ActivityLinksBinding>(this, R.layout.activity_links)
        binding.recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.recyclerView.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(this, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))
        binding.swipeRefreshLayout.setOnRefreshListener(this::refresh)
        binding.vm = vm
    }
}
