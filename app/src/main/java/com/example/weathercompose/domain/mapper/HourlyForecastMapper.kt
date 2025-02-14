package com.example.weathercompose.domain.mapper

import android.content.Context
import com.example.weathercompose.data.model.forecast.HourlyForecast
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.WeatherDescription
import com.example.weathercompose.utils.timeToHour

class HourlyForecastMapper(
    private val context: Context
) {

    fun mapToDomain(
        hourlyForecast: HourlyForecast,
    ): MutableMap<String, MutableList<HourlyForecastDomainModel>> {
        if (isDataConsistent(hourlyForecast)) {
            val dateToDailyForecastDomainModelData =
                mutableMapOf<String, MutableList<HourlyForecastDomainModel>>()

            for (index in hourlyForecast.dateAndTimeData!!.indices) {
                val dateAndHour = getDateAndHour(hourlyForecast.dateAndTimeData[index])

                val hourlyForecastDomainModel = HourlyForecastDomainModel(
                    date = dateAndHour.date,
                    hour = dateAndHour.hour,
                    weatherDescription = WeatherDescription.weatherCodeToDescription(
                        code = hourlyForecast.weatherCodes!![index]
                    ),
                    temperature = hourlyForecast.temperatureData!![index],
                    isDay = isDayTime(hourlyForecast.isDayData!![index]),
                )

                if (dateToDailyForecastDomainModelData.contains(dateAndHour.date)) {
                    dateToDailyForecastDomainModelData[dateAndHour.date]?.add(
                        hourlyForecastDomainModel
                    )
                } else {
                    dateToDailyForecastDomainModelData[dateAndHour.date] =
                        mutableListOf(hourlyForecastDomainModel)
                }
            }

            return dateToDailyForecastDomainModelData

        } else {
            throw IllegalStateException("DailyForecast object has inconsistent data!")
        }
    }

    private fun isDataConsistent(hourlyForecast: HourlyForecast): Boolean {
        with(hourlyForecast) {
            val lists = listOf(
                dateAndTimeData,
                weatherCodes,
                temperatureData,
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

    private data class DateAndHourPair(val date: String, val hour: String)

    private fun getDateAndHour(dateAndTime: String): DateAndHourPair {
        val (date, time) = dateAndTime.trim().split("T")
        val hour = timeToHour(time)
        return DateAndHourPair(date, hour)
    }

    companion object {
        private const val DAYLIGHT = 1
        private const val NIGHT = 0
    }
}


