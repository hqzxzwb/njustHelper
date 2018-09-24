package com.njust.helper.tools;

import android.support.annotation.Keep;

import org.json.JSONObject;

@Keep
public class JsonData<T> {
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

    private T content;
    private int status;

    public JsonData() {
    }

    public JsonData(String string) throws Exception {
        JSONObject object = new JSONObject(string);
        status = object.getInt("state");
        if (isValid()) {
            content = parseData(object);
        }
    }

    public boolean isValid() {
        return status == STATUS_SUCCESS;
    }

    public T getContent() {
        return content;
    }

    public int getStatus() {
        return status;
    }

    protected T parseData(JSONObject jsonObject) throws Exception {
        return null;
    }
}
