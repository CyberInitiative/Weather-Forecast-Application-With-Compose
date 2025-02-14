package com.example.weathercompose.ui.model

data class ForecastUIState(
    val isDataLoading: Boolean = true,
    val cityName: String = "",
    val forecasts: List<DailyForecastItem> = emptyList(),
    val errorMessage: String = ""
)