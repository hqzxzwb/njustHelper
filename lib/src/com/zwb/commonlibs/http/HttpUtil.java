package com.zwb.commonlibs.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HttpUtil {
    public static HttpResponse doGet(String url, Map<String, String> params) {
        StringBuilder sb = new StringBuilder(url);
        sb.append('?');
        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                sb.append(URLEncoder.encode(entry.getKey(), "UTF-8")).append('=')
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8")).append('&');
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        sb.setLength(sb.length() - 1);
        return doGet(sb.toString());
    }

    public static HttpResponse doGet(String url) {
        HttpResponse httpResponse = new HttpResponse();
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            setupURLConnection(urlConnection);
            urlConnection.connect();
            httpResponse.responseCode = urlConnection.getResponseCode();
            try {
                httpResponse.inputStream = urlConnection.getInputStream();
            } catch (IOException e) {
                httpResponse.inputStream = urlConnection.getErrorStream();
            }
        } catch (IOException e) {
            httpResponse.suppressedIOException = e;
        }
        return httpResponse;
    }

    private static void setupURLConnection(HttpURLConnection urlConnection) {
        urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
        urlConnection.setConnectTimeout(10000);
        urlConnection.setReadTimeout(10000);
    }
}
