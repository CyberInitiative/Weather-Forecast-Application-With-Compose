package com.example.weathercompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathercompose.data.api.Result
import com.example.weathercompose.domain.usecase.location.SearchLocationUseCase
import com.example.weathercompose.ui.ui_state.LocationSearchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationSearchViewModel(
    private val searchLocationUseCase: SearchLocationUseCase,
) : ViewModel() {
    private val _locationSearchState = MutableStateFlow(LocationSearchState())
    val locationSearchState: StateFlow<LocationSearchState> = _locationSearchState

    fun searchLocation(
        name: String,
    ) {
        viewModelScope.launch {
            _locationSearchState.value = _locationSearchState.value.copy(isLoading = true)
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
        }
    }
}