package com.example.weathercompose.data.database.entity.combined

import androidx.room.Embedded
import androidx.room.Relation
import com.example.weathercompose.data.database.entity.city.CityEntity
import com.example.weathercompose.data.database.entity.forecast.DailyForecastEntity

data class CityWithDailyForecasts(
    @Embedded val city: CityEntity,
    @Relation(
        entity = DailyForecastEntity::class,
        parentColumn = "cityId",
        entityColumn = "cityId"
    )
    val dailyForecasts: List<DailyForecastWithHourlyForecast>
)