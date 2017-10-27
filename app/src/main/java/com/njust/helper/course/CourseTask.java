package com.njust.helper.course;

import android.app.ProgressDialog;

import com.njust.helper.R;
import com.njust.helper.model.CaptchaData;
import com.njust.helper.model.CourseData;
import com.njust.helper.model.CourseInfo;
import com.njust.helper.model.CourseLoc;
import com.njust.helper.tools.AppHttpHelper;
import com.njust.helper.tools.JsonData;
import com.njust.helper.tools.JsonTask;
import com.njust.helper.tools.Prefs;
import com.zwb.commonlibs.http.HttpMap;
import com.zwb.commonlibs.utils.JsonUtils;

import org.json.JSONObject;

class CourseTask extends JsonTask<Void, CourseData> {
    private final CourseActivity mActivity;
    private ProgressDialog mProgressDialog;

    public CourseTask(CourseActivity activity) {
        mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        set_progress(true);
    }

    @Override
    protected JsonData<CourseData> doInBackground(Void... params) {
        HttpMap data = new HttpMap();

        String stuid = Prefs.getId(mActivity);
        String pwd = Prefs.getJwcPwd(mActivity);

        data.addParam("stuid", stuid).addParam("pwd", pwd);

        try {
            String string = new AppHttpHelper().getPostResult("course1013.php", data);
            return new JsonData<CourseData>(string) {
                @Override
                protected CourseData parseData(JSONObject jsonObject) throws Exception {
                    CourseData courseData = new CourseData();
                    courseData.setCourseall(jsonObject.getString("courseall"));
                    courseData.setStartdate(jsonObject.getString("startdate"));
                    courseData.setInfos(JsonUtils.parseArray(jsonObject.getJSONArray("info"), CourseInfo.class));
                    courseData.setLocs(JsonUtils.parseArray(jsonObject.getJSONArray("loc"), CourseLoc.class));
                    return courseData;
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonData.newNetErrorInstance();
    }

    @Override
    protected void onPostExecute(JsonData<CourseData> result) {
        super.onPostExecute(result);
        set_progress(false);
    }

    private void set_progress(boolean b) {
        if (b) {
            mProgressDialog = ProgressDialog.show(mActivity, "请稍候...", "正在导入课表");
        } else if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    protected void onNetError() {
        mActivity.showSnack(R.string.message_net_error);
    }

    @Override
    protected void onCaptchaError(CaptchaData captchaData) {
        //TODO
//        CaptchaActivity.startCaptcha(mActivity, captchaData.getContent(), captchaData.getCookie());
    }

    @Override
    protected void onServerError() {
        mActivity.showSnack(R.string.message_server_error);
    }

    @Override
    protected void onSuccess(CourseData courseData) {
        Prefs.putCourseInfo(mActivity, courseData.getCourseall(), courseData.getStartdate());
        CourseManager dao = CourseManager.getInstance(mActivity);
        dao.clear();
        if (courseData.getInfos().size() > 0) {
            dao.add(courseData.getInfos(), courseData.getLocs());
            mActivity.showSnack(R.string.message_course_import_success);
        } else {
            mActivity.showSnack("您的课表似乎是空的，过几天再来试试吧~");
        }
        mActivity.refresh();
    }

    @Override
    protected void onLogFailed() {
        mActivity.relogin();
    }

}
