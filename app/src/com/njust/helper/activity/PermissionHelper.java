package com.njust.helper.activity;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 动态申请权限相关
 */
public class PermissionHelper {
    public static void performSensitiveAction(final BaseActivity activity, final String[] permissions,
                                              HashMap<String, String> explanation, @NonNull final Runnable successCallback,
                                              final Runnable failCallback, final int requestCode) {
        List<String> list = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                list.add(permission);
            }
        }
        if (list.size() == 0) {
            successCallback.run();
        } else {
            final String[] permissionsToGrant = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                permissionsToGrant[i] = list.get(i);
            }
            StringBuilder builder = new StringBuilder();
            for (String permission : permissionsToGrant) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    builder.append(explanation.get(permission)).append("\n");
                }
            }
            if (builder.length() > 0) {
                new AlertDialog.Builder(activity)
                        .setTitle("App需要以下权限以继续操作：")
                        .setMessage(builder)
                        .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity, permissionsToGrant, requestCode);
                                activity.registerRequestPermissionsAction(requestCode, successCallback, failCallback);
                            }
                        })
                        .setCancelable(false)
                        .show();
            } else {
                ActivityCompat.requestPermissions(activity, permissionsToGrant, requestCode);
                activity.registerRequestPermissionsAction(requestCode, successCallback, failCallback);
            }
        }
    }
}
