package com.example.weathercompose.domain.mapper

import com.example.weathercompose.data.database.entity.CityEntity
import com.example.weathercompose.data.model.city.City
import com.example.weathercompose.domain.model.city.CityDomainModel

fun City.mapCityToDomainModel(): CityDomainModel {
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
            timezone = timezone ?: "auto",
            forecast = null
        )
    }
}

fun CityEntity.mapCityToDomainModel(): CityDomainModel {
    with(this) {
        return CityDomainModel(
            latitude = latitude,
            longitude = longitude,
            id = id,
            name = name,
            firstAdministrativeLevel = firstAdministrativeLevel,
            secondAdministrativeLevel = secondAdministrativeLevel,
            thirdAdministrativeLevel = thirdAdministrativeLevel,
            fourthAdministrativeLevel = fourthAdministrativeLevel,
            country = country,
            timezone = timezone,
            forecast = null,
        )
    }
}

fun CityDomainModel.mapToEntity(): CityEntity {
    with(this) {
        return CityEntity(
            id = id,
            latitude = latitude,
            longitude = longitude,
            name = name,
            firstAdministrativeLevel = firstAdministrativeLevel,
            secondAdministrativeLevel = secondAdministrativeLevel,
            thirdAdministrativeLevel = thirdAdministrativeLevel,
            fourthAdministrativeLevel = fourthAdministrativeLevel,
            country = country,
            timezone = timezone,
        )
    }
}
