package com.njust.helper.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.webkit.WebView;

import com.njust.helper.AccountActivity;
import com.njust.helper.R;
import com.njust.helper.model.CaptchaData;
import com.njust.helper.tools.JsonData;
import com.njust.helper.tools.ProgressAsyncTask;
import com.zwb.commonlibs.ui.ExtendedSwipeRefreshLayout;
import com.zwb.commonlibs.utils.MemCacheManager;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class WebViewActivity extends ProgressActivity implements SwipeRefreshLayout.OnRefreshListener {
    private WebView mWebView;
    private String mCacheName;

    @Override
    protected int layoutRes() {
        return R.layout.activity_only_webview;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void prepareViews() {
        mWebView = (WebView) findViewById(R.id.webView1);
        assert mWebView != null;
        mWebView.getSettings().setJavaScriptEnabled(true);

        loadId();

        mCacheName = createCacheName();
    }

    protected WebView getWebView() {
        return mWebView;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            switch (resultCode) {
                case RESULT_CANCELED:
                    finish();
                    break;
                case RESULT_OK:
                    onRefresh();
                    break;
            }
        }
    }

    @Override
    protected void firstRefresh() {
        String string = MemCacheManager.get(mCacheName);
        if (string == null) {
            onRefresh();
        } else {
            show_html(string);
        }
    }

    @Override
    protected void setupPullLayout(ExtendedSwipeRefreshLayout layout) {
        layout.setOnRefreshListener(this);
        layout.setSwipeView(mWebView);
    }

    protected abstract void loadId();

    protected abstract String createCacheName();

    @Override
    public void onRefresh() {
        attachAsyncTask(new MyTask());
    }

    private void show_html(String string) {
        mWebView.loadDataWithBaseURL(null, string, null, "utf-8", null);
    }

    protected boolean emptyParam() {
        return false;
    }

    protected abstract String getResponse() throws Exception;

    protected int getAccountRequest() {
        return AccountActivity.REQUEST_JWC;
    }

    protected int getServerErrorText() {
        return R.string.message_server_error;
    }

    private class MyTask extends ProgressAsyncTask<Void, String> {
        public MyTask() {
            super(WebViewActivity.this);
        }

        @Override
        protected JsonData<String> doInBackground(Void... params) {
            if (emptyParam()) {
                return JsonData.newLogFailedInstance();
            }
            try {
                String string = getResponse();
                return new JsonData<String>(string) {
                    @Override
                    protected String parseData(JSONObject jsonObject) throws JSONException {
                        return jsonObject.getString("content");
                    }
                };
            } catch (Exception e) {
                e.printStackTrace();
            }
            return JsonData.newNetErrorInstance();
        }

        @Override
        protected void onCancelled(JsonData<String> result) {
            if (result != null && result.isValid()) {
                MemCacheManager.put(mCacheName, result.getData());
            }
        }

        @Override
        protected void onNetError() {
            showSnack(R.string.message_net_error);
        }

        @Override
        protected void onCaptchaError(CaptchaData captchaData) {
            //TODO
//            CaptchaActivity.startCaptcha(WebViewActivity.this, captchaData.getContent(), captchaData.getCookie());
        }

        @Override
        protected void onServerError() {
            showSnack(getServerErrorText());
        }

        @Override
        protected void onSuccess(String s) {
            MemCacheManager.put(mCacheName, s);
            show_html(s);
        }

        @Override
        protected void onLogFailed() {
            show_html(getString(R.string.message_wrong_password));
            changeAccount(getAccountRequest());
        }
    }
}
