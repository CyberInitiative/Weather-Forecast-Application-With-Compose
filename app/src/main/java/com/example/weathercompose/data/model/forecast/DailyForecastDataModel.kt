package com.example.weathercompose.data.model.forecast

import com.google.gson.annotations.SerializedName

// https://open-meteo.com/en/docs
data class DailyForecastDataModel(
    @SerializedName("sunrise")
    val sunrises: List<String>?,
    @SerializedName("sunset")
    val sunsets: List<String>?,
    @SerializedName("temperature_2m_max")
    val maxTemperatureData: List<Double>?,
    @SerializedName("temperature_2m_min")
    val minTemperatureData: List<Double>?,
    @SerializedName("time")
    val dates: List<String>?,
    @SerializedName("weather_code")
    val weatherCodes: List<Int>?
)