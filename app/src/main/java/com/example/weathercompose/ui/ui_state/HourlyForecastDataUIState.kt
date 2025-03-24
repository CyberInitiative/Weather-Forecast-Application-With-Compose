package com.example.weathercompose.ui.ui_state

import com.example.weathercompose.ui.model.HourlyForecastItem

sealed class HourlyForecastDataUIState {

    data class HourlyForecastDataPresentUIState(val hourlyForecastItems: List<HourlyForecastItem>) :
        HourlyForecastDataUIState()

    data object NoActualForecastDataUIState : HourlyForecastDataUIState()
}