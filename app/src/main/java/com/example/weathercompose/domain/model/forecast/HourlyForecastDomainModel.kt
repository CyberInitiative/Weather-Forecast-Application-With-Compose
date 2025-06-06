package com.example.weathercompose.domain.model.forecast

import java.time.LocalDate
import java.time.LocalTime

data class HourlyForecastDomainModel(
    val date: LocalDate,
    val time: LocalTime,
    val temperature: Double,
    val relativeHumidity: Int,
    val precipitationProbability: Int,
    val weatherDescription: WeatherDescription,
    val windSpeed: Double,
    val isDay: Boolean,
) {

    fun isWeatherWithPrecipitationsOrOvercast(): Boolean {
        return when (weatherDescription) {
            WeatherDescription.FOG,
            WeatherDescription.DEPOSITING_RIME_FOG,
            WeatherDescription.OVERCAST,
            WeatherDescription.LIGHT_DRIZZLE,
            WeatherDescription.MODERATE_DRIZZLE,
            WeatherDescription.DENSE_DRIZZLE,
            WeatherDescription.LIGHT_FREEZING_DRIZZLE,
            WeatherDescription.DENSE_FREEZING_DRIZZLE,
            WeatherDescription.SLIGHT_RAIN,
            WeatherDescription.MODERATE_RAIN,
            WeatherDescription.HEAVY_RAIN,
            WeatherDescription.LIGHT_FREEZING_RAIN,
            WeatherDescription.HEAVY_FREEZING_RAIN,
            WeatherDescription.SLIGHT_SNOW_FALL,
            WeatherDescription.MODERATE_SNOW_FALL,
            WeatherDescription.HEAVY_SNOW_FALL,
            WeatherDescription.SNOW_GRAINS,
            WeatherDescription.SLIGHT_RAIN_SHOWERS,
            WeatherDescription.MODERATE_RAIN_SHOWERS,
            WeatherDescription.VIOLENT_RAIN_SHOWERS,
            WeatherDescription.SLIGHT_SNOW_SHOWERS,
            WeatherDescription.HEAVY_SNOW_SHOWERS,
            WeatherDescription.THUNDERSTORM,
            WeatherDescription.THUNDERSTORM_WITH_SLIGHT_HAIL,
            WeatherDescription.THUNDERSTORM_WITH_HEAVY_HAIL -> true

            else -> false
        }
    }

    fun isWeatherWithPrecipitations(): Boolean {
        return when (weatherDescription) {
            WeatherDescription.LIGHT_DRIZZLE,
            WeatherDescription.MODERATE_DRIZZLE,
            WeatherDescription.DENSE_DRIZZLE,
            WeatherDescription.LIGHT_FREEZING_DRIZZLE,
            WeatherDescription.DENSE_FREEZING_DRIZZLE,
            WeatherDescription.SLIGHT_RAIN,
            WeatherDescription.MODERATE_RAIN,
            WeatherDescription.HEAVY_RAIN,
            WeatherDescription.LIGHT_FREEZING_RAIN,
            WeatherDescription.HEAVY_FREEZING_RAIN,
            WeatherDescription.SLIGHT_SNOW_FALL,
            WeatherDescription.MODERATE_SNOW_FALL,
            WeatherDescription.HEAVY_SNOW_FALL,
            WeatherDescription.SNOW_GRAINS,
            WeatherDescription.SLIGHT_RAIN_SHOWERS,
            WeatherDescription.MODERATE_RAIN_SHOWERS,
            WeatherDescription.VIOLENT_RAIN_SHOWERS,
            WeatherDescription.SLIGHT_SNOW_SHOWERS,
            WeatherDescription.HEAVY_SNOW_SHOWERS,
            WeatherDescription.THUNDERSTORM,
            WeatherDescription.THUNDERSTORM_WITH_SLIGHT_HAIL,
            WeatherDescription.THUNDERSTORM_WITH_HEAVY_HAIL -> true

            else -> false
        }
    }
}