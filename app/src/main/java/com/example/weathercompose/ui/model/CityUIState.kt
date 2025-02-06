package com.example.weathercompose.ui.model

data class CityUIState(
    val id: String = "",
    val name: String = "",
    val country: String = "",
    val firstAdministrativeLevel: String = "",
    val secondAdministrativeLevel: String = "",
    val thirdAdministrativeLevel: String = "",
    val fourthAdministrativeLevel: String = "",
    val forecasts: List<DailyForecastItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String = "",
    val forecastLoadingErrorMessage: String = "",
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