package com.example.weathercompose.ui.ui_state

sealed class CityForecastUIState {

    data class ErrorForecastUIState(val errorMessage: String = "") : CityForecastUIState()

    data object NoCityDataForecastUIState : CityForecastUIState()

    data class CityDataUIState(
        val isDataLoading: Boolean = true,
        val cityName: String = "",
        val currentHourTemperature: String = "",
        val currentDayMaxAndMinTemperature: String = "",
        val currentDayWeatherStatus: String = "",
        val dailyForecastsUIState: DailyForecastDataUIState =
            DailyForecastDataUIState.NoActualForecastDataUIState,
        val hourlyForecastsUIState: HourlyForecastDataUIState =
            HourlyForecastDataUIState.NoActualForecastDataUIState,
    ) : CityForecastUIState()

}