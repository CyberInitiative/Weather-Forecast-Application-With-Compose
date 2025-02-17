package com.example.weathercompose.ui.mapper

import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.ui.model.ForecastUIState

class ForecastUIStateMapper {
    fun mapToUIModel(city: CityDomainModel): ForecastUIState {
        with(city) {
            return ForecastUIState(
                cityName = name,
                dailyForecasts = city.forecasts.map { it.mapToDailyForecastItem() },
                hourlyForecasts = emptyList(),
                errorMessage = errorMessage,
            )
        }
    }
}