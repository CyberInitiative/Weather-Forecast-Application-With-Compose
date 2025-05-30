package com.example.weathercompose.data.api

import com.example.weathercompose.data.model.forecast.CompleteForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastAPI {

    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("timezone") timeZone: String,
        @Query("daily") dailyOptions: List<String>,
        @Query("hourly") hourlyOptions: List<String>,
        @Query("forecast_days") forecastDays: Int,
    ): CompleteForecastResponse

    companion object {
        const val AUTOMATICALLY_DETECT_TIME_ZONE = "auto"
        const val DEFAULT_FORECAST_DAYS = 7

        val dailyOptions =
            listOf(
                "weather_code",
                "temperature_2m_max",
                "temperature_2m_min",
                "sunrise",
                "sunset",
            )

        val hourlyOptions =
            listOf(
                "temperature_2m",
                "relative_humidity_2m",
                "precipitation_probability",
                "wind_speed_10m",
                "weather_code",
                "is_day"
            )
    }
}