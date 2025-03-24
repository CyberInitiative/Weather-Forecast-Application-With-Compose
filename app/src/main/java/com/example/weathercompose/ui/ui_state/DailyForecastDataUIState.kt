package com.example.weathercompose.ui.ui_state

import com.example.weathercompose.ui.model.DailyForecastItem

sealed class DailyForecastDataUIState {

    data class DailyForecastDataPresentUIState(val dailyForecastItems: List<DailyForecastItem>) :
        DailyForecastDataUIState()

    data object NoActualForecastDataUIState : DailyForecastDataUIState()
}