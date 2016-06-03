package com.njust.helper.grade;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.njust.helper.activity.WebViewActivity;
import com.njust.helper.tools.AppHttpHelper;
import com.njust.helper.tools.Prefs;
import com.zwb.commonlibs.http.HttpHelper;

public class CommentTeacherActivity extends WebViewActivity {
    private String stuid;

    @SuppressLint("AddJavascriptInterface")
    @Override
    protected void loadId() {
        stuid = Prefs.getId(this);
        getWebView().addJavascriptInterface(this,"appInterface");
    }

    @Override
    protected boolean emptyParam() {
        return stuid.equals("");
    }

    @Override
    protected String getResponse() throws Exception {
        HttpHelper.HttpMap data = new HttpHelper.HttpMap();
//        data.addParam("cookie", Prefs.getCookie(this), "url", Prefs.getUrl(this));
//TODO
        return new AppHttpHelper().getPostResult("commentTeacher0620.php", data);
    }

    @Override
    protected String createCacheName() {
        return "pingjiao_" + stuid;
    }

    @JavascriptInterface
    public void grade() {
        Intent intent = new Intent(this, GradeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}