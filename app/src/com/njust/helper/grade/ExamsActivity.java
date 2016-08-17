package com.njust.helper.grade;

import com.njust.helper.activity.WebViewActivity;
import com.njust.helper.tools.AppHttpHelper;
import com.njust.helper.tools.Prefs;
import com.zwb.commonlibs.http.HttpHelper;

public class ExamsActivity extends WebViewActivity {

    private String stuid, pwd;

    @Override
    protected void loadId() {
        stuid = Prefs.getId(this);
        pwd = Prefs.getJwcPwd(this);
    }

    @Override
    protected boolean emptyParam() {
        return stuid.equals("");
    }


    @Override
    protected String buildCacheName() {
        return "exams_" + stuid;
    }

    @Override
    protected String getResponse() throws Exception {
        HttpHelper.HttpMap data = new HttpHelper.HttpMap();
        data.addParam("stuid", stuid).addParam("pwd", pwd);

        return new AppHttpHelper().getPostResult("exams.php", data);
    }

}
