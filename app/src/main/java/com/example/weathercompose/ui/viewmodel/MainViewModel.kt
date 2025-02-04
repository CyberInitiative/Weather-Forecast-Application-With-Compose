package com.example.weathercompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathercompose.data.api.ForecastService
import com.example.weathercompose.data.api.ResponseResult
import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.domain.usecase.city.LoadAllCitiesUseCase
import com.example.weathercompose.domain.usecase.city.LoadCityUseCase
import com.example.weathercompose.domain.usecase.forecast.LoadForecastUseCase
import com.example.weathercompose.ui.UIState
import com.example.weathercompose.ui.mapper.CityUIModelMapper
import com.example.weathercompose.ui.model.CityUIModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val loadForecastUseCase: LoadForecastUseCase,
    private val loadCityUseCase: LoadCityUseCase,
    private val cityUIModelMapper: CityUIModelMapper,
    private val loadAllCitiesUseCase: LoadAllCitiesUseCase,
) : ViewModel() {
    private var currentCity: CityDomainModel? = null

    private val loadedCities: MutableList<CityDomainModel> = mutableListOf()

    private val _currentCityState: MutableStateFlow<UIState<CityUIModel>> =
        MutableStateFlow(UIState.Loading())
    val currentCityState: MutableStateFlow<UIState<CityUIModel>> get() = _currentCityState

    init {
        viewModelScope.launch {
            loadForecasts()
        }
    }

    private suspend fun loadForecasts() {
        val loadedCities = loadAllCitiesUseCase.execute()
        if (loadedCities.isEmpty()) {
            currentCityState.value = UIState.Empty()
            return
        }

        val loadedCitiesWithForecast = loadedCities.map { city ->
            val forecast = loadForecastUseCase.execute(
                latitude = city.latitude,
                longitude = city.longitude,
                timeZone = city.timezone,
                dailyOptions = ForecastService.dailyOptions,
                hourlyOptions = ForecastService.hourlyOptions,
                forecastDays = ForecastService.DEFAULT_FORECAST_DAYS,
            )

            when (forecast) {
                is ResponseResult.Success -> city.copy(forecast = forecast.data)
                else -> city
            }
        }

        this.loadedCities.addAll(loadedCitiesWithForecast)

        currentCity = this.loadedCities.first()
        val cityUIModel = cityUIModelMapper.mapToUIModel(currentCity!!)

        currentCityState.value = UIState.Content(cityUIModel)
    }

    fun loadForecastForCity(cityId: Int) {
        val idOwnerCity = loadedCities.firstOrNull { it.id == cityId }

        if (idOwnerCity == null) {
            viewModelScope.launch {
                val loadedCity = loadCityUseCase.execute(cityId = cityId)

                loadedCities.add(loadedCity)
                currentCity = loadedCity

                val forecast = loadForecastUseCase.execute(
                    latitude = currentCity!!.latitude,
                    longitude = currentCity!!.longitude,
                    timeZone = currentCity!!.timezone,
                    dailyOptions = ForecastService.dailyOptions,
                    hourlyOptions = ForecastService.hourlyOptions,
                    forecastDays = ForecastService.DEFAULT_FORECAST_DAYS,
                )

                when (forecast) {
                    is ResponseResult.Success -> {
                        currentCity = currentCity!!.copy(forecast = forecast.data)
                        val mappedCity = cityUIModelMapper.mapToUIModel(currentCity!!)

                        currentCityState.value = UIState.Content(mappedCity)
                    }

                    is ResponseResult.Error -> {

                    }

                    is ResponseResult.Exception -> {

                    }
                }
            }
        } else {
            currentCity = idOwnerCity
            val mappedCity = cityUIModelMapper.mapToUIModel(currentCity!!)

            currentCityState.value = UIState.Content(mappedCity)
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}