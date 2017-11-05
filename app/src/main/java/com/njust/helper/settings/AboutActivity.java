package com.njust.helper.settings;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.njust.helper.R;
import com.njust.helper.activity.BaseActivity;

/**
 * 关于
 *
 * @author zwb
 */
public class AboutActivity extends BaseActivity {
    @Override
    protected int layoutRes() {
        return R.layout.activity_about;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about, menu);
        MenuItem item = menu.findItem(R.id.item_share);
        ShareActionProvider provider = new ShareActionProvider(this);
        Intent shareIntent = new Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, getString(R.string.share_about));
        provider.setShareIntent(shareIntent);
        MenuItemCompat.setActionProvider(item, provider);
        return true;
    }

    public void feedback(View view) {
        new AlertDialog.Builder(this)
                .setTitle("意见反馈")
                .setMessage("您可以加入QQ群217887769进行反馈")
                .setPositiveButton("点击直接加群", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://jq.qq.com/?_wv=1027&k=2HCZ1MK"));
                    startActivity(intent);
                })
                .setNegativeButton("返回", null)
                .show();
    }

    public void comment(View view) {
        try {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            showSnack("未找到应用市场");
        }
    }

    public void updateLog(View view) {
        UpdateLogDialog.showUpdateDialog(this);
    }
}