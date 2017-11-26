package com.njust.helper.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.ShareActionProvider
import android.view.Menu
import android.view.View
import com.njust.helper.R
import com.njust.helper.activity.BaseActivity

/**
 * 关于
 *
 * @author zwb
 */
class AboutActivity : BaseActivity() {
    override fun layoutRes(): Int {
        return R.layout.activity_about
    }

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

    fun feedback(view: View) {
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

    fun comment(view: View) {
        try {
            val uri = Uri.parse("market://details?id=" + packageName)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            showSnack("未找到应用市场")
        }

    }

    fun updateLog(view: View) {
        UpdateLogDialog.showUpdateDialog(this)
    }
}
