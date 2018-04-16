package com.njust.helper

import android.databinding.BindingAdapter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.njust.helper.databinding.FgmtWebViewBinding

class WebViewFragment : Fragment() {
    private var binding: FgmtWebViewBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FgmtWebViewBinding.inflate(inflater, container, false)
        this.binding = binding
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        @JvmStatic
        @BindingAdapter("html")
        public fun setHtml(webView: WebView, html: String) {
            webView.loadDataWithBaseURL(null, html, null, "utf-8", null)
        }
    }
}
