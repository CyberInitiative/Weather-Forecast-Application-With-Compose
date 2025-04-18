package com.example.weathercompose.data.database.entity.combined

import androidx.room.Embedded
import androidx.room.Relation
import com.example.weathercompose.data.database.entity.forecast.DailyForecastEntity
import com.example.weathercompose.data.database.entity.location.LocationEntity

data class LocationWithDailyForecasts(
    @Embedded val location: LocationEntity,
    @Relation(
        entity = DailyForecastEntity::class,
        parentColumn = "locationId",
        entityColumn = "locationId"
    )
    val dailyForecasts: List<DailyForecastWithHourlyForecast>
)