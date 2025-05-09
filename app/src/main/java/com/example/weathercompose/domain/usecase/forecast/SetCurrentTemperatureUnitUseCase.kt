package com.example.weathercompose.domain.usecase.forecast

import com.example.weathercompose.data.model.forecast.TemperatureUnit
import com.example.weathercompose.domain.repository.ForecastRepository

class SetCurrentTemperatureUnitUseCase(
    private val forecastRepository: ForecastRepository
) {
    suspend operator fun invoke(temperatureUnit: TemperatureUnit) {
        forecastRepository.setCurrentTemperatureUnit(temperatureUnit = temperatureUnit)
    }
}