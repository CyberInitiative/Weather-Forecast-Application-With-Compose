package com.example.weathercompose.domain.usecase.forecast

import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.repository.ForecastRepository

class SaveForecastsUseCase(
    private val forecastRepository: ForecastRepository,
) {

    suspend operator fun invoke(
        cityId: Long,
        dailyForecasts: List<DailyForecastDomainModel>
    ) {
        forecastRepository.saveForecasts(
            cityId = cityId,
            dailyForecasts = dailyForecasts,
        )
    }
}