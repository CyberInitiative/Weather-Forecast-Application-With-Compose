package com.example.weathercompose.domain.mapper

import android.content.Context
import com.example.weathercompose.data.model.forecast.HourlyForecast
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.WeatherDescription

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
                val dateAndTime = getDate(hourlyForecast.dateAndTimeData[index])

                val hourlyForecastDataModel = HourlyForecastDomainModel(
                    date = dateAndTime.date,
                    time = dateAndTime.time,
                    weatherDescription = WeatherDescription.weatherCodeToDescription(
                        code = hourlyForecast.weatherCodes!![index]
                    ),
                    temperature = hourlyForecast.temperatureData!![index],
                    isDay = isDayTime(hourlyForecast.isDayData!![index]),
                )

                if (dateToDailyForecastDomainModelData.contains(dateAndTime.date)) {
                    dateToDailyForecastDomainModelData[dateAndTime.date]?.add(
                        hourlyForecastDataModel
                    )
                } else {
                    dateToDailyForecastDomainModelData[dateAndTime.date] =
                        mutableListOf(hourlyForecastDataModel)
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

    private data class DateAndTimePair(val date: String, val time: String)

    private fun getDate(dateAndTime: String): DateAndTimePair {
        val (date, time) = dateAndTime.trim().split("T")
        return DateAndTimePair(date, time)
    }

    companion object {
        private const val DAYLIGHT = 1
        private const val NIGHT = 0
    }
}


