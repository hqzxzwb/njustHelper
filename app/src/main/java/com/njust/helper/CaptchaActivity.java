package com.njust.helper;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.njust.helper.activity.ProgressActivity;
import com.njust.helper.model.CaptchaData;
import com.njust.helper.model.LoginResult;
import com.njust.helper.tools.AppHttpHelper;
import com.njust.helper.tools.JsonData;
import com.njust.helper.tools.JsonTask;
import com.njust.helper.tools.Prefs;
import com.zwb.commonlibs.http.HttpMap;
import com.zwb.commonlibs.injection.IntentInjection;
import com.zwb.commonlibs.utils.JsonUtils;
import com.zwb.commonlibs.utils.LogUtils;

import org.json.JSONObject;

import butterknife.BindView;

public class CaptchaActivity extends ProgressActivity {
    public static final int REQUEST_CAPTCHA = CaptchaActivity.class.hashCode() >> 16;
    String stuid, pwd;
    @BindView(R.id.imageView1)
    ImageView imageView;
    @BindView(R.id.editText1)
    EditText editText;
    @BindView(R.id.button1)
    Button button;
    @BindView(R.id.layoutCaptcha)
    TextInputLayout textInputLayout;
    String cookie;
    /**
     * 0 - 教务处, 1 - 图书馆
     */
    @IntentInjection
    int type;

    public static void startCaptcha(Activity activity, int type) {
        Intent intent = new Intent(activity, CaptchaActivity.class);
        intent.putExtra("type", type);
        activity.startActivityForResult(intent, REQUEST_CAPTCHA);
    }

    Bitmap getBitmapFromBase64String(String string) {
        byte[] bytes = Base64.decode(string, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    protected int layoutRes() {
        return R.layout.activity_captcha;
    }

    @Override
    protected void prepareViews() {
        stuid = Prefs.getId(this);
        pwd = type == 0 ? Prefs.getJwcPwd(this) : Prefs.getLibPwd(this);

        attachAsyncTask(new CaptchaTask());
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageView1:
                attachAsyncTask(new CaptchaTask());
                break;
            case R.id.button1:
                String s = editText.getText().toString();
                if (TextUtils.isEmpty(s)) {
                    textInputLayout.setError("验证码不能为空");
                } else {
                    attachAsyncTask(new LoginTask(), editText.getText().toString());
                }
                break;
        }
    }

    @Override
    public void setRefreshing(boolean b) {
        super.setRefreshing(b);
        button.setEnabled(!b);
        imageView.setEnabled(!b);
    }

    private class CaptchaTask extends JsonTask<Void, CaptchaData> {
        private Dialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            editText.setText("");

            progress = ProgressDialog.show(CaptchaActivity.this, "", "正在加载验证码...");
        }

        @Override
        protected JsonData<CaptchaData> doInBackground(Void... params) {
            try {
                String string = new AppHttpHelper().getGetResult(type == 0 ? "jwcCaptcha.php" : "libCaptcha.php", null);
                return new JsonData<CaptchaData>(string) {
                    @Override
                    protected CaptchaData parseData(JSONObject jsonObject) throws Exception {
                        return JsonUtils.parseBean(jsonObject, CaptchaData.class);
                    }
                };
            } catch (Exception e) {
                e.printStackTrace();
            }
            return JsonData.newNetErrorInstance();
        }

        @Override
        protected void onSuccess(CaptchaData captchaData) {
            imageView.setImageBitmap(getBitmapFromBase64String(captchaData.getContent()));
            CaptchaActivity.this.cookie = captchaData.getCookie();
            textInputLayout.setError("");
        }

        @Override
        protected void onNetError() {
            textInputLayout.setError(getString(R.string.message_net_error));
        }

        @Override
        protected void onPostExecute(JsonData<CaptchaData> captchaDataJsonData) {
            super.onPostExecute(captchaDataJsonData);

            if (progress != null) progress.dismiss();
        }

        @Override
        protected void onServerError() {
            textInputLayout.setError(getString(R.string.message_server_error));
        }
    }

    private class LoginTask extends JsonTask<String, LoginResult> {
        private Dialog progress;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(CaptchaActivity.this, "", "正在登录...");
        }

        @Override
        protected JsonData<LoginResult> doInBackground(String... params) {
            HttpMap data = new HttpMap();
            data.addParam("stuid", stuid)
                    .addParam("pwd", pwd)
                    .addParam("captcha", params[0])
                    .addParam("cookie", cookie);
            try {
                String string = new AppHttpHelper().getPostResult(type == 0 ? "getCookie.php" : "libCookie.php", data);
                return new JsonData<LoginResult>(string) {
                    @Override
                    protected LoginResult parseData(JSONObject jsonObject) throws Exception {
                        return JsonUtils.parseBean(jsonObject, LoginResult.class);
                    }
                };
            } catch (Exception e) {
                e.printStackTrace();
            }
            return JsonData.newNetErrorInstance();
        }

        @Override
        protected void onNetError() {
            textInputLayout.setError(getString(R.string.message_net_error));
        }

        @Override
        protected void onPostExecute(JsonData<LoginResult> loginResultJsonData) {
            super.onPostExecute(loginResultJsonData);
            if (progress != null) progress.dismiss();
        }

        @Override
        protected void onCaptchaError(CaptchaData captchaData) {
            textInputLayout.setError("验证码错误，请重新输入");
            editText.setText("");
            CaptchaActivity.this.cookie = captchaData.getCookie();
        }

        @Override
        protected void onServerError() {
            textInputLayout.setError(getString(R.string.message_server_error));
        }

        @Override
        protected void onSuccess(LoginResult result) {
            LogUtils.i(this, result.getCookie());
            Prefs.putCookie(CaptchaActivity.this, result.getCookie(), result.getUrl(), type);
            setResult(RESULT_OK);
            finish();
        }

        @Override
        protected void onLogFailed() {
            changeAccount(type == 0 ? AccountActivity.REQUEST_JWC : AccountActivity.REQUEST_LIB);
        }
    }
}
