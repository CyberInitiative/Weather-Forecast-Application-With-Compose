package com.example.weathercompose.domain.model.location

import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.DataState
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel
import com.example.weathercompose.ui.model.WeatherAndDayTimeState
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

    fun getPrecipitationsAndTimeOfDayStateForCurrentHour(): WeatherAndDayTimeState {
        try {
            val hourlyForecast = getForecastForCurrentHour()

            return when {
                hourlyForecast.isDay && !hourlyForecast.isWeatherWithPrecipitations()
                    -> {
                    WeatherAndDayTimeState.NO_PRECIPITATION_DAY
                }

                !hourlyForecast.isDay && !hourlyForecast.isWeatherWithPrecipitations()
                    -> {
                    WeatherAndDayTimeState.NO_PRECIPITATION_NIGHT
                }

                hourlyForecast.isDay && hourlyForecast.isWeatherWithPrecipitations()
                    -> {
                    WeatherAndDayTimeState.OVERCAST_OR_PRECIPITATION_DAY
                }

                !hourlyForecast.isDay && hourlyForecast.isWeatherWithPrecipitations()
                    -> {
                    WeatherAndDayTimeState.OVERCAST_OR_PRECIPITATION_NIGHT
                }

                else -> WeatherAndDayTimeState.NO_PRECIPITATION_DAY
            }
        } catch (e: IllegalStateException){
            return WeatherAndDayTimeState.NO_PRECIPITATION_DAY
        }
    }

    fun shouldLoadForecasts(): Boolean {
        val isDataReady = this.forecastDataState is DataState.Ready
        val isDataLoading = this.forecastDataState is DataState.Loading
        val isTimestampExpired = this.isForecastLastUpdateTimestampExpired()
        return (!isDataReady || isTimestampExpired) && !isDataLoading
    }

    companion object {
        private const val TAG = "LocationDomainModel"
    }
}