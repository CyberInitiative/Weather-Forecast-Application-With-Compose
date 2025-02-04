package com.example.weathercompose.domain.mapper

import android.content.Context
import com.example.weathercompose.data.model.forecast.DailyForecast
import com.example.weathercompose.data.model.forecast.FullForecast
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.WeatherDescription

class DailyForecastMapper(
    private val context: Context,
    private val hourlyForecastMapper: HourlyForecastMapper,
) {

    fun mapToDomain(
        fullForecast: FullForecast,
    ): List<DailyForecastDomainModel> {
        if (isDataConsistent(fullForecast.dailyForecast)) {
            val dateToHourlyForecastDomainModel =
                hourlyForecastMapper.mapToDomain(fullForecast.hourlyForecast)

            with(fullForecast.dailyForecast) {
                val dailyForecastDomainModelData: MutableList<DailyForecastDomainModel> =
                    mutableListOf()
                for (index in dates!!.indices) {
                    val hourlyForecastDomainModelData =
                        dateToHourlyForecastDomainModel[dates[index]]

                    dailyForecastDomainModelData.add(
                        DailyForecastDomainModel(
                            date = dates[index],
                            weatherDescription = WeatherDescription.weatherCodeToDescription(
                                code = weatherCodes!![index]
                            ),
                            maxTemperature = maxTemperatureData!![index],
                            minTemperature = minTemperatureData!![index],
                            sunrise = sunrises!![index],
                            sunset = sunsets!![index],
                            hourlyForecastDomainModelData = hourlyForecastDomainModelData!!
                        )
                    )
                }
                return dailyForecastDomainModelData
            }
        } else {
            throw IllegalStateException("DailyForecast object has inconsistent data!")
        }
    }

    private fun isDataConsistent(dailyForecast: DailyForecast): Boolean {
        with(dailyForecast) {
            val lists = listOf(
                dates,
                weatherCodes,
                maxTemperatureData,
                minTemperatureData,
                sunrises,
                sunsets,
            )
            val isAllDataExists = lists.all { it != null }

            if (isAllDataExists) {
                return lists.all { it!!.size == lists.first()!!.size }
            }
        }
        return false
    }
}

