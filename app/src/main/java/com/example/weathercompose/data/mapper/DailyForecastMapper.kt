package com.example.weathercompose.data.mapper

import com.example.weathercompose.data.database.entity.forecast.DailyForecastEntity
import com.example.weathercompose.data.model.forecast.CompleteForecastResponse
import com.example.weathercompose.data.model.forecast.DailyForecastResponse
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.WeatherDescription
import java.time.LocalDate
import java.time.LocalDateTime

class DailyForecastMapper(
    private val hourlyForecastMapper: HourlyForecastMapper,
) {
    fun mapResponseToDailyForecastDomainModels(
        completeForecastResponse: CompleteForecastResponse,
    ): List<DailyForecastDomainModel> {
        if (isDataConsistent(completeForecastResponse.dailyForecastResponse)) {
            return createDailyForecastDomainModels(completeForecastResponse = completeForecastResponse)
        } else {
            throw IllegalStateException("Daily forecast response has inconsistent data!")
        }
    }

    private fun createDailyForecastDomainModels(
        completeForecastResponse: CompleteForecastResponse
    ): List<DailyForecastDomainModel> {
        val hourlyForecastData = completeForecastResponse.hourlyForecastResponse
        val dateToHourlyForecastDomainModels =
            hourlyForecastMapper.mapResponseToDateToHourlyForecastDomainModelMap(
                hourlyForecastData
            )

        with(completeForecastResponse.dailyForecastResponse) {
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
                        sunrise = LocalDateTime.parse(sunrises!![index]).toLocalTime().toString(),
                        sunset = LocalDateTime.parse(sunsets!![index]).toLocalTime().toString(),
                        hourlyForecasts = hourlyForecastDomainModels!!
                    )
                )
            }

            return dailyForecastDomainModels
        }
    }

    fun mapResponseToDailyForecastEntities(
        locationId: Long,
        completeForecastResponse: CompleteForecastResponse,
    ): List<DailyForecastEntity> {
        if (isDataConsistent(completeForecastResponse.dailyForecastResponse)) {
            return createDailyForecastEntities(
                locationId = locationId,
                completeForecastResponse = completeForecastResponse
            )
        } else {
            throw IllegalStateException("Daily forecast response has inconsistent data!")
        }
    }

    private fun createDailyForecastEntities(
        locationId: Long,
        completeForecastResponse: CompleteForecastResponse
    ): List<DailyForecastEntity> {
        val hourlyForecastResponse = completeForecastResponse.hourlyForecastResponse
        val dateToHourlyForecastEntities =
            hourlyForecastMapper.mapResponseToDateToHourlyForecastEntityMap(
                hourlyForecastResponse = hourlyForecastResponse
            )

        with(completeForecastResponse.dailyForecastResponse) {
            val dailyForecastEntities = mutableListOf<DailyForecastEntity>()
            val timestamp: Long = System.currentTimeMillis()

            for (index in dates!!.indices) {
                val date = LocalDate.parse(dates[index])
                val hourlyForecastEntities = dateToHourlyForecastEntities[date.toString()]

                dailyForecastEntities.add(
                    DailyForecastEntity(
                        locationId = locationId,
                        date = date.toString(),
                        weatherDescription = WeatherDescription.weatherCodeToDescription(
                            code = weatherCodes!![index]
                        ),
                        maxTemperature = maxTemperatureData!![index],
                        minTemperature = minTemperatureData!![index],
                        sunrise = sunrises!![index],
                        sunset = sunsets!![index],
                        timestamp = timestamp,
                        hourlyForecasts = hourlyForecastEntities!!
                    )
                )
            }

            return dailyForecastEntities
        }
    }

    private fun isDataConsistent(dailyForecastResponse: DailyForecastResponse): Boolean {
        with(dailyForecastResponse) {
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

