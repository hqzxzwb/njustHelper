package com.njust.helper.tools;

import android.content.Context;
import android.os.Build;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.njust.helper.BuildConfig;
import com.zwb.commonlibs.http.HttpHelper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zwb on 2015/4/12.
 * 扩展HttpHelper
 */
// TODO: 2016/6/4 change to volley implementation
public final class AppHttpHelper extends HttpHelper {
    private static AppHttpHelper instance = new AppHttpHelper();
    private RequestQueue requestQueue;

    public static AppHttpHelper getInstance() {
        return instance;
    }

    public void init(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    @Override
    protected void setupURLConnection(HttpURLConnection urlConnection) {
        super.setupURLConnection(urlConnection);
        urlConnection.setRequestProperty("njusthelper", String.valueOf(BuildConfig.VERSION_CODE));
        urlConnection.setRequestProperty("sdk", String.valueOf(Build.VERSION.SDK_INT));
        urlConnection.setRequestProperty("phone", Build.MANUFACTURER + " " + Build.MODEL);
    }

    @Override
    public String getGetResult(String form, HttpMap params) throws IOException {
        StringRequest request = new StringRequest(Request.Method.GET, Constants.BASE_URL + form + "?" + params.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("njusthelper", String.valueOf(BuildConfig.VERSION_CODE));
                map.put("sdk", String.valueOf(Build.VERSION.SDK_INT));
                map.put("phone", Build.MANUFACTURER + " " + Build.MODEL);
                return map;
            }
        };
        requestQueue.add(request);
        return super.getGetResult(Constants.BASE_URL + form, params);
    }

    @Override
    public String getPostResult(String form, HttpMap params) throws IOException {
        return super.getPostResult(Constants.BASE_URL + form, params);
    }
}
