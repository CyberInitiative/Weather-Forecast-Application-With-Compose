package com.example.weathercompose.data.database.entity.combined

import androidx.room.Embedded
import androidx.room.Relation
import com.example.weathercompose.data.database.entity.forecast.DailyForecastEntity
import com.example.weathercompose.data.database.entity.forecast.HourlyForecastEntity

data class DailyForecastWithHourlyForecast(
    @Embedded val dailyForecastEntity: DailyForecastEntity,
    @Relation(
        parentColumn = "dailyForecastId",
        entityColumn = "dailyForecastId"
    )
    val hourlyForecasts: List<HourlyForecastEntity>
) {

    fun getLocationId(): Long = dailyForecastEntity.locationId
}