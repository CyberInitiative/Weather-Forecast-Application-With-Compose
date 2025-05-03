package com.example.weathercompose.domain.model

sealed class DataState<out T> {
    data object Initial : DataState<Nothing>()
    data object Loading : DataState<Nothing>()
    data class Ready<out T>(val data: T) : DataState<T>()
    data class Error(val error: Throwable) : DataState<Nothing>()
    data object NoData : DataState<Nothing>()
}