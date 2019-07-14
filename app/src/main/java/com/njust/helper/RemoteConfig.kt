package com.njust.helper

import com.google.firebase.remoteconfig.FirebaseRemoteConfig

object RemoteConfig {
    fun getTermId(): String = FirebaseRemoteConfig.getInstance().getString("termId")
}
