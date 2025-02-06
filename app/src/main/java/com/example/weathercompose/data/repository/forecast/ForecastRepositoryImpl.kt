package com.example.weathercompose.data.repository.forecast

import com.example.weathercompose.data.api.ForecastService
import com.example.weathercompose.data.database.dao.CityDao
import com.example.weathercompose.data.model.forecast.FullForecast
import com.example.weathercompose.domain.mapper.mapToEntity
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel
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

    override suspend fun saveDailyForecasts(
        dailyForecastDomainModel: DailyForecastDomainModel,
        cityId: Long,
    ): Long {
        return withContext(dispatcher) {
            cityDao.insert(
                dailyForecasts = dailyForecastDomainModel.mapToEntity(cityId = cityId)
            )
        }
    }

    override suspend fun saveHourlyForecasts(
        hourlyForecastDomainModel: HourlyForecastDomainModel,
        dailyForecastId: Long,
    ): Long {
        return withContext(dispatcher) {
            cityDao.insert(
                hourlyForecasts = hourlyForecastDomainModel.mapToEntity(
                    dailyForecastId = dailyForecastId
                )
            )
        }
    }

}