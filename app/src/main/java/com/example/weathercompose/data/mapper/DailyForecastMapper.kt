package com.example.weathercompose.data.mapper

import com.example.weathercompose.data.model.forecast.DailyForecastDataModel
import com.example.weathercompose.data.model.forecast.FullForecast
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.WeatherDescription
import java.time.LocalDate

class DailyForecastMapper(
    private val hourlyForecastMapper: HourlyForecastMapper,
) {
    fun mapToDomain(
        fullForecast: FullForecast,
    ): List<DailyForecastDomainModel> {
        if (isDataConsistent(fullForecast.dailyForecastDataModel)) {
            return onDataConsistent(fullForecast = fullForecast)
        } else {
            throw IllegalStateException("DailyForecast object has inconsistent data!")
        }
    }

    private fun onDataConsistent(fullForecast: FullForecast)
            : MutableList<DailyForecastDomainModel> {
        val hourlyForecastData = fullForecast.hourlyForecastDataModel
        val dateToHourlyForecastDomainModels =
            hourlyForecastMapper.mapToDateToHourlyForecasts(hourlyForecastData)

        with(fullForecast.dailyForecastDataModel) {
            val dailyForecastDomainModels = mutableListOf<DailyForecastDomainModel>()

            for (index in dates!!.indices) {
                val date = LocalDate.parse(dates[index])
                val hourlyForecastDomainModels = dateToHourlyForecastDomainModels[date.toString()]

                dailyForecastDomainModels.add(
                    DailyForecastDomainModel(
                        date = date,
                        weatherDescription = WeatherDescription.weatherCodeToDescription(
                            code = weatherCodes!![index]
                        ),
                        maxTemperature = maxTemperatureData!![index],
                        minTemperature = minTemperatureData!![index],
                        sunrise = sunrises!![index],
                        sunset = sunsets!![index],
                        hourlyForecasts = hourlyForecastDomainModels!!
                    )
                )
            }

            return dailyForecastDomainModels
        }
    }

    private fun isDataConsistent(dailyForecastDataModel: DailyForecastDataModel): Boolean {
        with(dailyForecastDataModel) {
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

    companion object {
        private const val TAG = "DailyForecastMapper"
    }
}

