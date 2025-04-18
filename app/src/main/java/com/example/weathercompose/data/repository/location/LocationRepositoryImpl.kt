package com.example.weathercompose.data.repository.location

import com.example.weathercompose.data.api.GeocodingAPI
import com.example.weathercompose.data.database.dao.LocationDao
import com.example.weathercompose.data.mapper.mapToEntity
import com.example.weathercompose.data.mapper.mapToLocationDomainModel
import com.example.weathercompose.domain.model.location.LocationDomainModel
import com.example.weathercompose.domain.repository.LocationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class LocationRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val geocodingAPI: GeocodingAPI,
    private val locationDao: LocationDao,
) : LocationRepository {

    override suspend fun search(
        name: String,
        count: Int,
        language: String,
        format: String,
    ): List<LocationDomainModel> {
        return withContext(dispatcher) {
            geocodingAPI.searchLocation(
                name = name,
                count = count,
                language = language,
                format = format,
            ).locationSearchItems?.map { it.mapToLocationDomainModel() } ?: emptyList()
        }
    }

    override suspend fun loadAll(): List<LocationDomainModel> {
        return withContext(dispatcher) {
            locationDao.loadAll().map { it.mapToLocationDomainModel() }
        }
    }

    override suspend fun load(locationId: Long): LocationDomainModel {
        return withContext(dispatcher) {
            locationDao.load(locationId = locationId).mapToLocationDomainModel()
        }
    }

    override suspend fun insert(location: LocationDomainModel): Long {
        return withContext(dispatcher) {
            locationDao.insert(location.mapToEntity())
        }
    }

    override suspend fun delete(location: LocationDomainModel) {
        withContext(dispatcher) {
            locationDao.delete(location.mapToEntity())
        }
    }

    override suspend fun deleteLocationById(locationId: Long) {
        withContext(dispatcher) {
            locationDao.deleteLocationById(locationId = locationId)
        }
    }

    override suspend fun update(location: LocationDomainModel) {
        withContext(dispatcher) {
            locationDao.update(location.mapToEntity())
        }
    }

}