package com.example.weathercompose.domain.usecase.city

import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.domain.repository.CityRepository

class LoadAllCitiesUseCase(private val cityRepository: CityRepository) {

    suspend operator fun invoke(): List<CityDomainModel> {
        return cityRepository.loadAll()
    }
}