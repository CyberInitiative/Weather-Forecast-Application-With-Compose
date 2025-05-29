package com.example.weathercompose.data.model.forecast

import com.google.gson.annotations.SerializedName

// https://open-meteo.com/en/docs
data class HourlyForecastResponse(
    @SerializedName("time")
    val dateAndTimeData: List<String>?,
    @SerializedName("temperature_2m")
    val temperatureData: List<Double>?,
    @SerializedName("relative_humidity_2m")
    val relativeHumidity: List<Int>?,
    @SerializedName("precipitation_probability")
    val precipitationProbability: List<Int>?,
    @SerializedName("weather_code")
    val weatherCodes: List<Int>?,
    @SerializedName("wind_speed_10m")
    val windSpeed: List<Double>?,
    @SerializedName("is_day")
    val isDayData: List<Int>?,
)