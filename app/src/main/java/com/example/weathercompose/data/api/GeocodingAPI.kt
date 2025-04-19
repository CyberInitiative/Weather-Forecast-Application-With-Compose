package com.example.weathercompose.data.api

import com.example.weathercompose.data.model.location.LocationSearchResult
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingAPI {

    @GET("v1/search")
    suspend fun searchLocation(
        @Query("name") name: String,
        @Query("count") count: Int,
        @Query("language") language: String,
        @Query("format") format: String,
    ): LocationSearchResult

    companion object{
        const val DEFAULT_NUMBER_OF_RESULTS = 20
        const val DEFAULT_LANGUAGE = "en"
        const val DEFAULT_FORMAT = "json"
    }
}