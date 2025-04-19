package com.example.weathercompose.ui.ui_state

import com.example.weathercompose.domain.model.location.LocationDomainModel

data class LocationSearchUIState(
    val isLoading: Boolean = false,
    val locations: List<LocationDomainModel> = emptyList(),
    val errorMessage: String = ""
)