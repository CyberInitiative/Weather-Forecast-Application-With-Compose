package com.example.weathercompose.data.repository.forecast

import com.example.weathercompose.data.api.ForecastService
import com.example.weathercompose.data.model.forecast.FullForecast
import com.example.weathercompose.domain.repository.ForecastRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ForecastRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val forecastService: ForecastService,
) : ForecastRepository {

    override suspend fun load(
        latitude: Double,
        longitude: Double,
        timeZone: String,
        dailyOptions: List<String>,
        hourlyOptions: List<String>,
        forecastDays: Int,
    ): FullForecast {
        return withContext(dispatcher) {
            forecastService.getForecast(
                latitude = latitude,
                longitude = longitude,
                timeZone = timeZone,
                dailyOptions = dailyOptions,
                hourlyOptions = hourlyOptions,
                forecastDays = forecastDays,
            )
        }
    }

}