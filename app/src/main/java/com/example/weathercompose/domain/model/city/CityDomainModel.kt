package com.example.weathercompose.domain.model.city

import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel

data class CityDomainModel(
    val latitude: Double,
    val longitude: Double,
    val id: Int,
    val name: String,
    val firstAdministrativeLevel: String,
    val secondAdministrativeLevel: String,
    val thirdAdministrativeLevel: String,
    val fourthAdministrativeLevel: String,
    val country: String,
    val timezone: String,
    val forecast: List<DailyForecastDomainModel>?,
) {

    fun getFullLocation(): String {
        return listOf(
            name,
            country,
            firstAdministrativeLevel,
            secondAdministrativeLevel,
            thirdAdministrativeLevel,
            fourthAdministrativeLevel,
        ).filter { it.isNotEmpty() }.joinToString()
    }
}