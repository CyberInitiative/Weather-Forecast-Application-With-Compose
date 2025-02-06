package com.example.weathercompose.data.database.entity.city

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class CityEntity(
    @PrimaryKey(autoGenerate = true)
    val cityId: Long,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val firstAdministrativeLevel: String,
    val secondAdministrativeLevel: String,
    val thirdAdministrativeLevel: String,
    val fourthAdministrativeLevel: String,
    val country: String,
    val timezone: String,
)