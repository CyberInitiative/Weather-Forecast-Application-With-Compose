package com.example.weathercompose.ui.ui_state

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class LocationItemUIState {
    data object Loading : LocationItemUIState()
    data class DataLoaded(
        val currentHourTemperature: String,
        @StringRes
        val currentHourWeatherDescription: Int,
        @DrawableRes
        val currentHourWeatherIconRes: Int,
    ) : LocationItemUIState()
}