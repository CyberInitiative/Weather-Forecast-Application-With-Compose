package com.example.weathercompose

import android.app.Application
import com.example.weathercompose.data.dataModule
import com.example.weathercompose.domain.domainModule
import com.example.weathercompose.ui.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WeatherApplication : Application() {
    private val koinModules = dataModule + domainModule + uiModule

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@WeatherApplication)
            modules(koinModules)
        }
    }
}