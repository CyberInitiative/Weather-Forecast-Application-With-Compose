package com.example.weathercompose.ui.ui_state

import com.example.weathercompose.ui.model.ForecastUIState

data class LoadCitiesUIState(
    val cities: List<ForecastUIState> = emptyList(),
    val isLoading: Boolean = true,
)