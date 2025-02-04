package com.example.weathercompose.data.model.forecast

import com.google.gson.annotations.SerializedName

// https://open-meteo.com/en/docs
data class HourlyForecast(
    @SerializedName("is_day")
    val isDayData: List<Int>?,
    @SerializedName("temperature_2m")
    val temperatureData: List<Double>?,
    @SerializedName("time")
    val dateAndTimeData: List<String>?,
    @SerializedName("weather_code")
    val weatherCodes: List<Int>?
)