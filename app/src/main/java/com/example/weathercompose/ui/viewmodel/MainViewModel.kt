package com.example.weathercompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.ui.mapper.ForecastUIStateMapper
import com.example.weathercompose.ui.model.ForecastUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel(
    private val forecastUIStateMapper: ForecastUIStateMapper,
) : ViewModel() {
    private var loadedCities: List<CityDomainModel> = emptyList()

    private val _forecastUIState = MutableStateFlow(ForecastUIState())
    val forecastUIState: StateFlow<ForecastUIState> = _forecastUIState.asStateFlow()

    private var currentCityId: Long = NO_CURRENT_CITY_SET

    fun setLoadedCities(cities: List<CityDomainModel>) {
        loadedCities = cities

        if (loadedCities.isNotEmpty()) {
            val cityId = if (currentCityId == NO_CURRENT_CITY_SET) {
                loadedCities[0].id
            } else {
                currentCityId
            }
            setForecastForCity(cityId = cityId)
        }
    }

    fun setCurrentCityId(cityId: Long) {
        currentCityId = cityId
    }

    fun setForecastForCity(cityId: Long) {
        loadedCities.firstOrNull { city -> city.id == cityId }?.let {
            currentCityId = it.id
            val mappedCity = forecastUIStateMapper.mapToUIModel(it)
            _forecastUIState.update { mappedCity.copy(isDataLoading = false) }
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
        private const val NO_CURRENT_CITY_SET = -1L
    }
}