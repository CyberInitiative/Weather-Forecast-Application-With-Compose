package com.example.weathercompose.domain.repository

import com.example.weathercompose.data.model.forecast.FullForecast
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel

interface ForecastRepository {

    suspend fun loadForecastForLocation(
        latitude: Double,
        longitude: Double,
        timeZone: String,
        dailyOptions: List<String>,
        hourlyOptions: List<String>,
        forecastDays: Int,
    ): FullForecast

    suspend fun saveForecastForLocation(
        locationId: Long,
        dailyForecasts: List<DailyForecastDomainModel>,
    )

    suspend fun deleteForecastForLocation(locationId: Long)
}