package com.example.weathercompose.ui.ui_state

import androidx.annotation.DrawableRes
import com.example.weathercompose.R

sealed class LocationForecastUIState {

    data object InitialUIState : LocationForecastUIState()

    data object LoadingUIState : LocationForecastUIState()

    data class ErrorForecastUIState(val errorMessage: String = "") : LocationForecastUIState()

    data object NoLocationDataForecastUIState : LocationForecastUIState()

    data class LocationDataUIState(
        val isDataLoading: Boolean = true,
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
    ) : LocationForecastUIState()

}