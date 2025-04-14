package com.example.weathercompose.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathercompose.data.api.ResponseResult
import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.domain.usecase.city.DeleteCityUseCase
import com.example.weathercompose.domain.usecase.city.LoadAllCitiesUseCase
import com.example.weathercompose.domain.usecase.city.LoadCityUseCase
import com.example.weathercompose.domain.usecase.forecast.DeleteForecastUseCase
import com.example.weathercompose.domain.usecase.forecast.LoadForecastUseCase
import com.example.weathercompose.domain.usecase.forecast.SaveForecastUseCase
import com.example.weathercompose.ui.mapper.CityItemMapper
import com.example.weathercompose.ui.mapper.ForecastUIStateMapper
import com.example.weathercompose.ui.model.CityItem
import com.example.weathercompose.ui.model.PrecipitationCondition
import com.example.weathercompose.ui.ui_state.CityForecastUIState
import com.example.weathercompose.utils.NetworkManager
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForecastViewModel(
    private val loadCityUseCase: LoadCityUseCase,
    private val loadAllCitiesUseCase: LoadAllCitiesUseCase,
    private val deleteCityUseCase: DeleteCityUseCase,
    private val loadForecastUseCase: LoadForecastUseCase,
    private val saveForecastUseCase: SaveForecastUseCase,
    private val deleteForecastUseCase: DeleteForecastUseCase,
    private val networkManager: NetworkManager,
    private val forecastUIStateMapper: ForecastUIStateMapper,
    private val cityItemsMapper: CityItemMapper,
) : ViewModel() {
    private val _loadedCities = MutableStateFlow<List<CityDomainModel>>(value = emptyList())

    private val _currentCity = MutableStateFlow<CityDomainModel?>(null)

    private val _cityForecastUIState =
        MutableStateFlow<CityForecastUIState>(CityForecastUIState.InitialUIState)
    val cityForecastUIState: StateFlow<CityForecastUIState> = _cityForecastUIState

    private val _cityItems = MutableStateFlow<List<CityItem>>(emptyList())
    val cityItems: StateFlow<List<CityItem>> = _cityItems.asStateFlow()

    private val _isCitiesEmpty = MutableStateFlow<Boolean?>(null)
    val isCitiesEmpty: StateFlow<Boolean?> = _isCitiesEmpty.asStateFlow()

    private val _precipitationCondition = MutableStateFlow(
        value = PrecipitationCondition.NO_PRECIPITATION_DAY
    )
    val precipitationCondition: StateFlow<PrecipitationCondition>
        get() = _precipitationCondition

    init {
        viewModelScope.launch {
            _loadedCities.collect { cities ->
                val current = _currentCity.value
                if (current != null) {
                    val updated = cities.find { it.id == current.id }
                    if (updated != null && updated != current) {
                        _currentCity.value = updated
                    }
                } else if (_currentCity.value == null && cities.isNotEmpty()) {
                    _currentCity.update {
                        cities[0]
                    }
                }

                _cityItems.update { cities.map { cityItemsMapper.mapToCityItem(it) } }
            }
        }

        viewModelScope.launch {
            _currentCity.collect { city ->
                if (city != null) {
                    _precipitationCondition.value =
                        city.getPrecipitationsAndTimeOfDayStateForCurrentHour()
                    val cityForecastUIState = forecastUIStateMapper.mapToUIState(city = city)
                    _cityForecastUIState.update {
                        (cityForecastUIState as CityForecastUIState.CityDataUIState).copy(
                            isDataLoading = false
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            loadCities()
            if (networkManager.isInternetAvailable()) {
                loadForecastsForLoadedCities()
            }
        }
    }

    private suspend fun loadCities() {
        _loadedCities.update { loadAllCitiesUseCase() }
    }

    private suspend fun loadForecastsForLoadedCities() {
        val loadedCitiesValue = _loadedCities.value

        coroutineScope {
            loadedCitiesValue.forEach { city ->
                launch {
                    val result = loadForecastForCity(city)
                    _loadedCities.update { currentList ->
                        currentList.map { existing ->
                            if (existing.id == result.id) result else existing
                        }
                    }
                }
            }
        }
    }

    private suspend fun loadForecastForCity(city: CityDomainModel): CityDomainModel {
        with(city) {
            val forecastLoadingResponseResult = loadForecastUseCase.execute(
                latitude = latitude,
                longitude = longitude,
                timeZone = timeZone,
            )

            return when (forecastLoadingResponseResult) {
                is ResponseResult.Success -> {
                    val dailyForecasts = forecastLoadingResponseResult.data

                    deleteForecastUseCase.invoke(cityId = city.id)
                    saveForecastUseCase(
                        cityId = city.id,
                        dailyForecast = dailyForecasts,
                    )

                    city.copy(forecasts = dailyForecasts)
                }

                is ResponseResult.Error -> {
                    Log.d(
                        TAG, "loadForecastForCity() called; ResponseResult.Error: ${
                            forecastLoadingResponseResult.buildErrorMessage()
                        }"
                    )
                    city.copy(errorMessage = forecastLoadingResponseResult.buildErrorMessage())
                }

                is ResponseResult.Exception -> {
                    Log.d(
                        TAG, "loadForecastForCity() called; ResponseResult.Exception: ${
                            forecastLoadingResponseResult.buildExceptionMessage()
                        }"
                    )
                    city.copy(errorMessage = forecastLoadingResponseResult.buildExceptionMessage())
                }
            }
        }
    }

    fun deleteCity(cityId: Long) {
        viewModelScope.launch {
            deleteCityUseCase.invoke(cityId = cityId)
            val loadedCitiesValue = _loadedCities.value
            loadedCitiesValue.firstOrNull {
                it.id == cityId
            }?.let {
                loadedCitiesValue.toMutableList().remove(it)
                if (_currentCity.value?.id == it.id && loadedCitiesValue.isNotEmpty()) {
                    _currentCity.value = loadedCitiesValue[0]
                }
            }

            _cityItems.value.firstOrNull {
                it.id == cityId
            }?.let {
                val newList = _cityItems.value.toMutableList()
                newList.remove(it)
                _cityItems.update { newList }
            }
        }
    }

    suspend fun setCurrentCityForecast(cityId: Long) {
        _cityForecastUIState.update {
            CityForecastUIState.LoadingUIState
        }

        val loadedCitiesValue = _loadedCities.value

        val loadedCity =
            loadedCitiesValue.firstOrNull { city -> city.id == cityId } ?: loadCityUseCase.invoke(
                cityId = cityId
            )

        val currentCity = if (loadedCity.forecasts.isEmpty()) {
            loadForecastForCity(loadedCity)
        } else {
            loadedCity
        }

        _currentCity.update { currentCity }

        if (!loadedCitiesValue.contains(currentCity)) {
            _loadedCities.update { loadedCitiesValue + currentCity }

            val currentCityItems = _cityItems.value
            _cityItems.update { currentCityItems + cityItemsMapper.mapToCityItem(currentCity) }
        }
        _precipitationCondition.value =
            currentCity.getPrecipitationsAndTimeOfDayStateForCurrentHour()
        val cityForecastUIState = forecastUIStateMapper.mapToUIState(city = currentCity)
        _cityForecastUIState.update {
            (cityForecastUIState as CityForecastUIState.CityDataUIState).copy(
                isDataLoading = false
            )
        }
    }

    companion object {
        private const val TAG = "ForecastViewModel"
    }
}
