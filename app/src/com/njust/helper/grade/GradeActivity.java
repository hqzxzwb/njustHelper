package com.njust.helper.grade;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.webkit.JavascriptInterface;

import com.njust.helper.activity.WebViewActivity;
import com.njust.helper.tools.AppHttpHelper;
import com.njust.helper.tools.Prefs;
import com.zwb.commonlibs.http.HttpHelper;

public class GradeActivity extends WebViewActivity {
    private String stuid, pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewCompat.setTransitionName(getWebView(), "webView");
    }

    @Override
    protected String getResponse() throws Exception {
        HttpHelper.HttpMap data = new HttpHelper.HttpMap();
        data.addParam("stuid", stuid).addParam("pwd", pwd);
        return new AppHttpHelper().getPostResult("grade.php", data);
    }

    @SuppressLint("AddJavascriptInterface")
    @Override
    protected void loadId() {
        stuid = Prefs.getId(this);
        pwd = Prefs.getJwcPwd(this);
        getWebView().addJavascriptInterface(this, "appInterface");
    }

    @Override
    protected String createCacheName() {
        return "GradeNew_" + stuid;
    }

    @JavascriptInterface
    public void pingjiao() {
        Intent intent = new Intent(this, CommentTeacherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
