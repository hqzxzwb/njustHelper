package com.njust.helper.localapi;

import android.support.annotation.WorkerThread;

import com.zwb.commonlibs.http.HttpResponse;
import com.zwb.commonlibs.http.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestElement {
    private static final int METHOD_MASK = 1;
    private static final int METHOD_GET = 0;
    private static final int METHOD_POST = 1;
    private static final int REGEX_MODE_MASK = 2;
    private static final int REGEX_MODE_SINGLE = 0;
    private static final int REGEX_MODE_LIST = 1 << 1;

    private int flags;
    private String url;
    private String post;
    private String regex;

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public void fromJson(JSONObject object) throws JSONException {
        url = object.getString("url");
        regex = object.getString("regex");
        post = object.optString("post");
        flags |= "post".equalsIgnoreCase(object.getString("method")) ? METHOD_POST : METHOD_GET;
        flags |= "list".equalsIgnoreCase(object.getString("regexMode")) ? REGEX_MODE_LIST : REGEX_MODE_SINGLE;
    }

    @WorkerThread
    List<String[]> execute(Map<String, String> args, ApiConfiguration.ProgressCallback callback) {
        String url = bindArgs(this.url, args);
        callback.onProgress("正在打开" + url + "...");
        HttpResponse response;
        if ((flags & METHOD_POST) != 0) {
            String post = bindArgs(this.post, args);
            // TODO: 2017/3/2 post支持
            throw new UnsupportedOperationException();
        } else {
            response = HttpUtil.doGet(url);
        }
        if (response.responseCode == HttpURLConnection.HTTP_OK) {
            String result = response.getStringResult();
            callback.onProgress(result);
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(result);
            List<String[]> list = new ArrayList<>();
            callback.onProgress("正在解析...");
            while (matcher.find()) {
                callback.onProgress(matcher.group());
                int count = matcher.groupCount();
                String[] ss = new String[count + 1];
                for (int i = 0; i <= count; i++) {
                    ss[i] = matcher.group(i);
                }
                list.add(ss);
            }
            return list;
        } else {
            callback.onProgress("访问" + url + "失败，错误" + response.getFailureText());
            return null;
        }
    }

    private String bindArgs(String format, Map<String, String> args) {
        for (Map.Entry<String, String> entry : args.entrySet()) {
            format = format.replaceAll(Pattern.quote("{" + entry.getKey() + "}"), entry.getValue());
        }
        return format;
    }
}
