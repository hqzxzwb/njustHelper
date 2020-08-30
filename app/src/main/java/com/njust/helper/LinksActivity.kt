package com.njust.helper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.njust.helper.activity.BaseActivity
import com.njust.helper.api.sharedMoshi
import com.njust.helper.databinding.ActivityLinksBinding
import com.njust.helper.model.Link
import com.njust.helper.tools.SimpleListVm
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.buffer
import okio.source
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
        lifecycleScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    resources.openRawResource(R.raw.links).use {
                        val type = Types.newParameterizedType(List::class.java, Link::class.java)
                        val adapter = sharedMoshi.adapter<List<Link>>(type)
                        adapter.fromJson(it.source().buffer())!!
                    }
                }
                onDataReceived(data)
            } catch (e: Exception) {
                onError(e)
            }
        }
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
                FirebaseCrashlytics.getInstance().recordException(throwable)
            }
        }
    }

    override fun layoutRes(): Int = 0

    override fun layout() {
        val binding = DataBindingUtil.setContentView<ActivityLinksBinding>(this, R.layout.activity_links)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.swipeRefreshLayout.setOnRefreshListener(this::refresh)
        binding.vm = vm
    }
}
