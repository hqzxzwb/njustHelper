package com.njust.helper.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;

import com.google.android.gms.analytics.HitBuilders;
import com.njust.helper.AccountActivity;
import com.njust.helper.App;
import com.njust.helper.BuildConfig;
import com.njust.helper.R;
import com.zwb.commonlibs.injection.InjectionHelper;
import com.zwb.commonlibs.utils.LogUtils;

import java.lang.reflect.Field;

public abstract class BaseActivity extends AppCompatActivity {
    private SparseArray<Runnable> permissionActions = new SparseArray<>();
    private SparseArray<Runnable> permissionDeniedActions = new SparseArray<>();
    private long startTime;

    /**
     * 强制显示OverFlowButton
     */
    private void showOverFlowButton() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            showOverFlowButton();
        }

        setContentView(layoutRes());

        InjectionHelper.injectActivity(this);

        setupActionBar();

        if (!BuildConfig.DEBUG) {
            App.getDefaultTracker().send(new HitBuilders.EventBuilder()
                    .setCategory(getClass().getSimpleName())
                    .setAction("onCreate")
                    .build());
            startTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!BuildConfig.DEBUG) {
            App.getDefaultTracker().send(new HitBuilders.EventBuilder()
                    .setCategory(getClass().getSimpleName())
                    .setAction("onDestroy")
                    .setLabel("stayed for ? milliseconds")
                    .setValue(System.currentTimeMillis() - startTime)
                    .build());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!BuildConfig.DEBUG) {
            App.getDefaultTracker().setScreenName(getClass().getSimpleName());
            App.getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @LayoutRes
    protected abstract int layoutRes();

    protected void setupActionBar() {
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startActivity(Class<? extends Activity> class1) {
        startActivity(new Intent(this, class1));
    }

    public void startActivityForResult(Class<? extends Activity> class1, int requestCode) {
        startActivityForResult(new Intent(this, class1), requestCode);
    }

    public void showSnack(CharSequence text) {
        Snackbar.make(getViewForSnackBar(), text, Snackbar.LENGTH_LONG).show();
    }

    public void showSnack(int resId, Object... args) {
        showSnack(getString(resId, args));
    }

    public void showSnack(CharSequence text, CharSequence actionName, View.OnClickListener action) {
        Snackbar.make(getViewForSnackBar(), text, Snackbar.LENGTH_LONG)
                .setAction(actionName, action)
                .show();
    }

    protected View getViewForSnackBar() {
        return findViewById(Window.ID_ANDROID_CONTENT);
    }

    protected void changeAccount(final int account_request) {
        //可能在Activity退出后调用，所以用try防止建立对话框崩溃。
        try {
            new AlertDialog.Builder(this).setMessage(R.string.message_wrong_password)
                    .setPositiveButton(R.string.dialog_base_modify_immediately, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(BaseActivity.this, AccountActivity.class);
                            intent.putExtra(AccountActivity.EXTRA_PASSWORD_TYPE, account_request);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.action_back, null)
                    .show();
        } catch (Exception e) {
            LogUtils.i(this, "建立对话框失败");
        }
    }

    public void registerRequestPermissionsAction(int requestCode, Runnable successCallback, Runnable failCallback) {
        permissionActions.put(requestCode, successCallback);
        permissionDeniedActions.put(requestCode, failCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                Runnable runnable = permissionDeniedActions.get(requestCode);
                if (runnable != null) runnable.run();
                return;
            }
        }
        permissionActions.get(requestCode).run();
    }
}
