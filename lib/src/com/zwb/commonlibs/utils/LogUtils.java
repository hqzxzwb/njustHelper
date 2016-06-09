package com.zwb.commonlibs.utils;

import android.util.Log;

public final class LogUtils {
    public static void i(Object o, Object log) {
        i(o.getClass().getSimpleName() + "@" + Integer.toHexString(o.hashCode()), log);
    }

    public static void w(Object o, Object log) {
        w(o.getClass().getSimpleName() + "@" + Integer.toHexString(o.hashCode()), log);
    }

    public static void i(String tag, Object log) {
        Log.i(tag, toString(log));
    }

    public static void w(String tag, Object log) {
        Log.w(tag, toString(log));
    }

    private static String toString(Object o) {
        if (o == null) {
            return "null";
        } else if (o instanceof Iterable) {
            Iterable<?> iterable = (Iterable<?>) o;
            StringBuilder builder = new StringBuilder();
            for (Object object : iterable) {
                builder.append(object.toString()).append("\n");
            }
            return builder.toString();
        }
        return o.toString();
    }
}
