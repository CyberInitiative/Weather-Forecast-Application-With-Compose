package com.example.weathercompose.domain.usecase.city

import com.example.weathercompose.data.CityCacheManager
import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.domain.repository.CityRepository

class LoadAllCitiesUseCase(
    private val cityRepository: CityRepository,
    private val cityCacheManager: CityCacheManager,
) {

    suspend operator fun invoke(): List<CityDomainModel> {
        return cityCacheManager.getAllCities().ifEmpty {
            val loadedCities = cityRepository.loadAll()
            loadedCities.forEach {
                cityCacheManager.putCityCache(it.id, it)
            }

            loadedCities
        }
    }
}