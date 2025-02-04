package com.example.weathercompose.ui.model

data class DailyForecastItem(
    val date: String,
    val dayNameInWeek: String,
    val monthAndDayNumber: String,
    val weatherDescription: String,
    val maxTemperature: Int,
    val minTemperature: Int,
    val hourlyForecasts: List<HourlyForecastItem>,
)