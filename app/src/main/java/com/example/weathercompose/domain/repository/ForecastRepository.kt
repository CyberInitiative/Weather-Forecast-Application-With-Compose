package com.example.weathercompose.domain.repository

import com.example.weathercompose.data.model.forecast.FullForecast
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel

interface ForecastRepository {

    suspend fun load(
        latitude: Double,
        longitude: Double,
        timeZone: String,
        dailyOptions: List<String>,
        hourlyOptions: List<String>,
        forecastDays: Int,
    ): FullForecast

    suspend fun saveForecasts(
        cityId: Long,
        dailyForecasts: List<DailyForecastDomainModel>,
    )

    suspend fun deleteForecasts(cityId: Long)
}