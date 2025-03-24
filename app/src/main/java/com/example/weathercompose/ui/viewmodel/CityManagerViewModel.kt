package com.example.weathercompose.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathercompose.domain.usecase.city.DeleteCityUseCase
import com.example.weathercompose.domain.usecase.city.LoadAllCitiesUseCase
import com.example.weathercompose.ui.mapper.CityMapper
import com.example.weathercompose.ui.model.CityItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CityManagerViewModel(
    private val loadAllCitiesUseCase: LoadAllCitiesUseCase,
    private val deleteCityUseCase: DeleteCityUseCase,
    private val cityMapper: CityMapper,
) : ViewModel() {

    private val _cityItems = MutableStateFlow<List<CityItem>>(emptyList())
    val cityItems: StateFlow<List<CityItem>> = _cityItems.asStateFlow()

    init {
        viewModelScope.launch {
            val cityItems = loadAllCitiesUseCase.invoke().map { cityMapper.mapToCityItem(it) }
            _cityItems.update { cityItems }
        }
    }

    fun deleteCity(cityId: Long) {
        viewModelScope.launch {
            Log.d(TAG, "deleteCity() called; City id: $cityId")
            deleteCityUseCase.invoke(cityId = cityId)
            _cityItems.value.firstOrNull {
                it.id == cityId
            }?.let {
                val newList = _cityItems.value.toMutableList()
                newList.remove(it)
                _cityItems.update { newList }
            }
        }
    }

    companion object {
        private const val TAG = "CityManagerViewModel"
    }
}