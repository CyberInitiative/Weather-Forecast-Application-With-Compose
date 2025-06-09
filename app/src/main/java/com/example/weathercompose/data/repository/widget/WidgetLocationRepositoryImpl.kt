package com.example.weathercompose.data.repository.widget

import com.example.weathercompose.data.database.dao.WidgetDao
import com.example.weathercompose.data.model.widget.WidgetHourlyForecast
import com.example.weathercompose.data.model.widget.WidgetLocationWithForecasts
import com.example.weathercompose.domain.repository.WidgetLocationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class WidgetLocationRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val widgetDao: WidgetDao,
) : WidgetLocationRepository {

    override suspend fun findAllLocationsWithForecasts(): List<WidgetLocationWithForecasts> {
        return withContext(context = dispatcher) {
            widgetDao.findAllWidgetLocationsWithForecasts()
        }
    }

    override suspend fun findWidgetLocationsWithForecastsById(
        locationId: Long
    ): WidgetLocationWithForecasts? {
        return withContext(context = dispatcher) {
            widgetDao.findWidgetLocationsWithForecastsById(locationId = locationId)
        }
    }

    override suspend fun findHourlyForecastsByLocationIdAndDateAndStartHour(
        locationId: Long,
        date: String,
        startTime: String,
        limit: Int
    ): List<WidgetHourlyForecast> {
        return withContext(context = dispatcher) {
            widgetDao.findHourlyForecastsByLocationIdAndDateAndStartHour(
                locationId = locationId,
                date = date,
                startTime = startTime,
                limit = limit,
            )
        }
    }
}