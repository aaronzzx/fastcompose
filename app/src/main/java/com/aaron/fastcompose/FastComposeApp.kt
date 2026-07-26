package com.aaron.fastcompose

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

lateinit var appContext: Context
    private set

/**
 * @author aaronzzxup@gmail.com
 * @since 2022/12/28
 */
@HiltAndroidApp
class FastComposeApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}
