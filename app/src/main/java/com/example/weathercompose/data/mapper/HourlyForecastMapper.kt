package com.example.weathercompose.data.mapper

import com.example.weathercompose.data.model.forecast.HourlyForecastDataModel
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.WeatherDescription
import java.time.LocalDateTime

class HourlyForecastMapper {

    fun mapToDateToHourlyForecasts(
        hourlyForecastDataModel: HourlyForecastDataModel,
    ): MutableMap<String, MutableList<HourlyForecastDomainModel>> {
        if (isDataConsistent(hourlyForecastDataModel)) {
            return onDataConsistent(hourlyForecastDataModel = hourlyForecastDataModel)
        } else {
            throw IllegalStateException("DailyForecast object has inconsistent data!")
        }
    }

    private fun onDataConsistent(hourlyForecastDataModel: HourlyForecastDataModel)
            : MutableMap<String, MutableList<HourlyForecastDomainModel>> {
        val dateToForecasts = mutableMapOf<String, MutableList<HourlyForecastDomainModel>>()

        with(hourlyForecastDataModel) {
            for (index in dateAndTimeData!!.indices) {
                val dateAndTime = LocalDateTime.parse(dateAndTimeData[index])
                val date = dateAndTime.toLocalDate()
                val time = dateAndTime.toLocalTime()

                val hourlyForecastDomainModel = HourlyForecastDomainModel(
                    date = date,
                    time = time,
                    weatherDescription = WeatherDescription.weatherCodeToDescription(
                        code = hourlyForecastDataModel.weatherCodes!![index]
                    ),
                    temperature = hourlyForecastDataModel.temperatureData!![index],
                    isDay = isDayTime(hourlyForecastDataModel.isDayData!![index]),
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

    private fun isDataConsistent(hourlyForecastDataModel: HourlyForecastDataModel): Boolean {
        with(hourlyForecastDataModel) {
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

    companion object {
        private const val DAYLIGHT = 1
        private const val NIGHT = 0
    }
}


