package com.example.weathercompose.ui.navigation

import kotlinx.serialization.Serializable

sealed class NavigationRoute() {

    @Serializable
    data object Forecast : NavigationRoute()

    @Serializable
    data object CitiesManager : NavigationRoute()

    @Serializable
    data object CitySearch : NavigationRoute()
}