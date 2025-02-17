package com.example.weathercompose.ui.model

data class ForecastUIState(
    val isDataLoading: Boolean = true,
    val cityName: String = "",
    val dailyForecasts: List<DailyForecastItem> = emptyList(),
    val hourlyForecasts: List<HourlyForecastItem> = emptyList(),
    val errorMessage: String = ""
)