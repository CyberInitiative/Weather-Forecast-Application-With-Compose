package com.example.weathercompose.data.repository.location

import com.example.weathercompose.data.api.GeocodingAPI
import com.example.weathercompose.data.database.dao.LocationDao
import com.example.weathercompose.data.database.entity.location.LocationEntity
import com.example.weathercompose.data.mapper.mapToLocationDomainModel
import com.example.weathercompose.data.mapper.mapToLocationEntity
import com.example.weathercompose.domain.model.location.LocationDomainModel
import com.example.weathercompose.domain.repository.LocationRepository
import com.example.weathercompose.widget.WidgetUpdateManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LocationRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val geocodingAPI: GeocodingAPI,
    private val locationDao: LocationDao,
    private val widgetUpdateManager: WidgetUpdateManager,
) : LocationRepository {

    override suspend fun search(
        name: String,
        count: Int,
        language: String,
        format: String,
    ): List<LocationEntity> {
        return withContext(dispatcher) {
            geocodingAPI.searchLocation(
                name = name,
                count = count,
                language = language,
                format = format,
            ).locationSearchResponse?.map { it.mapToLocationEntity() } ?: emptyList()
        }
    }

    override suspend fun findAll(): List<LocationDomainModel> {
        return withContext(dispatcher) {
            locationDao.loadAll().map { it.mapToLocationDomainModel() }
        }
    }

    override fun findAllLocationsWithForecasts(): Flow<List<LocationDomainModel>> {
        return locationDao.loadAllLocationsWithForecasts().map { locations ->
            locations.map { it.mapToLocationDomainModel() }
        }
    }

    override suspend fun findById(locationId: Long): LocationDomainModel? {
        return withContext(dispatcher) {
            locationDao.load(locationId = locationId)?.mapToLocationDomainModel()
        }
    }

    override suspend fun insert(location: LocationEntity): Long {
        return withContext(dispatcher) {
            locationDao.insert(location)
        }
    }

    override suspend fun setLocationAsHome(locationId: Long) {
        withContext(dispatcher) {
            locationDao.setLocationAsHome(locationId = locationId)
            widgetUpdateManager.updateAll()
        }
    }

    override suspend fun findHomeLocation(): LocationDomainModel? {
        return withContext(dispatcher) {
            locationDao.findHomeLocation()?.mapToLocationDomainModel()
        }
    }

    override fun observeHomeLocation(): Flow<LocationDomainModel?> {
        return locationDao.observeHomeLocation().map { location ->
            location?.mapToLocationDomainModel()
        }
    }

    override suspend fun delete(location: LocationEntity) {
        withContext(dispatcher) {
            locationDao.delete(location)
        }
    }

    override suspend fun deleteLocationById(locationId: Long) {
        withContext(dispatcher) {
            locationDao.deleteLocationById(locationId = locationId)
        }
    }

    companion object {
        private const val TAG = "LocationRepositoryImpl"
    }
}