package com.njust.helper.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Prefs {
    private static SharedPreferences getPreference(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor(Context context, String name) {
        return getPreference(context, name).edit();
    }

    public static String getId(Context context) {
        return getPreference(context, "jwc").getString("studentID", "");
    }

    public static String getJwcPwd(Context context) {
        return getPreference(context, "jwc").getString("password", "");
    }

    public static String getLibPwd(Context context) {
        return getPreference(context, "jwc").getString("libPwd", "");
    }

    public static String getCookie(Context context, int type) {
        return getPreference(context, "jwc").getString(type == 0 ? "jwcCookie" : "libCookie", "");
    }

    public static String getUrl(Context context) {
        return getPreference(context, "jwc").getString("url", "");
    }

    public static boolean getLibCollectionHint(Context context) {
        return getPreference(context, "refresh").getBoolean("libCollectionHint", false);
    }

    public static void putLibCollectionHint(Context context, boolean hint) {
        getEditor(context, "refresh").putBoolean("libCollectionHint", hint).apply();
    }

    public static void putCookie(Context context, String cookie, String url, int type) {
        Editor editor = getEditor(context, "jwc");
        if (type == 0) {
            editor.putString("jwcCookie", cookie).putString("url", url);
        } else {
            editor.putString("libCookie", cookie);
        }
        editor.apply();
    }

    public static void putIdValues(Context context, String id, String jwcPwd, String libPwd) {
        Editor editor = getEditor(context, "jwc");
        editor.putString("studentID", id)
                .putString("password", jwcPwd)
                .putString("libPwd", libPwd)
                .putString("cookie", "");
        editor.apply();
    }

    public static int getVersion(Context context) {
        return getPreference(context, "refresh").getInt("version", 0);
    }

    public static void putVersion(Context context, int version) {
        getEditor(context, "refresh")
                .putInt("version", version)
                .apply();
    }

    public static int getCourseNotificationTime(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("course_time", 1200);
    }

    public static int getCourseNotificationMode(Context context) {
        String string = PreferenceManager.getDefaultSharedPreferences(context).getString("mode", "0");
        return Integer.parseInt(string);
    }
}
