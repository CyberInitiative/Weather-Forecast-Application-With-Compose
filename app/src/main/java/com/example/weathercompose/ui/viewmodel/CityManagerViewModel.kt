package com.example.weathercompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathercompose.domain.usecase.city.LoadAllCitiesUseCase
import com.example.weathercompose.ui.UIState
import com.example.weathercompose.ui.mapper.CityUIModelMapper
import com.example.weathercompose.ui.model.CityUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CityManagerViewModel(
    private val loadAllCitiesUseCase: LoadAllCitiesUseCase,
    private val cityUIModelMapper: CityUIModelMapper,
) : ViewModel() {
    private val _citiesUIState: MutableStateFlow<UIState<List<CityUIState>>> =
        MutableStateFlow(UIState.Loading())
    val citiesUIState: StateFlow<UIState<List<CityUIState>>> get() = _citiesUIState

    init {
        loadCities()
    }

    fun loadCities() {
        viewModelScope.launch {
            val cities = loadAllCitiesUseCase.execute()
            val mappedCities = cities.map { cityUIModelMapper.mapToUIModel(it) }
            _citiesUIState.value = UIState.Content(data = mappedCities)
        }
    }
}