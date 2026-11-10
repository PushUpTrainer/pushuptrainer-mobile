package com.example.pushuptrainer_mobile

import android.app.Application
import com.example.pushuptrainer_mobile.di.androidAppModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PushUpTrainerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PushUpTrainerApplication)
            modules(androidAppModules)
        }
    }
}
