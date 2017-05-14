package com.zwb.commonlibs.injection;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.zwb.commonlibs.utils.LogUtils;

import java.lang.reflect.Field;

public class InjectionHelper {
    private static final String TAG = "InjectionHelper";

    public static void injectView(Object object, View view) {
        for (Field field : object.getClass().getDeclaredFields()) {
            ViewInjection injection = field.getAnnotation(ViewInjection.class);
            if (injection != null) {
                field.setAccessible(true);
                try {
                    field.set(object, view.findViewById(injection.value()));
                } catch (Exception e) {
                    reportError(field);
                }
            }
        }
    }

    public static void injectIntent(Activity activity, Intent intent) {
        Bundle bundle = intent.getExtras();
        Uri uri = intent.getData();
        for (Field field : activity.getClass().getDeclaredFields()) {
            IntentInjection injection = field.getAnnotation(IntentInjection.class);
            if (injection != null) {
                field.setAccessible(true);
                String string = injection.value();
                if (string.equals("")) {
                    string = field.getName();
                }
                try {
                    field.set(activity, bundle.get(string));
                } catch (Exception e) {
                    reportError(field);
                }
            }

            UriParamInjection uriParamInjection = field.getAnnotation(UriParamInjection.class);
            if (uriParamInjection != null) {
                field.setAccessible(true);
                String string = uriParamInjection.value();
                if (string.equals("")) {
                    string = field.getName();
                }
                try {
                    field.set(activity, uri.getQueryParameter(string));
                } catch (Exception e) {
                    reportError(field);
                }
            }
        }
    }

    public static void injectActivity(Activity activity) {
        Bundle bundle = activity.getIntent().getExtras();
        Uri uri = activity.getIntent().getData();
        for (Field field : activity.getClass().getDeclaredFields()) {
            ViewInjection viewInjection = field.getAnnotation(ViewInjection.class);
            if (viewInjection != null) {
                field.setAccessible(true);
                try {
                    field.set(activity, activity.findViewById(viewInjection.value()));
                } catch (Exception e) {
                    reportError(field);
                }
            }

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

            UriParamInjection uriParamInjection = field.getAnnotation(UriParamInjection.class);
            if (uriParamInjection != null) {
                field.setAccessible(true);
                String string = uriParamInjection.value();
                if (string.equals("")) {
                    string = field.getName();
                }
                try {
                    field.set(activity, uri.getQueryParameter(string));
                } catch (Exception e) {
                    reportError(field);
                }
            }
        }
    }

    private static void reportError(Field field) {
        LogUtils.w(TAG, field.getName() + "注入失败");
    }
}
