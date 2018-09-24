package com.njust.helper.tools;

import android.support.annotation.Keep;

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
    private int state;

    public JsonData() {
    }

    public boolean isValid() {
        return state == STATUS_SUCCESS;
    }

    public T getContent() {
        return content;
    }

    public int getState() {
        return state;
    }
}
