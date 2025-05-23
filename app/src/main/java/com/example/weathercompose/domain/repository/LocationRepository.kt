package com.example.weathercompose.domain.repository

import com.example.weathercompose.data.database.entity.location.LocationEntity
import com.example.weathercompose.domain.model.location.LocationDomainModel
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    suspend fun search(
        name: String,
        count: Int,
        language: String,
        format: String,
    ): List<LocationEntity>

    suspend fun loadAll(): List<LocationDomainModel>

    fun loadAllLocationsWithForecasts(): Flow<List<LocationDomainModel>>

    suspend fun load(locationId: Long): LocationDomainModel?

    suspend fun insert(location: LocationEntity): Long

    suspend fun setLocationAsHome(locationId: Long)

    suspend fun delete(location: LocationEntity)

    suspend fun deleteLocationById(locationId: Long)

}