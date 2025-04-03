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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
    private var loadedCities: MutableList<CityDomainModel> = mutableListOf()
    private var currentCityId: Long = 0

    private val _cityItems = MutableStateFlow<List<CityItem>>(emptyList())
    val cityItems: StateFlow<List<CityItem>> = _cityItems.asStateFlow()

    private val _cityForecastUIState =
        MutableStateFlow<CityForecastUIState>(CityForecastUIState.CityDataUIState())
    val cityForecastUIState: StateFlow<CityForecastUIState> = _cityForecastUIState.asStateFlow()

    private val _precipitationCondition = MutableStateFlow(
        value = PrecipitationCondition.NO_PRECIPITATION_DAY
    )
    val precipitationCondition: StateFlow<PrecipitationCondition>
        get() = _precipitationCondition.asStateFlow()

    init {
        viewModelScope.launch {
            loadCities()

            if (loadedCities.isNotEmpty()) {
                setCurrentCityForecast(loadedCities[0].id)
            }
        }
    }

    suspend fun setCurrentCityForecast(cityId: Long) {
        val loadedCity =
            loadedCities.firstOrNull { city -> city.id == cityId } ?: loadCityUseCase.invoke(
                cityId = cityId
            )

        val currentCity = if (loadedCity.forecasts.isEmpty()) {
            loadForecastForCity(loadedCity)
        } else {
            loadedCity
        }

        if (!loadedCities.contains(currentCity)) {
            loadedCities.add(currentCity)

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

    private suspend fun loadCities() {
        loadedCities = if (networkManager.isInternetAvailable()) {
            loadForecastsForCities(loadAllCitiesUseCase()).toMutableList()
        } else {
            loadAllCitiesUseCase().toMutableList()
        }

        val cityItems = loadedCities.map { cityItemsMapper.mapToCityItem(it) }
        _cityItems.update { cityItems }
    }

    private suspend fun loadForecastsForCities(cities: List<CityDomainModel>) =
        coroutineScope {
            cities.map { city ->
                async {
                    loadForecastForCity(city)
                }
            }.awaitAll()
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
            loadedCities.firstOrNull {
                it.id == cityId
            }?.let {
                loadedCities.remove(it)
                if (currentCityId == it.id && loadedCities.isNotEmpty()) {
                    currentCityId = loadedCities[0].id
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

    companion object {
        private const val TAG = "MainViewModel"
    }
}