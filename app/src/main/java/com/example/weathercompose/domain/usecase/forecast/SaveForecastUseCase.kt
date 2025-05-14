package com.example.weathercompose.domain.usecase.forecast

import com.example.weathercompose.data.database.entity.forecast.DailyForecastEntity
import com.example.weathercompose.domain.repository.ForecastRepository

class SaveForecastUseCase(private val forecastRepository: ForecastRepository) {

    suspend operator fun invoke(
        locationId: Long,
        dailyForecastEntities: List<DailyForecastEntity>
    ) {
        forecastRepository.saveForecastsForLocation(
            locationId = locationId,
            dailyForecastEntities = dailyForecastEntities,
        )
    }

    companion object {
        private const val TAG = "SaveForecastUseCase"
    }
}