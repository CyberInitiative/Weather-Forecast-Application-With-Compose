package com.example.weathercompose.ui.ui_state

import com.example.weathercompose.ui.model.DailyForecastItem
import com.example.weathercompose.ui.model.HourlyForecastItem

data class LocationUIState(
    val locationName: String = "",
    val currentHourTemperature: String = "",
    val currentDayOfWeekAndDate: String = "",
    val currentDayMaxTemperature: String = "",
    val currentDayMinTemperature: String = "",
    val currentHourWeatherStatus: String = "",
    val dailyForecasts: List<DailyForecastItem> = emptyList(),
    val hourlyForecasts: List<HourlyForecastItem> = emptyList(),
    val loading: Boolean = false,
    val errorMessage: String = "",
)

/*
sealed LocationUIState {
  data object Loading : LocationForecastState()
  data class LoadingForecastError(val errorMessage: String = "") : LocationForecastState()
  data object NoLocationData : LocationForecastState()
  data class ReadyLocationData(
        val locationName: String = "",
        val currentHourTemperature: String = "",
        val currentDayOfWeekAndDate: String = "",
        val currentDayMaxTemperature: String = "",
        val currentDayMinTemperature: String = "",
        val currentHourWeatherStatus: String = "",
        @DrawableRes val currentWeatherIcon: Int = R.drawable.ic_launcher_background,
        val dailyForecastsUIState: DailyForecastDataUIState =
            DailyForecastDataUIState.NoActualForecastDataUIState,
        val hourlyForecastsUIState: HourlyForecastDataUIState =
            HourlyForecastDataUIState.NoActualForecastDataUIState,
    ) : LocationUIState()
 */