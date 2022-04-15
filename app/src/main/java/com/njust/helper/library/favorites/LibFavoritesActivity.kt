package com.njust.helper.library.favorites

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.njust.helper.library.book.LibDetailActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LibFavoritesActivity : AppCompatActivity() {
  private val manager = LibFavoritesManager
  private val vm = LibFavoritesViewModel(
    onClickHome = this::finish,
    onClickItem = {
      startActivity(LibDetailActivity.buildIntent(this, it.id))
    },
    onClickDelete = this::onClickDelete,
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      LibFavoritesScreen(vm = this.vm)
    }

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        manager.all()
          .collectLatest {
            vm.itemsLoaded = true
            vm.items = it.map(::LibFavoritesItemViewModel)
          }
      }
    }
  }

  private fun onClickDelete() {
    val idsToDelete = vm.items.mapNotNull {
      if (it.checked) it.item.id else null
    }
    if (idsToDelete.isEmpty()) {
      lifecycleScope.launch {
        vm.snackbarMessageFlow.emit("没有选中的图书")
      }
      return
    }
    AlertDialog.Builder(this)
      .setTitle("删除")
      .setMessage("确认删除 ${idsToDelete.size} 本图书？")
      .setPositiveButton("删除") { _, _ ->
        lifecycleScope.launch {
          manager.remove(idsToDelete)
        }
        vm.exitModificationMode()
      }
      .setNegativeButton("取消", null)
      .show()
  }

  override fun onBackPressed() {
    if (vm.inModification) {
      vm.exitModificationMode()
    } else {
      super.onBackPressed()
    }
  }
}
