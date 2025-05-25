package com.example.weathercompose.domain.model.location

import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.DataState
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel
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
    var isHomeLocation: Boolean = false,
    var forecastDataState: DataState<List<DailyForecastDomainModel>> = DataState.Initial,
) {

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

    companion object {
        private const val TAG = "LocationDomainModel"
    }
}