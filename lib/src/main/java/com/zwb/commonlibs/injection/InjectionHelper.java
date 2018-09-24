package com.zwb.commonlibs.injection;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Field;

public class InjectionHelper {
    private static final String TAG = "InjectionHelper";

    public static void injectActivity(Activity activity) {
        Bundle bundle = activity.getIntent().getExtras();
        for (Field field : activity.getClass().getDeclaredFields()) {
            IntentInjection intentInjection = field.getAnnotation(IntentInjection.class);
            if (intentInjection != null) {
                field.setAccessible(true);
                String string = intentInjection.value();
                if (string.equals("")) {
                    string = field.getName();
                }
                try {
                    field.set(activity, bundle.get(string));
                } catch (Exception e) {
                    reportError(field);
                }
            }
        }
    }

    private static void reportError(Field field) {
        Log.w(TAG, field.getName() + "注入失败");
    }
}
