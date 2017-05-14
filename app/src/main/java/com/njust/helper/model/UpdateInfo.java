package com.njust.helper.model;

import java.io.Serializable;

public class UpdateInfo implements Serializable {
    private int versionCode;
    private String updateLog;
    private String url;
    private String versionName;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getUpdateLog() {
        return updateLog;
    }

    public void setUpdateLog(String updateLog) {
        this.updateLog = updateLog;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    @Override
    public String toString() {
        return "发现新版本\n\n"
                + "版本号：" + versionName + "\n\n"
                + "更新日志：\n"
                + updateLog;
    }
}
