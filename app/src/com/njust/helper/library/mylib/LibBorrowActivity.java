package com.njust.helper.library.mylib;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.njust.helper.AccountActivity;
import com.njust.helper.R;
import com.njust.helper.activity.ProgressActivity;
import com.njust.helper.tools.AppHttpHelper;
import com.njust.helper.tools.JsonData;
import com.njust.helper.tools.JsonTask;
import com.njust.helper.tools.Prefs;
import com.zwb.commonlibs.http.HttpHelper;
import com.zwb.commonlibs.injection.ViewInjection;
import com.zwb.commonlibs.ui.ExtendedSwipeRefreshLayout;

import org.json.JSONObject;

public class LibBorrowActivity extends ProgressActivity {
    private String stuid, pwd;
    @ViewInjection(R.id.webView1)
    private WebView webView;
    @ViewInjection(R.id.progressBar)
    private ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void prepareViews() {
        stuid = Prefs.getId(this);
        pwd = Prefs.getLibPwd(this);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });
        attachAsyncTask(new UrlTask());
    }

    @Override
    protected void setupPullLayout(ExtendedSwipeRefreshLayout refreshLayout) {
        refreshLayout.setSwipeView(webView);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                attachAsyncTask(new UrlTask());
            }
        });
    }

    @Override
    protected int layoutRes() {
        return R.layout.activity_lib_borrow;
    }

    private class UrlTask extends JsonTask<Void, String> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(LibBorrowActivity.this, "正在加载", "请稍候……");
        }

        @Override
        protected JsonData<String> doInBackground(Void... params) {
            HttpHelper.HttpMap data = new HttpHelper.HttpMap();
            data.addParam("stuid", stuid).addParam("pwd", pwd);
            try {
                String s = new AppHttpHelper().getPostResult("libBorrow.php", data);
                return new JsonData<String>(s) {
                    @Override
                    protected String parseData(JSONObject jsonObject) throws Exception {
                        return jsonObject.getString("content");
                    }
                };
            } catch (Exception e) {
                e.printStackTrace();
                return JsonData.newNetErrorInstance();
            }
        }

        @Override
        protected void onPostExecute(JsonData<String> stringJsonData) {
            super.onPostExecute(stringJsonData);

            if (dialog != null) {
                dialog.dismiss();
            }
            setRefreshing(false);
        }

        @Override
        protected void onSuccess(String s) {
            webView.loadUrl(s);
        }

        @Override
        protected void onLogFailed() {
            changeAccount(AccountActivity.REQUEST_LIB);
        }

        @Override
        protected void onNetError() {
            showSnack(R.string.message_net_error);
        }
    }
}