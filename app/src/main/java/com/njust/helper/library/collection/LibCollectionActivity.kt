package com.njust.helper.library.collection

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.njust.helper.R
import com.njust.helper.activity.BaseActivity
import com.njust.helper.databinding.ItemLibCollectBinding
import com.njust.helper.library.book.LibDetailActivity
import com.njust.helper.tools.Constants
import com.njust.helper.tools.DataBindingHolder
import com.njust.helper.tools.Prefs
import java.text.SimpleDateFormat
import java.util.*

class LibCollectionActivity : BaseActivity() {
  private lateinit var manager: LibCollectManager
  private lateinit var adapter: LibCollectionAdapter
  private lateinit var mList: MutableList<LibCollectItem>
  private val itemsToRemove = HashSet<String>()
  private lateinit var recyclerView: RecyclerView
  private lateinit var emptyView: View
  private lateinit var coordinatorLayout: View

  override fun layoutRes(): Int {
    return R.layout.activity_lib_collection
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    recyclerView = findViewById(R.id.recyclerView)
    emptyView = findViewById(R.id.emptyView)
    coordinatorLayout = findViewById(R.id.coordinatorLayout)

    manager = LibCollectManager.getInstance(this)
    mList = manager.listCollects()
    adapter = LibCollectionAdapter()
    recyclerView.apply {
      setHasFixedSize(true)
      layoutManager = LinearLayoutManager(this@LibCollectionActivity)
      addItemDecoration(DividerItemDecoration(this@LibCollectionActivity, DividerItemDecoration.VERTICAL))
      this.adapter = this@LibCollectionActivity.adapter
    }

    val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
      override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(0, ItemTouchHelper.START or ItemTouchHelper.END)
      }

      override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
      }

      override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        deleteItem(viewHolder.bindingAdapterPosition)
      }
    })
    itemTouchHelper.attachToRecyclerView(recyclerView)
    onEmptyStateChange(mList.isEmpty())

    if (!Prefs.getLibCollectionHint(this)) {
      showSnack("图书详情页可以收藏\n左右滑动条目以删除", "不再提示") {
        Prefs.putLibCollectionHint(this@LibCollectionActivity, true)
      }
    }
  }

  internal fun deleteItem(position: Int) {
    itemsToRemove.add(adapter.delete(position)!!.id)
    showSnack("您删除了一本图书", "撤销") {
      val libCollectItem = adapter.restore()
      if (libCollectItem != null) {
        itemsToRemove.remove(libCollectItem.id)
      }
      showSnack("已撤销更改")
    }
  }

  fun onEmptyStateChange(empty: Boolean) {
    emptyView.visibility = if (empty) View.VISIBLE else View.GONE
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == android.R.id.home) {
      onBackPressed()
      return true
    }
    return super.onOptionsItemSelected(item)
  }

  override fun getViewForSnackBar(): View {
    return coordinatorLayout
  }

  internal fun showLibDetail(id: String) {
    startActivityForResult(LibDetailActivity.buildIntent(this, id), REQUEST_CODE_LIB_DETAIL)
  }

  override fun onBackPressed() {
    if (itemsToRemove.size == 0) {
      finish()
    } else {
      val listener = DialogInterface.OnClickListener { _, which ->
        if (which == DialogInterface.BUTTON_POSITIVE) {
          manager.removeCollects(itemsToRemove)
        }
        finish()
      }
      AlertDialog.Builder(this)
          .setTitle("注意")
          .setMessage("您对收藏的图书作出了更改，是否确认保存？")
          .setPositiveButton("保存更改", listener)
          .setNegativeButton("放弃更改", listener)
          .show()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == REQUEST_CODE_LIB_DETAIL) {
      if (resultCode == RESULT_OK && data != null) {
        if (!data.getBooleanExtra("isCollected", true)) {
          val id = data.getStringExtra(Constants.EXTRA_ID)
          for (i in mList.indices) {
            if (mList[i].id == id) {
              adapter.delete(i)
              break
            }
          }
        }
      }
    }
  }

  inner class LibCollectionAdapter : RecyclerView.Adapter<DataBindingHolder<ItemLibCollectBinding>>() {
    private var restoreItem: LibCollectItem? = null
    private var restorePosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingHolder<ItemLibCollectBinding> {
      return DataBindingHolder(ItemLibCollectBinding.inflate(
          LayoutInflater.from(this@LibCollectionActivity), parent, false))
    }

    override fun onBindViewHolder(holder: DataBindingHolder<ItemLibCollectBinding>, position: Int) {
      val libCollectItem = mList[position]
      holder.dataBinding.item = libCollectItem
      val id = mList[position].id
      holder.itemView.setOnClickListener { showLibDetail(id) }
      holder.itemView.setOnLongClickListener { view ->
        AlertDialog.Builder(view.context)
            .setTitle("确定删除这条收藏吗?")
            .setMessage(libCollectItem.name)
            .setPositiveButton("删除") { _, _ ->
              deleteItem(holder.bindingAdapterPosition)
            }
            .setNegativeButton("取消", null)
            .show()
        true
      }
    }

    internal fun delete(position: Int): LibCollectItem? {
      restoreItem = mList.removeAt(position)
      restorePosition = position
      notifyItemRemoved(position)
      onEmptyStateChange(mList.isEmpty())
      return restoreItem
    }

    internal fun restore(): LibCollectItem? {
      val libCollectItem = restoreItem
      if (libCollectItem != null) {
        mList.add(restorePosition, libCollectItem)
        onEmptyStateChange(mList.isEmpty())
        notifyItemInserted(restorePosition)
        restoreItem = null
        return libCollectItem
      }
      return null
    }

    override fun getItemCount(): Int {
      return mList.size
    }
  }

  companion object {
    private const val REQUEST_CODE_LIB_DETAIL = 0

    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    @JvmStatic
    fun getDateString(time: Long): String {
      return "收藏时间：" + DATE_FORMAT.format(Date(time))
    }
  }
}
