package com.example.weathercompose.domain.usecase.location

import com.example.weathercompose.data.database.entity.location.LocationEntity
import com.example.weathercompose.domain.repository.LocationRepository

class SaveLocationUseCase(private val locationRepository: LocationRepository) {

    suspend operator fun invoke(location: LocationEntity) : Long {
        return locationRepository.insert(location = location)
    }
}