package com.example.weathercompose.ui.viewmodel

import android.util.Log
import androidx.annotation.ColorRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathercompose.R
import com.example.weathercompose.data.api.ResponseResult
import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.domain.usecase.city.LoadAllCitiesUseCase
import com.example.weathercompose.domain.usecase.city.LoadCityUseCase
import com.example.weathercompose.domain.usecase.forecast.DeleteForecastsUseCase
import com.example.weathercompose.domain.usecase.forecast.LoadForecastUseCase
import com.example.weathercompose.domain.usecase.forecast.SaveForecastsUseCase
import com.example.weathercompose.ui.mapper.ForecastUIStateMapper
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

class MainViewModel(
    private val forecastUIStateMapper: ForecastUIStateMapper,
    private val loadCityUseCase: LoadCityUseCase,
    private val loadAllCitiesUseCase: LoadAllCitiesUseCase,
    private val loadForecastUseCase: LoadForecastUseCase,
    private val saveForecastsUseCase: SaveForecastsUseCase,
    private val deleteForecastsUseCase: DeleteForecastsUseCase,
    private val networkManager: NetworkManager,
) : ViewModel() {
    private var loadedCities: MutableList<CityDomainModel> = mutableListOf()

    private val _cityForecastUIState =
        MutableStateFlow<CityForecastUIState>(CityForecastUIState.CityDataUIState())
    val cityForecastUIState: StateFlow<CityForecastUIState> = _cityForecastUIState.asStateFlow()

    private val _precipitationCondition = MutableStateFlow(
        value = PrecipitationCondition.NO_PRECIPITATION_DAY
    )
    val precipitationCondition: StateFlow<PrecipitationCondition>
        get() = _precipitationCondition.asStateFlow()

    @ColorRes
    var rowColor: Int = R.color.liberty

    init {
        viewModelScope.launch {
            loadCities()

            if (loadedCities.isNotEmpty()) {
                setCurrentCityForecast(loadedCities[0].id)
            }
        }
    }

    suspend fun setCurrentCityForecast(cityId: Long) {
        val city = loadedCities.firstOrNull { city -> city.id == cityId } ?: loadCityUseCase.invoke(
            cityId = cityId
        ).also {
            this.loadedCities.add(it)
        }

        _precipitationCondition.value = city.getPrecipitationsAndTimeOfDayStateForCurrentHour()
        val cityForecastUIState = forecastUIStateMapper.mapToUIState(city = city)
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
        private const val TAG = "MainViewModel"
    }
}