package com.njust.helper.settings

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.njust.helper.R
import com.njust.helper.update.UpdateLogDialog
import com.zwb.commonlibs.utils.requireSystemService
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * 关于
 *
 * @author zwb
 */
class AboutActivity : AppCompatActivity() {
  private val snackbarMessageFlow = MutableSharedFlow<String>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      AboutScreen(
        onClickHome = { finish() },
        onClickFeedback = { onClickFeedback() },
        onClickComment = { onClickComment() },
        onClickUpdateLog = { onClickUpdateLog() },
        onClickShare = { onClickShare() },
        snackbarMessageFlow = snackbarMessageFlow,
      )
    }
  }

  private fun onClickFeedback() {
    AlertDialog.Builder(this)
      .setTitle("意见反馈")
      .setMessage("您可以加入QQ群${QQ_GROUP_ID}进行反馈")
      .setPositiveButton("点击直接加群") { _, _ ->
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(QQ_GROUP_URI))
        startActivity(intent)
      }
      .setNeutralButton("复制群号") { _, _ ->
        val clipboardManager = requireSystemService<ClipboardManager>()
        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, QQ_GROUP_ID))
        }
        .setNegativeButton("返回", null)
        .show()
  }

  private fun onClickComment() {
    try {
      val uri = Uri.parse("market://details?id=$packageName")
      val intent = Intent(Intent.ACTION_VIEW, uri)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      startActivity(intent)
    } catch (e: ActivityNotFoundException) {
      lifecycleScope.launchWhenResumed {
        snackbarMessageFlow.emit("未找到应用市场")
      }
    }
  }

  private fun onClickUpdateLog() {
    UpdateLogDialog.showUpdateDialog(this)
  }

  private fun onClickShare() {
    val shareIntent = Intent(Intent.ACTION_SEND)
      .setType("text/plain")
      .putExtra(Intent.EXTRA_TEXT, getString(R.string.share_about))
    try {
      startActivity(shareIntent)
    } catch (e: ActivityNotFoundException) {
      lifecycleScope.launchWhenResumed {
        snackbarMessageFlow.emit("未找到可分享的应用")
      }
    }
  }
}

private const val QQ_GROUP_ID = "217887769"
private const val QQ_GROUP_URI = "http://jq.qq.com/?_wv=1027&k=2HCZ1MK"
