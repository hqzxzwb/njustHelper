package com.njust.helper.library.borrowed;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.njust.helper.R;
import com.njust.helper.account.AccountActivity;
import com.njust.helper.activity.ProgressActivity;
import com.njust.helper.tools.AppHttpHelper;
import com.njust.helper.tools.JsonData;
import com.njust.helper.tools.Prefs;
import com.zwb.commonlibs.http.HttpMap;

import org.json.JSONObject;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LibBorrowActivity extends ProgressActivity implements SwipeRefreshLayout.OnRefreshListener {
    String stuid, pwd;
    @BindView(R.id.webView1)
    WebView webView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    ProgressDialog dialog;

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
    }

    @Override
    protected void firstRefresh() {
        onRefresh();
    }

    @Override
    protected void setupPullLayout(SwipeRefreshLayout refreshLayout) {
        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected boolean addRefreshLayoutAutomatically() {
        return false;
    }

    @Override
    protected int layoutRes() {
        return R.layout.activity_lib_borrow;
    }

    @Override
    public void onRefresh() {
        dialog = ProgressDialog.show(LibBorrowActivity.this, "正在加载", "请稍候……");
        Observable
                .fromCallable(() -> {
                    try {
                        HttpMap map = new HttpMap();
                        map.addParam("stuid", stuid);
                        map.addParam("pwd", pwd);
                        AppHttpHelper httpHelper = new AppHttpHelper();
                        String response = httpHelper.getPostResult("libBorrow.php", map);
                        return new JsonData<String>(response) {
                            @Override
                            protected String parseData(JSONObject jsonObject) throws Exception {
                                return jsonObject.getString("content");
                            }
                        };
                    } catch (Exception e) {
                        return JsonData.<String>newNetErrorInstance();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonData -> {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    setRefreshing(false);
                    switch (jsonData.getStatus()) {
                        case JsonData.STATUS_SUCCESS:
                            webView.loadUrl(jsonData.getData());
                            break;
                        case JsonData.STATUS_LOG_FAIL:
                            changeAccount(AccountActivity.REQUEST_LIB);
                            break;
                        case JsonData.STATUS_NET_ERROR:
                            showSnack(R.string.message_net_error);
                            break;
                    }
                });
    }
}
