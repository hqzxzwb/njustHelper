package com.njust.helper.settings

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.view.Menu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.ShareActionProvider
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import com.njust.helper.R
import com.njust.helper.activity.BaseActivity
import com.njust.helper.databinding.ActivityAboutBinding
import com.njust.helper.update.UpdateLogDialog
import com.zwb.commonlibs.utils.requireSystemService

/**
 * 关于
 *
 * @author zwb
 */
class AboutActivity : BaseActivity() {
  override fun layout() {
    val binding = DataBindingUtil.setContentView<ActivityAboutBinding>(this, R.layout.activity_about)
    binding.clickHandler = this
  }

  override fun layoutRes(): Int = 0

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.about, menu)
    val item = menu.findItem(R.id.item_share)
    val provider = ShareActionProvider(this)
    val shareIntent = Intent(Intent.ACTION_SEND)
        .setType("text/plain")
        .putExtra(Intent.EXTRA_TEXT, getString(R.string.share_about))
    provider.setShareIntent(shareIntent)
    MenuItemCompat.setActionProvider(item, provider)
    return true
  }

  fun onClickFeedback() {
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

  fun onClickComment() {
    try {
      val uri = Uri.parse("market://details?id=$packageName")
      val intent = Intent(Intent.ACTION_VIEW, uri)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      startActivity(intent)
    } catch (e: ActivityNotFoundException) {
      showSnack("未找到应用市场")
    }

  }

  fun onClickUpdateLog() {
    UpdateLogDialog.showUpdateDialog(this)
  }
}

private const val QQ_GROUP_ID = "217887769"
private const val QQ_GROUP_URI = "http://jq.qq.com/?_wv=1027&k=2HCZ1MK"
