package com.example.weathercompose.data.model.forecast

import com.google.gson.annotations.SerializedName

// https://open-meteo.com/en/docs
data class DailyUnitsResponse(
    @SerializedName("sunrise")
    val sunrise: String,
    @SerializedName("sunset")
    val sunset: String,
    @SerializedName("temperature_2m_max")
    val maxTemperature: String,
    @SerializedName("temperature_2m_min")
    val minTemperature: String,
    @SerializedName("time")
    val date: String,
    @SerializedName("weather_code")
    val weatherCode: String
)