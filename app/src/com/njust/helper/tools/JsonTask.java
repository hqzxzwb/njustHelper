package com.njust.helper.tools;

import android.os.AsyncTask;

import com.njust.helper.model.CaptchaData;

public abstract class JsonTask<Param, Result> extends AsyncTask<Param, Void, JsonData<Result>> {
    @Override
    protected void onPostExecute(JsonData<Result> resultJsonData) {
        switch (resultJsonData.getStatus()) {
            case JsonData.STATUS_SUCCESS:
                onSuccess(resultJsonData.getData());
                break;
            case JsonData.STATUS_LOG_FAIL:
                onLogFailed();
                break;
            case JsonData.STATUS_SERVER_ERROR:
                onServerError();
                break;
            case JsonData.STATUS_CAPTCHA_ERROR:
                onCaptchaError(resultJsonData.getCaptchaData());
                break;
            case JsonData.STATUS_NET_ERROR:
                onNetError();
                break;
        }
    }

    protected void onNetError() {

    }

    protected void onCaptchaError(CaptchaData captchaData) {

    }

    protected void onServerError() {

    }

    protected void onSuccess(Result result) {

    }

    protected void onLogFailed() {

    }
}
