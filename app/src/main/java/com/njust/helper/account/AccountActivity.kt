package com.njust.helper.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.njust.helper.R
import com.njust.helper.RemoteConfig
import com.njust.helper.activity.BaseActivity
import com.njust.helper.main.MainActivity
import com.njust.helper.tools.Prefs
import kotlinx.android.synthetic.main.activity_account.*

/**
 * 账户切换
 *
 * @author zwb
 */
class AccountActivity : BaseActivity() {
    companion object {
        const val REQUEST_JWC = 2
        const val REQUEST_LIB = 4
        const val EXTRA_PASSWORD_TYPE = "password_type"

        @JvmStatic
        fun alertPasswordError(context: Context, accountRequest: Int) {
            try {
                AlertDialog.Builder(context)
                        .setMessage(R.string.message_wrong_password)
                        .setPositiveButton(R.string.dialog_base_modify_immediately) { _, _ ->
                            val intent = Intent(context, AccountActivity::class.java)
                            intent.putExtra(AccountActivity.EXTRA_PASSWORD_TYPE, accountRequest)
                            context.startActivity(intent)
                        }
                        .setNegativeButton(R.string.action_back, null)
                        .show()
            } catch (e: Exception) {
                Log.i(TAG, "建立对话框失败")
            }
        }

    }

    private var type: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        type = intent.getIntExtra(EXTRA_PASSWORD_TYPE, 0)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // 读入原账号数据
        stuidText.setText(Prefs.getId(this))
        jwcPwdText.setText(Prefs.getJwcPwd(this))
        libPwdText.setText(Prefs.getLibPwd(this))

        termIdText.setText(Prefs.getStuTermId(this))
        termStartIdText.setText(Prefs.getStuTermStartId(this))

        RemoteConfig.setTerm(Prefs.getStuTermId(this).toString(),Prefs.getStuTermStartId(this).toString())

        // 设置焦点
        when (type) {
            REQUEST_JWC -> jwcPwdText.requestFocus()
            REQUEST_LIB -> libPwdText.requestFocus()
            else -> stuidText.requestFocus()
        }
    }

    override fun layoutRes(): Int {
        return R.layout.activity_account
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.account, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_save -> {
                val stuid = stuidText.text.toString().trim()
                val jwcPwd = jwcPwdText.text.toString()
                val libPwd = libPwdText.text.toString()

                val termId = termIdText.text.toString().trim()
                val termStartId = termStartIdText.text.toString().trim()

                if (stuid == "") {
                    showSnack(getString(R.string.toast_input_id))
                    return true
                }
                if (jwcPwd == "") {
                    showSnack(getString(R.string.toast_input_jwc_pwd))
                    return true
                }
                if (libPwd == "") {
                    showSnack(getString(R.string.toast_input_lib_pwd))
                    return true
                }
                if (termId == "") {
                    showSnack(getString(R.string.toast_input_term_id))
                    return true
                }
                if (termStartId == "") {
                    showSnack(getString(R.string.toast_input_term_start_id))
                    return true
                }

                //调用储存结构
                RemoteConfig.setTerm(termId,termStartId)

                Prefs.putIdValues(this, stuid, jwcPwd, libPwd,termId,termStartId)
                Prefs.putCookie(this, "", null, 1)
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }
}
