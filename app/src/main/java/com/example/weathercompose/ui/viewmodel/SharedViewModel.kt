package com.example.weathercompose.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathercompose.data.api.ResponseResult
import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.domain.usecase.city.LoadAllCitiesUseCase
import com.example.weathercompose.domain.usecase.city.LoadCityUseCase
import com.example.weathercompose.domain.usecase.forecast.DeleteForecastsUseCase
import com.example.weathercompose.domain.usecase.forecast.LoadForecastUseCase
import com.example.weathercompose.domain.usecase.forecast.SaveForecastsUseCase
import com.example.weathercompose.utils.NetworkManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SharedViewModel(
    private val loadCityUseCase: LoadCityUseCase,
    private val loadAllCitiesUseCase: LoadAllCitiesUseCase,
    private val loadForecastUseCase: LoadForecastUseCase,
    private val saveForecastsUseCase: SaveForecastsUseCase,
    private val deleteForecastsUseCase: DeleteForecastsUseCase,
    private val networkManager: NetworkManager,
) : ViewModel() {
    private val _loadedCitiesState = MutableStateFlow<List<CityDomainModel>>(value = emptyList())
    val loadedCitiesState: StateFlow<List<CityDomainModel>> = _loadedCitiesState.asStateFlow()

    init {
        viewModelScope.launch {
            loadCities()
        }
    }

    suspend fun loadCity(cityId: Long) {
        if (!isCityLoaded(cityId = cityId)) {
            val loadedCity = loadCityUseCase(cityId = cityId)
            _loadedCitiesState.update { it + loadForecastForCity(loadedCity) }
        }
    }

    private fun isCityLoaded(cityId: Long): Boolean {
        val currentLoadedCities = _loadedCitiesState.value
        return currentLoadedCities.any { city -> city.id == cityId }
    }

    private suspend fun loadCities() {
        val loadedCities = if (networkManager.isInternetAvailable()) {
            loadForecastsForCities(loadAllCitiesUseCase())
        } else {
            loadAllCitiesUseCase()
        }

        _loadedCitiesState.update { loadedCities }
    }

    private suspend fun loadForecastsForCities(cities: List<CityDomainModel>): List<CityDomainModel> =
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

                    deleteForecastsUseCase.invoke(cityId = city.id)
                    saveForecastsUseCase(
                        cityId = city.id,
                        dailyForecasts = dailyForecasts,
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

    companion object {
        private const val TAG = "SharedViewModel"
    }
}