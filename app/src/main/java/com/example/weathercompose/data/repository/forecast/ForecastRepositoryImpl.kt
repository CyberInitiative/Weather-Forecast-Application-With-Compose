package com.example.weathercompose.data.repository.forecast

import com.example.weathercompose.data.api.ForecastAPI
import com.example.weathercompose.data.api.Result
import com.example.weathercompose.data.database.dao.ForecastDao
import com.example.weathercompose.data.database.entity.forecast.DailyForecastEntity
import com.example.weathercompose.data.mapper.mapToDailyForecastDomainModel
import com.example.weathercompose.data.mapper.mapToHourlyForecastDomainModel
import com.example.weathercompose.data.model.forecast.CompleteForecastResponse
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel
import com.example.weathercompose.domain.repository.ForecastRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime

class ForecastRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val forecastAPI: ForecastAPI,
    private val forecastDao: ForecastDao,
) : ForecastRepository {

    override suspend fun loadForecast(
        latitude: Double,
        longitude: Double,
        timeZone: String,
        dailyOptions: List<String>,
        hourlyOptions: List<String>,
        forecastDays: Int,
    ): Result<CompleteForecastResponse> {
        return try {
            val response = forecastAPI.getForecast(
                latitude = latitude,
                longitude = longitude,
                timeZone = timeZone,
                dailyOptions = dailyOptions,
                hourlyOptions = hourlyOptions,
                forecastDays = forecastDays,
            )

            Result.Success(data = response)
        } catch (e: Exception) {
            when (e) {
                is HttpException -> Result.Error(
                    error = "Failed with code ${e.code()}, message: ${e.message()}"
                )

                is IOException -> Result.Error(
                    error = "Failed with IOException; message: ${e.message}"
                )

                else -> Result.Error(error = "Failed with exception; message: ${e.message}")
            }
        }
    }

    override suspend fun findDailyForecastsWithHourlyForecastsByLocationId(
        locationId: Long
    ): List<DailyForecastDomainModel> {
        return withContext(dispatcher) {
            val dailyForecastWithHourlyForecast =
                forecastDao.findDailyForecastsWithHourlyForecastsByLocationId(
                    locationId = locationId
                )
            dailyForecastWithHourlyForecast.map { it.mapToDailyForecastDomainModel() }
        }
    }

    override suspend fun findDailyForecastByLocationIdAndDate(
        locationId: Long,
        date: LocalDate
    ): DailyForecastDomainModel? {
        return withContext(dispatcher){
            forecastDao.findDailyForecastByLocationIdAndDate(
                locationId = locationId,
                date = date.toString()
            )?.mapToHourlyForecastDomainModel()
        }
    }

    override suspend fun findHourlyForecastsByLocationId(
        locationId: Long,
        date: LocalDate,
        startHour: LocalTime,
        limit: Int
    ): List<HourlyForecastDomainModel> {
        return withContext(dispatcher) {
            forecastDao.findHourlyForecastsByLocationIdAndDateAndStartHour(
                locationId = locationId,
                date = date.toString(),
                startTime = startHour.toString(),
                limit = limit,
            ).map { it.mapToHourlyForecastDomainModel() }
        }
    }

    override suspend fun saveForecastsForLocation(
        locationId: Long,
        dailyForecastEntities: List<DailyForecastEntity>
    ) {
        withContext(dispatcher) {
            forecastDao.saveForecasts(
                locationId = locationId,
                dailyForecasts = dailyForecastEntities,
            )
        }
    }

    override suspend fun deleteForecastForLocation(locationId: Long) {
        withContext(dispatcher) {
            forecastDao.deleteDailyForecastsByLocationId(locationId = locationId)
        }
    }
}