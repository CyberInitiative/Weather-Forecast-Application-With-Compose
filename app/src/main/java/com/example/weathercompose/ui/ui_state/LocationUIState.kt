package com.example.weathercompose.ui.ui_state

import com.example.weathercompose.ui.model.DailyForecastItem
import com.example.weathercompose.ui.model.HourlyForecastItem
import com.example.weathercompose.ui.model.WeatherAndDayTimeState

data class LocationUIState(
    val id: Long,
    val locationName: String = "",
    val locationCountry: String = "",
    val currentHourTemperature: String = "",
    val currentDayOfWeekAndDate: String = "",
    val currentDayMaxTemperature: String = "",
    val currentDayMinTemperature: String = "",
    val currentHourWeatherStatus: String = "",
    val currentRelativeHumidity: String = "",
    val currentWindSpeed: String = "",
    val sunrise: String = "",
    val sunset: String = "",
    val dailyForecasts: List<DailyForecastItem> = emptyList(),
    val hourlyForecasts: List<HourlyForecastItem> = emptyList(),
    val weatherAndDayTimeState: WeatherAndDayTimeState = WeatherAndDayTimeState.NO_PRECIPITATION_DAY,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
)