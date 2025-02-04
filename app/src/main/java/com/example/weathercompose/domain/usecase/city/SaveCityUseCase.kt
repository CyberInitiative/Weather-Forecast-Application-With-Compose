package com.example.weathercompose.domain.usecase.city

import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.domain.repository.CityRepository

class SaveCityUseCase(private val cityRepository: CityRepository) {

    suspend fun execute(city: CityDomainModel) {
        cityRepository.insert(city = city)
    }
}