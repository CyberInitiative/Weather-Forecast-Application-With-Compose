package com.example.weathercompose.domain.usecase.location

import com.example.weathercompose.domain.model.location.LocationDomainModel
import com.example.weathercompose.domain.repository.LocationRepository

class SaveLocationUseCase(private val locationRepository: LocationRepository) {

    suspend operator fun invoke(location: LocationDomainModel) : Long {
        return locationRepository.insert(location = location)
    }
}