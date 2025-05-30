package com.example.weathercompose.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class LocationItem(
    val id: Long,
    val name: String,
    val currentHourTemperature: String,
    @StringRes
    val currentHourWeatherDescription: Int,
    @DrawableRes
    val currentHourWeatherIconRes: Int,
    val isHomeLocation: Boolean,
)