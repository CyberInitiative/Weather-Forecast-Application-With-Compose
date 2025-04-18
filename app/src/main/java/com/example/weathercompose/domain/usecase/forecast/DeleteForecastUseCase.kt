package com.example.weathercompose.domain.usecase.forecast

import com.example.weathercompose.domain.repository.ForecastRepository

class DeleteForecastUseCase(
    private val forecastRepository: ForecastRepository,
) {

    suspend operator fun invoke(locationId: Long) {
        forecastRepository.deleteForecastForLocation(locationId = locationId)
    }
}