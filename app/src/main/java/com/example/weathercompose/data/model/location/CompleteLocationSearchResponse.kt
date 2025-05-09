package com.example.weathercompose.data.model.location

import com.google.gson.annotations.SerializedName

// https://open-meteo.com/en/docs/geocoding-api
data class CompleteLocationSearchResponse(
    @SerializedName("generationtime_ms")
    val generationTimeInMilliseconds: Double?,
    @SerializedName("results")
    val locationSearchResponse: List<LocationSearchResponse>?
)