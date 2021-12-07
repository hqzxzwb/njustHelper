package com.njust.helper.activity

import android.os.AsyncTask
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.njust.helper.R
import java.util.*

abstract class ProgressActivity : BaseActivity {
  constructor() : super()
  constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

  protected lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

  private val taskMap = WeakHashMap<String, AsyncTask<*, *, *>>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    prepareViews()

    setupPullLayout(mSwipeRefreshLayout)

    firstRefresh()
  }

  protected abstract fun prepareViews()

  protected open fun setupPullLayout(refreshLayout: SwipeRefreshLayout) {
    refreshLayout.isEnabled = false
  }

  protected open fun firstRefresh() {

  }

  protected open fun addRefreshLayoutAutomatically(): Boolean {
    return true
  }

  override fun setContentView(layoutResID: Int) {
    if (addRefreshLayoutAutomatically()) {
      mSwipeRefreshLayout = SwipeRefreshLayout(this)

      mSwipeRefreshLayout.setColorSchemeColors(
          ContextCompat.getColor(this, android.R.color.holo_blue_bright),
          ContextCompat.getColor(this, android.R.color.holo_green_light),
          ContextCompat.getColor(this, android.R.color.holo_orange_light)
      )
      mSwipeRefreshLayout.layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT)
      layoutInflater.inflate(layoutResID, mSwipeRefreshLayout)
      setContentView(mSwipeRefreshLayout)
    } else {
      super.setContentView(layoutResID)
      mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
    }
  }

  fun setRefreshing(b: Boolean) {
    //        mSwipeRefreshLayout.setRefreshing(b);
    mSwipeRefreshLayout.post { mSwipeRefreshLayout.isRefreshing = b }
  }

  @SafeVarargs
  fun <Params> attachAsyncTask(task: AsyncTask<Params, *, *>, vararg params: Params) {
    taskMap[task.javaClass.name] = task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, *params)
  }

  override fun onDestroy() {
    super.onDestroy()

    for (task in taskMap.values) {
      task?.cancel(true)
    }
  }
}
