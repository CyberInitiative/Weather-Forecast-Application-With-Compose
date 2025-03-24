package com.example.weathercompose.domain.model.city

import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel
import com.example.weathercompose.ui.model.PrecipitationCondition
import com.example.weathercompose.utils.getCurrentDateAndHourInTimeZone
import java.time.LocalDateTime

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
        val currentDateAndHour = getCurrentDateAndHourInTimeZone(timeZone)
        val dailyForecast = forecasts.first { it.date == currentDateAndHour.toLocalDate() }

        return dailyForecast.hourlyForecasts.first { it.time == currentDateAndHour.toLocalTime() }
    }

    fun getForecastFor24Hours(): List<HourlyForecastDomainModel> {
        val currentDateAndHour = getCurrentDateAndHourInTimeZone(timeZone)

        return forecasts.asSequence().filter {
            it.date == currentDateAndHour.toLocalDate() ||
                    it.date == currentDateAndHour.toLocalDate().plusDays(1)
        }.flatMap { it.hourlyForecasts }
            .filter { LocalDateTime.of(it.date, it.time) > currentDateAndHour.toLocalDateTime() }
            .take(24)
            .toList()
    }

    fun getPrecipitationsAndTimeOfDayStateForCurrentHour(): PrecipitationCondition {
        val hourlyForecast = getForecastForCurrentHour()
        return when {
            hourlyForecast.isDay && !hourlyForecast.isWeatherWithPrecipitations()
                -> {
                PrecipitationCondition.NO_PRECIPITATION_DAY
            }

            !hourlyForecast.isDay && !hourlyForecast.isWeatherWithPrecipitations()
                -> {
                PrecipitationCondition.NO_PRECIPITATION_NIGHT
            }

            hourlyForecast.isDay && hourlyForecast.isWeatherWithPrecipitations()
                -> {
                PrecipitationCondition.PRECIPITATION_DAY
            }

            !hourlyForecast.isDay && hourlyForecast.isWeatherWithPrecipitations()
                -> {
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