package com.example.weathercompose.ui.viewmodel

import android.util.Log
import androidx.annotation.ColorRes
import androidx.lifecycle.ViewModel
import com.example.weathercompose.R
import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.ui.mapper.ForecastUIStateMapper
import com.example.weathercompose.ui.model.ForecastUIState
import com.example.weathercompose.ui.model.PrecipitationCondition
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

    private val _currentCityIdState = MutableStateFlow(NO_CURRENT_CITY_SET)
    val currentCityIdState: StateFlow<Long> = _currentCityIdState.asStateFlow()

    private val _precipitationCondition = MutableStateFlow(
        value = PrecipitationCondition.NO_PRECIPITATION_DAY
    )
    val precipitationCondition: StateFlow<PrecipitationCondition>
        get() =
            _precipitationCondition.asStateFlow()

    @ColorRes
    var rowColor: Int = R.color.liberty

    fun setLoadedCities(cities: List<CityDomainModel>) {
        loadedCities = cities
        if (loadedCities.isNotEmpty() && _currentCityIdState.value == NO_CURRENT_CITY_SET) {
            _currentCityIdState.value = loadedCities[0].id
        }
    }

    fun setCurrentCityId(cityId: Long) {
        _currentCityIdState.value = cityId
    }

    fun setForecastForCity(cityId: Long) {
        loadedCities.firstOrNull { city -> city.id == cityId }?.let {
            _currentCityIdState.value = it.id
            setPrecipitationsAndTimeOfDayState(it)
            val mappedCity = forecastUIStateMapper.mapToUIModel(it)
            _forecastUIState.update { mappedCity.copy(isDataLoading = false) }
        }
    }

    private fun setPrecipitationsAndTimeOfDayState(cityDomainModel: CityDomainModel) {
        val res = cityDomainModel.getPrecipitationsAndTimeOfDayStateForCurrentHour()
        Log.d(TAG, "setPrecipitationsAndTimeOfDayState: $res")
        _precipitationCondition.value =
            res
    }

    companion object {
        private const val TAG = "MainViewModel"
        private const val NO_CURRENT_CITY_SET = -1L
    }
}