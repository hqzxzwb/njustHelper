package com.njust.helper.library.book

import android.content.Context
import android.content.Intent
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.njust.helper.BuildConfig
import com.njust.helper.R
import com.njust.helper.activity.ProgressActivity
import com.njust.helper.library.LibraryApi
import com.njust.helper.library.collection.LibCollectManager
import com.njust.helper.tools.Constants
import com.njust.helper.tools.ServerErrorException
import com.tencent.bugly.crashreport.CrashReport
import com.zwb.commonlibs.utils.MemCacheManager
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_lib_collection.*
import java.io.IOException

class LibDetailActivity : ProgressActivity(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var idString: String

    private var manager: LibCollectManager? = null
    private var title: String? = null
    private var isCollected = false
    private var adapter: LibDetailAdapter? = null

    private var code: String? = null

    private val resultIntent = Intent()

    internal val cacheName: String
        get() = "libDetail_$idString"

    override fun layoutRes(): Int {
        return R.layout.activity_lib_detail
    }

    override fun prepareViews() {
        idString = intent.getStringExtra(Constants.EXTRA_ID)
        manager = LibCollectManager.getInstance(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        adapter = LibDetailAdapter(this)
        recyclerView.adapter = adapter

        resultIntent.putExtra(Constants.EXTRA_ID, idString)
        setResult(RESULT_OK, resultIntent)
    }

    override fun firstRefresh() {
        val data = MemCacheManager.get<LibDetailData>(cacheName)
        if (data == null) {
            onRefresh()
        } else {
            notifyData(data)
        }
    }

    override fun setupPullLayout(layout: SwipeRefreshLayout) {
        layout.setOnRefreshListener(this)
    }

    internal fun notifyData(data: LibDetailData) {
        val strings = data.head!!.split("\n")
        if (strings.size > 1) {
            title = strings[1]
        }
        val list = data.states
        code = if (list!!.isEmpty()) "" else list[0].code
        adapter!!.setData(data)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.lib_detail, menu)
        val item = menu.findItem(R.id.item_collect)
        if (manager!!.checkCollect(idString)) {
            item.setIcon(R.drawable.ic_action_important)
            isCollected = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_collect -> {
                if (title == null) {
                    showSnack("收藏失败，请刷新后重试")
                } else if (isCollected) {
                    manager!!.removeCollect(idString)
                    showSnack("已取消收藏")
                    isCollected = false
                    item.setIcon(R.drawable.ic_action_not_important)
                } else if (manager!!.addCollect(idString, title!!, code!!)) {
                    showSnack("收藏成功")
                    isCollected = true
                    item.setIcon(R.drawable.ic_action_important)
                } else {
                    showSnack("收藏失败,这本书已经收藏 ")
                    isCollected = true
                    item.setIcon(R.drawable.ic_action_important)
                }
                resultIntent.putExtra("isCollected", isCollected)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onRefresh() {
        LibraryApi.detail(idString)
                .subscribeBy(
                        onSuccess = {
                            notifyData(it)
                            setRefreshing(false)
                        },
                        onError = {
                            onError(it)
                            setRefreshing(false)
                        }
                )
                .addToLifecycleManagement()
    }

    private fun onError(throwable: Throwable) {
        if (throwable is IOException) {
            showSnack(R.string.message_net_error)
        } else if (throwable is ServerErrorException) {
            showSnack(R.string.message_server_error_lib)
        } else {
            if (BuildConfig.DEBUG) {
                throw throwable
            } else {
                CrashReport.postCatchedException(throwable)
            }
        }
    }

    companion object {
        fun buildIntent(context: Context, idString: String): Intent {
            val intent = Intent(context, LibDetailActivity::class.java)
            intent.putExtra(Constants.EXTRA_ID, idString)
            return intent
        }
    }
}
