package com.njust.helper.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.Window
import butterknife.ButterKnife
import com.njust.helper.R
import com.njust.helper.account.AccountActivity
import com.zwb.commonlibs.injection.InjectionHelper
import com.zwb.commonlibs.utils.LogUtils

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val res = layoutRes()
        if (res != 0) {
            setContentView(res)
        } else {
            layout()
        }

        InjectionHelper.injectActivity(this)
        ButterKnife.bind(this)

        setupActionBar()
    }

    @LayoutRes
    protected abstract fun layoutRes(): Int

    open protected fun layout() {
    }

    open protected fun setupActionBar() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun startActivity(cls: Class<out Activity>) {
        startActivity(Intent(this, cls))
    }

    fun startActivityForResult(cls: Class<out Activity>, requestCode: Int) {
        startActivityForResult(Intent(this, cls), requestCode)
    }

    fun showSnack(text: CharSequence) {
        Snackbar.make(getViewForSnackBar(), text, Snackbar.LENGTH_LONG).show()
    }

    fun showSnack(resId: Int, vararg args: Any) {
        showSnack(getString(resId, *args))
    }

    fun showSnack(text: CharSequence, actionName: CharSequence, action: View.OnClickListener) {
        Snackbar.make(getViewForSnackBar(), text, Snackbar.LENGTH_LONG)
                .setAction(actionName, action)
                .show()
    }

    open protected fun getViewForSnackBar(): View {
        return findViewById(Window.ID_ANDROID_CONTENT)
    }

    open protected fun changeAccount(accountRequest: Int) {
        try {
            AlertDialog.Builder(this)
                    .setMessage(R.string.message_wrong_password)
                    .setPositiveButton(R.string.dialog_base_modify_immediately,
                            { _, _ ->
                                val intent = Intent(this, AccountActivity::class.java)
                                intent.putExtra(AccountActivity.EXTRA_PASSWORD_TYPE, accountRequest)
                                startActivity(intent)
                            })
                    .setNegativeButton(R.string.action_back, null)
                    .show()
        } catch (e: Exception) {
            LogUtils.i(this, "建立对话框失败")
        }
    }
}
