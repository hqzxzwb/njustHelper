package com.njust.helper.update

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.njust.helper.BR
import com.njust.helper.model.UpdateInfo

class UpdateActivityVm : BaseObservable() {
    var state: Int = STATE_PENDING
        set(value) {
            field = value
            notifyPropertyChanged(BR.state)
            notifyPropertyChanged(BR.buttonText)
            notifyPropertyChanged(BR.messageText)
        }
        @Bindable get
    var progress: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.progress)
        }
        @Bindable get
    var updateInfo: UpdateInfo? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.updateInfo)
            notifyPropertyChanged(BR.messageText)
        }
        @Bindable get
    var buttonText: String
        set(value) {
            throw UnsupportedOperationException()
        }
        @Bindable get() {
            return when (state) {
                STATE_PENDING, STATE_NO_UPDATE, STATE_CHECK_FAILED -> "检查更新"
                STATE_CHECKING -> "正在检测更新……"
                STATE_HAS_UPDATE, STATE_DOWNLOAD_ERROR_FILE, STATE_DOWNLOAD_ERROR_NET -> "立即下载"
                STATE_DOWNLOADING -> "正在下载……"
                STATE_DOWNLOAD_COMPLETE -> "立即安装"
                else -> ""
            }
        }
    var messageText: String
        set(value) {
            throw UnsupportedOperationException()
        }
        @Bindable get() {
            return when (state) {
                STATE_HAS_UPDATE -> updateInfo.toString()
                STATE_NO_UPDATE -> "您已经在使用最新版本"
                STATE_CHECK_FAILED -> "检测失败，请检查网络连接后重试"
                STATE_DOWNLOADING -> updateInfo.toString()
                STATE_DOWNLOAD_COMPLETE -> "下载完成，点击按钮立即安装"
                STATE_DOWNLOAD_ERROR_FILE -> "文件存储失败，请检查您的SD卡后重试"
                STATE_DOWNLOAD_ERROR_NET -> "下载失败，请检查网络连接后重试"
                else -> ""
            }
        }

    companion object {
        const val STATE_PENDING = 0
        const val STATE_CHECKING = 1
        const val STATE_HAS_UPDATE = 2
        const val STATE_NO_UPDATE = 3
        const val STATE_CHECK_FAILED = 4
        const val STATE_DOWNLOADING = 5
        const val STATE_DOWNLOAD_COMPLETE = 6
        const val STATE_DOWNLOAD_ERROR_FILE = 7
        const val STATE_DOWNLOAD_ERROR_NET = 8
    }
}
