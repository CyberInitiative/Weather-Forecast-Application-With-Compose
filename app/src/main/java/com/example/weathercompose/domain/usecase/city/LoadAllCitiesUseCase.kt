package com.example.weathercompose.domain.usecase.city

import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.domain.repository.CityRepository

class LoadAllCitiesUseCase(private val cityRepository: CityRepository) {

    suspend fun execute(): List<CityDomainModel> {
        return cityRepository.loadAll()
    }
}