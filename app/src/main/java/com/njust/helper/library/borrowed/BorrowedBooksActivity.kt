package com.njust.helper.library.borrowed

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.njust.helper.R
import com.njust.helper.account.AccountActivity
import com.njust.helper.activity.BaseActivity
import com.njust.helper.databinding.ActivityLibBorrowBinding
import com.njust.helper.tools.JsonData
import com.njust.helper.tools.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers

class BorrowedBooksActivity : BaseActivity() {
    private lateinit var stuid: String
    private lateinit var pwd: String
    private var dialog: ProgressDialog? = null

    private lateinit var binding: ActivityLibBorrowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        stuid = Prefs.getId(this)
        pwd = Prefs.getLibPwd(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        onRefresh()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun layout() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lib_borrow)

        binding.swipeRefreshLayout.setOnRefreshListener { onRefresh() }

        binding.webView1.settings.javaScriptEnabled = true
        binding.webView1.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                binding.loadingPage = true
            }

            override fun onPageFinished(view: WebView, url: String?) {
                binding.loadingPage = false
            }
        }
    }

    override fun layoutRes(): Int = 0

    private fun onRefresh() {
        dialog = ProgressDialog.show(this@BorrowedBooksActivity, "正在加载", "请稍候……")
        BorrowedBooksApi.INSTANCE
                .borrowedBooks(stuid, pwd)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    dialog?.dismiss()
                    binding.loading = false
                    when (it.status) {
                        JsonData.STATUS_SUCCESS -> binding.webView1.loadUrl(it.data)
                        JsonData.STATUS_LOG_FAIL -> AccountActivity.alertPasswordError(this, AccountActivity.REQUEST_LIB)
                    }
                }, {
                    dialog?.dismiss()
                    binding.loading = false
                    showSnack(R.string.message_net_error)
                })
    }
}
