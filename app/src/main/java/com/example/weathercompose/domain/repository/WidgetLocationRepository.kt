package com.example.weathercompose.domain.repository

import com.example.weathercompose.data.model.widget.WidgetHourlyForecast
import com.example.weathercompose.data.model.widget.WidgetLocationWithForecasts

interface WidgetLocationRepository {

    suspend fun findAllLocationsWithForecasts(): List<WidgetLocationWithForecasts>

    suspend fun findWidgetLocationsWithForecastsById(
        locationId: Long
    ): WidgetLocationWithForecasts?

    suspend fun findHourlyForecastsByLocationIdAndDateAndStartHour(
        locationId: Long,
        date: String,
        startTime: String,
        limit: Int
    ): List<WidgetHourlyForecast>
}