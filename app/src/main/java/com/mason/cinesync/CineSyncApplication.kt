package com.mason.cinesync

import android.app.Application
import com.mason.cinesync.token.TokenManager

class CineSyncApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 初始化 TokenManager
        TokenManager.init(this)
    }
}

