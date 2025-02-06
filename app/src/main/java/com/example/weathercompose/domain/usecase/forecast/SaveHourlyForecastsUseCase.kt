package com.example.weathercompose.domain.usecase.forecast

import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel
import com.example.weathercompose.domain.repository.ForecastRepository

class SaveHourlyForecastsUseCase(private val forecastRepository: ForecastRepository) {
    suspend fun execute(
        hourlyForecastDomainModel: HourlyForecastDomainModel,
        dailyForecastId: Long
    ) {
        forecastRepository.saveHourlyForecasts(
            hourlyForecastDomainModel = hourlyForecastDomainModel,
            dailyForecastId = dailyForecastId,
        )
    }
}