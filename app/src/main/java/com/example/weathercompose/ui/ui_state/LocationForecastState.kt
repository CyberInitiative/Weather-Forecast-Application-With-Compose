package com.example.weathercompose.ui.ui_state

import androidx.annotation.DrawableRes
import com.example.weathercompose.R

sealed class LocationForecastState {

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
    ) : LocationForecastState()

}