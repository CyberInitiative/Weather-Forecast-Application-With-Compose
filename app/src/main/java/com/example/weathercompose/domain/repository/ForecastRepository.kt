package com.example.weathercompose.domain.repository

import com.example.weathercompose.data.model.forecast.FullForecast

interface ForecastRepository {
    suspend fun load(
        latitude: Double,
        longitude: Double,
        timeZone: String,
        dailyOptions: List<String>,
        hourlyOptions: List<String>,
        forecastDays: Int,
    ): FullForecast
}