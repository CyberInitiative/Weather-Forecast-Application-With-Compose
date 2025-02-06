package com.example.weathercompose.ui.ui_state

import com.example.weathercompose.ui.model.CityUIState

data class LoadCitiesUIState(
    val cities: List<CityUIState> = emptyList(),
    val isLoading: Boolean = true,
)