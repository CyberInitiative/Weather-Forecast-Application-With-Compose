package com.example.weathercompose.data.api

import com.example.weathercompose.data.model.city.CitySearchResult
import retrofit2.http.GET
import retrofit2.http.Query

interface CityService {

    @GET("v1/search")
    suspend fun searchCities(
        @Query("name") name: String,
        @Query("count") count: Int,
        @Query("language") language: String,
        @Query("format") format: String,
    ): CitySearchResult
}