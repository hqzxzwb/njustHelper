package com.njust.helper.library.borrowed;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.njust.helper.account.AccountActivity;
import com.njust.helper.BuildConfig;
import com.njust.helper.R;
import com.njust.helper.activity.ProgressActivity;
import com.njust.helper.tools.AppHttpHelper;
import com.njust.helper.tools.JsonData;
import com.njust.helper.tools.Prefs;
import com.zwb.commonlibs.utils.LogUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

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

        Request<JsonData<String>> request = new Request<JsonData<String>>(Request.Method.POST,
                BuildConfig.BASE_URL + "libBorrow.php",
                error -> {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    setRefreshing(false);
                    showSnack(R.string.message_net_error);
                }) {
            Response.Listener<JsonData<String>> listener = new Response.Listener<JsonData<String>>() {
                @Override
                public void onResponse(JsonData<String> response) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    setRefreshing(false);
                    switch (response.getStatus()) {
                        case JsonData.STATUS_SUCCESS:
                            webView.loadUrl(response.getData());
                            break;
                        case JsonData.STATUS_LOG_FAIL:
                            changeAccount(AccountActivity.REQUEST_LIB);
                            break;
                    }
                }
            };

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("stuid", stuid);
                map.put("pwd", pwd);
                return map;
            }

            @Override
            protected Response<JsonData<String>> parseNetworkResponse(NetworkResponse response) {
                try {
                    String string = new String(response.data);
                    LogUtils.i(LibBorrowActivity.class, string);
                    JsonData<String> jsonData = new JsonData<String>(string) {
                        @Override
                        protected String parseData(JSONObject jsonObject) throws Exception {
                            return jsonObject.getString("content");
                        }
                    };
                    return Response.success(jsonData, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return Response.error(new VolleyError("数据解析失败"));
            }

            @Override
            protected void deliverResponse(JsonData<String> response) {
                listener.onResponse(response);
            }
        };
        AppHttpHelper.getInstance().getRequestQueue().add(request);
    }
}