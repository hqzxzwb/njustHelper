package com.njust.helper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.njust.helper.activity.BaseActivity;
import com.njust.helper.tools.Prefs;
import com.zwb.commonlibs.injection.IntentInjection;
import com.zwb.commonlibs.injection.ViewInjection;

/**
 * 账户切换
 *
 * @author zwb
 */
public class AccountActivity extends BaseActivity {
    // 由错误密码触发的账户切换页面打开，教务处密码错误将使焦点落到教务处密码EditText，图书馆密码错误使焦点落到图书馆密码EditText。
    public static final int REQUEST_JWC = 2;
    public static final int REQUEST_LIB = 4;

    public static final String EXTRA_PASSWORD_TYPE = "password_type";

    @ViewInjection(R.id.editText1)
    private EditText stuidText;
    @ViewInjection(R.id.editText2)
    private EditText jwcPwdText;
    @ViewInjection(R.id.editText3)
    private EditText libPwdText;

    @IntentInjection(EXTRA_PASSWORD_TYPE)
    private int type;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // 读入原账号数据
        stuidText.setText(Prefs.getId(this));
        jwcPwdText.setText(Prefs.getJwcPwd(this));
        libPwdText.setText(Prefs.getLibPwd(this));

        // 设置焦点
        switch (type) {
            case REQUEST_JWC:
                jwcPwdText.requestFocus();
                break;
            case REQUEST_LIB:
                libPwdText.requestFocus();
                break;
            default:
                stuidText.requestFocus();
                break;
        }
    }

    @Override
    protected int layoutRes() {
        return R.layout.activity_account;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_save:
                String stuid = stuidText.getText().toString().trim();
                String jwcPwd = jwcPwdText.getText().toString();
                String libPwd = libPwdText.getText().toString();
                if (stuid.equals("")) {
                    showSnack(getString(R.string.toast_input_id));
                    return true;
                }
                if (jwcPwd.equals("")) {
                    showSnack(getString(R.string.toast_input_jwc_pwd));
                    return true;
                }
                if (libPwd.equals("")) {
                    showSnack(getString(R.string.toast_input_lib_pwd));
                    return true;
                }
                Prefs.putIdValues(this, stuid, jwcPwd, libPwd);
                Prefs.putCookie(this, "", null, 1);
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            case R.id.item_help:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_account_help)
                        .setMessage(R.string.message_account_passwords)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}