package com.example.weathercompose.domain.repository

import com.example.weathercompose.data.api.Result
import com.example.weathercompose.data.database.entity.forecast.DailyForecastEntity
import com.example.weathercompose.data.model.forecast.CompleteForecastResponse
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel
import java.time.LocalDate
import java.time.LocalTime

interface ForecastRepository {

    suspend fun loadForecast(
        latitude: Double,
        longitude: Double,
        timeZone: String,
        dailyOptions: List<String>,
        hourlyOptions: List<String>,
        forecastDays: Int,
    ): Result<CompleteForecastResponse>

    suspend fun findDailyForecastsWithHourlyForecastsByLocationId(
        locationId: Long,
    ): List<DailyForecastDomainModel>

    suspend fun findDailyForecastByLocationIdAndDate(
        locationId: Long,
        date: LocalDate,
    ): DailyForecastDomainModel?

    suspend fun findHourlyForecastsByLocationId(
        locationId: Long,
        date: LocalDate,
        startHour: LocalTime,
        limit: Int,
    ): List<HourlyForecastDomainModel>

    suspend fun saveForecastsForLocation(
        locationId: Long,
        dailyForecastEntities: List<DailyForecastEntity>,
    )

    suspend fun deleteForecastForLocation(locationId: Long)
}