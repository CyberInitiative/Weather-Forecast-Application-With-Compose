package com.example.weathercompose.domain.usecase.location

import com.example.weathercompose.domain.repository.LocationRepository

class SetLocationAsHomeUseCase(private val locationRepository: LocationRepository) {

    suspend operator fun invoke(locationId: Long) {
        locationRepository.setLocationAsHome(locationId = locationId)
    }
}