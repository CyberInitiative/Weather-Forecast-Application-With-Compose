package com.example.weathercompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathercompose.data.api.Result
import com.example.weathercompose.domain.usecase.location.SearchLocationUseCase
import com.example.weathercompose.ui.ui_state.LocationSearchState
import com.example.weathercompose.utils.NetworkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationSearchViewModel(
    private val searchLocationUseCase: SearchLocationUseCase,
    private val networkManager: NetworkManager,
) : ViewModel() {
    private val _locationSearchState = MutableStateFlow(LocationSearchState())
    val locationSearchState: StateFlow<LocationSearchState> = _locationSearchState

    fun searchLocation(
        name: String,
    ) {
        viewModelScope.launch {
            _locationSearchState.value = _locationSearchState.value.copy(isLoading = true)
            if (name.length >= 2) {
                when (val locationSearchResult = searchLocationUseCase(name = name)) {
                    is Result.Success -> {
                        _locationSearchState.value = _locationSearchState.value.copy(
                            isLoading = false,
                            locations = locationSearchResult.data
                        )
                    }

                    is Result.Error -> {
                        _locationSearchState.value = _locationSearchState.value.copy(
                            isLoading = false,
                            locations = emptyList(),
                            errorMessage = locationSearchResult.error,
                        )
                    }
                }
            } else {
                _locationSearchState.value = _locationSearchState.value.copy(
                    isLoading = false,
                    locations = emptyList()
                )
            }
        }
    }

    fun clearSearchResult(){
        _locationSearchState.value = _locationSearchState.value.copy(
            isLoading = false,
            locations = emptyList()
        )
    }

    fun isNetworkAvailable(): Boolean {
        return networkManager.isInternetAvailable()
    }
}