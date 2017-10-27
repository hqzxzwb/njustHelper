package com.njust.helper.grade;

import com.njust.helper.activity.WebViewActivity;
import com.njust.helper.tools.AppHttpHelper;
import com.njust.helper.tools.Prefs;
import com.zwb.commonlibs.http.HttpMap;


public class GradeLevelActivity extends WebViewActivity {
    private String stuid, pwd;

    @Override
    protected String getResponse() throws Exception {
        HttpMap data = new HttpMap();
        data.addParam("stuid", stuid).addParam("pwd", pwd);

        return new AppHttpHelper().getPostResult("grade_level.php", data);
    }

    @Override
    protected void loadId() {
        stuid = Prefs.getId(this);
        pwd = Prefs.getJwcPwd(this);
    }

    @Override
    protected String buildCacheName() {
        return "GradeLevel_" + stuid;
    }
}
