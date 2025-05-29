package com.example.weathercompose

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.weathercompose.data.dataModule
import com.example.weathercompose.domain.domainModule
import com.example.weathercompose.domain.usecase.settings.GetForecastUpdateFrequencyUseCase
import com.example.weathercompose.ui.uiModule
import com.example.weathercompose.utils.utilsModule
import com.example.weathercompose.workmanager.ForecastUpdatingWorker
import com.example.weathercompose.workmanager.workerModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform.getKoin
import java.util.concurrent.TimeUnit

class WeatherApplication : Application() {
    private val koinModules = dataModule + domainModule + uiModule + utilsModule + workerModule
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@WeatherApplication)
            workManagerFactory()
            modules(koinModules)
        }

        val getForecastUpdateFrequencyUseCase: GetForecastUpdateFrequencyUseCase = getKoin().get()

        applicationScope.launch {
            getForecastUpdateFrequencyUseCase().collect { frequencyInHours ->
                setForecastUpdatingWorker(frequencyInHours.value)
            }
        }
    }

    private fun setForecastUpdatingWorker(frequencyInHours: Int) {
        val workRequest = PeriodicWorkRequestBuilder<ForecastUpdatingWorker>(
            frequencyInHours.toLong(), TimeUnit.HOURS
            //16L, TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            FORECAST_UPDATING_WORK,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    companion object {
        const val FORECAST_UPDATING_WORK = "forecastUpdatingWork"
    }
}