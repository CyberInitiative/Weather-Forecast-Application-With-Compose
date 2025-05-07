package com.example.weathercompose.domain.usecase.forecast

import com.example.weathercompose.data.api.ForecastAPI
import com.example.weathercompose.data.api.Result
import com.example.weathercompose.data.mapper.DailyForecastMapper
import com.example.weathercompose.data.model.forecast.CompleteForecastResponse
import com.example.weathercompose.domain.model.forecast.ForecastLoadResult
import com.example.weathercompose.domain.model.location.LocationDomainModel
import com.example.weathercompose.domain.repository.ForecastRepository

class LoadForecastUseCase(
    private val forecastRepository: ForecastRepository,
    private val saveForecastUseCase: SaveForecastUseCase,
    private val dailyForecastMapper: DailyForecastMapper,
) {
    suspend operator fun invoke(
        locationDomainModel: LocationDomainModel
    ): ForecastLoadResult {
        val isTimestampExpired = locationDomainModel.isForecastLastUpdateTimestampExpired()
        return if (isTimestampExpired) {
            onTimestampExpired(locationDomainModel = locationDomainModel)
        } else {
            getCachedForecasts(locationDomainModel = locationDomainModel)
        }
    }

    private suspend fun onTimestampExpired(
        locationDomainModel: LocationDomainModel
    ): ForecastLoadResult {
        val completeForecast = forecastRepository.loadForecast(
            latitude = locationDomainModel.latitude,
            longitude = locationDomainModel.longitude,
            timeZone = locationDomainModel.timeZone,
            dailyOptions = ForecastAPI.dailyOptions,
            hourlyOptions = ForecastAPI.hourlyOptions,
            forecastDays = ForecastAPI.DEFAULT_FORECAST_DAYS,
        )

        return when (completeForecast) {
            is Result.Success -> {
                getForecastsFromNetwork(
                    locationDomainModel = locationDomainModel,
                    completeForecast = completeForecast.data,
                )
            }

            is Result.Error -> {
                getCachedForecasts(locationDomainModel = locationDomainModel)
            }
        }
    }

    private suspend fun getForecastsFromNetwork(
        locationDomainModel: LocationDomainModel,
        completeForecast: CompleteForecastResponse,
    ): ForecastLoadResult {
        val timestamp = saveForecastEntities(
            locationDomainModel = locationDomainModel,
            completeForecast = completeForecast,
        )

        val forecasts = dailyForecastMapper.mapResponseToDailyForecastDomainModels(
            completeForecastResponse = completeForecast,
        )

        return ForecastLoadResult(
            forecastLoadTimestamp = timestamp,
            forecastLoadResult = Result.Success(data = forecasts)
        )
    }

    private suspend fun saveForecastEntities(
        locationDomainModel: LocationDomainModel,
        completeForecast: CompleteForecastResponse,
    ): Long {
        val forecastEntities = dailyForecastMapper.mapResponseToDailyForecastEntities(
            locationId = locationDomainModel.id,
            completeForecastResponse = completeForecast,
        )

        return saveForecastUseCase.invoke(
            locationId = locationDomainModel.id,
            dailyForecastEntities = forecastEntities,
        )
    }

    private suspend fun getCachedForecasts(
        locationDomainModel: LocationDomainModel,
    ): ForecastLoadResult {
        val cachedData = forecastRepository.getForecastsByLocationID(
            locationId = locationDomainModel.id
        )

        return if (cachedData.isNotEmpty()) {
            ForecastLoadResult(
                forecastLoadResult = Result.Success(cachedData)
            )
        } else {
            ForecastLoadResult(
                forecastLoadResult = Result.Error(error = "No data available!")
            )
        }
    }
}