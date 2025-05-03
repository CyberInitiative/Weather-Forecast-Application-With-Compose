package com.example.weathercompose.ui.ui_state

import com.example.weathercompose.data.database.entity.location.LocationEntity

data class LocationSearchState(
    val isLoading: Boolean = false,
    val locations: List<LocationEntity> = emptyList(),
    val errorMessage: String = ""
)