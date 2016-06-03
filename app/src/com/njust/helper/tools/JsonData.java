package com.njust.helper.tools;

import com.njust.helper.model.CaptchaData;
import com.zwb.commonlibs.utils.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class JsonData<T> {
    /**
     * 验证码错误
     */
    public static final int STATUS_CAPTCHA_ERROR = 4;
    /**
     * 登录失败
     */
    public static final int STATUS_LOG_FAIL = 2;
    /**
     * 网络错误
     */
    public static final int STATUS_NET_ERROR = -1;
    /**
     * 获取数据成功
     */
    public static final int STATUS_SUCCESS = 1;
    /**
     * 服务器问题
     */
    public static final int STATUS_SERVER_ERROR = 3;

    private T data;
    private int status;
    private CaptchaData captchaData;

    public JsonData(String string) throws Exception {
        JSONObject object = new JSONObject(string);
        status = object.getInt("state");
        if (isValid()) {
            data = parseData(object);
        } else if (status == STATUS_CAPTCHA_ERROR) {
            captchaData = JsonUtils.parseBean(object, CaptchaData.class);
        }
    }

    private JsonData(int status) {
        this.status = status;
    }

    public static <T> JsonData<T> newNetErrorInstance() {
        return new JsonData<T>(STATUS_NET_ERROR) {
            @Override
            protected T parseData(JSONObject jsonObject) throws JSONException {
                return null;
            }
        };
    }

    public static <T> JsonData<T> newLogFailedInstance() {
        return new JsonData<T>(STATUS_LOG_FAIL) {
            @Override
            protected T parseData(JSONObject jsonObject) throws JSONException {
                return null;
            }
        };
    }

    public boolean isValid() {
        return status == STATUS_SUCCESS;
    }

    public T getData() {
        return data;
    }

    public int getStatus() {
        return status;
    }

    protected abstract T parseData(JSONObject jsonObject) throws Exception;

    public CaptchaData getCaptchaData() {
        return captchaData;
    }
}
