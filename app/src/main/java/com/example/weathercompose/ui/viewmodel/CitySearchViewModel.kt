package com.example.weathercompose.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathercompose.data.api.ResponseResult
import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.domain.usecase.city.SaveCityUseCase
import com.example.weathercompose.domain.usecase.city.SearchCityUseCase
import com.example.weathercompose.ui.UIState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CitySearchViewModel(
    private val searchCityUseCase: SearchCityUseCase,
    private val saveCityUseCase: SaveCityUseCase,
) : ViewModel() {
    private val _citySearchUIResult: MutableStateFlow<UIState<List<CityDomainModel>>> =
        MutableStateFlow(UIState.Empty())
    val citySearchResult: Flow<UIState<List<CityDomainModel>>> get() = _citySearchUIResult

    fun searchCity(
        name: String,
        count: Int = DEFAULT_NUMBER_OF_RESULTS,
        language: String = DEFAULT_LANGUAGE,
        format: String = DEFAULT_FORMAT,
    ) {
        viewModelScope.launch {
            _citySearchUIResult.value = UIState.Loading()
            val citySearchResult = searchCityUseCase.execute(
                name = name,
                count = count,
                language = language,
                format = format,
            )

            when (citySearchResult) {
                is ResponseResult.Success -> {
                    _citySearchUIResult.value = UIState.Content(data = citySearchResult.data)
                }

                is ResponseResult.Error -> {
                    val code = citySearchResult.code
                    val message = citySearchResult.message.orEmpty()
                    val errorMessage = "Request error; Code: $code; Message: $message"
                    _citySearchUIResult.value = UIState.Error(message = errorMessage)
                }

                is ResponseResult.Exception -> {
                    _citySearchUIResult.value =
                        UIState.Error(message = citySearchResult.throwable.message.orEmpty())
                }
            }
        }
    }

    fun saveCity(city: CityDomainModel): Job {
        return viewModelScope.launch {
            val id = saveCityUseCase.execute(city = city)
            Log.d(TAG, "saveCity() called; id: $id")
        }
    }

    companion object {
        private const val TAG = "CitySearchViewModel"

        private const val DEFAULT_NUMBER_OF_RESULTS = 20
        private const val DEFAULT_LANGUAGE = "en"
        private const val DEFAULT_FORMAT = "json"
    }
}