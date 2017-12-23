package com.njust.helper.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UpdateInfo implements Parcelable {
    private int versionCode;
    private String updateLog;
    private String url;
    private String versionName;

    public UpdateInfo() {
    }

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

    protected UpdateInfo(Parcel in) {
        versionCode = in.readInt();
        updateLog = in.readString();
        url = in.readString();
        versionName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(versionCode);
        dest.writeString(updateLog);
        dest.writeString(url);
        dest.writeString(versionName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UpdateInfo> CREATOR = new Creator<UpdateInfo>() {
        @Override
        public UpdateInfo createFromParcel(Parcel in) {
            return new UpdateInfo(in);
        }

        @Override
        public UpdateInfo[] newArray(int size) {
            return new UpdateInfo[size];
        }
    };
}
