package com.njust.helper.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

abstract class BaseActivity : AppCompatActivity {
  constructor() : super()
  constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    layout()

    setupActionBar()
  }

  protected open fun layout() {
  }

  protected open fun setupActionBar() {
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

  protected open fun getViewForSnackBar(): View {
    return findViewById(Window.ID_ANDROID_CONTENT)
  }

  companion object {
    const val TAG = "BaseActivity"
  }
}
