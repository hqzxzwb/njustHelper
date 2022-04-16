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

    public static String getUrl(Context context) {
        return getPreference(context, "jwc").getString("url", "");
    }

    public static void putIdValues(Context context, String id, String jwcPwd, String libPwd) {
        Editor editor = getEditor(context, "jwc");
        editor.putString("studentID", id)
                .putString("password", jwcPwd)
                .putString("libPwd", libPwd)
                .putString("cookie", "");
        editor.apply();
    }

    public static void putJwcPwd(Context context, String jwcPwd) {
        Editor editor = getEditor(context, "jwc");
        editor
                .putString("password", jwcPwd)
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
