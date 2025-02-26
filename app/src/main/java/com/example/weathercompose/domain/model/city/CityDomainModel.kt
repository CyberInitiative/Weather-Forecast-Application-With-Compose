package com.example.weathercompose.domain.model.city

import android.util.Log
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel
import com.example.weathercompose.ui.model.PrecipitationCondition
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

data class CityDomainModel(
    val latitude: Double,
    val longitude: Double,
    val id: Long,
    val name: String,
    val firstAdministrativeLevel: String,
    val secondAdministrativeLevel: String,
    val thirdAdministrativeLevel: String,
    val fourthAdministrativeLevel: String,
    val country: String,
    val timeZone: String,
    val forecasts: List<DailyForecastDomainModel>,
    val errorMessage: String = "",
) {

    fun getForecastForCurrentHour(): HourlyForecastDomainModel {
        val now = ZonedDateTime.now(ZoneId.of(timeZone))
            .truncatedTo(ChronoUnit.MINUTES)
            .withMinute(0)
        val dailyForecast = forecasts.first { it.date == now.toLocalDate() }

        return dailyForecast.hourlyForecasts.first { it.time == now.toLocalTime() }
    }

    fun getForecastFor24Hours(): List<HourlyForecastDomainModel> {
        val now = ZonedDateTime.now(ZoneId.of(timeZone)).truncatedTo(ChronoUnit.MINUTES)

        return forecasts.asSequence().filter {
            it.date == now.toLocalDate() || it.date == now.toLocalDate().plusDays(1)
        }.flatMap { it.hourlyForecasts }
            .filter { LocalDateTime.of(it.date, it.time) > now.toLocalDateTime() }
            .take(24)
            .toList()
    }

    fun getPrecipitationsAndTimeOfDayStateForCurrentHour(): PrecipitationCondition {
        val hourlyForecast = getForecastForCurrentHour()
        return when {
            hourlyForecast.isDay && !hourlyForecast.isWeatherWithPrecipitations()
                -> {
                Log.d(TAG, "case 1")
                PrecipitationCondition.NO_PRECIPITATION_DAY
            }

            !hourlyForecast.isDay && !hourlyForecast.isWeatherWithPrecipitations()
                -> {
                Log.d(TAG, "case 2")
                PrecipitationCondition.NO_PRECIPITATION_NIGHT
            }

            hourlyForecast.isDay && hourlyForecast.isWeatherWithPrecipitations()
                -> {
                Log.d(TAG, "case 3")
                PrecipitationCondition.PRECIPITATION_DAY
            }

            !hourlyForecast.isDay && hourlyForecast.isWeatherWithPrecipitations()
                -> {
                Log.d(TAG, "case 4")
                PrecipitationCondition.PRECIPITATION_NIGHT
            }

            else -> PrecipitationCondition.NO_PRECIPITATION_DAY
        }
    }

    fun getFullLocationName(): String {
        return listOf(
            name,
            country,
            firstAdministrativeLevel,
            secondAdministrativeLevel,
            thirdAdministrativeLevel,
            fourthAdministrativeLevel,
        ).filter { it.isNotEmpty() }.joinToString()
    }

    companion object {
        private const val TAG = "CityDomainModel"
    }
}