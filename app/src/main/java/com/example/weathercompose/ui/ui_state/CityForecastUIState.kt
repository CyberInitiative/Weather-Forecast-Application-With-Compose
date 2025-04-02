package com.example.weathercompose.ui.ui_state

import androidx.annotation.DrawableRes
import com.example.weathercompose.R

sealed class CityForecastUIState {

    data class ErrorForecastUIState(val errorMessage: String = "") : CityForecastUIState()

    data object NoCityDataForecastUIState : CityForecastUIState()

    data class CityDataUIState(
        val isDataLoading: Boolean = true,
        val cityName: String = "",
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
    ) : CityForecastUIState()

}