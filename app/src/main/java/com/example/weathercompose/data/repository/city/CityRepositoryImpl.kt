package com.example.weathercompose.data.repository.city

import com.example.weathercompose.data.api.CityService
import com.example.weathercompose.data.database.dao.CityDao
import com.example.weathercompose.domain.mapper.mapToCityDomainModel
import com.example.weathercompose.domain.mapper.mapToEntity
import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.domain.repository.CityRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CityRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val cityService: CityService,
    private val cityDao: CityDao,
) : CityRepository {

    override suspend fun search(
        name: String,
        count: Int,
        language: String,
        format: String,
    ): List<CityDomainModel> {
        return withContext(dispatcher) {
            cityService.searchCities(
                name = name,
                count = count,
                language = language,
                format = format,
            ).cities?.map { it.mapToCityDomainModel() } ?: emptyList()
        }
    }

    override suspend fun loadAll(): List<CityDomainModel> {
        return withContext(dispatcher) {
            cityDao.loadAll().map { it.mapToCityDomainModel() }
        }
    }

    override suspend fun load(cityId: Long): CityDomainModel {
        return withContext(dispatcher) {
            cityDao.load(cityId = cityId).mapToCityDomainModel()
        }
    }

    override suspend fun insert(city: CityDomainModel): Long {
        return withContext(dispatcher) {
            cityDao.insert(city.mapToEntity())
        }
    }

    override suspend fun delete(city: CityDomainModel) {
        withContext(dispatcher) {
            cityDao.delete(city.mapToEntity())
        }
    }

    override suspend fun update(city: CityDomainModel) {
        withContext(dispatcher) {
            cityDao.update(city.mapToEntity())
        }
    }

}