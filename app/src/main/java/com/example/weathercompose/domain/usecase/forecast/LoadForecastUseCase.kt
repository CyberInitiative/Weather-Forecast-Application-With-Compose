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
        loadingStrategy: LoadingStrategy,
        locationDomainModel: LocationDomainModel
    ): Result<List<DailyForecastDomainModel>> {
        return when (loadingStrategy) {
            LoadingStrategy.FORCE_NETWORK -> getForecastsFromNetwork(
                locationDomainModel = locationDomainModel,
            )

            LoadingStrategy.FORCE_CACHE -> getCachedForecasts(
                isForcedToUseCacheOnly = true,
                locationDomainModel = locationDomainModel,
            )

            LoadingStrategy.DEFAULT -> getCachedForecasts(
                isForcedToUseCacheOnly = false,
                locationDomainModel = locationDomainModel,
            )
        }
    }

    private suspend fun getForecastsFromNetwork(
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
                saveAndMapForecasts(
                    locationDomainModel = locationDomainModel,
                    completeForecast = completeForecast.data,
                )
            }

            is Result.Error -> {
                getCachedForecasts(
                    isForcedToUseCacheOnly = true,
                    locationDomainModel = locationDomainModel,
                )
            }
        }
    }

    private suspend fun saveAndMapForecasts(
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
        isForcedToUseCacheOnly: Boolean,
        locationDomainModel: LocationDomainModel,
    ): Result<List<DailyForecastDomainModel>> {
        val cachedData = forecastRepository.getForecastsByLocationID(
            locationId = locationDomainModel.id
        )

        if (cachedData.isNotEmpty()) {
            return Result.Success(data = cachedData)
        }

        return if (isForcedToUseCacheOnly) {
            Result.Error(error = "Failed to load forecast!")
        } else {
            getForecastsFromNetwork(locationDomainModel)
        }
    }

    enum class LoadingStrategy {
        /**
         * If the FORCE_NETWORK strategy is selected, the forecast will initially be retrieved
         * from the network; if that attempt fails, it will fall back to retrieving it from
         * the cache.
         */
        FORCE_NETWORK,

        /**
         * If the FORCE_CACHE strategy is selected, upon failure to load the forecast
         * from the cache, no attempt will be made to retrieve it from the network,
         * and Result.Error will be returned.
         */
        FORCE_CACHE,

        /**
         * If the DEFAULT strategy is selected, it will first attempt to retrieve the forecast
         * from the cache; if that fails, it will then attempt to retrieve it from the network.
         */
        DEFAULT,
    }
}