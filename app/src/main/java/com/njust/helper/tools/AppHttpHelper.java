package com.njust.helper.tools;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.njust.helper.BuildConfig;
import com.zwb.commonlibs.http.HttpHelper;
import com.zwb.commonlibs.http.HttpMap;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by zwb on 2015/4/12.
 * 扩展HttpHelper
 */
public final class AppHttpHelper extends HttpHelper {
    private static AppHttpHelper instance = new AppHttpHelper();
    private RequestQueue requestQueue;

    public static AppHttpHelper getInstance() {
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public void init(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }


    @Override
    protected void setupURLConnection(@NonNull HttpURLConnection urlConnection) {
        super.setupURLConnection(urlConnection);
        urlConnection.setRequestProperty("njusthelper", String.valueOf(BuildConfig.VERSION_CODE));
        urlConnection.setRequestProperty("sdk", String.valueOf(Build.VERSION.SDK_INT));
        urlConnection.setRequestProperty("phone", Build.MANUFACTURER + " " + Build.MODEL);
    }

    @Override
    public String getGetResult(@NonNull String form, @NonNull HttpMap params) throws IOException {
        return super.getGetResult(BuildConfig.BASE_URL + form, params);
    }

    @Override
    public String getPostResult(@NonNull String form, HttpMap params) throws IOException {
        return super.getPostResult(BuildConfig.BASE_URL + form, params);
    }
}
