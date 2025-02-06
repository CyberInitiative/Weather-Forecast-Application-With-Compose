package com.example.weathercompose.ui.mapper

import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.ui.model.CityUIState

class CityUIModelMapper(
    private val forecastMapper: ForecastMapper,
) {

    fun mapToUIModel(city: CityDomainModel): CityUIState {
        with(city) {
            return CityUIState(
                id = id.toString(),
                name = name,
                country = country,
                firstAdministrativeLevel = firstAdministrativeLevel,
                secondAdministrativeLevel = secondAdministrativeLevel,
                thirdAdministrativeLevel = thirdAdministrativeLevel,
                fourthAdministrativeLevel = fourthAdministrativeLevel,
                forecasts = forecastMapper.mapForecast(forecasts = forecast),
            )
        }
    }
}