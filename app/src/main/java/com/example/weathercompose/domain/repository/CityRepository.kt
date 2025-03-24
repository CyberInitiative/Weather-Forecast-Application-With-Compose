package com.example.weathercompose.domain.repository

import com.example.weathercompose.domain.model.city.CityDomainModel

interface CityRepository {
    suspend fun search(
        name: String,
        count: Int,
        language: String,
        format: String,
    ): List<CityDomainModel>

    suspend fun loadAll(): List<CityDomainModel>

    suspend fun load(cityId: Long): CityDomainModel

    suspend fun insert(city: CityDomainModel): Long

    suspend fun delete(city: CityDomainModel)

    suspend fun deleteCityById(cityId: Long)

    suspend fun update(city: CityDomainModel)
}