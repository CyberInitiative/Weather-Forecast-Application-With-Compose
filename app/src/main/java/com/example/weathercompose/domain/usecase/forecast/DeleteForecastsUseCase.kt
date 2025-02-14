package com.example.weathercompose.domain.usecase.forecast

import com.example.weathercompose.domain.repository.ForecastRepository

class DeleteForecastsUseCase(private val forecastRepository: ForecastRepository) {
    suspend operator fun invoke(cityId: Long) {
        forecastRepository.deleteForecasts(cityId = cityId)
    }
}