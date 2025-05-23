package com.example.weathercompose.data.mapper

import com.example.weathercompose.data.database.entity.combined.LocationWithDailyForecasts
import com.example.weathercompose.data.database.entity.location.LocationEntity
import com.example.weathercompose.data.model.location.LocationSearchResponse
import com.example.weathercompose.domain.model.forecast.DataState
import com.example.weathercompose.domain.model.location.LocationDomainModel

fun LocationSearchResponse.mapToLocationEntity(): LocationEntity {
    with(this) {
        return LocationEntity(
            locationId = id ?: throw IllegalStateException("Mapping failure; id can't be null!"),
            // For some reason Sumatra island has null for latitude
            // (actual latitude value for Sumatra is close or equal to 0).
            latitude = latitude ?: 0.0,
            longitude = longitude ?: 0.0,
            name = name ?: throw IllegalStateException("Mapping failure; name can't be null!"),
            firstAdministrativeLevel = firstAdministrativeLevel.orEmpty(),
            secondAdministrativeLevel = secondAdministrativeLevel.orEmpty(),
            thirdAdministrativeLevel = thirdAdministrativeLevel.orEmpty(),
            fourthAdministrativeLevel = fourthAdministrativeLevel.orEmpty(),
            country = country.orEmpty(),
            timeZone = timezone ?: "auto",
        )
    }
}

fun LocationEntity.mapToLocationDomainModel(): LocationDomainModel {
    with(this) {
        return LocationDomainModel(
            latitude = latitude,
            longitude = longitude,
            id = locationId,
            name = name,
            firstAdministrativeLevel = firstAdministrativeLevel,
            secondAdministrativeLevel = secondAdministrativeLevel,
            thirdAdministrativeLevel = thirdAdministrativeLevel,
            fourthAdministrativeLevel = fourthAdministrativeLevel,
            country = country,
            timeZone = timeZone,
            isHomeLocation = isHomeLocation,
        )
    }
}

fun LocationWithDailyForecasts.mapToLocationDomainModel(): LocationDomainModel {
    with(this) {
        val forecasts = dailyForecasts.map { it.mapToDailyForecastDomainModel() }
        val forecastDataState = when {
            forecasts.isNotEmpty() -> DataState.Ready(data = forecasts)
            forecasts.isEmpty() -> DataState.NoData

            else -> DataState.Error(error = "Failed to load forecast data.")
        }

        return LocationDomainModel(
            latitude = location.latitude,
            longitude = location.longitude,
            id = location.locationId,
            name = location.name,
            firstAdministrativeLevel = location.firstAdministrativeLevel,
            secondAdministrativeLevel = location.secondAdministrativeLevel,
            thirdAdministrativeLevel = location.thirdAdministrativeLevel,
            fourthAdministrativeLevel = location.fourthAdministrativeLevel,
            country = location.country,
            timeZone = location.timeZone,
            forecastDataState = forecastDataState,
            isHomeLocation = location.isHomeLocation,
        )
    }
}