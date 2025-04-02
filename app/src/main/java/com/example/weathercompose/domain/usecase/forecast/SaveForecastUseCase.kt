package com.example.weathercompose.domain.usecase.forecast

import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.repository.ForecastRepository

class SaveForecastUseCase(
    private val forecastRepository: ForecastRepository,
) {

    suspend operator fun invoke(
        cityId: Long,
        dailyForecast: List<DailyForecastDomainModel>
    ) {
        forecastRepository.saveForecastForCity(
            cityId = cityId,
            dailyForecasts = dailyForecast,
        )
    }
}