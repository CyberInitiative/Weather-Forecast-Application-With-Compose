package com.example.weathercompose.domain.usecase.forecast

import com.example.weathercompose.data.api.ForecastAPI
import com.example.weathercompose.data.api.Result
import com.example.weathercompose.data.mapper.DailyForecastMapper
import com.example.weathercompose.data.model.forecast.CompleteForecastResponse
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.location.LocationDomainModel
import com.example.weathercompose.domain.repository.ForecastRepository

class LoadForecastUseCase(
    private val forecastRepository: ForecastRepository,
    private val saveForecastUseCase: SaveForecastUseCase,
    private val dailyForecastMapper: DailyForecastMapper,
) {
    suspend operator fun invoke(
        forceLoadFromNetwork: Boolean,
        locationDomainModel: LocationDomainModel
    ): Result<List<DailyForecastDomainModel>> {
        return if (forceLoadFromNetwork) {
            getForecastsFromNetwork(
                forceLoadFromNetwork = true,
                locationDomainModel = locationDomainModel
            )
        } else {
            getCachedForecasts(locationDomainModel = locationDomainModel)
        }
    }

    private suspend fun getForecastsFromNetwork(
        forceLoadFromNetwork: Boolean,
        locationDomainModel: LocationDomainModel
    ): Result<List<DailyForecastDomainModel>> {
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
                if (!forceLoadFromNetwork) {
                    getCachedForecasts(locationDomainModel = locationDomainModel)
                } else {
                    Result.Error(error = "Failed to load forecast!")
                }
            }
        }
    }

    private suspend fun getForecastsFromNetwork(
        locationDomainModel: LocationDomainModel,
        completeForecast: CompleteForecastResponse,
    ): Result<List<DailyForecastDomainModel>> {
        saveForecastEntities(
            locationDomainModel = locationDomainModel,
            completeForecast = completeForecast,
        )

        val forecasts = dailyForecastMapper.mapResponseToDailyForecastDomainModels(
            completeForecastResponse = completeForecast,
        )

        return Result.Success(data = forecasts)
    }

    private suspend fun saveForecastEntities(
        locationDomainModel: LocationDomainModel,
        completeForecast: CompleteForecastResponse,
    ) {
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
    ): Result<List<DailyForecastDomainModel>> {
        val cachedData = forecastRepository.getForecastsByLocationID(
            locationId = locationDomainModel.id
        )

        if (cachedData.isNotEmpty()) {
            return Result.Success(cachedData)
        }

        val result = getForecastsFromNetwork(
            forceLoadFromNetwork = false,
            locationDomainModel = locationDomainModel
        )

        return if (result is Result.Success) {
            result
        } else {
            Result.Error("No data available!")
        }
    }
}