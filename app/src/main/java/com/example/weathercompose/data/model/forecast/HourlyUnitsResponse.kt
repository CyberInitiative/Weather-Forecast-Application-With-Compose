package com.example.weathercompose.data.model.forecast

import com.google.gson.annotations.SerializedName

// https://open-meteo.com/en/docs
data class HourlyUnitsResponse(
    @SerializedName("is_day")
    val isDay: String,
    @SerializedName("temperature_2m")
    val temperature: String,
    @SerializedName("time")
    val dateAndTime: String,
    @SerializedName("weather_code")
    val weatherCode: String
)