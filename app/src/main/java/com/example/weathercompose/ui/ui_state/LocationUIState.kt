package com.example.weathercompose.ui.ui_state

import com.example.weathercompose.ui.model.DailyForecastItem
import com.example.weathercompose.ui.model.HourlyForecastItem

data class LocationUIState(
    val id: Long,
    val locationName: String = "",
    val currentHourTemperature: String = "",
    val currentDayOfWeekAndDate: String = "",
    val currentDayMaxTemperature: String = "",
    val currentDayMinTemperature: String = "",
    val currentHourWeatherStatus: String = "",
    val dailyForecasts: List<DailyForecastItem> = emptyList(),
    val hourlyForecasts: List<HourlyForecastItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = "",
)