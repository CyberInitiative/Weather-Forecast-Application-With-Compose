package com.example.weathercompose.data.model.city

import com.google.gson.annotations.SerializedName

// https://open-meteo.com/en/docs/geocoding-api
data class CitySearchResult(
    @SerializedName("generationtime_ms")
    val generationTimeInMilliseconds: Double?,
    @SerializedName("results")
    val cities: List<City>?
)