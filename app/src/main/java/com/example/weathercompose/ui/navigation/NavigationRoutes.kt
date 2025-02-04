package com.example.weathercompose.ui.navigation

import kotlinx.serialization.Serializable

sealed class NavigationRoutes() {

    @Serializable
    object Forecast : NavigationRoutes()
    @Serializable
    object CitiesManager : NavigationRoutes()
    @Serializable
    object CitySearch : NavigationRoutes()
}