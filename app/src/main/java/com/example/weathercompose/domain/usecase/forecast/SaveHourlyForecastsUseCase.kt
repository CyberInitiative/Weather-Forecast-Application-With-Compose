package com.example.weathercompose.domain.usecase.forecast

import com.example.weathercompose.domain.repository.ForecastRepository

class SaveHourlyForecastsUseCase(private val forecastRepository: ForecastRepository) {
//    suspend operator fun invoke(
//        hourlyForecastDomainModel: HourlyForecastDomainModel,
//        dailyForecastId: Long
//    ) {
//        forecastRepository.saveHourlyForecasts(
//            hourlyForecastDomainModel = hourlyForecastDomainModel,
//            dailyForecastId = dailyForecastId,
//        )
//    }
}