package com.example.weathercompose.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathercompose.data.api.ResponseResult
import com.example.weathercompose.domain.model.location.LocationDomainModel
import com.example.weathercompose.domain.usecase.location.SaveLocationUseCase
import com.example.weathercompose.domain.usecase.location.SearchLocationUseCase
import com.example.weathercompose.ui.UIState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LocationSearchViewModel(
    private val searchLocationUseCase: SearchLocationUseCase,
    private val saveLocationUseCase: SaveLocationUseCase,
) : ViewModel() {
    private val _locationSearchUIResult: MutableStateFlow<UIState<List<LocationDomainModel>>> =
        MutableStateFlow(UIState.Empty())
    val locationSearchResult: Flow<UIState<List<LocationDomainModel>>> get() = _locationSearchUIResult

    fun searchLocation(
        name: String,
        count: Int = DEFAULT_NUMBER_OF_RESULTS,
        language: String = DEFAULT_LANGUAGE,
        format: String = DEFAULT_FORMAT,
    ) {
        viewModelScope.launch {
            _locationSearchUIResult.value = UIState.Loading()
            val locationSearchResult = searchLocationUseCase.execute(
                name = name,
                count = count,
                language = language,
                format = format,
            )

            when (locationSearchResult) {
                is ResponseResult.Success -> {
                    _locationSearchUIResult.value =
                        UIState.Content(data = locationSearchResult.data)
                }

                is ResponseResult.Error -> {
                    val code = locationSearchResult.code
                    val message = locationSearchResult.message.orEmpty()
                    val errorMessage = "Request error; Code: $code; Message: $message"
                    _locationSearchUIResult.value = UIState.Error(message = errorMessage)
                }

                is ResponseResult.Exception -> {
                    _locationSearchUIResult.value =
                        UIState.Error(message = locationSearchResult.throwable.message.orEmpty())
                }
            }
        }
    }

    fun saveLocation(location: LocationDomainModel): Job {
        return viewModelScope.launch {
            val id = saveLocationUseCase.execute(location = location)
            Log.d(TAG, "saveLocation() called; id: $id")
        }
    }

    companion object {
        private const val TAG = "LocationSearchViewModel"

        private const val DEFAULT_NUMBER_OF_RESULTS = 20
        private const val DEFAULT_LANGUAGE = "en"
        private const val DEFAULT_FORMAT = "json"
    }
}