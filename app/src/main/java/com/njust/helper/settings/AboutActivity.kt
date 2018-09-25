package com.njust.helper.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.net.Uri
import androidx.core.view.MenuItemCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.ShareActionProvider
import android.view.Menu
import com.njust.helper.R
import com.njust.helper.activity.BaseActivity
import com.njust.helper.databinding.ActivityAboutBinding
import com.njust.helper.update.UpdateLogDialog

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
                .setMessage("您可以加入QQ群217887769进行反馈")
                .setPositiveButton("点击直接加群") { _, _ ->
                    val intent = Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://jq.qq.com/?_wv=1027&k=2HCZ1MK"))
                    startActivity(intent)
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
