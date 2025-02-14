package com.example.weathercompose.ui.mapper

import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.ui.model.ForecastUIState

class ForecastUIStateMapper(
    private val forecastMapper: ForecastMapper,
) {

    fun mapToUIModel(city: CityDomainModel): ForecastUIState {
        with(city) {
            return ForecastUIState(
                cityName = name,
                forecasts = forecastMapper.mapForecast(forecasts = forecast),
                errorMessage = errorMessage,
            )
        }
    }
}