package com.example.weathercompose.domain.usecase.forecast

import com.example.weathercompose.domain.repository.ForecastRepository

class SaveDailyForecastsUseCase(private val forecastRepository: ForecastRepository) {
//    suspend operator fun invoke(dailyForecastDomainModel: DailyForecastDomainModel, cityId: Long): Long {
//        return forecastRepository.saveDailyForecasts(
//            dailyForecastDomainModel = dailyForecastDomainModel,
//            cityId = cityId,
//        )
//    }
}