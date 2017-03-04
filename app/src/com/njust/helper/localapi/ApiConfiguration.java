package com.njust.helper.localapi;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import com.zwb.commonlibs.utils.JsonUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ApiConfiguration {
    private static HashMap<String, ApiConfiguration> map = new HashMap<>();

    private List<RequestElement> requestElements = new ArrayList<>();
    private String[] fieldMap;

    public static void init(Context context) {
        File file = new File(context.getFilesDir(), "ApiConfiguration.json");
        String s;
        if (!file.exists()) {
            AssetManager am = context.getAssets();
            try {
                InputStream is = am.open("ApiConfiguration.json");
                try {
                    s = IOUtils.toString(is);
                } finally {
                    IOUtils.closeQuietly(is);
                }
            } catch (IOException e) {
                throw new RuntimeException("Exception caught when reading configuration", e);
            }
        } else {
            try {
                s = FileUtils.readFileToString(file);
            } catch (IOException e) {
                throw new RuntimeException("Exception caught when reading configuration", e);
            }
        }
        try {
            parse(s);
        } catch (JSONException e) {
            throw new RuntimeException("Exception when parsing configuration JSON", e);
        }
    }

    private static void parse(String s) throws JSONException {
        JSONObject o = new JSONObject(s);
        Iterator<String> keys = o.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            JSONObject item = o.getJSONObject(key);
            JSONArray a = item.getJSONArray("actions");
            ApiConfiguration ac = new ApiConfiguration();
            for (int i = a.length() - 1; i >= 0; i--) {
                RequestElement re = new RequestElement();
                re.fromJson(a.getJSONObject(i));
                ac.requestElements.add(re);
            }
            a = item.getJSONArray("map");
            int length = a.length();
            ac.fieldMap = new String[length];
            for (int i = 0; i < length; i++) {
                ac.fieldMap[i] = a.getString(i);
            }
            map.put(key, ac);
        }
    }

    public static ApiConfiguration getConfiguration(String name) {
        return map.get(name);
    }

    @WorkerThread
    public <T> List<T> execute(Map<String, String> args, Class<T> tClass, ProgressCallback callback) {
        List<String[]> elementResultList = null;
        for (RequestElement element : requestElements) {
            if (Thread.currentThread().isInterrupted()) {
                return null;
            }
            elementResultList = element.execute(args, callback);
            if (elementResultList == null) {
                return null;
            }
            args = new HashMap<>();
            if (!elementResultList.isEmpty()) {
                String[] strings = elementResultList.get(0);
                for (int i = strings.length - 1; i >= 0; i--) {
                    args.put(String.valueOf(i), strings[i]);
                }
            }
        }
        assert elementResultList != null;
        List<T> list = new ArrayList<>();
        for (String[] strings : elementResultList) {
            try {
                list.add(reflectClass(strings, tClass));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private <T> T reflectClass(String[] strings, Class<T> tClass) throws Exception {
        int length = strings.length;
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < length; i++) {
            String fieldName = fieldMap[i];
            if (TextUtils.isEmpty(fieldName)) continue;
            try {
                jsonObject.put(fieldName, strings[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return JsonUtils.parseBean(jsonObject, tClass);
    }

    public interface ProgressCallback {
        void onProgress(String progress);
    }
}
