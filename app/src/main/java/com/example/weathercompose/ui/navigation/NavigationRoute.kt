package com.example.weathercompose.ui.navigation

import kotlinx.serialization.Serializable

sealed class NavigationRoute() {

    @Serializable
    data class Forecast(val locationId: Long?) : NavigationRoute()

    @Serializable
    data object LocationsManager : NavigationRoute()

    @Serializable
    data class LocationSearch(val isLocationsEmpty: Boolean) : NavigationRoute()
}