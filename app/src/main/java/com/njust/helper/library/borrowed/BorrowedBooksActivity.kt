package com.njust.helper.library.borrowed

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.njust.helper.R
import com.njust.helper.account.AccountActivity
import com.njust.helper.activity.BaseActivity
import com.njust.helper.api.LoginErrorException
import com.njust.helper.api.library.LibraryApi
import com.njust.helper.databinding.ActivityLibBorrowBinding
import com.njust.helper.tools.Prefs
import kotlinx.coroutines.launch

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
        lifecycleScope.launch {
            try {
                val result = LibraryApi.borrowed(stuid, pwd)
                binding.webView1.loadUrl(result)
            } catch (e: Exception) {
                if (e is LoginErrorException) {
                    AccountActivity.alertPasswordError(
                        this@BorrowedBooksActivity,
                        AccountActivity.REQUEST_LIB
                    )
                } else {
                    showSnack(R.string.message_net_error)
                }
            } finally {
                dialog?.dismiss()
                binding.loading = false
            }
        }
    }
}
