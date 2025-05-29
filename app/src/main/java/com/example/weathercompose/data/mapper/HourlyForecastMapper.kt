package com.example.weathercompose.data.mapper

import com.example.weathercompose.data.database.entity.forecast.HourlyForecastEntity
import com.example.weathercompose.data.model.forecast.HourlyForecastResponse
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.WeatherDescription
import java.time.LocalDateTime

class HourlyForecastMapper {

    fun mapResponseToDateToHourlyForecastDomainModelMap(
        hourlyForecastResponse: HourlyForecastResponse,
    ): MutableMap<String, MutableList<HourlyForecastDomainModel>> {
        if (isDataConsistent(hourlyForecastResponse)) {
            return createDateToHourlyForecastDomainModelMap(
                hourlyForecastResponse = hourlyForecastResponse
            )
        } else {
            throw IllegalStateException("Hourly forecast response has inconsistent data!")
        }
    }

    private fun createDateToHourlyForecastDomainModelMap(
        hourlyForecastResponse: HourlyForecastResponse
    ): MutableMap<String, MutableList<HourlyForecastDomainModel>> {
        val dateToForecasts = mutableMapOf<String, MutableList<HourlyForecastDomainModel>>()

        with(hourlyForecastResponse) {
            for (index in dateAndTimeData!!.indices) {
                val dateAndTime = LocalDateTime.parse(dateAndTimeData[index])
                val date = dateAndTime.toLocalDate()
                val time = dateAndTime.toLocalTime()

                val hourlyForecastDomainModel = HourlyForecastDomainModel(
                    date = date,
                    time = time,
                    temperature = temperatureData!![index],
                    relativeHumidity = relativeHumidity!![index],
                    precipitationProbability = precipitationProbability!![index],
                    weatherDescription = WeatherDescription.weatherCodeToDescription(
                        code = weatherCodes!![index]
                    ),
                    windSpeed = windSpeed!![index],
                    isDay = isDayTime(isDayData!![index]),
                )

                if (dateToForecasts.contains(date.toString())) {
                    dateToForecasts[date.toString()]?.add(
                        hourlyForecastDomainModel
                    )
                } else {
                    dateToForecasts[date.toString()] = mutableListOf(hourlyForecastDomainModel)
                }
            }
        }

        return dateToForecasts
    }

    fun mapResponseToDateToHourlyForecastEntityMap(
        hourlyForecastResponse: HourlyForecastResponse
    ): MutableMap<String, MutableList<HourlyForecastEntity>> {
        if (isDataConsistent(hourlyForecastResponse)) {
            return createDateToHourlyForecastEntityMap(
                hourlyForecastResponse = hourlyForecastResponse
            )
        } else {
            throw IllegalStateException("Hourly forecast response has inconsistent data!")
        }
    }

    private fun createDateToHourlyForecastEntityMap(
        hourlyForecastResponse: HourlyForecastResponse
    ): MutableMap<String, MutableList<HourlyForecastEntity>> {
        val dateToForecasts = mutableMapOf<String, MutableList<HourlyForecastEntity>>()

        with(hourlyForecastResponse) {
            for (index in dateAndTimeData!!.indices) {
                val dateAndTime = LocalDateTime.parse(dateAndTimeData[index])
                val date = dateAndTime.toLocalDate()
                val time = dateAndTime.toLocalTime()

                val hourlyForecastEntity = HourlyForecastEntity(
                    dailyForecastId = NO_DAILY_FORECAST_ID_SET,
                    date = date.toString(),
                    time = time.toString(),
                    temperature = temperatureData!![index],
                    weatherDescription = WeatherDescription.weatherCodeToDescription(
                        code = weatherCodes!![index]
                    ),
                    relativeHumidity = relativeHumidity!![index],
                    precipitationProbability = precipitationProbability!![index],
                    windSpeed = windSpeed!![index],
                    isDay = isDayTime(isDayData!![index]),
                )

                if (dateToForecasts.contains(date.toString())) {
                    dateToForecasts[date.toString()]?.add(
                        hourlyForecastEntity
                    )
                } else {
                    dateToForecasts[date.toString()] = mutableListOf(hourlyForecastEntity)
                }
            }
        }

        return dateToForecasts
    }

    private fun isDataConsistent(hourlyForecastResponse: HourlyForecastResponse): Boolean {
        with(hourlyForecastResponse) {
            val lists = listOf(
                dateAndTimeData,
                temperatureData,
                relativeHumidity,
                precipitationProbability,
                weatherCodes,
                windSpeed,
                isDayData,
            )
            val isAllDataExists = lists.all { it != null }

            if (isAllDataExists) {
                return lists.all { it!!.size == lists.first()!!.size }
            }
        }
        return false
    }

    private fun isDayTime(code: Int): Boolean {
        return when (code) {
            DAYLIGHT -> true
            NIGHT -> false
            else -> throw IllegalStateException("code expected to be 1 (DAYLIGHT) or 0 (NIGHT)!")
        }
    }

    companion object {
        private const val NO_DAILY_FORECAST_ID_SET = 0L

        private const val DAYLIGHT = 1
        private const val NIGHT = 0

        private const val TAG = "HourlyForecastMapper"
    }
}


