package com.njust.helper.settings;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.njust.helper.BuildConfig;
import com.njust.helper.R;

public class UpdateLogDialog {
    private static CharSequence getMessage() {
        StringBuilder builder = new StringBuilder()
                .append(BuildConfig.VERSION_CODE)
                .append(" v")
                .append(BuildConfig.VERSION_NAME)
                .append("更新日志：\n");
        builder.append("1.修复 收藏的图书长按删除没有效果的问题\n");
        builder.append("2.优化 无收藏的图书时，界面显示相应的提示\n");
        return builder;
    }

    public static void showUpdateDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("更新日志：")
                .setNegativeButton(R.string.action_back, null)
                .setMessage(getMessage())
                .show();
    }
}
