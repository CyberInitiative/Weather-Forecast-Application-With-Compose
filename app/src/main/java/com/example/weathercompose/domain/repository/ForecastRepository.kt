package com.example.weathercompose.domain.repository

import com.example.weathercompose.data.api.Result
import com.example.weathercompose.data.database.entity.forecast.DailyForecastEntity
import com.example.weathercompose.data.model.forecast.CompleteForecastResponse
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel

interface ForecastRepository {

    suspend fun getForecastsByLocationID(
        locationId: Long,
    ): List<DailyForecastDomainModel>

    suspend fun loadForecast(
        latitude: Double,
        longitude: Double,
        timeZone: String,
        dailyOptions: List<String>,
        hourlyOptions: List<String>,
        forecastDays: Int,
    ): Result<CompleteForecastResponse>

    suspend fun saveForecastsForLocation(
        locationId: Long,
        dailyForecastEntities: List<DailyForecastEntity>,
    )

    suspend fun deleteForecastForLocation(locationId: Long)
}