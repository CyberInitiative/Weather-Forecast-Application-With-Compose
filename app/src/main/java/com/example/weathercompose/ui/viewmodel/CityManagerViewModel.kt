package com.example.weathercompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.domain.usecase.city.LoadAllCitiesUseCase
import com.example.weathercompose.ui.mapper.CityMapper
import com.example.weathercompose.ui.mapper.ForecastUIStateMapper
import com.example.weathercompose.ui.model.CityItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CityManagerViewModel(
    private val loadAllCitiesUseCase: LoadAllCitiesUseCase,
    private val forecastUIStateMapper: ForecastUIStateMapper,
    private val cityMapper: CityMapper,
    ) : ViewModel() {
    private var loadedCities: List<CityDomainModel> = emptyList()

    private val _cityItems = MutableStateFlow<List<CityItem>>(emptyList())
    val cityItems: StateFlow<List<CityItem>> = _cityItems.asStateFlow()

    fun setLoadedCities(cities: List<CityDomainModel>) {
        loadedCities = cities

        val cityItems = loadedCities.map { cityMapper.mapToCityItem(it) }
        _cityItems.update { cityItems }
    }

//    private val _citiesUIState: MutableStateFlow<UIState<List<ForecastUIState>>> =
//        MutableStateFlow(UIState.Loading())
//    val citiesUIState: StateFlow<UIState<List<ForecastUIState>>> get() = _citiesUIState

//    init {
//        loadCities()
//    }

//    fun loadCities() {
//        viewModelScope.launch {
//            val cities = loadAllCitiesUseCase()
//            val mappedCities = cities.map { forecastUIStateMapper.mapToUIModel(it) }
//            _citiesUIState.value = UIState.Content(data = mappedCities)
//        }
//    }
}