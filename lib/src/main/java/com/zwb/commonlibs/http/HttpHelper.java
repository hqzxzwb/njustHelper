package com.zwb.commonlibs.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.zwb.commonlibs.utils.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zwb on 2015/4/11.
 * 基于URLConnection的Http实现
 */
public class HttpHelper {
    private String encoding = "utf-8";

    private static String inputStream2String(InputStream is, String encoding) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        return baos.toString(encoding);
    }

    public static boolean getNetworkState(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED;
    }

    private static String buildUrl(String url, HttpMap map) {
        return map != null ? url + "?" + map : url;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getGetResult(String url) throws IOException {
        String string = inputStream2String(getGetInputStream(url), encoding);
        LogUtils.i(this, string);
        return string;
    }

    public String getGetResult(String url, HttpMap map) throws IOException {
        return getGetResult(buildUrl(url, map));
    }

    public InputStream getGetInputStream(String url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        setupURLConnection(urlConnection);
        urlConnection.connect();
        return urlConnection.getInputStream();
    }

    public String getPostResult(String url, HttpMap data) throws IOException {
        String string = inputStream2String(getPostInputStream(url, data), encoding);
        LogUtils.i(this, string);
        return string;
    }

    public InputStream getPostInputStream(String url, HttpMap data) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        setupURLConnection(urlConnection);
        if (data != null) {
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.getOutputStream().write(data.toString().getBytes());
            urlConnection.getOutputStream().flush();
            urlConnection.getOutputStream().close();
        }
//        LogUtils.i(this, "responseCode=" + urlConnection.getResponseCode());
        return urlConnection.getInputStream();
    }

    protected void setupURLConnection(HttpURLConnection urlConnection) {
        urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
        urlConnection.setConnectTimeout(10000);
        urlConnection.setReadTimeout(10000);
    }

    public static class HttpMap {
        private Map<String, String> map = new HashMap<>();

        public HttpMap addParam(String name, Object value) {
            map.put(name, value == null ? "" : value.toString());
            return this;
        }

        public HttpMap clear() {
            map.clear();
            return this;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                try {
                    builder.append(URLEncoder.encode(entry.getKey(), "utf-8"))
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue().toString(), "utf-8"))
                            .append("&");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            if (builder.length() > 0) {
                builder.deleteCharAt(builder.length() - 1);
            }
            return builder.toString();
        }
    }
}
