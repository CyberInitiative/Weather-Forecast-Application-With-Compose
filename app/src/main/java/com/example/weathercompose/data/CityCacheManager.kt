package com.example.weathercompose.data

import com.example.weathercompose.domain.model.city.CityDomainModel

class CityCacheManager {
    private val idToCityCache = HashMap<Long, CityDomainModel>()

    fun putCityCache(cityId: Long, city: CityDomainModel) {
        idToCityCache[cityId] = city
    }

    fun getCityCache(cityId: Long): CityDomainModel? {
        return idToCityCache[cityId]
    }

    fun getAllCities(): List<CityDomainModel> {
        return idToCityCache.values.toList()
    }

    fun deleteCityFromCache(cityId: Long){
        idToCityCache.remove(cityId)
    }
}