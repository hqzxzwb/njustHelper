package com.zwb.commonlibs.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PermissionUtils {
    private static final String TAG = "PermissionUtils";
    private static Random random = new Random();
    // TODO: 16/10/11 如果一次权限请求没有返回结果(一般也不会发生),说不定就会有内存泄漏。待优化。
    private static SparseArray<Holder> holderSparseArray = new SparseArray<>();

    public static void checkPermission(final Activity activity, final Holder holder) {
        final List<String> toRequest = new ArrayList<>();
        List<String> toShowRationale = new ArrayList<>();
        for (String s : holder.permissions()) {
            if (ActivityCompat.checkSelfPermission(activity, s) == PackageManager.PERMISSION_DENIED) {
                toRequest.add(s);
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, s)) {
                    toShowRationale.add(s);
                }
            }
        }
        if (toRequest.isEmpty()) {
            holder.onGrant();
            return;
        }
        String rationale;
        if (toShowRationale.isEmpty() || (rationale = holder.onShowRationale(toShowRationale)) == null) {
            int requestCode;
            do {
                requestCode = random.nextInt(0xFFFF);   // 1~0xFFFF
            } while (holderSparseArray.indexOfKey(requestCode) >= 0);
            ActivityCompat.requestPermissions(activity, toRequest.toArray(
                    new String[toRequest.size()]), requestCode);
            holderSparseArray.put(requestCode, holder);
        } else {
            // show rationale
            new AlertDialog.Builder(activity)
                    .setTitle(holder.getRationaleDialogTitle())
                    .setMessage(rationale)
                    .setPositiveButton(holder.getRationalePositionButtonText(),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int requestCode;
                                    do {
                                        requestCode = random.nextInt(0xFFFF);   // 0x0~0xFFFF
                                    } while (holderSparseArray.indexOfKey(requestCode) >= 0);
                                    ActivityCompat.requestPermissions(activity, toRequest.toArray(
                                            new String[toRequest.size()]), requestCode);
                                    holderSparseArray.put(requestCode, holder);
                                }
                            })
                    .setCancelable(false)
                    .show();
        }
    }

    public static void handlePermissionResult(int requestCode, int[] grantResults) {
        Holder holder = holderSparseArray.get(requestCode);
        if (holder == null) {
            return;
        }
        boolean success = true;
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                success = false;
                break;
            }
        }
        if (success) {
            holder.onGrant();
        } else {
            holder.onDeny();
        }
        holderSparseArray.remove(requestCode);
    }

    public static void goToSetting(Activity activity, String appId) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + appId));
        activity.startActivity(intent);
    }

    public interface Holder {
        String[] permissions();

        void onGrant();

        void onDeny();

        /**
         * 显示权限解释
         *
         * @param permissions 要解释的权限
         * @return 说明文字
         */
        String onShowRationale(List<String> permissions);

        String getRationaleDialogTitle();

        String getRationalePositionButtonText();
    }
}
