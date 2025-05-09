package com.example.weathercompose.data.model.forecast

import com.google.gson.annotations.SerializedName

// https://open-meteo.com/en/docs
data class CompleteForecastResponse(
    @SerializedName("daily")
    val dailyForecastResponse: DailyForecastResponse,
    @SerializedName("daily_units")
    val dailyUnitsResponse: DailyUnitsResponse,
    @SerializedName("elevation")
    val elevation: Double,
    @SerializedName("generationtime_ms")
    val generationTimeInMilliseconds: Double,
    @SerializedName("hourly")
    val hourlyForecastResponse: HourlyForecastResponse,
    @SerializedName("hourly_units")
    val hourlyUnitsResponse: HourlyUnitsResponse,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("timezone_abbreviation")
    val timezoneAbbreviation: String,
    @SerializedName("utc_offset_seconds")
    val utcOffsetSeconds: Int
)