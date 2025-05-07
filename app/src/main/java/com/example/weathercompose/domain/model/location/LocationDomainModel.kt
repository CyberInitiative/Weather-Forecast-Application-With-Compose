package com.example.weathercompose.domain.model.location

import com.example.weathercompose.domain.model.forecast.DataState
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel
import com.example.weathercompose.ui.model.PrecipitationCondition
import com.example.weathercompose.utils.getCurrentDateAndHourInTimeZone
import java.time.LocalDateTime

data class LocationDomainModel(
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
    val forecastLastUpdateTimestamp: Long = 0L,
    var forecastDataState: DataState<List<DailyForecastDomainModel>> = DataState.Initial,
) {

    fun isForecastLastUpdateTimestampExpired(): Boolean {
        if (forecastLastUpdateTimestamp == 0L) return true
        val currentTimeMillis = System.currentTimeMillis()
        val oneHourInMillis = 60 * 60 * 1000
        return currentTimeMillis - forecastLastUpdateTimestamp > oneHourInMillis
    }

    fun getForecastForCurrentHour(): HourlyForecastDomainModel {
        if (forecastDataState is DataState.Ready) {
            val currentDateAndHour = getCurrentDateAndHourInTimeZone(timeZone)

            val forecastReadyData =
                forecastDataState as DataState.Ready<List<DailyForecastDomainModel>>
            val dailyForecast = forecastReadyData.data.firstOrNull {
                it.date == currentDateAndHour.toLocalDate()
            } ?: throw IllegalStateException("No data available!")
            return dailyForecast.hourlyForecasts.firstOrNull {
                it.time == currentDateAndHour.toLocalTime()
            } ?: throw IllegalStateException("No data available!")
        } else {
            throw IllegalStateException("Forecast data is not ready!")
        }
    }

    fun getForecastFor24Hours(): List<HourlyForecastDomainModel> {
        if (forecastDataState is DataState.Ready) {
            val currentDateAndHour = getCurrentDateAndHourInTimeZone(timeZone)
            val forecastReadyData =
                forecastDataState as DataState.Ready<List<DailyForecastDomainModel>>

            return forecastReadyData.data.asSequence().filter {
                it.date == currentDateAndHour.toLocalDate() ||
                        it.date == currentDateAndHour.toLocalDate().plusDays(1)
            }.flatMap { it.hourlyForecasts }
                .filter {
                    LocalDateTime.of(
                        it.date,
                        it.time
                    ) > currentDateAndHour.toLocalDateTime()
                }
                .take(24)
                .toList()

        } else {
            throw IllegalStateException("Forecast data is not ready!")
        }
    }

    fun getPrecipitationsAndTimeOfDayStateForCurrentHour(): PrecipitationCondition {
        try {
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
        } catch (e: IllegalStateException){
            return PrecipitationCondition.NO_PRECIPITATION_DAY
        }
    }

    companion object {
        private const val TAG = "LocationDomainModel"
    }
}