package com.example.weathercompose.ui.model

data class CityUIModel(
    val id: Int,
    val name: String,
    val country: String,
    val firstAdministrativeLevel: String,
    val secondAdministrativeLevel: String,
    val thirdAdministrativeLevel: String,
    val fourthAdministrativeLevel: String,
    val forecasts: List<DailyForecastItem>,
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