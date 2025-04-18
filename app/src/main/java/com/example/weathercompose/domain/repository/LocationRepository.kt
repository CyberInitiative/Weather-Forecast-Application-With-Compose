package com.example.weathercompose.domain.repository

import com.example.weathercompose.domain.model.location.LocationDomainModel

interface LocationRepository {
    suspend fun search(
        name: String,
        count: Int,
        language: String,
        format: String,
    ): List<LocationDomainModel>

    suspend fun loadAll(): List<LocationDomainModel>

    suspend fun load(locationId: Long): LocationDomainModel?

    suspend fun insert(location: LocationDomainModel): Long

    suspend fun delete(location: LocationDomainModel)

    suspend fun deleteLocationById(locationId: Long)

    suspend fun update(location: LocationDomainModel)
}