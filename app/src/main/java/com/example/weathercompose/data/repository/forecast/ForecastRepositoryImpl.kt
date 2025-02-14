package com.example.weathercompose.data.repository.forecast

import com.example.weathercompose.data.api.ForecastService
import com.example.weathercompose.data.database.dao.CityDao
import com.example.weathercompose.data.database.entity.forecast.DailyForecastEntity
import com.example.weathercompose.data.model.forecast.FullForecast
import com.example.weathercompose.domain.mapper.mapToEntity
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.repository.ForecastRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ForecastRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val forecastService: ForecastService,
    private val cityDao: CityDao,
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

    override suspend fun saveForecasts(
        cityId: Long,
        dailyForecasts: List<DailyForecastDomainModel>
    ) {
        withContext(dispatcher) {
            val mappedDailyForecasts = mutableListOf<DailyForecastEntity>()
            for (dailyForecast in dailyForecasts) {
                val mappedDailyForecast = dailyForecast.mapToEntity(cityId = cityId)
                val mappedHourlyForecasts = dailyForecast.hourlyForecasts.map { it.mapToEntity() }
                mappedDailyForecast.hourlyForecasts = mappedHourlyForecasts
                mappedDailyForecasts.add(mappedDailyForecast)
            }
            cityDao.saveForecasts(dailyForecasts = mappedDailyForecasts)
        }
    }

    override suspend fun deleteForecasts(cityId: Long) {
        withContext(dispatcher) {
            cityDao.deleteDailyForecastsByCityId(cityId = cityId)
        }
    }

}