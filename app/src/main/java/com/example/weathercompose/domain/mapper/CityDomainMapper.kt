package com.example.weathercompose.domain.mapper

import com.example.weathercompose.data.database.entity.city.CityEntity
import com.example.weathercompose.data.database.entity.combined.CityWithDailyForecasts
import com.example.weathercompose.data.model.city.City
import com.example.weathercompose.domain.model.city.CityDomainModel

fun CityWithDailyForecasts.mapToCityDomainModel(): CityDomainModel {
    with(this) {
        return CityDomainModel(
            latitude = city.latitude,
            longitude = city.longitude,
            id = city.cityId,
            name = city.name,
            firstAdministrativeLevel = city.firstAdministrativeLevel,
            secondAdministrativeLevel = city.secondAdministrativeLevel,
            thirdAdministrativeLevel = city.thirdAdministrativeLevel,
            fourthAdministrativeLevel = city.fourthAdministrativeLevel,
            country = city.country,
            timeZone = city.timezone,
            forecast = dailyForecasts.map { it.mapToDailyForecastDomainModel() },
        )
    }
}

fun City.mapToCityDomainModel(): CityDomainModel {
    with(this) {
        return CityDomainModel(
            // For some reason Sumatra island has null for latitude.
            // Actual latitude value for Sumatra is close or equal to 0.
            latitude = latitude ?: 0.0,
            longitude = longitude ?: 0.0,
            id = id ?: throw IllegalStateException("Mapping failure; id can't be null!"),
            name = name ?: throw IllegalStateException("Mapping failure; name can't be null!"),
            firstAdministrativeLevel = firstAdministrativeLevel.orEmpty(),
            secondAdministrativeLevel = secondAdministrativeLevel.orEmpty(),
            thirdAdministrativeLevel = thirdAdministrativeLevel.orEmpty(),
            fourthAdministrativeLevel = fourthAdministrativeLevel.orEmpty(),
            country = country.orEmpty(),
            timeZone = timezone ?: "auto",
            forecast = emptyList(),
        )
    }
}

fun CityEntity.mapToCityDomainModel(): CityDomainModel {
    with(this) {
        return CityDomainModel(
            latitude = latitude,
            longitude = longitude,
            id = cityId,
            name = name,
            firstAdministrativeLevel = firstAdministrativeLevel,
            secondAdministrativeLevel = secondAdministrativeLevel,
            thirdAdministrativeLevel = thirdAdministrativeLevel,
            fourthAdministrativeLevel = fourthAdministrativeLevel,
            country = country,
            timeZone = timezone,
            forecast = emptyList(),
        )
    }
}

fun CityDomainModel.mapToEntity(): CityEntity {
    with(this) {
        return CityEntity(
            cityId = id,
            latitude = latitude,
            longitude = longitude,
            name = name,
            firstAdministrativeLevel = firstAdministrativeLevel,
            secondAdministrativeLevel = secondAdministrativeLevel,
            thirdAdministrativeLevel = thirdAdministrativeLevel,
            fourthAdministrativeLevel = fourthAdministrativeLevel,
            country = country,
            timezone = timeZone,
        )
    }
}
