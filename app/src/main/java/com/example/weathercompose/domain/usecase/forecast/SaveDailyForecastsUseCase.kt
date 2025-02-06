package com.example.weathercompose.domain.usecase.forecast

import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.repository.ForecastRepository

class SaveDailyForecastsUseCase(private val forecastRepository: ForecastRepository) {
    suspend fun execute(dailyForecastDomainModel: DailyForecastDomainModel, cityId: Long): Long {
        return forecastRepository.saveDailyForecasts(
            dailyForecastDomainModel = dailyForecastDomainModel,
            cityId = cityId,
        )
    }
}