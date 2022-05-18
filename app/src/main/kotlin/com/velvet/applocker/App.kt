package com.velvet.applocker

import android.app.Application
import android.content.Intent
import androidx.core.content.ContextCompat
import com.velvet.applocker.service.AppLockerService
import com.velvet.applocker.di.appModule
import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@App)
            modules(appModule)
        }
        ContextCompat.startForegroundService(
            this, Intent(this, AppLockerService::class.java)
        )
    }
}