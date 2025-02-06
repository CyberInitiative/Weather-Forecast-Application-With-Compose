package com.example.weathercompose.domain.repository

import com.example.weathercompose.data.model.forecast.FullForecast
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel

interface ForecastRepository {

    suspend fun load(
        latitude: Double,
        longitude: Double,
        timeZone: String,
        dailyOptions: List<String>,
        hourlyOptions: List<String>,
        forecastDays: Int,
    ): FullForecast

    suspend fun saveDailyForecasts(
        dailyForecastDomainModel: DailyForecastDomainModel,
        cityId: Long,
    ): Long

    suspend fun saveHourlyForecasts(
        hourlyForecastDomainModel: HourlyForecastDomainModel,
        dailyForecastId: Long,
    ): Long
}