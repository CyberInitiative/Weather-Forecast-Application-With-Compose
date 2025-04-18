package com.example.weathercompose.data.repository.forecast

import com.example.weathercompose.data.api.ForecastAPI
import com.example.weathercompose.data.database.dao.LocationDao
import com.example.weathercompose.data.database.entity.forecast.DailyForecastEntity
import com.example.weathercompose.data.mapper.mapToDailyForecastEntity
import com.example.weathercompose.data.mapper.mapToHourlyForecastEntity
import com.example.weathercompose.data.model.forecast.FullForecast
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.repository.ForecastRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ForecastRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val forecastAPI: ForecastAPI,
    private val locationDao: LocationDao,
) : ForecastRepository {

    override suspend fun loadForecastForLocation(
        latitude: Double,
        longitude: Double,
        timeZone: String,
        dailyOptions: List<String>,
        hourlyOptions: List<String>,
        forecastDays: Int,
    ): FullForecast {
        return withContext(dispatcher) {
            forecastAPI.getForecast(
                latitude = latitude,
                longitude = longitude,
                timeZone = timeZone,
                dailyOptions = dailyOptions,
                hourlyOptions = hourlyOptions,
                forecastDays = forecastDays,
            )
        }
    }

    override suspend fun saveForecastForLocation(
        locationId: Long,
        dailyForecasts: List<DailyForecastDomainModel>
    ) {
        withContext(dispatcher) {
            val mappedDailyForecasts = mutableListOf<DailyForecastEntity>()
            for (dailyForecast in dailyForecasts) {
                val mappedDailyForecast = dailyForecast.mapToDailyForecastEntity(locationId = locationId)
                val mappedHourlyForecasts =
                    dailyForecast.hourlyForecasts.map { it.mapToHourlyForecastEntity() }
                mappedDailyForecast.hourlyForecasts = mappedHourlyForecasts
                mappedDailyForecasts.add(mappedDailyForecast)
            }
            locationDao.saveForecasts(dailyForecasts = mappedDailyForecasts)
        }
    }

    override suspend fun deleteForecastForLocation(locationId: Long) {
        withContext(dispatcher) {
            locationDao.deleteDailyForecastsByLocationId(locationId = locationId)
        }
    }

}