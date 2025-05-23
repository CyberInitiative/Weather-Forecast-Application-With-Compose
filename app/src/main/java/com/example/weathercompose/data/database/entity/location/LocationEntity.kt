package com.example.weathercompose.data.database.entity.location

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val locationId: Long,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val firstAdministrativeLevel: String,
    val secondAdministrativeLevel: String,
    val thirdAdministrativeLevel: String,
    val fourthAdministrativeLevel: String,
    val country: String,
    val timeZone: String,
    val isHomeLocation: Boolean = false,
) {

    fun getFullLocationName(): String {
        return listOf(
            name,
            country,
            firstAdministrativeLevel,
            secondAdministrativeLevel,
            thirdAdministrativeLevel,
            fourthAdministrativeLevel,
        ).filter { it.isNotEmpty() }.joinToString()
    }
}