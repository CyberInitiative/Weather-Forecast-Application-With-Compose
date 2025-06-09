package com.example.weathercompose.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weathercompose.domain.usecase.forecast.LoadForecastUseCase
import com.example.weathercompose.domain.usecase.location.FindAllLocationsUseCase
import com.example.weathercompose.domain.usecase.settings.SetLastTimeForecastUpdatedUseCase
import com.example.weathercompose.widget.WidgetUpdateManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class ForecastUpdatingWorker(
    appContext: Context,
    params: WorkerParameters,
    private val findAllLocationsUseCase: FindAllLocationsUseCase,
    private val loadForecastUseCase: LoadForecastUseCase,
    private val setLastTimeForecastUpdatedUseCase: SetLastTimeForecastUpdatedUseCase,
    private val widgetUpdateManager: WidgetUpdateManager
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val locations = findAllLocationsUseCase()

            coroutineScope {
                locations.map { location ->
                    async {
                        loadForecastUseCase(
                            LoadForecastUseCase.LoadingStrategy.FORCE_NETWORK,
                            locationDomainModel = location
                        )
                    }
                }.awaitAll()
                setLastTimeForecastUpdatedUseCase(System.currentTimeMillis())
                widgetUpdateManager.updateAll()
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}