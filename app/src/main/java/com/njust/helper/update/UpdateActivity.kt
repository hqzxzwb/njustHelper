package com.njust.helper.update

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.njust.helper.R
import com.njust.helper.databinding.ActivityUpdateBinding
import com.njust.helper.model.UpdateInfo
import com.njust.helper.tools.Constants
import com.njust.helper.tools.JsonData
import com.njust.helper.update.UpdateActivityVm.Companion.STATE_DOWNLOADING
import com.njust.helper.update.UpdateActivityVm.Companion.STATE_DOWNLOAD_COMPLETE
import com.njust.helper.update.UpdateActivityVm.Companion.STATE_DOWNLOAD_ERROR_FILE
import com.njust.helper.update.UpdateActivityVm.Companion.STATE_DOWNLOAD_ERROR_NET
import com.njust.helper.update.UpdateActivityVm.Companion.STATE_HAS_UPDATE
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class UpdateActivity : AppCompatActivity() {
    private var updateInfo: UpdateInfo? = null
    private lateinit var vm: UpdateActivityVm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityUpdateBinding>(this, R.layout.activity_update)
        vm = UpdateActivityVm()
        binding.vm = vm
        updateInfo = intent.getParcelableExtra(EXRTA_UPDATE_INFO)
        if (updateInfo != null) {
            vm.updateInfo = updateInfo
            vm.state = STATE_HAS_UPDATE
        }
    }

    fun onClick(view: View) {
        when (vm.state) {
            STATE_HAS_UPDATE, STATE_DOWNLOAD_ERROR_FILE, STATE_DOWNLOAD_ERROR_NET -> DownloadTask(updateInfo!!.url).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            STATE_DOWNLOAD_COMPLETE -> startInstall()
        }
    }

    internal fun startInstall() {
        val file: File
        try {
            file = getUpdateFile(updateInfo!!.versionCode)
            if (!file.exists()) throw Exception("更新文件已被删除")
        } catch (e: Exception) {
            vm.state = STATE_DOWNLOAD_ERROR_FILE
            return
        }

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(getUriFromFile(file), "application/vnd.android.package-archive")
        //新启Task。否则安装过程app退出会导致安装界面也退出，体验不好。
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK or
                Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(intent)
    }

    private fun getUriFromFile(file: File): Uri {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Maybe some old devices don't support non-file uri.
            Uri.fromFile(file)
        } else {
            FileProvider.getUriForFile(this, Constants.FILE_PROVIDER_AUTH, file)
        }
    }

    internal fun getUpdateFile(versionCode: Int): File {
        val file = File(externalCacheDir, "update/" + versionCode + "update.apk")

        file.parentFile.mkdirs()
        return file
    }

    private inner class DownloadTask internal constructor(private val url: String) : AsyncTask<Void, Int, Int>() {
        override fun onPreExecute() {
            vm.state = STATE_DOWNLOADING
        }

        override fun doInBackground(vararg params: Void): Int? {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connectTimeout = 3000
                connection.readTimeout = 10000
                connection.connect()
                val responseCode = connection.responseCode
                if (responseCode != 200) {
                    return JsonData.STATUS_NET_ERROR
                }
                val contentLength = connection.contentLength.toLong()
                val buffer = ByteArray(1024)
                BufferedInputStream(connection.inputStream).use { inputStream ->
                    val file = getUpdateFile(updateInfo!!.versionCode)

                    file.parentFile.mkdirs()
                    if (file.exists()) {
                        if (!file.isDirectory && file.length() == contentLength) {
                            publishProgress(100)
                            return JsonData.STATUS_SUCCESS
                        }
                        if (!file.delete()) return JsonData.STATUS_CAPTCHA_ERROR
                    }
                    BufferedOutputStream(FileOutputStream(file)).use { outputStream ->
                        var lengthWritten: Long = 0
                        var lastProgress: Long = 0
                        while (true) {
                            val i = inputStream.read(buffer, 0, 1024)
                            if (i < 0) {
                                break
                            }
                            outputStream.write(buffer, 0, i)
                            lengthWritten += i.toLong()
                            val progress = (lengthWritten * 100 / contentLength).toInt()
                            if (progress > lastProgress) {
                                lastProgress = progress.toLong()
                                publishProgress(progress)
                            }
                        }
                        return JsonData.STATUS_SUCCESS
                    }
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                return JsonData.STATUS_CAPTCHA_ERROR
            } catch (e: IOException) {
                e.printStackTrace()
                return JsonData.STATUS_NET_ERROR
            }
        }

        override fun onProgressUpdate(vararg values: Int?) {
            vm.progress = values[0]!!
        }

        override fun onPostExecute(integer: Int?) {
            val result = integer!!
            if (result == JsonData.STATUS_SUCCESS) {
                vm.state = STATE_DOWNLOAD_COMPLETE
                startInstall()
            } else if (result == JsonData.STATUS_NET_ERROR) {
                vm.state = STATE_DOWNLOAD_ERROR_NET
            } else if (result == JsonData.STATUS_CAPTCHA_ERROR) {
                vm.state = STATE_DOWNLOAD_ERROR_FILE
            }
        }
    }

    companion object {
        const val EXRTA_UPDATE_INFO = "updateInfo"

        @JvmStatic
        fun createIntent(context: Context, updateInfo: UpdateInfo): Intent {
            return Intent(context, UpdateActivity::class.java)
                    .putExtra(EXRTA_UPDATE_INFO, updateInfo)
        }
    }
}
