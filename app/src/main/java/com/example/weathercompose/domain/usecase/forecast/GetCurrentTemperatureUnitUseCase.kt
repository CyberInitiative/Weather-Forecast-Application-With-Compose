package com.example.weathercompose.domain.usecase.forecast

import com.example.weathercompose.data.model.forecast.TemperatureUnit
import com.example.weathercompose.domain.repository.ForecastRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentTemperatureUnitUseCase(
    private val forecastRepository: ForecastRepository
) {
    fun execute(): Flow<TemperatureUnit> {
        return forecastRepository.getCurrentTemperatureUnit()
    }
}